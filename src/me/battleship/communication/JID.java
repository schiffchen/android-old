package me.battleship.communication;

/**
 * A class representing a jabber id
 *
 * @author Manuel Vögele
 */
public class JID
{
	/**
	 * The node
	 */
	private String node;
	
	/**
	 * The domain
	 */
	private String domain;
	
	/**
	 * The resource
	 */
	private String resource;
	
	/**
	 * The full id
	 */
	private String id;
	
	/**
	 * Initializes a jabber id using a full id
	 * 
	 * @param id the id
	 */
	public JID(String id)
	{
		this.id = id;
		int atpos = id.indexOf('@');
		int slashpos = id.indexOf('/');
		node = id.substring(0, atpos);
		domain = id.substring(atpos + 1, slashpos);
		resource = id.substring(slashpos + 1, id.length());
	}
	
	/**
	 * Returns the node
	 * @return the node
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * Returns the domain
	 * @return the domain
	 */
	public String getDomain()
	{
		return domain;
	}

	/**
	 * Returns the resource
	 * @return the resource
	 */
	public String getResource()
	{
		return resource;
	}

	/**
	 * Retunrs the id
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return id;
	}
}
