package me.battleship.communication.messages;

import org.jivesoftware.smack.packet.Message;

/**
 * A parent class for any messages sent by battleship
 *
 * @author Manuel Vögele
 */
public class BattleshipMessage extends Message
{
	/**
	 * Creates a new messages and sets some default parameters
	 */
	public BattleshipMessage()
	{
		super();
		setType(Type.normal);
	}
}
