import shapeless._

case class Greeting(message: String)

object Main extends Demo {
  val generic = Generic[Greeting]

  val hlist: String :: HNil =
    "Hello from shapeless!" :: HNil

  val greeting: Greeting =
    generic.from(hlist)

  println(greeting.message)
}
