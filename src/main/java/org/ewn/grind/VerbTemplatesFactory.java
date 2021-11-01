package org.ewn.grind;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VerbTemplatesFactory
{

	public static void parseVerbTemplates(String dir, Map<String, int[]> map) throws IOException
	{
		// iterate on lines
		final File file = new File(dir, "sentidx.vrb");
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
					String[] fields = line.split("[\\s,]+");
					String sensekey = fields[0];
					int[] templateIds = new int[fields.length - 1];
					for (int i = 1; i < fields.length; i++)
					{
						templateIds[i - 1] = Integer.parseInt(fields[i]);
					}
					map.put(sensekey, templateIds);
					valueCount++;
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] verb templates at line " + lineCount + " " + e);
				}
			}
			System.err.println("Map VerbTemplate[sensekey] " + valueCount);
		}

	}

	public static Map<String, int[]> makeVerbTemplatesMap(String dir) throws IOException
	{
		Map<String, int[]> map = new HashMap<>();
		parseVerbTemplates(dir, map);
		return map;
	}

	public static void main(String[] args) throws IOException
	{
		Map<String, int[]> map = makeVerbTemplatesMap(".");
		System.out.println("map keys        " + map.size());
		for (String sk : new String[] { "abide%2:31:00::", "abominate%2:37:00::", "abash%2:37:00::", "amble%2:38:00::" })
		{
			int[] templateIds = map.get(sk);
			System.out.println("key " + sk + /* Arrays.toString(templateIds) ++ */ " " + Formatter.join(templateIds, ',', "%d"));
		}
	}
}
