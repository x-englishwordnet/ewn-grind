package org.ewn.pojos;

/**
 * Index
 * 
 * @author Bernard Bou
 */

public class CoreIndex
{
	private final Lemma lemma;

	private final BaseSense[] senses;

	protected CoreIndex(final Lemma lemma, final BaseSense[] senses)
	{
		this.lemma = lemma;
		this.senses = senses;
	}

	public static CoreIndex parse(final String line)
	{
		// split into fields
		final String[] fields = line.split("\\s+");

		int fieldPointer = 0;

		// lemma/word
		final String lemmaString = fields[fieldPointer];
		final Lemma lemma = Lemma.make(lemmaString);
		fieldPointer++;

		// part-of-speech
		final Pos pos = Pos.parse(fields[fieldPointer].charAt(0));
		fieldPointer++;

		// polysemy count
		final int senseCount = Integer.parseInt(fields[fieldPointer]);
		fieldPointer++;

		// relation count
		final int relationCount = Integer.parseInt(fields[fieldPointer], 10);
		fieldPointer++;

		// relations
		fieldPointer += relationCount;

		// polysemy count 2
		fieldPointer++;

		// tag count
		fieldPointer++;

		// senses
		final BaseSense[] senses = new BaseSense[senseCount];
		for (int i = 0; i < senseCount; i++)
		{
			senses[i] = BaseSense.make(lemma, pos, fields[fieldPointer], i + 1);
			fieldPointer++;
		}
		return new CoreIndex(lemma, senses);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(this.lemma.toString());
		sb.append(" senses={");
		for (int i = 0; i < this.senses.length; i++)
		{
			if (i != 0)
			{
				sb.append(" ");
			}
			sb.append(this.senses[i].toString());
		}
		sb.append("}");
		return sb.toString();
	}
}
