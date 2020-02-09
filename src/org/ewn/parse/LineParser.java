package org.ewn.parse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.ewn.pojos.ParsePojoException;
import org.ewn.pojos.Synset;

public class LineParser
{
	public static void main(String[] args) throws IOException, ParsePojoException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Input
		String dir = args[0];
		String posName = args[1];
		long fileOffset = Long.parseLong(args[2]);
		final boolean isAdj = posName.equals("adj");

		// Process
		String line = read(dir, posName, fileOffset);
		System.out.println(line);
		Synset synset = parseSynset(line, isAdj);
		System.out.println(synset);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	public static String read(final String dir, final String posName, final long fileOffset) throws IOException
	{
		final File file = new File(dir, "data." + posName);
		try (final RandomAccessFile raFile = new RandomAccessFile(file, "r"))
		{
			raFile.seek(fileOffset);
			return raFile.readLine();
		}
	}

	private static Synset parseSynset(String line, boolean isAdj) throws ParsePojoException
	{
		return Synset.parseSynset(line, isAdj);
	}
}
