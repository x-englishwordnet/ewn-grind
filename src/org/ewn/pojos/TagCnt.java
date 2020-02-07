package org.ewn.pojos;

/**
 * Tag
 * 
 * @author Bernard Bou
 */
public class TagCnt
{
	private final int tagCount;

	public TagCnt(final int tagCount)
	{
		this.tagCount = tagCount;
	}

	public static TagCnt parse(final String tagCountString)
	{
		final int tagCount = Integer.parseInt(tagCountString);
		return new TagCnt(tagCount);
	}

	public int getTagCount()
	{
		return this.tagCount;
	}

	public String toXString()
	{
		return "tag=" + this.tagCount; 
	}

	@Override
	public String toString()
	{
		return Integer.toString(this.tagCount); 
	}
}