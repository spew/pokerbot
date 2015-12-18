//import com.typesafe.sbt.SbtNativePackager._
//import NativePackagerKeys._
//import sbt.Keys._

//import sbtdocker.DockerKeys._

name := "pokerbot"

version := "1.0"
scalaVersion := "2.10.5"
sbtVersion := "0.13.8"

//packageArchetype.java_application

// https://github.com/sbt/sbt/issues/1010
//incOptions := incOptions.value.withNameHashing(true)

resolvers += "project-local" at "file:///" + (baseDirectory.value / "repo").getAbsolutePath.toString

libraryDependencies ++= Seq(
  //"ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.hibernate" % "hibernate-core" % "4.3.6.Final",
  "org.pircbotx" % "pircbotx" % "2.0.1",
  "org.jsoup" % "jsoup" % "1.8.1",
  "com.google.apis" % "google-api-services-customsearch" % "v1-rev33-1.17.0-rc" exclude(org = "commons-logging", name = "commons-logging"),
  "com.google.apis" % "google-api-services-youtube" % "v3-rev107-1.18.0-rc" exclude(org = "commons-logging", name = "commons-logging"),
  "com.google.http-client" % "google-http-client-jackson" % "1.15.0-rc",
  "org.twitter4j" % "twitter4j-core" % "3.0.5",
  "com.sachinhandiekar" % "jInstagram" % "1.1.3" exclude(org = "org.slf4j", name = "jcl-over-slf4j"),
  "org.ocpsoft.prettytime" % "prettytime" % "3.2.4.Final",
  "it" % "tomatoclient" % "0.0.1-SNAPSHOT"
)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.2.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
libraryDependencies += "org.joda" % "joda-money" % "0.9.1"
libraryDependencies += "com.xeiam.xchange" % "xchange-bitcoinaverage" % "2.1.0" exclude(org = "org.java-websocket", name = "Java-WebSocket")
libraryDependencies += "com.xeiam.xchange" % "xchange-bitstamp" % "2.1.0" exclude(org = "org.java-websocket", name = "Java-WebSocket")
libraryDependencies += "com.xeiam.xchange" % "xchange-coinbase" % "2.1.0" exclude(org = "org.java-websocket", name = "Java-WebSocket")
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"
libraryDependencies += "com.stackmob" %% "newman" % "1.3.5"
libraryDependencies += "org.scribe" % "scribe" % "1.3.5"
libraryDependencies += "org.json4s" %% "json4s-ext" % "3.2.11"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "com.jsuereth" %% "scala-arm" % "1.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

//excludeAll(ExclusionRule(organization = "org.slf4j", name = "jcl-over-slf4j"))

enablePlugins(DockerPlugin)
//docker <<= docker.dependsOn(Keys.`package`.in(Compile, packageBin))
docker <<= (docker dependsOn assembly)
dockerfile in docker := {
  val artifact = (assemblyOutputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"
  // any vals to be declared here
  new sbtdocker.mutable.Dockerfile {
    from("java:8")
    add(artifact, artifactTargetPath)
    //entryPoint("java", "-jar", artifactTargetPath)
  }
}