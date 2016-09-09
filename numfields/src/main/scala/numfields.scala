import shapeless._
import shapeless.ops.nat._

/**
 * Type class calculating
 * the maximum number of fields
 * in instances of any ADT `A`.
 *
 * This kind of approach could be useful,
 * e.g., for preallocating space in a buffer.
 * However, it doesn't handle a lot of complexity.
 * It only takes fields in products into account.
 * It doesn't consider things like Lists etc.
 */
trait NumFields[A] {
  type Out <: Nat
  def value(implicit toInt: ToInt[Out]): Int =
    toInt.apply()
}



object NumFields extends NumFieldsInstances

trait NumFieldsFunctions {
  type Aux[A, N <: Nat] = NumFields[A] { type Out = N }

  def apply[A](implicit numFields: NumFields[A]): Aux[A, numFields.Out] =
    numFields
}

trait NumFieldsInstances extends LowPriorityNumFieldsInstances {
  implicit val hnilInstance: Aux[HNil, Nat._0] =
    new NumFields[HNil] { type Out = Nat._0 }

  implicit def hlistInstance[Head, Tail <: HList, HeadSize <: Nat, TailSize <: Nat, TotalSize <: Nat](
    implicit
    hSize: Lazy[NumFields.Aux[Head, HeadSize]],
    tSize: NumFields.Aux[Tail, TailSize],
    sum: Sum.Aux[HeadSize, TailSize, TotalSize]
  ): Aux[Head :: Tail, TotalSize] =
    new NumFields[Head :: Tail] { type Out = TotalSize }

  implicit val cnilInstance: Aux[CNil, Nat._0] =
    new NumFields[CNil] { type Out = Nat._0 }

  implicit def coproductInstance[Head, Tail <: Coproduct, HeadSize <: Nat, TailSize <: Nat, MaxSize <: Nat](
    implicit
    hSize: Lazy[NumFields.Aux[Head, HeadSize]],
    tSize: NumFields.Aux[Tail, TailSize],
    max: Max.Aux[HeadSize, TailSize, MaxSize]
  ): Aux[Head :+: Tail, MaxSize] =
    new NumFields[Head :+: Tail] { type Out = MaxSize }

  implicit def genericInstance[A, Repr, Size <: Nat](
    implicit
    gen: Generic.Aux[A, Repr],
    size: Lazy[NumFields.Aux[Repr, Size]]
  ): Aux[A, Size] =
    new NumFields[A] { type Out = Size }
}

trait LowPriorityNumFieldsInstances extends NumFieldsFunctions {
  implicit def anyInstance[A](implicit ev: LowPriority): Aux[A, Nat._1] =
    new NumFields[A] { type Out = Nat._1 }
}



sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape



object Main extends Demo {
  println("Number of fields in Shape: "     + NumFields[Shape].value)
  println("Number of fields in Rectangle: " + NumFields[Rectangle].value)
  println("Number of fields in Circle: "    + NumFields[Circle].value)
}