---
title: "Three kinds of text corpora"
layout: page
---

## Playing along at home

If you want to try out some of these code snippets, you can :

1.  Open an sbt console in the root directory of this repository:  `sbt console`
2. Import a ton of useful libraries.  Copy and paste these lines into your console:

```tut:silent
import edu.holycross.shot.tabulae._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.histoutils._
import edu.holycross.shot.latin._
import edu.holycross.shot.latincorpus._
import edu.holycross.shot.mid.validator._
```


## Text corpora

We can work with a hierarchy of three kinds of corpora.  Each corpus adds a successively higher level of analysis.

### A citable corpus

The `Corpus` is just a sequence of citable nodes of text.  You can build this from a CEX file:

```tut:silent
val corpus = CorpusSource.fromFile(s"cex/hyginus.cex", cexHeader = true)
```

See [a few things you can do with a citable corpus](citableCorpus/)


### A tokenizable corpus

A tokenizable corpus adds to a citable corpus the knowledge of a specified orthographic system.

```tut:silent
val tcorpus = TokenizableCorpus(corpus, Latin23Alphabet)
```

See [a few things you can do with a tokenizable corpus](tokenizableCorpus/)


### A morphologically parsed Latin corpus

A morphologically parsed Latin corpus applies morphological analysis to  the lexical tokens in a tokenizable corpus.

Here's one approach to create a parsed corpus.

#### Preparatory work

1. Compile a parser with `tabulae`
2.  Find a word list from your tokenizable corpus (that's just `tokenizableCorpus.wordList`)
3.  Parse the words in the wordlist, and save the results in a file.

For this tutorial, we've done that and saved the parsing output in this file:

```tut:silent
val parserOutput = "workfiles/hyginus-parsed.txt"
```


#### Creating a parsed Latin corpus

We'll read the parser output from the file into a Vector of Strings:

```tut:silent
import scala.io.Source
val fst = Source.fromFile(parserOutput).getLines.toVector
```

and then create a morphologically parsed corpus:

```tut:silent
val latinCorpus = LatinCorpus.fromFstLines(
    corpus,
    Latin23Alphabet,
    fst,
    strict = false
  )
```

See [a few things you can do with a morphologically parsed corpus](parsedCorpus/)
