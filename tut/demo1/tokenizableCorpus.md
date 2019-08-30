---
title: "Demo a tokenizable corpus"
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

Start with a citable corpus, and add an orthographic system.

For these examples, we'll select a single section of text from a larger corpus, and make a tokenizable corpus from it.


```tut:silent
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
val tcorpus = TokenizableCorpus(corpus, Latin23Alphabet)
```

This new corpus can be tokenized.

```tut:silent
val tokens = tcorpus.tokens
```

How many?

```tut
tokens.size
```

Peek at a few tokens:

```tut
tokens(8)
tokens(9)
tokens(10)
tokens(11)
```

Notice that tokens have a URN with an additional tier added to the passage hierarchy.  This identifies the individual token within the canonical citation unit. Tokens also have a *type*:  here, a `PunctuationToken` is a different type of token from a `LexicalToken`.

Get just the lexical tokens:

```tut:silent
tcorpus.lexicalTokens
```

How many?

```tut
tcorpus.lexicalTokens.size
```

Let's see how they're distributed:

```tut
tcorpus.lexHistogram
```

or what the distribution is of our token categories:

```tut
tcorpus.categoryHistogram
```
