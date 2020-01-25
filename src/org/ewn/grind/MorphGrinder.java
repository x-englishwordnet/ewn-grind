package org.ewn.grind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	 * @throws XPathExpressionException
	 */
	public void makeMorph(PrintStream ps, String xpath) throws XPathExpressionException
	{
		ArrayList<String> lines = new ArrayList<>();

		// iterate synset elements
		NodeList lexEntryNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = lexEntryNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node lexEntryNode = lexEntryNodes.item(i);
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			Element lemmaElement = XmlUtils.getFirstChildElement(lexEntryElement, "Lemma");
			String lemma = lemmaElement.getAttribute("writtenForm");

			List<Element> formElements = XmlUtils.getChildElements(lexEntryElement, "Form");
			for (Element formElement : formElements)
			{
				String form = formElement.getAttribute("writtenForm");
				String line = String.format("%s %s", form, lemma);
				lines.add(line);
			}
		}
		Collections.sort(lines);
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Morphs: " + n + " for " + xpath);
	}
}
