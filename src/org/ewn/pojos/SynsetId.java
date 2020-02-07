package org.ewn.pojos;

/**
 * Synset Id
 * 
 * @author Bernard Bou
 */
public class SynsetId
{
	/**
	 * Part of speech
	 */
	private final Pos pos;

	/**
	 * The offset value is guaranteed to be unique only relative to the part of speech
	 */
	private final long offset;

	/**
	 * Constructor
	 * 
	 * @param pos part of speech
	 * @param offset offset in file
	 */
	public SynsetId(final Pos pos, final long offset)
	{
		this.pos = pos;
		this.offset = offset;
	}

	/**
	 * Get file offset
	 * 
	 * @return file offset
	 */
	public long getOffset()
	{
		return this.offset;
	}

	/**
	 * Get part of speech
	 * 
	 * @return part of speech
	 */
	public Pos getPos()
	{
		return this.pos;
	}

	@Override
	public String toString()
	{
		return this.getPos().toTag() + '-' + this.getOffset();
	}

	// U N I Q U E N E S S E X T E N S I O N

	/**
	 * Constructor from unique id
	 * 
	 * @param uniqueId unique id
	 */
	private SynsetId(final long uniqueId)
	{
		for (final Pos p : Pos.values())
		{
			if (uniqueId >= SynsetId.getBaseUID(p.toChar()) && uniqueId < SynsetId.getCeilingUID(p.toChar()))
			{
				final long relativeId = uniqueId - SynsetId.getBaseUID(p.toChar());
				this.pos = p;
				this.offset = relativeId;
				return;
			}
		}
		throw new IllegalArgumentException("Illegal synset unique id : " + uniqueId);
	}

	/**
	 * Make synset id fri unique id
	 * 
	 * @param uniqueId unique id
	 * @return synset
	 */
	public static SynsetId make(final long uniqueId)
	{
		return new SynsetId(uniqueId);
	}

	/**
	 * Get unique id
	 * 
	 * @return unique id
	 */
	public long toUID()
	{
		final long base = SynsetId.getBaseUID(this.pos.toChar());
		return base + this.offset;
	}

	/**
	 * Get unique id string
	 * 
	 * @return unique id string
	 */
	public String toUIDString()
	{
		final long uid = toUID();
		return Long.toString(uid);
	}

	/**
	 * Get base UID for pos
	 * 
	 * @param pos part of speech
	 * @return base uid for given pos
	 */
	private static long getBaseUID(final char pos)
	{
		switch (pos)
		{
		case 'n':
			return 100000000;
		case 'v':
			return 200000000;
		case 'a':
		case 's':
			return 300000000;
		case 'r':
			return 400000000;
		default:
			break;
		}
		return 900000000; // invalid value
	}

	/**
	 * Get ceiling UID for pos
	 * 
	 * @param pos part of speech
	 * @return ceiling uid for given pos
	 */
	private static long getCeilingUID(final char pos)
	{
		return SynsetId.getBaseUID(pos) + 100000000 - 1;
	}
}
