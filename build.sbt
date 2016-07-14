name := "akka-serialization-test"

organization := "com.github.dnvriend"

version := "1.0.3"

scalaVersion := "2.11.8"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val akkaVersion = "2.4.8"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.7" % Test,
    "com.twitter" %% "chill-akka" % "0.8.0",
    "com.sksamuel.avro4s" %% "avro4s-core" % "1.5.1",
    "org.apache.avro" % "avro" % "1.8.1",
    "org.json4s" %% "json4s-native" % "3.4.0",
    "com.trueaccord.scalapb" %% "scalapb-json4s" % "0.1.1",
    "org.scalaz" %% "scalaz-core" % "7.2.4",
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.3.2" % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "2.2.6" % Test,
    "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
  )
}

fork in Test := true

scalacOptions ++= Seq("-feature", "-language:higherKinds", "-language:implicitConversions", "-deprecation", "-Ybackend:GenBCode", "-Ydelambdafy:method", "-target:jvm-1.8")

javaOptions in Test ++= Seq("-Xms30m", "-Xmx30m")

parallelExecution in Test := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

// enable scala code formatting //
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

// Scalariform settings
SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
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

PB.javaConversions in PB.protobufConfig := true

// protoc-jar which is on the sbt classpath //
// see: https://github.com/os72/protoc-jar
PB.runProtoc in PB.protobufConfig := (args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))

// other options
//protobuf 2.4.1: -v2.4.1, -v241
//protobuf 2.5.0: -v2.5.0, -v250
//protobuf 2.6.1: -v2.6.1, -v261
//protobuf 3.0.0: -v3.0.0, -v300