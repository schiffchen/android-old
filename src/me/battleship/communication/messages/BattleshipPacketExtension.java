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
	/**
	 * The namespace for battleship messages
	 */
	public static final String NAMESPACE = "http://battleship.me/xmlns/";
	
	/**
	 * The name of the element
	 */
	private String elementName;
	
	/**
	 * The attributes of the element
	 */
	private Map<String, String> attributes;
	
	/**
	 * The subelements of the element
	 */
	private List<BattleshipPacketExtension> subElements;
	
	/**
	 * Creates a new element
	 * 
	 * @param element the type of the element
	 */
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
	
	/**
	 * Creates a new element
	 * 
	 * @param elementName the name of the element
	 */
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
	
	/**
	 * Adds a sub element to this element
	 * @param extension the sub element
	 */
	public void addSubElement(BattleshipPacketExtension extension)
	{
		subElements.add(extension);
	}
	
	/**
	 * Returns the sub elements of the specified element
	 * @return a list of sub elements
	 */
	public List<BattleshipPacketExtension> getSubElements()
	{
		return subElements;
	}
	
	/**
	 * Sets the specified attribute.
	 * 
	 * @param attribute the attribute
	 * @param value the value
	 */
	public void setAttribute(String attribute, String value)
	{
		attributes.put(attribute, value);
	}
	
	/**
	 * Returns the attributes
	 * @return the attributes
	 */
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
