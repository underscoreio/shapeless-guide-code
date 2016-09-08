**SLIDES - INTRO [0:00]**

**SLIDES - PRODUCTS**

**EXAMPLE - PRODUCTS (representations.scala)**

- Summon a generic for Employee
- Summon a generic for IceCream
- Demonstrate Employee and IceCream Reprs
- Convert IceCream to Employee

**SLIDES - COPRODUCTS**

**EXAMPLE - COPRODUCTS (representations.scala)**

- Copy and paste Shape code in
- Summon a generic for Shape
- Demonstrate Shape Reprs for Rectangle and Circle

**SLIDES - WRITING GENERIC CODE [30:00]**

**EXAMPLE - TYPE CLASS INSTANCES (csv.scala)**

- Ignore Lazy
- Instances for HNil and ::
- Instances for CNil and :+:
- Hand-wave over the dependent types part of Generic:
  - Try to use gen.Repr
  - Correct it to a type refinement

**SLIDES - DEPENDENT TYPES [60:00]**

**EXAMPLE - AUX PATTREN (csv.scala)**

- Correct genericEnc to the Aux pattern

**EXAMPLE - IMPLICIT DIVERGENCE AND LAZY (csv.scala)**

- Copy and paste code for Tree
- Demonstrate "implicit not found"

**SLIDES - IMPLICIT DIVERGENCE AND LAZY**

**EXAMPLE - IMPLICIT DIVERGENCE AND LAZY**

- Add Lazy to instances for ::, :+:, and Generic

**SLIDES - LITERAL TYPES**

**EXAMPLE - LITERAL TYPES (REPL)**

- Create two vars:
  - an Int
  - an instance of 42 (use SIP-23 syntax)
- Show the types of each
- Attempt to assign 43 to each

**SLIDES - TYPE TAGGING**

**EXAMPLE - TYPE TAGGING (REPL)**

- Use shapeless.syntax.singleton.->>
  to create an Int tagged with a String
- Show the type (V with KeyTag[K, V])
- Show the FieldType type alias
- Demonstrate what a labelled HList might look like

**EXAMPLE - JSON (json.scala)**

- Write JsonObjectEncoders for
  HNil, ::, CNil, :+:, and Generic
- Use Lazy immediately

**SLIDES - Putting It All Together**

**EXAMPLE - Migrations (migrations.scala)**

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
  inter   : Intersection.Aux[ARepr, BRepr, CommonFields],
  diff    : Diff.Aux[BRepr, CommonFields, AddedFields],
  empty   : Empty[AddedFields],
  prepend : Prepend.Aux[AddedFields, CommonFields, Unaligned],
  align   : Align[Unaligned, BRepr]
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

**SLIDES - Summary**
