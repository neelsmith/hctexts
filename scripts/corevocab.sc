import scala.io.Source
import java.io.PrintWriter


val f = "mtVocabByLivyFreq.cex"
val lns = Source.fromFile(f).getLines.toVector


for (ln <- lns) {
  val parts = ln.split("#").toVector
}



val urlBase = "http://folio2.furman.edu/lewis-short/index.html?urn=urn:cite2:hmt:ls:"

def formatLines(srcLines: Vector[String], threshold: Int = 100, currentPoS: String = "", outputLines: Vector[String] =  Vector.empty[String]) :  Vector[String] = {

  if (srcLines.isEmpty) {
    outputLines


  } else {
    val cols = srcLines.head.split("#").toVector
    val pos = cols(0)
    //val lemma = cols(1).replaceAll("ls.", "").replaceAll(":(.+)", s"${1}")
    val lemmaParts = cols(1).replaceAll("ls.", "").split(":").toVector
    val count = cols(2).toInt
    val entry = if (count > threshold) {
      val lnk = s"${urlBase}${lemmaParts(0)}"
      val lemma = lemmaParts(1)
      //println(s"${pos} - [${lemma}](${lnk})")
      val vocabItem = s"1. [${lemma}](${lnk}) (${count})"

      if (pos != currentPoS) {
        val hdr = "\n\n## " + pos + "\n"
        formatLines(srcLines.tail, threshold, pos,  outputLines :+ hdr :+ vocabItem)

      } else {
        formatLines(srcLines.tail, threshold, currentPoS, outputLines :+ vocabItem)
      }



    } else {
      formatLines(srcLines.tail, threshold, currentPoS, outputLines)
    }
    entry
  }
}

def printEm(cutoff: Int = threshold) = {
  val formatted = formatLines(lns).mkString("\n")
  new PrintWriter(s"vocab-${cutoff}-cutoff.md"){write(formatted);close;}
}
