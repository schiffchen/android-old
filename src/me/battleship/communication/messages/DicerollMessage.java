package me.battleship.communication.messages;

public class DicerollMessage extends BattleshipMessage
{
	public DicerollMessage(int dice)
	{
		BattleshipPacketExtension root = new BattleshipPacketExtension(ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension diceroll = new BattleshipPacketExtension(ExtensionElements.DICEROLL);
		diceroll.setAttribute("dice", String.valueOf(dice));
		root.addSubElement(diceroll);
		addExtension(root);
	}
}
