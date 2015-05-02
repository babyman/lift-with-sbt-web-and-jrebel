# Lift with sbt-web and jRebel

My attempt to configure lift with sbt-web asset processing and hot class and resource deployment.

# Quick-start

Make sure you have JREBEL_HOME defined as an environment variable, for example I have the following defined in
my ~/.bash_profile:

    export JREBEL_HOME=$HOME/bin/jrebel

To launch lift execute the ./sbt script and then the following:

    ./sbt

    clean
    ; container:start ; ~compile

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

### xsbt-web-plugin

### sbt-jrebel-plugin

## Scala code

Editing the Scala code files should also trigger a compile and hot reload of the classes.

## Asset Processing

Editing the ReactJS file in src/main/static/js will trigger a recompile to target/web/stage/static/js, this change should be
picked up without a container restart.

## Public files

Changes to the template files and other static resources saved in /src/main/public (rather than the usual /src/main/webapp) should
also be picked up and applied, again without a container restart.

# Improvements / Shaky stuff

Currently you **must** have jRebel installed and working for container:start to succeed, I have some notes in the build.sbt
file regarding this but I have to do some sbt research to make this happen...

Sbt currently always generates a rebel.xml file, even when packaging a war file for deployment, as per the jRebel sbt plugin
this should be avoided.
