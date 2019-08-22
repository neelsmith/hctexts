
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


// Map of corpus labels to IDs for orthography systems
val orthoMap = Map(

  "hyginus" -> "lat23",
  "livy-mt" -> "lat24",
  "germanicus" -> "lat24",
  "metamorphoses" -> "lat24",

  "eutropius" -> "lat24",
  "nepos" -> "lat24"

  /*
  "antoninus" -> "litgreek",
  "oeconomicus" -> "litgreek",
  "iliad-allen" -> "litgreek"
  */
)

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


/** Get string output of executing a system process.
*
* @param cmd String of command to execute.
*/
def execOutput(cmd: String) : String = {
  cmd !!
}

// Get FST output of parsing list of words in a file.
def parseWordsFile(wordsFile: String, ortho: String ) : String = {
  val fstParser = s"parsers/shared-${ortho}/latin.a"

  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  execOutput(cmd)
}


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
