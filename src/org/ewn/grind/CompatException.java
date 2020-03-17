package org.ewn.grind;

public class CompatException extends Exception
{
	public CompatException(String message)
	{
		super(message);
	}

	public CompatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CompatException(Throwable cause)
	{
		super(cause);
	}
}
