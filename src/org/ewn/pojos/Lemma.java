package org.ewn.pojos;

/**
 * Lemma (normalized, lower-cased)
 *
 * @author Bernard Bou
 */
public class Lemma /* extends NormalizedString */ implements Comparable<Lemma>
{
	private final String entry;

	/**
	 * Constructor from normalized string
	 *
	 * @param normString normalized string
	 */
	protected Lemma(final NormalizedString normString)
	{
		// to lower case
		this.entry = normString.entry.toLowerCase();
	}

	// factory

	/**
	 * Make from bare normalized string
	 * 
	 * @param bareNormalized normalized bare normalized string
	 * @return lemma
	 */
	public static Lemma make(final BareNormalizedString bareNormalized)
	{
		return new Lemma(bareNormalized);
	}

	/**
	 * Make from normalized string
	 * 
	 * @param normalized normalized string
	 * @return lemma
	 */
	public static Lemma make(final NormalizedString normalized)
	{
		return new Lemma(normalized);
	}

	/**
	 * Make from rawString
	 * 
	 * @param rawString raw string
	 * @return lemma
	 */
	public static Lemma make(final String rawString)
	{
		// normalize spaces then lowercase
		return new Lemma(new NormalizedString(rawString));
	}

	@Override
	public int hashCode()
	{
		return this.entry.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NormalizedString))
			return false;
		final NormalizedString other = (NormalizedString) obj;
		if (this.entry == null)
			return other.entry == null;
		else
			return this.entry.equals(other.entry);
	}

	@Override
	public int compareTo(final Lemma other)
	{
		return this.entry.compareTo(other.entry);
	}

	@Override
	public String toString()
	{
		return this.entry;
	}
}
