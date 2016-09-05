/*

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

*/