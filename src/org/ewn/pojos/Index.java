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

	public static Index parse(final String line) throws ParsePojoException
	{
		// split into fields
		final String[] fields = line.split("\\s+");

		int fieldPointer = 0;

		// lemma
		final String lemmaString = fields[fieldPointer];
		final Lemma lemma = Lemma.make(lemmaString);
		fieldPointer++;

		// part-of-speech
		final Pos pos = Pos.parse(fields[fieldPointer].charAt(0));
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
			relationTypes[i] = RelationType.parse(fields[fieldPointer + i]);
		}
		fieldPointer += relationTypesCount;

		// polysemy count 2
		final int senseCount2 = Integer.parseInt(fields[fieldPointer]);
		assert senseCount == senseCount2;
		fieldPointer++;

		// tag count
		final TagCnt tagCnt = TagCnt.parse(fields[fieldPointer]);
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
