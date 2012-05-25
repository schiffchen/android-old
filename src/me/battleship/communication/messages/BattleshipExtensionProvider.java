package me.battleship.communication.messages;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

public class BattleshipExtensionProvider extends EmbeddedExtensionProvider
{
	public static final BattleshipExtensionProvider INSTANCE = new BattleshipExtensionProvider();
	
	/**
	 * Creates a new instance.
	 */
	private BattleshipExtensionProvider()
	{
		// Nothing to do - only for making constructor private
	}
	
	@Override
	public PacketExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends PacketExtension> content)
	{
		BattleshipPacketExtension extension = new BattleshipPacketExtension(currentElement);
		extension.getSubElements().addAll((List<BattleshipPacketExtension>) content);
		extension.getAttributes().putAll(attributeMap);
		return extension;
	}
}