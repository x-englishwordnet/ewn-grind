package org.ewn.grind;

import org.ewn.grind.Memory.Unit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grinder
{
	/**
	 * Main entry point
	 *
	 * @param args command-line arguments [-compat:lexid] [-compat:pointer] mergedXml [outputDir]
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
		boolean traceHeap = false;
		String traceHeapEnv = System.getenv("TRACEHEAP");
		if (traceHeapEnv != null)
		{
			traceHeap = Boolean.parseBoolean(traceHeapEnv);
		}
		if (traceHeap)
			System.err.println(Memory.heapInfo("before maps", Unit.M));

		// Argument switches processing
		int nArg = args.length; // left
		int iArg = 0; // current

		if (nArg > 0 && "-compat:pointer".equals(args[iArg])) // if left and is "-compat:pointer"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.POINTER_COMPAT = true;
		}

		if (nArg > 0 && "-compat:lexid".equals(args[iArg])) // if left and is "-compat:lexid"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.LEXID_COMPAT = true;
		}
		if (nArg > 0 && "-compat:verbframe".equals(args[iArg])) // if left and is "-compat:verbframe"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.VERBFRAME_COMPAT = true;
		}

		// Input
		String filename = args[iArg];
		nArg--; // left: decrement
		iArg++; // current: move to next

		// Output
		File dir;
		if (nArg > 0) // if left
		{
			dir = new File(args[iArg]);
			// nArg--; // left: decrement
			// iArg++; // current: move to next
			if (!dir.exists())
				// noinspection ResultOfMethodCallIgnored
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
		if (traceHeap)
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
	 * @param dir              output directory
	 * @param doc              parsed XML W3C document
	 * @param sensesBySynsetId sense elements mapped by synsetId (whose 'synset' attribute = synsetId)
	 * @param synsetsById      synset elements mapped by synsetId
	 * @param sensesById       sense elements mapped by synsetId
	 * @param offsets          offsets mapped by synsetId
	 * @throws IOException              io
	 * @throws XPathExpressionException xpath
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
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.noun")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, SynsetProcessor.NOUN_SYNSET_XPATH);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.verb")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, SynsetProcessor.VERB_SYNSET_XPATH);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adj")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, SynsetProcessor.ADJ_SYNSET_XPATH);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adv")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, SynsetProcessor.ADV_SYNSET_XPATH);
			grinder.report();
		}
	}

	/**
	 * @param dir         output directory
	 * @param doc         parsed XML document
	 * @param synsetsById synset elements mapped by synsetId
	 * @param offsets     offsets mapped by synsetId
	 * @throws IOException              io
	 * @throws XPathExpressionException xpath
	 */
	public static void indexWords(File dir, Document doc, //
			Map<String, Element> synsetsById, //
			Map<String, Long> offsets //
	) throws IOException, XPathExpressionException
	{
		// Index
		WordIndexer indexer = new WordIndexer(doc, synsetsById, offsets);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.noun")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, WordIndexer.NOUN_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.verb")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, WordIndexer.VERB_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adj")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, WordIndexer.ADJ_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adv")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, WordIndexer.ADV_LEXENTRIES_XPATH);
		}
	}

	/**
	 * Grind index.sense
	 *
	 * @param dir     output directory
	 * @param doc     parsed XML document
	 * @param offsets offsets mapped by synsetId
	 * @throws IOException io
	 */
	public static void indexSenses(File dir, Document doc, //
			Map<String, Long> offsets) throws IOException
	{
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense")), true, Flags.charSet.name()))
		{
			new SenseIndexer(doc, offsets).makeIndexCased(ps);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense.pools")), true, Flags.charSet.name()))
		{
			new SenseIndexer(doc, offsets).makeIndexLowerMultiValue(ps);
		}
		/*
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense.both")), true, Flags.charSet.name()))
		{
			new SenseIndexer(doc, offsets).makeIndexBoth(ps);
		}
		*/
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense.legacy")), true, Flags.charSet.name()))
		{
			new SenseIndexer(doc, offsets).makeIndexLegacy(ps);
		}
	}

	/**
	 * Grind {noun|verb|adj|adv}.exc
	 *
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @throws IOException              io
	 * @throws XPathExpressionException xpath
	 */
	public static void morphs(File dir, Document doc) throws IOException, XPathExpressionException
	{
		MorphGrinder grinder = new MorphGrinder(doc);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "noun.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, MorphGrinder.NOUN_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "verb.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, MorphGrinder.VERB_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adj.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, MorphGrinder.ADJ_LEXENTRIES_XPATH);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adv.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, MorphGrinder.ADV_LEXENTRIES_XPATH);
		}
	}

	/**
	 * Grind sentidx.vrb
	 *
	 * @param dir output directory
	 * @param doc parsed XML document
	 * @throws IOException              io
	 * @throws XPathExpressionException xpath
	 */
	public static void templates(File dir, Document doc) throws IOException, XPathExpressionException
	{
		TemplateIndexer indexer = new TemplateIndexer(doc);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "sentidx.vrb")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps);
		}
	}
}
