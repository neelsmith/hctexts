import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.mid.validator._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._

import edu.holycross.shot.histoutils._

import java.io.PrintWriter


val catalogFile = "cex/florus-catalog.cex"
val hyginusFile = "cex/florus.cex"
val ortho = Latin24Alphabet



val catalogCex = Source.fromFile(catalogFile).getLines.mkString("\n")
val textCex = Source.fromFile(hyginusFile).getLines.mkString("\n")
val textRepo = TextRepositorySource.fromCexString(catalogCex + "\n" + textCex)
val tcorpus = TokenizableCorpus(textRepo.corpus, ortho)
