---
title: "Demo a parsed corpus"
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
```


### Preparatory work

We've already parsed a word list for our corpus and saved the output to a file (as in [this example](../))

```tut:silent
import scala.io.Source
val fst = Source.fromFile(parserOutput).getLines.toVector
```


### Create the parsed corpus

```tut:silent
val latinCorpus = LatinCorpus.fromFstLines(
    corpus,
    Latin23Alphabet,
    fst,
    strict = false
  )
```
