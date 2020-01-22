#!/bin/bash

# input XML file
IN=/opt/data/nlp/wordnet/WordNet-2019/git_forked/english-wordnet/merged/english-wordnet-2019.xml

# n|v|a|r
POS=$1

# offset (ie 1740)
OFS=$2

java -cp ewn-grind.jar org.ewn.grind.Grinder1 "${IN}" ${POS} ${OFS} 

