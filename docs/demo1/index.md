---
title: "Demo a LatinCorpus"
layout: page
---





We can work with a hierarchy of three kinds of corpora.  Each kind of corpus adds a higher level of analysis.

## A citable corpus

The `Corpus` is just a sequence of citable nodes of text.  You can build this from a CEX file:

```scala
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
```

See [a few things you can do with a citable corpus](citableCorpus/)


## A tokenizable corpus

A tokenizable corpus adds to a citable corpus the knowledge of a specified orthographic system.

```scala
val tcorpus = TokenizableCorpus(corpus, Latin23Alphabet)
```

See [a few things you can do with a tokenizable corpus](tokenizableCorpus/)


## A morphologically parsed corpus

Prerequisites: build a parser, and parse your corpus.
