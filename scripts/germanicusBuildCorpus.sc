
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.mid.validator._

// germanicus:

val catalog = "editions/catalog.cex"
val citation = "editions/citation.cex"
val xmlDir = "editions/xml"
/*
def loadCorpus(cexFile: String = "ocre-data/raw.cex") : Corpus = {
  println("Loading corpus...")
  val cex = Source.fromFile(cexFile).getLines.mkString("\n")
  val c = Corpus(cex)
  println("Done.")
  c
}
*/


val corpus = TextRepositorySource.fromFiles(catalog, citation, xmlDir).corpus
val cexXml = corpus.cex("#")


val reader = MidVerseLReader(MidDiplomaticEdition)
val univocal = Corpus(corpus.nodes.map(reader.editedNode(_)))

import java.io.PrintWriter
new PrintWriter("cex/germanicus.cex"){write(univocal.cex("#")); close;}
