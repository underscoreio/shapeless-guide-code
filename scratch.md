EMPLOYEE AND ICECREAM
=====================

```scala
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

val employee = Employee("Bill", 1, true)
val employee = Employee("Milton", 3, false)

val iceCream = IceCream("Cornetto", 0, true)
val iceCream = IceCream("Sundae", 1, false)

val employees = List(
  Employee("Bill", 1, true),
  Employee("Peter", 2, false),
  Employee("Milton", 3, false)
)

val iceCreams = List(
  IceCream("Cornetto", 0, true),
  IceCream("Sundae", 1, false),
  IceCream("Ice Lolly", 0, false)
)
```

SHAPE
=====

```scala
sealed trait Shape

final case class Rectangle(
  width: Double,
  height: Double
) extends Shape

final case class Circle(
  radius: Double
) extends Shape

val shape1: Shape = Rectangle(3, 4)
val shape2: Shape = Circle(1)

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
```

TREE
====

```scala
sealed trait Tree
final case class Branch(left: Tree, right: Tree)
final case class Leaf(value: Int)
```

MIGRATIONS
==========

```scala
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
```

EMPTY
=====

```scala
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
```
