package org.ewn.grind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WordIndexer
{
	public static final String NOUN_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"n\"]";

	public static final String VERB_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"v\"]";

	public static final String ADJ_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"a\" or Lemma/@partOfSpeech=\"s\"]";

	public static final String ADV_LEXENTRIES_XPATH = "/LexicalResource/Lexicon/LexicalEntry[Lemma/@partOfSpeech=\"r\"]";

	private Document doc;

	private Map<String, Element> synsetsById;

	private Map<String, Long> offsets;

	public WordIndexer(Document doc, Map<String, Element> synsetsById, Map<String, Long> offsets)
	{
		this.doc = doc;
		this.synsetsById = synsetsById;
		this.offsets = offsets;
	}

	/**
	 * Make index
	 * 
	 * @param ps print stream
	 * @param xpath xpath for lexical entry nodes
	 * @throws XPathExpressionException
	 */
	public void makeIndex(PrintStream ps, String xpath) throws XPathExpressionException
	{
		ps.print(Formatter.PRINCETON_HEADER);

		NodeList lexEntryNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = lexEntryNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node lexEntryNode = lexEntryNodes.item(i);
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			// lemma, pos
			Element lemmaElement = XmlUtils.getFirstChildElement(lexEntryElement, "Lemma");
			String lemma = lemmaElement.getAttribute("writtenForm");
			String pos = lemmaElement.getAttribute("partOfSpeech");

			// init
			List<String> synsetIds = new ArrayList<>();
			Set<String> relationPointers = new TreeSet<>();

			// senses
			NodeList senseNodes = lexEntryElement.getElementsByTagName("Sense");
			int nSenses = senseNodes.getLength();
			for (int j = 0; j < nSenses; j++)
			{
				Node senseNode = senseNodes.item(j);
				assert senseNode.getNodeType() == Node.ELEMENT_NODE;
				Element senseElement = (Element) senseNode;

				// synsetid
				String synsetId = senseElement.getAttribute("synset");
				synsetIds.add(synsetId);

				// target synset element
				Element synsetElement = synsetsById.get(synsetId);

				// synset relations
				NodeList synsetRelationNodes = synsetElement.getElementsByTagName("SynsetRelation");
				int nSynsetRelations = synsetRelationNodes.getLength();
				for (int k = 0; k < nSynsetRelations; k++)
				{
					Node synsetRelationNode = synsetRelationNodes.item(k);
					assert synsetRelationNode.getNodeType() == Node.ELEMENT_NODE;
					Element synsetRelationElement = (Element) synsetRelationNode;

					String type = synsetRelationElement.getAttribute("relType");
					String pointer = Coder.codeRelation(type, pos.charAt(0));
					relationPointers.add(pointer);
				}
			}

			// sense relations
			NodeList senseRelationNodes = lexEntryElement.getElementsByTagName("SenseRelation");
			int nSenseRelations = senseRelationNodes.getLength();
			for (int k = 0; k < nSenseRelations; k++)
			{
				Node senseRelationNode = senseRelationNodes.item(k);
				assert senseRelationNode.getNodeType() == Node.ELEMENT_NODE;
				Element senseRelationElement = (Element) senseRelationNode;

				String type = senseRelationElement.getAttribute("relType");
				String pointer = Coder.codeRelation(type, pos.charAt(0));
				relationPointers.add(pointer);
			}

			String ptrs = Formatter.joinNum(relationPointers, "%d");
			String ofs = String.format("%d %d %s", synsetIds.size(), 0, Formatter.join(synsetIds, ' ', false, s -> String.format("%08d", offsets.get(s))));

			String data = String.format("%s %s %d %s %s", Formatter.escape(lemma), pos, nSenses, ptrs, ofs);
			ps.println(data);
		}
		System.err.println("Words: " + n + " for " + xpath);
	}
}
