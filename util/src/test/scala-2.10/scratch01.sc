import net.liftweb.json
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._



val f0 = (s: String) => s
val f1 = (s: String) => s.toInt

val joe = Map( "Body" -> f0, "4" -> f0)

for ((k, f) <- joe) yield f(k)


val deb = Map("Body" -> JsonAST.JInt(1234), "Id" -> JsonAST.JString("1234"))

json.compactRender(deb)

val x: Any = "1234"

x match {
  case x: Int => JsonAST.JInt(x)
  case s: String => JsonAST.JString(s)
}

JsonAST.JInt(1234).isInstanceOf[JsonAST.JValue]

