---
title: "A citable corpus in Scala"
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
```

Create a corpus from a CEX file:

```tut:silent
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
```

How many citable passages?

```tut
corpus.size
```

The "twiddle" operator: create a new corpus containing only nodes with a matching URN:

```tut:silent
val section1urn = CtsUrn("urn:cts:latinLit:stoa1263.stoa001.hc:1pr")
val section1corpus = corpus ~~ section1urn
```

How big is the new corpus?

```tut
section1corpus.size
```

You can always work directly with the sequence of citable nodes.  Peek at the first two nodes (indexing starts with 0):

```tut
section1corpus.nodes(0)
section1corpus.nodes(1)
```

Map each node to its text content (ignoring its URN):

```tut:silent
val textOnly = section1corpus.nodes.map(_.text)
```

and then make a single string out of the resulting list:

```tut
textOnly.mkString("\n\n")
```
