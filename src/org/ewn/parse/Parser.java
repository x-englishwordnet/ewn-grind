package org.ewn.parse;

import java.io.IOException;

public class Parser
{
	public static void main(String[] args) throws IOException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Input
		String dir = args[0];

		// Process
		for (final String posName : new String[] { "noun", "verb", "adj", "adv" })
		{
			DataParser.parseData(dir, posName);
		}

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}
}
