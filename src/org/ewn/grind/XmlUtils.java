package org.ewn.grind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class XmlUtils
{
	private XmlUtils()
	{
	}

	static Document getDocument(String filePath, boolean withSchema) throws SAXException, ParserConfigurationException, IOException
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		builderFactory.setNamespaceAware(true);
		if (withSchema)
		{
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File("schema.xsd"));
			builderFactory.setSchema(schema);
		}

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(new File(filePath));
		doc.getDocumentElement().normalize();
		System.err.println("Document " + filePath);
		return doc;
	}

	static Map<String, Element> makeElementMap(Document doc, String tag, String key)
	{
		System.err.print("Map " + tag + "[@" + key + "] ");
		Map<String, Element> map = new HashMap<>();
		NodeList nodes = doc.getElementsByTagName(tag);
		int n = nodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node node = nodes.item(i);
			assert node.getNodeType() == Node.ELEMENT_NODE;
			Element element = (Element) node;
			String attr = element.getAttribute(key);
			map.put(attr, element);
		}
		System.err.println(map.size());
		return map;
	}

	static Map<String, List<Element>> makeElementMultiMap(Document doc, String tag, String key)
	{
		System.err.print("MultiMap " + tag + "[@" + key + "] ");
		Map<String, List<Element>> map = new HashMap<>();
		NodeList nodes = doc.getElementsByTagName(tag);
		int n = nodes.getLength();
		for (int i = 0; i < n; i++)
		{
			Node node = nodes.item(i);
			assert node.getNodeType() == Node.ELEMENT_NODE;
			Element element = (Element) node;
			String attr = element.getAttribute(key);
			List<Element> elements = map.computeIfAbsent(attr, k -> new ArrayList<>());
			elements.add(element);
		}
		System.err.println(map.size());
		return map;
	}

	static Element getFirstChildElement(Element element, String tag)
	{
		NodeList nodeList = element.getElementsByTagName(tag);
		if (nodeList.getLength() == 1)
		{
			Node node = nodeList.item(0);
			assert node.getNodeType() == Node.ELEMENT_NODE;
			return (Element) node;
		}
		return null;
	}

	static List<Element> getChildElements(Element element, String tag)
	{
		NodeList nodeList = element.getElementsByTagName(tag);
		if (nodeList.getLength() > 0)
		{
			List<Element> elements = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				assert node.getNodeType() == Node.ELEMENT_NODE;
				elements.add((Element) node);
			}
			return elements;
		}
		return null;
	}

	static NodeList getXPathNodeList(String expr, Document doc) throws XPathExpressionException
	{
		return (NodeList) XPathFactory.newInstance().newXPath().compile(expr).evaluate(doc, XPathConstants.NODESET);
	}
}
