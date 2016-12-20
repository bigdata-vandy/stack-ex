/**
  * Created by arnold-jr on 11/7/16.
  */
import edu.vanderbilt.accre.xmltojson.{AttributeMapper, XMLToJSONConverter}

import scala.xml.{Node, NodeSeq}
import org.scalatest.WordSpecLike


class TestXMLtoJSONConverter extends XMLToJSONConverter
  with WordSpecLike {

  "loadString" when {
    "passed an invalid XML string" should {
      "return NodeSeq.Empty" in {
        assert(loadString("<row Id=\"1\"") == NodeSeq.Empty)
      }
    }
    "passed a valid XML string" should {
      "have type XML.Node" in {
        assert(loadString("<a></a>" ).isInstanceOf[Node])
      }
      "parse nested elements" in {
        assert(loadString("<a><b></b></a>") == <a><b></b></a>)
      }
    }
    "passed an unclosed but valid XML string" should {
      "have type XML.Node" in {
        assert(loadString("<row Id=\"1\"/>" ).isInstanceOf[Node])
      }
      "parse both tags and attributes " in {
        assert(loadString("<row Id=\"1\"/>" ) == <row Id="1"></row>)
      }
    }
  }

  "getAttributes for a valid xml element" when {
    val elem: NodeSeq = <row Id="1" PostTypeId="2"/>
    "passed an empty list of attributes" should {
      "return an empty map" in {
        assert(getAttributes(elem, List[String]()) == Map.empty)
      }
    }
    "passed an empty map of attributes" should {
      "return an empty map" in {
        val emptyMap: AttributeMapper = Map.empty
        assert(getAttributes(elem, emptyMap) == Map.empty)
      }
    }
    "passed a valid list of attributes" should {
      "return the attributes values as a list" in {
        assert(getAttributes(elem, List("Id", "PostTypeId")) ==
          Map("Id" -> "1", "PostTypeId" -> "2"))
      }
    }
    "passed an invalid list of attributes" should {
      "return a map with keys and empty string values" in {
        assert(getAttributes(elem, List("Ids", "PosttypeId")) ==
          Map("Ids" -> "", "PosttypeId" -> ""))
      }
    }
    "passed a map with valid attributes" should {
      "return a map with keys and correct string values" in {
        val mapperFun = (s: String) => "#" ++ s
        assert(getAttributes(elem, Map("Id" -> mapperFun)) ==
          Map("Id" -> "#1"))
      }
    }
    "passed a map with invalid attributes" should {
      "return a map with keys and transformed string values" in {
        val mapperFun = (s: String) => "#" ++ s
        assert(getAttributes(elem, Map("Ids" -> mapperFun)) ==
          Map("Ids" -> "#"))
      }
    }
  }

  "toJsonString" when {
    "passed an empty map " should {
      "return an empty JSON string" in {
        assert(toJsonString(Map.empty[String, Any]) == "{}")
      }
    }
    "passed a Map[String, String] " should {
      "return a JSON string of strings" in {
        assert(toJsonString(Map("foo" -> "bar")) == "{\"foo\":\"bar\"}")
      }
    }
    "passed a Map[String, Int] " should {
      "return a JSON object of String:Int" in {
        assert(toJsonString(Map("foo" -> 2)) == "{\"foo\":2}")
      }
    }
  }

  "xmlToJson" when {
    "passed an empty" should {
      "return " in  {
        assert(
          xmlToJson("<row Id=\"1\" PostTypeId=\"2\"/>")(List("Id")) == "asdf")
      }
    }
  }

}
