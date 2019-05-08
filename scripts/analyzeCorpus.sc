// scripts to analyze a corpus, and print FST output and
// list of tokens with failed analyzes to files.

import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import scala.io.Source
import edu.holycross.shot.mid.validator._
import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps
import edu.holycross.shot.latin._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

//////// CONFIGURE LOCAL SET UP  /////////////////////////////

// CEX file for corpus of texts: relative reference since we
// expect this script to be loaded from root directory of repo
val corpusLabel = "germanicus"

//val parser = "parsers/lat24.a"

val orth = "lat25"

// Explicit paths to SFTS binaries and make.  Adjust SFST paths
// to /usr/bin if using default install on Linux.
val compiler = "/usr/local/bin/fst-compiler-utf8"
val fstinfl = "/usr/local/bin/fst-infl"
val make = "/usr/bin/make"


def summarizeFst(fst: String, total: Int) : Unit = {
  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  println("Failed on " + failures.size + " forms out of " + total + " total.")
}

// no.lines in a file
def lineCount(f: String): Int = {
  Source.fromFile(f).getLines.size

}


def compile(repo: String =  "/Users/nsmith/repos/arch-data/coins/tabulae", orth : String  = "lat25") = {
  val tabulae = File(repo)
  val datasets = "morphology"

  val conf =  Configuration(compiler,fstinfl,make,datasets)

  try {
    FstCompiler.compile(File(datasets), File(repo), orth, conf, true)
    val tabulaeParser = repo/s"parsers/${orth}/latin.a"


    val localParser = File(s"parsers/${orth}.a")
    cp(tabulaeParser, localParser)
    println(s"\nCompilation completed.  Parser ${orth}.a is " +
    "available in directory \"parser\"\n\n")
  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }
}

/** Get string output of executing system process.
*
* @param cmd String of command to execute.
*/
def execOutput(cmd: String) : String = {
  cmd !!
}

def msg(txt: String): Unit  = {
  println("\n\n")
  println(txt)
  println("\n")
}


def o2corpus(corpusLabel : String) : Corpus = {
  val cex = s"cex/${corpusLabel}.cex"
  CorpusSource.fromFile(cex)
}


// Find lexical tokens for a corpus.
def corpusLex(label: String = corpusLabel) = {
  msg("Tokenizing texts..")
  val allTokens = Latin24Alphabet.tokenizeCorpus(o2corpus(label))
  msg("Done.")
  allTokens.filter(_.tokenCategory == Some(LexicalToken))
}


// Find distinct forms for a corpus
def corpusForms(label: String = corpusLabel) = {
  val lex = corpusLex(label)
  lex.map(_.string.toLowerCase).distinct.sorted
}

def printWordList(label: String = corpusLabel) = {
  val forms = corpusForms(label)
  val wordsFile = s"${label}-words.txt"
  new PrintWriter(wordsFile){ write(forms.mkString("\n") + "\n"); close; }
}

//val lewisShort = "ls-data.cex"


// FST output of parsing a corpus
def parseCorpus(label: String = corpusLabel, ortho: String =  orth) : String = {
  val cmd = s"${fstinfl} parsers/${ortho}.a ${label}-words.txt"
  msg("Beginning to parse word list in " + label + "-words.txt")
  println("Please be patient: there will be a pause after")
  println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
  val fst = execOutput(cmd)
  fst
}

// write output to two files:
// 1.  Complete FST output
// 2.  List of failed tokens
def printParses(label: String = corpusLabel)  : Unit = {
  val fst = parseCorpus(label)
  new PrintWriter(s"${label}-fst.txt") {write(fst); close;}
  msg("Done.")

  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  val forms = corpusForms(label)
  println("Failed on " + failures.size + " forms out of " + forms.size + " total.")
  new PrintWriter(s"${label}-failed.txt"){write(failures.mkString("\n")); close;}
}


// Get FST output of parsing list of words in a file.
def parseWordsFile(wordsFile: String, fstParser: String ) : String = {
  val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}

def compileAndParse(wordsFile: String, fstParser: String) : String = {
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
