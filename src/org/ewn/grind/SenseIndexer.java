package org.ewn.grind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SenseIndexer
{
	// sense_key synset_offset sense_number tag_cnt

	private final Document doc;

	private final Map<String, Long> offsets;

	public SenseIndexer(Document doc, Map<String, Long> offsets)
	{
		super();
		this.doc = doc;
		this.offsets = offsets;
	}

	/**
	 * Make index.sense
	 * 
	 * @param ps print stream
	 */
	public void makeIndex(PrintStream ps)
	{
		ArrayList<String> lines = new ArrayList<>();

		NodeList senseNodes = doc.getElementsByTagName(XmlNames.SENSE_TAG);
		int n = senseNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node senseNode = senseNodes.item(i);
			assert senseNode.getNodeType() == Node.ELEMENT_NODE;
			Element senseElement = (Element) senseNode;

			String sensekey = senseElement.getAttribute("sensekey");
			//String sensekeyLower = sensekey.toLowerCase();
			String synsetId = senseElement.getAttribute("synset");
			String nth = senseElement.getAttribute("n");
			String tagCountAttr = senseElement.getAttribute("tagcnt");
			long offset = offsets.get(synsetId);
			int senseNum = Integer.parseInt(nth);
			int tagCount = 0;
			if (!tagCountAttr.isEmpty())
				tagCount = Integer.parseInt(tagCountAttr);

			String line = String.format("%s %08d %d %d", sensekey, offset, senseNum, tagCount);
			lines.add(line);
			/*
			if (!sensekey.equals(sensekeyLower))
			{
				String line2 = String.format("%s %08d %d %d", sensekeyLower, offset, senseNum, tagCount);
				lines.add(line2);
			}
			*/
		}
		Collections.sort(lines);
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.println("Senses: " + n);
	}
}
