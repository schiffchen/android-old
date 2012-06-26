package me.battleship.communication.messages;

/**
 * A message sent to indicate that the game has ended 
 *
 * @author Manuel Vögele
 */
public class GamestateMessage extends BattleshipMessage
{
	/**
	 * Instantiates a new gamestate message
	 * 
	 * @param looserJID the JID of the looser
	 */
	public GamestateMessage(String looserJID)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension gamestate = new BattleshipPacketExtension(ExtensionElements.GAMESTATE);
		gamestate.setAttribute("state", "end");
		gamestate.setAttribute("looser", looserJID);
		root.addSubElement(gamestate);
		addExtension(root);
	}
}
