import scala.io.Source
import java.io.PrintWriter


val f = "morphology/lat24/stems-tables/nouns/lat24nouns.cex"


def sortFile(fName: String =  f) : Unit = {
  val lines = Source.fromFile(fName).getLines.toVector
  val label = lines.head
  val mapped = for (entry <- lines.tail) yield {
    val cols = entry.split("#")
    val lexNum = cols(1).replaceFirst("ls.n", "").toInt
    (lexNum, entry)
  }
  for (l <- mapped.sortBy(_._1).distinct) {
    println(l._2)
  }
}
