name := "akka-serialization-test"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.11.7"

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

libraryDependencies ++= {
  val akkaVersion = "2.4.2-RC1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "org.json4s" %% "json4s-native" % "3.2.10",
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.2.1" % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "2.2.4" % Test,
    "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
  )
}

fork in Test := true

javaOptions in Test ++= Seq("-Xms30m", "-Xmx30m")

parallelExecution in Test := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

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
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

enablePlugins(AutomateHeaderPlugin)

// enable protobuf plugin //
// see: https://trueaccord.github.io/ScalaPB/sbt-settings.html
import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

PB.protobufSettings

// protoc-jar which is on the sbt classpath //
// see: https://github.com/os72/protoc-jar
PB.runProtoc in PB.protobufConfig := (args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))