
import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

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
  "livy" -> "lat24",
  "germanicus" ->  "lat24",
  "metamorphoses" ->  "lat24",

  "eutropius" ->  "lat24",
  "nepos" ->  "lat24"

  /*
  "antoninus" -> "litgreek",
  "oeconomicus" -> "litgreek",
  "iliad-allen" -> "litgreek"
  */
)

val orthoClassMap = Map(
  "lat23" -> Latin23Alphabet,
  "lat24" -> Latin24Alphabet
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

def ohco2Corpus(label: String) = {
  CorpusSource.fromFile(s"cex/${label}.cex", cexHeader = true)
}

def tokenizableCorpus(label: String) = {
  val orthoString = orthoMap(label)
  TokenizableCorpus(ohco2Corpus(label), orthoClassMap(orthoString))
}

def wordList(label: String) = {
  tokenizableCorpus(label).wordList
}
def printWordList(label: String) = {
  new PrintWriter(s"${label}-words.txt"){write(wordList(label).mkString("\n")); close;}
}


def parseWordsFile(wordsFile: String, ortho: String ) : String = {
  val fstParser = s"parsers/shared-${ortho}/latin.a"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  fst
}

def parse(label: String) = {
  val wordList = s"${label}-words.txt"
  println("Please be patient: this is slow.")
  println("When the entire process is over, you'll see a")
  println("message reading \"Parsing complete.\"\n\n")
  val parses = parseWordsFile(wordList, orthoMap(label) )
  println("\nParsing complete.\n")
  parses
}

/* Wait for latincorpus library to be published!
def latinCorpus(label: String) = {
  val fstOutput = File(s"${label}-parsed.txt")
  if (! fstOutput.exists) {
    println("Please compile parsed output before running this.")
    //println(s"E.g., run\n\tparse(\"" + label + "\")")
  } else {
    val ortho = orthoMap(label)
    val lc = LatinCorpus(
      ohco2Corpus(label),
      orthoClassMap(ortho),
      fstOutput.lines.mkString("\n")
    )
  }
}
*/

def printParses(label: String) = {
  val fstOutput = parse(label)
  new PrintWriter(s"${label}-parsed.txt"){write(fstOutput); close;}
}

def percentWordList(label: String, pct: Int = 50) = {
  val tc = tokenizableCorpus(label)
  println("Getting histogram for " + label)
  val hist : Histogram[String] = tc.lexHistogram
  println("Finding top " + pct + " pct.")
  hist.takePercent(pct).map(_.item)
}

def percentParse(label: String, pct: Int = 50) = {
  val words = percentWordList(label, pct)
  val fName = s"${label}-${pct}pct.txt"
  new PrintWriter(fName){write(words.mkString("\n"));close;}
  println("Beginning to parse.. be patient.")
  val fst = parseWordsFile(fName, orthoMap(label))
  val parses = FstReader.parseFstLines(fst.split("\n").toVector)
  println("Done parsing.")
  val fails = parses.filter(_.analyses.isEmpty).map(_.token)
  println("Failed on " + fails.size + " tokens.")
  val failedFile = s"${label}-${pct}pct-failed.txt"
  new PrintWriter(failedFile){write(fails.mkString("\n"));close;}
  println("Wrote list to "+ failedFile)
}


def testCorpus = Vector("hyginus", "livy-mt", "nepos")
def overlaps(labels: Vector[String] = testCorpus, pct: Int = 50) = {

}

def dumbOverlap(pct: Int = 50) = {
   val nepwords =  percentWordList("nepos", pct)
   val mtwords = percentWordList("livy-mt", pct)
   val hwords = percentWordList("hyginus", pct)
   println(s"Top ${pct}% of nepos: " + nepwords.size)
   println(s"Top ${pct}% of MT-selections of Livy: " + mtwords.size)
   println(s"Top ${pct}% of Hyginus: " + hwords.size)

   val nPlusL = nepwords.toSet.intersect(mtwords.toSet)
   println("Intersection of Nepos and Livy: " + nPlusL.size)
   val nPlusLplusH = nPlusL.intersect(hwords.toSet)
   println("Intersection of Nepos, Livy and Hyginus: " + nPlusLplusH.size)
   nPlusLplusH.toVector.sorted
}

// Let's go ahead and lazy val some corpora:
lazy val hyg = tokenizableCorpus("hyginus")
lazy val met = tokenizableCorpus("metamorphoses")
lazy val livy = tokenizableCorpus("livy")
lazy val mt = tokenizableCorpus("livy-mt")
lazy val nep = tokenizableCorpus("nepos")


def info = {
  println("\n\nCompile a parser for default datasets (\"shared\" and \"lat23\"):")
  println("\n\tcompile()")

  println("\nCompile a parser for specific datasets:")
  println("\n\tcompile([Vector(CORPUSDATASETS)]")
  println("  e.g.,")
  println("\tcompile(Vector(\"shared\", \"lat24\")\n")

  println("\nLoad a citable corpus by label:")
  println("\n\tval corpus = ohco2Corpus(LABEL)")
  println("  e.g.,")
  println("\tval corpus = ohco2Corpus(\"hyginus\")")

  println("\nLoad a citable and tokenizable corpus by label:")
  println("\n\tval corpus = tokenizableCorpus(LABEL)")
  println("  e.g.,")
  println("\tval corpus = tokenizableCorpus(\"hyginus\")")

  println("\nSee this information again:")
  println("\n\tinfo")
}



println("\n\nSee things you can do with this script:")
println("\n\tinfo")
//info
