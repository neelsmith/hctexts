
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "edu.holycross.shot.cite" %% "xcite" % "4.0.2",
  "edu.holycross.shot" %% "ohco2" % "10.12.5",

  "edu.holycross.shot" %% "latphone" % "2.4.0",

  "edu.holycross.shot" %% "midvalidator" % "6.1.1",

  "edu.holycross.shot" %% "tabulae" % "2.3.0",
  "com.github.pathikrit" %% "better-files" % "3.5.0"
)
