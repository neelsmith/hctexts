
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




def printParses(wordsFile: String = "germanicus-words.txt")  : Unit = {

  val cmd = s"${fstinfl} ${parser} ${wordsFile}"
  msg("Beginning to parse word list in " + wordsFile)
  println("Please be patient: there will be a pause after")
  println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
  val fst = execOutput(cmd)
  new PrintWriter(parseOutput) {write(fst); close;}
  msg("Done.")


  val fstLines = fst.split("\n").toVector
  val failures = fstLines.filter(_.startsWith("no result for ")).map(_.replaceFirst("no result for ", ""))

  println("Failed on " + failures.size + " forms out of " + forms.size + " total.")
  new PrintWriter("germanicus-failed.txt"){write(failures.mkString("\n")); close;}
}
