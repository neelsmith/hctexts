// label ls data

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

val idxFile = "scripts/ls_indexData.txt"
val lines = File(idxFile).lines.toVector

val lsIdMap = for (ln <- lines) yield {
  val parts = ln.split("#")
  s"ls.${parts(0)}" -> parts(1)
}
val idMap = lsIdMap.toMap


def label(s: String) = {
  try {
    s + ":" + idMap(s)
  } catch {
    case t: Throwable => s
  }
}
//
val lemmaLists =  mtparsed.map(_.lemmaId)
val labelledLemmata = lemmaLists.map( v => v.map(s =>  {
  try {
    s + ":" + idMap(s)
  } catch {
    case t: Throwable => s
  }
 }
))
val grouped = labelledLemmata.groupBy( l => l).map{ case(k,v)=> (k,v.size)}


// just gets number of forms, not frequencies
val lemmaHist : Histogram[Vector[String]]= Histogram(grouped.toVector.map{ case (k,v) => Frequency(k,v)})

// drop unanlyzed forms...
val vocabHist = Histogram(lemmaHist.frequencies.filter(_.count < 600))


mtparsed(1).token  +  label(mtparsed(1).lemmaId(0))
 mt.lexHistogram.frequencies.filter(_.item == mtparsed(1).token)
