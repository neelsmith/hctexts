// substtitue values
import scala.io.Source
import java.io.PrintWriter

val substitutionList = "scripts/buildCex/eutropius-que.txt"
val cexText = "cex/eutropius.cex"



// Recusively process list of strings with enclitic -que.
// Replace any enclitics showing up in the substitution list with
// explicitly demarcated form.
def substituteEnclitic(enclitic: String, sub: String, txt: String, subList: Vector[String]) :  String = {
  if (subList.isEmpty) {
    val hyphenate = sub.replaceFirst(enclitic + "$", "-" + enclitic)
    //println(sub + "->" + hyphenate)
    txt.replaceAll(sub,hyphenate)
  } else {
    val hyphenate = sub.replaceFirst("que$", "-que")
    val newText = txt.replaceAll(sub, hyphenate)
    substituteEnclitic(enclitic, subList.head, newText, subList.tail)
  }
}


def modifyText(srcFile: String = cexText, subListFile: String = substitutionList, enclitic: String = "que") : String = {
  val txt  = Source.fromFile(srcFile).getLines.mkString("\n")
  val subList = Source.fromFile(subListFile).getLines.toVector
  val modified = substituteEnclitic(enclitic, subList.head, txt, subList.tail)
  modified
}

//
// new PrintWriter(f){write(modified);close;}


println("\n\nUsage:")
