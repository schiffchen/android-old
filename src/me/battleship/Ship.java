package me.battleship;

public class Ship
{
	private ShipType type;
	
	private int size;
	
	private int drawable;
	
	private int name;
	
	private int x;
	
	private int y;
	
	public Ship(ShipType type, int x, int y)
	{
		if (type == null)
		{
			throw new NullPointerException("type may not be null");
		}
		this.type = type;
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				size = 5;
				name = R.string.aircraft_carrier;
				// TODO: Set drawable
				break;
			case BATTLESHIP:
				size = 4;
				name = R.string.battleship;
				// TODO: Set drawable
				break;
			case SUBMARINE:
				size = 3;
				name = R.string.submarine;
				break;
			case DESTROYER:
				size = 2;
				name = R.string.destoryer;
				break;
		}
	}
	
	public ShipType getType()
	{
		return type;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getDrawable()
	{
		return drawable;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
