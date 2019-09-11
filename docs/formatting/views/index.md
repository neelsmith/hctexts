---
title: Generate views with morphological highlighting
layout: page
---


**TLDR;**

You can just load the script in `scripts/formatter.sc` and use its predefined functions.


### The full story

You need to load the libraries you see imported in `scripts/formatter.sc`.




Load both an OHCO2 corpus, and the output of an analysis of it by a `tabulae` parser.

```scala
val reducedSyntax = CorpusSource.fromFile("cex/livy-reduced.cex", cexHeader=true)
val fstFile = "src/test/resources/wk1-parsed.txt"
val fstLines = Source.fromFile(fstFile).getLines.toVector
```

Select a passage from the corpus, and make a `LatinPhrase` out of it.

```scala
  val urn = CtsUrn("urn:cts:omar:stoa0179.stoa001:1.4")
  val psgCorpus = reducedSyntax ~~ urn
  val latinCorpus = LatinCorpus.fromFstLines(psgCorpus,Latin24Syntax,fstLines,strict=false)
  val phr = LatinPhrase(latinCorpus.tokens)
```

Now you can use the `Latin24SyntaxString` object to write files:

```scala
  Latin24SyntaxString.printPosHighlight(phr,s"livy-${urn.passageComponent}-pos.md")
  Latin24SyntaxString.printPosHovers(phr,s"livy-${urn.passageComponent}-hover")
```

## What you get

These three files:

- syntactically formatted with [color coding of part of speech](output/livy-1.4-pos.md)
- syntactically formatted with [tooltips on substantives](output/livy-1.4-hover-nouns-pronouns) (nouns, pronouns, adjectives)
- syntactically formatted with [tooltips on verbs](output/livy-1.4-hover-verbs) (conjugated forms, infinitives, participles)
