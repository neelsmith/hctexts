// get PoS and lemma for LS!

import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._


import edu.holycross.shot.mid.validator._


// Define selection to work with:
val rng = CtsUrn("urn:cts:omar:stoa0179.stoa001.omar:1.4-1.6")

val corpus = CorpusSource.fromFile("cex/livy-mt.cex", cexHeader = true)
val subc = corpus ~~ rng

import scala.io.Source
val fstLines = Source.fromFile("lat213/livy-all-parses.txt").getLines.toVector

val latc = LatinCorpus.fromFstLines(subc, Latin23Alphabet, fstLines, strict=false)




val ls = LewisShort

val analyzedTokens = latc.tokens.filter(_.analyses.nonEmpty)

def pos(label: String) = {
  label match {
    case "participle" => "verb"
    case "gerundive" => "verb"
    case "supine" => "verb"
    case "gerund" => "verb"
    case "infinitive" => "verb"
    case s: String => s

  }
}




val posPlusId = analyzedTokens.map(t => t.analyses.map(form => (pos(form.posLabel), ls.label(form.lemmaId))))

val vocabPlusPos = posPlusId.flatten.distinct

case class VocabItem(pos: String, lemmaId: String) {
  override def toString = {
    pos + "#" + lemmaId
  }
}

val vocab = vocabPlusPos.map{ case (pos, lemma) => VocabItem(pos,lemma) }





val livy = CorpusSource.fromFile("cex/livy.cex", cexHeader = true)

import java.io.PrintWriter
def printBookFreqs(bk: String) = {
  val livyBook = livy ~~ CtsUrn(s"urn:cts:omar:stoa0179.stoa001.omar:${bk}")
  val livyLatC = LatinCorpus.fromFstLines(livyBook, Latin23Alphabet, fstLines, strict=false)
  val lexH = livyLatC.labelledLexemeHistogram
  val counts = vocab.map(v => (v, lexH.countForItem(v.lemmaId)))
  val vocabFreqs = counts.map{ case (v,i) => Frequency(v,i) }
  val vocabHist = Histogram(counts.map{ case (v,i) => Frequency(v,i) })

  val doubleSorted  = vocabHist.frequencies.sortBy( f => (f.item.pos, f.count)).reverse

  val strOutput = doubleSorted.sortBy( f => (f.item.pos, f.count)).reverse.map(f => f.item + "#" + f.count).mkString("\n")
  new PrintWriter(s"vocabByPos-${bk}.cex"){write(strOutput);close;}
}




def bookEm = {
  for (bkNum <- livy.nodes.map(_.urn.collapsePassageTo(1)).distinct) {
    printBookFreqs(bkNum.passageComponent)
  }
}
