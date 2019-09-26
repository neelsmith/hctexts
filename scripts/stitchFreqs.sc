import scala.io.Source
import edu.holycross.shot.histoutils._

val bookList = Vector("1", "10","2","21","22","23","24","25","26","27","28","29","3","30","31","32","33","34","35","36","37","38","39","4","40","41","42","43","44","45","5","6","7","8","9")

case class VocabItem(pos: String, lemmaId: String) {
  override def toString = {
    pos + "#" + lemmaId
  }
}

def histoBook(bk: String) : Histogram[VocabItem] = {
  val f = s"vocabByPos-${bk}.cex"
  val lns = Source.fromFile(f).getLines.toVector
  val freqs = for (ln <- lns) yield {
    val cols = ln.split("#")
    Frequency(VocabItem(cols(0),  cols(1)), cols(2).toInt)
  }
  Histogram(freqs)
}

val zeroState = Vector.empty[Frequency[VocabItem]]
val zeroHist = Histogram(zeroState)

def stitch(bks: Vector[String], cumulative: Histogram[VocabItem] = zeroHist) :  Histogram[VocabItem]= {

  if (bks.isEmpty) {
    cumulative
  } else {

    stitch(bks.tail, cumulative ++ histoBook(bks.head))
  }
}

val stitched = stitch(bookList)
val sorted = stitched.frequencies.sortBy(fr => (fr.item.pos, fr.count)).reverse

val outputStr = sorted.map(f => f.item + "#" + f.count).mkString("\n")
