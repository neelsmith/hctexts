import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

// invoke this script from root diretory of the repository.
val f = "socnet/hyginus-persons-3+-alpha.csv"
val catalogFile = "cex/catalog.cex"
val hyginusFile = "cex/hyginus.cex"

val catalogCex = Source.fromFile(catalogFile).getLines.mkString("\n")
val textCex = Source.fromFile(hyginusFile).getLines.mkString("\n")
val textRepo = TextRepositorySource.fromCexString(catalogCex + "\n" + textCex)

def namesList(fName: String = f, delimiter: String = ",") : Vector[String] = {
  val namesData = Source.fromFile(fName).getLines.toVector
  val namesList = for (item <- namesData) yield {
    val cols = item.split(delimiter)
    cols(0)
  }
  namesList.toVector
}
