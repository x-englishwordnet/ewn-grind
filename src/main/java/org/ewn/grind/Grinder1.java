package org.ewn.grind;

import org.ewn.grind.Memory.Unit;
import org.oewntk.parse.DataParser1;
import org.oewntk.pojos.ParsePojoException;
import org.oewntk.pojos.Synset;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Main class that generates one line of the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grinder1
{
	/**
	 * Main entry point
	 *
	 * @param args command-line arguments ([0] merged XML filename,[1] pos, [2] offset) # 1 input XML file # 2 SYNSETID | -sense | -offset # 3 SENSEID | POS
	 *             (n|v|a|r|s) # 4 OFFSET (ie 1740)
	 * @throws SAXException                 sax
	 * @throws ParserConfigurationException parser configuration
	 * @throws IOException                  io
	 * @throws XPathExpressionException     xpath
	 */
	public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, XPathExpressionException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		System.err.println(Memory.heapInfo("before maps", Unit.M));

		// Input
		String filename = args[0];
		String extraArg1 = args[1];
		boolean isOffset = extraArg1.equals("-offset");
		boolean isSense = extraArg1.equals("-sense");
		String extraArg2 = isOffset || isSense ? args[2] : null;
		String extraArg3 = isOffset ? args[3] : null;

		// XML document
		Document doc = XmlUtils.getDocument(filename, false);

		// Maps
		Map<String, List<Element>> sensesBySynsetId = XmlUtils.makeElementMultiMap(doc, XmlNames.SENSE_TAG, XmlNames.SYNSET_ATTR);
		Map<String, Element> synsetsById = XmlUtils.makeElementMap(doc, XmlNames.SYNSET_TAG, XmlNames.ID_ATTR);
		Map<String, Element> sensesById = XmlUtils.makeElementMap(doc, XmlNames.SENSE_TAG, XmlNames.ID_ATTR);

		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(doc, sensesBySynsetId, synsetsById, sensesById).compute();

		// SynsetId, SenseId, w31 offset
		String synsetId;
		if (isSense)
		{
			Element senseElement = sensesById.get(extraArg2);
			synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
		}
		else if (isOffset)
		{
			char pos = extraArg2.charAt(0);
			long offset31 = Long.parseLong(extraArg3);
			synsetId = String.format("oewn-%08d-%c", offset31, pos);
		}
		else
		{
			synsetId = extraArg1;
		}

		// Heap
		System.err.println(Memory.heapInfo("after maps", Unit.M));

		// Process
		Element synsetElement = synsetsById.get(synsetId);
		long offset = offsets.get(synsetId);
		data(synsetElement, offset, doc, sensesBySynsetId, synsetsById, sensesById, offsets);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	/**
	 * Grind data for this synset
	 *
	 * @param synsetElement    synset element
	 * @param offset           offset
	 * @param doc              parsed XML W3C document
	 * @param sensesBySynsetId sense elements mapped by synsetId (whose 'synset' attribute = synsetId)
	 * @param synsetsById      synset elements mapped by synsetId
	 * @param sensesById       sense elements mapped by synsetId
	 * @param offsets          offsets mapped by synsetId
	 */
	public static void data(Element synsetElement, long offset, Document doc, //
			Map<String, List<Element>> sensesBySynsetId, //
			Map<String, Element> synsetsById, //
			Map<String, Element> sensesById, //
			Map<String, Long> offsets //
	)
	{
		// Data
		DataGrinder factory = new DataGrinder(doc, sensesBySynsetId, synsetsById, sensesById, offsets);
		String line = factory.getData(synsetElement, offset);
		System.out.println(line);
		try
		{
			Synset s = DataParser1.parseSynset(line, false);
			System.out.println(s.toPrettyString());
		}
		catch (ParsePojoException e)
		{
			e.printStackTrace();
		}
	}
}
