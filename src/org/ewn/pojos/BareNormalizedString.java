package org.ewn.pojos;

/**
 * Normalized string with possible suffix (adj position) stripped
 *
 * @author Bernard Bou
 */
public class BareNormalizedString extends NormalizedString
{
	/**
	 * Constructor
	 *
	 * @param normalized string with possible suffix
	 */
	public BareNormalizedString(final NormalizedString normalized)
	{
		super(normalized);

		// remove possible trailing adj position between parentheses
		this.entry = strip(this.entry);
	}

	/**
	 * Constructor
	 *
	 * @param raw string with possible suffix
	 */
	public BareNormalizedString(final String raw)
	{
		super(raw);

		// remove possible trailing adj position between parentheses
		this.entry = strip(this.entry);
	}

	public static String strip(String str)
	{
		return str.replaceAll("\\(.*\\)", "");
	}
}
