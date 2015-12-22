resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases"

// to show a dependency graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.0")

// to format scala source code
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// enable updating file headers eg. for copyright
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "1.5.0")

// enable compiling *.proto files
addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.4.19")

// compiling *.proto files without protoc (for self contained builds)
libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "2.x.5"
)