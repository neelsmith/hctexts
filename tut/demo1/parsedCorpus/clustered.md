---
title: "Clustered"
layout: page
---


```tut:invisible
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

import edu.holycross.shot.histoutils._

import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._


import edu.holycross.shot.mid.validator._


val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
val parserOutput = "workfiles/hyginus-parsed.txt"

import scala.io.Source
val fst = Source.fromFile(parserOutput).getLines.toVector

val latinCorpus = LatinCorpus.fromFstLines(
    corpus,
    Latin23Alphabet,
    fst,
    strict = false
  )
```

Group tokens into citable units:

```tut:silent
val clusters = latinCorpus.clusterByCitation
```

Create a filter to select citable sequences:

```tut:silent
val morphFilter = MorphologyCollectionsFilter(clusters)
```

Now find citable sequences in which all substantives appear in a case given in a limiting list:

```tut:silent
val caseList = Vector(Nominative, Genitive)
val nomgenOnly = morphFilter.limitSubstantiveCase(caseList)
```

The result is a Vector.  How many?

```tut
nomgenOnly.size
```

We can turn those results into object called `LatinCitaleUnit`s.


```tut
val citableUnits = nomgenOnly.map(tkn => tkn match {
  case citable: LatinCitableUnit => citable
})
```

From these, you can create normal citable nodes.

```tut
val canonical = citableUnits.map(_.canonicalNode)
```

So if you just wanted to see the text of those canonically citable nodes, you can do that easily. Let's take the first 5 passges that matched our criteria:

```tut
canonical.take(5).map(_.text).mkString("\n\n")
```
