
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "edu.holycross.shot.cite" %% "xcite" % "3.7.0",
  "edu.holycross.shot" %% "ohco2" % "10.11.1",

  "edu.holycross.shot" %% "latphone" % "2.4.0",

  "edu.holycross.shot" %% "midvalidator" % "5.4.0",

  "edu.holycross.shot" %% "tabulae" % "2.2.0",
  "com.github.pathikrit" %% "better-files" % "3.5.0"
)
