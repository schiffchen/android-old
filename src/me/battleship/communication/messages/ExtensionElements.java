package me.battleship.communication.messages;

import org.jivesoftware.smack.provider.ProviderManager;

/**
 * A enum containing any type of message sent and received by battleship
 *
 * @author Manuel Vögele
 */
@SuppressWarnings("javadoc")
public enum ExtensionElements
{
	BATTLESHIP("battleship"),
	QUEUEING("queueing"),
	DICEROLL("diceroll"),
	SHOOT("shoot"),
	SHIP("ship"),
	PING("ping"),
	GAMESTATE("gamestate");
	
	/**
	 * The name of the element as used in xml files
	 */
	private final String elementName;
	
	/**
	 * Sets the name of the element
	 * 
	 * @param elementName the name of the element
	 */
	private ExtensionElements(String elementName)
	{
		this.elementName = elementName;
	}
	
	/**
	 * Returns the name of the element as used in xml files
	 * @return the name of the element as used in xml files
	 */
	public String getElementName()
	{
		return elementName;
	}
	
	static
	{
		ProviderManager providerManager = ProviderManager.getInstance();
		for (ExtensionElements element : ExtensionElements.values())
		{
			providerManager.addExtensionProvider(element.getElementName(), BattleshipPacketExtension.NAMESPACE, BattleshipExtensionProvider.INSTANCE);
		}
	}
}
