package org.ewn.pojos;

/**
 * Core Sense with sensekey
 * 
 * @author Bernard Bou
 */
public class CoreSense extends BaseSense
{
	protected final Sensekey sensekey;

	public CoreSense(final SynsetId synsetId, final Lemma lemma, final int sensenum, final Sensekey sensekey)
	{
		super(synsetId, lemma, sensenum);
		this.sensekey = sensekey;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" k=");
		sb.append(this.sensekey.toString());
		return sb.toString();
	}
}
