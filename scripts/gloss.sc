import scala.io.Source
import java.io.PrintWriter
import edu.holycross.shot.cite._

val ls = "lewis-short/ls-entries.cex"
val vocabFile = "workfiles/florus/shorty-ids.txt"
val vocabList = Source.fromFile(vocabFile).getLines.toVector


// An entry in Lewis-Short
case class Entry (
  seq: Int,
  urn: Cite2Urn,
  lemma: String,
  entry: String
  ) {

  def basicMarkdown : String = {
    "###" + lemma.replaceAll("[0-9]", "") + s"\n\n${entry}"
  }
}


// create a map of id strings to LS Entry objects
def lsEntries(lsFile: String = ls):  Map[String, Entry] = {
  val lines = Source.fromFile(ls).getLines.toVector
  val entries = for (l <- lines.tail) yield {
    val cols = l.split("#")
    Entry(cols(0).toInt,
    Cite2Urn(cols(1)),
    cols(2),
    cols(3)
    )
  }
  val indexed = entries.map(e => (e.urn.objectComponent -> e))
  indexed.toMap
}

// we need a latincorpus....
// filter vocablist by frequency in corpus
//
// Then:
// 1. map vocab list to lsEntries
// 2. map entries to markdown
//
/*
// 1. map vocab to LS Entriy
val vocabEntries =  for (v <- vocab) yield {
  mapped(v)
}

// 2. map entries to markdown
val glossary  = vocabEntries.sortBy(_.seq).map(_.basicMarkdown).mkString("\n\n")
new PrintWriter("shorty-glossary.md"){write(glossary);close;}
*/
