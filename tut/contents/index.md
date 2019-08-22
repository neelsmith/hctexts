---
title: Contents of this repository
layout: page
---



## Organization of files

- `cex`: citable editions of texts in CEX format.
- `morphology-latin`: each subdirectory has tabular data in delimited-text files for building an orthography-specific morphological parser with `tabulae`:
    - `lat23` "23-character" orthography lacking consonantal "j" or "v".
    - `lat24` "24-character" orthography with consonantal "v" but not "j".
    - `lat25` "25-character" orthography with consonantal "v" and "j".
    - `shared` Data that can be merged with any of `lat23`, `lat24` or `lat25` data sets.
-  `scripts`: Scala scripts for building morphological parsers and analyzing corpora morphologically
-  `parsers`: directory where binary SFST parsers are compiled.


Other:

- `xml-editions`:  cataloged XML editions of various texts.  This source material can be used to generate more straightforward, equivalent CEX editions.
