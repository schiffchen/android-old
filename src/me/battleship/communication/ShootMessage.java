package me.battleship.communication;

import me.battleship.Orientation;
import me.battleship.Ship;
import me.battleship.communication.messages.BattleshipMessage;
import me.battleship.communication.messages.BattleshipPacketExtension;
import me.battleship.communication.messages.ExtensionElements;

/**
 * A message for sending shots and shot results to the opponent
 *
 * @author Manuel Vögele
 */
public class ShootMessage extends BattleshipMessage
{
	/**
	 * Initializes a message for sending shots to the enemy
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	public ShootMessage(int x, int y)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension shoot = new BattleshipPacketExtension(ExtensionElements.SHOOT);
		shoot.setAttribute("x", String.valueOf(x));
		shoot.setAttribute("y", String.valueOf(y));
		root.addSubElement(shoot);
		addExtension(root);
	}
	
	/**
	 * Initializes a message for sending a result of a shot of the enemy
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @param result
	 *           the result
	 * @param ship
	 *           the ship that was sunk or <code>null</code> if no ship was sunk
	 */
	public ShootMessage(int x, int y, Result result, Ship ship)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension shoot = new BattleshipPacketExtension(ExtensionElements.SHOOT);
		shoot.setAttribute("x", String.valueOf(x));
		shoot.setAttribute("y", String.valueOf(y));
		shoot.setAttribute("result", result.toString());
		root.addSubElement(shoot);
		if (ship != null)
		{
			BattleshipPacketExtension shipExtension = new BattleshipPacketExtension(ExtensionElements.SHIP);
			shipExtension.setAttribute("x", String.valueOf(ship.getX()));
			shipExtension.setAttribute("y", String.valueOf(ship.getY()));
			shipExtension.setAttribute("orientation", (ship.getOrientation() == Orientation.HORIZONTAL ? "horizontal" : "vertical"));
			shipExtension.setAttribute("size", String.valueOf(ship.getSize()));
			shipExtension.setAttribute("destroyed", "true");
			root.addSubElement(shipExtension);
		}
		addExtension(root);
	}
}
