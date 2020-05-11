package org.ewn.grind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SenseIndexer
{
	// sense_key synset_offset sense_number tag_cnt

	static final Comparator<String> lexicalComparatorLowerFirst = (s1, s2) -> {
		int c = s1.compareToIgnoreCase(s2);
		if (c != 0)
			return c;
		return -s1.compareTo(s2);
	};

	static final Comparator<String> lexicalComparatorUpperFirst = (s1, s2) -> {
		int c = s1.compareToIgnoreCase(s2);
		if (c != 0)
			return c;
		return s1.compareTo(s2);
	};

	static private class Data
	{
		public final long offset;
		public final int sensenum;
		public final int tagCnt;

		public Data(long offset, int sensenum, int tagCnt)
		{
			this.offset = offset;
			this.sensenum = sensenum;
			this.tagCnt = tagCnt;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Data data = (Data) o;
			return offset == data.offset && sensenum == data.sensenum && tagCnt == data.tagCnt;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(offset, sensenum, tagCnt);
		}
	}

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
	public void makeIndexCased(PrintStream ps)
	{
		SortedSet<String> lines = new TreeSet<>(String::compareTo);

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
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.printf("Senses (cased): %d, %d lines%n", n, lines.size());
	}

	/**
	 * Make index.sense in multi-value mode.
	 * Each key is a lower-cased sensekey and is unique.
	 * The line is extended beyond the first value with extra values.
	 *
	 * @param ps print stream
	 */
	public void makeIndexLowerMultiValue(PrintStream ps)
	{
		System.err.print("Senses (lower-cased,multi): ");
		/*
		// replace LexId with N
		makeIndexLowerMultiValue(ps, (e) -> {
			String sk = e.getAttribute(XmlNames.SENSEKEY_ATTR);
			int p = sk.indexOf('%');
			int q = sk.indexOf(':', p + 1);
			int u = sk.indexOf(':', q + 1);
			int v = sk.indexOf(':', u + 1);
			String head = sk.substring(0, u);
			String tail = sk.substring(v + 1);
			int sensenum = Integer.parseInt(e.getAttribute(XmlNames.N_ATTR));
			return String.format("%s:%02d:%s", head.toLowerCase(), sensenum, tail);
		});
		*/
		makeIndexLowerMultiValue(ps, (e) -> e.getAttribute(XmlNames.SENSEKEY_ATTR));
	}

	/**
	 * Make index.sense (legacy mode)
	 * Uses EWN sensekeys (dc:identifier attr).
	 *
	 * @param ps print stream
	 */
	public void makeIndexLegacy(PrintStream ps)
	{
		System.err.print("Senses (lower-cased,multi, legacy): ");
		makeIndexLowerMultiValue(ps, (e) -> e.getAttributeNS(XmlNames.NS_DC, XmlNames.SENSEKEY_LEGACY_ATTR));
	}

	/**
	 * Make index.sense in multi-value mode.
	 * Each key is a lower-cased sensekey and is unique.
	 * The line is extended beyond the first value with extra values.
	 *
	 * @param ps        print stream
	 * @param keyGetter sensekey getter function
	 */
	private void makeIndexLowerMultiValue(PrintStream ps, Function<Element, String> keyGetter)
	{
		Map<String, LinkedHashSet<Data>> entries = new TreeMap<>(String::compareToIgnoreCase);

		NodeList senseNodes = doc.getElementsByTagName(XmlNames.SENSE_TAG);
		int n = senseNodes.getLength();
		assert n >= 1;
		for (int i = 0; i < n; i++)
		{
			Node senseNode = senseNodes.item(i);
			assert senseNode.getNodeType() == Node.ELEMENT_NODE;
			Element senseElement = (Element) senseNode;

			String sensekey = keyGetter.apply(senseElement);
			String synsetId = senseElement.getAttribute(XmlNames.SYNSET_ATTR);
			String nth = senseElement.getAttribute(XmlNames.N_ATTR);
			String tagCountAttr = senseElement.getAttribute(XmlNames.TAGCOUNT_ATTR);
			long offset = offsets.get(synsetId);
			int senseNum = Integer.parseInt(nth);
			int tagCount = 0;
			if (!tagCountAttr.isEmpty())
				tagCount = Integer.parseInt(tagCountAttr);

			// data
			LinkedHashSet<Data> entry = entries.computeIfAbsent(sensekey, (s) -> new LinkedHashSet<>());
			Data data = new Data(offset, senseNum, tagCount);
			entry.add(data);
		}
		for (Map.Entry<String, LinkedHashSet<Data>> entry : entries.entrySet())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(entry.getKey().toLowerCase());
			LinkedHashSet<Data> values = entry.getValue();
			List<Data> datas = new ArrayList<>(values);
			datas.sort(Comparator.comparingInt(d -> d.sensenum));
			for (Data data : datas)
				sb.append(String.format(" %08d %d %d", data.offset, data.sensenum, data.tagCnt));
			ps.println(sb.toString());
		}
		System.err.printf("%d, %d lines %n", n, entries.size());
	}

	/**
	 * Make index.sense
	 * Sensekeys are lower-cased.
	 * Multiple lines may have the same key, which makes
	 * binary search yield unpredictable (non-deterministic) results.
	 *
	 * @param ps print stream
	 */
	public void makeIndexLowerMultiKey(PrintStream ps)
	{
		SortedSet<String> lines = new TreeSet<>(lexicalComparatorUpperFirst);

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

			String sensekeyLower = sensekey.toLowerCase();
			String line = String.format("%s %08d %d %d", sensekeyLower, offset, senseNum, tagCount);
			lines.add(line);
		}
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.printf("Senses (lower): %d, %d lines%n", n, lines.size());
	}

	/**
	 * Make index.sense (with both cased and lower-case mode)
	 * When the sensekey is cased, two lines are generated, the first with lower-case, the second with cased.
	 *
	 * @param ps print stream
	 */
	public void makeIndexBoth(PrintStream ps)
	{
		SortedSet<String> lines = new TreeSet<>(lexicalComparatorUpperFirst);

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
		for (String line : lines)
		{
			ps.println(line);
		}
		System.err.printf("Senses (both): %d, %d lines %n", n, lines.size());
	}
}
