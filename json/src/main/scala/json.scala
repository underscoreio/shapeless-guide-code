


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



  // implicit val hnilEnc: JsonObjectEncoder[HNil] =
  //   pureObj(hnil => JsonObject(Nil))

  // implicit def hlistEnc // ...

  // implicit val cnilEnc: JsonObjectEncoder[CNil] =
  //   pureObj(cnil => ???)

  // implicit def coproductEnc // ...

  // implicit def genericEnc // ...
}



final case class Employee(
  name    : String,
  number  : Int,
  manager : Boolean
)

final case class IceCream(
  name        : String,
  numCherries : Int,
  inCone      : Boolean
)

sealed trait Shape

final case class Rectangle(
  width: Double,
  height: Double
) extends Shape

final case class Circle(
  radius: Double
) extends Shape



object Main extends Demo {

  val employee1 = Employee("Alice", 1, true)
  val employee2 = Employee("Bob", 2, false)
  val employee3 = Employee("Charlie", 3, false)

  val iceCream1 = IceCream("Cornetto", 0, true)
  val iceCream2 = IceCream("Sundae", 1, false)

  val shape1: Shape = Rectangle(3, 4)
  val shape2: Shape = Circle(1)

}


