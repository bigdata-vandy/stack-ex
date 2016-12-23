import edu.vanderbilt.accre.xmltojson.XMLToJSONConverter
import net.liftweb.json.JsonAST

import scala.xml.{Node, NodeSeq}
import org.scalatest.{PrivateMethodTester, WordSpec}

/**
  * Created by arnold-jr on 11/7/16.
  */
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

        "passed  valid XML string" should {
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


    "invoking mapToJson" when {
      val converter = XMLToJSONConverter(List("foo"))
      val decorateMapToJson =
        PrivateMethod[Map[String, JsonAST.JValue]]('mapToJson)
      def mapToJson(m: Map[String, Any]) =
        converter invokePrivate decorateMapToJson(m)

      "passed an empty map " should {
        "return a map to an empty Json value" in {
          assert(mapToJson(Map.empty[String, Any]) ==
            Map.empty[String, JsonAST.JValue]
          )
        }
      }

      "passed a Map[String, String] " should {
        "return a map to a JString" in {
          assert(mapToJson(Map("foo" -> "bar")) ==
            Map("foo" -> JsonAST.JString("bar"))
          )
        }
      }

      "passed a Map[String, Int] " should {
        "return a map to a JInt" in {
          assert(mapToJson(Map("foo" -> 2)) ==
            Map("foo" -> JsonAST.JInt(2))
          )
        }
      }
      "passed a Map[String, List[String]] " should {
        "return a map to a List[JString]" in {
          assert(mapToJson(Map("foo" -> List("a", "b", "c"))) ==
            Map("foo" ->
              JsonAST.JArray(
                List(JsonAST.JString("a"),
                JsonAST.JString("b"),
                JsonAST.JString("c")
              )))
          )
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
