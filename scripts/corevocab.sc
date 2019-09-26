import scala.io.Source

val f = "core-vocab.cex"

val lns = Source.fromFile(f).getLines.toVector


for (ln <- lns) {
  val parts = ln.split("#").toVector
}



val urlBase = "http://folio2.furman.edu/lewis-short/index.html?urn=urn:cite2:hmt:ls:"
def formatLines(srcLines: Vector[String], currentPoS: String = "", outputLines: Vector[String] =  Vector.empty[String]) :  Vector[String] = {

  if (srcLines.isEmpty) {
    outputLines
  } else {
    val cols = srcLines.head.split("#").toVector

    val pos = cols(0)
    //val lemma = cols(1).replaceAll("ls.", "").replaceAll(":(.+)", s"${1}")
    val lemmaParts = cols(1).replaceAll("ls.", "").split(":").toVector
    val count = cols(2).toInt
    if (count > 250) {
      val lnk = s"${urlBase}${lemmaParts(0)}"
      val lemma = lemmaParts(1)
      println(s"${pos} - [${lemma}](${lnk})")
    } else {
      //println("too low")
    }
    formatLines(srcLines.tail, currentPoS, outputLines)
  }
}
