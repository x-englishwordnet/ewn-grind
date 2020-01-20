package org.ewn.pojos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adjective Lemma
 * 
 * @author Bernard Bou
 */
public class AdjLemma extends Lemma
{
	private static final String REGEX = "\\((a|p|ip)\\)";

	private static final Pattern PATTERN = Pattern.compile(AdjLemma.REGEX);

	protected final AdjPosition adjPosition;

	/**
	 * Constructor
	 * 
	 * @param normString normalized string
	 */
	public AdjLemma(final NormalizedString normString)
	{
		super(normString);

		// trailing adjective position
		final Matcher matcher = AdjLemma.PATTERN.matcher(this.normalized);
		if (matcher.find())
		{
			// parse position
			this.adjPosition = AdjPosition.parse(matcher.group());
			// strip position
			this.normalized = this.normalized.substring(0, matcher.start());
		}
		else
			this.adjPosition = null;
	}

	/**
	 * Get position
	 * 
	 * @return position
	 */
	public AdjPosition getPosition()
	{
		return this.adjPosition;
	}

	/**
	 * Get position tag (to append to lemma)
	 * 
	 * @return position string
	 */
	public String toPositionSuffix()
	{
		if (this.adjPosition == null)
			return "";
		return "(" + this.adjPosition.getTag() + ")";
	}
}
