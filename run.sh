#!/bin/bash

IN=/opt/data/nlp/wordnet/WordNet-2019/git_forked/english-wordnet/merged/english-wordnet-2019.xml
OUTDIR=out

java -jar ewn-grind.jar "${IN}" "${OUTDIR}" 

