package org.ewn.pojos;

/**
 * Lex Domain
 *
 * @author Bernard Bou
 */
public class LexDomain
{
	private static final LexDomain[] LEX_DOMAINS = { //
			new LexDomain(0, "adj.all", Pos.ADJ), //
			new LexDomain(1, "adj.pert", Pos.ADJ), //
			new LexDomain(2, "adv.all", Pos.ADV), //
			new LexDomain(3, "noun.tops", Pos.NOUN), //
			new LexDomain(4, "noun.act", Pos.NOUN), //
			new LexDomain(5, "noun.animal", Pos.NOUN), //
			new LexDomain(6, "noun.artifact", Pos.NOUN), //
			new LexDomain(7, "noun.attribute", Pos.NOUN), //
			new LexDomain(8, "noun.body", Pos.NOUN), //
			new LexDomain(9, "noun.cognition", Pos.NOUN), //
			new LexDomain(10, "noun.communication", Pos.NOUN), //
			new LexDomain(11, "noun.event", Pos.NOUN), //
			new LexDomain(12, "noun.feeling", Pos.NOUN), //
			new LexDomain(13, "noun.food", Pos.NOUN), //
			new LexDomain(14, "noun.group", Pos.NOUN), //
			new LexDomain(15, "noun.location", Pos.NOUN), //
			new LexDomain(16, "noun.motive", Pos.NOUN), //
			new LexDomain(17, "noun.object", Pos.NOUN), //
			new LexDomain(18, "noun.person", Pos.NOUN), //
			new LexDomain(19, "noun.phenomenon", Pos.NOUN), //
			new LexDomain(20, "noun.plant", Pos.NOUN), //
			new LexDomain(21, "noun.possession", Pos.NOUN), //
			new LexDomain(22, "noun.process", Pos.NOUN), //
			new LexDomain(23, "noun.quantity", Pos.NOUN), //
			new LexDomain(24, "noun.relation", Pos.NOUN), //
			new LexDomain(25, "noun.shape", Pos.NOUN), //
			new LexDomain(26, "noun.state", Pos.NOUN), //
			new LexDomain(27, "noun.substance", Pos.NOUN), //
			new LexDomain(28, "noun.time", Pos.NOUN), //
			new LexDomain(29, "verb.body", Pos.VERB), //
			new LexDomain(30, "verb.change", Pos.VERB), //
			new LexDomain(31, "verb.cognition", Pos.VERB), //
			new LexDomain(32, "verb.communication", Pos.VERB), //
			new LexDomain(33, "verb.competition", Pos.VERB), //
			new LexDomain(34, "verb.consumption", Pos.VERB), //
			new LexDomain(35, "verb.contact", Pos.VERB), //
			new LexDomain(36, "verb.creation", Pos.VERB), //
			new LexDomain(37, "verb.emotion", Pos.VERB), //
			new LexDomain(38, "verb.motion", Pos.VERB), //
			new LexDomain(39, "verb.perception", Pos.VERB), //
			new LexDomain(40, "verb.possession", Pos.VERB), //
			new LexDomain(41, "verb.social", Pos.VERB), //
			new LexDomain(42, "verb.stative", Pos.VERB), //
			new LexDomain(43, "verb.weather", Pos.VERB), //
			new LexDomain(44, "adj.ppl", Pos.ADJ) };

	private final int id;

	private final String name;

	private final String domain;

	private final Pos pos;

	private LexDomain(final int id, final String name, final Pos pos)
	{
		final String[] fields = name.split("\\.");
		this.id = id;
		this.name = name;
		this.domain = fields[1];
		this.pos = pos;
	}

	/**
	 * Parse lex domain from string
	 * 
	 * @param str
	 *            string
	 * @return lex domain
	 * @throws ParsePojoException
	 *             parse exception
	 */
	public static LexDomain parseLexDomain(final String str) throws ParsePojoException
	{
		final int id = Integer.parseInt(str);
		if (id >= 0 && id < LexDomain.LEX_DOMAINS.length)
			return LexDomain.LEX_DOMAINS[id];

		throw new ParsePojoException("LexDomain:" + str);
	}

	public int getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public String getDomain()
	{
		return this.domain;
	}

	public Pos getPosId()
	{
		return this.pos;
	}

	public static LexDomain[] values()
	{
		return LexDomain.LEX_DOMAINS;
	}

	@Override
	public String toString()
	{
		return Integer.toString(this.id);
	}
}
