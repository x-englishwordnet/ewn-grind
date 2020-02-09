package org.ewn.parse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.ewn.pojos.ParsePojoException;

public class Parser
{
	public static void main(String[] args) throws FileNotFoundException, IOException, ParsePojoException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Input
		String dir = args[0];

		// Process
		for (final String posName : new String[] { "noun", "verb", "adj", "adv" })
		{
			DataParser.parseSynsets(dir, posName);
		}
		for (final String posName : new String[] { "noun", "verb", "adj", "adv" })
		{
			IndexParser.parseIndexes(dir, posName);
		}
		SenseParser.parseSenses(dir);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}
}
