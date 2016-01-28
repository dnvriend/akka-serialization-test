resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases"

// to show a dependency graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.0")

// to format scala source code
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// enable updating file headers eg. for copyright
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "1.5.0")

// decode scala errors to human readable form
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.1")

// enable compiling *.proto files
// see: http://trueaccord.github.io/ScalaPB/sbt-settings.html
addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.21")

// compiling *.proto files without protoc (for self contained builds)
// see: https://github.com/os72/protoc-jar
libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.0.0-b2"
)