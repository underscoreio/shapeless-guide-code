import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import shapeless._

case class WaterQuality(
  location: Gps,
  temperature: Int,
  phosphate: Double,
  coliforms: Boolean
)

case class Gps(
  lat: Double,
  lng: Double
)

object Main extends Demo {
  val waterQuality =
    WaterQuality(
      location = Gps(51.522873, -0.007854),
      temperature = 6,
      phosphate = 0.9,
      coliforms = false
    )

  case class Index[A](id: String, func: WaterQuality => A) {
    def apply(value: WaterQuality): A = func(value)
  }

  val latitude: Index[Double]   = Index("latitude", _.location.lat)
  val longitude: Index[Double]  = Index("longitude", _.location.lng)
  val temperature: Index[Int]   = Index("temperature", _.temperature)
  val phosphate: Index[Double]  = Index("phosphate", _.phosphate)
  val coliforms: Index[Boolean] = Index("coliforms", _.coliforms)

  val allIndices =
    latitude ::
      longitude ::
      temperature ::
      phosphate ::
      coliforms ::
      Nil

  def applyIndex(index: Index[_]): Any =
    (index.id, index.apply(waterQuality))

  val attributes =
    allIndices.map(applyIndex)

  // val jsonAttributes =
  //   attributes.map(???).toMap.asJson

  println(waterQuality)
  println(allIndices)
  println(attributes)
  // println(jsonAttributes)
}
