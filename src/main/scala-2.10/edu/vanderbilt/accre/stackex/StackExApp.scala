/** StackExApp.scala
  * Created by arnold-jr on 11/8/16.
  */
package edu.vanderbilt.accre.stackex

import edu.vanderbilt.accre.xmltojson.XMLToJSONConverter
import org.apache.spark.{SparkConf, SparkContext}


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

    val postsXML = sc.textFile(postsFile)

    val fString = (s: String) => s
    val converter = XMLToJSONConverter(Map("Body" -> fString))

    val postsJSON = postsXML
      .map(line => converter.xmlToJson(line))

    if (true) {
      (postsJSON take 10) foreach println
    }

    // Writes output to file
    postsJSON.saveAsTextFile(outputFile)

    sc.stop()

  }
}
