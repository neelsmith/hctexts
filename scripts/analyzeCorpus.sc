
import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._
import edu.holycross.shot.greek._

import edu.holycross.shot.mid.validator._

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps

val workDir = File("workfiles")

// Map of corpus labels to IDs for orthography systems
val orthoMap = Map(
  "hyginus" -> "lat23",
  "livy-mt" -> "lat24",
  "livy" -> "lat24",
  "periochae" -> "lat24",
  "germanicus" ->  "lat24",
  "germanicus-breysig" ->  "lat24",
  "metamorphoses" ->  "lat24",

  "ocre43k" ->  "lat24",

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
  val fstParser = s"parsers/shared-${ortho}-shared-xls/latin.a"

  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  execOutput(cmd)
}


def compile (
  corpusList: Vector[String] = Vector("shared", "lat23", "shared-xls"),
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

  val words =  wordList(label).mkString("\n")
  val outFile = workDir / s"${label}-words.txt"
  new PrintWriter(outFile.toString){write(words); close;}
}
/*
val idxFile = "scripts/ls_indexData.txt"
val lines = File(idxFile).lines.toVector



val lsIdMap = for (ln <- lines) yield {
  val parts = ln.split("#")
  s"ls.${parts(0)}" -> parts(1)
}*/



def parseWordsFile(wordsFile: String, label: String) : String = {
  val fstParser = s"parsers/shared-" + label + "-shared-xls/latin.a"
  val cmd = s"${fstinfl} ${fstParser} ${wordsFile}"
  val fst = execOutput(cmd)
  fst
}

def parse(label: String) = {
  val wordList = workDir / s"${label}-words.txt"
  println("Please be patient: this is slow.")
  println("When the entire process is over, you'll see a")
  println("message reading \"Parsing complete.\"\n\n")
  val parses = parseWordsFile(wordList.toString, orthoMap(label) )
  println("\nParsing complete.\n")
  parses
}


def latinCorpus(label: String) : Option[LatinCorpus] = {
  val fstOutput = workDir / s"${label}-parsed.txt"
  if (! fstOutput.exists) {
    println("Please compile parsed output before running this.")
    None
    //println(s"E.g., run\n\tparse(\"" + label + "\")")
  } else {
    val ortho = orthoMap(label)
    val lc = LatinCorpus.fromFstLines(
      ohco2Corpus(label),
      orthoClassMap(ortho),
      fstOutput.lines.toVector,
      strict = false
    )
    Some(lc)
  }
}


def printParses(label: String) = {
  val fstOutput = parse(label)
  val outFile = workDir /  s"${label}-parsed.txt"
  new PrintWriter(outFile.toString){write(fstOutput); close;}
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
  val fName = workDir / s"${label}-${pct}pct.txt"
  new PrintWriter(fName.toString){write(words.mkString("\n"));close;}
  println("Beginning to parse.. be patient.")
  val fst = parseWordsFile(fName.toString, orthoMap(label))
  val parses = FstReader.parseFstLines(fst.split("\n").toVector)
  println("Done parsing.")
  val fails = parses.filter(_.analyses.isEmpty).map(_.token)
  println("Failed on " + fails.size + " tokens.")
  val failedFile = workDir / s"${label}-${pct}pct-failed.txt"
  new PrintWriter(failedFile.toString){write(fails.mkString("\n"));close;}
  println("Wrote list to "+ failedFile)
}


def testCorpus = Vector("hyginus", "livy-mt", "nepos")
def overlaps(labels: Vector[String] = testCorpus, pct: Int = 50) = {

}

def dumbOverlap(pct: Int = 50) = {
   val nepwords =  percentWordList("nepos", pct)
   val mtwords = percentWordList("livy-mt", pct)
   val hwords = percentWordList("hyginus", pct)
   val gbwords = percentWordList("germanicus-breysig", pct)
   println("\n\n")
   println(s"Top ${pct}% of Nepos: " + nepwords.size + " words.")
   println(s"Top ${pct}% of MT-selections of Livy: " + mtwords.size +  " words.")

   val proseyDiff = nepwords.toSet.intersect(mtwords.toSet)
   val proseyJoin = nepwords.toSet.union(mtwords.toSet)
   println("\nCompare Nepos and Livy:")
   println("\tintersection: " + proseyDiff.size)
   println("\tunion: " + proseyJoin.size)


   println("\n")
   println(s"Top ${pct}% of Hyginus: " + hwords.size + " words.")
   println(s"Top ${pct}% of Germanicus: " + gbwords.size + " words.")

   val poeticDiff = hwords.toSet.intersect(gbwords.toSet)
   val poeticJoin = hwords.toSet.union(gbwords.toSet)

  println("\nCompare Germanicus and Hyginus:")
  println("\tintersection: " + poeticDiff.size)
  println("\tunion: " + poeticJoin.size)

  println("\n\n")
  println("All four texts:")
  println("\tintersection: " + proseyDiff.intersect(poeticDiff).size)
  println("\tunion: " + proseyJoin.union(poeticJoin).size)


  println(s"\n\nHere are words appearing in top ${pct} pct of all 4 texts:")
  println("\n" +  proseyDiff.intersect(poeticDiff).toVector.sorted.mkString("\n"))
}

// Let's go ahead and lazy val some corpora:
lazy val hyg = tokenizableCorpus("hyginus")
lazy val met = tokenizableCorpus("metamorphoses")
lazy val livy = tokenizableCorpus("livy")
lazy val mt = tokenizableCorpus("livy-mt")
lazy val nep = tokenizableCorpus("nepos")


def info = {
  println("\n\nCompile a parser for default datasets (\"shared\", \"lat23\" and \"shared-xls\"):")
  println("\n\tcompile()")

  println("\nCompile a parser for specific datasets:")
  println("\n\tcompile([Vector(CORPUSDATASETS)]")
  println("  e.g.,")
  println("\tcompile(Vector(\"shared\", \"lat24\", \"shared-xls\")\n")

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
