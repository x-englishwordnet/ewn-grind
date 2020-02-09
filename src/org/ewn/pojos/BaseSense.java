package org.ewn.pojos;

/**
 * Base Sense
 *
 * @author Bernard Bou
 */
public class BaseSense
{
	public final Lemma lemma;

	public final SynsetId synsetId;

	public final int senseNum;

	protected BaseSense(final SynsetId synsetId, final Lemma word, final int senseNum)
	{
		this.lemma = word;
		this.synsetId = synsetId;
		this.senseNum = senseNum;
	}

	public static BaseSense make(final Lemma lemma, final Pos pos, final String synsetIdString, final int senseNum)
	{
		return new BaseSense(new SynsetId(pos, Long.parseLong(synsetIdString)), lemma, senseNum);
	}

	@Override
	public String toString()
	{
		return "(w=" + this.lemma.toString() + "-s=" + this.synsetId.toString() + "-n=" + this.senseNum + ")";
	}
}