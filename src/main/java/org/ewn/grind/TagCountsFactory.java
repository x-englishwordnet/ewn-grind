package org.ewn.grind;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TagCountsFactory
{

	public static void parseTagCounts(String dir, Map<String, Integer> map) throws IOException
	{
		// iterate on lines
		final File file = new File(dir, "data-tagcounts.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Flags.charSet)))
		{
			long valueCount = 0;
			int lineCount = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				lineCount++;
				if (line.isEmpty() || line.charAt(0) == ' ')
				{
					continue;
				}

				try
				{
					String[] fields = line.split("\\s+");
					String sensekey = fields[0];
					// int sensenum = Integer.parseInt(fields[1]);
					int tagCnt = Integer.parseInt(fields[2]);
					map.put(sensekey, tagCnt);
					valueCount++;
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] at line " + lineCount + " " + e);
				}
			}
			System.err.println("Map TagCount[sensekey] " + valueCount);
		}
	}

	public static Map<String, Integer> makeTagCountsMap(String dir) throws IOException
	{
		Map<String, Integer> map = new HashMap<>();
		parseTagCounts(dir, map);
		return map;
	}

	public static void main(String[] args) throws IOException
	{
		Map<String, Integer> map = makeTagCountsMap(".");
		System.out.println("map keys        " + map.size());
	}
}
