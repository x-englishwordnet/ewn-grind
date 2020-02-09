package org.ewn.pojos;

/**
 * Verb Frame (in verb.Framestext)
 *
 * @author Bernard Bou
 */
public class VerbFrame
{
	/**
	 * Frame id
	 */
	public final int id;

	/**
	 * Frame text
	 */
	public final String frame;

	/**
	 * Constructor
	 *
	 * @param id frame id
	 * @param frame frame text
	 */
	private VerbFrame(final int id, final String frame)
	{
		super();
		this.id = id;
		this.frame = frame;
	}

	/**
	 * Parse from line
	 *
	 * @param line line
	 * @return verb frame
	 */
	public static VerbFrame parse(final String line)
	{
		final int id = Integer.parseInt(line.split("\\s+")[0]);
		final String text = line.substring(3);
		return new VerbFrame(id, text);
	}

	@Override
	public String toString()
	{
		return "id=" + this.id + " frame=" + this.frame;
	}
}
