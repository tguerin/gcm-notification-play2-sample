import sbt._
import Keys._
import play.Project._
import cloudbees.Plugin._

object ApplicationBuild extends Build {

  val appVersion = "1.0-SNAPSHOT"
  val appName = "gcm-notification-server"


  val appDependencies = Seq(
    "com.google.android.gcm" % "gcm-server" % "1.0.2", jdbc, anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(cloudBeesSettings: _*)
    .settings(CloudBees.applicationId := Some("gcm-server-sample"))
    .settings(resolvers += "GCM Server Repository" at "https://raw.github" + ".com/slorber/gcm-server-repository/master/releases/")

}
