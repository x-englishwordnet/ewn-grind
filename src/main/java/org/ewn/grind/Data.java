package org.ewn.grind;

import java.util.*;

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
	 * Synset member lemma. Members are ordered lemmas.
	 * Lexid (from lexicographer file should not exceed 15 in compat mode)
	 */
	static class Member
	{
		protected final String lemma;

		protected final int lexid;

		public final int order;

		public Member(String lemma, int lexid, int order)
		{
			super();
			this.lemma = lemma;
			if (Flags.LEXID_COMPAT)
			{
				this.lexid = lexid % 16; // 16 -> 0
				if (lexid > 16)
					//throw new RuntimeException("Out of range lexid" + lemma + " " + lexid);
					System.err.printf("Out of range lexid %s: %d tweaked to %d%n", lemma, lexid, this.lexid);
			}
			else
				this.lexid = lexid;
			this.order = order;
		}

		public String toWndbString()
		{
			return String.format(Flags.LEXID_COMPAT ? "%s %1X" : "%s %X", lemma, lexid);
		}

		@Override public String toString()
		{
			return String.format("Member %s lexid:%X order:%d", lemma, lexid, order);
		}
	}

	/**
	 * Adjective synset member lemmas with position constraint expressed (ip,p,a).
	 */
	static class AdjMember extends Member
	{
		private final String position;

		public AdjMember(String lemma, int lexid, int order, String position)
		{
			super(lemma, lexid, order);
			this.position = position;
		}

		@Override public String toWndbString()
		{
			return String.format(Flags.LEXID_COMPAT ? "%s(%s) %1X" : "%s(%s) %X", lemma, position, lexid);
		}

		@Override public String toString()
		{
			return String.format("Adj Member %s(%s) %X %d", lemma, position, lexid, order);
		}
	}

	/**
	 * Members, a sorted list
	 */
	static class Members extends TreeSet<Member>
	{
		private static final long serialVersionUID = 4565855492410766062L;

		public Members()
		{
			super(Comparator.comparingInt((Member m) -> m.order).thenComparing(m -> m.lemma));
			// the thenComparing should not be called !
		}

		public int indexOf(final Member member)
		{
			// if the element exists in the TreeSet
			if (contains(member))
			{
				// the element index will be equal to the size of the headSet for the element
				return headSet(member).size();
			}
			throw new IllegalStateException("Member index of " + member.toString() + " not contained in " + Arrays.toString(this.toArray()));
		}

		public String toWndbString()
		{
			return Formatter.joinNum(this, "%02x", Member::toWndbString);
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
		 * @param type          type of relation @see Coder.codeRelation
		 * @param pos           source part of speech
		 * @param targetPos     target part of speech
		 * @param targetOffset  relation target offset
		 * @param sourceWordNum word number in source synset
		 * @param targetWordNum word number in target synset
		 */
		public Relation(String type, char pos, char targetPos, long targetOffset, int sourceWordNum, int targetWordNum) throws CompatException
		{
			super();
			this.ptrSymbol = Coder.codeRelation(type, pos);
			this.pos = pos;
			this.targetPos = targetPos;
			this.targetOffset = targetOffset;
			this.sourceWordNum = sourceWordNum;
			this.targetWordNum = targetWordNum;
		}

		public String toWndbString()
		{
			return String.format("%s %08d %c %02x%02x", ptrSymbol, targetOffset, targetPos, sourceWordNum, targetWordNum);
		}

		@Override public String toString()
		{
			return String.format("Relation %s %08d %c %02x%02x", ptrSymbol, targetOffset, targetPos, sourceWordNum, targetWordNum);
		}
	}

	/**
	 * Verb (syntactic) frames
	 */
	static class Frame
	{
		public final int frameNum;

		public final int memberNum;

		/**
		 * Constructor
		 *
		 * @param frameNum  frame number
		 * @param memberNum 1-based lemma member number in synset this frame applies to
		 */
		public Frame(int frameNum, int memberNum)
		{
			super();
			this.frameNum = frameNum;
			this.memberNum = memberNum;
		}

		public String toWndbString()
		{
			return String.format("+ %02d %02x", frameNum, memberNum);
		}

		@Override public String toString()
		{
			return String.format("Frame %02d %02x", frameNum, memberNum);
		}
	}

	/**
	 * Verb (syntactic) frames, a list of frames mapped per given frameNum
	 */
	static class Frames extends HashMap<Integer, List<Frame>>
	{
		private static final long serialVersionUID = -3313309054723217964L;

		public Frames()
		{
			super();
		}

		public void add(final Frame frame)
		{
			List<Frame> frames2 = computeIfAbsent(frame.frameNum, k -> new ArrayList<>());
			frames2.add(frame);
		}

		/**
		 * Join frames.
		 * If a frame applies to all words, then frame num is zeroed
		 *
		 * @param pos          part of speech
		 * @param membersCount synset member count
		 * @return formatted verb frames
		 */
		public String toWndbString(char pos, int membersCount)
		{
			if (pos != 'v')
				return "";
			// compulsory for verbs even if empty
			if (size() < 1)
				return "00";
			List<Frame> resultFrames = new ArrayList<>();
			for (Entry<Integer, List<Frame>> entry : entrySet())
			{
				Integer frameNum = entry.getKey();
				List<Frame> framesWithFrameNum = entry.getValue();
				if (framesWithFrameNum.size() == membersCount)
					resultFrames.add(new Frame(frameNum, 0));
				else
					resultFrames.addAll(framesWithFrameNum);
			}
			return Formatter.joinNum(resultFrames, "%02d", Frame::toWndbString);
		}
	}
}
