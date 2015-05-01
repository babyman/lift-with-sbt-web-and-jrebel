resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")

addSbtPlugin("com.github.ddispaltro" % "sbt-reactjs" % "0.5.0")

// web container plugin - see https://github.com/earldouglas/xwp-template
// --------------------
//addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.7.0")
addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "1.1.0")

//Enable the sbt idea plugin
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

//Enable the sbt eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")
