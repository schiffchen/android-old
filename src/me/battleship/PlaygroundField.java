package me.battleship;

public class PlaygroundField
{
	private boolean hit;
	
	private boolean ship;
	
	public PlaygroundField(boolean ship)
	{
		hit = false;
		this.ship = ship;
	}

	public boolean isHit()
	{
		return hit;
	}

	public void setHit(boolean hit)
	{
		this.hit = hit;
	}
	
	public void setShip(boolean ship)
	{
		this.ship = ship;
	}
	
	public boolean isShip()
	{
		return ship;
	}
}
