scalaOrganization in ThisBuild := "org.typelevel"
scalaVersion      in ThisBuild := "2.11.8"

scalacOptions in Global ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Yliteral-types"
)

libraryDependencies in Global ++= Seq(
  "com.chuusai"   %% "shapeless" % "2.3.2",
  "org.typelevel" %% "cats"      % "0.7.0",
  "org.scalactic" %% "scalactic" % "2.2.6" % Test,
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)

def cmds(extraImports: String *) =
  initialCommands in console := s"""
    |import shapeless._
    |${extraImports.map("import " + _).mkString("\n")}
    |import Main._
  """.trim.stripMargin

lazy val representations =
  project.in(file("representations"))
  .settings(cmds())

lazy val generic =
  project.in(file("generic"))
  .settings(cmds())

lazy val literalTypes =
  project.in(file("literaltypes"))
  .settings(cmds())

lazy val labelledGeneric =
  project.in(file("labelledgeneric"))
  .settings(cmds("shapeless.labelled.FieldType"))

lazy val root = project.in(file("."))
  .aggregate(
    representations,
    generic,
    literalTypes,
    labelledGeneric
  )
