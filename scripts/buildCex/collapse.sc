import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

def addText(accum: String, toAdd: String) : String = {
  val hyphenated = "-$".r
  hyphenated.findFirstIn(toAdd) match {
    case None => {
      // word break, add space:
      accum + toAdd + " "
    }
    case _ => {
      // Drop hyphen:
      accum + toAdd.dropRight(1)
    }
  }
}

def collapse(src: Vector[String],
  currRef: String = "",
  currText: String = "",
  output: Vector[String] = Vector.empty[String]): Vector[String] = {

  if (src.isEmpty) {
    output

  } else {
    val cols = src.head.split("#")
    if (cols.size == 2) {
      val cite = cols(0)
      val txt = cols(1)

      // First node;
      if (currRef.isEmpty) {
        collapse(src.tail, cite,  addText(currText, txt.trim), output)

      } else if (cite == currRef) {
        // Continuing content of same node:
        collapse(src.tail, currRef,  addText(currText, txt.trim), output)

      } else {
        // Write  prev passage, start new node:
        val textNode = s"${currRef}#${currText}"
        collapse(src.tail,cite, addText("",txt.trim), output :+ textNode.trim)
      }

    } else {
      println("FAILED ON '" + src.head + "'")
      // From Hyginus source, OK to ignore bad input.
      collapse(src.tail, currRef, currText, output)
    }

  }
}



def hyg(chunk: Int = 0) : Vector[String] = {
  val f = "hyginus-source-w-cites.txt"
  val lines = Source.fromFile(f).getLines.toVector.map(_.trim).filter(_.nonEmpty)

  val sample = if (chunk == 0) {lines.size} else { chunk}
  collapse(lines.take(sample))
}


def ohco2 (cex: Vector[String] = hyg()): Vector[Option[CitableNode]] = {

  val nds = for (n <- cex) yield {
    val cols = n.split("#")
    try {
      val urn = CtsUrn(cols(0))
      Some(CitableNode(urn,cols(1)))
    } catch {
      case t: Throwable => {
        println("FAILED ON " + n)
        None
      }
    }
  }
  nds
}

// val o2 = ohco2()
//  val bigS = o2.flatten.map( n => n.urn.passageComponent + ": " + n.text).mkString("\n\n")
