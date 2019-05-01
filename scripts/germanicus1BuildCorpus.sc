
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.mid.validator._

// germanicus:
val catalog = "editions/catalog.cex"
val citation = "editions/citation.cex"
val xmlDir = "editions/xml"



val corpus = TextRepositorySource.fromFiles(catalog, citation, xmlDir).corpus
val cexXml = corpus.cex("#")


val reader = MidVerseLReader(MidDiplomaticEdition)
val univocal = Corpus(corpus.nodes.map(reader.editedNode(_)))

import java.io.PrintWriter
new PrintWriter("cex/germanicus.cex"){write(univocal.cex("#")); close;}
