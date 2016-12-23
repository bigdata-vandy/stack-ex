/**
  * Created by arnold-jr on 11/7/16.
  */
package edu.vanderbilt.accre

import scala.xml.{NodeSeq, XML}
import scala.util.Try


package object xmltools {
  /**
    * Parses a possibly malformed XML string and returns an XML element.
    * failing quietly
    *
    * @param line
    * @return XML Node
    *
    */
  def loadString(line: String): NodeSeq =
    Try(XML.loadString(line)) getOrElse NodeSeq.Empty

  /**
    * Gets attribute values from an XML NodeSeq
    *
    * @param elem XML.NodeSeq constructed from "row" tag
    * @param attr attribute identifier
    * @return String with attribute payload
    */
  def getAttribute(elem: NodeSeq)(attr: String) = (elem \ ("@" + attr)).text


}
