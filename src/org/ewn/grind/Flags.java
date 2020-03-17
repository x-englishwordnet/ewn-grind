package org.ewn.grind;

/**
 * This class groups settings flags that affect the grinder's behaviour.
 *
 * @author Bernard Bou
 */
public class Flags
{
	/**
	 * Compat mode switch that does not allow lexid to be greater than 16. See PWN grinder source in wnparse.y
	 */
	public static boolean LEXID_COMPAT = false;

	/**
	 * Compat mode switch that does not allow pointers beyond those used in PWN.
	 */
	public static  boolean POINTER_COMPAT = false;

	private Flags()
	{
	}
}
