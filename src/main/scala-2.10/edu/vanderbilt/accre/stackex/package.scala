package edu.vanderbilt.accre

import org.htmlcleaner.{HtmlCleaner, HtmlNode, TagNode, TagNodeVisitor}
import org.apache.commons.lang.StringEscapeUtils.escapeHtml

import scala.util.Try

/**
  * Created by arnold-jr on 12/22/16.
  */
package object stackex {

  def getFullText(text: String) = escapeHtml(text)

  def getInt(s: String): Int = Try(s.toInt) getOrElse Int.MinValue

  def getTags(text: String): List[String] = {
    val tagPattern = "(?<=&lt;)\\S+?(?=&gt;)".r
    (tagPattern findAllIn escapeHtml(text)) toList
  }


  def getTextFromHtml(html: String): String = {
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

    val cleaner = new HtmlCleaner
    val rootNode = cleaner.clean(html).getElementsByName("body",false)(0)

    // Prunes the tree
    rootNode.traverse(tagNodeVisitor)

    rootNode.getText.toString
  }



}
