package me.battleship.communication.messages;


public class QueueMessage extends BattleshipMessage
{
	public QueueMessage()
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = new BattleshipPacketExtension(ExtensionElements.QUEUEING);
		queueing.setAttribute("action", "request");
		root.addSubElement(queueing);
		addExtension(root);
	}
}
