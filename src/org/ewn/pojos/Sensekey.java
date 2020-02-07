package org.ewn.pojos;

/**
 * Sensekey
 *
 * @author Bernard Bou
 */
public class Sensekey
{
	private final String key;

	private final Lemma lemma;

	private final Pos pos;

	private final LexDomain lexDomain;

	private final int lexId;

	private Sensekey(final Lemma lemma, final Pos pos, final LexDomain lexDomain, final int lexId, final String key)
	{
		this.key = key.trim();
		this.lemma = lemma;
		this.pos = pos;
		this.lexDomain = lexDomain;
		this.lexId = lexId;
	}

	public static Sensekey parse(final String skString)
	{
		if (skString == null)
			return null;
		final String[] fields = skString.split("([%:])");
		if (fields.length < 4)
			throw new IllegalArgumentException(skString);
		try
		{
			final Lemma lemma = Lemma.make(fields[0]);
			final Pos pos = Pos.parse(Integer.parseInt(fields[1]));
			final LexDomain lexDomain = LexDomain.parse(fields[2]);
			final int lexid = Integer.parseInt(fields[3]);
			return new Sensekey(lemma, pos, lexDomain, lexid, skString);
		}
		catch (final Exception e)
		{
			throw new IllegalArgumentException(skString);
		}
	}

	public LexDomain getLexDomain()
	{
		return this.lexDomain;
	}

	public Lemma getLemma()
	{
		return this.lemma;
	}

	public int getLexId()
	{
		return this.lexId;
	}

	public Pos getPos()
	{
		return this.pos;
	}

	public String getKey()
	{
		return this.key;
	}

	@Override public String toString()
	{
		return this.key;
	}

	public String toXString()
	{
		return "word=" + this.lemma + " lexid=" + this.lexId + " lexdomain=" + this.lexDomain + " pos=" + this.pos;
	}
}