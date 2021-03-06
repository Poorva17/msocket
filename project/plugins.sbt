addSbtPlugin("com.timushev.sbt"   % "sbt-updates"              % "0.5.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.2.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.31")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"      % "0.15.0-0.6")
addSbtPlugin("io.spray"           % "sbt-revolver"             % "0.9.1")
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

libraryDependencies += "com.sun.activation" % "javax.activation" % "1.2.0"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  //"-Xfatal-warnings",
  "-Xlint:-unused,_",
  "-Ywarn-dead-code",
  "-Xfuture"
)
