package org.ewn.pojos;

/**
 * Pair of lemma with cased form
 *
 * @author Bernard Bou
 */
public class LemmaWithCase
{
	public final Lemma lemma;

	public final BareNormalizedString cased;

	public LemmaWithCase(final Lemma lemma, final BareNormalizedString cased)
	{
		this.lemma = lemma;
		this.cased = cased;
	}

	@Override
	public String toString()
	{
		return "lemma=" + this.lemma + " cased=" + this.cased;
	}
}
