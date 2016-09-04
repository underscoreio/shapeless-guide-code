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

object JsonEncoder extends JsonEncoderFunctions
  with JsonEncoderInstances

trait JsonEncoderFunctions {
  def apply[A](implicit encoder: JsonEncoder[A]): JsonEncoder[A] =
    encoder

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
}

trait JsonEncoderInstances extends LowPriorityJsonEncoderInstances {
  self: JsonEncoderFunctions =>

  implicit val stringEncoder: JsonEncoder[String] =
    pure(str => JsonString(str))

  implicit val intEncoder: JsonEncoder[Int] =
    pure(num => JsonNumber(num))

  implicit val doubleEncoder: JsonEncoder[Double] =
    pure(num => JsonNumber(num))

  implicit val booleanEncoder: JsonEncoder[Boolean] =
    pure(bool => JsonBoolean(bool))

  implicit def optionEncoder[A](implicit encoder: JsonEncoder[A]): JsonEncoder[Option[A]] =
    pure(opt => opt.map(encoder.encode).getOrElse(JsonNull))

  implicit def listEncoder[A](implicit encoder: JsonEncoder[A]): JsonEncoder[List[A]] =
    pure(list => JsonArray(list.map(encoder.encode)))
}

trait LowPriorityJsonEncoderInstances {
  self: JsonEncoderFunctions =>

  import shapeless.{HList, ::, HNil, Lazy, Witness}
  import shapeless.labelled.FieldType

  implicit val hnilEncoder: JsonObjectEncoder[HNil] =
    pureObj(hnil => JsonObject(Nil))

  implicit def hlistEncoder[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hEncoder: Lazy[JsonEncoder[H]],
    tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :: T] =
    pureObj {
      case h :: t =>
        val hField  = witness.value.name -> hEncoder.value.encode(h)
        val tFields = tEncoder.encode(t).fields
        JsonObject(hField :: tFields)
    }

  import shapeless.{Coproduct, :+:, CNil, Inl, Inr}

  implicit val cnilEncoder: JsonObjectEncoder[CNil] =
    pureObj(cnil => ???)

  implicit def coproductEncoder[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hEncoder: Lazy[JsonEncoder[H]],
    tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :+: T] =
    pureObj {
      case Inl(h) => JsonObject(List(witness.value.name -> hEncoder.value.encode(h)))
      case Inr(t) => tEncoder.encode(t)
    }

  import shapeless.LabelledGeneric

  implicit def genericEncoder[A, R](
    implicit
    gen: LabelledGeneric.Aux[A, R],
    enc: Lazy[JsonEncoder[R]]
  ): JsonEncoder[A] =
    pure(a => enc.value.encode(gen.to(a)))
}

object Main {
  import shapeless._

  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double) extends Shape

  val shapes: List[Shape] =
    List(
      Rectangle(1, 2),
      Circle(3),
      Rectangle(4, 5),
      Circle(6)
    )

  val optShapes: List[Option[Shape]] =
    List(
      Some(Rectangle(1, 2)),
      Some(Circle(3)),
      None,
      Some(Rectangle(4, 5)),
      Some(Circle(6)),
      None
    )

  def main(args: Array[String]): Unit = {
    println("Shapes " + shapes)
    println("Shapes as JSON: " + Json.stringify(Json.encode(shapes)))
    println("Optional shapes " + optShapes)
    println("Optional shapes as JSON: " + Json.stringify(Json.encode(optShapes)))
  }
}