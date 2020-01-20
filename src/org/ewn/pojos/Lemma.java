package org.ewn.pojos;

/**
 * Lemma
 * 
 * @author Bernard Bou
 */
public class Lemma extends NormalizedString
{
	protected Lemma(final NormalizedString normString)
	{
		this.normalized = normString.toString();

		// to lower case
		this.normalized = this.normalized.toLowerCase();
	}

	// factory
	
	public static Lemma make(final NormalizedString normString, final boolean isAdj)
	{
		return isAdj ? new AdjLemma(normString) : new Lemma(normString);
	}

	public static Lemma make(final String rawString, final boolean isAdj)
	{
		return Lemma.make(new NormalizedString(rawString), isAdj);
	}

	public static Lemma make(final String rawString)
	{
		return Lemma.make(new NormalizedString(rawString), false);
	}
}
