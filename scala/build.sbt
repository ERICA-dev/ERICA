name := "root"

version := "1.0"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  resolvers += "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/Releases",
  scalacOptions  := Seq(
    "-encoding", "utf8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-target:jvm-1.8",
    "-language:implicitConversions",
    "-language:postfixOps"
  )
)

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % Test

lazy val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor"                     % "2.4.7",
  "com.codemettle.reactivemq" %% "reactivemq"             % "0.5.0",
  "org.apache.activemq" % "activemq-client"               % "5.13.3",
  "org.json4s" %% "json4s-native"                         % "3.3.0",
  "org.json4s" %% "json4s-ext"                            % "3.3.0",
  "org.json4s" %% "json4s-jackson"                        % "3.3.0",
  "com.github.nscala-time" %% "nscala-time"               % "2.12.0",
  "wabisabi" %% "wabisabi"                                % "2.1.4",
  "org.scalatest"     %% "scalatest"                      % "2.2.5" % "test"
)


lazy val root = project.in( file(".") )
  .aggregate(bus_apis, config, services)

lazy val bus_apis = (project in file("bus_apis")).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= dependencies)

lazy val config = (project in file("config")).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= dependencies)

lazy val services = (project in file("services")).
  dependsOn(config, bus_apis).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= dependencies)




