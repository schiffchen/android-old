package me.battleship.communication;

/**
 * The content of the attribute containing whether water or ship was hit 
 *
 * @author Manuel Vögele
 */
@SuppressWarnings("javadoc")
public enum Result
{
	WATER("water"),
	SHIP("ship");
	
	/**
	 * Returns the result represented by the specified string
	 * 
	 * @param s the string
	 * @return the result represented by the specified string
	 */
	public static Result getResultForString(String s)
	{
		if (WATER.toString().equals(s))
		{
			return WATER;
		}
		if (SHIP.toString().equals(s))
		{
			return SHIP;
		}
		return null;
	}
	
	/**
	 * The text representation of the elements as used in xml files
	 */
	private final String text;
	
	/**
	 * Sets the text of the elements
	 * @param text the text of the elements
	 */
	private Result(String text)
	{
		this.text = text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
