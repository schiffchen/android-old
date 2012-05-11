package me.battleship;

/**
 * A ship
 * 
 * @author manuel
 */
public class Ship
{
	/**
	 * The type of the ship
	 */
	private ShipType type;
	
	/**
	 * The size of the ship
	 */
	private int size;
	
	/**
	 * The id of the drawable of the ship
	 */
	private int drawable;
	
	/**
	 * The id of the name of the ship
	 */
	private int name;
	
	/**
	 * The x position of the ship
	 */
	private int x;
	
	/**
	 * The y position of the ship
	 */
	private int y;
	
	/**
	 * Constructs a new ship
	 * 
	 * @param type The type of the ship
	 * @param x The x position of the ship
	 * @param y The y position of the ship
	 */
	public Ship(ShipType type, int x, int y)
	{
		if (type == null)
		{
			throw new NullPointerException("type may not be null");
		}
		this.type = type;
		this.x = x;
		this.y = y;
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
	
	/**
	 * Returns the type of the ship
	 * 
	 * @return the type of the ship
	 */
	public ShipType getType()
	{
		return type;
	}
	
	/**
	 * Returns the size of the ship
	 * @return the size of the ship
	 */
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Returns the id of the name of the ship
	 * @return the id of the name of the ship
	 */
	public int getName()
	{
		return name;
	}
	
	/**
	 * Returns the id of the drawable of the ship
	 * @return the id of the drawable of the ship
	 */
	public int getDrawable()
	{
		return drawable;
	}
	
	/**
	 * Returns the x pos of the ship
	 * @return the x pos of the ship
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Returns the y pos of the ship
	 * @return the y pos of the ship
	 */
	public int getY()
	{
		return y;
	}
}
