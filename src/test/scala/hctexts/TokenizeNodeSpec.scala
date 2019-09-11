package edu.holycross.shot.hctexts

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import  edu.holycross.shot.mid.validator._
import edu.holycross.shot.latin._
import org.scalatest.FlatSpec



class TokenizeNodeSpec extends FlatSpec {








  "The Latin24Syntax object" should "split syntactic markup" in {
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    //println(Latin24Syntax.syntaxStrings(txt))
  }
  it should "handle > as a syntax token" in {
    val u = CtsUrn("urn:cts:latinLit:phi0881.phi003.latinlib:126")
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    val cn = CitableNode(u,txt)
    println("CONVERTED:\n" + Latin24Syntax.nodeToTokens(cn).map(t => t.text + " " + t.tokenCategory.get).mkString("\n"))

  }


}
