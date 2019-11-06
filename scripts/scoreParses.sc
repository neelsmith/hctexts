

// EXAMPLE SET UP
/*
val ohist = ovid.lextHistogram

*/

def countsForKeys(total : Int = 0, hist: Histogram[String], keyList: Vector[String]) : Int = {
  if (keyList.isEmpty) {
    total
  } else {
    val matches = hist.frequencies.filter(_.item == keyList.head)

    val newTotal = total + matches(0).count
    println("After " + matches(0) + ", total now " + newTotal)
    countsForKeys(newTotal, hist, keyList.tail)
  }
}


def score(hist: Histogram[String], fst: String) = {
  val parses = FstReader.parseFstString(fst)
  val formsParsed = parses.filter(_.analyses.nonEmpty).size
  println(s"${parses.size} forms examined. ${formsParses} parsed successfully.")



}
