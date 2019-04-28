
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import scala.io.Source
import edu.holycross.shot.mid.validator._
import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps
import edu.holycross.shot.latin._

//////// CONFIGURE LOCAL SET UP  /////////////////////////////

// CEX file for corpus of texts: relative reference since we
// expect this script to be loaded from root directory of repo
val cex = "cex/germanicus.cex"
val parser = "parsers/germanicus.a"

// Explicit paths to SFTS binaries and make.  Adjust SFST paths
// to /usr/bin if using default install on Linux.
val compiler = "/usr/local/bin/fst-compiler-utf8"
val fstinfl = "/usr/local/bin/fst-infl"
val make = "/usr/bin/make"



def msg(txt: String): Unit  = {
  println("\n\n")
  println(txt)
  println("\n")
}

//////// PROCESS NUMISMATIC CORPUS  //////////////////////////
// Intermediate files used in parsing:
val wordsFile = "germanicus-words.txt"
val parseOutput = "germanicus-fst.txt"
//val lewisShort = "ls-data.cex"


val corpus = CorpusSource.fromFile("cex/germanicus.cex")

msg("Tokenizing texts..")
val allTokens = Latin24Alphabet.tokenizeCorpus(corpus)
msg("Done.")

val lexTokens = allTokens.filter(_.tokenCategory == Some(LexicalToken))
val forms = lexTokens.map(_.string.toLowerCase).distinct.sorted
new PrintWriter(wordsFile){ write(forms.mkString("\n") + "\n"); close; }


val cmd = s"${fstinfl} ${parser} ${wordsFile}"
msg("Beginning to parse word list in " + wordsFile)
println("Please be patient: there will be a pause after")
println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
val parses = cmd !!
new PrintWriter(parseOutput) {write(parses); close;}
msg("Done.")

msg("Reading SFST output into object model..")
val parsedObjs = FstFileReader.formsFromFile(parseOutput)
msg("Done.")
