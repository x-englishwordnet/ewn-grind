package org.ewn.grind;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.ewn.grind.Memory.Unit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Main class that generates one line of the WN database in the WNDB format as per wndb(5WN)
 * 
 * @see https://wordnet.princeton.edu/documentation/wndb5wn
 * @author Bernard Bou
 */
public class Grinder1
{
	/**
	 * Main entry point
	 * 
	 * @param args command-line arguments
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, XPathExpressionException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		System.err.println(Memory.heapInfo("before maps", Unit.M));

		// Input
		String filename = args[0];
		char pos = args[1].charAt(0);
		long offset = Long.parseLong(args[2]);

		// XML document
		Document doc = XmlUtils.getDocument(filename, false);

		// Maps
		Map<String, List<Element>> sensesBySynsetId = XmlUtils.makeElementMultiMap(doc, "Sense", "synset");
		Map<String, Element> synsetsById = XmlUtils.makeElementMap(doc, "Synset", "id");
		Map<String, Element> sensesById = XmlUtils.makeElementMap(doc, "Sense", "id");

		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(doc, sensesBySynsetId, synsetsById, sensesById).compute();

		// Heap
		System.err.println(Memory.heapInfo("after maps", Unit.M));

		// Process
		data(pos, offset, doc, sensesBySynsetId, synsetsById, sensesById, offsets);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	/**
	 * Grind data for this synset
	 * 
	 * @param char pos
	 * @param offset offset
	 * @param doc parsed XML W3C document
	 * @param sensesBySynsetId sense elements mapped by synsetId (whose 'synset' attribute = synsetId)
	 * @param synsetsById synset elements mapped by synsetId
	 * @param sensesById sense elements mapped by synsetId
	 * @param offsets offsets mapped by synsetId
	 */
	public static void data(char pos, long offset, Document doc, //
			Map<String, List<Element>> sensesBySynsetId, //
			Map<String, Element> synsetsById, //
			Map<String, Element> sensesById, //
			Map<String, Long> offsets //
	)
	{
		// Data
		DataGrinder factory = new DataGrinder(doc, sensesBySynsetId, synsetsById, sensesById, offsets);
		String id = String.format("ewn-%08d-%c", offset, pos);
		Element synsetElement = synsetsById.get(id);
		String line = factory.getData(synsetElement, offset);
		System.out.println(line);
	}
}
