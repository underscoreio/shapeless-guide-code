import shapeless.Generic

final case class Employee(name: String, number: Int, manager: Boolean)
final case class IceCream(name: String, numCherries: Int, inCone: Boolean)

sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape

object Main extends Demo {
  val employee = Employee("Bill", 1, true)
  val iceCream = IceCream("Cornetto", 0, true)

  val rectangle : Shape = Rectangle(1, 2)
  val circle    : Shape = Circle(3)

  val employeeGen = Generic[Employee]
  val iceCreamGen = Generic[IceCream]
  val shapeGen    = Generic[Shape]

  println("Employee: " + employee)
  println("IceCream: " + iceCream)

  println("Employee Repr: " + employeeGen.to(employee))
  println("IceCream Repr: " + iceCreamGen.to(iceCream))

  println("Employee as IceCream: " + iceCreamGen.from(employeeGen.to(employee)))
  println("IceCream as Employee: " + employeeGen.from(iceCreamGen.to(iceCream)))

  println("Shape Repr: " + shapeGen.to(rectangle))
  println("Shape Repr: " + shapeGen.to(circle))
}
