import scala.io.Source
import java.io.PrintWriter

// Sort a CEX of verb stems by their sequence in Lewis-Short.

val verbFile = "morphology-latin/shared/stems-tables/verbs/verbs.cex"

// This is for Lewis-Short.  You can add others if you have
// your own URN strings with numeric terminal value.
val prefixList = Vector("ls.n")

def stripPrefix(s: String, prefixes: Vector[String]): String = {
  if (prefixes.isEmpty) {
    s
  } else {
    stripPrefix(s.replaceFirst(prefixes.head, ""), prefixes.tail)
  }
}

/**
*/
def sortFile(fName: String =  verbFile, prefixes: Vector[String] = prefixList) : Unit = {
  val lines = Source.fromFile(fName).getLines.toVector
  val label = lines.head
  // pair up entries with numeric number of LS id:
  val paired = for (entry <- lines.tail.filter(_.nonEmpty)) yield {
    val cols = entry.split("#")
    // cols(1) is the Lewis-Short identifier:  strip off
    // its prefix and make it an Int for numeric sorting:
    val lexNum = stripPrefix(cols(1),prefixes).toInt
    (lexNum, entry)
  }
  // sort by number (part 1), produce entry only (part 2)
  val sorted = for (l <- paired.sortBy(_._1).distinct) yield {
    l._2
  }
  // rewrite file:
  new PrintWriter(fName){ write (label + "\n\n" + sorted.mkString("\n")); close;}
}
