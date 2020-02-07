package org.ewn.pojos;

/**
 * Tag
 *
 * @author Bernard Bou
 */
public class TagCnt
{
	private final int tagCount;

	private TagCnt(final int tagCount)
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

	@Override public String toString()
	{
		return "tag=" + this.tagCount;
	}
}