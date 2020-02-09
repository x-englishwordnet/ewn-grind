package org.ewn.grind;

/**
 * Intermediate data, that are usually accumulated, not Pojos
 *
 * @author Bernard Bou
 */
public class Data
{
	private Data()
	{
	}

	/**
	 * Synset member words. Lexid (from lexicographer file should not exceed 15 in compat mode)
	 */
	static class Word
	{
		private final String lemma;

		private final int lexid;

		public Word(String lemma, int lexid)
		{
			super();
			this.lemma = lemma;
			if (Flags.LEXID_COMPAT)
			{
				if (lexid > 16)
					throw new RuntimeException("Out of range lexid" + lemma + " " + lexid);
				this.lexid = lexid % 16; // 16 -> 0
			}
			else
				this.lexid = lexid;
		}

		@Override
		public String toString()
		{
			int lexid2 = lexid % 16;
			return String.format(Flags.LEXID_COMPAT ? "%s %1X" : "%s %X", lemma, lexid2);
		}
	}

	/**
	 * Semantic or lexical relations
	 */
	static class Relation
	{
		final String ptrSymbol;

		final long targetOffset;

		final char pos;

		final char targetPos;

		final int sourceWordNum;

		final int targetWordNum;

		/**
		 * Constructor
		 *
		 * @param type type of relation @see Coder.codeRelation
		 * @param pos source part of speech
		 * @param targetPos target part of speech
		 * @param targetOffset relation target offset
		 * @param sourceWordNum word number in source synset
		 * @param targetWordNum word number in target synset
		 */
		public Relation(String type, char pos, char targetPos, long targetOffset, int sourceWordNum, int targetWordNum)
		{
			super();
			this.ptrSymbol = Coder.codeRelation(type, pos);
			this.pos = pos;
			this.targetPos = targetPos;
			this.targetOffset = targetOffset;
			this.sourceWordNum = sourceWordNum;
			this.targetWordNum = targetWordNum;
		}

		@Override
		public String toString()
		{
			return String.format("%s %08d %c %02x%02x", ptrSymbol, targetOffset, targetPos, sourceWordNum, targetWordNum);
		}
	}

	/**
	 * Verb (syntactic) frames
	 */
	static class Frame
	{
		public final int frameNum;

		public final int wordNum;

		/**
		 * Constructor
		 *
		 * @param frameNum frame number
		 * @param wordNum 1-based word number in synset this frame applies to
		 */
		public Frame(int frameNum, int wordNum)
		{
			super();
			this.frameNum = frameNum;
			this.wordNum = wordNum;
		}

		@Override
		public String toString()
		{
			return String.format("+ %02d %02x", frameNum, wordNum);
		}
	}
}
