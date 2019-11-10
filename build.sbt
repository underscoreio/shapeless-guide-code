scalaVersion in ThisBuild := "2.13.1"

// Refine scalac params from tpolecat
scalacOptions --= Seq(
  "-Xfatal-warnings"
)

libraryDependencies in Global ++= Seq(
  "com.chuusai"   %% "shapeless"     % "2.3.3",
  "org.typelevel" %% "cats-core"     % "2.0.0",
  "io.circe"      %% "circe-core"    % "0.12.3",
  "io.circe"      %% "circe-generic" % "0.12.3",
  "io.circe"      %% "circe-parser"  % "0.12.3",
  "org.scalactic" %% "scalactic"     % "3.0.8" % Test,
  "org.scalatest" %% "scalatest"     % "3.0.8" % Test
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

lazy val root = project
  .in(file("."))
  .aggregate(
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

addCompilerPlugin(scalafixSemanticdb)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("lint", "; compile:scalafix --check ; test:scalafix --check")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("cov", "; clean; coverage; test; coverageReport")
