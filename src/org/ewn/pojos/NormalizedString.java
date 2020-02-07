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
		this.entry = NormalizedString.normalize(rawStr);
	}

	protected NormalizedString(final NormalizedString other)
	{
		this.entry = other.entry;
	}

	protected NormalizedString()
	{
		this.entry = null;
	}

	private static String normalize(final String rawStr)
	{
		// convert underscore to space
		String result = rawStr.replace('_', ' ');

		// double single quote to single quote
		result = result.replace("''", "'");
		return result;
	}

	public String getNormalized()
	{
		return entry;
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NormalizedString))
			return false;
		NormalizedString other = (NormalizedString) obj;
		if (this.entry == null)
		{
			return other.entry == null;
		}
		else return entry.equals(other.entry);
	}

	@Override
	public int compareTo(NormalizedString other)
	{
		return this.entry.compareTo(other.entry);
	}
}
