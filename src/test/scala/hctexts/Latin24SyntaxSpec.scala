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
    println("CONVERTED " + Latin24Syntax.nodeToTokens(cn))

  }


  it should "deal with a longer selection" in {

    val sntx = """|sed debebatur, ut opinor, fatis tantae origo urbis maximique secundum deorum opes imperii principium.|||>*vi compressa Vestalis cum geminum partum edidisset,*|>|>*seu ita rata, seu quia deus auctor culpae honestior erat,*||Martem incertae stirpis patrem nuncupat.||sed nec dii nec homines aut ipsam aut stirpem a crudelitate regia vindicant;||sacerdos vincta in custodiam datur;|||>pueros in profluentem aquam mitti||iubet.||forte quadam divinitus super ripas Tiberis effusus lenibus stagnis nec adiri usquam ad iusti cursum poterat amnis||et||> posse quamvis languida mergi aqua infantes||spem ferentibus dabat.||ita, velut defuncti regis imperio, in proxima eluvie,||>*ubi nunc ficus Ruminalis est — Romularem vocatam ferunt—,*||pueros exponunt.||vastae tum in his locis solitudines erant.||tenet fama,||>*cum fluitantem alveum,*|>|>> *quo expositi erant pueri,*|>|> *tenuis in sicco aqua destituisset*,||>lupam sitientem ex montibus,|>|> *qui circa sunt*,|>|>ad puerilem vagitum cursum flexisse;||>eam summissas infantibus adeo mitem praebuisse mammas,|>|>*ut lingua lambentem pueros magister regii pecoris invenerit—*|||>Faustulo fuisse nomen||ferunt—;|||>ab eo ad stabula Larentiae uxori educandos datos.|||sunt||>*qui*|>|>>Larentiam vulgato corpore lupam inter pastores vocatam|>|>*putent*;|>|>inde locum fabulae ac miraculo datum.*||ita geniti itaque educati,||>*cum primum adolevit aetas*,||nec in stabulis nec ad pecora segnes, venando peragrare saltus.||hinc robore corporibus animisque sumpto iam non feras tantum subsistere,||sed in latrones praeda onustos impetus facere||pastoribusque rapta dividere||et cum his crescente in dies grege iuvenum seria ac iocos celebrare .|"""

    val synstrs = Latin24Syntax.syntaxStrings(sntx)

  }

}
