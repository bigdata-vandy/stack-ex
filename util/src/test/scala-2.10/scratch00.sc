import edu.vanderbilt.accre.xmltojson.XMLToJSONConverter
import edu.xmlparsing._

import scala.xml.{Node, NodeSeq, XML}
import scala.util.Try

val elem = <a><b></b></a>

elem \ "a" == NodeSeq.Empty

elem \ "b"

elem \\ "b"

"a".isInstanceOf[String]

elem.isInstanceOf[Node]

XMLToJSONConverter.loadString("<row>fooey</row>").text

val deb =
  Try (
    XML.loadString(
      """<row Id="5" PostTypeId="1" CreationDate="2014-05-13T23:58:30.457""""
    )
  ) getOrElse NodeSeq.Empty

XMLToJSONConverter.loadString("<a><item></item></a>")

XMLToJSONConverter.loadString("""<a Body="foo" />""").text

XMLToJSONConverter.loadString("""<?xml version="1.0" encoding="utf-8"?>""")
XMLToJSONConverter.loadString("""<posts>""")

NodeSeq.Empty.text
"@" + "Body"

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

val joe = XML.loadString(s)

XMLToJSONConverter.getAttributes(joe, Map("Body" -> ))
