package org.ewn.pojos;

/**
 * Sensekey
 *
 * @author Bernard Bou
 */
public class Sensekey
{
	private final String key;

	private final NormalizedString word; // may contain uppercase

	private final Pos pos;

	private final LexDomain lexDomain;

	private final int lexId;

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            other sensekey
	 */
	public Sensekey(final Sensekey other)
	{
		this.key = other.key;
		this.word = other.word;
		this.pos = other.pos;
		this.lexDomain = other.lexDomain;
		this.lexId = other.lexId;
	}

	/**
	 * Constructor
	 * 
	 * @param word
	 *            word (may contain uppercase)
	 * @param pos
	 *            part of speech
	 * @param lexDomain
	 *            lex domain
	 * @param lexId
	 *            lexid
	 * @param key
	 *            sensekey
	 */
	private Sensekey(final NormalizedString word, final Pos pos, final LexDomain lexDomain, final int lexId, final String key)
	{
		this.key = key.trim();
		this.word = word;
		this.pos = pos;
		this.lexDomain = lexDomain;
		this.lexId = lexId;
	}

	/**
	 * Parse sensekey from string
	 * 
	 * @param str
	 *            string
	 * @return sensekey
	 * @throws ParsePojoException
	 *             parse exception
	 */
	public static Sensekey parseSensekey(final String str) throws ParsePojoException
	{
		if (str == null)
			return null;
		final String[] fields = str.split("([%:])");
		if (fields.length < 4)
			throw new ParsePojoException("Sensekey:" + str);
		try
		{
			final NormalizedString word = new NormalizedString(fields[0].replace('#', ':'));
			final Pos pos = Pos.fromIndex(Integer.parseInt(fields[1]));
			final LexDomain lexDomain = LexDomain.parseLexDomain(fields[2]);
			final int lexid = Integer.parseInt(fields[3]);
			return new Sensekey(word, pos, lexDomain, lexid, str);
		}
		catch (Exception e)
		{
			throw new ParsePojoException("Sensekey:" + str);
		}
	}

	public LexDomain getLexDomain()
	{
		return this.lexDomain;
	}

	public NormalizedString getWord()
	{
		return this.word;
	}

	public Lemma getLemma()
	{
		return new Lemma(this.word);
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

	@Override
	public String toString()
	{
		return this.key;
	}

	public String toXString()
	{
		return "word=" + this.word + " lexid=" + this.lexId + " lexdomain=" + this.lexDomain + " pos=" + this.pos;
	}
}