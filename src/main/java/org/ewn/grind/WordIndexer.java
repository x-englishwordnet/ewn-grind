package org.ewn.grind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintStream;
import java.util.*;

import javax.xml.xpath.XPathExpressionException;

/**
 * This class produces the index.{noun|verb|adj|adv} files
 *
 * @author Bernard Bou
 */
public class WordIndexer
{
	/**
	 * Format in data file
	 */
	private static final String WORD_FORMAT = "%s %s %d %s %d %d %s  ";
	// lemma
	// pos
	// synset_cnt
	// ptrs=p_cnt  [ptr_symbol...]
	// ofs=sense_cnt
	// tagsense_cnt (0)
	// synset_offset  [synset_offset...]

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
	 * @param doc         W3C document
	 * @param synsetsById map of synset elements indexed by their synset id key
	 * @param offsets     offsets indexed by synset id key
	 */
	public WordIndexer(Document doc, Map<String, Element> synsetsById, Map<String, Long> offsets)
	{
		this.doc = doc;
		this.synsetsById = synsetsById;
		this.offsets = offsets;
	}

	private static class IndexData
	{
		private String pos;

		final Set<String> synsetIds = new LinkedHashSet<>();

		final Set<String> relationPointers = new TreeSet<>();

		public String getPos()
		{
			if ("s".equals(pos))
			{
				return "a";
			}
			return pos;
		}
	}

	/**
	 * Make index
	 *
	 * @param ps    print stream
	 * @param xpath xpath for lexical entry nodes
	 * @throws XPathExpressionException xpath
	 */
	public void makeIndex(PrintStream ps, String xpath) throws XPathExpressionException
	{
		Map<String, Integer> incompats = new HashMap<>();

		ps.print(Formatter.OEWN_HEADER);

		// collect lines in a set to avoid duplicate lines that arise from lower casing of lemma
		Map<String, IndexData> indexEntries = new TreeMap<>();

		NodeList lexEntryNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = lexEntryNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node lexEntryNode = lexEntryNodes.item(i);
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			// lemma, pos
			Element lemmaElement = XmlUtils.getUniqueChildElement(lexEntryElement, XmlNames.LEMMA_TAG);
			assert lemmaElement != null;
			String form = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
			String key = Formatter.escape(form.toLowerCase());
			String pos = lemmaElement.getAttribute(XmlNames.POS_ATTR);

			// init
			IndexData data = indexEntries.computeIfAbsent(key, k -> new IndexData());

			// pos
			data.pos = pos;

			// senses
			List<Element> senseElements = XmlUtils.getChildElementsSortedBy(lexEntryElement, XmlNames.SENSE_TAG, (element1, element2) -> {
				String nAttr1 = element1.getAttribute(XmlNames.N_ATTR);
				String nAttr2 = element2.getAttribute(XmlNames.N_ATTR);
				if (nAttr1.isEmpty())
				{
					throw new IllegalArgumentException(element1.getAttribute(XmlNames.ID_ATTR) + " has no 'n' attr");
				}
				if (nAttr2.isEmpty())
				{
					throw new IllegalArgumentException(element2.getAttribute(XmlNames.ID_ATTR) + " has no 'n' attr");
				}
				int n1 = Integer.parseInt(nAttr1);
				int n2 = Integer.parseInt(nAttr2);
				return Integer.compare(n1, n2);
			});

			if (senseElements == null)
			{
				throw new IllegalArgumentException("LexicalEntry " + lexEntryElement.getAttribute(XmlNames.ID_ATTR) + " has no Sense");
			}
			else
			{
				int previousRank = -1;
				for (Element senseElement : senseElements)
				{
					// check ordering
					String nAttr = senseElement.getAttribute(XmlNames.N_ATTR);
					if (nAttr.isEmpty())
					{
						// current wn.xml has no 'n'
						throw new IllegalArgumentException("LexEntry " + lexEntryElement.getAttribute(XmlNames.ID_ATTR) + " with no 'n' attribute");
					}
					int rank = Integer.parseInt(nAttr);
					if (previousRank >= rank)
					{
						throw new IllegalArgumentException("LexEntry " + lexEntryElement.getAttribute(XmlNames.ID_ATTR) + " " + " previous=" + previousRank + " current=" + rank);
					}
					previousRank = rank;

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
							String cause = e.getCause().getMessage();
							int count = incompats.computeIfAbsent(cause, (c) -> 0) + 1;
							incompats.put(cause, count);
							continue;
						}
						catch (IllegalArgumentException e)
						{
							String cause = e.getClass().getName() + ' ' + e.getMessage();
							System.err.printf("Illegal relation %s id=%s%n", cause, synsetElement.getAttribute("id"));
							throw e;
						}
						data.relationPointers.add(pointer);
					}
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
					String cause = e.getCause().getMessage();
					int count = incompats.computeIfAbsent(cause, (c) -> 0) + 1;
					incompats.put(cause, count);
					continue;
				}
				catch (IllegalArgumentException e)
				{
					String cause = e.getClass().getName() + ' ' + e.getMessage();
					System.err.printf("Illegal relation %s id=%s%n", cause, type);
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

			String ptrs = Formatter.joinNum(data.relationPointers, "%d", String::toString);
			String ofs = Formatter.join(data.synsetIds, ' ', false, s -> String.format("%08d", offsets.get(s)));
			String line = String.format(WORD_FORMAT, key, data.getPos(), nSenses, ptrs, nSenses, 0, ofs);
			ps.println(line);
			count++;
		}

		// report incompats
		if (incompats.size() > 0)
		{
			for (Map.Entry<String, Integer> entry : incompats.entrySet())
			{
				System.err.printf("Incompatibilities '%s': %d%n", entry.getKey(), entry.getValue());
			}
		}
		System.err.println("Words: " + count + '/' + n + " lexentries for " + xpath);
	}
}
