scalaVersion in ThisBuild := "2.13.5"

scalacOptions in Global ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Yliteral-types"
)

libraryDependencies in Global ++= Seq(
  "com.chuusai"   %% "shapeless"     % "2.3.3",
  "org.typelevel" %% "cats-core"     % "2.2.0",
  "io.circe"      %% "circe-core"    % "0.13.0",
  "io.circe"      %% "circe-generic" % "0.13.0",
  "io.circe"      %% "circe-parser"  % "0.13.0",
  "org.scalactic" %% "scalactic"     % "3.2.5" % Test,
  "org.scalatest" %% "scalatest"     % "3.2.5" % Test
)

lazy val common =
  project.in(file("common"))

lazy val representations =
  project.in(file("representations")).dependsOn(common)

lazy val csv =
  project.in(file("csv")).dependsOn(common)

lazy val literaltypes =
  project.in(file("literaltypes")).dependsOn(common)

lazy val json =
  project.in(file("json")).dependsOn(common)

lazy val numfields =
  project.in(file("numfields")).dependsOn(common)

lazy val random =
  project.in(file("random")).dependsOn(common)

lazy val migrations =
  project.in(file("migrations")).dependsOn(common)

lazy val mapping =
  project.in(file("mapping")).dependsOn(common)

lazy val root = project.in(file("."))
  .aggregate(
    representations,
    csv,
    literaltypes,
    json,
    numfields,
    random,
    migrations,
    mapping
  )
