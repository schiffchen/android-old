package me.battleship.communication.messages;

import org.jivesoftware.smack.provider.ProviderManager;

public enum ExtensionElements
{
	BATTLESHIP("battleship"),
	QUEUEING("queueing");
	
	private final String elementName;
	
	private ExtensionElements(String elementName)
	{
		this.elementName = elementName;
	}
	
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
