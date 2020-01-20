package org.ewn.parse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.ewn.pojos.Synset;

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
			process(dir, posName);
		}

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	private static void process(final String dir, final String posName) throws IOException
	{
		final boolean isAdj = posName.equals("adj");
		System.out.println(posName.toUpperCase());

		// iterate on lines
		final File file = new File(dir, "data." + posName);
		try (RandomAccessFile raFile = new RandomAccessFile(file, "r"))
		{
			raFile.seek(0);

			// iterate on lines
			int offsetErrorCount = 0;
			int parseErrorCount = 0;
			long parseSuccessCount = 0;
			int lineCount = 0;

			String line;
			long fileOffset = raFile.getFilePointer();
			for (; (line = raFile.readLine()) != null; fileOffset = raFile.getFilePointer())
			{
				lineCount++;
				if (line.isEmpty() || line.charAt(0) == ' ')
				{
					continue;
				}

				// split into fields
				final String[] lineFields = line.split("\\s+");

				// read offset
				long readOffset = Long.parseLong(lineFields[0]);
				if (fileOffset != readOffset)
				{
					System.out.println(posName + ';' + lineCount + " offset=" + fileOffset + " line=[" + line + "]");
					offsetErrorCount++;
					continue;
				}

				// read
				try
				{
					parseSynset(line, isAdj);
				}
				catch (final Exception e)
				{
					System.err.println(file.getName() + ';' + lineCount + " offset=" + fileOffset + " line=[" + line + "] " + e.getMessage());
					parseErrorCount++;
					continue;
				}

				// counter
				parseSuccessCount++;
			}
			System.out.println("lines          	" + lineCount);
			System.out.println("offset errors  	" + offsetErrorCount);
			System.out.println("parse errors   	" + parseErrorCount);
			System.out.println("parse successes " + parseSuccessCount);
		}
	}

	private static Synset parseSynset(String line, boolean isAdj)
	{
		return Synset.parseSynset(line, isAdj);
	}
}
