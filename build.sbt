name := "akka-elasticsearch"

organization := "com.github.dnvriend"

version := "1.0.3"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"     % "2.3.11",
  "io.spray"               %% "spray-json"     % "1.3.2",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.6.0",
  "org.scalatest"          %% "scalatest"      % "2.1.4" % "test"
)

autoCompilerPlugins := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true