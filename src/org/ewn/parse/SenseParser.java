package org.ewn.parse;

import org.ewn.grind.Flags;
import org.ewn.pojos.ParsePojoException;
import org.ewn.pojos.Sense;

import java.io.*;

/**
 * Sense Parser (index.sense)
 *
 * @author Bernard Bou
 */
public class SenseParser
{
	private static final boolean THROW = false;

	private static final boolean DUMP = false;

	public static void main(String[] args) throws IOException, ParsePojoException
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

	static void parseSenses(final String dir) throws IOException, ParsePojoException
	{
		System.out.println("* Senses");

		// iterate on lines
		final File file = new File(dir, "index.sense");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Flags.charSet)))
		{
			long senseCount = 0;
			int lineCount = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				lineCount++;

				try
				{
					Sense sense = Sense.parseSense(line);
					senseCount++;
					if (DUMP)
						System.out.println(sense);
				}
				catch (final ParsePojoException e)
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
