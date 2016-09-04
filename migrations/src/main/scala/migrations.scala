import cats.Monoid
import cats.instances.all._
import shapeless._
import shapeless.labelled.{field, FieldType}
import shapeless.ops.hlist
import shapeless.ops.coproduct

trait Migration[A, B] {
  def apply(original: A): B
}

object MigrationSyntax {
  implicit class MigrationOps[A](original: A) {
    def migrateTo[B](implicit migration: Migration[A, B]): B =
      migration(original)
  }
}

object Migration {
  def apply[A, B](implicit migration: Migration[A, B]): Migration[A, B] =
    migration

  def pure[A, B](func: A => B): Migration[A, B] =
    new Migration[A, B] {
      def apply(original: A): B =
        func(original)
    }

  implicit def genericMigration[
    A,
    B,
    ARepr        <: HList,
    BRepr        <: HList,
    CommonFields <: HList,
    AddedFields  <: HList,
    Unaligned    <: HList
  ](
    implicit
    aGen    : LabelledGeneric.Aux[A, ARepr],
    bGen    : LabelledGeneric.Aux[B, BRepr],
    inter   : hlist.Intersection.Aux[ARepr, BRepr, CommonFields],
    diff    : hlist.Diff.Aux[BRepr, CommonFields, AddedFields],
    empty   : Empty[AddedFields],
    prepend : hlist.Prepend.Aux[AddedFields, CommonFields, Unaligned],
    align   : hlist.Align[Unaligned, BRepr]
  ): Migration[A, B] =
    pure { a =>
      val aRepr     = aGen.to(a)
      val common    = inter(aRepr)
      val added     = empty.value
      val unaligned = prepend(added, common)
      val bRepr     = align(unaligned)
      bGen.from(bRepr)
    }
}

case class Empty[A](value: A)

object Empty {
  def apply[A](implicit empty: Empty[A]): Empty[A] =
    empty

  implicit def monoidEmpty[A](implicit monoid: Monoid[A]): Empty[A] =
    Empty(monoid.empty)

  implicit def hnilEmpty: Empty[HNil] =
    Empty(HNil)

  implicit def hlistEmpty[K <: Symbol, H, T <: HList](
    implicit
    hEmpty: Lazy[Empty[H]],
    tEmpty: Empty[T]
  ): Empty[FieldType[K, H] :: T] =
    Empty(field[K](hEmpty.value.value) :: tEmpty.value)
}

object Main {
  import MigrationSyntax._

  case class SameA(a: String, b: Int, c: Boolean)
  case class SameB(a: String, b: Int, c: Boolean)

  case class DropFieldA(a: String, b: Int, c: Boolean)
  case class DropFieldB(a: String, c: Boolean)

  case class AddFieldA(a: String)
  case class AddFieldB(a: String, z: Int)

  case class ReorderA(a: String, z: Int)
  case class ReorderB(z: Int, a: String)

  case class KitchenSinkA(a: String, b: Int, c: Boolean)
  case class KitchenSinkB(c: Boolean, z: Option[String], a: String)

  def main(args: Array[String]): Unit = {
    println(SameA("abc", 123, true).migrateTo[SameA])
    println(DropFieldA("abc", 123, true).migrateTo[DropFieldA])
    println(AddFieldA("abc").migrateTo[AddFieldA])
    println(ReorderA("abc", 123).migrateTo[ReorderA])
    println(KitchenSinkA("abc", 123, true).migrateTo[KitchenSinkB])
  }
}