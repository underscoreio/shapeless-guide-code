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

lazy val repr = project.in(file("repr"))
  .settings(
    initialCommands in console := s"""
      |import shapeless.Generic
      |import Main._
    """.trim.stripMargin
  )

lazy val generic = project.in(file("generic"))
  .settings(
    initialCommands in console := s"""
      |import shapeless.Generic
      |import shapeless.{HList, ::, HNil}
      |import Main._
    """.trim.stripMargin
  )
