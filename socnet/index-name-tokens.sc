import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.mid.validator._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._


// invoke this script from root diretory of the repository.
val f = "socnet/hyginus-persons-3+-alpha.csv"
val catalogFile = "cex/catalog.cex"
val hyginusFile = "cex/hyginus.cex"
val ortho = Latin23Alphabet


val catalogCex = Source.fromFile(catalogFile).getLines.mkString("\n")
val textCex = Source.fromFile(hyginusFile).getLines.mkString("\n")
val textRepo = TextRepositorySource.fromCexString(catalogCex + "\n" + textCex)
val tcorpus = TokenizableCorpus(textRepo.corpus, ortho)

def namesList(fName: String = f, delimiter: String = ",") : Vector[String] = {
  val namesData = Source.fromFile(fName).getLines.toVector
  val namesList = for (item <- namesData) yield {
    val cols = item.split(delimiter)
    cols(0)
  }
  namesList.toVector
}

val authorityList = namesList()
val tokenOpts = for (tkn <- tcorpus.tokens) yield {
  if (authorityList.contains(tkn.string.toLowerCase)) {
    Some(tkn)
  } else {
    None
  }
}

val tokens = tokenOpts.flatten

val indexLines = for (t <- tokens) yield {
  s"${t.urn}#${t.string}"
}

import java.io.PrintWriter
new PrintWriter("socnet/namesIndex.cex"){write(indexLines.mkString("\n")); close;}
