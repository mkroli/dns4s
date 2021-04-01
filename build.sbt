/*
 * Copyright 2014-2019 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import ReleaseTransformations._

lazy val scalaVersions = "2.13.5" :: "2.12.13" :: "2.11.12" :: "2.10.7" :: "3.0.0-RC2" :: Nil

lazy val guavaDependencies = Seq(
  "com.google.guava"         % "guava"  % "[15.0,24.0)",
  "com.google.code.findbugs" % "jsr305" % "[0.+,)" % "provided"
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor"   % "[2.3.0,2.7.0)" cross (CrossVersion.for3Use2_13),
  "com.typesafe.akka" %% "akka-testkit" % "[2.3.0,2.7.0)" % "test" cross (CrossVersion.for3Use2_13)
)

lazy val nettyDependencies = Seq(
  "io.netty" % "netty-handler" % "[4.0.0,4.2.0)"
)

lazy val scalaTestDependencies = Def.setting(
  Seq(
    "org.scalatest" %% "scalatest"         % "[3.2.2,3.2.6]" % "test",
    "org.scalatest" %% "scalatest-funspec" % "[3.2.2,3.2.6]" % "test"
  ) :+ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 10)) => "org.scalatestplus" %% "scalacheck-1-14" % "[3.2.2.0,3.2.6.0]" % "test"
    case _             => "org.scalatestplus" %% "scalacheck-1-15" % "[3.2.2.0,3.2.6.0]" % "test"
  })
)

def projectSettings(n: String, d: String) = Seq(
  name := n,
  description := d,
  organization := "com.github.mkroli",
  scalaVersion := scalaVersions.head,
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation") ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 10 | 11)) => Seq("-target:jvm-1.6")
    case Some((3, 0))       => Seq("-Xtarget", "8", "-source:future-migration")
    case _                  => Seq("-target:jvm-1.8")
  }),
  mimaPreviousArtifacts := Set(organization.value %% name.value % "0.10"),
  crossScalaVersions := scalaVersions,
  autoAPIMappings := true,
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := sonatypePublishToBundle.value,
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/mkroli/dns4s")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/mkroli/dns4s"),
      "scm:git:https://github.com/mkroli/dns4s.git"
    )
  ),
  developers := List(
    Developer(
      id = "mkroli",
      name = "Michael Krolikowski",
      email = "mkroli@yahoo.de",
      url = url("https://github.com/mkroli")
    )
  )
)

def projectOsgiSettings(bundleName: String, packagesPrefix: String, packages: String*) =
  osgiSettings ++ Seq(
    OsgiKeys.exportPackage := packages.map(pkg => packagesPrefix :: (if (pkg.isEmpty) Nil else pkg :: "*" :: Nil) mkString "."),
    OsgiKeys.privatePackage := Nil,
    OsgiKeys.additionalHeaders += "Bundle-Name" -> bundleName
  )

lazy val dns4sProjectSettings = Seq(
  libraryDependencies ++= guavaDependencies ++ scalaTestDependencies.value
)

lazy val dns4sAkkaProjectSettings = Seq(
  libraryDependencies ++= guavaDependencies ++ akkaDependencies ++ scalaTestDependencies.value
)

lazy val dns4sNettyProjectSettings = Seq(
  libraryDependencies ++= nettyDependencies ++ scalaTestDependencies.value
)

lazy val projectReleaseSettings = Seq(
  releaseCrossBuild := true,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("+ publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    releaseStepTask(ghpagesPushSite),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val parentSettings = Seq(publishArtifact := false)

lazy val siteSettings = ParadoxMaterialThemePlugin.paradoxMaterialThemeSettings(Paradox) ++ Seq(
  Compile / doc / scalacOptions ++= Seq("-skip-packages", "akka.pattern", "-doc-title", name.value, "-doc-version", version.value),
  git.remoteRepo := "https://github.com/mkroli/dns4s.git",
  ScalaUnidoc / siteSubdirName := "api",
  addMappingsToSiteDir(ScalaUnidoc / packageDoc / mappings, ScalaUnidoc / siteSubdirName),
  mdocAutoDependency := (CrossVersion.partialVersion(scalaVersion.value) == Some((2, 13))),
  mdocIn := sourceDirectory.value / "main" / "doc",
  Paradox / sourceDirectory := mdocOut.value,
  Paradox / paradoxTheme / sourceDirectory := sourceDirectory.value / "main" / "paradox" / "template",
  makeSite := makeSite.dependsOn(mdoc.toTask("")).value,
  Paradox / paradoxNavigationDepth.withRank(KeyRanks.Invisible) := 5,
  Paradox / paradoxProperties ~= (_ - "github.base_url"),
  Paradox / paradoxProperties += ("version" -> version.value),
  Paradox / paradoxMaterialTheme := {
    ParadoxMaterialTheme()
      .withCopyright("© Michael Krolikowski")
      .withRepository(uri("https://github.com/mkroli/dns4s"))
      .withSocial(uri("https://github.com/mkroli"))
  },
  ghpagesPushSite := ghpagesPushSite.dependsOn(makeSite).value
)

lazy val dns4sRoot = Project(id = "dns4s", base = file("."))
  .settings(
    Defaults.coreDefaultSettings ++
      projectSettings("dns4s", "Scala DNS implementation") ++
      projectReleaseSettings ++
      parentSettings ++
      siteSettings
  )
  .enablePlugins(GhpagesPlugin, ScalaUnidocPlugin, MdocPlugin, ParadoxSitePlugin, ParadoxMaterialThemePlugin)
  .aggregate(dns4sCore, dns4sAkka, dns4sNetty)
  .dependsOn(dns4sCore, dns4sAkka, dns4sNetty)

lazy val dns4sCore = Project(id = "dns4s-core", base = file("core"))
  .settings(
    Defaults.coreDefaultSettings ++
      projectOsgiSettings("dns4s-core", "com.github.mkroli.dns4s", "", "dsl", "section") ++
      projectSettings("dns4s-core", "Scala DNS implementation") ++
      dns4sProjectSettings
  )
  .enablePlugins(SbtOsgi)

lazy val dns4sAkka = Project(id = "dns4s-akka", base = file("akka"))
  .settings(
    Defaults.coreDefaultSettings ++
      projectOsgiSettings("dns4s-akka", "com.github.mkroli.dns4s", "akka") ++
      projectSettings("dns4s-akka", "Scala DNS implementation - Akka extension") ++
      dns4sAkkaProjectSettings
  )
  .enablePlugins(SbtOsgi)
  .dependsOn(dns4sCore)

lazy val dns4sNetty = Project(id = "dns4s-netty", base = file("netty"))
  .settings(
    Defaults.coreDefaultSettings ++
      projectOsgiSettings("dns4s-netty", "com.github.mkroli.dns4s", "netty") ++
      projectSettings("dns4s-netty", "Scala DNS implementation - Netty extension") ++
      dns4sNettyProjectSettings
  )
  .enablePlugins(SbtOsgi)
  .dependsOn(dns4sCore)
