package org.ewn.pojos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lemma (normalized, lower-cased)
 * 
 * @author Bernard Bou
 */
public class Lemma extends NormalizedString
{
	/**
	 * Adjective Lemma (suffix-stripped)
	 * 
	 * @author Bernard Bou
	 */
	public static class AdjLemma extends Lemma
	{
		private static final String REGEX = "\\((a|p|ip)\\)";

		private static final Pattern PATTERN = Pattern.compile(AdjLemma.REGEX);

		protected final AdjPosition adjPosition;

		/**
		 * Constructor
		 * 
		 * @param normString
		 *            normalized string
		 */
		private AdjLemma(final NormalizedString normString)
		{
			super(normString);

			// trailing adjective position
			final Matcher matcher = AdjLemma.PATTERN.matcher(this.entry);
			if (matcher.find())
			{
				// parse position
				this.adjPosition = AdjPosition.parse(matcher.group());
				// strip position
				this.entry = this.entry.substring(0, matcher.start());
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

		@Override
		public boolean equals(Object obj)
		{
			// ignore position
			return super.equals(obj);
		}

		@SuppressWarnings("EmptyMethod")
		@Override
		public int hashCode()
		{
			// ignore position
			return super.hashCode();
		}
	}

	/**
	 * Constructor from normalized string
	 * 
	 * @param normString
	 *            normalized string
	 */
	private Lemma(final NormalizedString normString)
	{
		this.entry = normString.toString();

		// to lower case
		this.entry = this.entry.toLowerCase();
	}

	// factory

	public static Lemma make(final NormalizedString normString, final boolean isAdj)
	{
		return isAdj ? new AdjLemma(normString) : new Lemma(normString);
	}

	public static Lemma make(final BareNormalizedString bareString)
	{
		return new Lemma(bareString);
	}

	public static Lemma make(final String rawString, final boolean isAdj)
	{
		return Lemma.make(new NormalizedString(rawString), isAdj);
	}

	public static Lemma make(final String rawString)
	{
		return Lemma.make(new NormalizedString(rawString), false);
	}
}
