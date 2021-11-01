package org.ewn.grind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class produces the index.{noun|verb|adj|adv}.exc files
 *
 * @author Bernard Bou
 */
public class MorphGrinder
{
	/**
	 * XPath for noun lexical entry elements
	 */
	public static final String NOUN_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='n' and count(%s)>0]", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.FORM_TAG);

	/**
	 * XPath for verb lexical entry elements
	 */
	public static final String VERB_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='v' and count(%s)>0]", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.FORM_TAG);

	/**
	 * XPath for adj lexical entry elements
	 */
	public static final String ADJ_LEXENTRIES_XPATH = String.format("/%s/%s/%s[(%s/@%s='a' or %s/@%s='s') and count(%s)>0]", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.FORM_TAG);

	/**
	 * XPath for adv lexical entry elements
	 */
	public static final String ADV_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='r' and count(%s)>0]", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.FORM_TAG);

	/**
	 * W3C document
	 */
	private final Document doc;

	/**
	 * Constructor
	 *
	 * @param doc W3C document
	 */
	public MorphGrinder(Document doc)
	{
		this.doc = doc;
	}

	/**
	 * Make morph files
	 *
	 * @param ps print stream
	 * @param xpath xpath selecting lexical entry elements
	 * @throws XPathExpressionException xpath
	 */
	public void makeMorph(PrintStream ps, String xpath) throws XPathExpressionException
	{
		Set<String> lines = new TreeSet<>();

		// iterate lex entry elements
		NodeList lexEntryNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = lexEntryNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node lexEntryNode = lexEntryNodes.item(i);
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			Element lemmaElement = XmlUtils.getUniqueChildElement(lexEntryElement, XmlNames.LEMMA_TAG);
			assert lemmaElement != null;
			String lemma = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);

			List<Element> formElements = XmlUtils.getChildElements(lexEntryElement, XmlNames.FORM_TAG);
			if (formElements != null)
			{
				for (Element formElement : formElements)
				{
					String form = formElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
					String line = String.format("%s %s", form, lemma);
					lines.add(line);
				}
			}
		}
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Morphs: " + n + " for " + xpath);
	}
}
