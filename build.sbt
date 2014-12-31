import AssemblyKeys._

organization := "com.debugger87.textrank"

name := "textrank"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.graphstream" % "gs-core" % "1.1.2",
  "info.sunng.segs" % "ansjseg" % "1.0.0-SNAPSHOT",
  "edu.stanford.nlp" % "stanford-corenlp" % "1.3.3",
  "com.typesafe.akka" % "akka-slf4j_2.10" % "2.3.4",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "junit" % "junit" % "4.12"
)

resolvers ++= Seq(
  "maven.mei.fm" at "http://maven.mei.fm/nexus/content/groups/public/"
)

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
    case m if m.toLowerCase.matches("meta-inf.*\\.mf$") => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.rsa$") => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.dsa$") => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }
}

net.virtualvoid.sbt.graph.Plugin.graphSettings