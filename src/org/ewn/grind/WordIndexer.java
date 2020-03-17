package org.ewn.grind;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class produces the index.{noun|verb|adj|adv} files
 *
 * @author Bernard Bou
 */
public class WordIndexer
{
	/**
	 * XPath for noun lexical entry elements
	 */
	public static final String NOUN_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='n']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR);

	/**
	 * XPath for verb lexical entry elements
	 */
	public static final String VERB_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='v']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR);

	/**
	 * XPath for adj lexical entry elements
	 */
	public static final String ADJ_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='a' or %s/@%s='s']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR);

	/**
	 * XPath for adv lexical entry elements
	 */
	public static final String ADV_LEXENTRIES_XPATH = String.format("/%s/%s/%s[%s/@%s='r']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.LEXICALENTRY_TAG, XmlNames.LEMMA_TAG, XmlNames.POS_ATTR);

	/**
	 * W3C document
	 */
	private final Document doc;

	/**
	 * Map of synset elements indexed by their synset id key
	 */
	private final Map<String, Element> synsetsById;

	/**
	 * Synset offsets indexed by synset id key
	 */
	private final Map<String, Long> offsets;

	/**
	 * Constructor
	 *
	 * @param doc W3C document
	 * @param synsetsById map of synset elements indexed by their synset id key
	 * @param offsets offsets indexed by synset id key
	 */
	public WordIndexer(Document doc, Map<String, Element> synsetsById, Map<String, Long> offsets)
	{
		this.doc = doc;
		this.synsetsById = synsetsById;
		this.offsets = offsets;
	}

	private static class IndexData
	{
		String pos;

		final Set<String> synsetIds = new LinkedHashSet<>();

		final Set<String> relationPointers = new TreeSet<>();
	}

	/**
	 * Make index
	 *
	 * @param ps print stream
	 * @param xpath xpath for lexical entry nodes
	 * @throws XPathExpressionException xpath
	 */
	public void makeIndex(PrintStream ps, String xpath) throws XPathExpressionException
	{
		ps.print(Formatter.PRINCETON_HEADER);

		// collect lines in a set to avoid duplicate lines that arise from lowercasing of lemma
		Map<String, IndexData> indexEntries = new TreeMap<>();

		NodeList lexEntryNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = lexEntryNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node lexEntryNode = lexEntryNodes.item(i);
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			// lemma, pos
			Element lemmaElement = XmlUtils.getFirstChildElement(lexEntryElement, XmlNames.LEMMA_TAG);
			assert lemmaElement != null;
			String form = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
			String key = Formatter.escape(form.toLowerCase());
			String pos = lemmaElement.getAttribute(XmlNames.POS_ATTR);

			// init
			IndexData data = indexEntries.computeIfAbsent(key, k -> new IndexData());

			// pos
			data.pos = pos;

			// senses
			NodeList senseNodes = lexEntryElement.getElementsByTagName(XmlNames.SENSE_TAG);
			int nSenses = senseNodes.getLength();
			assert nSenses >= 1;
			for (int j = 0; j < nSenses; j++)
			{
				Node senseNode = senseNodes.item(j);
				assert senseNode.getNodeType() == Node.ELEMENT_NODE;
				Element senseElement = (Element) senseNode;

				// synsetid
				String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
				data.synsetIds.add(synsetId);

				// target synset element
				Element synsetElement = synsetsById.get(synsetId);

				// synset relations
				NodeList synsetRelationNodes = synsetElement.getElementsByTagName(XmlNames.SYNSETRELATION_TAG);
				int nSynsetRelations = synsetRelationNodes.getLength();
				for (int k = 0; k < nSynsetRelations; k++)
				{
					Node synsetRelationNode = synsetRelationNodes.item(k);
					assert synsetRelationNode.getNodeType() == Node.ELEMENT_NODE;
					Element synsetRelationElement = (Element) synsetRelationNode;

					String type = synsetRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);
					String pointer;
					try
					{
						pointer = Coder.codeRelation(type, pos.charAt(0));
					}
					catch (CompatException e)
					{
						System.err.println(e.getMessage());
						continue;
					}
					data.relationPointers.add(pointer);
				}
			}

			// sense relations
			NodeList senseRelationNodes = lexEntryElement.getElementsByTagName(XmlNames.SENSERELATION_TAG);
			int nSenseRelations = senseRelationNodes.getLength();
			for (int k = 0; k < nSenseRelations; k++)
			{
				Node senseRelationNode = senseRelationNodes.item(k);
				assert senseRelationNode.getNodeType() == Node.ELEMENT_NODE;
				Element senseRelationElement = (Element) senseRelationNode;

				String type = senseRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);
				String pointer;
				try
				{
					pointer = Coder.codeRelation(type, pos.charAt(0));
				}
				catch (CompatException e)
				{
					System.err.println(e.getMessage());
					continue;
				}
				data.relationPointers.add(pointer);
			}
		}

		int count = 0;
		for (Map.Entry<String, IndexData> indexEntry : indexEntries.entrySet())
		{
			String key = indexEntry.getKey();
			IndexData data = indexEntry.getValue();
			int nSenses = data.synsetIds.size();

			String ptrs = Formatter.joinNum(data.relationPointers, "%d");
			String ofs = String.format("%d %d %s", nSenses, 0, Formatter.join(data.synsetIds, ' ', false, s -> String.format("%08d", offsets.get(s))));
			String line = String.format("%s %s %d %s %s", key, data.pos, nSenses, ptrs, ofs);
			ps.println(line);
			count++;
		}
		System.err.println("Words: " + count + '/' + n + " lexentries for " + xpath);
	}
}
