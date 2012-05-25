package me.battleship.communication.messages;

import org.jivesoftware.smack.packet.Message;

public class MessageUtil
{
	/**
	 * Returns the packet extension for the specified element
	 * @param message the message the extension is in
	 * @param element the element type
	 * @return the packet extension
	 */
	public static BattleshipPacketExtension getPacketExtension(Message message, ExtensionElements element)
	{
		return (BattleshipPacketExtension) message.getExtension(element.getElementName(), BattleshipPacketExtension.NAMESPACE);
	}
}
