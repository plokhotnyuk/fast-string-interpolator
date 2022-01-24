import sbt.url
import scala.sys.process._

lazy val oldVersion = "git describe --abbrev=0".!!.trim.replaceAll("^v", "")

lazy val commonSettings = Seq(
  organization := "com.github.plokhotnyuk.fsi",
  organizationHomepage := Some(url("https://github.com/plokhotnyuk")),
  homepage := Some(url("https://github.com/plokhotnyuk/fast-string-interpolator")),
  licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))),
  startYear := Some(2018),
  developers := List(
    Developer(
      id = "plokhotnyuk",
      name = "Andriy Plokhotnyuk",
      email = "plokhotnyuk@gmail.com",
      url = url("https://github.com/aplokhotnyuk")
    ),
    Developer(
      id = "AnderEnder",
      name = "Andrii Radyk",
      email = "ander.ender@gmail.com",
      url = url("https://github.com/AnderEnder")
    ),
  ),
  scalaVersion := "2.12.15",
  resolvers += Resolver.sonatypeRepo("staging"),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-target:jvm-1.8",
        "-feature",
        "-unchecked",
        "-Ywarn-dead-code",
        "-Xlint"
      )
      case _ => Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-feature",
        "-unchecked",
        "-Xcheck-macros"
      )
    }
  },
  testOptions in Test += Tests.Argument("-oDF"),
  parallelExecution in ThisBuild := false,
  publishTo := sonatypePublishToBundle.value,
  sonatypeProfileName := "com.github.plokhotnyuk",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/plokhotnyuk/fast-string-interpolator"),
      "scm:git@github.com:plokhotnyuk/fast-string-interpolator.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false }
)

lazy val noPublishSettings = Seq(
  skip in publish := true,
  mimaPreviousArtifacts := Set()
)

lazy val publishSettings = Seq(
  packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> moduleName.value),
  mimaCheckDirection := {
    def isPatch: Boolean = {
      val Array(newMajor, newMinor, _) = version.value.split('.')
      val Array(oldMajor, oldMinor, _) = oldVersion.split('.')
      newMajor == oldMajor && newMinor == oldMinor
    }

    if (isPatch) "both" else "backward"
  },
  mimaPreviousArtifacts := {
    val Some((scalaMajor, _)) = CrossVersion.partialVersion(scalaVersion.value)

    def isCheckingRequired: Boolean = {
      val Array(newMajor, _, _) = version.value.split('.')
      val Array(oldMajor, _, _) = oldVersion.split('.')
      newMajor == oldMajor && scalaMajor == 2 // FIXME remove scala version check after release for Scala 3
    }

    if (isCheckingRequired) Set(organization.value %% moduleName.value % oldVersion)
    else Set()
  },
  mimaReportSignatureProblems := true
)

lazy val `fast-string-interpolator` = project.in(file("."))
  .aggregate(`fsi-macros`, `fsi-benchmark-core`, `fsi-benchmark`)
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val `fsi-macros` = project
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    crossScalaVersions := Seq("3.1.1", "2.13.8", scalaVersion.value, "2.11.12"),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => Seq(
          "org.scala-lang" % "scala-reflect" % scalaVersion.value,
          "org.scalatest" %% "scalatest" % "3.2.11" % Test
        )
        case _ => Seq("org.scalatest" %% "scalatest" % "3.2.11" % Test)
      }
    }
  )

lazy val `fsi-benchmark-core` = project
  .enablePlugins(JmhPlugin)
  .dependsOn(`fsi-macros`)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Seq("2.13.8", scalaVersion.value, "2.11.12"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.11" % Test
    )
  )

lazy val `fsi-benchmark` = project
  .enablePlugins(JmhPlugin)
  .dependsOn(`fsi-benchmark-core`)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Seq(scalaVersion.value),
    libraryDependencies ++= Seq(
      "com.dongxiguo" %% "fastring" % "1.0.0",
      "com.outr" %% "perfolation" % "1.1.7",
      "com.outr" %% "scribe-slf4j" % "2.7.13" % Test,
      "org.scalatest" %% "scalatest" % "3.2.11" % Test
    )
  )
