---
title: "Demo a citable corpus"
layout: page
---





Create a corpus from a CEX file:

```scala
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
```

How many citable passages?

```scala
scala> corpus.size
res0: Int = 1234
```

The "twiddle" operator: create a new corpus containing only nodes with a matching URN:

```scala
val section1urn = CtsUrn("urn:cts:latinLit:stoa1263.stoa001.hc:1pr")
val section1corpus = corpus ~~ section1urn
```

How big is the new corpus?

```scala
scala> section1corpus.size
res1: Int = 3
```

You can always work directly with the sequence of citable nodes.  Peek at the first two nodes (indexing starts with 0):

```scala
scala> section1corpus.nodes(0)
res2: edu.holycross.shot.ohco2.CitableNode = CitableNode(urn:cts:latinLit:stoa1263.stoa001.hc:1pr.title,THEMISTO.)

scala> section1corpus.nodes(1)
res3: edu.holycross.shot.ohco2.CitableNode = CitableNode(urn:cts:latinLit:stoa1263.stoa001.hc:1pr.1,Athamas Aeoli filius habuit ex Nebula uxore filium Phrixum et filiam Hellen, et ex Themisto Hypsei filia filios duos, Sphincium et Orchomenum, et ex Ino Cadmi filia filios duos, Learchum et Melicerten.)
```

Map each node to its text content (ignoring its URN):

```scala
val textOnly = section1corpus.nodes.map(_.text)
```

and then make a single string out of the resulting list:

```scala
scala> textOnly.mkString("\n\n")
res4: String =
THEMISTO.

Athamas Aeoli filius habuit ex Nebula uxore filium Phrixum et filiam Hellen, et ex Themisto Hypsei filia filios duos, Sphincium et Orchomenum, et ex Ino Cadmi filia filios duos, Learchum et Melicerten.

Themisto, quod se Ino coniugio priuasset, filios eius interficere uoluit; itaque in regia latuit clam et occasione nacta, cum putaret se inimicae natos interfecisse, suos imprudens occidit, a nutrice decepta quod eis uestem perperam iniecerat. Themisto cognita re ipsa se interfecit.
```
