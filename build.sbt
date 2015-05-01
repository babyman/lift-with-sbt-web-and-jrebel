name := "Lift 2.6 starter template"

version := "0.0.4"

organization := "net.liftweb"

scalaVersion := "2.11.2"

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                "releases"        at "https://oss.sonatype.org/content/repositories/releases"
                )

unmanagedResourceDirectories in Test <+= target { _ / "web/stage" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6.2"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.8",
    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
    "org.eclipse.jetty" % "jetty-plus"          % "8.1.7.v20120910"  % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"    % "logback-classic"     % "1.0.6",
    "org.specs2"        %% "specs2"             % "2.3.12"           % "test"
  )
}




// sbt-web setup
// ------------------------------------------------------------------

enablePlugins(SbtWeb)

pipelineStages := Seq()

// include stage task when compiling
compile <<= (compile in Compile) dependsOn WebKeys.stage


// web application container support
// ------------------------------------------------------------------

enablePlugins(XwpJetty)

//jettyPort in container := 8085

// set the webapp source to match sbt-webs output
webappSrc in webapp <<= WebKeys.stage in Assets

webInfClasses in webapp := true


// jRebel class reload support
// ------------------------------------------------------------------

seq(jrebelSettings: _*)

jrebel.webLinks += (target in Compile).value / "web/stage"

val JREBEL_HOME = sys.env("JREBEL_HOME")

javaOptions in container ++= Seq(
  s"-agentpath:$JREBEL_HOME/lib/libjrebel64.dylib",
  "-noverify",
  "-XX:+UseConcMarkSweepGC",
  "-XX:+CMSClassUnloadingEnabled"
)
