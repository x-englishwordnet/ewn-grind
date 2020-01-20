package org.ewn.pojos;

/**
 * Verb Frame
 * 
 * @author Bernard Bou
 */
public class VerbFrame
{
	private final Lemma[] lemmas;

	private final int frameId;

	public VerbFrame(final Lemma[] lemmas, final int frameId)
	{
		this.lemmas = lemmas;
		this.frameId = frameId;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		int i = 0;
		for (final Lemma lemma : this.lemmas)
		{
			if (i != 0)
			{
				sb.append(",");
			}
			sb.append(lemma.toString());
			i++;
		}
		sb.append("}:");
		sb.append(this.frameId);
		return sb.toString();
	}
}