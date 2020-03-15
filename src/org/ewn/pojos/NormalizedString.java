package org.ewn.pojos;

import java.io.Serializable;

/**
 * Normalized string
 *
 * @author Bernard Bou
 */
public class NormalizedString implements Comparable<NormalizedString>, Serializable
{
	private static final long serialVersionUID = 1L;

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
	public int compareTo(final NormalizedString other)
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
