
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

// load an OHCO2 corpus for label
def o2corpus(corpusLabel : String) : Corpus = {
  val cex = s"cex/${corpusLabel}.cex"
  CorpusSource.fromFile(cex)
}

/** Get string output of executing a system process.
*
* @param cmd String of command to execute.
*/
def execOutput(cmd: String) : String = {
  cmd !!
}

// pretty print user messages for reading in terminal
def msg(txt: String): Unit  = {
  println("\n\n")
  println(txt)
  println("\n")
}

// Tokenize a corpus
def corpusTokens(label: String) : Vector[MidToken] = {
  orthoMap(label) match {
    case "lat23" => Latin23Alphabet.tokenizeCorpus(o2corpus(label))
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
def corpusLex(label: String) : Vector[MidToken] = {
  println("\nTokenizing texts..")
  val allTokens = corpusTokens(label)
  println("Done.\n")
  allTokens.filter(_.tokenCategory.toString == "Some(LexicalToken)")
}


// Find distinct forms for a corpus.
// For Latin, the resulting list is an alphabetized
// list of unique forms in all lower case.
def corpusForms(label: String) : Vector[String] = {
  val lex = corpusLex(label)
  lang(label) match {
    case "lat" =>   lex.map(_.string.toLowerCase).distinct.sorted
    case _ =>  lex.map(_.string.toLowerCase).distinct.sorted
    // Not working with greek yet..
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

case class Frequency(string: String, count: Int) {
  def cex = {string + "#" + count}
}
// Find histogram of forms
def corpusFormsHisto(label: String) : Vector[Frequency]= {
  val lex = corpusLex(label).map(_.string.toLowerCase)
  val counts = lex.groupBy(w => w).map{ case (k,v) => (k, v.size)  }
  val sorted = counts.toVector.sortBy(_._2).reverse
  sorted.map( freq => Frequency(freq._1, freq._2))
}



// Write a histogram to a file
def printHisto(freqs: Vector[Frequency], fName: String): Unit = {
  new PrintWriter(fName) { write(freqs.map(_.cex).mkString("\n")); close;}
}

// Write the form histogram for a given corpus to a file
def printFormHisto(label: String) : Unit = {
  val histo = corpusFormsHisto(label)
  printHisto(histo, s"${label}-forms-histogram.cex")
}

// Compile list of forms for a corpus and write to a text file.
def printWordListAlpha(label: String) : Unit = {
  val forms = corpusForms(label)
  val wordsFile = s"${label}-forms.txt"
  new PrintWriter(wordsFile){ write(forms.mkString("\n") + "\n"); close; }
}

// Compile list of forms for a corpus and write to a text file
// sorted by frequency
def printWordListByFreq(label : String) : Unit = {
  val histo = corpusFormsHisto(label)
  new PrintWriter(label + "-words-by-freq.txt") { write(histo.map(_.string).mkString("\n"));close;}
}


def summarizeFst(fst: String, total: Int) : Unit = {
  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  println("Failed on " + failures.size + " forms out of " + total + " total.")
}

// no.lines in a file
def lineCount(f: String): Int = {
  File(f).lines.size
}


def parseWordList(words: Vector[String], ortho: String) : String = {
  val fstParser = s"parsers/shared-${ortho}/latin.a"

  val cmd = s"echo ${words} ${fstinfl} ${fstParser}"
  val fst = execOutput(cmd)
  summarizeFst(fst, words.size)
  fst
}
// Get FST output of parsing list of words in a file.
def parseWordsFile(wordsFile: String, ortho: String ) : String = {
  val fstParser = s"parsers/shared-${ortho}/latin.a"
  //val fstinfl = "/usr/local/bin/fst-infl"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  summarizeFst(fst, lineCount(wordsFile))
  fst
}


def failedList(fstOutput: String) : Vector[String] = {
  fstOutput.split("\n").toVector.filter(_.contains("no result")).map(_.replaceFirst("no result for ",""))
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


def parseCorpus(label: String) = {
  //val forms  = corpusFormsHisto(label).map(_.string)
  printWordListByFreq(label)
  val f = label + "-words-by-freq.txt"
  parseWordsFile(f, orthoMap(label))
}



// Recompile parsre for given ortho, and parse a words file.
def reparse(wordsFile: String, ortho: String) = {
  try {
    msg("Recompiling parser for " + ortho)
    compile(Vector("shared", ortho), File("morphology-latin"))
    val parser = repo / s"parsers/shared-${ortho}/latin.a"
    if (parser.exists) {
      msg("Applying new parser to " + wordsFile)
      parseWordsFile(wordsFile, ortho)
    } else {
      msg("Failed to compile parser " + parser)
    }


  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }

}

def reparseCorpus(label: String) = {
  printWordListByFreq(label)
  val f = label + "-words-by-freq.txt"
  reparse(f,orthoMap(label))
}

val summary = """
load a corpus
get word list for a corpus
get histogram for a corpus
get parse results for a corpus

compile a parser
"""
def info :  Unit = {
  msg("Things you can do:")
  println(summary)
}
