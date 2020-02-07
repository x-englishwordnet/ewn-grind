package org.ewn.pojos;

/**
 * Normalized string
 *
 * @author Bernard Bou
 */
public class NormalizedString
{
	protected String normalized;

	public NormalizedString(final String rawStr)
	{
		this.normalized = NormalizedString.normalize(rawStr);
	}

	protected NormalizedString(final NormalizedString other)
	{
		this.normalized = other.normalized;
	}

	protected NormalizedString()
	{
		this.normalized = null;
	}

	@Override public String toString()
	{
		return this.normalized;
	}

	private static String normalize(final String rawStr)
	{
		// convert underscore to space
		String result = rawStr.replace('_', ' ');

		// double single quote to single quote
		result = result.replace("''", "'");
		return result;
	}
}
