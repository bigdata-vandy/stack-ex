/**
  * Created by arnold-jr on 11/7/16.
  */
package edu.vanderbilt.accre.xmltojson

import scala.xml.{NodeSeq, XML}
import scala.util.Try
import net.liftweb.json
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._



abstract class XMLToJSONConverter {


  /**
    * Parses a possibly malformed XML string and returns an XML element
    * @param line
    * @return XML Node
    *
    */
  protected def loadString(line: String): NodeSeq =
    Try ( XML.loadString(line) ) getOrElse NodeSeq.Empty


  /**
    * Gets attribute values from an XML NodeSeq
    * @param elem an XML NodeSeq
    * @param attrIter attribute iterable, i.e. List[String] or AttributeMapper
    * @return map of attribute -> value: Any
    */
  protected def getAttributes[T](elem: NodeSeq, attrIter: T)
                                (implicit attrColl: AttributeCollection[T]) =
    for {
      (a, f) <- attrColl.getAttributeMapper(attrIter)
    } yield a -> f((elem \ ("@" + a) ).text)


  /**
    * Renders JSON string from map of attribute -> value: Any
    * @param m attribute map
    * @return JSON string
    */
  protected def toJsonString(m: Map[String, Any]): String = {
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
    * @param line XML string snippet
    * @param attrColl attribute collection
    * @return single line JSON string
    */
  def xmlToJson[T: AttributeCollection](line: String)(attrColl: T): String =
    toJsonString(getAttributes(loadString(line), attrColl))


}
