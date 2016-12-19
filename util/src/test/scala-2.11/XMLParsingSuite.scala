/**
  * Created by arnold-jr on 11/7/16.
  */
package edu.xmlparsing

import edu.vanderbilt.accre.xmltojson.XMLToJSON

import scala.xml.{Node, NodeSeq, XML}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class XMLParsingSuite extends FunSuite {

  test("1 = 1") {
    assert( 1 === 1)
  }

  test("missing element should return NodeSeq.Empty") {
    val elem = XMLToJSON.loadString("<a></a>")
    assert( elem \ "a" == NodeSeq.Empty )
  }

  test("Node should be returned") {
    val elem = XMLToJSON.loadString("<a><b></b></a>")
    assert( elem.isInstanceOf[Node] )
  }

  test("child text should be returned") {
    val elem = XMLToJSON.loadString("<a><b>foo</b></a>")
    assert( (elem \ "b").text == "foo" )
  }

  test("closed elements should parse correctly") {
    val elem = XMLToJSON.loadString("""<a Body="foo" />""")
    assert( elem.text == "" )
  }

  test("unclosed elements should return NodeSeq.Empty") {
    val elem = XMLToJSON.loadString("""<a Body="foo" """)
    assert( elem == NodeSeq.Empty )
  }

  val s =
    """<row Id="5" PostTypeId="1" CreationDate="2014-05-13T23:58:30.457"
      |Score="7" ViewCount="296" Body="&lt;p&gt;I've always been interested in
      |machine learning, but I can't figure out one thing about starting out
      |with a simple &quot;Hello World&quot; example - how can I avoid
      |hard-coding behavior?&lt;/p&gt;&#xA;&#xA;&lt;p&gt;For example, if
      |I wanted to &quot;teach&quot; a bot how to avoid randomly placed
      |obstacles, I couldn't just use relative motion, because the obstacles
      |move around, but I don't want to hard code, say, distance, because that
      |ruins the whole point of machine learning.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;
      |Obviously, randomly generating code would be impractical, so how could I
      |do this?&lt;/p&gt;&#xA;" OwnerUserId="5"
      |LastActivityDate="2014-05-14T00:36:31.077" Title="How can I do simple
      |machine learning without hard-coding behavior?"
      |Tags="&lt;machine-learning&gt;" AnswerCount="1" CommentCount="1"
      |FavoriteCount="1" ClosedDate="2014-05-14T14:40:25.950" />""".stripMargin

  test("a representative string should be parsed correctly") {
    val elem = XMLToJSON.loadString(s)
    assert((elem \ "@Id").text == "5")
    assert((elem \ "@ViewCount").text == "296")
  }

  test("XMLParser.getAttributes should return a Map[String, Any]") {
    val elem = XMLToJSON.loadString(s)

    val f0 = (s: String) => s
    val f1 = (s: String) => s.toInt

    val attrMap = Map("Id" -> f0, "ViewCount" -> f1)

    assert(XMLToJSON.getAttributes(elem, attrMap) ==
      Map("Id"-> "5", "ViewCount" -> 296)
    )
  }


}
