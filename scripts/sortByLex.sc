import scala.io.Source
import java.io.PrintWriter


val f = "morphology/lat24/stems-tables/nouns/lat24nouns.cex"

// This is for Lewis-Short.  You can add others if you have
// your own URN strings with numeric terminal value.
val prefixList = Vector("ls.n", "livylex.n")

def stripPrefix(s: String, prefixes: Vector[String]): String = {
  if (prefixes.isEmpty) {
    s
  } else {
    stripPrefix(s.replaceFirst(prefixes.head, ""), prefixes.tail)
  }
}

def sortFile(fName: String =  f, prefixes: Vector[String] = prefixList) : Unit = {
  val lines = Source.fromFile(fName).getLines.toVector
  val label = lines.head
  val mapped = for (entry <- lines.tail.filter(_.nonEmpty)) yield {
    val cols = entry.split("#")


    val lexNum = stripPrefix(cols(1),prefixes).toInt
    (lexNum, entry)
  }
  val sorted = for (l <- mapped.sortBy(_._1).distinct) yield {
    l._2
  }

  new PrintWriter(fName){ write (label + "\n\n" + sorted.mkString("\n")); close;}
}
