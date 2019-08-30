---
title: "Demo a tokenizable corpus"
layout: page
---





Start with a citable corpus, and add an orthographic system.

For these examples, we'll select a single section of text from a larger corpus, and make a tokenizable corpus from it.


```scala
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
val tcorpus = TokenizableCorpus(corpus, Latin23Alphabet)
```

This new corpus can be tokenized.

```scala
val tokens = tcorpus.tokens
```

How many?

```scala
scala> tokens.size
res0: Int = 33438
```

Peek at a few tokens:

```scala
scala> tokens(8)
res1: edu.holycross.shot.mid.validator.MidToken = MidToken(urn:cts:latinLit:stoa1263.stoa001.hc_tkns:pr.1.0,Ex,Some(LexicalToken))

scala> tokens(9)
res2: edu.holycross.shot.mid.validator.MidToken = MidToken(urn:cts:latinLit:stoa1263.stoa001.hc_tkns:pr.1.1,Caligine,Some(LexicalToken))

scala> tokens(10)
res3: edu.holycross.shot.mid.validator.MidToken = MidToken(urn:cts:latinLit:stoa1263.stoa001.hc_tkns:pr.1.2,Chaos,Some(LexicalToken))

scala> tokens(11)
res4: edu.holycross.shot.mid.validator.MidToken = MidToken(urn:cts:latinLit:stoa1263.stoa001.hc_tkns:pr.1.2_0,:,Some(PunctuationToken))
```

Notice that tokens have a URN with an additional tier added to the passage hierarchy.  This identifies the individual token within the canonical citation unit. Tokens also have a *type*:  here, a `PunctuationToken` is a different type of token from a `LexicalToken`.

Get just the lexical tokens:

```scala
tcorpus.lexicalTokens
```

How many?

```scala
scala> tcorpus.lexicalTokens.size
res6: Int = 28208
```

Let's see how they're distributed:

```scala
scala> tcorpus.lexHistogram
res7: edu.holycross.shot.histoutils.Histogram[String] = Histogram(Vector(Frequency(et,935), Frequency(in,707), Frequency(cum,621), Frequency(filius,402), Frequency(ex,391), Frequency(est,305), Frequency(ad,298), Frequency(qui,286), Frequency(se,276), Frequency(ut,219), Frequency(que,218), Frequency(quod,215), Frequency(filia,199), Frequency(ab,171), Frequency(eum,171), Frequency(a,153), Frequency(autem,136), Frequency(quae,133), Frequency(quem,120), Frequency(eius,116), Frequency(filium,110), Frequency(esse,102), Frequency(iouis,99), Frequency(ei,96), Frequency(eam,89), Frequency(non,88), Frequency(sunt,83), Frequency(id,83), Frequency(esset,81), Frequency(quam,77), Frequency(dicitur,75), Frequency(eo,74), Frequency(quo,73), Frequency(filiam,73), Frequency(inte...
```

or what the distribution is of our token categories:

```scala
scala> tcorpus.categoryHistogram
res8: edu.holycross.shot.histoutils.Histogram[edu.holycross.shot.mid.validator.MidTokenCategory] = Histogram(Vector(Frequency(LexicalToken,28208), Frequency(PunctuationToken,4669), Frequency(NumericToken,13)))
```
