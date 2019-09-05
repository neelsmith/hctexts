
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",

  "edu.holycross.shot.cite" %% "xcite" % "4.1.0",
  "edu.holycross.shot" %% "ohco2" % "10.14.0",

  "edu.holycross.shot" %% "midvalidator" % "9.1.0",

  "edu.holycross.shot" %% "greek" % "2.4.0",
  "edu.holycross.shot" %% "latphone" % "2.5.2",
  "edu.holycross.shot" %% "latincorpus" % "1.6.0",
  "edu.holycross.shot" %% "tabulae" % "5.5.0",

  "edu.holycross.shot" %% "histoutils" % "2.1.0",

  "com.github.pathikrit" %% "better-files" % "3.5.0"
)
tutTargetDirectory := file("docs")
tutSourceDirectory := file("tut")
enablePlugins(TutPlugin)
