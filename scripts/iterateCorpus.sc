// useful scripts for iteratively developing a parser
// for a given corpus

import edu.holycross.shot.tabulae.builder._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import sys.process._
import scala.language.postfixOps
import scala.io.Source

val compiler = "/usr/local/bin/fst-compiler-utf8"
val fstinfl = "/usr/local/bin/fst-infl"
val make = "/usr/bin/make"

val parser = "parsers/lat24.a"

/** Get string output of executing system process.
*
* @param cmd String of command to execute.
*/
def execOutput(cmd: String) : String = {
  cmd !!
}


def compile(repo: String =  "/Users/nsmith/repos/arch-data/coins/tabulae") = {
  val tabulae = File(repo)
  val datasets = "morphology"
  val c = "lat24"
  val conf =  Configuration(compiler,fstinfl,make,datasets)

  try {
    FstCompiler.compile(File(datasets), File(repo), c, conf, true)
    val tabulaeParser = repo/s"parsers/${c}/latin.a"
    val localParser = File(parser)
    cp(tabulaeParser, localParser)
    println("\nCompilation completed.  Parser lat24.a is " +
    "available in directory \"parser\"\n\n")
  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }
}



def summarizeFst(fst: String, total: Int) : Unit = {
  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  println("Failed on " + failures.size + " forms out of " + total + " total.")
}

// no.lines in a file
def lineCount(f: String): Int = {
  Source.fromFile(f).getLines.size

}
// Get FST output of parsing list of words in a file.
def parseWordsFile(wordsFile: String, fstParser: String = parser) : String = {
  val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}

def compileAndParse(wordsFile: String, fstParser: String = parser) : String = {
  compile()
  val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst =  execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}

def info: Unit = {
  println("\n\nThings you can do:\n")
  println("Rebuild parser:\n")
  println("\tcompile()\n\n")
  println("Parse a word list:\n")
  println("\tparseWordsFile(FILENAME)")

}
