package me.battleship;

/**
 * This is where game takes place
 * 
 * @author manuel
 */
public class Playground
{
	/**
	 * The size of the playground. The playground is assumed to be square so this is used for the x and y size.
	 */
	public static final int PLAYGROUND_SIZE = 10;
	
	/**
	 * Indicates how big the array to store the {@link PlaygroundField PlaygroundFields} in has to be.
	 */
	public static final int ARRAY_SIZE = PLAYGROUND_SIZE * PLAYGROUND_SIZE;
	
	/**
	 * Stores all fields of this playground.
	 */
	private PlaygroundField[] fields;
	
	/**
	 * Constructs a new playground
	 */
	public Playground()
	{
		fields = new PlaygroundField[PLAYGROUND_SIZE*PLAYGROUND_SIZE];
		for (int i = 0;i < ARRAY_SIZE;i++)
		{
			fields[i] = new PlaygroundField();
		}
	}
	
	/**
	 * Returns a field of the playground
	 * 
	 * @param x the x position of the field
	 * @param y the y position of the field
	 * @return the field at the specified postitino
	 */
	public PlaygroundField getField(int x, int y)
	{
		return fields[x + y * PLAYGROUND_SIZE];
	}
}
