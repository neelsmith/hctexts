package edu.holycross.shot.hctexts

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import  edu.holycross.shot.mid.validator._
import edu.holycross.shot.latin._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.latincorpus._
import scala.io.Source

import org.scalatest.FlatSpec

class TokenizeNodeSpec extends FlatSpec {

  val sntx = """|sed debebatur, ut opinor, fatis tantae origo urbis maximique secundum deorum opes imperii principium.|||>*vi compressa Vestalis cum geminum partum edidisset,*|>|>*seu ita rata, seu quia deus auctor culpae honestior erat,*||Martem incertae stirpis patrem nuncupat.||sed nec dii nec homines aut ipsam aut stirpem a crudelitate regia vindicant;||sacerdos vincta in custodiam datur;|||>pueros in profluentem aquam mitti||iubet.||forte quadam divinitus super ripas Tiberis effusus lenibus stagnis nec adiri usquam ad iusti cursum poterat amnis||et||> posse quamvis languida mergi aqua infantes||spem ferentibus dabat.||ita, velut defuncti regis imperio, in proxima eluvie,||>*ubi nunc ficus Ruminalis est — Romularem vocatam ferunt—,*||pueros exponunt.||vastae tum in his locis solitudines erant.||tenet fama,||>*cum fluitantem alveum,*|>|>> *quo expositi erant pueri,*|>|> *tenuis in sicco aqua destituisset*,||>lupam sitientem ex montibus,|>|> *qui circa sunt*,|>|>ad puerilem vagitum cursum flexisse;||>eam summissas infantibus adeo mitem praebuisse mammas,|>|>*ut lingua lambentem pueros magister regii pecoris invenerit—*|||>Faustulo fuisse nomen||ferunt—;|||>ab eo ad stabula Larentiae uxori educandos datos.|||sunt||>*qui*|>|>>Larentiam vulgato corpore lupam inter pastores vocatam|>|>*putent*;|>|>inde locum fabulae ac miraculo datum.*||ita geniti itaque educati,||>*cum primum adolevit aetas*,||nec in stabulis nec ad pecora segnes, venando peragrare saltus.||hinc robore corporibus animisque sumpto iam non feras tantum subsistere,||sed in latrones praeda onustos impetus facere||pastoribusque rapta dividere||et cum his crescente in dies grege iuvenum seria ac iocos celebrare .|"""


  val urn = CtsUrn("urn:cts:omar:stoa0179.stoa001.reduced:1.4")
  val cn = CitableNode(urn, sntx)

  "The Latin24Syntax object" should "split syntactic markup" in {
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    //println(Latin24Syntax.syntaxStrings(txt))
  }
  it should "handle > as a syntax token" in {
    val u = CtsUrn("urn:cts:latinLit:phi0881.phi003.latinlib:126")
    val txt = ">increpat \"o patrum suboles oblita priorum,"
    val cn = CitableNode(u,txt)
    //println("CONVERTED:\n" + Latin24Syntax.nodeToTokens(cn).map(t => t.text + " " + t.tokenCategory.get).mkString("\n"))
  }

  it should "try somehow to handle morphology" in  {
    val c = Corpus(Vector(cn))
    val fstFile = "src/test/resources/wk1-parsed.txt"
    val fst = Source.fromFile(fstFile).getLines.toVector
    val lc = LatinCorpus.fromFstLines(c,Latin24Syntax,fst,strict=false)
    val mf = MorphologyFilter(pos = Some("verb"))
    val phr = LatinPhrase(lc.tokens)
    val html = phr.hover(Vector(mf)).replaceAll("\\|", "\n")

  }


}
