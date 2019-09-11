
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._


import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._

import edu.holycross.shot.hctexts._

import scala.io.Source
import java.io.PrintWriter

val reducedSyntax = CorpusSource.fromFile("cex/livy-reduced.cex", cexHeader=true)
//val fullSyntax = CorpusSource.fromFile("cex/livy-syntax.cex", cexHeader=true)
val fstFile = "src/test/resources/wk1-parsed.txt"
val fstLines = Source.fromFile(fstFile).getLines.toVector


val livy = CtsUrn("urn:cts:omar:stoa0179.stoa001:")

def reducedPassage(psg: String, baseDir : String = "lat213") = {
  val u = livy.addPassage(psg)
  val psgCorpus = reducedSyntax ~~ u
  val latinCorpus = LatinCorpus.fromFstLines(psgCorpus,Latin24Syntax,fstLines,strict=false)
  val phr = LatinPhrase(latinCorpus.tokens)


  Latin24SyntaxString.printPosHighlight(phr,s"${baseDir}/livy-${psg}-pos.md")
  Latin24SyntaxString.printPosHovers(phr,s"${baseDir}/livy-${psg}-hover")
}
