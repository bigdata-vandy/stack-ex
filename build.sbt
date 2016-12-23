lazy val commonSettings = Seq(
  organization := "edu.vanderbilt.accre",
  version := "0.1.0",
  scalaVersion := "2.10.5",
  test in assembly := {},
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "latest.integration" % "test"
  ),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)


lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    name := "util",
    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "2.6.3"
      )
  )

lazy val core = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("edu.vanderbilt.accre.stackex.StackExApp"),
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "1.6.2" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.6.2" % "provided",
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.18",
      "com.databricks" %% "spark-xml" % "0.4.1"
    )
  ).
  dependsOn(util)
