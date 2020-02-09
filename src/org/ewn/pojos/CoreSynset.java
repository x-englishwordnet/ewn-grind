package org.ewn.pojos;

/**
 * Core Synset (without relations and frames)
 *
 * @author Bernard Bou
 */
public class CoreSynset
{
	public final SynsetId synsetId;

	public final Lemma[] lemmas;

	public final Pos pos;

	public final LexDomain lexDomain;

	public final Gloss gloss;

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param lemmas lemmas
	 * @param pos part of speech
	 * @param lexDomain lex domain
	 * @param gloss gloss
	 */
	protected CoreSynset(final SynsetId synsetId, final Lemma[] lemmas, final Pos pos, final LexDomain lexDomain, final Gloss gloss)
	{
		this.synsetId = synsetId;
		this.lemmas = lemmas;
		this.pos = pos;
		this.lexDomain = lexDomain;
		this.gloss = gloss;
	}

	/**
	 * Parse from line
	 *
	 * @param line line
	 * @return members
	 */
	public static BareNormalizedString[] parseMembers(final String line)
	{
		// split into fields
		final String[] fields = line.split("\\s+");
		return CoreSynset.parseMembers(fields);
	}

	/**
	 * Parse from line
	 *
	 * @param line line
	 * @param isAdj whether adj synsets are being parsed
	 * @return synset
	 * @throws ParsePojoException parse exception
	 */
	public static CoreSynset parse(final String line, final boolean isAdj) throws ParsePojoException
	{
		// split into fields
		final String[] fields = line.split("\\s+");
		int fieldPointer = 0;

		// offset
		final long offset = Integer.parseInt(fields[fieldPointer]);
		fieldPointer++;

		// lexdomain
		final LexDomain lexDomain = LexDomain.parse(fields[fieldPointer]);
		fieldPointer++;

		// part-of-speech
		final Pos pos = Pos.parse(fields[fieldPointer].charAt(0));
		// fieldPointer++;

		// id
		final SynsetId synsetId = new SynsetId(pos, offset);

		// lemma set
		final NormalizedString[] members = CoreSynset.parseMembers(fields);
		final Lemma[] lemmas = new Lemma[members.length];
		for (int i = 0; i < members.length; i++)
		{
			lemmas[i] = isAdj ? AdjLemma.makeAdj(members[i]) : Lemma.make(members[i]);
		}
		// fieldPointer += 1 + 2 * members.length;

		// glossary
		final Gloss gloss = new Gloss(line.substring(line.indexOf('|') + 1), synsetId);

		return new CoreSynset(synsetId, lemmas, pos, lexDomain, gloss);
	}

	/**
	 * Parse members from fields
	 *
	 * @param fields fields
	 * @return array of bare normalized strings
	 */
	private static BareNormalizedString[] parseMembers(final String[] fields)
	{
		// data
		int fieldPointer = 3;

		// count
		final int count = Integer.parseInt(fields[fieldPointer], 16);
		fieldPointer++;

		// members
		final BareNormalizedString[] members = new BareNormalizedString[count];
		for (int i = 0; i < count; i++)
		{
			members[i] = new BareNormalizedString(fields[fieldPointer]);
			fieldPointer++;

			// lexid skipped
			fieldPointer++;
		}
		return members;
	}

	public SynsetId getId()
	{
		return this.synsetId;
	}

	public Lemma[] getLemmas()
	{
		return this.lemmas;
	}

	public Pos getPos()
	{
		return this.pos;
	}

	public LexDomain getLexDomain()
	{
		return this.lexDomain;
	}

	public Gloss getGloss()
	{
		return this.gloss;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("id=");
		sb.append(String.format("%08d", this.synsetId.getOffset()));
		sb.append(" words={");
		int i = 0;
		for (final Lemma lemma : this.lemmas)
		{
			if (i != 0)
			{
				sb.append(",");
			}
			sb.append(lemma.toString());
			if (lemma instanceof AdjLemma)
			{
				final AdjLemma adjLemma = (AdjLemma) lemma;
				sb.append(adjLemma.toPositionSuffix());
			}
			i++;
		}
		sb.append("} pos=");
		sb.append(this.pos);
		sb.append(" lexdomain=");
		sb.append(this.lexDomain);
		return sb.toString();
	}
}