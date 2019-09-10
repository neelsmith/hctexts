package edu.holycross.shot.hctexts

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import  edu.holycross.shot.mid.validator._
import edu.holycross.shot.latin._
import org.scalatest.FlatSpec



class Latin24AlphabetMidTraitSpec extends FlatSpec {








  "The Latin24Syntax object" should "recognize double quotes as punctuation" in {
    val cex = """urn:cts:latinLit:phi0881.phi003.latinlib:126#increpat "o patrum suboles oblita priorum,"""
    val corpus = Corpus(cex)
    val tokens = Latin24Syntax.tokenizeCorpus(corpus)
    //println(tokens)

  }

  it should "recognize plus sign as enclitic delimiter" in {


    val cex = "urn:cts:omar:stoa0179.stoa001.omar:1.pr.7#suum conditoris+que sui parentem"
    val corpus = Corpus(cex)
    val tokens = Latin24Syntax.tokenizeCorpus(corpus)
    //println(tokens)
  }

  it should "recognize single quote as elision marker" in pending /*{

  }*/

  it should "convert a node to a vector of tokens" in {
    val u = CtsUrn("urn:cts:latinLit:phi0881.phi003.latinlib:126")
    val txt = "increpat \"o patrum suboles oblita priorum,"
    val cn = CitableNode(u,txt)
    //println(Latin24Syntax.nodeToTokens(cn))
  }

  it should "split syntactic markup" in {
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    //println(Latin24Syntax.syntaxStrings(txt))
  }
  it should "handle > as a syntax token" in {
    val u = CtsUrn("urn:cts:latinLit:phi0881.phi003.latinlib:126")
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    val cn = CitableNode(u,txt)
    println("CONVERTED" + Latin24Syntax.nodeToTokens(cn))

  }


}
