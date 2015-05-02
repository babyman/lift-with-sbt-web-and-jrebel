# Lift with sbt-web and jRebel

My attempt to configure lift with sbt-web asset processing and hot class and resource deployment.

# Quick-start

Make sure you have JREBEL_HOME defined as an environment variable, for example I have the following defined in
my ~/.bash_profile:

    export JREBEL_HOME=$HOME/bin/jrebel

To launch lift execute the ./sbt script to load sbt, then the following to start the web server and compile source on
file changes:

    clean
    ; container:start ; ~compile


# Further Improvements / Shaky stuff

Currently you **must** have jRebel installed and working for container:start to succeed, I have some notes in the build.sbt
file regarding this but I have to do some sbt research to make this happen...

Sbt currently always generates a rebel.xml file, even when packaging a war file for deployment, as per the jRebel sbt plugin
this should be avoided (https://github.com/Gekkio/sbt-jrebel-plugin#features).

# Changes

Upgraded sbt-launch-xx.jar to 0.13.8 to support enablePlugins(...)

## project/plugins.sbt

Added the following sbt-web plugins and resolver:

    resolvers += Resolver.typesafeRepo("releases")
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")
    addSbtPlugin("com.github.ddispaltro" % "sbt-reactjs" % "0.5.0")
    addSbtPlugin("com.slidingautonomy.sbt" % "sbt-filter" % "1.0.1")

Add the jRebel sbt plugin:

    addSbtPlugin("fi.gekkio.sbtplugins" % "sbt-jrebel-plugin" % "0.10.0")


Upgraded the xsbt-web-plugin plugin from version 0.7.0:

    addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "1.1.0")


## build.sbt

Pointed unmanagedResourceDirectories to target/web/stage instead of /src/main/webapp:

    unmanagedResourceDirectories in Test <+= target { _ / "web/stage" }

### sbt-web

Enable the sbt-web plugin, define the pipeline and configure the filter to exclude ReactJS source files when copying the
compile results.  Finally hook the sbt-web stage task into the more general sbt compile task.

    enablePlugins(SbtWeb)

    pipelineStages := Seq(filter)

    includeFilter in filter := "*.jsx"

    // include stage task when compiling
    compile <<= (compile in Compile) dependsOn WebKeys.stage


### xsbt-web-plugin

Load Jetty and point its source directory setting to the sbt-web stage directory instead of /src/main/webapp
(see http://stackoverflow.com/questions/29154360/how-can-sbt-web-output-be-used-with-xsbt-web-plugin-via-a-sbt-project-dependency):

    enablePlugins(XwpJetty)

    webappSrc in webapp <<= WebKeys.stage in Assets


### sbt-jrebel-plugin

Setup jRebel as per the instructions [here](https://github.com/earldouglas/xsbt-web-plugin#using-jrebel) except point the weblinks setting to the
sbt-web stage directory and use an environment variable to find the jRebel agent:

    seq(jrebelSettings: _*)

    jrebel.webLinks += (target in Compile).value / "web/stage"

    val JREBEL_HOME = sys.env.get("JREBEL_HOME")

    jrebel.enabled := JREBEL_HOME.isDefined

    javaOptions in container ++= Seq(
      s"-agentpath:${JREBEL_HOME.get}/lib/libjrebel64.dylib",
      "-noverify",
      "-XX:+UseConcMarkSweepGC",
      "-XX:+CMSClassUnloadingEnabled"

## Scala code

Editing the Scala code files should also trigger a compile and hot reload of the classes.

## Asset Processing

Editing the ReactJS file in src/main/static/js will trigger a recompile to target/web/stage/static/js, this change should be
picked up without a container restart.

## Public files

Changes to the template files and other static resources saved in /src/main/public (rather than the usual /src/main/webapp) should
also be picked up and applied, again without a container restart.
