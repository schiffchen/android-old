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
	
	/**
	 * Creates a message for pinging the matchmaker
	 * @param id the queueId
	 */
	public QueueMessage(String id)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = new BattleshipPacketExtension(ExtensionElements.QUEUEING);
		queueing.setAttribute("action", "ping");
		queueing.setAttribute("id", id);
		root.addSubElement(queueing);
		addExtension(root);
	}
	
	/**
	 * Creates a message for confirming the assingment of an opponent
	 * 
	 * @param opponentJID the opponent id
	 * @param matchId the match id
	 */
	public QueueMessage(String opponentJID, String matchId)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = new BattleshipPacketExtension(ExtensionElements.QUEUEING);
		queueing.setAttribute("action", "assigned");
		queueing.setAttribute("jid", opponentJID);
		queueing.setAttribute("mid", matchId);
		root.addSubElement(queueing);
		addExtension(root);
	}
}
