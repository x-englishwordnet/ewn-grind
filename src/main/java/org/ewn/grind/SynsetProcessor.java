package org.ewn.grind;

import org.ewn.grind.Data.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.function.ToLongFunction;

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
	 * Log error flag (avoid duplicate messages)
	 *
	 * @return whether to log errors
	 */
	@SuppressWarnings("SameReturnValue")
	protected abstract boolean log();

	/**
	 * Constructor
	 *
	 * @param doc              W3C document
	 * @param sensesBySynsetId map of sense elements indexed with key=synsetId
	 * @param synsetsById      synset elements mapped by id
	 * @param sensesById       sense elements mapped by id
	 * @param offsetFunction   function that, when applied to a synsetId, yields the synset offset in the data files. May be dummy constant function.
	 */
	public SynsetProcessor(Document doc, Map<String, List<Element>> sensesBySynsetId, Map<String, Element> synsetsById, Map<String, Element> sensesById,
			ToLongFunction<String> offsetFunction)
	{
		this.doc = doc;
		this.sensesBySynsetId = sensesBySynsetId;
		this.synsetsById = synsetsById;
		this.sensesById = sensesById;
		this.offsetFunction = offsetFunction;
		this.incompats = new HashMap<>();
	}

	/**
	 * Intermediate class to detect duplicates
	 */
	static class XMLRelation implements Comparable<XMLRelation>
	{
		public final boolean isSenseRelation;
		public final String relType;
		public final String target;

		XMLRelation(boolean isSenseRelation, String relType, String target)
		{
			this.isSenseRelation = isSenseRelation;
			this.relType = relType;
			this.target = target;
		}

		// identity

		@Override public boolean equals(Object other)
		{
			if (this == other)
				return true;
			if (other == null || getClass() != other.getClass())
				return false;
			XMLRelation that = (XMLRelation) other;
			return isSenseRelation == that.isSenseRelation && Objects.equals(relType, that.relType) && Objects.equals(target, that.target);
		}

		@Override public int hashCode()
		{
			return Objects.hash(isSenseRelation, relType, target);
		}

		// order

		@Override public int compareTo(XMLRelation other)
		{
			int c = Boolean.compare(this.isSenseRelation, other.isSenseRelation);
			if (c != 0)
				return c;
			c = this.relType.compareTo(other.relType);
			if (c != 0)
				return c;
			return this.relType.compareTo(other.relType);
		}

		// string

		@Override public String toString()
		{
			return (this.isSenseRelation ? "SenseRelation" : "SynsetRelation") + " relType=" + this.relType + " target=" + this.target;
		}
	}

	private Members buildMembers(List<Element> senseElements)
	{
		Members members = new Members();
		assert !senseElements.isEmpty();
		for (Element senseElement : senseElements)
		{
			Member member = buildMember(senseElement);
			members.add(member);
		}
		assert senseElements.size() == members.size();
		return members;
	}

	private Member buildMember(Element senseElement)
	{
		// order attribute
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

		String escaped = Formatter.escape(lemma);
		return adjPosition.isEmpty() ? new Member(escaped, lexid, order) : new AdjMember(escaped, lexid, order, adjPosition);
	}

	/**
	 * Get data and yield line
	 *
	 * @param synsetElement synset element
	 * @param offset        allocated offset for the synset
	 * @return line
	 */
	protected String getData(Element synsetElement, long offset)
	{
		// init
		List<Relation> relations = new ArrayList<>();
		Frames frames = new Frames();

		// attribute data
		String synsetId = synsetElement.getAttribute(XmlNames.ID_ATTR);
		char pos = synsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);

		// senses
		List<Element> senseElements = sensesBySynsetId.get(synsetId);
		assert !senseElements.isEmpty();

		// build members ordered set
		Members members = buildMembers(senseElements);

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
		Set<XMLRelation> xmlSemRelationSet = new LinkedHashSet<>();
		for (int r = 0; r < nSem; r++)
		{
			Node semRelationNode = semRelationNodes.item(r);
			assert semRelationNode.getNodeType() == Node.ELEMENT_NODE;
			Element semRelationElement = (Element) semRelationNode;
			String type = semRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);
			String targetSynsetId = semRelationElement.getAttribute(XmlNames.TARGET_ATTR);
			XMLRelation xmlRelation = new XMLRelation(false, type, targetSynsetId);
			boolean wasThere = !xmlSemRelationSet.add(xmlRelation);
			if (wasThere && log())
			{
				System.err.printf("[W] Synset %s duplicate %s%n", synsetElement.getAttribute(XmlNames.ID_ATTR), xmlRelation);
			}
		}
		for (XMLRelation xmlRelation : xmlSemRelationSet)
		{
			Element targetSynsetElement = synsetsById.get(xmlRelation.target);

			long targetOffset = this.offsetFunction.applyAsLong(xmlRelation.target);
			char targetPos = targetSynsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);
			Relation relation;
			try
			{
				relation = new Relation(xmlRelation.relType, pos, targetPos, targetOffset, 0, 0);
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
				if (log())
				{
					String cause = e.getClass().getName() + ' ' + e.getMessage();
					System.err.printf("Illegal relation %s id=%s offset=%d%n", cause, synsetElement.getAttribute("id"), offset);
				}
				throw e;
			}
			relations.add(relation);
		}

		// iterate sense elements that have this synset element as target in "synset" attribute
		for (Element senseElement : senseElements)
		{
			// member
			Member member = buildMember(senseElement);
			int memberIndex = members.indexOf(member) + 1;

			// verb frames attribute
			String syntacticBehaviour = senseElement.getAttribute(XmlNames.VERBFRAMES_ATTR);
			if (!syntacticBehaviour.isEmpty())
			{
				String[] syntacticBehaviours = syntacticBehaviour.split("\\s+");
				for (String syntacticBehaviourId : syntacticBehaviours)
				{
					//String frameNum = syntacticBehaviourId.substring(7);
					//Frame frame = new Frame(Integer.parseInt(frameNum), memberIndex);
					
					Frame frame = new Frame(Coder.codeFrameId(syntacticBehaviourId), memberIndex);
					frames.add(frame);
				}
			}

			// sense relations
			NodeList lexRelationNodes = senseElement.getElementsByTagName(XmlNames.SENSERELATION_TAG);
			int nLex = lexRelationNodes.getLength();
			Set<XMLRelation> xmlLexRelationSet = new LinkedHashSet<>();
			for (int r = 0; r < nLex; r++)
			{
				Node lexRelationNode = lexRelationNodes.item(r);
				assert lexRelationNode.getNodeType() == Node.ELEMENT_NODE;
				Element lexRelationElement = (Element) lexRelationNode;
				String type = lexRelationElement.getAttribute(XmlNames.RELTYPE_ATTR);
				String targetSenseId = lexRelationElement.getAttribute(XmlNames.TARGET_ATTR);
				XMLRelation xmlRelation = new XMLRelation(true, type, targetSenseId);
				boolean wasThere = !xmlLexRelationSet.add(xmlRelation);
				if (wasThere && log())
				{
					System.err.printf("[W] Sense %s duplicate %s%n", senseElement.getAttribute(XmlNames.ID_ATTR), xmlRelation);
				}
			}
			for (XMLRelation xmlRelation : xmlLexRelationSet)
			{
				Element targetSenseElement = sensesById.get(xmlRelation.target);
				String targetSynsetId = targetSenseElement.getAttribute(XmlNames.SYNSET_ATTR);
				Element targetSynsetElement = synsetsById.get(targetSynsetId);

				Relation relation;
				try
				{
					relation = buildLexRelation(xmlRelation.relType, pos, memberIndex, targetSenseElement, targetSynsetElement, targetSynsetId);
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
					String cause = e.getClass().getName() + ' ' + e.getMessage();
					System.err.printf("Illegal relation %s id=%s offset=%d%n", cause, synsetElement.getAttribute("id"), offset);
					//throw e;
					continue;
				}
				relations.add(relation);
			}
		}

		// assemble
		String membersData = members.toWndbString();
		String relatedData = Formatter.joinNum(relations, "%03d", Relation::toWndbString);
		String verbframesData = frames.toWndbString(pos, members.size());
		if (!verbframesData.isEmpty())
			verbframesData = ' ' + verbframesData;
		assert definitionElements != null;
		String definitionsData = Formatter.join(definitionElements, "; ", false, Element::getTextContent);
		String examplesData =
				exampleElements == null || exampleElements.isEmpty() ? "" : "; " + Formatter.join(exampleElements, ' ', false, Element::getTextContent);
		return String.format(SYNSET_FORMAT, offset, lexfilenum, pos, membersData, relatedData, verbframesData, definitionsData, examplesData);
	}

	/**
	 * Collect lemmas that are member of this synset
	 *
	 * @param synsetElement    synset element
	 * @param sensesBySynsetId senses by synsetId
	 * @return ordered set of lemma members
	 */
	public static Members buildMembers(Element synsetElement, Map<String, List<Element>> sensesBySynsetId)
	{
		Members members = new Members();
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

			int lexid = Integer.parseInt(senseElement.getAttribute(XmlNames.LEXID_ATTR));
			int order = Integer.parseInt(senseElement.getAttribute(XmlNames.ORDER_ATTR));
			String lemma = lemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
			Member member = new Member(lemma, lexid, order);
			members.add(member);
		}
		assert !members.isEmpty();
		return members;
	}

	/**
	 * Build relation
	 *
	 * @param type                relation type
	 * @param pos                 part of speech
	 * @param lemmaIndex          lemmaIndex
	 * @param targetSenseElement  target sense element
	 * @param targetSynsetElement target synset element
	 * @param targetSynsetId      target synsetid
	 * @return relation
	 * @throws CompatException when relation is not legacy compatible
	 */
	protected Relation buildLexRelation(String type, char pos, int lemmaIndex, Element targetSenseElement, Element targetSynsetElement, String targetSynsetId)
			throws CompatException
	{
		// target synset members
		Members targetMembers = buildMembers(targetSynsetElement, sensesBySynsetId);

		// target synset member
		Node targetLexEntryNode = targetSenseElement.getParentNode();
		assert targetLexEntryNode.getNodeType() == Node.ELEMENT_NODE;
		Element targetLexEntryElement = (Element) targetLexEntryNode;
		Element targetLemmaElement = XmlUtils.getUniqueChildElement(targetLexEntryElement, XmlNames.LEMMA_TAG);
		assert targetLemmaElement != null;
		int targetLexid = Integer.parseInt(targetSenseElement.getAttribute(XmlNames.LEXID_ATTR));
		int targetOrder = Integer.parseInt(targetSenseElement.getAttribute(XmlNames.ORDER_ATTR));
		String targetLemma = targetLemmaElement.getAttribute(XmlNames.WRITTENFORM_ATTR);
		Member targetMember = new Member(targetLemma, targetLexid, targetOrder);

		// which
		int targetMemberNum = targetMembers.indexOf(targetMember);
		char targetPos = targetSynsetElement.getAttribute(XmlNames.POS_ATTR).charAt(0);
		long targetOffset = this.offsetFunction.applyAsLong(targetSynsetId);
		return new Relation(type, pos, targetPos, targetOffset, lemmaIndex, targetMemberNum + 1);
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
