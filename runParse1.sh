#!/bin/bash

INDIR20=/opt/data/nlp/wordnet/WordNet-2.0/dict
INDIR21=/opt/data/nlp/wordnet/WordNet-2.1/dict
INDIR30=/opt/data/nlp/wordnet/WordNet-3.0/dict
INDIR31=/opt/data/nlp/wordnet/WordNet-3.1/dict
INDIRXX=out

# noun|verb|adj|adv
POS=$1

# offset (ie 1740)
OFS=$2

# version
VER=$3
case "$VER" in
	20) INDIR="${INDIR20}" ;;
	21) INDIR="${INDIR21}" ;;
	30) INDIR="${INDIR30}" ;;
	31) INDIR="${INDIR31}" ;;
	xx) INDIR="${INDIRXX}" ;;
esac

echo "${INDIR}"
java -cp ewn-grind.jar org.ewn.parse.LineParser "${INDIR}" ${POS} ${OFS} 

