package org.ewn.pojos;

import java.util.function.Function;

/**
 * Lemma Reference (nth word in synset)
 *
 * @author Bernard Bou
 */
public class LemmaRef
{
	/**
	 * SynsetId of the synset the lemma is member of
	 */
	private final SynsetId synsetId;

	/**
	 * 1-based number of the word in the lemma list
	 */
	private final int wordNum;

	/**
	 * Constructor
	 *
	 * @param synsetId synsetId of the synset the lemma is member of
	 * @param wordNum  1-based number of the word in the lemma list
	 */
	protected LemmaRef(final SynsetId synsetId, final int wordNum)
	{
		this.synsetId = synsetId;
		this.wordNum = wordNum;
	}

	@Override public String toString()
	{
		return this.synsetId + "[" + this.wordNum + "]";
	}

	/**
	 * Dereference / Resolve
	 *
	 * @param f functions that when applied to synsetId yields synset
	 * @return lemma referred to by reference
	 */
	public Lemma resolve(Function<SynsetId, Synset> f)
	{
		Synset synset = f.apply(this.synsetId);
		return synset.getLemmas()[this.wordNum - 1];
	}
}
