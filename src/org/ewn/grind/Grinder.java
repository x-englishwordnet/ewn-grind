package org.ewn.grind;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.ewn.grind.Memory.Unit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 * 
 * @see https://wordnet.princeton.edu/documentation/wndb5wn
 * @author Bernard Bou
 */
public class Grinder
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

		// Output
		File dir;
		if (args.length > 1)
		{
			dir = new File(args[1]);
			if (!dir.exists())
				dir.mkdirs();
		}
		else
		{
			dir = new File(".");
		}
		System.err.println("Output " + dir.getAbsolutePath());

		// XML document
		Document doc = XmlUtils.getDocument(filename, false);

		// Maps
		Map<String, List<Element>> sensesBySynsetId = XmlUtils.makeElementMultiMap(doc, XmlNames.SENSE_TAG, XmlNames.SYNSET_ATTR);
		Map<String, Element> synsetsById = XmlUtils.makeElementMap(doc, XmlNames.SYNSET_TAG, XmlNames.ID_ATTR);
		Map<String, Element> sensesById = XmlUtils.makeElementMap(doc, XmlNames.SENSE_TAG, XmlNames.ID_ATTR);

		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(doc, sensesBySynsetId, synsetsById, sensesById).compute();

		// Heap
		System.err.println(Memory.heapInfo("after maps", Unit.M));

		// Process
		data(dir, doc, sensesBySynsetId, synsetsById, sensesById, offsets);
		indexWords(dir, doc, synsetsById, offsets);
		indexSenses(dir, doc, offsets);
		morphs(dir, doc);
		templates(dir, doc);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	/**
	 * Grind data.{noun|verb|adj|adv}
	 * 
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @param sensesBySynsetId sense elements mapped by synsetId (whose 'synset' attribute = synsetId)
	 * @param synsetsById synset elements mapped by synsetId
	 * @param sensesById sense elements mapped by synsetId
	 * @param offsets offsets mapped by synsetId
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void data(File dir, Document doc, //
			Map<String, List<Element>> sensesBySynsetId, //
			Map<String, Element> synsetsById, //
			Map<String, Element> sensesById, //
			Map<String, Long> offsets //
	) throws IOException, XPathExpressionException
	{
		// Data
		DataGrinder grinder = new DataGrinder(doc, sensesBySynsetId, synsetsById, sensesById, offsets);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.noun"))))
		{
			grinder.makeData(ps, SynsetProcessor.NOUN_SYNSET_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.verb"))))
		{
			grinder.makeData(ps, SynsetProcessor.VERB_SYNSET_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adj"))))
		{
			grinder.makeData(ps, SynsetProcessor.ADJ_SYNSET_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adv"))))
		{
			grinder.makeData(ps, SynsetProcessor.ADV_SYNSET_XPATH);
		}
	}

	/**
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @param synsetsById synset elements mapped by synsetId
	 * @param offsets offsets mapped by synsetId
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void indexWords(File dir, Document doc, //
			Map<String, Element> synsetsById, //
			Map<String, Long> offsets //
	) throws IOException, XPathExpressionException
	{
		// Index
		WordIndexer indexer = new WordIndexer(doc, synsetsById, offsets);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.noun"))))
		{
			indexer.makeIndex(ps, WordIndexer.NOUN_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.verb"))))
		{
			indexer.makeIndex(ps, WordIndexer.VERB_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adj"))))
		{
			indexer.makeIndex(ps, WordIndexer.ADJ_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adv"))))
		{
			indexer.makeIndex(ps, WordIndexer.ADV_LEXENTRIES_XPATH);
		}
	}

	/**
	 * Grind index.sense
	 * 
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @param offsets offsets mapped by synsetId
	 * @throws IOException
	 */
	public static void indexSenses(File dir, Document doc, //
			Map<String, Long> offsets) throws IOException
	{
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense"))))
		{
			new SenseIndexer(doc, offsets).makeIndex(ps);
		}
	}

	/**
	 * Grind {noun|verb|adj|adv}.exc
	 * 
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void morphs(File dir, Document doc) throws IOException, XPathExpressionException
	{
		MorphGrinder grinder = new MorphGrinder(doc);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "noun.exc"))))
		{
			grinder.makeMorph(ps, MorphGrinder.NOUN_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "verb.exc"))))
		{
			grinder.makeMorph(ps, MorphGrinder.VERB_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adj.exc"))))
		{
			grinder.makeMorph(ps, MorphGrinder.ADJ_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adv.exc"))))
		{
			grinder.makeMorph(ps, MorphGrinder.ADV_LEXENTRIES_XPATH);
		}
	}

	/**
	 * Grind sentidx.vrb
	 * 
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @throws IOException
	 * @throws XPathExpressionException 
	 */
	public static void templates(File dir, Document doc) throws IOException, XPathExpressionException
	{
		TemplateIndexer indexer = new TemplateIndexer(doc);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "sentidx.vrb"))))
		{
			indexer.makeIndex(ps);
		}
	}
}
