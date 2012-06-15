package me.battleship;

import android.util.Log;

/**
 * A ship
 * 
 * @author manuel
 */
public class Ship
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "Ship";
	
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
	 * The orientation of the ship
	 */
	private Orientation orientation;

	/**
	 * Constructs a new ship
	 * 
	 * @param type
	 *           The type of the ship
	 * @param x
	 *           The x position of the ship
	 * @param y
	 *           The y position of the ship
	 * @param orientation
	 *           the orientation of the ship
	 */
	public Ship(ShipType type, int x, int y, Orientation orientation)
	{
		if (type == null)
		{
			throw new NullPointerException("type may not be null");
		}
		this.type = type;
		this.x = x;
		this.y = y;
		this.orientation = orientation;
		this.size = getSizeForType(type);
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				name = R.string.aircraft_carrier;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.aircraftcarrier_horizontal : R.drawable.aircraftcarrier_vertical);
			break;
			case BATTLESHIP:
				name = R.string.battleship;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.battleship_horizontal : R.drawable.battleship_vertical);
			break;
			case SUBMARINE:
				name = R.string.submarine;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.submarine_horizontal : R.drawable.submarine_vertical);
			break;
			case DESTROYER:
				name = R.string.destoryer;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.destroyer_horizontal : R.drawable.destroyer_vertical);
			break;
		}
	}
	
	/**
	 * Returns the size for the specified ship type
	 * 
	 * @param type
	 *           the type of the ship
	 * @return the size
	 */
	public static int getSizeForType(ShipType type)
	{
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				return 5;
			case BATTLESHIP:
				return 4;
			case SUBMARINE:
				return 3;
			case DESTROYER:
				return 2;
			default:
				Log.wtf(LOG_TAG, "Unrecognized value " + type + " in getSizeForType(ShipType)");
				return -1;
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
	 * 
	 * @return the size of the ship
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Returns the id of the name of the ship
	 * 
	 * @return the id of the name of the ship
	 */
	public int getName()
	{
		return name;
	}

	/**
	 * Returns the id of the drawable of the ship
	 * 
	 * @return the id of the drawable of the ship
	 */
	public int getDrawable()
	{
		return drawable;
	}

	/**
	 * Returns the x pos of the ship
	 * 
	 * @return the x pos of the ship
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y pos of the ship
	 * 
	 * @return the y pos of the ship
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Returns the orientation of the ship
	 * 
	 * @return the orientation of the ship
	 */
	public Orientation getOrientation()
	{
		return orientation;
	}
}
