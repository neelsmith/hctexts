package edu.holycross.shot.hctexts

import edu.holycross.shot.mid.validator._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._
import edu.holycross.shot.tabulae._


import java.io.PrintWriter

import wvlet.log._
import wvlet.log.LogFormatter.SourceCodeLogFormatter

//import scala.scalajs.js
//import scala.scalajs.js.annotation._
/** Latin alphabet with 24 alphabetic characters.
* "i"is treated as a semivowel, but vocalic "u" and
* consonantal "v" are distingished.
*/
//@JSExportAll
object Latin24SyntaxString extends  LogSupport {
  //Logger.setDefaultLogLevel(LogLevel.WARN)

  val verbFilt = MorphologyFilter(pos = Some("verb"))
  val nounFilt = MorphologyFilter(pos = Some("noun"))
  val conjFilt = MorphologyFilter(indeclinablePoS = Some(Conjunction))

  val spanEnd = "</span>"

  def format(s: String) : String = {
     val sub = s.replaceAll("\\|", "\n").replaceAll("^> ", ">").replaceAll("\\* ", "*")
     sub.split("\n").map(_.replaceAll("^[ ]+","")).mkString("\n")
  }

  def printPos(phr: LatinPhrase, fName: String) : Unit = {

    val verbHl = Highlighter(verbFilt, "<span style=\"color:blue\">", spanEnd)
    val nounHl = Highlighter(nounFilt, "<span style=\"color:green\">", spanEnd)
    val conjHl = Highlighter(conjFilt, "<span style=\"color:orange\">", spanEnd)

    val hls = Vector(verbHl, nounHl, conjHl)
    val md = format(phr.highlightForms(hls))
    new PrintWriter(fName){write(md);close;}
  }
}
