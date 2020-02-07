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
	protected static final String NS_DC = "http://purl.org/dc/elements/1.1/";

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

	static final String TARGET_ATTR = "target";

	static final String RELTYPE_ATTR = "relType";

	static final String LEXID_ATTR = "lexid";

	static final String LEXFILE_ATTR = "subject";

	static final String SENSEKEY_ATTR = "sensekey";

	static final String SYNTACTICBEHAVIOUR_ATTR = "syntactic_behaviour";

	static final String SENTENCE_TEMPLATE_ATTR = "sentence_template";

	static final String TAGCOUNT_ATTR = "tagcnt";
}
