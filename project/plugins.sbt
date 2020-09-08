resolvers += Resolver.sonatypeRepo("staging")
resolvers += Resolver.bintrayIvyRepo("typesafe", "sbt-plugins")
resolvers += Resolver.bintrayIvyRepo("sbt", "sbt-plugin-releases")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.4")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.7")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.8.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

libraryDependencies ++= Seq(
  "org.openjdk.jmh" % "jmh-core" % "1.25.2",
  "org.openjdk.jmh" % "jmh-generator-asm" % "1.25.2",
  "org.openjdk.jmh" % "jmh-generator-bytecode" % "1.25.2",
  "org.openjdk.jmh" % "jmh-generator-reflection" % "1.25.2"
)