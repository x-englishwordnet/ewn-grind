package org.ewn.pojos;

/**
 * Semantical Relation (a lexical relation is an extended semantical relation)
 *
 * @author Bernard Bou
 */
public class SemRelation
{
	public final RelationType type;

	public final SynsetId fromSynsetId;

	public final SynsetId toSynsetId;

	/**
	 * Constructor
	 *
	 * @param type relation type
	 * @param fromSynsetId source synset id
	 * @param toSynsetId target synset id
	 */
	public SemRelation(final RelationType type, final SynsetId fromSynsetId, final SynsetId toSynsetId)
	{
		this.type = type;
		this.fromSynsetId = fromSynsetId;
		this.toSynsetId = toSynsetId;
	}

	@Override
	public String toString()
	{
		return this.type.getName() + ":" + this.toSynsetId.toString();
	}
}
