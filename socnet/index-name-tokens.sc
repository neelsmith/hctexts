import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.mid.validator._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._

import edu.holycross.shot.histoutils._

import java.io.PrintWriter


// invoke this script from root diretory of the repository.
val f = "socnet/hyginus-persons-3+-alpha.csv"
val catalogFile = "cex/hyginus-catalog.cex"
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



// Type-parameterize this!
case class PairOStrings(s1: String, s2: String)



def writeTokenIndex(tokens: Vector[MidToken], fName : String  = "socnet/namesIndex.cex") = {
  val indexLines = for (t <- tokens) yield {
    s"${t.urn}#${t.string}"
  }

  new PrintWriter(fName){write(indexLines.mkString("\n")); close;}
}

val urnIndex = tokens.groupBy(_.urn.collapsePassageTo(2))

val tokenCounts = tokens.groupBy(_.string.toLowerCase).toVector.map{ case (k,v) => Frequency(k,v.size) }
val tokenHist : Histogram[String] = Histogram(tokenCounts)


// Pair a key value with every string token
def pairKey (s: String, tokens: Vector[MidToken]) : Vector[PairOStrings]= {
  val stringPairs = for (t <- tokens) yield {
    println("\tmatch " + s.toLowerCase + " and " + t.string.toLowerCase)
    val s1 = s.toLowerCase
    val s2 = t.string.toLowerCase
    if (s1 < s2) {
      PairOStrings(s1, s2)
    } else {
      PairOStrings(s2,s1)
    }
  }


  //println("PairKey result is " + stringPairs.toVector)
  stringPairs.toVector
}



def pairings(tokens: Vector[MidToken], pairs: Vector[PairOStrings] =  Vector.empty[PairOStrings])  : Vector[PairOStrings]  = {
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
  pairings(idx._2, Vector.empty[PairOStrings])
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

def gml(
    hist: Histogram[String],
    matchVector: Vector[Vector[PairOStrings]]
  )  : String = {
  val header = Vector(
    "graph [",
    "comment \"Co-occurrence network in Hyginus, Fabulae\"",
    "directed 0",
    "id hyginus1",
    "label \"Personal names in Hyginus, Fabulae\""
  ).mkString("\n\t")
  val trailer = "]\n"
  val nodeDeff = for (fr <- hist.frequencies) yield {
    val id = itemId(fr.item, hist.frequencies)
    val gml = s"\tnode [\n\t\tid ${id.get}\n\t\tlabel " + "\"" + s"${fr.item}" + "\"" +  s"\n\t\tfreqs ${fr.count}\n\t]"
    gml
  }


/* Format edges on this model:
	edge [
		source 217
		target 158
		weight 2
	]
*/
  val grouped = matchVector.flatten.groupBy(pr => pr)
  val pairCounts = grouped.map{ case (k,v) => (k,v.size) }.toVector.map{ case (i,count) => Frequency(i,count) }
  val pairHist = Histogram(pairCounts)
  val edgeDeff = for (prCount <- pairCounts) yield {
    //println(prCount.count + " for pair " + prCount.item)

    val source = itemId(prCount.item.s1,hist.frequencies)
    val target = itemId(prCount.item.s2,hist.frequencies)
    "\tedge [\n\t\tsource " + source.get + "\n\t\ttarget " + target.get + "\n" + "\t\tweight " +prCount.count + "\n\t]"
  }

  header + nodeDeff.mkString("\n") + edgeDeff.mkString("\n") + "\n" + trailer
}
