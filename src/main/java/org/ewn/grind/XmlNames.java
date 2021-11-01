package org.ewn.grind;

/**
 * Tags and attributes in XML files
 *
 * @author Bernard Bou
 */
public class XmlNames
{
	private XmlNames()
	{
	}

	/**
	 * DC XML namespace
	 */
	protected static final String NS_DC = "https://globalwordnet.github.io/schemas/dc/";

	// TAGS AND ATTRIBUTES

	static final String LEXICALRESOURCE_TAG = "LexicalResource";

	static final String LEXICON_TAG = "Lexicon";

	static final String LEXICALENTRY_TAG = "LexicalEntry";

	static final String SYNSET_TAG = "Synset";

	static final String DEFINITION_TAG = "Definition";

	static final String EXAMPLE_TAG = "Example";

	static final String SYNSETRELATION_TAG = "SynsetRelation";

	static final String LEMMA_TAG = "Lemma";

	static final String SENSE_TAG = "Sense";

	static final String SENSERELATION_TAG = "SenseRelation";

	static final String FORM_TAG = "Form";

	static final String ID_ATTR = "id";

	static final String N_ATTR = "n";

	static final String POS_ATTR = "partOfSpeech";

	static final String WRITTENFORM_ATTR = "writtenForm";

	static final String SYNSET_ATTR = "synset";

	static final String MEMBERS_ATTR = "members";

	static final String TARGET_ATTR = "target";

	static final String RELTYPE_ATTR = "relType";

	static final String LEXFILE_ATTR = "subject";

	static final String ADJPOSITION_ATTR = "adjposition"; // "adjPosition"

	static final String VERBFRAMES_ATTR = "subcat";
}
