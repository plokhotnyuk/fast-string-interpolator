import sbt.url
import org.scalajs.linker.interface.{CheckedBehavior, ESVersion}
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
  scalaVersion := "2.12.17",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) => Seq("-language:higherKinds")
    case Some((2, 13)) => Seq("-Wnonunit-statement")
    case Some((3, _)) => Seq("-Xcheck-macros")
    case _ => Seq()
  }),
  Test / testOptions += Tests.Argument("-oDF"),
  ThisBuild / parallelExecution := false,
  publishTo := sonatypePublishToBundle.value,
  sonatypeProfileName := "com.github.plokhotnyuk",
  versionScheme := Some("early-semver"),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/plokhotnyuk/fast-string-interpolator"),
      "scm:git@github.com:plokhotnyuk/fast-string-interpolator.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false }
)

lazy val jsSettings = Seq(
  scalaJSLinkerConfig ~= {
    _.withSemantics({
      _.optimized
        .withProductionMode(true)
        .withAsInstanceOfs(CheckedBehavior.Unchecked)
        .withStringIndexOutOfBounds(CheckedBehavior.Unchecked)
        .withArrayIndexOutOfBounds(CheckedBehavior.Unchecked)
    }).withClosureCompiler(true)
      .withESFeatures(_.withESVersion(ESVersion.ES2015))
      .withModuleKind(ModuleKind.CommonJSModule)
  },
  coverageEnabled := false // FIXME: Unexpected crash of scalac
)

lazy val nativeSettings = Seq(
  coverageEnabled := false // FIXME: Unexpected linking error
)

lazy val noPublishSettings = Seq(
  publish / skip := true,
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
    def isCheckingRequired: Boolean = {
      val Array(newMajor, _, _) = version.value.split('.')
      val Array(oldMajor, _, _) = oldVersion.split('.')
      newMajor == oldMajor
    }

    if (isCheckingRequired) Set(organization.value %%% moduleName.value % oldVersion)
    else Set()
  },
  mimaReportSignatureProblems := true
)

lazy val `fast-string-interpolator` = project.in(file("."))
  .aggregate(`fsi-macrosJVM`, `fsi-macrosJS`, `fsi-macrosNative`, `fsi-benchmark-coreJVM`, `fsi-benchmarkJVM`)
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val `fsi-macros` = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    crossScalaVersions := Seq("3.2.2", "2.13.10", scalaVersion.value, "2.11.12"),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => Seq(
          "org.scala-lang" % "scala-reflect" % scalaVersion.value,
          "org.scalatest" %% "scalatest" % "3.2.15" % Test
        )
        case _ => Seq("org.scalatest" %% "scalatest" % "3.2.15" % Test)
      }
    }
  )

lazy val `fsi-macrosJVM` = `fsi-macros`.jvm

lazy val `fsi-macrosJS` = `fsi-macros`.js
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.4.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.4.0"
    )
  )

lazy val `fsi-macrosNative` = `fsi-macros`.native
  .settings(nativeSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.4.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.4.0"
    )
  )

lazy val `fsi-benchmark-core` = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .dependsOn(`fsi-macros`)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Seq("2.13.10", scalaVersion.value, "2.11.12"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    )
  )

lazy val `fsi-benchmark-coreJVM` = `fsi-benchmark-core`.jvm
  .enablePlugins(JmhPlugin)

lazy val `fsi-benchmark` = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .dependsOn(`fsi-benchmark-core`)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Seq(scalaVersion.value),
    libraryDependencies ++= Seq(
      "com.dongxiguo" %% "fastring" % "1.0.0",
      "com.outr" %% "perfolation" % "1.1.7",
      "com.outr" %% "scribe-slf4j" % "2.7.13" % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    )
  )

lazy val `fsi-benchmarkJVM` = `fsi-benchmark`.jvm
  .enablePlugins(JmhPlugin)
