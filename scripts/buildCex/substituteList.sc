// substtitue values
import scala.io.Source
import java.io.PrintWriter

val subs = "eutropius-que.txt"
val f = "cex/eutropius.cex"
val txt  = Source.fromFile(f).getLines.mkString("\n")

val subList = Source.fromFile(subs).getLines.toVector

def subVal(sub: String, txt: String, subList: Vector[String]) :  String = {
  if (subList.isEmpty) {
    val hyphenate = sub.replaceFirst("que$", "-que")
    //println(sub + "->" + hyphenate)
    txt.replaceAll(sub,hyphenate)
  } else {
    val hyphenate = sub.replaceFirst("que$", "-que")
    val newText = txt.replaceAll(sub, hyphenate)
    subVal(subList.head, newText, subList.tail)
  }
}

val modified = subVal(subList.head, txt, subList.tail)

new PrintWriter(f){write(txt);close;}
