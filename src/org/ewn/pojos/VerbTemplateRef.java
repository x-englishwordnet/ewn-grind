package org.ewn.pojos;

/**
 * Sense reference to verb template
 *
 * @author Bernard Bou
 */
public class VerbTemplateRef
{
	/**
	 * Sensekey
	 */
	public final Sensekey sensekey;

	/**
	 * Verb template id
	 */
	public final int id;

	/**
	 * Constructor
	 *
	 * @param sensekey sensekey
	 * @param id verb template id
	 */
	private VerbTemplateRef(final Sensekey sensekey, final int id)
	{
		this.id = id;
		this.sensekey = sensekey;
	}

	/**
	 * Parse from line
	 *
	 * @param line line
	 * @return array of verb template references
	 * @throws ParsePojoException parse exception
	 */
	public static VerbTemplateRef[] parse(final String line) throws ParsePojoException
	{
		final String[] fields = line.split("\\s+");
		if (fields.length <= 1)
			return null;

		// parse sensekey
		final Sensekey sensekey = Sensekey.parse(fields[0]);

		final String[] subFields = fields[1].split(",");
		final int count = subFields.length;

		// pair sensekey with id for each id
		final VerbTemplateRef[] refs = new VerbTemplateRef[count];
		for (int i = 0; i < count; i++)
		{
			final int id = Integer.parseInt(subFields[i]);
			refs[i] = new VerbTemplateRef(sensekey, id);
		}
		return refs;
	}

	@Override
	public String toString()
	{
		return "id=" + this.id + " sensekey=" + this.sensekey;
	}
}