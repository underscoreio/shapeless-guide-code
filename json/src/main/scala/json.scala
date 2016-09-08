


sealed abstract class Json
final case class JsonObject(fields: List[(String, Json)]) extends Json
final case class JsonArray(items: List[Json]) extends Json
final case class JsonString(value: String) extends Json
final case class JsonNumber(value: Double) extends Json
final case class JsonBoolean(value: Boolean) extends Json
case object JsonNull extends Json



object Json {
  def encode[A](value: A)(implicit encoder: JsonEncoder[A]): Json =
    encoder.encode(value)

  def stringify(json: Json): String = json match {
    case JsonObject(fields) => "{" + fields.map(stringifyField).mkString(",") + "}"
    case JsonArray(items)   => "[" + items.map(stringify).mkString(",") + "]"
    case JsonString(value)  => "\"" + escape(value) + "\""
    case JsonNumber(value)  => value.toString
    case JsonBoolean(value) => value.toString
    case JsonNull           => "null"
  }

  private def stringifyField(field: (String, Json)): String = {
    val (name, value) = field
    escape(name) + ":" + stringify(value)
  }

  private def escape(str: String): String =
    "\"" + str.replaceAll("\"", "\\\\\"") + "\""
}



trait JsonEncoder[A] {
  def encode(value: A): Json
}

trait JsonObjectEncoder[A] extends JsonEncoder[A] {
  def encode(value: A): JsonObject
}



object JsonEncoder {
  def pure[A](func: A => Json): JsonEncoder[A] =
    new JsonEncoder[A] {
      def encode(value: A): Json =
        func(value)
    }

  def pureObj[A](func: A => JsonObject): JsonObjectEncoder[A] =
    new JsonObjectEncoder[A] {
      def encode(value: A): JsonObject =
        func(value)
    }

  // implicit val stringEnc: JsonEncoder[String] =
  //   pure(str => JsonString(str))

  // implicit val intEnc: JsonEncoder[Int] =
  //   pure(num => JsonNumber(num))

  // implicit val doubleEnc: JsonEncoder[Double] =
  //   pure(num => JsonNumber(num))

  // implicit val booleanEnc: JsonEncoder[Boolean] =
  //   pure(bool => JsonBoolean(bool))
}



object Main extends Demo {

  // TODO: Put demo data here

}


