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

public class MorphGrinder
{
	public static final String NOUN_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"n\" and count(Form)>0]";

	public static final String VERB_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"v\" and count(Form)>0]";

	public static final String ADJ_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[(Lemma/@partOfSpeech=\"a\" or Lemma/@partOfSpeech=\"s\") and count(Form)>0]";

	public static final String ADV_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"r\" and count(Form)>0]";

	private final Document doc;

	public MorphGrinder(Document doc)
	{
		this.doc = doc;
	}

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
