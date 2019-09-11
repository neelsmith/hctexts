package edu.holycross.shot.hctexts

import edu.holycross.shot.mid.validator._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.latin._


import wvlet.log._
import wvlet.log.LogFormatter.SourceCodeLogFormatter

//import scala.scalajs.js
//import scala.scalajs.js.annotation._
/** Latin alphabet with 24 alphabetic characters.
* "i"is treated as a semivowel, but vocalic "u" and
* consonantal "v" are distingished.
*/
//@JSExportAll
object Latin24Syntax extends LatinAlphabet with MidOrthography with LogSupport {
  Logger.setDefaultLogLevel(LogLevel.DEBUG)
  /** Descriptive phrase required by MidOrthography trait.*/
  def orthography = "Latin alphabet with 24 alphabetic characters and additional markup for syntactic features"

  /** Descriptive phrase required by MidOrthography trait.*/
  def label = """ Latin alphabet with 24 alphabetic characters.
  "i" is treated as a semivowel, but vocalic "u" and
  consonantal "v" are distingished. """


  /** Set of all valid Unicode code points.*/
  val charSet:  Set[Int] = (for (ch <- alphabetString ++ punctuationString ++ metaCharacters ++ syntaxCharacters) yield {ch.toInt}).toSet


  /** True if cp is a valid code point. Required by
  * MidOrthography trait.
  *
  * @param cp Code point to test.
  */
  def validCP(cp: Int): Boolean  = {
    charSet.contains(cp)
  }


  /** List of token categories recognizable from this orthography.
  * Required by MidOrthography trait.
  */
  def tokenCategories: Vector[MidTokenCategory]  = {
    Vector(edu.holycross.shot.latin.PraenomenToken, edu.holycross.shot.latin.PunctuationToken, edu.holycross.shot.latin.LexicalToken, edu.holycross.shot.latin.NumericToken, InvalidToken)
  }

  /** Remove trailing punctuation chracters.*/
  def depunctuate (s: String, depunctVector: Vector[String] = Vector.empty): Vector[String] = {
    val trimmed = s.trim
    val trailChar = s"${trimmed.last}"
    if (punctuationString.contains(trailChar)) {
      val dropLast = trimmed.reverse.tail.reverse
      if (dropLast.nonEmpty) {
        depunctuate(dropLast, trailChar +: depunctVector)
      } else {
        s +: depunctVector
      }

    } else {
      s +: depunctVector
    }
  }


  /** Break up string based on "syntactic markup" characters.
  */
  def syntaxStrings (src: String,
    current: String = "",
    syntaxVector: Vector[String] = Vector.empty): Vector[String] = {
    if  (src.isEmpty) {
      syntaxVector :+ current

    } else if (syntaxCharacters.contains(src.head)) {
      val hd : String = src.head.toString
      val vect : Vector[String] = syntaxVector :+ current :+ hd
      syntaxStrings(src.tail, " ", vect)

     } else {
       val newString : String = current + src.head.toString
       syntaxStrings(src.tail, newString, syntaxVector)
     }
  }

  def lexicalCategory(s: String): Option[MidTokenCategory] = {
  if (numerics.contains(s(0).toUpper)) {
    if (numeric(s)) {
      Some(edu.holycross.shot.latin.NumericToken)
    } else {
      None
    }
  } else if (alphabetString.contains(s(0).toLower)) {
    if (alphabetic(s)) {
      Some(edu.holycross.shot.latin.LexicalToken)
    } else {
      None
    }

  } else if (syntaxCharacters.contains(s(0))) {
    Some(edu.holycross.shot.latin.PunctuationToken)

  } else {
    None
  }
}



def nodeToTokens(n: CitableNode) : Vector[MidToken] = {
    val urn = n.urn
    val strs : Vector[String] = syntaxStrings(n.text).filter(_.nonEmpty)
    debug("SYNTAX STRS:  " + strs)
    // initial chunking on white space, enclitic delimiter, and elision marker
    val units = strs.map(_.split("[ \\+\\']").filter(_.nonEmpty)).flatten
    debug("UNITS " + units)


    val classified = for (unit <- units.zipWithIndex) yield {
      debug("UNIT: " + unit)
      val newPassage = urn.passageComponent + "." + unit._2
      val newVersion = urn.addVersion(urn.versionOption.getOrElse("") + "_tkns")
      val newUrn = CtsUrn(newVersion.dropPassage.toString + newPassage)
      val trimmed = unit._1.trim
      // process praenomina first since "." is part
      // of the token:
      val tokenClass: Vector[MidToken] = if
       (LatinTextReader.praenomina.contains(unit._1)) {
        Vector(MidToken(newUrn, unit._1, Some(edu.holycross.shot.latin.PraenomenToken)))

      } else  if (trimmed(0) == '"') {
        val v = Vector(MidToken(newUrn, "\"", Some(edu.holycross.shot.latin.PunctuationToken)))
        debug("Punctuation vector: " + v)
        v

      } else {
        val depunctuated = depunctuate(unit._1)
        val first =  MidToken(newUrn, depunctuated.head, lexicalCategory(depunctuated.head))
        val trailingPunct = for (punct <- depunctuated.tail zipWithIndex) yield {
          MidToken(CtsUrn(newUrn + "_" + punct._2), punct._1, Some(edu.holycross.shot.latin.PunctuationToken))
        }
        debug("First " + first + ", trailing punct " + trailingPunct)
        val v = first +: trailingPunct
        debug("resulting vector: " + v)
        v
      }
      tokenClass
    }
    debug("Classified " + classified.toVector)
    classified.toVector.flatten
  }



  /** Tokenization of a citable node of text in this orthography.
  *
  * @param n Node of text to tokenize.
  */
  def tokenizeNode(n: CitableNode): Vector[MidToken] = {
    nodeToTokens(n)
  }


  //Regular expressions for syllabification
  /** RE for vowel-consonant-vowel pattern.*/
  val vcv = "(.*[aeiou])([bcdfghklmnpqrstvx])([aeiou].*)".r
  /** RE for vowel+consonant cluster pattern.*/
  val consCluster = "(.*[aeiou])([bcdfghklmnpqrstvx]+)([bcdfghklmnpqrstvx])([aeiou].*)".r
  /** RE for vowel+mute+liquid pattern.*/
  val muteLiquid = "(.*[aeiou])([bdgptc])([lr])([aeiou].*)".r
  /** RE for diphthong+vowel pattern.*/
  val diphVowel = "(.*)(ae|au|ei|eu|oe)([aeiou].*)".r
  /** RE for a followed by non-diphthong. */
  val aSplits = "(.*a)([io].*)".r
  /** RE for e followed by non-diphthong. */
  val eSplits = "(.*e)([ao].*)".r
  /** RE for i followed by non-diphthong. */
  val iSplits = "(.*i)([aeiou].*)".r
  /** RE for o followed by non-diphthong. */
  val oSplits = "(.*o)([aiou].*)".r
  /** RE for a followed by vowel. */
  val uSplits = "(.*u)([aeiou].*)".r

  // adjust semivowels
  /** RE for word-initial i followed by vowel. */
  val initialJ = "^i-([aeiou].+)".r
  /** RE for syllable-initial i followed by vowel. */
  val syllInitialJ = "(.+[aeiou])-([bcfghklmnpqrvx]?)i-([aeiou].+)".r
  /** RE for diphthong ei when it should be read as e+semivowel i. */
  val fakeDiphthongI = "(.*)ei-([aeiou].+)".r

  /** RE for word-initial v followed by vowel. */
  val initialV = "^v-(.+)".r
  /** RE for syllable-initial v followed by vowel. */
  val syllInitialV = "(.+[aeiou])-([bcfghklmnpqrvx]?)v-([aeiou].+)".r
  /** RE for  e followed by v. */
  val fakeDiphthongU = "(.*)(av|ev)-([aeiou].+)".r

  /** Ordered sequence of alphabetic characters.*/
  def alphabetString: String = {
    "abcdefghiklmnopqrstuvxyz"

  }
  /** Ordered sequence of allowed punctuation characters.*/
  def punctuationString: String = {
    //"(),;:.?"
    """—-(),;:."?!"""
  }


  def metaCharacters: String = {
    "'+"
  }

  def syntaxCharacters: String = {
    "|>*"
  }

  /** Set of all recognized diphthongs.*/
  def diphthongs: Set[String] = {
    Set("ae","au",
        "ei", "eu",
        "oe"
        //huius,cuius,huic,cui,hui are exceptions
    )
  }
  /** Set of all recognized consonants.*/
  def consonants: Set[String] = {
    Set("b","c","d","f","g","h","k","l","m","n","p","q","r","s","t","v","x","y","z")
  }
  /** Set of all recognized vowels.*/
  def vowels: Set[String] = {
    Set("a","e","o","u")
  }
  /** Set of all recognized semivowels.*/
  def semivowels: Set[String] = {
    Set("i")
  }

  /** True if s is composed only of alphabetic characters.
  *
  * @param s String to check.

  def alphabetic(s: String) : Boolean= {
    val alphaOnly = s.filter(c => alphabetString.contains(c.toLower))
    (alphaOnly.size == s.size)
  }  */

  /** Divide a given String into a Vector of
  * Strings.
  *
  * @param s String to syllabify.
  */
  def syllabify(s: String): Vector[String] = {
    val protectQu = s.replaceAll("qu","qu").toLowerCase
    val a =   protectQu match {
      case aSplits(opening,apart) =>
      Vector(opening,apart).mkString("-")
      case _ => protectQu
    }
    val e =  a match {
      case eSplits(opening,epart) =>
      Vector(opening,epart).mkString("-")
      case _ => a
    }
    val i =  e match {
      case iSplits(opening,ipart) =>
      Vector(opening,ipart).mkString("-")
      case _ => e
    }
    val o =  i match {
      case oSplits(opening,opart) =>
      Vector(opening,opart).mkString("-")
      case _ => i
    }
    val u =  o match {
      case uSplits(opening,upart) =>
      Vector(opening,upart).mkString("-")
      case _ => o
    }
    // Rules for grouping consonants:
    val rule1 = u match {
      case vcv(open,cons,close) =>
        Vector(open,cons + close).mkString("-")
      case _ => u
    }
    val rule2 = rule1 match {
      case muteLiquid(open,mute,liquid,close) =>
       Vector(open, mute + liquid + close).mkString("-")
      case _ => rule1
    }
    val rule3 = rule2 match {
      case consCluster(open,cluster,lastC,close) =>
        Vector(open +cluster, lastC + close).mkString("-")
      case _ => rule2
    }
    val rule4 = rule3 match {
        case diphVowel(open,diphthong,close) =>
        Vector(open + diphthong, close).mkString("-")
      case _ => rule3
    }



    if (rule4.toLowerCase == protectQu) {
      val adjusted = restoreSemiConsonants(protectQu)
      adjusted.replaceAll("qu","qu").split("-").toVector
    } else {
      syllabify(rule4)
    }
  }

  def restoreSemiConsonants(s: String) : String = {
    val i1 = s match {
      case initialJ(x) => "i" + x
      case _ => s
    }
    val i2 = i1  match {
      case syllInitialJ(start,cons,rest) =>  start + cons + "-i" + rest
      case _ => i1
    }
    val i3 = i2 match {
      case fakeDiphthongI(start,rest) => start + "e-i"  + rest
      case _ => i2
    }

    val   v1 = i3 match {
      case syllInitialV(start,cons,rest) =>  start + cons + "-u" + rest
      case _ => i3
    }

    val v2 = v1 match {
      case initialV(x) => "u" + x
      case _ => v1
    }

    val v3 = v2 match {
      case fakeDiphthongU(start,diph,rest) => {
        start + diph(0)  + "-" + diph(1) + rest
      }
      case _ => v2
    }
    v3
  }
  override def toString: String = {
    "Latin alphabet with 24 alphabetic characters."
  }

}
