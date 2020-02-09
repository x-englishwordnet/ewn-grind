package org.ewn.pojos;

/**
 * Core Sense with sensekey
 *
 * @author Bernard Bou
 */
public class CoreSense extends BaseSense
{
	public final Sensekey sensekey;

	public CoreSense(final SynsetId synsetId, final Lemma lemma, final int sensenum, final Sensekey sensekey)
	{
		super(synsetId, lemma, sensenum);
		this.sensekey = sensekey;
	}

	@Override
	public String toString()
	{
		return super.toString() + " k=" + this.sensekey.toString();
	}
}
