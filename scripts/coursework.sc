import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._
import edu.holycross.shot.greek._

import edu.holycross.shot.mid.validator._


// Load latc in morph+ls.sc

val ambigs = latc.singleLexeme.filter(_.analyses.size > 1).map(t => t.analyses.map(_.formLabel))
val frs = ambigs.groupBy(v => v).map{ case (v,v2) => Frequency(v,v2.size) }
val ambigHist = Histogram(frs.toVector)

Or this for ambigs
latc.singleLexeme.map(t => t.analyses.filterNot(_.posLabel.contains("vocative"))).filter(_.size > 1


 multis = latc.singleLexeme.filter(_.analyses.size > 1)

multis.flatMap(m => m.analyses.map(_.formLabel))


val labs = multis.map(m => m.analyses.map(_.formLabel).sorted.mkString(", OR "))

val noVoc =labs.filterNot(_.contains("vocative"))
