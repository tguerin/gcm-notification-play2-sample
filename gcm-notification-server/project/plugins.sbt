// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Snasphots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.4")

addSbtPlugin("com.cloudbees.deploy.play" % "sbt-cloudbees-play-plugin" % "0.5-SNAPSHOT")