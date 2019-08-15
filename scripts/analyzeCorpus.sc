// scripts for working with a corpus.
//
// The one bit of magic in these functions is a
// convention for finding files and other data for a given corpus
// keyed to a "corpus label."
//
//  Corpus labels map between two key things:
//
// 1. a file named cex/LABEL.cex. This is the CEX file for the corpus.  Load an OHCO2 corpus from this. Example: o2corpus(LABEL) create an ohco2 Corpus.
// 2. a key in this scripts `orthoMap`.  E.g.,  orthoMap(LABEL) produces
// the id for the   orthography system used in this corpus.



import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._


import edu.holycross.shot.latin._
import edu.holycross.shot.greek._

import edu.holycross.shot.mid.validator._

import scala.io.Source

import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps


import better.files._
import java.io.{File => JFile}
import better.files.Dsl._


// Map of corpus labels to IDs for orthography systems
val orthoMap = Map(

  "eutropius" -> "lat24",
  "germanicus" -> "lat24",
  "hyginus" -> "lat24",
   "nepos" -> "lat24",

  "antoninus" -> "litgreek",
  "oeconomicus" -> "litgreek",
  "iliad-allen" -> "litgreek"
)

def lang(label: String) : String  = {
  orthoMap(label) match {
    case "lat24" => "lat"
    case "litgreek" => "grc"
    case _ => {
      val err = s"Label ${label} not mapped to an ISO lang code."
      println(err)
      throw new Exception(err)
    }
  }
}

//////// CONFIGURE LOCAL SET UP  /////////////////////////////
//
// Explicit paths to SFTS binaries and make.  Adjust SFST paths
// to /usr/bin if using default install on Linux.
val compiler = "/usr/local/bin/fst-compiler-utf8"
val fstinfl = "/usr/local/bin/fst-infl"
val make = "/usr/bin/make"
// Explicit path to directory with tabulae repo:
val tabulaeDir = "/Users/nsmith/Desktop/tabulae"

// Count successes/failures in an SFST string
def summarizeFst(fst: String, total: Int) : Unit = {
  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  println("Failed on " + failures.size + " forms out of " + total + " total.")
}

// no.lines in a file
def lineCount(f: String): Int = {
  Source.fromFile(f).getLines.size
}


// Compile a binary SFST parser using tabulae
def tabulae(ortho : String, tabulaeRepo: String =  tabulaeDir) = {
  //val tabulae = File(tabulaeRepo)
  val datasets = "morphology-latin"

  val conf =  Configuration(compiler,fstinfl,make,datasets)
  val tabulaeParser = tabulaeRepo/s"parsers/${ortho}/latin.a"
  val localParser = File(s"parsers/${ortho}.a")

  try {
    FstCompiler.compile(File(datasets), File(tabulaeRepo), ortho, conf, true)

    cp(tabulaeParser, localParser)
    println(s"\nCompilation completed.  Parser ${ortho}.a is " +
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

// pretty print user messages
def msg(txt: String): Unit  = {
  println("\n\n")
  println(txt)
  println("\n")
}


// load OHCO2 corpus for label
def o2corpus(corpusLabel : String) : Corpus = {
  val cex = s"cex/${corpusLabel}.cex"
  CorpusSource.fromFile(cex)
}

// Tokenize corpus
def corpusTokens(label: String) = {
  orthoMap(label) match {
    case "lat24" => Latin24Alphabet.tokenizeCorpus(o2corpus(label))
    case "lat25" => Latin25Alphabet.tokenizeCorpus(o2corpus(label))
    case "litgreek" => LiteraryGreekString.tokenizeCorpus(o2corpus(label))
    case s: String => {
      val msg = s"Orthographic system ${orthoMap(label)} for label ${label} not recognized or not implemented."
      println(msg)
      throw new Exception(msg)
    }
  }
}

// Find lexical tokens for a corpus.
def corpusLex(label: String) = {
  msg("Tokenizing texts..")
  val allTokens = corpusTokens(label)
  msg("Done.")
  allTokens.filter(_.tokenCategory.toString == "Some(LexicalToken)")
}


// Find distinct forms for a Greek corpus
def corpusForms(label: String) = {
  val lex = corpusLex(label)
  lang(label) match {
    case "lat" =>   lex.map(_.string.toLowerCase).distinct.sorted
    case _ =>  lex.map(_.string.toLowerCase).distinct.sorted
    /*
    case "grc" =>  {
      msg("Beginning to sort Greek text for " + label + "...")
      val strs = lex.map(_.string.toLowerCase).distinct
      msg(s"${strs.size} distinct strings, converting to LGS...")
      val lgs = strs.map(LiteraryGreekString(_))
      msg(s"${lgs.size} distinct Greek strings, flipping grave accents...")
      val flipped = lgs.map(_.flipGrave).distinct
      msg(s"${flipped.size} distinct Greek strings remain, sorting...")
      val aciiSort = flipped.sortWith(_ < _)
      msg("Done sorting, mapping to Unicode...")
      val sortedForms =asciiSort.map(_.ucode)
      msg("Done sorting!")


      sortedForms
    }
    */
  }
}

// Compile list of forms for a corpus and write to a text file.
def printWordList(label: String) = {
  val forms = corpusForms(label)
  val wordsFile = s"${label}-words.txt"
  new PrintWriter(wordsFile){ write(forms.mkString("\n") + "\n"); close; }
}

//val lewisShort = "ls-data.cex"


// FST output of parsing a corpus
def parseCorpus(label: String) : String = {
  val ortho = orthoMap(label)
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
def printParses(label: String)  : Unit = {
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
def parseWordsFile(wordsFile: String, ortho: String ) : String = {
  val fstParser = s"parsers/${ortho}.a"
  val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}

// Recompile parse and parse words file.
def tabulaeAndParse(wordsFile: String, ortho: String) : String = {
  val fstParser = s"parsers/${ortho}.a"
  tabulae(ortho)
  val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst =  execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}

def info: Unit = {
  println("\n\nThings you can do:\n")

  println("""
  tabulae: (ortho: String, tabulaeRepo: String) Unit.  Compile a binary SFST parser for a given orthography.
  o2corpus: (corpusLabel: String)Corpus. Load OHCO2 corpus for label.
  corpusTokens: (label: String)Vector[MidToken]. Tokenize a corpus identified by label.
  corpusLex: (label: String)Vector[MidToken].  Find lexical tokens for a corpus.
  corpusForms: (label: String)Vector[String].  Find distinct forms for a corpus.
  printWordList: (label: String)PrintWriter. Compile list of forms for a corpus and write to a text file.
  parseCorpus: (label: String)String.  Use FST parse to parse a corpus
  printParses: (label: String)Unit
  parseWordsFile: (wordsFile: String, ortho: String).  Parse words in a file.
  tabulaeAndParse: (wordsFile: String, ortho: String).  Recompile parser and parse words file.
""")
/*
orthoMap: scala.collection.immutable.Map[String,String] = Map(nepos -> lat25, germanicus -> lat24)
compiler: String = /usr/local/bin/fst-compiler-utf8
fstinfl: String = /usr/local/bin/fst-infl
make: String = /usr/bin/make
tabulaeDir: String = /Users/nsmith/repos/arch-data/coins/tabulae
summarizeFst: (fst: String, total: Int)Unit
lineCount: (f: String)Int
tabulae: (ortho: String, tabulaeRepo: String)Unit
execOutput: (cmd: String)String
msg: (txt: String)Unit
o2corpus: (corpusLabel: String)edu.holycross.shot.ohco2.Corpus
corpusTokens: (label: String)Vector[edu.holycross.shot.mid.validator.MidToken]
corpusLex: (label: String)scala.collection.immutable.Vector[edu.holycross.shot.mid.validator.MidToken]
corpusForms: (label: String)scala.collection.immutable.Vector[String]
printWordList: (label: String)java.io.PrintWriter
parseCorpus: (label: String)String
printParses: (label: String)Unit
parseWordsFile: (wordsFile: String, ortho: String)String
tabulaeAndParse: (wordsFile: String, ortho: String)String
info: Unit

*/
}
