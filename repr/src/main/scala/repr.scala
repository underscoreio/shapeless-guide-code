case class Employee(name: String, number: Int, manager: Boolean)

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

object Main {
  val employee = Employee("Bill", 1, true)
  val iceCream = IceCream("Cornetto", 0, true)

  import shapeless.Generic
  val employeeGen = Generic[Employee]
  val iceCreamGen = Generic[IceCream]

  def main(args: Array[String]): Unit = {
    println("Employee: " + employee)
    println("IceCream: " + iceCream)

    println("Employee Repr: " + employeeGen.to(employee))
    println("IceCream Repr: " + iceCreamGen.to(iceCream))

    println("Employee as IceCream: " + iceCreamGen.from(employeeGen.to(employee)))
    println("IceCream as Employee: " + employeeGen.from(iceCreamGen.to(iceCream)))
  }
}
