# `hctexts`


This repository hosts a selection of Greek and Latin texts used in teaching at Holy Cross, along with related analytical material, including morphological databases.  The material in this repository is the basis for a pilot program in 2019-2020 restructuring the organization and sequence of our Latin curriculum based on a computational analysis of a target corpus students are reading.


## What's here

Digital texts are in the [cex](./cex) directory, in the simple delimited-text [CEX format](https://cite-architecture.github.io/citedx/CEX-spec-3.0.1/), using CTS URNs to identify each citable node.  As indicated in the associated catalogs, these texts derive from openly licensed material available from projects such as [Tesserae](http://tesserae.caset.buffalo.edu/) and [the Latin Library](https://www.thelatinlibrary.com/).

The [lewis-short](/lewis-short) directory mirrors the markdown version of Lewis-Short's lexicon distributed under a CC 3.0 BY-NC-SA license at <https://github.com/Eumaeus/cex_lewis_and_short>.  Entries in the lexicon are identified by CTS URN:  where possible, there are coordinated with identifiers for lexemes in our morphological lexica.

The [morphology-latin](./morphology-latin) directory includes morphological datasets for building parsers with [tabulae](https://github.com/neelsmith/tabulae).  The datasets are organized to minimize effort in developing parsers following different orthographic practices (such as differing use of *i*, *j*, *u* and *v*) by isolating common data in [morphology-latin/shared](./morphology-latin/shared), and factoring out material specific to different orthographies as follows:


- [morphology-latin/shared](.morphology-latin/shared): data that can be used in common with `lat23`, `lat24` and `lat25` data sets because it includes no *i/j* or *u/v* characters.  Lexemes are identified with URNs mapped to the URNs in Lewis and Short.
- [morphology-latin/shared-xls](.morphology-latin/shared-xls): data that can be used in common with `lat23`, `lat24` and `lat25` data sets because it includes no *i/j* or *u/v* characters.  Lexemes do not have a corresponding URN in Lewis and Short.
- [morphology-latin/lat23](./morphology-latin/lat23): orthography with 23 alphabetic characters (*i* and *u* both vocalic and semivocalic)
- [morphology-latin/lat24](./morphology-latin/lat24): orthography with 24 alphabetic characters (*i* both vocalic and semivocalic, distinct *u* and *v*)
- [morphology-latin/lat25](./morphology-latin/lat25): orthography with 25 alphabetic characters (*i*, *j*, *u* and *v* all distinct)


### Utilities

The [scripts](./scripts) directory includes some useful scripts for building morphological parsers and analyzing texts.  Filenames with an `.sc` extension are Scala scripts that can be loaded in an sbt console  (with `:load FILENAME` in an sbt console). Filenames with an `.amm` extension are Scala scripts that can be run in an [ammonite REPL](https://ammonite.io/), or with the [Atom editor](https://atom.io/) with the [Hydrogen plugin](https://atom.io/packages/hydrogen).

- `compiler.sc` Defines a single function, `compile`, that takes a Vector of directory names for morphological data sets to include. E.g., `compile(Vector("shared", "shared-xls", "lat23"))` compiles a morphological data from the two shared data sets (one with lexemes identified by Lewis-Short URN, and one identified by URNs not appearing in Lewis-Short) together with data in an orthography using 23 alphabetic characters.


## Technological infrastructure

Prerequisites:

- the [Stuttgart FST toolkit](https://www.cis.uni-muenchen.de/~schmid/tools/SFST/)
- a JVM and [sbt](https://www.scala-sbt.org/)


Automatically included code libraries we rely on:

-  [tabulae](https://github.com/neelsmith/tabulae):  we use `tabulae` to compile a binary morphological parser with SFST, parse automatically generated wordlists, and analyze the parser's output for individual tokens.
- [latin-corpus](https://github.com/neelsmith/tabulae):  `latin-corpus` `latin-corpus` offers higher-level manipulation of a corpus in terms of its morphology by associating a citable corpus with the output from a parser built with `tabulae`. It can profile usage of forms or vocabulary in a corpus, and filter the corpus to include or exclude passages containing a specified set of features or vocabulary.


## Current work and future plans

In 2019-2020, we are using material from this repository in two course sequences.  Students in first-year Latin are reading unaltered material from Hyginus' *Fabulae*; students in intermediate Latin are reading targeted selections from the first five books of Livy's *History*.  We will be evaluating the results of this pilot in the spring of 2020, and are preparing an application for a three-year project to extend the pilot program.

## More information


For latin-corpus: <https://github.com/neelsmith/latin-corpus>



For tabulae:  <https://github.com/neelsmith/tabulae>

Recent and upcoming conference presentations:

- Daniel Libatique, Dominic Machado, Neel Smith, "*Caveat magister*: a computational approach to designing a Latin curriculum" (Digital Humanities 2020, Ottawa).
- Dominic Machado and Daniel Libatique, "*Lector Intende, Laetaberis*: A Research-Based Approach to Introductory Latin," Classical Association of New England Annual Meeting 2020.
- Neel Smith, “Digital Approaches to Recognizing Latinities (pl.)”, Classical Association of New England Annual Meeting 2020.
- Richard Ciolek, James Garry, Michael Rehab, Neel Smith, Zachary Sowerby, “Corpus-specific morphological analysis for research and teaching” conference “Digital Approaches to Teaching Historical Languages” (Humboldt University, Berlin), March, 2019.
