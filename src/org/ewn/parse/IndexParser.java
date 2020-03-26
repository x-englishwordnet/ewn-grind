package org.ewn.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ewn.grind.Flags;
import org.ewn.pojos.CoreIndex;
import org.ewn.pojos.ParsePojoException;

/**
 * Index parser index.{noun|verb|adj|adv}
 *
 * @author Bernard Bou
 */
public class IndexParser
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
		for (final String posName : new String[] { "noun", "verb", "adj", "adv" })
		{
			IndexParser.parseIndexes(dir, posName);
		}

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	public static void parseIndexes(String dir, String posName) throws IOException, ParsePojoException
	{
		System.out.println("* Indexes " + posName);

		// iterate on lines
		final File file = new File(dir, "index." + posName);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Flags.charSet)))
		{
			long indexCount = 0;
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
					CoreIndex index = CoreIndex.parseCoreIndex(line);
					indexCount++;
					if (DUMP)
						System.out.println(index);
				}
				catch (final ParsePojoException e)
				{
					System.err.printf("%n%s:%d line=[%s] except=%s", file.getName(), lineCount, line, e);
					if (THROW)
						throw e;
				}
			}
			System.out.println("lines          	" + lineCount);
			System.out.println("parse successes " + indexCount);
		}
	}
}
