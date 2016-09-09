import cats.Monoid
import cats.instances.all._
import shapeless._
import shapeless.labelled.{field, FieldType}
import shapeless.ops.hlist._



/**
 * Type class for converting values of type A
 * to another type B.
 */
trait Migration[A, B] {
  def apply(original: A): B
}



/** Type class instances for Migration */
object Migration {
  /**
   * This single rule covers
   * the following changes to product types:
   *
   * - fields that are in A but not in B;
   * - fields that are in B but not in A
   *   (provided we can calculate "empty" values as defaults)
   * - reorderings of fields
   *
   * The rule does *not* cover:
   *
   * - renaming fields
   * - changing the types of fields
   *
   * (these would make it a heuristic process)
   */
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
    inter   : Intersection.Aux[ARepr, BRepr, CommonFields],
    diff    : Diff.Aux[BRepr, CommonFields, AddedFields],
    empty   : Empty[AddedFields], // see below
    prepend : Prepend.Aux[AddedFields, CommonFields, Unaligned],
    align   : Align[Unaligned, BRepr]
  ): Migration[A, B] =
    new Migration[A, B] {
      def apply(a: A): B = {
        val aRepr     = aGen.to(a)
        val common    = inter(aRepr)
        val added     = empty.value
        val unaligned = prepend(added, common)
        val bRepr     = align(unaligned)
        bGen.from(bRepr)
      }
    }
}


/**
 * Helper type class for computing "empty" values
 * fill in new fields.
 */
case class Empty[A](value: A)

object Empty {
  /**
   * By default we rely on Cats' Monoid
   * to provide empty values.
   *
   * This has instances for the most common types:
   * Int, String, List, Option, etc.
   *
   * We could probably generate Monoid instances
   * for HNil and :: directly.
   * I haven't tried this yet.
   * Maybe github.com/milessabin/kittens provides them?
   */
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



// Some example case migrations:



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



object Main extends Demo {
  implicit class MigrationOps[A](original: A) {
    def migrateTo[B](implicit migration: Migration[A, B]): B =
      migration(original)
  }

  println(SameA("abc", 123, true).migrateTo[SameB])
  println(DropFieldA("abc", 123, true).migrateTo[DropFieldB])
  println(AddFieldA("abc").migrateTo[AddFieldB])
  println(ReorderA("abc", 123).migrateTo[ReorderB])
  println(KitchenSinkA("abc", 123, true).migrateTo[KitchenSinkB])
}