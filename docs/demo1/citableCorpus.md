---
title: "A citable corpus in Scala"
layout: page
---





Create a corpus from a CEX file:

```scala
val corpus = CorpusSource.fromFile(s"cex/livy-mt.cex", cexHeader = true)
```

How many citable passages?

```scala
scala> corpus.size
res0: Int = 542
```

The "twiddle" operator: create a new corpus containing only nodes with a matching URN:

```scala
val chapterUrn = CtsUrn("urn:cts:omar:stoa0179.stoa001:1.4")
val chapter = corpus ~~ chapterUrn
```

How big is the new corpus?

```scala
scala> chapter.size
res1: Int = 9
```

You can always work directly with the sequence of citable nodes.  Peek at the first two nodes (indexing starts with 0):

```scala
scala> chapter.nodes(0)
res2: edu.holycross.shot.ohco2.CitableNode = CitableNode(urn:cts:omar:stoa0179.stoa001.omar:1.4.1,sed debebatur, ut opinor, fatis tantae origo urbis maximi+que secundum deorum opes imperii principium. vi compressa Vestalis cum geminum partum edidisset,)

scala> chapter.nodes(1)
res3: edu.holycross.shot.ohco2.CitableNode = CitableNode(urn:cts:omar:stoa0179.stoa001.omar:1.4.2,seu ita rata, seu quia deus auctor culpae honestior erat, Martem incertae stirpis patrem nuncupat.)
```

Map each node to its text content (ignoring its URN):

```scala
val textOnly = chapter.nodes.map(_.text)
```

and then make a single string out of the resulting list:

```scala
scala> textOnly.mkString("\n\n")
res4: String =
sed debebatur, ut opinor, fatis tantae origo urbis maximi+que secundum deorum opes imperii principium. vi compressa Vestalis cum geminum partum edidisset,

seu ita rata, seu quia deus auctor culpae honestior erat, Martem incertae stirpis patrem nuncupat.

sed nec dii nec homines aut ipsam aut stirpem a crudelitate regia vindicant;

sacerdos vincta in custodiam datur; pueros in profluentem aquam mitti iubet. forte quadam divinitus super ripas Tiberis effusus lenibus stagnis nec adiri usquam ad iusti cursum poterat amnis et posse quamvis languida mergi aqua infantes spem ferentibus dabat.

ita, velut defuncti regis imperio, in proxima eluvie, ubi nunc ficus Ruminalis est — Romularem vocatam ferunt—, pueros exponunt. vastae tum in his locis solitudi...
```
