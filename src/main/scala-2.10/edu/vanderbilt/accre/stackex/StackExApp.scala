package edu.vanderbilt.accre.stackex

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext


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


  def main(args: Array[String]): Unit = {

    val (postsFile, outputFile) = parseArgs(args)

    val conf = new SparkConf()
      .setAppName("Stack-Ex Application")

    val sc = new SparkContext(conf)


    def writeXMLToJSON() = {
      val sqlContext = new SQLContext(sc)

      import sqlContext.implicits._

      // Creates a new DataFrame with one XML element per line
      val df = sc.textFile(postsFile)
        .map(line => Post(line))
        .filter(p => p.id != Int.MinValue)
        .toDF(Post.fieldNames: _*)

      val postsJSON = df.toJSON

      postsJSON take 5 foreach println

      postsJSON.saveAsTextFile(outputFile)
    }

    writeXMLToJSON()

    sc.stop()

  }
}
