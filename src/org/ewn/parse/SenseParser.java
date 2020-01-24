package org.ewn.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ewn.pojos.Sense;

/**
 * Sense Parser (index.sense)
 * 
 * @author Bernard Bou
 */
public class SenseParser
{
	private static final boolean THROW = false;

	private static final boolean DUMP = false;

	public static void main(String[] args) throws IOException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Input
		String dir = args[0];

		// Process
		parseSenses(dir);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	static void parseSenses(final String dir) throws IOException
	{
		// iterate on lines
		final File file = new File(dir, "index.sense");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
		{
			long senseCount = 0;
			int lineCount = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				lineCount++;

				try
				{
					Sense sense = Sense.parse(line);
					senseCount++;
					if (DUMP)
						System.out.println(sense);
				}
				catch (final Exception e)
				{
					System.err.printf("%n%s:%d line=[%s] except=%s", file.getName(), lineCount, line, e);
					if (THROW)
						throw e;
				}
			}
			System.out.println("lines          	" + lineCount);
			System.out.println("parse successes " + senseCount);
		}
	}
}
