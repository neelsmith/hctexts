import edu.holycross.shot.tabulae.builder._
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._
import edu.holycross.shot.greek._

import edu.holycross.shot.mid.validator._

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import java.io.PrintWriter
import sys.process._
import scala.language.postfixOps

import scala.io._


def latin(label: String, orthography: MidOrthography )= {
  val corpus = CorpusSource.fromFile(s"cex/${label}.cex", cexHeader=true)
  val fstLines = Source.fromFile(s"workfiles/${label}-parsed.txt").getLines.toVector
  LatinCorpus.fromFstLines(corpus,orthography, fstLines, strict=false)
}

def tokenPosMap(latin: LatinCorpus) : Map[String, String] = {
  latin.analyzed.map(t => (t.text, t.analyses.map(_.posLabel).distinct.mkString(", "))).toMap
}

def tokenPos(tkn: String, posMap: Map[String, String]) : String = {
  try {
    posMap(tkn)
  } catch {
    case t: Throwable => "not yet analyzed"
  }
}

def lexemePos(latin: LatinCorpus): Map[String, String] = {
  latin.analyzed.map(t =>  t.analyses.map(a => (LewisShort.label(a.lemmaId), a.posLabel  ))).flatten.distinct.toMap
}

def lexPosHistogram(label: String, orthography: MidOrthography): Histogram[String] = {
  val latcorp = latin(label, orthography)
  val posMap = lexemePos(latcorp)
  val freqs: Vector[Frequency[String]] = latcorp.labelledLexemeHistogram.frequencies.map(freq => Frequency(freq.item + ", " + posMap(freq.item), freq.count))
  Histogram(freqs)
}

def tokenPosHistogram(label: String, orthography: MidOrthography): Histogram[String] = {
  val latcorp = latin(label, orthography)
  val posMap = tokenPosMap(latcorp)

  val freqs: Vector[Frequency[String]] = latcorp.lexTokenHistogram.frequencies.map(freq => Frequency(freq.item + ", " + tokenPos(freq.item, posMap), freq.count))
  Histogram(freqs)
}
