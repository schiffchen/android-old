package me.battleship;

/**
 * A single field of the {@link Playground}
 *
 * @author manuel
 */
public class PlaygroundField
{
	/**
	 * Indicates whether this field was already hit
	 */
	private boolean hit;
	
	/**
	 * Indicates whether a ship is on this field
	 */
	private boolean isShip;
	
	/**
	 * The ship placed on this field
	 */
	private Ship ship;
	
	/**
	 * Constructs a new playground field. All values are <code>false</code> per default.
	 */
	public PlaygroundField()
	{
		hit = false;
		isShip = false;
		ship = null;
	}

	/**
	 * Returns whether this field was already hit
	 * @return <code>true</code> if this field was already hit
	 */
	public boolean isHit()
	{
		return hit;
	}

	/**
	 * Set if the field was already hit
	 * @param hit <code>true</code> if the field was already hit
	 */
	public void setHit(boolean hit)
	{
		this.hit = hit;
	}
	
	/**
	 * Set if a ship is on this field
	 * @param ship <code>true</code> if a ship is on this field
	 */
	public void setIsShip(boolean ship)
	{
		this.isShip = ship;
	}
	
	/**
	 * Returns whether a ship is on this field
	 * @return <code>true</code> if a ship is on this field
	 */
	public boolean isShip()
	{
		return isShip;
	}
	
	/**
	 * Sets a ship on this field
	 * @param ship the ship
	 */
	public void setShip(Ship ship)
	{
		this.ship = ship;
	}
	
	/**
	 * Returns the ship placed on this field
	 * @return the ship placed on this field
	 */
	public Ship getShip()
	{
		return ship;
	}
}
