package edu.vanderbilt.accre

import org.htmlcleaner.{HtmlCleaner, HtmlNode, TagNode, TagNodeVisitor}
import org.apache.commons.lang.StringEscapeUtils.escapeHtml

import scala.util.Try

/**
  * Created by arnold-jr on 12/22/16.
  */
package object stackex {

  val tagNodeVisitor = new TagNodeVisitor {

    val includedTags = List("b", "blockquote", "dl", "dt", "em", "h1", "h2",
      "h3", "i", "li", "ol", "p", "strong", "ul")
    override def visit(tagNode: TagNode, htmlNode: HtmlNode): Boolean = {
      htmlNode match {
        case t: TagNode =>
          if (!(includedTags contains t.getName)) {
            t.removeFromTree()
          }
        case _ =>
      }
      true
    }
  }

  def getInt(s: String): Int = Try(s.toInt) getOrElse Int.MinValue


  val getTextFromHtml: String => String  = (html: String) => {
    val cleaner = new HtmlCleaner
    val rootNode = cleaner.clean(html).getElementsByName("body",false)(0)

    // Prunes the tree
    rootNode.traverse(tagNodeVisitor)

    rootNode.getText.toString
  }

  val getFullText = (text: String) => escapeHtml(text)

  val getTags: String => List[String] = (text: String) => {
    val tagPattern = "(?<=&lt;)\\S+?(?=&gt;)".r
    (tagPattern findAllIn escapeHtml(text)) toList
  }

}
