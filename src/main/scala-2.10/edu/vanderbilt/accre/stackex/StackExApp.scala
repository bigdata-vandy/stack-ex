package edu.vanderbilt.accre.stackex

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification._
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.feature._
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD


/** StackExApp.scala
  * Created by arnold-jr on 11/8/16.
  */

/**
  * Runs Spark app for processing Posts.xml files from the
  * Stack Exchange Data Dump [https://archive.org/details/stackexchange].
  *
  */
object StackExApp {

  // Enables RDD.toDF() et al.
  import SparkContextKeeper.sqlContext.implicits._

  /**
    * Parses application arguments to main function.
    *
    * @param args space-delimted input arguments
    * @return tuple of input and output files
    */
  def parseArgs(args: Array[String]): Tuple2[String, String] = {
    if (args.length != 2) {
      System.err.println(
        "Usage: StackExApp <postsFile> <outputFile>")
      System.exit(1)
    }
    (args(0), args(1))
  }

  /**
    * Reads input from disk and maps to
    * [[edu.vanderbilt.accre.stackex.Post]] records.
    *
    * @param postsFile path to Posts.xml file
    * @return RDD with one [[Post]] record per line
    */
  def readPostsXML(postsFile: String): RDD[Post] = {

    import SparkContextKeeper.sc

    // Creates a new DataFrame with one row XML element per line
    sc.textFile(postsFile, 2)
      .map(line => Post(line))
      .filter(p => p.id != Int.MinValue)
  }

  /**
    * Saves a [[DataFrame]] as lines of JSON.
    *
    * @param df [[DataFrame]] to write
    * @param outputPath path to output directory
    */
  def writeToJSON(df: DataFrame, outputPath: String) = {
    val postsJSON = df.toJSON
    postsJSON take 25 foreach println
    postsJSON.saveAsTextFile(outputPath)
  }

  /**
    * Reads a Posts.xml file and writes to lines of JSON.
    *
    * @param postsFile input Posts.xml path
    * @param outputFile output directory path
    */
  def writeXMLToJSON(postsFile: String, outputFile: String): Unit = {
    val df  = readPostsXML(postsFile).toDF(Post.fieldNames: _*)
    writeToJSON(df, outputFile)
  }


  /**
    * Learns to classify posts as questions or answers depending on the
    * content of their "Body" attribute.
    *
    * @param postsFile path to Posts.xml file
    */
  def learnPostType(postsFile: String): Unit = {

    /**
      * Transforms a string into a more appropriate format.
      *
      * Much of the necessary tranformation is taken care of by the
      * [[Tokenizer]] class.
      *
      * Steps:
      * 1. Replaces multiple whitespace characters with a single space.
      *
      * @param s body text
      * @return transformed text
      */
    def cleanBody(s: String): String =
      s.replaceAll("""\\s+""", " ")
        //.replaceAll("""[\p{Punct}&&[^']]""", " ")

    /**
      * Creates the desired DataFrame with desired types (i.e. [[Double]])
      */
    val df = readPostsXML(postsFile)
      .filter(p => p.body.length > 0)
      .map{p => (p.postTypeId, cleanBody(p.body))}
      .filter{
        case (id: Int, body: String) =>
          body.length > 0 && List(1, 2).contains(id)
      }
      .map{
        case (x, b: String) => (x,
          b,
          b contains "?",
          (b count(_ == "?")).toDouble,
          (b length).toDouble
        )
      }
      .toDF("labelString",
        "body",
        "hasQMark",
        "numQMarks",
        "numChars")

    df.show(10)

    // Create the pipeline elements

    // Labels must be numeric
    val labelIndexer = new StringIndexer()
      .setInputCol("labelString")
      .setOutputCol("label")


    // Tokenize the string
    val tokenizer = new Tokenizer()
      .setInputCol("body")
      .setOutputCol("meaningfulWords")

    // Remove stop words
    val remover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("meaningfulWords")


    val numFeatures = 10000

    // Use TF-IDF to extract features
    val hashingTF = new HashingTF()
      .setInputCol("meaningfulWords")
      .setOutputCol("wordCounts")
      .setNumFeatures(numFeatures)

    /*
    val idf = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("word_count")


    // Use Word2Vec to extract features
    val word2Vec = new Word2Vec()
      .setInputCol("meaningfulWords")
      .setOutputCol("features")
      .setVectorSize(numFeatures)
      .setMinCount(0)
      */


    val assembler = new VectorAssembler()
      .setInputCols(Array("wordCounts", "hasQMark", "numQMarks", "numChars"))
      .setOutputCol("assembledFeatures")

    // Finds features upon which labels depend most, according to the
    // Chi-Squared test of independence
    val selector = new ChiSqSelector()
      .setNumTopFeatures(100)
      .setLabelCol("label")
      .setFeaturesCol("assembledFeatures")
      .setOutputCol("features")

    // Specify random forest classifier
    val classifier = new RandomForestClassifier()
      .setFeaturesCol("features")
      .setSeed(42L)


    /*
    // Specify layers for the neural network:
    val layers = Array[Int](
      numFeatures,
      numFeatures,
      2)

    val classifier = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setSeed(42L)
    */

    // Specify pipeline
    val pipeline = new Pipeline()
      .setStages(
        Array(
          labelIndexer,
          tokenizer,
          hashingTF,
          assembler,
          selector,
          classifier
        )
      )


    // Split data into train and test
    val Array(train, test) = df
      .randomSplit(Array(0.7, 0.3), seed = 42L)


    // Fit the model
    val model = pipeline.fit(train)

    // Compute precision on train set
    val resultTrain = model.transform(train)

    // Compute precision on the test set
    val resultCV = model.transform(test)

    // Metrics
    val metricsTrain = new BinaryClassificationMetrics(
      resultTrain.select("prediction","label").as[(Double, Double)].rdd
    )
    val auROCTrain = metricsTrain.areaUnderROC

    val metricsCV = new BinaryClassificationMetrics(
      resultCV.select("prediction","label").as[(Double, Double)].rdd
    )
    val auROC = metricsCV.areaUnderROC
    println(s"Area under ROC: Train = $auROCTrain CV = $auROC")

    // F-measure
    val f1ScoreTrain = metricsTrain.fMeasureByThreshold
    f1ScoreTrain.foreach { case (t, f) =>
      println(s"Threshold Train: $t, F-score: $f, Beta = 1")
    }

    val f1ScoreCV = metricsCV.fMeasureByThreshold
    f1ScoreCV.foreach { case (t, f) =>
      println(s"Threshold CV: $t, F-score: $f, Beta = 1")
    }

    // Precision by threshold
    metricsCV.precisionByThreshold.foreach { case (t, p) =>
      println(s"Threshold CV: $t, Precision: $p")
    }

    // Recall by threshold
    metricsCV.recallByThreshold.foreach { case (t, r) =>
      println(s"Threshold CV: $t, Recall: $r")
    }

  }


  def main(args: Array[String]): Unit = {

    val (postsFile, outputFile) = parseArgs(args)

    //writeXMLToJSON(postsFile, outputFile)

    learnPostType(postsFile)

    SparkContextKeeper.stop()

  }
}
