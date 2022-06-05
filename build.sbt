scalaVersion in ThisBuild := "2.13.5"

scalacOptions in Global ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-feature",
  // Linter configuration (replaces -Xfatal-warnings, -Xlint, etc). More info here:
  // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
  List(
    "-Wconf",
    List(
      // We need to disable the byname-implicit warning in Scala 2.13.3+
      "cat=lint-byname-implicit:silent",
      // Anything else can be an error, though
      "any:error",
    ).mkString(",")
  ).mkString(":")
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

lazy val helloworld =
  project.in(file("helloworld")).dependsOn(common)

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

lazy val root = project.in(file(".")).aggregate(
  helloworld,
  representations,
  csv,
  literaltypes,
  json,
  numfields,
  random,
  migrations,
  mapping
)
