package org.ewn.grind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SenseIndexer
{
	// sense_key synset_offset sense_number tag_cnt

	/**
	 * W3C document
	 */
	private final Document doc;

	/**
	 * Synset offsets map indexed by synsetid key
	 */
	private final Map<String, Long> offsets;

	/**
	 * Constructor
	 *
	 * @param doc     W3C document
	 * @param offsets synset offsets map indexed by synsetid key
	 */
	public SenseIndexer(Document doc, Map<String, Long> offsets)
	{
		super();
		this.doc = doc;
		this.offsets = offsets;
	}

	/**
	 * Make index.sense
	 * Sensekeys are cased.
	 *
	 * @param ps print stream
	 */
	public void makeIndex(PrintStream ps)
	{
		ArrayList<String> lines = new ArrayList<>();

		NodeList senseNodes = doc.getElementsByTagName(XmlNames.SENSE_TAG);
		int n = senseNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node senseNode = senseNodes.item(i);
			assert senseNode.getNodeType() == Node.ELEMENT_NODE;
			Element senseElement = (Element) senseNode;

			String sensekey = senseElement.getAttribute(XmlNames.SENSEKEY_ATTR);
			String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
			String nth = senseElement.getAttribute(XmlNames.N_ATTR);
			String tagCountAttr = senseElement.getAttribute(XmlNames.TAGCOUNT_ATTR);
			long offset = offsets.get(synsetId);
			int senseNum = Integer.parseInt(nth);
			int tagCount = 0;
			if (!tagCountAttr.isEmpty())
				tagCount = Integer.parseInt(tagCountAttr);

			String line = String.format("%s %08d %d %d", sensekey, offset, senseNum, tagCount);
			lines.add(line);
		}
		Collections.sort(lines);
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Senses: " + n);
	}

	/**
	 * Make index.sense (compatible mode)
	 * When the sensekey is cased, two lines are generated, the first with lower-case, the second with cased.
	 *
	 * @param ps print stream
	 */
	public void makeIndexCompat(PrintStream ps)
	{
		ArrayList<String> lines = new ArrayList<>();

		NodeList senseNodes = doc.getElementsByTagName(XmlNames.SENSE_TAG);
		int n = senseNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node senseNode = senseNodes.item(i);
			assert senseNode.getNodeType() == Node.ELEMENT_NODE;
			Element senseElement = (Element) senseNode;

			String sensekey = senseElement.getAttribute(XmlNames.SENSEKEY_ATTR);
			String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
			String nth = senseElement.getAttribute(XmlNames.N_ATTR);
			String tagCountAttr = senseElement.getAttribute(XmlNames.TAGCOUNT_ATTR);
			long offset = offsets.get(synsetId);
			int senseNum = Integer.parseInt(nth);
			int tagCount = 0;
			if (!tagCountAttr.isEmpty())
				tagCount = Integer.parseInt(tagCountAttr);

			// lowercase first
			String sensekeyLower = sensekey.toLowerCase();
			String line = String.format("%s %08d %d %d", sensekeyLower, offset, senseNum, tagCount);
			lines.add(line);

			// cased second if needed
			if (!sensekey.equals(sensekeyLower))
			{
				String line2 = String.format("%s %08d %d %d", sensekey, offset, senseNum, tagCount);
				lines.add(line2);
			}
		}
		Collections.sort(lines);
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Senses: " + n);
	}

	/**
	 * Make index.sense (legacy mode)
	 * Uses EWN sensekeys (dc:identifier attr).
	 *
	 * @param ps print stream
	 */
	public void makeIndexLegacy(PrintStream ps)
	{
		ArrayList<String> lines = new ArrayList<>();

		NodeList senseNodes = doc.getElementsByTagName(XmlNames.SENSE_TAG);
		int n = senseNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node senseNode = senseNodes.item(i);
			assert senseNode.getNodeType() == Node.ELEMENT_NODE;
			Element senseElement = (Element) senseNode;

			String sensekey = senseElement.getAttributeNS(XmlNames.NS_DC, XmlNames.SENSEKEY_LEGACY_ATTR);
			String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
			String nth = senseElement.getAttribute(XmlNames.N_ATTR);
			String tagCountAttr = senseElement.getAttribute(XmlNames.TAGCOUNT_ATTR);
			long offset = offsets.get(synsetId);
			int senseNum = Integer.parseInt(nth);
			int tagCount = 0;
			if (!tagCountAttr.isEmpty())
				tagCount = Integer.parseInt(tagCountAttr);

			// lowercase first
			String sensekeyLower = sensekey.toLowerCase();
			String line = String.format("%s %08d %d %d", sensekeyLower, offset, senseNum, tagCount);
			lines.add(line);
		}
		Collections.sort(lines);
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Senses: " + n);
	}
}
