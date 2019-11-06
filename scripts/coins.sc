import edu.holycross.shot.tabulae._
import edu.holycross.shot.ocho2._
import edu.holycross.shot.cite._

val fstFile = "coins-fst.txt"
val analyzed = FstFileReader.fromFile(fstFile)


//1. get concordance from corpus
//2. map tokens to analysis
//3. filter on analysis
