/*
 * Copyright 2014 Michael Krolikowski
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

object Build extends sbt.Build {
  def projectSettings(n: String) = Seq(
    name := n,
    organization := "com.github.mkroli",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"))

  lazy val dns4sProjectDependencies = Seq(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.+" % "test"))

  lazy val dns4sAkkaProjectDependencies = Seq(
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "15.+",
      "com.google.code.findbugs" % "jsr305" % "+" % "provided",
      "com.typesafe.akka" %% "akka-actor" % "2.2.+"))

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
    publishArtifact := false
  )

  lazy val dns4sRoot = Project(
    id = "dns4s",
    base = file("."),
    settings = Defaults.defaultSettings ++
      releaseSettings ++
      projectSettings("dns4s") ++
      projectReleaseSettings ++
      parentSettings)
    .aggregate(dns4sCore, dns4sAkka)

  lazy val dns4sCore = Project(
    id = "dns4s-core",
    base = file("core"),
    settings = Defaults.defaultSettings ++
      projectSettings("dns4s-core") ++
      dns4sProjectDependencies)

  lazy val dns4sAkka = Project(
    id = "dns4s-akka",
    base = file("akka"),
    settings = Defaults.defaultSettings ++
      projectSettings("dns4s-akka") ++
      dns4sAkkaProjectDependencies)
    .dependsOn(dns4sCore)
}
