package me.battleship.communication.messages;

/**
 * A message for queueing at the matchmaker
 *
 * @author Manuel Vögele
 */
public class QueueMessage extends BattleshipMessage
{
	/**
	 * Creates a message for adding the client to the matchmaker queue
	 */
	public QueueMessage()
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = new BattleshipPacketExtension(ExtensionElements.QUEUEING);
		queueing.setAttribute("action", "request");
		root.addSubElement(queueing);
		addExtension(root);
	}
}
