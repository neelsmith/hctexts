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
  val colorMap = Map(

    "conjunction" -> "#e7298a",
    "preposition" -> "#e7298a",

    "participle" -> "#3182bd",
    "infinitive" -> "#08519c",
    "conjugated verb" -> "#6baed6", //


    "adjective" -> "#74c476",
    "pronoun" -> "#31a354",
    "noun" -> "#006d2c"
  )


  val verbFilt = MorphologyFilter(pos = Some("verb"))
  val infinFilt = MorphologyFilter(pos = Some("infinitive"))
  val ptcplFilt = MorphologyFilter(pos = Some("participle"))

  val adjFilt = MorphologyFilter(pos = Some("adjective"))
  val nounFilt = MorphologyFilter(pos = Some("noun"))
  val pronounFilt = MorphologyFilter(pos = Some("pronoun"))

  val conjFilt = MorphologyFilter(indeclinablePoS = Some(Conjunction))
  val prepFilt = MorphologyFilter(indeclinablePoS = Some(Preposition))

  val spanEnd = "</span>"


  /** Format single-line syntax markdown as regular markdown.
  *
  * @param s String to format.
  */
  def format(s: String) : String = {
     val sub = s.replaceAll("\\|", "\n").replaceAll("^> ", ">").replaceAll("\\* ", "*").replaceAll(" \\*","*")
     sub.split("\n").map(_.replaceAll("^[ ]+","")).mkString("\n")
  }


  /** Format markdown color key for part of speech highlighting.*/
  def colorKey : String = {
    val keys = for (pos <- Vector("conjugated verb", "participle",  "infinitive", "noun",  "pronoun", "adjective", "conjunction", "preposition")) yield {
      "<span style=\"color:" + colorMap(pos) + "\">" + pos + spanEnd
    }
    keys.mkString("\n\n")
  }



  def posHighlight(phr: LatinPhrase) : String = {


    val verbHl = Highlighter(verbFilt, "<span style=\"color:" + colorMap("conjugated verb") + "\">", spanEnd)
    val infinHl = Highlighter(infinFilt, "<span style=\"color:" + colorMap("infinitive") + "\">", spanEnd)
    val ptcplHl = Highlighter(ptcplFilt, "<span style=\"color:" + colorMap("participle") + "\">", spanEnd)

    val nounHl = Highlighter(nounFilt, "<span style=\"color:" + colorMap("noun") + "\">", spanEnd)
    val pronounHl = Highlighter(pronounFilt, "<span style=\"color:" + colorMap("pronoun") + "\">", spanEnd)
    val adjHl = Highlighter(adjFilt, "<span style=\"color:" + colorMap("adjective") + "\">", spanEnd)

    val conjHl = Highlighter(conjFilt, "<span style=\"color:" +  colorMap("conjunction") + "\">", spanEnd)
    val prepHl = Highlighter(prepFilt, "<span style=\"color:" +  colorMap("preposition") + "\">", spanEnd)

    val hls = Vector(verbHl, infinHl, ptcplHl,nounHl, conjHl)
    val md = format(phr.highlightForms(hls))

    val legend = "**Legend**\n\n" + colorKey + "\n\n"
    legend + "**Text**\n\n" + md
  }

  /** Write to a `LatinPhrase` to a file with parts of speech
  * highlighted.
  *
  * @param phr The LatinPhrase to write.
  * @param fName Name of the output file.
  */
  def printPosHighlight(phr: LatinPhrase, fName: String, title: String) : Unit = {
    val hdr = s"---\nlayout: page\ntitle: ${title}\n\n---\n\n"
    val md = posHighlight(phr)
    new PrintWriter(fName){write(hdr + md);close;}
  }



  /** Write a LatinPhrase to mutliple files with a different
  * part of speech highlighted with morphological analysis tool
  * tip in each file.
  *
  * @param phr LatinPhrase to write.
  * @param fBase Base of file name to write.
  */
  def printPosHovers(phr: LatinPhrase, fBase: String, title: String) : Unit = {
    val nounHdr = "---\nlayout: page\ntitle: \"" + title + ": substantives highlighted\"\n\n---\n\n"
    val nounFile = fBase + "-nouns-pronouns.md"
    val nounMd = format(phr.hover(Vector(nounFilt, pronounFilt)))
    new PrintWriter(nounFile){write(nounHdr + nounMd);close;}

    val verbHdr = "---\nlayout: page\ntitle: \"" + title + ": verbs highlighted\"\n\n---\n\n"

    val verbFile = fBase + "-verbs.md"
    val verbMd = format(phr.hover(Vector(verbFilt, infinFilt, ptcplFilt )))
    new PrintWriter(verbFile){write(verbHdr + verbMd);close;}
  }
}
