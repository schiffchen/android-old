package me.battleship.communication.messages;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * A abstract implementation for PacketExtension
 *
 * @author Manuel Vögele
 */
public class BattleshipPacketExtension implements PacketExtension
{
	public static final String NAMESPACE = "http://battleship.me/xmlns/";
	
	private String elementName;
	
	private Map<String, String> attributes;
	
	private List<BattleshipPacketExtension> subElements;
	
	public BattleshipPacketExtension(ExtensionElements element)
	{
		this.elementName = element.getElementName();
		attributes = new HashMap<String, String>();
		subElements = new LinkedList<BattleshipPacketExtension>();
		if (element == ExtensionElements.BATTLESHIP)
		{
			attributes.put("xmlns", NAMESPACE);
		}
	}
	
	public BattleshipPacketExtension(String elementName)
	{
		this.elementName = elementName;
		attributes = new HashMap<String, String>();
		subElements = new LinkedList<BattleshipPacketExtension>();
		if (elementName.equals(ExtensionElements.BATTLESHIP.getElementName()) )
		{
			attributes.put("xmlns", NAMESPACE);
		}
	}
	
	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}
	
	public void addSubElement(BattleshipPacketExtension extension)
	{
		subElements.add(extension);
	}
	
	public List<BattleshipPacketExtension> getSubElements()
	{
		return subElements;
	}
	
	public void setAttribute(String attribute, String value)
	{
		attributes.put(attribute, value);
	}
	
	public Map<String, String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String toXML()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('<').append(elementName).append(' ');
		for (Entry<String, String> entry : attributes.entrySet())
		{
			builder.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\" ");
		}
		if (subElements.size() == 0)
		{
			builder.append("/>");
			return builder.toString();
		}
		builder.append('>');
		for (PacketExtension extension : subElements)
		{
			builder.append(extension.toXML());
		}
		builder.append("</").append(elementName).append('>');
		return builder.toString();
	}

	@Override
	public String getElementName()
	{
		return elementName;
	}
}
