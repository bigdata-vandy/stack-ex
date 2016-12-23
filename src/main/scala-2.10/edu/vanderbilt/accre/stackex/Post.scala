package edu.vanderbilt.accre.stackex

import scala.xml.{NodeSeq, XML}
import scala.util.Try

/**
  * Created by arnold-jr on 12/22/16.
  */
case class Post (id: Int,
                 postTypeId: Int,
                 body: String,
                 score: Int,
                 tags: List[String])

object Post {

  val fieldNames = List("Id", "PostTypeId", "Body", "Score", "Tags")

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
    * @param elem XML.NodeSeq constructed from "row" tag
    * @param attr attribute identifier
    * @return String with attribute payload
    */
  def getAttribute(elem: NodeSeq)(attr: String) = (elem \ ("@" + attr)).text

  def apply(line: String)() = {
    val elem = loadString(line)
    val getAttr = getAttribute(elem)(_)

    new Post(getInt(getAttr("Id")),
      getInt(getAttr("PostTypeId")),
      getTextFromHtml(getAttr("Body")),
      getInt(getAttr("Score")),
      getTags(getAttr("Tags"))
    )
  }

}