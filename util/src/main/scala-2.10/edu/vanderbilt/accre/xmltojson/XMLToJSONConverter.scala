/**
  * Created by arnold-jr on 11/7/16.
  */
package edu.vanderbilt.accre.xmltojson

import scala.xml.{NodeSeq, XML}
import scala.util.Try
import net.liftweb.json
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._


object XMLToJSONConverter {
  type AttributeMap = Map[String, String => Any]


  class XMLToJSONConverter(attributeMapper: AttributeMap) {

    /**
      * Parses a possibly malformed XML string and returns an XML element
      *
      * @param line
      * @return XML Node
      *
      */
    private def loadString(line: String): NodeSeq =
    Try(XML.loadString(line)) getOrElse NodeSeq.Empty


    /**
      * Gets attribute values from an XML NodeSeq
      *
      * @param elem an XML NodeSeq
      * @return map of attribute -> value: Any
      */
    private def getAttributes(elem: NodeSeq): Map[String, Any] =
    for {
      (a, f) <- attributeMapper
    } yield a -> f((elem \ ("@" + a)).text)


    /**
      * Renders JSON string from map of attribute -> value: Any
      *
      * @param m attribute map
      * @return JSON string
      */
    private def toJsonString(m: Map[String, Any]): String = {
      val mJson: Map[String, JsonAST.JValue] = for {
        (k, v) <- m
      } yield k -> (v match {
        case i: Int => JsonAST.JInt(i)
        case f: Float => JsonAST.JDouble(f)
        case d: Double => JsonAST.JDouble(d)
        case s: String => JsonAST.JString(s)
        case _ => JsonAST.JNothing
      })
      json.compactRender(mJson)
    }

    /**
      * Creates JSON string from XML string
      *
      * @param line XML string snippet
      * @return single line JSON string
      */
    def xmlToJson(line: String): String =
      toJsonString(getAttributes(loadString(line)))

  }

  def apply(attrs: List[String]) = {
    val ident = (s: String) => s
    val attrMap = (for (v <- attrs) yield v -> ident).toMap
    new XMLToJSONConverter(attrMap)
  }

  def apply(attrMap: AttributeMap) =
    new XMLToJSONConverter(attrMap)

}
