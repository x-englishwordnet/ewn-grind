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

	public static Pos parse(final char id) throws ParsePojoException
	{
		for (final Pos pos : Pos.values())
		{
			if (id == pos.id)
				return pos;
		}
		throw new ParsePojoException("Pos:" + id);
	}

	public static Pos parse(final String name) throws ParsePojoException
	{
		for (final Pos pos : Pos.values())
		{
			if (name.equals(pos.name))
				return pos;
		}
		throw new ParsePojoException("Pos:" + name);
	}

	public static Pos parse(final int index0)
	{
		final int index = index0 - 1;
		if (index >= 0 && index < Pos.values().length)
			return Pos.values()[index];
		throw new IllegalArgumentException("Pos:" + index);
	}

	public static Pos toPos(final String name)
	{
		for (final Pos pos : Pos.values())
		{
			if (name.equals(pos.name))
				return pos;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return Character.toString(this.id);
	}
}
