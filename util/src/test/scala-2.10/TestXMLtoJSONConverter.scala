/**
  * Created by arnold-jr on 11/7/16.
  */
import edu.vanderbilt.accre.xmltojson.XMLToJSONConverter

import scala.xml.{Node, NodeSeq}
import org.scalatest.{PrivateMethodTester, WordSpec}


class TestXMLtoJSONConverter extends WordSpec with PrivateMethodTester {

  "A converter" when {

    "initialized with an empty list of attributes" when {
      val converter = XMLToJSONConverter(List.empty[String])

      "invoking loadString" when {
        val decorateLoadString = PrivateMethod[NodeSeq]('loadString)
        def loadString(s: String) =
          converter invokePrivate decorateLoadString(s)

        "passed an invalid XML string " should {
          "return NodeSeq.Empty" in {
            assert(loadString("<row Id=\"1\"") == NodeSeq.Empty)
          }
        }

        "passed a valid XML string" should {
          "have type XML.Node" in {
            assert(loadString("<a></a>").isInstanceOf[Node])
          }
          "parse nested elements" in {
            assert(loadString("<a><b></b></a>") == <a><b></b></a>)
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

      "invoking getAttributes" should {
        val decorateGetAttributes =
          PrivateMethod[Map[String, Any]]('getAttributes)
        def getAttributes(elem: NodeSeq) =
          converter invokePrivate decorateGetAttributes(elem)
        val elem: NodeSeq = <row Id="1" PostTypeId="2"/>
        "return an empty map" in {
          assert(getAttributes(elem) == Map[String, Any]())
        }

      }
    }


    "invoking toJsonString" when {
      val converter = XMLToJSONConverter(List("foo"))
      val decorateToJsonString =
        PrivateMethod[String]('toJsonString)
      def toJsonString(m: Map[String, Any]) =
        converter invokePrivate decorateToJsonString(m)

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

    "instantiated with a list of attributes" when {
      val converter = XMLToJSONConverter(List("Id", "PostTypeId"))

      "invoking xmlToJson" when {

        "passed a valid XML string" should {

          "return the correct JSON string" in {
            assert(converter.xmlToJson("<row Id=\"1\" PostTypeId=\"2\" />") ==
              """{"Id":"1","PostTypeId":"2"}""")
          }
        }

        "passed an invalid XML string" should {

          "return the empty string values" in {
            assert(converter.xmlToJson("<row Id=\"1\" PostTypeId=\"2\"") ==
              """{"Id":"","PostTypeId":""}""")
          }
        }
      }
    }
  }
}
