import scala.io.Source
import java.io.PrintWriter

// Sort a CEX of verb stems by their sequence in Lewis-Short.
// Here's an example file:
val verbFile = "morphology-latin/shared/stems-tables/verbs-simplex/verbs.cex"

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
    val lexString = stripPrefix(cols(1),prefixes).replaceAll("[a-z]+$","")
    val lexNum = lexString.toInt

    (lexNum, entry)
  }
  // sort by number (part 1), produce entry only (part 2)
  val sorted = for (l <- paired.sortBy(_._1).distinct) yield {
    l._2
  }

  // rewrite file:
  new PrintWriter(fName){ write (label + "\n\n" + sorted.mkString("\n")); close;}
}

val sharedCex = Vector (
  "morphology-latin/shared/stems-tables/adjectives/adjs.cex",
  "morphology-latin/shared/stems-tables/adjectives/properadjs.cex",

  "morphology-latin/shared/stems-tables/indeclinables/indecls.cex",
  "morphology-latin/shared/stems-tables/indeclinables/numerals.cex",


  "morphology-latin/shared/stems-tables/nouns/cognomina-ls.cex",
  "morphology-latin/shared/stems-tables/nouns/ethnics.cex",
  "morphology-latin/shared/stems-tables/nouns/geo-ls.cex",
  "morphology-latin/shared/stems-tables/nouns/gods.cex",
  "morphology-latin/shared/stems-tables/nouns/nomina-ls.cex",
  "morphology-latin/shared/stems-tables/nouns/nouns.cex",
  "morphology-latin/shared/stems-tables/nouns/persons.cex",

  "morphology-latin/shared/stems-tables/verbs-compound/compverbs.cex",
  "morphology-latin/shared/stems-tables/verbs-simplex/verbs.cex"

)

val orthoLimitedCex = Vector(
    "morphology-latin/lat23/stems-tables/adjectives/adjs.cex",
    "morphology-latin/lat24/stems-tables/adjectives/adjs.cex",
    "morphology-latin/lat25/stems-tables/adjectives/adjs.cex",

    "morphology-latin/lat23/stems-tables/indeclinables/indecls.cex",
    "morphology-latin/lat23/stems-tables/indeclinables/numerals.cex",
    "morphology-latin/lat24/stems-tables/indeclinables/indecls.cex",
    "morphology-latin/lat24/stems-tables/indeclinables/numerals.cex",
    "morphology-latin/lat25/stems-tables/indeclinables/indecls.cex",
    "morphology-latin/lat25/stems-tables/indeclinables/numerals.cex",

    "morphology-latin/lat23/stems-tables/nouns/nouns.cex",
    "morphology-latin/lat23/stems-tables/nouns/nomina-ls.cex",
    "morphology-latin/lat24/stems-tables/nouns/nouns.cex",
    "morphology-latin/lat24/stems-tables/nouns/nomina-ls.cex",
    "morphology-latin/lat25/stems-tables/nouns/nouns.cex",
    "morphology-latin/lat25/stems-tables/nouns/nomina-ls.cex",

    "morphology-latin/lat23/stems-tables/verbs-compound/compverbs.cex",
    "morphology-latin/lat23/stems-tables/verbs-simplex/verbs.cex",

    "morphology-latin/lat24/stems-tables/verbs-compound/compverbs.cex",
    "morphology-latin/lat24/stems-tables/verbs-simplex/verbs.cex"  ,

    "morphology-latin/lat25/stems-tables/verbs-compound/compverbs.cex",
    "morphology-latin/lat25/stems-tables/verbs-simplex/verbs.cex"
)

def tidyAll : Unit = {
  for (cexFile <- sharedCex) {
    println("Sort " + cexFile + "...")
    sortFile(cexFile)
  }
  for (cexFile <- orthoLimitedCex) {
    println("Sort " + cexFile + "...")
    sortFile(cexFile)
  }
  
  println("\nDone tidying.")
}
println("\n\nSort a tabular file by Lewis-Short ID:\n")
println("\tsortFile([FILE], [PREFIXLIST])\n")
println("Sort predefined list of CEX files by Lewis-Short ID:\n")
println("\ttidyAll\n")
