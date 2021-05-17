organization := "org.bruchez.olivier"
version := "1.0"
scalaVersion := "2.13.5"
name := "health-analysis"

libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.31.5",
  "com.google.apis" % "google-api-services-sheets" % "v4-rev614-1.18.0-rc",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.31.5",
  "com.typesafe" % "config" % "1.4.1",
  "org.apache.poi" % "poi" % "5.0.0"
)

// If you break an SBT task ([Ctrl]-[C]), don't quit SBT, just quit the task
cancelable in Global := true
