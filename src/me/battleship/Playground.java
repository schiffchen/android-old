package me.battleship;

public class Playground
{
	public static final int PLAYGROUND_SIZE = 10;
	
	public static final int ARRAY_SIZE = PLAYGROUND_SIZE * PLAYGROUND_SIZE;
	
	private PlaygroundField[] fields;
	
	public Playground()
	{
		fields = new PlaygroundField[PLAYGROUND_SIZE*PLAYGROUND_SIZE];
		for (int i = 0;i < ARRAY_SIZE;i++)
		{
			fields[i] = new PlaygroundField(false);
		}
		for (PlaygroundField field : fields)
		{
			System.out.println(field);
		}
	}
	
	public PlaygroundField getField(int x, int y)
	{
		return fields[x + y * PLAYGROUND_SIZE];
	}
}
