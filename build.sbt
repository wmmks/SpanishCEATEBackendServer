name := """SpanishCeateBackendServer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.0.8"
)
// https://mvnrepository.com/artifact/dom4j/dom4j
libraryDependencies += "dom4j" % "dom4j" % "1.6.1"




fork in run := true