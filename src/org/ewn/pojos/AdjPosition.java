package org.ewn.pojos;

/**
 * Adjective Position
 *
 * @author Bernard Bou
 */
public enum AdjPosition
{
	PREDICATIVE("p", "predicate"), //
	ATTRIBUTIVE("a", "attributive"), //
	POSTNOMINAL("ip", "immediately postnominal");

	private final String tag;

	private final String description;

	/**
	 * Constructor
	 *
	 * @param tag         position tag
	 * @param description position description
	 */
	AdjPosition(final String tag, final String description)
	{
		this.tag = tag;
		this.description = description;
	}

	public static AdjPosition parse(final String suffix)
	{
		// remove parentheses
		final String name = suffix.substring(1, suffix.length() - 1);

		// look up
		for (final AdjPosition adjPosition : AdjPosition.values())
		{
			if (name.equals(adjPosition.tag))
				return adjPosition;
		}
		throw new IllegalArgumentException(name);
	}

	public String getTag()
	{
		return this.tag;
	}

	public String getDescription()
	{
		return this.description;
	}

	@Override public String toString()
	{
		return "(" + this.tag + "}";
	}
}