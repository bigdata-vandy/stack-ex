package edu.vanderbilt.accre.stackex

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * Created by joshuaarnold on 1/3/17.
  */
object SparkContextKeeper {
  val conf = new SparkConf().setAppName("Stack-Ex Application")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  def stop(): Unit = sc.stop()

}
