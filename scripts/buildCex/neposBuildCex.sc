// Read XML text repository, and use an MidReader to generate a
// univocal CEX version of Germanicus, Aratea.


import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.mid.validator._

// germanicus:
val catalog = "editions/catalog.cex"
val citation = "editions/citation.cex"
val xmlDir = "editions/xml"




val corpus = TextRepositorySource.fromFiles(catalog, citation, xmlDir).corpus
val neposUrn = CtsUrn("urn:cts:latinLit:stoa0588.stoa001.fleckeisen:")
val neposXml = corpus ~~ neposUrn



val reader = MidTeiReader(MidDiplomaticEdition)
val univocal = Corpus(neposXml.nodes.map(reader.editedNode(_)))


val cex = univocal.cex("#")

import java.io.PrintWriter
new PrintWriter("cex/nepos.cex"){write(cex); close;}
