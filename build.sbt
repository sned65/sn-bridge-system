name := "bridge-system"
organization := "sne"
maintainer := "nicksm65@gmail.com"
version := "0.9.0"

scalaVersion := "2.13.4"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked"
)

val fopLibrary = Seq(
    "org.apache.xmlgraphics" % "fop" % "2.5" excludeAll(
      ExclusionRule(organization = "javax.media"),
      ExclusionRule(organization = "com.sun.media"),
    )
)

libraryDependencies ++= Seq(
    "com.github.scopt" %% "scopt" % "4.0.0",
    "com.lihaoyi" %% "fastparse" % "2.2.2",
    "com.lihaoyi" %% "os-lib" % "0.7.1",
    "org.scalatest" %% "scalatest" % "3.2.2" % Test
) ++ fopLibrary

enablePlugins(JavaAppPackaging)

/////////////////////////////////////////////////////////////////////////////////////////
// Add the 'examples' directory into distributive
import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "examples")

/////////////////////////////////////////////////////////////////////////////////////////
// By default, the dist task will include the API documentation in the generated package.
// This is not necessary.
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
