scalaVersion in ThisBuild := "2.13.1"

libraryDependencies in Global ++= Seq(
  "com.chuusai"   %% "shapeless"     % "2.3.3",
  "org.typelevel" %% "cats-core"     % "2.0.0",
  "io.circe"      %% "circe-core"    % "0.12.3",
  "io.circe"      %% "circe-generic" % "0.12.3",
  "io.circe"      %% "circe-parser"  % "0.12.3"
)

lazy val commonSettings = Seq(
  scalacOptions --= Seq(
    "-Xfatal-warnings"
  )
)

lazy val common =
  project
    .in(file("common"))
    .settings(commonSettings)

lazy val helloworld =
  project
    .in(file("helloworld"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val representations =
  project
    .in(file("representations"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val csv =
  project
    .in(file("csv"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val literaltypes =
  project
    .in(file("literaltypes"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val json =
  project
    .in(file("json"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val numfields =
  project
    .in(file("numfields"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val random =
  project
    .in(file("random"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val migrations =
  project
    .in(file("migrations"))
    .dependsOn(common)
    .settings(commonSettings)

lazy val mapping =
  project
    .in(file("mapping"))
    .dependsOn(common)
    .settings(commonSettings)

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
  .settings(commonSettings)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("lint", "; compile:scalafix --check ; test:scalafix --check")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("cov", "; clean; coverage; test; coverageReport")
