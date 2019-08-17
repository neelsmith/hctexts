
import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._


import edu.holycross.shot.latin._
import edu.holycross.shot.greek._

import edu.holycross.shot.mid.validator._

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps

val macInstall = File("/usr/local/bin/")
val linuxInstall = File("/usr/bin/")

val compiler = if ( (macInstall / "fst-compiler-utf8").exists) {
  macInstall / "fst-compiler-utf8"
} else {
  linuxInstall / "fst-compiler-utf8"
}
val fstinfl = if ( (macInstall / "fst-infl").exists) {
  macInstall / "fst-infl"
} else {
  linuxInstall / "fst-infl"
}
val make = "/usr/bin/make"
val repo = "."



// Map of corpus labels to IDs for orthography systems
val orthoMap = Map(

  "hyginus" -> "lat23",
  "germanicus" -> "lat24",
  "metamorphoses" -> "lat24",

  "eutropius" -> "lat24",
  "nepos" -> "lat24",

  "antoninus" -> "litgreek",
  "oeconomicus" -> "litgreek",
  "iliad-allen" -> "litgreek"
)

// Given a corpus label, figure out the ISO code
// for its language from its orthography system:
def lang(label: String) : String  = {
  orthoMap(label) match {
    case "lat23" => "lat"
    case "lat24" => "lat"
    case "lat25" => "lat"
    case "litgreek" => "grc"
    case _ => {
      val err = s"Label ${label} not mapped to an ISO lang code."
      println(err)
      throw new Exception(err)
    }
  }
}


// Compile a parser with tabulae
def compile (
  corpusList: Vector[String] = Vector("shared", "lat23"),
  datasets: File = repo / "morphology-latin" ) = {

  val conf =  Configuration(compiler.toString, fstinfl.toString, make, datasets.toString)
  val parserDir = repo / "parsers"
  val fst = repo / "fst"
  try {
    FstCompiler.compile(datasets, corpusList, parserDir, fst, conf)

    println("\nCompilation completed.\nParser is available in " +  parserDir + "/" + corpusList.mkString("-") + "/latin.a")
  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }

}
