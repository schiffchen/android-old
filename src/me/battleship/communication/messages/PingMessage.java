package me.battleship.communication.messages;

/**
 * A ping message that will be sent to the opponent 
 *
 * @author Manuel Vögele
 */
public class PingMessage extends BattleshipMessage
{
	/**
	 * Initializes a new ping message
	 */
	public PingMessage()
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension ping = new BattleshipPacketExtension(ExtensionElements.PING);
		root.addSubElement(ping);
		addExtension(root);
	}
}
