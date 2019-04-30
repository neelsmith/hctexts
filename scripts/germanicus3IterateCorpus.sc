
import edu.holycross.shot.tabulae.builder._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import sys.process._
import scala.language.postfixOps


val compiler = "/usr/local/bin/fst-compiler-utf8"
val fstinfl = "/usr/local/bin/fst-infl"
val make = "/usr/bin/make"



def compile(repo: String =  "/Users/nsmith/repos/arch-data/coins/tabulae") = {
  val tabulae = File(repo)
  val datasets = "morphology"
  val c = "lat24"
  val conf =  Configuration(compiler,fstinfl,make,datasets)

  try {
    FstCompiler.compile(File(datasets), File(repo), c, conf, true)
    val tabulaeParser = repo/s"parsers/${c}/latin.a"
    val localParser = File("parsers/germanicus.a")
    cp(tabulaeParser, localParser)
    println("\nCompilation completed.  Parser germanicus.a is " +
    "available in directory \"parser\"\n\n")
  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }
}

def parseWordsFile(wordsFile: String) : String = {
  val fstinfl = "/usr/local/bin/fst-infl"
  val parser = "parsers/germanicus.a"
  val cmd = s"${fstinfl} ${parser} ${wordsFile}"
  cmd !!
}

def compileAndParse(wordsFile: String) : String = {
  compile()
  val fstinfl = "/usr/local/bin/fst-infl"
  val parser = "parsers/germanicus.a"
  val cmd = s"${fstinfl} ${parser} ${wordsFile}"
  cmd !!
}

def info: Unit = {
  println("\n\nThings you can do:\n")
  println("Rebuild parser:\n")
  println("\tcompile()\n\n")
  println("Parse a word list:\n")
  println("\tparseWordsFile(FILENAME)")

}
