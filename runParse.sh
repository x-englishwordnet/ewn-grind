#!/bin/bash

#INDIR=/opt/data/nlp/wordnet/WordNet-2.0/dict
#INDIR=/opt/data/nlp/wordnet/WordNet-2.1/dict
#INDIR=/opt/data/nlp/wordnet/WordNet-3.0/dict
#INDIR=/opt/data/nlp/wordnet/WordNet-3.1/dict
INDIR=out

java -cp ewn-grind.jar org.ewn.parse.Parser "${INDIR}" 

