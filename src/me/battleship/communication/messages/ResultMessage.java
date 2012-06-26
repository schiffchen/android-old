package me.battleship.communication.messages;

/**
 * A message for sending game statistics to the matchmaker 
 *
 * @author Manuel Vögele
 */
public class ResultMessage extends BattleshipMessage
{
	/**
	 * Initializes a new ResultMessage
	 * @param matchId the id of the match
	 * @param winnerJID the JID of the winner
	 */
	public ResultMessage(String matchId, String winnerJID)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension result = new BattleshipPacketExtension(ExtensionElements.RESULT);
		result.setAttribute("mid", matchId);
		result.setAttribute("winner", winnerJID);
		root.addSubElement(result);
		addExtension(root);
	}
}
