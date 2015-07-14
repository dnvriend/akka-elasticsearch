name := "akka-elasticsearch"

organization := "com.github.dnvriend"

version := "1.0.7"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaVersion = "2.3.12"
  Seq(
    "com.typesafe.akka"      %% "akka-actor"     % akkaVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.6.5",
    "org.scalatest"          %% "scalatest"      % "2.2.4" % "test,it"
  )
}

autoCompilerPlugins := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

fork in Test := true

fork in IntegrationTest := true

testForkedParallel in Test := false

testForkedParallel in IntegrationTest := false

parallelExecution in Test := false

parallelExecution in IntegrationTest := false

// enable scala code formatting //
import scalariform.formatter.preferences._

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(RewriteArrowSymbols, true)

// enable updating file headers //
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2015", "Dennis Vriend"),
  "conf" -> Apache2_0("2015", "Dennis Vriend", "#")
)

// enable plugins //
lazy val akkaElasticSearch = project.in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
