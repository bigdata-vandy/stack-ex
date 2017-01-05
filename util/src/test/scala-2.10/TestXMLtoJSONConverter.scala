import edu.vanderbilt.accre.xmltools._
import net.liftweb.json.JsonAST

import scala.xml.{Node, NodeSeq}
import org.scalatest.{WordSpec}

/**
  * Created by arnold-jr on 11/7/16.
  */
class TestXMLTools extends WordSpec {

  "loadString" when {
    "passed an invalid XML string " should {
      "return NodeSeq.Empty" in {
        assert(loadString("<row Id=\"1\"") == NodeSeq.Empty)
      }
    }

    "passed  valid XML string" should {
      "have type XML.Node" in {
        assert(loadString("<a></a>").isInstanceOf[Node])
      }
      "parse nested elements" in {
        assert(loadString("<a><b></b></a>") == <a>
          <b></b>
        </a>)
      }
    }

    "passed an unclosed but valid XML string" should {
      "have type XML.Node" in {
        assert(loadString("<row Id=\"1\"/>").isInstanceOf[Node])
      }
      "parse both tags and attributes " in {
        assert(loadString("<row Id=\"1\"/>") == <row Id="1"></row>)
      }
    }
  }

  "getAttribute" when {
    val elem = <row Id="1" PostTypeId="2"/>
    "passed an XML element" should {
      val getAttr = getAttribute(elem)(_)
      "return an empty map" in {
        assert(getAttr("Id") == "1")
      }
    }
  }

}
