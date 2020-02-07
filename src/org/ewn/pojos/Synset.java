package org.ewn.pojos;

/**
 * Synset, a core synset extended to include relations and possibly verb frames
 * 
 * @author Bernard Bou
 */
public class Synset extends CoreSynset
{
	public final SemRelation[] semRelations;

	public final VerbFrameRef[] verbFrameRefs;

	/**
	 * Constructor
	 * 
	 * @param synsetId synset id
	 * @param lemmas lemmas
	 * @param pos part of speech
	 * @param lexDomain lex domain
	 * @param gloss gloss
	 * @param semRelations relations
	 * @param verbFrames verb frames
	 */
	private Synset(final SynsetId synsetId, final Lemma[] lemmas, final Pos pos, final LexDomain lexDomain, final Gloss gloss, final SemRelation[] semRelations, final VerbFrameRef[] verbFrames)
	{
		super(synsetId, lemmas, pos, lexDomain, gloss);
		this.semRelations = semRelations;
		this.verbFrameRefs = verbFrames;
	}

	/**
	 * Parse from line
	 * 
	 * @param line line
	 * @param isAdj whether adj synsets are being parsed
	 * @return synset
	 */
	public static Synset parseSynset(final String line, final boolean isAdj)
	{
		final CoreSynset protoSynset = CoreSynset.parse(line, isAdj);

		// copy data from proto
		final Pos pos = protoSynset.getPos();
		final Lemma[] lemmas = protoSynset.getLemmas();
		final SynsetId synsetId = protoSynset.getId();
		final LexDomain lexDomain = protoSynset.getLexDomain();
		final Gloss gloss = protoSynset.getGloss();

		// split into fields
		final String[] fields = line.split("\\s+");
		int fieldPointer = 0;

		// data
		SemRelation[] semRelations;
		VerbFrameRef[] frames = null;

		// offset
		fieldPointer++;

		// lexdomain
		fieldPointer++;

		// part-of-speech
		fieldPointer++;

		// lemma count
		fieldPointer++;

		// lemma set
		fieldPointer += 2 * lemmas.length;

		// relation count
		final int relationCount = Integer.parseInt(fields[fieldPointer], 10);
		fieldPointer++;

		// relations
		semRelations = new SemRelation[relationCount];
		for (int i = 0; i < relationCount; i++)
		{
			// read data
			final String relationTypeField = fields[fieldPointer++];
			final String relationSynsetIdField = fields[fieldPointer++];
			final String relationPosField = fields[fieldPointer++];
			final String relationSourceTargetField = fields[fieldPointer++];
			final long relationSynsetId = Long.parseLong(relationSynsetIdField);
			final int relationSourceTarget = Integer.parseInt(relationSourceTargetField, 16);

			// compute
			final Pos relationPos = Pos.parse(relationPosField.charAt(0));
			final RelationType relationType = RelationType.parse(relationTypeField);
			final SynsetId toId = new SynsetId(relationPos, relationSynsetId);

			// create
			if (relationSourceTarget != 0)
			{
				final int fromWordIndex = relationSourceTarget >> 8;
				final int toWordIndex = relationSourceTarget & 0xff;
				final Lemma fromLemma = lemmas[fromWordIndex - 1];
				final LemmaRef toLemma = new LemmaRef(toId, toWordIndex);
				semRelations[i] = new LexRelation(relationType, synsetId, toId, fromLemma, toLemma);
			}
			else
			{
				semRelations[i] = new SemRelation(relationType, synsetId, toId);
			}
		}

		// frames
		if (pos.toChar() == 'v' && !fields[fieldPointer].equals("|"))
		{
			// frame count
			final int frameCount = Integer.parseInt(fields[fieldPointer], 10);
			fieldPointer++;

			// frames
			frames = new VerbFrameRef[frameCount];
			for (int i = 0; i < frameCount; i++)
			{
				// read data
				fieldPointer++; // '+'
				final String frameIdField = fields[fieldPointer++];
				final String wordIndexField = fields[fieldPointer++];

				// compute
				final int frameId = Integer.parseInt(frameIdField);
				final int wordIndex = Integer.parseInt(wordIndexField, 16);

				// create
				Lemma[] frameLemmas;
				if (wordIndex != 0)
				{
					frameLemmas = new Lemma[] { lemmas[wordIndex - 1] };
				}
				else // 0 means all
				{
					frameLemmas = lemmas;
				}
				frames[i] = new VerbFrameRef(frameLemmas, frameId);
			}
		}
		return new Synset(synsetId, lemmas, pos, lexDomain, gloss, semRelations, frames);
	}

	public SemRelation[] getRelations()
	{
		return semRelations;
	}

	public VerbFrameRef[] getVerbFrames()
	{
		return verbFrameRefs;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		if (this.semRelations != null)
		{
			sb.append(" relations={");
			for (int i = 0; i < this.semRelations.length; i++)
			{
				if (i != 0)
				{
					sb.append(",");
				}
				sb.append(this.semRelations[i].toString());
			}
			sb.append("}");
		}
		if (this.verbFrameRefs != null)
		{
			sb.append(" frames={");
			for (int i = 0; i < this.verbFrameRefs.length; i++)
			{
				if (i != 0)
				{
					sb.append(",");
				}
				sb.append(this.verbFrameRefs[i].toString());
			}
			sb.append("}");
		}
		return sb.toString();
	}
}