package org.ewn.pojos;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gloss
 * 
 * @author Bernard Bou
 */
public final class Gloss
{
	private final String[] splitGloss;

	private final SynsetId synsetId;

	public Gloss(final String gloss)
	{
		this.synsetId = null;
		this.splitGloss = split(gloss.trim());
	}

	public Gloss(final String gloss, final SynsetId synsetId)
	{
		this.synsetId = synsetId;
		this.splitGloss = split(gloss.trim());
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.splitGloss.length; i++)
		{
			if (i != 0)
			{
				sb.append(";");
			}
			sb.append(this.splitGloss[i]);
		}
		return sb.toString();
	}

	private String[] split(final String gloss)
	{
		final String REGEX = "\\\"[^\\\"]*\\\"";
		final Pattern thePattern = Pattern.compile(REGEX);

		int quoteCount = 0;
		for (int p = 0; (p = gloss.indexOf('"', p + 1)) != -1;)
		{
			quoteCount++;
		}
		if (quoteCount % 2 != 0)
		{
			System.err.println("Uneven quotes in " + (this.synsetId != null ? this.synsetId : "unknown") + ":" + gloss);
		}

		final Matcher matcher = thePattern.matcher(gloss); // get a matcher object
		int count = 0;
		int split = -1;
		while (matcher.find())
		{
			if (count == 0)
			{
				split = matcher.start();
			}
			count++;
		}

		final String[] result = new String[count + 1];

		// [0] definition
		String definition = split == -1 ? gloss : gloss.substring(0, split);
		definition = definition.replaceFirst("[;\\s]*$", "");
		result[0] = definition;

		// [1-n] samples
		matcher.reset();
		for (count = 1; matcher.find(); count++)
		{
			String sample = matcher.group();
			if (sample.startsWith("\"") && sample.endsWith("\""))
			{
				sample = sample.substring(1, sample.length() - 1);
			}
			result[count] = sample;
		}
		return result;
	}

	public String getDefinition()
	{
		return this.splitGloss[0];
	}

	public String[] getSamples()
	{
		return Arrays.copyOfRange(this.splitGloss, 1, this.splitGloss.length);
	}
}
