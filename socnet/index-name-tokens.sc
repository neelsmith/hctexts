import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.mid.validator._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._

import edu.holycross.shot.histoutils._

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


def writeTokenIndex(tokens: Vector[MidToken], fName : String  = "socnet/namesIndex.cex") = {
  val indexLines = for (t <- tokens) yield {
    s"${t.urn}#${t.string}"
  }
  import java.io.PrintWriter
  new PrintWriter(fName){write(indexLines.mkString("\n")); close;}
}

val urnIndex = tokens.groupBy(_.urn.collapsePassageTo(2))

val tokenCounts = tokens.groupBy(_.string.toLowerCase).toVector.map{ case (k,v) => Frequency(k,v.size) }
val tokenHist : Histogram[String] = Histogram(tokenCounts)


// Pair a key value with every string token
def pairKey (s: String, tokens: Vector[MidToken]) : Vector[(String,String)]= {
  val stringPairs = for (t <- tokens) yield {
    println("\tmatch " + s.toLowerCase + " and " + t.string.toLowerCase)
    (s.toLowerCase, t.string.toLowerCase)
  }
  println("PairKey esult is " + stringPairs.toVector)
  stringPairs.toVector
}



def pairings(tokens: Vector[MidToken], pairs: Vector[(String, String)] =  Vector.empty[(String, String)])  : Vector[(String, String)]  = {
  if (tokens.isEmpty) {
    pairs
  } else {
    val tkn = tokens.head
    println("\t" + tkn.string.toLowerCase)
    val pair = pairKey(tkn.string, tokens.tail)

    pairings(tokens.tail, pairs ++ pair)
  }
}

val graphMatches = for (idx <- urnIndex.toVector) yield {
  println("Pair " + idx._2.map(_.string.toLowerCase))
  pairings(idx._2, Vector.empty[(String, String)])
}

// Get an artibtrary ID for a String
def itemId(s: String, freqs: Vector[Frequency[String]]) : Option[Int] = {
  try {
    val matches = freqs.filter(_.item == s)
    val idx: Int = freqs.indexOf(matches(0))
    Some(idx)
  } catch {
    case t: Throwable => None
  }
}

def gml(hist: Histogram[String]) = {
  val header = Vector("graph","comment \"Co-occurrence network in Hyginus, Fabule\"", "directed 0", "id hyginus1", "label \"Personal names in Hyginus, Fabule\"").mkString("\n\t") + "\n"

/*
  node [
		id 151
		label "Ixion"
		freqs 1
	]
*/
  val nodeDeff = for (fr <- hist.frequencies) yield {
    val id = itemId(fr.item, hist.frequencies)
    val gml = s"\tnode [\n\t\tid ${id.get}\n\t\tlabel " + "\"" + s"${fr.item}" + "\"" +  s"\n\t\tfreqs ${fr.count}\n\t]"
    gml
  }
  // Get an unique INT id:
  println




}
