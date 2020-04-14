package org.ewn.grind;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.ToLongFunction;

import org.ewn.grind.Data.AdjWord;
import org.ewn.grind.Data.Frame;
import org.ewn.grind.Data.Relation;
import org.ewn.grind.Data.Word;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This abstract class iterates over the synset elements to produce a line of data. The real classes implement some functions differently.
 *
 * @author Bernard Bou
 */
public abstract class SynsetProcessor
{
	/**
	 * Format in data file
	 */
	public static final String SYNSET_FORMAT = "%08d %02d %c %s %s%s | %s%s\n";
	// offset
	// lexfilenum
	// pos
	// members
	// relations
	// frames
	// definition
	// example

	/**
	 * XPath for noun synset elements
	 */
	protected static final String NOUN_SYNSET_XPATH = String.format("/%s/%s/%s[@%s='n']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.SYNSET_TAG, XmlNames.POS_ATTR);

	/**
	 * XPath for verb synset elements
	 */
	protected static final String VERB_SYNSET_XPATH = String.format("/%s/%s/%s[@%s='v']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.SYNSET_TAG, XmlNames.POS_ATTR);

	/**
	 * XPath for adj synset elements
	 */
	protected static final String ADJ_SYNSET_XPATH = String.format("/%s/%s/%s[@%s='a' or @%s='s']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.SYNSET_TAG, XmlNames.POS_ATTR, XmlNames.POS_ATTR);

	/**
	 * XPath for adv synset elements
	 */
	protected static final String ADV_SYNSET_XPATH = String.format("/%s/%s/%s[@%s='r']", //
			XmlNames.LEXICALRESOURCE_TAG, XmlNames.LEXICON_TAG, XmlNames.SYNSET_TAG, XmlNames.POS_ATTR);

	/**
	 * W3C document
	 */
	protected final Document doc;

	/**
	 * Map of sense elements by synsetid
	 */
	protected final Map<String, List<Element>> sensesBySynsetId;

	/**
	 * Synset elements mapped by id
	 */
	protected final Map<String, Element> synsetsById;

	/**
	 * Sense elements mapped by id
	 */
	protected final Map<String, Element> sensesById;

	/**
	 * Function that, when applied to a synsetId, yields the synset offset in the data files. May be dummy constant function.
	 */
	protected final ToLongFunction<String> offsetFunction;

	/**
	 * Report incompatibility counts (indexed by cause)
	 */
	protected final Map<String, Integer> incompats;

	/**
	 * Constructor
	 *
	 * @param doc W3C document
	 * @param sensesBySynsetId map of sense elements indexed with key=synsetId
	 * @param synsetsById synset elements mapped by id
	 * @param sensesById sense elements mapped by id
	 * @param offsetFunction function that, when applied to a synsetId, yields the synset offset in the data files. May be dummy constant function.
	 */
	public SynsetProcessor(Document doc, Map<String, List<Element>> sensesBySynsetId, Map<String, Element> synsetsById, Map<String, Element> sensesById, ToLongFunction<String> offsetFunction)
	{
		this.doc = doc;
		this.sensesBySynsetId = sensesBySynsetId;
		this.synsetsById = synsetsById;
		this.sensesById = sensesById;
		this.offsetFunction = offsetFunction;
		this.incompats = new HashMap<>();
	}

	/**
	 * Get data and yield line
	 *
	 * @param synsetElement synset element
	 * @param offset allocated offset for the synset
	 * @return line
	 */
	protected String getData(Element synsetElement, long offset)
	{
		// init
		List<Relation> relations = new ArrayList<>();
		List<String> lemmas = new ArrayList<>();
		List<Word> words = new ArrayList<>();
		Map<Integer, List<Frame>> frames = new HashMap<>();

		// attribute data
		String synsetId = synsetElement.getAttribute(XmlNames.ID_ATTR);
		char pos = synsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);

		// definition and examples
		// Element definitionElement = XmlUtils.getUniqueChildElement(synsetElement, XmlNames.DEFINITION_TAG);
		// allow multiple definitions
		// Element definitionElement = XmlUtils.getFirstChildElement(synsetElement, XmlNames.DEFINITION_TAG);
		// and join them
		List<Element> definitionElements = XmlUtils.getChildElements(synsetElement, XmlNames.DEFINITION_TAG);
		List<Element> exampleElements = XmlUtils.getChildElements(synsetElement, XmlNames.EXAMPLE_TAG);

		// lexfile num
		int lexfilenum = buildLexfileNum(synsetElement);

		// synset relations
		NodeList semRelationNodes = synsetElement.getElementsByTagName(XmlNames.SYNSETRELATION_TAG);
		int nSem = semRelationNodes.getLength();
		for (int r = 0; r < nSem; r++)
		{
			Node semRelationNode = semRelationNodes.item(r);
			assert semRelationNode.getNodeType() == Node.ELEMENT_NODE;
			Element semRelationElement = (Element) semRelationNode;
			String type = semRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);
			String targetSynsetId = semRelationElement.getAttribute(XmlNames.TARGET_ATTR);
			Element targetSynsetElement = synsetsById.get(targetSynsetId);

			long targetOffset = this.offsetFunction.applyAsLong(targetSynsetId);
			char targetPos = targetSynsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);
			Relation relation;
			try
			{
				relation = new Relation(type, pos, targetPos, targetOffset, 0, 0);
			}
			catch (CompatException e)
			{
				String cause = e.getCause().getMessage();
				int count = this.incompats.computeIfAbsent(cause, (c) -> 0) + 1;
				this.incompats.put(cause, count);
				continue;
			}
			catch (IllegalArgumentException e)
			{
				String cause = e.getMessage();
				System.err.printf("Illegal relation %s id=%s offset=%d%n", cause, synsetElement.getAttribute("id"), offset);
				continue;
			}
			relations.add(relation);
		}

		// iterate sense elements that have this synset element as target in "synset" attribute
		List<Element> senseElements = sensesBySynsetId.get(synsetId);
		assert !senseElements.isEmpty();
		for (Element senseElement : senseElements)
		{
			// lexid attribute
			String orderAttr = senseElement.getAttribute(XmlNames.ORDER_ATTR);
			int order = orderAttr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(orderAttr);

			// lexid attribute
			int lexid = Integer.parseInt(senseElement.getAttribute(XmlNames.LEXID_ATTR));
			String adjPosition = senseElement.getAttribute(XmlNames.ADJPOSITION_ATTR);

			// lexical entry element
			Node lexEntryNode = senseElement.getParentNode();
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;

			// lemma element
			Element lemmaElement = XmlUtils.getUniqueChildElement(lexEntryElement, XmlNames.LEMMA_TAG);
			assert lemmaElement != null;
			String lemma = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
			lemmas.add(lemma);
			int lemmaIndex = lemmas.indexOf(lemma) + 1;
			String escaped = Formatter.escape(lemma);
			Word word = adjPosition.isEmpty() ? new Word(escaped, lexid, order) : new AdjWord(escaped, lexid, order, adjPosition);
			words.add(word);

			// verb frames attribute
			String syntacticBehaviour = senseElement.getAttribute(XmlNames.VERBFRAMES_ATTR);
			if (!syntacticBehaviour.isEmpty())
			{
				String[] syntacticBehaviours = syntacticBehaviour.split("\\s+");
				for (String syntacticBehaviourId : syntacticBehaviours)
				{
					String frameNum = syntacticBehaviourId.substring(7);
					Frame frame = new Frame(Integer.parseInt(frameNum), lemmaIndex);
					List<Frame> frames2 = frames.computeIfAbsent(frame.frameNum, k -> new ArrayList<>());
					frames2.add(frame);
				}
			}

			// sense relations
			NodeList lexRelationNodes = senseElement.getElementsByTagName(XmlNames.SENSERELATION_TAG);
			int nLex = lexRelationNodes.getLength();
			for (int r = 0; r < nLex; r++)
			{
				Node lexRelationNode = lexRelationNodes.item(r);
				assert lexRelationNode.getNodeType() == Node.ELEMENT_NODE;
				Element lexRelationElement = (Element) lexRelationNode;
				String type = lexRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);

				String targetSenseId = lexRelationElement.getAttribute(XmlNames.TARGET_ATTR);
				Element targetSenseElement = sensesById.get(targetSenseId);
				String targetSynsetId = targetSenseElement.getAttribute(XmlNames.SYNSET_ATTR);
				Element targetSynsetElement = synsetsById.get(targetSynsetId);

				Relation relation;
				try
				{
					relation = buildLexRelation(type, pos, lemmaIndex, targetSenseElement, targetSynsetElement, targetSynsetId);
				}
				catch (CompatException e)
				{
					String cause = e.getCause().getMessage();
					int count = this.incompats.computeIfAbsent(cause, (c) -> 0) + 1;
					this.incompats.put(cause, count);
					continue;
				}
				catch (IllegalArgumentException e)
				{
					String cause = e.getMessage();
					System.err.printf("Illegal relation %s id=%s offset=%d%n", cause, synsetElement.getAttribute("id"), offset);
					continue;
				}
				relations.add(relation);
			}
		}

		// assemble
		words.sort(Comparator.comparingInt((Word w) -> w.order));
		String members = Formatter.joinNum(words, "%02x");
		String related = Formatter.joinNum(relations, "%03d");
		String verbframes = frames.size() < 1 ? "" : ' ' + joinFrames(frames, words.size());
		assert definitionElements != null;
		String definition = Formatter.join(definitionElements, "; ", false, Element::getTextContent);
		String examples = exampleElements == null || exampleElements.isEmpty() ? "" : "; " + Formatter.join(exampleElements, ' ', false, Element::getTextContent);
		return String.format(SYNSET_FORMAT, offset, lexfilenum, pos, members, related, verbframes, definition, examples);
	}

	/**
	 * Collect lemmas that are member of this synset
	 *
	 * @param synsetElement synset element
	 * @param sensesBySynsetId senses by synsetId
	 * @return array of lemmas
	 */
	public static List<String> buildLemmas(Element synsetElement, Map<String, List<Element>> sensesBySynsetId)
	{
		ArrayList<String> lemmas = new ArrayList<>();
		String synsetId = synsetElement.getAttribute(XmlNames.ID_ATTR);
		List<Element> senseElements = sensesBySynsetId.get(synsetId);
		assert !senseElements.isEmpty();
		for (Element senseElement : senseElements)
		{
			Node lexEntryNode = senseElement.getParentNode();
			assert lexEntryNode.getNodeType() == Node.ELEMENT_NODE;
			Element lexEntryElement = (Element) lexEntryNode;
			Element lemmaElement = XmlUtils.getUniqueChildElement(lexEntryElement, XmlNames.LEMMA_TAG);
			assert lemmaElement != null;
			String lemma = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
			lemmas.add(lemma);
		}
		assert !lemmas.isEmpty();
		return lemmas;
	}

	/**
	 * Build relation
	 *
	 * @param type relation type
	 * @param pos part of speech
	 * @param lemmaIndex lemmaIndex
	 * @param targetSenseElement target sense element
	 * @param targetSynsetElement target synset element
	 * @param targetSynsetId target synsetid
	 * @return relation
	 * @throws CompatException when relation is not legacy compatible
	 */
	protected Relation buildLexRelation(String type, char pos, int lemmaIndex, Element targetSenseElement, Element targetSynsetElement, String targetSynsetId) throws CompatException
	{
		Node targetLexEntryNode = targetSenseElement.getParentNode();
		assert targetLexEntryNode.getNodeType() == Node.ELEMENT_NODE;
		Element targetLexEntryElement = (Element) targetLexEntryNode;
		Element targetLemmaElement = XmlUtils.getUniqueChildElement(targetLexEntryElement, XmlNames.LEMMA_TAG);
		assert targetLemmaElement != null;
		String targetLemma = targetLemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);

		List<String> targetLemmas = buildLemmas(targetSynsetElement, sensesBySynsetId);
		char targetPos = targetSynsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);
		long targetOffset = this.offsetFunction.applyAsLong(targetSynsetId);
		int targetWordNum = targetLemmas.indexOf(targetLemma) + 1;
		return new Relation(type, pos, targetPos, targetOffset, lemmaIndex, targetWordNum);
	}

	/**
	 * Build lexfile num (result does not matter in terms of output format length)
	 *
	 * @param synsetElement synset element
	 * @return lexfile num
	 */
	protected int buildLexfileNum(Element synsetElement)
	{
		String lexfile = synsetElement.getAttributeNS(XmlNames.NS_DC, XmlNames.LEXFILE_ATTR);
		return Coder.codeLexFile(lexfile);
	}

	/**
	 * Join frames, if a frame applies to all words, then wordCount is zeroed
	 *
	 * @param frames list of frames mapped per given frameNum
	 * @param wordCount word count in synset
	 * @return formatted verb frames
	 */
	private String joinFrames(Map<Integer, List<Frame>> frames, int wordCount)
	{
		List<Frame> resultFrames = new ArrayList<>();
		for (Entry<Integer, List<Frame>> entry : frames.entrySet())
		{
			Integer frameNum = entry.getKey();
			List<Frame> framesWithFrameNum = entry.getValue();
			if (framesWithFrameNum.size() == wordCount)
				resultFrames.add(new Frame(frameNum, 0));
			else
				resultFrames.addAll(framesWithFrameNum);
		}
		return Formatter.joinNum(resultFrames, "%02d");
	}

	/**
	 * Report
	 */
	public void report()
	{
		if (this.incompats.size() > 0)
		{
			for (Map.Entry<String, Integer> entry : incompats.entrySet())
			{
				System.err.printf("Incompatibilities '%s': %d%n", entry.getKey(), entry.getValue());
			}
			this.incompats.clear();
		}
	}
}
