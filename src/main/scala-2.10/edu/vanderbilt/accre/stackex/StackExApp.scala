package edu.vanderbilt.accre.stackex

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification._
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.ml.feature._
import org.apache.spark.rdd.RDD


/** StackExApp.scala
  * Created by arnold-jr on 11/8/16.
  */

object StackExApp {

  def parseArgs(args: Array[String]) = {
    if (args.length != 2) {
      System.err.println(
        "Usage: StackExApp <postsFile> <outputFile>")
      System.exit(1)
    }
    (args(0), args(1))
  }

  def readPostsXML(postsFile: String): RDD[Post] = {

    import SparkContextKeeper.sc

    // Creates a new DataFrame with one row XML element per line
    sc.textFile(postsFile)
      .map(line => Post(line))
      .filter(p => p.id != Int.MinValue)
  }

  def writeToJSON(df: DataFrame, outputFile: String) = {
    val postsJSON = df.toJSON
    postsJSON take 25 foreach println
    postsJSON.saveAsTextFile(outputFile)
  }

  def writeXMLToJSON(postsFile: String, outputFile: String): Unit = {
    import SparkContextKeeper.sqlContext.implicits._
    val df  = readPostsXML(postsFile).toDF(Post.fieldNames: _*)
    writeToJSON(df, outputFile)
  }


  def learnTags(postsFile: String) = {
    import SparkContextKeeper.sqlContext.implicits._

    // UDF for extracting first tag from "Tags" column
    val tagColFun = udf { tagSeq: Seq[String] => tagSeq.head }
      .apply(col("Tags"))

    // Filters out Posts that don't have bodies and tags
    val df = readPostsXML(postsFile)
      .filter(p => p.tags.length > 0 && p.body.length > 0)
      .map(p => p.copy(body = p.body.replaceAll("""[\p{Punct}&&[^']]""", "")))
      .toDF(Post.fieldNames: _*)
      .withColumn("labelWord", tagColFun)


    // Create the pipeline elements

    // Create string labels to integers
    val labelIndexer = new StringIndexer()
      .setInputCol("labelWord")
      .setOutputCol("label")
      .fit(df)

    // Tokenize the string
    val tokenizer = new Tokenizer()
      .setInputCol("Body")
      .setOutputCol("words")

    // Remove stop words
    val remover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("meaningfulWords")


    val numFeatures = 100

    /*
    // Use TF-IDF to extract features
    val hashingTF = new HashingTF()
      .setInputCol("meaningfulWords")
      .setOutputCol("rawFeatures")
      .setNumFeatures(numFeatures)
    val idf = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")
    */


    // Use Word2Vec to extract features
    val word2Vec = new Word2Vec()
      .setInputCol("meaningfulWords")
      .setOutputCol("features")
      .setVectorSize(numFeatures)
      .setMinCount(0)

    // Use a scaler
    val scaler = new MinMaxScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")


    /*
    // Specify logistic regression
    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    val classifier =  new OneVsRest()
      .setClassifier(lr)
    */

    /*
    // Specify random forest classifier
    val classifier = new RandomForestClassifier()
      .setFeaturesCol("scaledFeatures")
      .setSeed(42L)
    */

    // Specify layers for the neural network:
    // input layer of size 100 (features), two intermediate of size 5 and 4
    // and output of size 12 (classes)
    val layers = Array[Int](
      numFeatures,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (3 * numFeatures).round.toInt,
      (1 * numFeatures).round.toInt,
      numFeatures,
      labelIndexer.labels.length)

    val classifier = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setSeed(42L)
      //.setFeaturesCol("scaledFeatures")

    // Specify pipeline
    val pipeline = new Pipeline()
      .setStages(
        Array(labelIndexer, tokenizer, remover, word2Vec, classifier)
      )
      /*
      .setStages(
        Array(labelIndexer, tokenizer, remover, hashingTF, idf, classifier)
      )
       */

    // Split data into train and test
    val Array(train, test) = df
      .randomSplit(Array(0.7, 0.3), seed = 42L)

    // Fit the model
    val model = pipeline.fit(train)

    // Compute precision on the test set
    val result = model.transform(test)
    //DEBUG
    result.show()

    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("precision")
      .setLabelCol("label")
      .setPredictionCol("prediction")
    println("Precision: " + evaluator.evaluate(result))

    result.select("meaningfulWords").take(5).foreach(println)

  }

  def main(args: Array[String]): Unit = {

    val (postsFile, outputFile) = parseArgs(args)

    //writeXMLToJSON(postsFile, outputFile)

    learnTags(postsFile)

    SparkContextKeeper.stop()

  }
}
