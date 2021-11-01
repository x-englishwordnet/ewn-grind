package org.ewn.grind;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Tags and attributes in XML files
 *
 * @author Bernard Bou
 * @author bbou
 * @author bbou
 */
public class XmlExtractor
{
	private XmlExtractor()
	{
	}

	static String getSensekey(Element senseElement)
	{
		String id = senseElement.getAttribute(XmlNames.ID_ATTR);
		String sk = id.substring("oewn-".length());
		int b = sk.indexOf("__");

		String lemma = sk.substring(0, b);
		lemma = lemma.replace("-ap-", "'");
		lemma = lemma.replace("-ap-", "'");
		lemma = lemma.replace("-lb-", "(");
		lemma = lemma.replace("-rb-", ")");
		lemma = lemma.replace("-sl-", "/");
		lemma = lemma.replace("-cm-", ",");
		lemma = lemma.replace("-ex-", "!");

		String tail = sk.substring(b + 2);
		tail = tail.replace(".", ":");
		return lemma + '%' + tail;

		// return senseElement.getAttribute(XmlNames.SENSEKEY_ATTR);
	}

	static int getLexid(Element senseElement)
	{
		String id = senseElement.getAttribute(XmlNames.ID_ATTR);
		String sk = id.substring("oewn-".length());
		int b = sk.indexOf("__");
		b += 2 + 5;
		String lexid = sk.substring(b, b + 2);
		return Integer.parseInt(lexid);

		// return Integer.parseInt(senseElement.getAttribute(XmlNames.LEXID_ATTR));
	}

	static String getAdjPosition(Element senseElement)
	{
		return senseElement.getAttribute(XmlNames.ADJPOSITION_ATTR);
	}

	static String getVerbFrames(Element senseElement)
	{
		return senseElement.getAttribute(XmlNames.VERBFRAMES_ATTR);
	}

	static int getOrder(Element senseElement, Map<String, Element> synsetsById)
	{
		Element lexElement = XmlUtils.getParentElement(senseElement);
		String lexId = lexElement.getAttribute(XmlNames.ID_ATTR);
		String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
		Element synsetElement = synsetsById.get(synsetId);
		String membersAttr = synsetElement.getAttribute(XmlNames.MEMBERS_ATTR);
		String[] members = membersAttr.split("\\s+");
		int i = 0;
		for (String member : members)
		{
			if (lexId.equals(member))
				return i;
			i++;
		}
		throw new RuntimeException("[E] member attr not found " + lexId);
	}

	static int getTagCount(Element senseElement, Map<String, Integer> map)
	{
		String sensekey = XmlExtractor.getSensekey(senseElement);
		Integer tagCount = map.get(sensekey);
		if (tagCount == null)
			return 0;
		return tagCount;
	}

	static String getVerbTemplates(Element senseElement, Map<String, int[]> map)
	{
		String sensekey = XmlExtractor.getSensekey(senseElement);
		int[] templateIds = map.get(sensekey);
		if (templateIds == null)
			return "";
		return Formatter.join(templateIds, ' ', "%d");
	}
}
