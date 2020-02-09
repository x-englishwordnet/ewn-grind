package org.ewn.pojos;

/**
 * Normalized string
 *
 * @author Bernard Bou
 */
public class NormalizedString implements Comparable<NormalizedString>
{
	protected String entry;

	public NormalizedString(final String rawStr)
	{
		this.entry = normalize(rawStr);
	}

	protected NormalizedString(final NormalizedString other)
	{
		this.entry = other.entry;
	}

	public static String normalize(final String rawStr)
	{
		// convert underscore to space
		String result = rawStr.replace('_', ' ');

		// double single quote to single quote
		result = result.replace("''", "'");
		return result;
	}

	public String getNormalized()
	{
		return this.entry;
	}

	@Override
	public String toString()
	{
		return this.entry;
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
	public int compareTo(final NormalizedString other)
	{
		return this.entry.compareTo(other.entry);
	}
}
