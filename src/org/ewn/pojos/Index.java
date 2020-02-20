package org.ewn.pojos;

public class Index extends CoreIndex
{
	final RelationType[] relationTypes;

	final TagCnt tagCnt;

	public Index(final Lemma lemma, final BaseSense[] senses, final RelationType[] relationTypes, final TagCnt tagCnt)
	{
		super(lemma, senses);
		this.relationTypes = relationTypes;
		this.tagCnt = tagCnt;
	}

	/**
	 * Parse index from line
	 * 
	 * @param line
	 *            line
	 * @return index
	 * @throws ParsePojoException
	 *             pase exception
	 */
	public static Index parseIndex(final String line) throws ParsePojoException
	{
		try
		{
			// split into fields
			final String[] fields = line.split("\\s+");

			int fieldPointer = 0;

			// lemma
			final String lemmaString = fields[fieldPointer];
			final Lemma lemma = Lemma.make(lemmaString);
			fieldPointer++;

			// part-of-speech
			final Pos pos = Pos.parsePos(fields[fieldPointer].charAt(0));
			fieldPointer++;

			// polysemy count
			final int senseCount = Integer.parseInt(fields[fieldPointer]);
			fieldPointer++;

			// relation types count
			final int relationTypesCount = Integer.parseInt(fields[fieldPointer], 10);
			fieldPointer++;

			// relation types
			final RelationType[] relationTypes = new RelationType[relationTypesCount];
			for (int i = 0; i < relationTypesCount; i++)
			{
				relationTypes[i] = RelationType.parseRelationType(fields[fieldPointer + i]);
			}
			fieldPointer += relationTypesCount;

			// polysemy count 2
			final int senseCount2 = Integer.parseInt(fields[fieldPointer]);
			assert senseCount == senseCount2;
			fieldPointer++;

			// tag count
			final TagCnt tagCnt = TagCnt.parseTagCnt(fields[fieldPointer]);
			fieldPointer++;

			// senses
			final BaseSense[] senses = new BaseSense[senseCount];
			for (int i = 0; i < senseCount; i++)
			{
				senses[i] = BaseSense.make(lemma, pos, fields[fieldPointer], i + 1);
				fieldPointer++;
			}
			return new Index(lemma, senses, relationTypes, tagCnt);
		}
		catch (Exception e)
		{
			throw new ParsePojoException(e);
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" relations={");
		for (int i = 0; i < this.relationTypes.length; i++)
		{
			if (i != 0)
			{
				sb.append(" ");
			}
			sb.append(this.relationTypes[i].toString());
		}
		sb.append("}");
		sb.append(" tagcnt=");
		sb.append(this.tagCnt);
		return sb.toString();
	}

}
