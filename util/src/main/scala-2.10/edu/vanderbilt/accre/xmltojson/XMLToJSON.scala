/**
  * Created by arnold-jr on 11/7/16.
  */
package edu.vanderbilt.accre.xmltojson

import scala.xml.{XML, NodeSeq}
import scala.util.Try
import net.liftweb.json
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._


object XMLToJSON {

  /**
    * Parses a possibly malformed XML string and returns an XML element
    * @param line
    * @return XML Node
    *
    */
  private def loadString(line: String): NodeSeq =
    Try ( XML.loadString(line) ) getOrElse NodeSeq.Empty


  private type AttributeMapper = Map[String, String => Any]

  /**
    * Gets attribute values from an XML NodeSeq
    * @param elem an XML NodeSeq
    * @param attrMap map of attribute -> value mapping function
    * @return map of attribute -> value: Any
    */
  private def getAttributes(elem: NodeSeq,
                    attrMap: AttributeMapper): Map[String, Any] =
    for ((a, f) <- attrMap) yield a -> f((elem \ ("@" + a) ).text)

  /**
    * Gets attribute values from an XML NodeSeq
    * @param elem an XML NodeSeq
    * @param attrs list of attributes to query
    * @return map of attribute -> value: Any
    */
  private def getAttributes(elem: NodeSeq,
                    attrs: List[String]): Map[String, Any] =
    (for (a <- attrs) yield a -> (elem \ ("@" + a) ).text).toMap

  /**
    * Renders JSON string from map of attribute -> value: Any
    * @param m attribute map
    * @return JSON string
    */
  private def toJsonString(m: Map[String, Any]): String = {
    val mJson: Map[String, JsonAST.JValue] = for {
      (k, v) <- m
    } yield k -> (v match {
      case x: Int => JsonAST.JInt(x)
      case s: String => JsonAST.JString(s)
      case _ => JsonAST.JNothing
    })
    json.compactRender(mJson)
  }

  /**
    * Creates JSON string from XML string
    * @param line XML string snippet
    * @param attributeMapper map of attribute name -> value mapper function
    * @return single line JSON string
    */
  def xmlToJson(line: String, attributeMapper: AttributeMapper): String = {
    toJsonString(getAttributes(loadString(line), attributeMapper))
  }

  /**
    * Creates JSON string from XML string
    * @param line XML string snippet
    * @param attributes list of attributes to query
    * @return single line JSON string
    */
  def xmlToJson(line: String, attributes: List[String]): String = {
    toJsonString(getAttributes(loadString(line), attributes))
  }

}
