package me.battleship.communication.messages;

/**
 * A message for sending the diceroll to the opponent 
 *
 * @author Manuel Vögele
 */
public class DicerollMessage extends BattleshipMessage
{
	/**
	 * Instantiates a message to send the diceroll to the opponent
	 * 
	 * @param dice the result of the diceroll
	 */
	public DicerollMessage(int dice)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension diceroll = new BattleshipPacketExtension(ExtensionElements.DICEROLL);
		diceroll.setAttribute("dice", String.valueOf(dice));
		root.addSubElement(diceroll);
		addExtension(root);
	}
}
