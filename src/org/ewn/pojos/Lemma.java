package org.ewn.pojos;

import java.io.Serializable;

/**
 * Lemma (normalized, lower-cased)
 *
 * @author Bernard Bou
 */
public class Lemma /* extends NormalizedString */ implements Comparable<Lemma>, Serializable
{
	private static final long serialVersionUID = 1L;

	private final String entry;

	/**
	 * Constructor from normalized string
	 *
	 * @param normString
	 *            normalized string
	 */
	protected Lemma(final NormalizedString normString)
	{
		// to lower case
		this.entry = normString.entry.toLowerCase();
		//if (this.entry.matches(".*\\(.*\\)"))
		//	throw new RuntimeException(this.entry);
	}

	// factory

	/**
	 * Make from bare normalized string
	 *
	 * @param bareNormalized
	 *            normalized bare normalized string
	 * @return lemma
	 */
	public static Lemma make(final BareNormalizedString bareNormalized)
	{
		return new Lemma(bareNormalized);
	}

	/**
	 * Make from normalized string
	 *
	 * @param normalized
	 *            normalized string
	 * @return lemma
	 */
	public static Lemma make(final NormalizedString normalized)
	{
		return new Lemma(normalized);
	}

	/**
	 * Make from rawString
	 *
	 * @param rawString
	 *            raw string
	 * @return lemma
	 */
	public static Lemma make(final String rawString)
	{
		// normalize spaces then lowercase
		return new Lemma(new NormalizedString(rawString));
	}

	// I D E N T I T Y

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

	// O R D E R I N G

	@Override
	public int compareTo(final Lemma other)
	{
		return this.entry.compareTo(other.entry);
	}

	// T O S T R I N G

	@Override
	public String toString()
	{
		return this.entry;
	}
}
