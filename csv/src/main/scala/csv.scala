trait CsvEncoder[A] {
  val width: Int
  def encode(value: A): List[String]
}

object CsvEncoder extends CsvEncoderFunctions
  with CsvEncoderInstances

trait CsvEncoderFunctions {
  def apply[A](implicit encoder: CsvEncoder[A]): CsvEncoder[A] =
    encoder

  def writeCsv[A](values: List[A])(implicit encoder: CsvEncoder[A]): String =
    values.map(value => encoder.encode(value).mkString(",")).mkString("\n")

  def pure[A](w: Int)(func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      val width = w
      def encode(value: A): List[String] =
        func(value)
    }
}

trait CsvEncoderInstances extends LowPriorityCsvEncoderInstances {
  self: CsvEncoderFunctions =>

  implicit val stringEncoder: CsvEncoder[String] =
    pure(1)(str => List(str))

  implicit val intEncoder: CsvEncoder[Int] =
    pure(1)(num => List(num.toString))

  implicit val doubleEncoder: CsvEncoder[Double] =
    pure(1)(num => List(num.toString))

  implicit val booleanEncoder: CsvEncoder[Boolean] =
    pure(1)(bool => List(if(bool) "yes" else "no"))

  implicit def optionEncoder[A](implicit encoder: CsvEncoder[A]): CsvEncoder[Option[A]] =
    pure(encoder.width)(opt => opt.map(encoder.encode).getOrElse(List.fill(encoder.width)("")))
}

trait LowPriorityCsvEncoderInstances {
  self: CsvEncoderFunctions =>

  import shapeless.{HList, ::, HNil, Lazy}

  implicit val hnilEncoder: CsvEncoder[HNil] =
    pure(0)(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](
    implicit
    hEncoder: Lazy[CsvEncoder[H]],
    tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :: T] =
    pure(hEncoder.value.width + tEncoder.width) {
      case h :: t =>
        hEncoder.value.encode(h) ++ tEncoder.encode(t)
    }

  import shapeless.{Coproduct, :+:, CNil, Inl, Inr}

  implicit val cnilEncoder: CsvEncoder[CNil] =
    pure(0)(cnil => ???)

  implicit def coproductEncoder[H, T <: Coproduct](
    implicit
    hEncoder: Lazy[CsvEncoder[H]],
    tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :+: T] =
    pure(hEncoder.value.width + tEncoder.width) {
      case Inl(h) => hEncoder.value.encode(h) ++ List.fill(tEncoder.width)("")
      case Inr(t) => List.fill(hEncoder.value.width)("") ++ tEncoder.encode(t)
    }

  import shapeless.Generic

  implicit def genericEncoder[A, R](
    implicit
    gen: Generic.Aux[A, R],
    enc: Lazy[CsvEncoder[R]]
  ): CsvEncoder[A] =
    pure(enc.value.width)(a => enc.value.encode(gen.to(a)))
}

object Main {
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
    println("Shapes as CSV:\n" + CsvEncoder.writeCsv(shapes))
    println("Optional shapes " + optShapes)
    println("Optional shapes as CSV:\n" + CsvEncoder.writeCsv(optShapes))
  }
}