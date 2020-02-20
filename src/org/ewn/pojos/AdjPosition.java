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
	 * @param tag
	 *            position tag
	 * @param description
	 *            position description
	 */
	AdjPosition(final String tag, final String description)
	{
		this.tag = tag;
		this.description = description;
	}

	/**
	 * Find adj position from tag
	 *
	 * @param tag
	 *            tag
	 * @return adj position
	 */
	public static AdjPosition find(final String tag)
	{
		for (final AdjPosition position : AdjPosition.values())
			if (position.tag.equals(tag))
				return position;
		return null;
	}

	/**
	 * Parse adj position from line
	 *
	 * @param suffix
	 *            suffix = '(tag)'
	 * @return adj position
	 * @throws ParsePojoException
	 *             parse exception
	 */
	public static AdjPosition parseAdjPosition(final String suffix) throws ParsePojoException
	{
		// remove parentheses
		final String name = suffix.substring(1, suffix.length() - 1);

		// look up
		for (final AdjPosition adjPosition : AdjPosition.values())
		{
			if (name.equals(adjPosition.tag))
				return adjPosition;
		}
		throw new ParsePojoException("AdjPosition:" + name);
	}

	public String getTag()
	{
		return this.tag;
	}

	public String getDescription()
	{
		return this.description;
	}

	@Override
	public String toString()
	{
		return "(" + this.tag + "}";
	}
}