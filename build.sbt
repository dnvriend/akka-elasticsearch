name := "akka-elasticsearch"

organization := "com.github.dnvriend"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.2"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++=
  {
    val akkaV = "2.3.6"
    val jsonV = "1.2.6"
    val esV = "1.3.2"
    Seq(
      "org.scala-lang"          % "scala-library"                  % scalaVersion.value,
      "com.typesafe.akka"      %% "akka-actor"                     % akkaV,
      "io.spray"               %% "spray-json"                     % jsonV,
      "com.sksamuel.elastic4s" %% "elastic4s"                      % esV,
      "org.scalatest"          %% "scalatest"                      % "2.1.4" % "test"
    )
  }

autoCompilerPlugins := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

publishArtifact in Test := false

fork := true