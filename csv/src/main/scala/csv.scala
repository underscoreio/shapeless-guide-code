trait CsvEncoder[A] {
  val width: Int
  def encode(value: A): List[String]
}

object CsvEncoder {
  def apply(implicit encoder: CsvEncoder[A]): CsvEncoder[A] =
    encoder

  def pure[A](func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      def apply(value: A): List[String] =
        func(value)
    }
}

object Main {
  def main(args: Array[String]): Unit = {
    // TODO: Write code
  }
}