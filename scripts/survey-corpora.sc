import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.latin._
import edu.holycross.shot.mid.validator._
import edu.holycross.shot.latincorpus._
import scala.io.Source


// 1. Load citable corpora from a URL or File
val livyFile = "cex/livy.cex"
val livy = CorpusSource.fromFile(livyFile, cexHeader = true)
val tlivy = TokenizableCorpus(livy, Latin24Alphabet )
val livyFstFile = "li-work/livy-parsed.txt"
val livyFstOutput = Source.fromFile(livyFstFile).getLines.toVector
val livyMorphed = LatinCorpus.fromFstLines(
  livy,
  Latin24Alphabet,
  livyFstOutput,
  strict = false
)

val livyHisto = livyMorphed.labelledLexemeHistogram


val mtFile = "cex/livy-mt.cex"
val mt = CorpusSource.fromFile(mtFile, cexHeader = true)
val tmt = TokenizableCorpus(mt, Latin24Alphabet )
val mtMorphed = LatinCorpus.fromFstLines(
  mt,
  Latin24Alphabet,
  livyFstOutput,
  strict = false
)
val mtHisto = mtMorphed.labelledLexemeHistogram


// Compare Hyginus
val hygFile = "cex/hyginus.cex"
val hyg = CorpusSource.fromFile(hygFile, cexHeader = true)
val thyg = TokenizableCorpus(hyg, Latin23Alphabet )
val hyginusFstFile = "hy-work/hyginus-tokens-parses.txt"
val hyginusFstOutput = Source.fromFile(hyginusFstFile).getLines.toVector
val hygMorphed = LatinCorpus.fromFstLines(
  hyg,
  Latin23Alphabet,
  hyginusFstOutput,
  strict = false
)
val hygHisto = hygMorphed.labelledLexemeHistogram





def coverage(lc: LatinCorpus, pct: Int) : String = {
  val pctTotal = edu.holycross.shot.histoutils.Histogram(lc.labelledLexemeHistogram.takePercent(pct)).total

  val tokensPct =  ((pctTotal   / lc.lexicalTokens.size.toDouble) * 100).toInt

  lc.labelledLexemeHistogram.takePercent(pct).size + s" cover ${pct}% of analyses (max. " + pctTotal   + " tokens / "  + lc.lexicalTokens.size  + " = " + tokensPct + "% of lexical tokens)"
}

def survey(lc: LatinCorpus, corpus: Corpus, label: String) = {
  println("SURVEYING COPRUS " + label)

  // Maybe worth abstracting these as methods on LatinCorpus?
  val unanalyzedLex = lc.noAnalysis.filter(t => t.category == edu.holycross.shot.latin.LexicalToken)
  val maxOccurrences = lc.labelledLexemeHistogram.frequencies.map(fr => fr.count).sum

  println("Citable passages of text: " + corpus.size)
  println("Total tokens (all categories): " + lc.size) // == lc.tokens.size

  println("Lexical tokens: " + lc.lexicalTokens.size)
  println("Lexical tokens analyzed: " + lc.analyzed.size)
  println("\t(Token-level coverage of morphological analysis:\n\t" + lc.analyzed.size + " analyzed / " + lc.lexicalTokens.size + " lexical tokens)")

  println("Total analyses: " + lc.allAnalyses.size)
  println("\t(Morphological ambiguity of analyzed tokens: \n\t" + lc.allAnalyses.size  + " analyses / " +  + lc.analyzed.size + " analyzed tokens)" )
  println("Lexical tokens with no analysis: " + unanalyzedLex.size)
  println("Unanalyzed tokens (all categories): " + lc.noAnalysis.size)
  println("All tokens accounted for in analyzed/unanalzyed reports? " + (lc.size == (lc.analyzed.size + lc.noAnalysis.size) ))

  println("Lexemes recognized: " + lc.labelledLexemeHistogram.size)
  println("\t(Morphological density:\n\t" + lc.analyzed.size + " analyzed tokens / " + lc.labelledLexemeHistogram.size + " lexemes)")

  println("Possible tokens for recognized lexemes: " + maxOccurrences)
  println("\t(Lexical ambiguity of lexeme indexing:\n\t" + maxOccurrences + " possible token occurrences / " + lc.analyzed.size + " tokens)")

  edu.holycross.shot.histoutils.Histogram(lc.labelledLexemeHistogram.takePercent(95)).total

  val pcts = Vector(100, 95, 90, 85)

  println("\nOut of " + lc.labelledLexemeHistogram.size + " lexemes:")
  for (pct <- pcts) {
    println("\t" + coverage(lc, pct))
  }
}


println("SURVEY COPORA\n\n")
survey(hygMorphed, hyg, "Hyginus")
println("\n")
survey(livyMorphed, livy, "Livy")
println("\n")
survey(mtMorphed, mt, "Minkova-Tunberg")
