name := "bridge-system"
organization := "sne"
maintainer := "nicksm65@gmail.com"
version := "0.9.2"

scalaVersion := "2.13.6"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked"
)

val fopLibrary = Seq(
    "org.apache.xmlgraphics" % "fop" % "2.6" excludeAll(
      ExclusionRule(organization = "javax.media"),
      ExclusionRule(organization = "com.sun.media"),
    )
)

libraryDependencies ++= Seq(
    "com.github.scopt" %% "scopt" % "4.0.1",
    "com.lihaoyi" %% "fastparse" % "2.3.2",
    "com.lihaoyi" %% "os-lib" % "0.7.8",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test
) ++ fopLibrary

enablePlugins(JavaAppPackaging)

/////////////////////////////////////////////////////////////////////////////////////////
// Add the 'examples' directory into distributive
import com.typesafe.sbt.packager.MappingsHelper._
Universal / mappings ++= directory(baseDirectory.value / "examples")

/////////////////////////////////////////////////////////////////////////////////////////
// By default, the dist task will include the API documentation in the generated package.
// This is not necessary.
Compile / doc / sources := Seq.empty
Compile / packageDoc / publishArtifact := false
