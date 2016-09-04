object Main {
  import shapeless.Witness
  import shapeless.labelled.{KeyTag, FieldType}
  import shapeless.syntax.singleton._

  // Regular Int:
  val someNumber = 6 * 7

  // Singleton typed Int:
  val theAnswer: 42 = 6 * 7 // syntax for Typelevel Scala 2.11.8+ or Lightbend Scala 2.12.1+
  // val theAnswer = 42.narrow // syntax with older Scala

  // Tagged expression:
  val tagged = "numCherries" ->> 100

  // "Untags" a tagged expresion by removing the tag type:
  def getFieldValue[K, A](value: FieldType[K, A]): A =
    value

  // Retrieves the tag from a tagged expression as a runtime value:
  def getTagAsValue[K, A](value: FieldType[K, A])(implicit witness: Witness.Aux[K]): K =
    witness.value

  def main(args: Array[String]): Unit = {
    println("Some number " + someNumber)
    println("The answer " + theAnswer)

    println("Value of tagged expression: " + getFieldValue(tagged))
    println("Tag from tagged expression: " + getTagAsValue(tagged))
  }
}