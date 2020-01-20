package org.ewn.grind;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class produces the data.{noun|verb|adj|adv} files
 * 
 * @author Bernard Bou
 */
public class DataGrinder extends SynsetProcessor
{
	/**
	 * Constructor
	 * 
	 * @param doc document
	 * @param sensesBySynsetId map of senses with key=synsetId
	 * @param synsetsById synset elements mapped by id
	 * @param sensesById sense elements mapped by id
	 * @param offsetMap
	 */
	public DataGrinder(Document doc, Map<String, List<Element>> sensesBySynsetId, Map<String, Element> synsetsById, Map<String, Element> sensesById, Map<String, Long> offsetMap)
	{
		super(doc, sensesBySynsetId, synsetsById, sensesById, offsetMap::get);
	}

	/**
	 * Make data
	 * 
	 * @param ps print stream
	 * @param xpath xpath of selected sense elements
	 * @throws ParserConfigurationException
	 */
	public void makeData(PrintStream ps, String xpath) throws XPathExpressionException
	{
		ps.print(Formatter.PRINCETON_HEADER);
		long offset = Formatter.PRINCETON_HEADER.length();
		Element previous = null;

		// iterate synset elements
		NodeList synsetNodes = XmlUtils.getXPathNodeList(xpath, doc);
		int n = synsetNodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node synsetNode = synsetNodes.item(i);
			assert synsetNode.getNodeType() == Node.ELEMENT_NODE;
			Element synsetElement = (Element) synsetNode;
			String id = synsetElement.getAttribute("id");
			long offset0 = this.offsetFunction.applyAsLong(id);
			if (offset0 != offset)
			{
				String line = getData(previous, 0);
				String line0 = new OffsetFactory(doc, sensesBySynsetId, synsetsById, sensesById).getData(previous, 0);
				throw new RuntimeException("miscomputed offset for " + id + "\n[then]=" + line0 + "[now ]=" + line);
			}

			String line = getData(synsetElement, offset);
			ps.print(line);

			offset += line.length();
			previous = synsetElement;
		}
		System.err.println("Synsets: " + n + " for " + xpath);
	}
}
