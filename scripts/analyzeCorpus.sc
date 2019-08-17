
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


/** Get string output of executing a system process.
*
* @param cmd String of command to execute.
*/
def execOutput(cmd: String) : String = {
  cmd !!
}
