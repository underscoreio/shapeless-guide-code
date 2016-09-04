object Main {
  import shapeless.Witness
  import shapeless.labelled.{KeyTag, FieldType}
  import shapeless.syntax.singleton._



  // A brief introduction to type-level tagging:

  // Start with a plain Int:
  val x = 100

  // Create a "tag" (a trait with no runtime semantics):
  trait Tag

  // Apply the tag to the Int:
  val y = x.asInstanceOf[Int with Tag]

  // We can still use the tagged expression as a regular Int:
  val z = y + 1

  // At compile time the expression is
  // both of type Int and of type Tag.
  // We can hook into this using implicits:
  implicitly[y.type <:< Int]
  implicitly[y.type <:< Tag]



  // A brief introduction to literal/singleton types:

  // Regular Int:
  val someNumber = 6 * 7

  // Singleton typed Int.
  //
  // The type of each expression is '42', sometimes written "Int(42)".
  // This type is a subtype of Int. The only permitted value is 42.
  val theAnswer: 42 = 6 * 7  // syntax for Typelevel Scala 2.11.8+ or Lightbend Scala 2.12.1+
  val theAnswer2 = 42.narrow // syntax for older Scala compilers



  // Shapeless provides nice syntax (->>) to let us tag an expression
  // with the literal type of another expression.
  //
  // The resulting type is `FieldType[K, A]`
  // where `K` is the tag type and `A` is the result type.
  //
  // `FieldType[K, A]` is an alias for `A with KeyTag[K, A]`.
  // `KeyTag` is a phantom type similar to our `Tag` above.
  //
  // By making the literal type *and* the result type parts of the tag,
  // we get a bit more flexibility when we're searching for implicits.

  // Tagged expression:
  val tagged = "numCherries" ->> 100

  // "Untags" a tagged expresion by removing the tag type:
  def getFieldValue[K, A](value: FieldType[K, A]): A =
    value

  // Retrieves the tag from a tagged expression as a runtime value:
  def getTagAsValue[K, A](value: FieldType[K, A])(implicit witness: Witness.Aux[K]): K =
    witness.value



  def main(args: Array[String]): Unit = {
    println("x " + x)
    println("y " + y)
    println("z " + z)

    println("Some number " + someNumber)
    println("The answer " + theAnswer)

    println("Value of tagged expression: " + getFieldValue(tagged))
    println("Tag from tagged expression: " + getTagAsValue(tagged))
  }
}