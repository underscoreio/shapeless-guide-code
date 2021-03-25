import shapeless._
import shapeless.ops.coproduct
import shapeless.ops.nat

trait Random[A] {
  def get: A
}

object Random {
  def sample[A](size: Int)(implicit random: Random[A]): List[A] =
    (1 to size).map(_ => random.get).toList

  def createRandom[A](body: => A): Random[A] =
    new Random[A] {
      def get = body
    }

  implicit val intRandom: Random[Int] =
    createRandom(scala.util.Random.nextInt(10))

  implicit val booleanRandom: Random[Boolean] =
    createRandom(scala.util.Random.nextBoolean())

  implicit val charRandom: Random[Char] =
    createRandom(('A'.toInt + scala.util.Random.nextInt(26)).toChar)

  implicit val stringRandom: Random[String] =
    createRandom {
      (0 to intRandom.get).
        map(_ => charRandom.get).mkString
    }

  implicit def genericRandom[A, R](
    implicit
    gen: Generic.Aux[A, R],
    random: Lazy[Random[R]]
  ): Random[A] =
    createRandom {
      gen.from(random.value.get)
    }

  implicit val hnilRandom: Random[HNil] =
    createRandom {
      HNil
    }

  implicit def hlistRandom[H, T <: HList](
    implicit
    hRandom: Random[H],
    tRandom: Lazy[Random[T]]
  ): Random[H :: T] =
    createRandom {
      hRandom.get :: tRandom.value.get
    }

  implicit val cnilRandom: Random[CNil] =
    createRandom {
      throw new Exception("Mass hysteria!")
    }

  implicit def coproductRandom[H, T <: Coproduct, L <: Nat](
    implicit
    hRandom: Random[H],
    tRandom: Lazy[Random[T]],
    tLength: coproduct.Length.Aux[T, L],
    tLengthAsInt: nat.ToInt[L]
  ): Random[H :+: T] = {
    val len  = 1 + tLengthAsInt()
    val prob = 1.0 / len
    createRandom {
      if(scala.util.Random.nextDouble() < prob) {
        Inl(hRandom.get)
      } else {
        Inr(tRandom.value.get)
      }
    }
  }
}

object Main extends Demo {
  case class Coord(x: Int, y: Int)
  case class Cell(col: Char, row: Int)
  case object Foo

  scala.util.Random.setSeed(0)
  Random.sample[Coord](10).foreach(println)
  Random.sample[Cell](10).foreach(println)
  Random.sample[Foo.type](10).foreach(println)

  sealed trait Light
  case object Red   extends Light
  case object Amber extends Light
  case object Green extends Light

  Random.sample[Light](10).foreach(println)
}
