package org.ewn.grind;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.ewn.grind.Data.Relation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class computes file offsets that serve as synset id in the WNDB format. It does so by iterating over synset elements and yielding a dummy line string of
 * the same length as the final string. The offset counter is moved by the line's length.
 *
 * @author Bernard Bou
 */
public class OffsetFactory extends SynsetProcessor
{
	/**
	 * Constructor
	 *
	 * @param doc W3C document
	 * @param sensesBySynsetId map of senses with key=synsetId
	 * @param sensesById sense elements mapped by id
	 */
	public OffsetFactory(Document doc, Map<String, List<Element>> sensesBySynsetId, Map<String, Element> synsetsById, Map<String, Element> sensesById)
	{
		super(doc, sensesBySynsetId, synsetsById, sensesById, s -> 0L /* dummy synset */);
	}

	/**
	 * Compute synset offsets
	 *
	 * @param xpath selection of synset elements
	 * @param offsets result map
	 * @throws XPathExpressionException xpath
	 */
	public void compute(String xpath, Map<String, Long> offsets) throws XPathExpressionException
	{
		long offset = Formatter.PRINCETON_HEADER.length();

		// iterate synset elements
		NodeList synsetNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = synsetNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node synsetNode = synsetNodes.item(i);
			assert synsetNode.getNodeType() == Node.ELEMENT_NODE;
			Element synsetElement = (Element) synsetNode;
			String id = synsetElement.getAttribute(XmlNames.ID_ATTR);

			String data = getData(synsetElement, dummyOfs);
			offsets.put(id, offset);

			offset += data.length();
		}
		System.err.println("Computed offsets for " + xpath);
	}

	/**
	 * Compute offsets mapped by synsetId
	 *
	 * @return map of offsets by synsetId
	 * @throws XPathExpressionException xpath
	 */
	Map<String, Long> compute() throws XPathExpressionException
	{
		Map<String, Long> offsets = new HashMap<>();
		compute(SynsetProcessor.NOUN_SYNSET_XPATH, offsets);
		compute(SynsetProcessor.VERB_SYNSET_XPATH, offsets);
		compute(SynsetProcessor.ADJ_SYNSET_XPATH, offsets);
		compute(SynsetProcessor.ADV_SYNSET_XPATH, offsets);
		return offsets;
	}

	// I M P L E M E N T A T I O N

	private final long dummyOfs = this.offsetFunction.applyAsLong("");

	private static final int DUMMY_NUM = 0;

	@Override
	protected Relation buildLexRelation(String type, char pos, int lemmaIndex, Element targetSenseElement, Element targetSynsetElement, String targetSynsetId)
			throws CompatException
	{
		char targetPos = targetSynsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);
		return new Relation(type, pos, targetPos, dummyOfs, DUMMY_NUM, DUMMY_NUM);
	}

	@Override
	protected int buildLexfileNum(Element synsetElement)
	{
		return DUMMY_NUM;
	}
}
