package org.ewn.grind;

import java.util.Collection;
import java.util.function.Function;

class Formatter
{
	private Formatter()
	{
	}

	/**
	 * Escape string
	 * 
	 * @param item string to escape
	 * @return escaped string
	 */
	static String escape(String item)
	{
		return item.replace(' ', '_');
	}

	/**
	 * Join items
	 * 
	 * @param items collection of items of type T
	 * @param delim delimiter
	 * @param escape whether to escape
	 * @return joined string representation of items
	 */
	static <T> String join(Collection<T> items, char delim, boolean escape)
	{
		return join(items, delim, escape, T::toString);
	}

	/**
	 * Join items
	 * 
	 * @param items collection of items of type T
	 * @param delim delimiter
	 * @param escape whether to escape
	 * @param f string function to represent item
	 * @return joined string representation of items
	 */
	static <T> String join(Collection<T> items, char delim, boolean escape, Function<T, String> f)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T item : items)
		{
			if (first)
				first = false;
			else
				sb.append(delim);
			String value = f.apply(item);
			sb.append(escape ? escape(value) : value);
		}
		return sb.toString();
	}

	/**
	 * Join items, prefix with count
	 * 
	 * @param items collection of items of type T
	 * @param countFormat format of count field
	 * @return joined string representation of items preceded by count
	 */
	static <T> String joinNum(Collection<T> items, String countFormat)
	{
		return joinNum(items, countFormat, T::toString);
	}

	/**
	 * Join items, prefix with count
	 * 
	 * @param items collection of items of type T
	 * @param countFormat format of count field
	 * @param f collection of items of type T
	 * @return joined string representation of items preceded by count
	 */
	static <T> String joinNum(Collection<T> items, String countFormat, Function<T, String> f)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(countFormat, items.size()));
		for (T item : items)
		{
			sb.append(' ');
			String value = f.apply(item);
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * Header of data files
	 */
	static final String PRINCETON_HEADER = //
			"  1 This software and database is being provided to you, the LICENSEE, by  \n" + //
					"  2 Princeton University under the following license.  By obtaining, using  \n" + //
					"  3 and/or copying this software and database, you agree that you have  \n" + //
					"  4 read, understood, and will comply with these terms and conditions.:  \n" + //
					"  5   \n" + //
					"  6 Permission to use, copy, modify and distribute this software and  \n" + //
					"  7 database and its documentation for any purpose and without fee or  \n" + //
					"  8 royalty is hereby granted, provided that you agree to comply with  \n" + //
					"  9 the following copyright notice and statements, including the disclaimer,  \n" + //
					"  10 and that the same appear on ALL copies of the software, database and  \n" + //
					"  11 documentation, including modifications that you make for internal  \n" + //
					"  12 use or for distribution.  \n" + //
					"  13   \n" + //
					"  14 WordNet 3.1 Copyright 2011 by Princeton University.  All rights reserved.  \n" + //
					"  15   \n" + //
					"  16 THIS SOFTWARE AND DATABASE IS PROVIDED \"AS IS\" AND PRINCETON  \n" + //
					"  17 UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR  \n" + //
					"  18 IMPLIED.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, PRINCETON  \n" + //
					"  19 UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES OF MERCHANT-  \n" + //
					"  20 ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE  \n" + //
					"  21 OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT  \n" + //
					"  22 INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR  \n" + //
					"  23 OTHER RIGHTS.  \n" + //
					"  24   \n" + //
					"  25 The name of Princeton University or Princeton may not be used in  \n" + //
					"  26 advertising or publicity pertaining to distribution of the software  \n" + //
					"  27 and/or database.  Title to copyright in this software, database and  \n" + //
					"  28 any associated documentation shall at all times remain with  \n" + //
					"  29 Princeton University and LICENSEE agrees to preserve same.  \n"; //
}