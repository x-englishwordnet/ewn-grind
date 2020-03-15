package org.ewn.pojos;

/**
 * Pos (part of speech)
 *
 * @author Bernard Bou
 */
public enum Pos
{
	NOUN('n', "noun", "noun"), //
	VERB('v', "verb", "verb"), //
	ADJ('a', "adj", "adjective"), //
	ADV('r', "adv", "adverb"), //
	ADJSAT('s', "adj", "adjective satellite");

	private final char id;

	private final String name;

	private final String description;

	Pos(final char id, final String name, final String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public char toChar()
	{
		return this.id;
	}

	public String toTag()
	{
		return String.valueOf(this.id);
	}

	public String getName()
	{
		return this.name;
	}

	public String getDescription()
	{
		return this.description;
	}

	/**
	 * Parse pos from character id
	 * 
	 * @param id
	 *            character id
	 * @return pos
	 * @throws ParsePojoException
	 *             parse exception
	 */
	public static Pos parsePos(final char id) throws ParsePojoException
	{
		for (final Pos pos : Pos.values())
		{
			if (id == pos.id)
				return pos;
		}
		throw new ParsePojoException("Pos:" + id);
	}

	/**
	 * Parse pos from name
	 * 
	 * @param name
	 *            name
	 * @return pos
	 * @throws ParsePojoException
	 *             parse exception
	 */
	public static Pos parsePos(final String name) throws ParsePojoException
	{
		for (final Pos pos : Pos.values())
		{
			if (name.equals(pos.name))
				return pos;
		}
		throw new ParsePojoException("Pos:" + name);
	}

	/**
	 * Make pos from pos index
	 * 
	 * @param index0
	 *            index
	 * @return pos
	 */
	public static Pos fromIndex(final int index0)
	{
		final int index = index0 - 1;
		if (index >= 0 && index < Pos.values().length)
			return Pos.values()[index];
		throw new IllegalArgumentException("Pos:" + index);
	}

	/**
	 * Make pos from name
	 * 
	 * @param name
	 *            name
	 * @return pos
	 */
	public static Pos fromName(final String name)
	{
		for (final Pos pos : Pos.values())
		{
			if (name.equals(pos.name))
				return pos;
		}
		return null;
	}

	public boolean isAdj()
	{
		return this.equals(Pos.ADJ) || this.equals(Pos.ADJSAT);
	}

	@Override
	public String toString()
	{
		return Character.toString(this.id);
	}
}
