package org.ewn.pojos;

/**
 * Lex Domain
 * 
 * @author Bernard Bou
 */
public class LexDomain
{
	private static final LexDomain[] LEX_DOMAINS = { //
			new LexDomain(0, "adj.all"), //
			new LexDomain(1, "adj.pert"), //
			new LexDomain(2, "adv.all"), //
			new LexDomain(3, "noun.tops"), //
			new LexDomain(4, "noun.act"), //
			new LexDomain(5, "noun.animal"), //
			new LexDomain(6, "noun.artifact"), //
			new LexDomain(7, "noun.attribute"), //
			new LexDomain(8, "noun.body"), //
			new LexDomain(9, "noun.cognition"), //
			new LexDomain(10, "noun.communication"), //
			new LexDomain(11, "noun.event"), //
			new LexDomain(12, "noun.feeling"), //
			new LexDomain(13, "noun.food"), //
			new LexDomain(14, "noun.group"), //
			new LexDomain(15, "noun.location"), //
			new LexDomain(16, "noun.motive"), //
			new LexDomain(17, "noun.object"), //
			new LexDomain(18, "noun.person"), //
			new LexDomain(19, "noun.phenomenon"), //
			new LexDomain(20, "noun.plant"), //
			new LexDomain(21, "noun.possession"), //
			new LexDomain(22, "noun.process"), //
			new LexDomain(23, "noun.quantity"), //
			new LexDomain(24, "noun.relation"), //
			new LexDomain(25, "noun.shape"), //
			new LexDomain(26, "noun.state"), //
			new LexDomain(27, "noun.substance"), //
			new LexDomain(28, "noun.time"), //
			new LexDomain(29, "verb.body"), //
			new LexDomain(30, "verb.change"), //
			new LexDomain(31, "verb.cognition"), //
			new LexDomain(32, "verb.communication"), //
			new LexDomain(33, "verb.competition"), //
			new LexDomain(34, "verb.consumption"), //
			new LexDomain(35, "verb.contact"), //
			new LexDomain(36, "verb.creation"), //
			new LexDomain(37, "verb.emotion"), //
			new LexDomain(38, "verb.motion"), //
			new LexDomain(39, "verb.perception"), //
			new LexDomain(40, "verb.possession"), //
			new LexDomain(41, "verb.social"), //
			new LexDomain(42, "verb.stative"), //
			new LexDomain(43, "verb.weather"), //
			new LexDomain(44, "adj.ppl") };

	private final int id;

	private final String name;

	private final String domain;

	private final Pos pos;

	private LexDomain(final int id, final String name)
	{
		final String[] fields = name.split("\\.");
		this.id = id;
		this.name = name;
		this.domain = fields[1];
		this.pos = Pos.parse(fields[0]);
	}

	public static LexDomain parse(final String str)
	{
		final int id = Integer.parseInt(str);
		if (id >= 0 && id < LexDomain.LEX_DOMAINS.length)
			return LexDomain.LEX_DOMAINS[id];

		throw new IllegalArgumentException("LexDomain:" + str);
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
		return LEX_DOMAINS;
	}

	@Override
	public String toString()
	{
		return Integer.toString(this.id);
	}
}
