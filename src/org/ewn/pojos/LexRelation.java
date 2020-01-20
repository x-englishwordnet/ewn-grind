package org.ewn.pojos;

import java.util.function.Function;

/**
 * Lexical Relation (a lexical relation is an extended semantical relation)
 * 
 * @author Bernard Bou
 */
public class LexRelation extends SemRelation
{
	private final Lemma fromWord;

	private final LemmaRef toWord;

	public LexRelation(final RelationType type, final SynsetId fromSynsetId, final SynsetId toSynsetId, final Lemma fromWord, final LemmaRef toWord)
	{
		super(type, fromSynsetId, toSynsetId);
		this.fromWord = fromWord;
		this.toWord = toWord;
	}

	@Override
	public String toString()
	{
		return this.type.getName() + "-[" + this.fromWord + "]:" + this.toWord;
	}

	public Lemma resolveToWord(Function<SynsetId, Synset> f)
	{
		return this.toWord.resolve(f);
	}
}
