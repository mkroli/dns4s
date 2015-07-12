/*
 * Copyright 2014-2015 Michael Krolikowski
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
import sbt._
import sbt.Keys._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import com.typesafe.sbt.osgi.SbtOsgi._

object Build extends sbt.Build {
  lazy val scalaVersions = "2.11.7" :: "2.10.5" :: Nil
  lazy val akkaVersion = "2.3.12"
  lazy val scalaTestVersion = "2.2.5"

  def projectSettings(n: String) = Seq(
    name := n,
    organization := "com.github.mkroli",
    scalaVersion := scalaVersions.head,
    crossScalaVersions := scalaVersions,
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"))

  def projectOsgiSettings(bundleName: String, packagesPrefix: String, packages: String*) = osgiSettings ++ Seq(
    OsgiKeys.exportPackage := packages.map(pkg => packagesPrefix :: (if (pkg.isEmpty) Nil else pkg :: "*" :: Nil) mkString "."),
    OsgiKeys.privatePackage := Nil,
    OsgiKeys.additionalHeaders += "Bundle-Name" -> bundleName)

  lazy val dns4sProjectSettings = Seq(
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "[15.+,18.+]",
      "com.google.code.findbugs" % "jsr305" % "+" % "provided",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.1" % "test"))

  lazy val dns4sAkkaProjectSettings = Seq(
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "[15.+,18.+]",
      "com.google.code.findbugs" % "jsr305" % "+" % "provided",
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"))

  lazy val dns4sNettyProjectSettings = Seq(
    libraryDependencies ++= Seq(
      "io.netty" % "netty-handler" % "4.0.+",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"))

  lazy val projectReleaseSettings = Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion))

  lazy val parentSettings = Seq(
    publishArtifact := false)

  lazy val dns4sRoot = Project(
    id = "dns4s",
    base = file("."),
    settings = Defaults.defaultSettings ++
      releaseSettings ++
      projectSettings("dns4s") ++
      projectReleaseSettings ++
      parentSettings)
    .aggregate(dns4sCore, dns4sAkka, dns4sNetty)

  lazy val dns4sCore = Project(
    id = "dns4s-core",
    base = file("core"),
    settings = Defaults.defaultSettings ++
      projectOsgiSettings("dns4s-core", "com.github.mkroli.dns4s", "", "dsl", "section") ++
      projectSettings("dns4s-core") ++
      dns4sProjectSettings)

  lazy val dns4sAkka = Project(
    id = "dns4s-akka",
    base = file("akka"),
    settings = Defaults.defaultSettings ++
      projectOsgiSettings("dns4s-akka", "com.github.mkroli.dns4s", "akka") ++
      projectSettings("dns4s-akka") ++
      dns4sAkkaProjectSettings)
    .dependsOn(dns4sCore)

  lazy val dns4sNetty = Project(
    id = "dns4s-netty",
    base = file("netty"),
    settings = Defaults.defaultSettings ++
      projectOsgiSettings("dns4s-netty", "com.github.mkroli.dns4s", "netty") ++
      projectSettings("dns4s-netty") ++
      dns4sNettyProjectSettings)
    .dependsOn(dns4sCore)
}
