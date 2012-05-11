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
	private boolean ship;
	
	/**
	 * Constructs a new playground field. All values are <code>false</code> per default.
	 */
	public PlaygroundField()
	{
		hit = false;
		ship = false;
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
	public void setShip(boolean ship)
	{
		this.ship = ship;
	}
	
	/**
	 * Returns whether a ship is on this field
	 * @return <code>true</code> if a ship is on this field
	 */
	public boolean isShip()
	{
		return ship;
	}
}
