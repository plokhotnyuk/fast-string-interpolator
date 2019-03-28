import com.typesafe.sbt.pgp.PgpKeys._
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbt.Keys.scalacOptions
import sbt.url
import scala.sys.process._

lazy val oldVersion = "git describe --abbrev=0".!!.trim.replaceAll("^v", "")

def mimaSettings = mimaDefaultSettings ++ Seq(
  mimaCheckDirection := {
    def isPatch = {
      val Array(newMajor, newMinor, _) = version.value.split('.')
      val Array(oldMajor, oldMinor, _) = oldVersion.split('.')
      newMajor == oldMajor && newMinor == oldMinor
    }

    if (isPatch) "both" else "backward"
  },
  mimaPreviousArtifacts := {
    def isCheckingRequired = {
      val Array(newMajor, newMinor, _) = version.value.split('.')
      val Array(oldMajor, oldMinor, _) = oldVersion.split('.')
      newMajor == oldMajor && (newMajor != "0" || newMinor == oldMinor)
    }

    if (isCheckingRequired) Set(organization.value %% moduleName.value % oldVersion)
    else Set()
  }
)

lazy val commonSettings = Seq(
  organization := "com.sizmek.fsi",
  organizationHomepage := Some(url("https://sizmek.com")),
  homepage := Some(url("https://github.com/Sizmek/fast-string-interpolator")),
  licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))),
  startYear := Some(2018),
  developers := List(
    Developer(
      id = "plokhotnyuk",
      name = "Andriy Plokhotnyuk",
      email = "andriy.plokhotnyuk@sizmek.com",
      url = url("https://twitter.com/aplokhotnyuk")
    ),
    Developer(
      id = "AnderEnder",
      name = "Andrii Radyk",
      email = "andrii.radyk@sizmek.com",
      url = url("https://github.com/AnderEnder")
    ),
  ),
  scalaVersion := "2.12.8",
  resolvers += Resolver.jcenterRepo,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Xfuture",
    "-Xlint"
  ),
  testOptions in Test += Tests.Argument("-oDF"),
  parallelExecution in ThisBuild := false
)

lazy val noPublishSettings = Seq(
  skip in publish := true,
  publishArtifact := false,
  // Replace tasks to work around https://github.com/sbt/sbt-bintray/issues/93
  bintrayRelease := ((): Unit),
  bintrayEnsureBintrayPackageExists := ((): Unit),
  bintrayEnsureLicenses := ((): Unit),
)

lazy val publishSettings = Seq(
  bintrayOrganization := Some("sizmek"),
  bintrayRepository := "sizmek-maven",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Sizmek/fast-string-interpolator"),
      "scm:git@github.com:Sizmek/fast-string-interpolator.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  // FIXME: remove setting of overwrite flag when the following issue will be fixed: https://github.com/sbt/sbt/issues/3725
  publishConfiguration := publishConfiguration.value.withOverwrite(isSnapshot.value),
  publishSignedConfiguration := publishSignedConfiguration.value.withOverwrite(isSnapshot.value),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(isSnapshot.value),
  publishLocalSignedConfiguration := publishLocalSignedConfiguration.value.withOverwrite(isSnapshot.value)
)

lazy val `fast-string-interpolator` = project.in(file("."))
  .aggregate(`fsi-macros`, `fsi-benchmark-core`, `fsi-benchmark`)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)

lazy val `fsi-macros` = project
  .settings(commonSettings: _*)
  .settings(mimaSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    crossScalaVersions := Seq("2.13.0-M5", "2.13.0-M4", "2.12.8", "2.11.12"),
    libraryDependencies ++= {
      val scalatestV =
        if (scalaVersion.value == "2.13.0-M4") "3.0.6-SNAP2"
        else "3.0.6-SNAP3"
      Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scalatest" %% "scalatest" % scalatestV % Test
      )
    }
  )

lazy val `fsi-benchmark-core` = project
  .enablePlugins(JmhPlugin)
  .dependsOn(`fsi-macros`)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    crossScalaVersions := Seq("2.13.0-M5", "2.13.0-M4", "2.12.8", "2.11.12"),
    libraryDependencies ++= {
      val scalatestV =
        if (scalaVersion.value == "2.13.0-M4") "3.0.6-SNAP2"
        else "3.0.6-SNAP3"
      Seq(
        "pl.project13.scala" % "sbt-jmh-extras" % "0.3.6",
        "org.scalatest" %% "scalatest" % scalatestV % Test
      )
    }
  )

lazy val `fsi-benchmark` = project
  .enablePlugins(JmhPlugin)
  .dependsOn(`fsi-benchmark-core`)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    crossScalaVersions := Seq("2.12.8", "2.11.12"),
    libraryDependencies ++= Seq(
      "com.dongxiguo" %% "fastring" % "1.0.0",
      "com.outr" %% "perfolation" % "1.1.0",
      "com.outr" %% "scribe-slf4j" % "2.7.2" % Test,
      "org.scalatest" %% "scalatest" % "3.0.7" % Test
    )
  )
