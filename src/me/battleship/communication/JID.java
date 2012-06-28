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
	 * @throws NullPointerException when the id is null
	 * @throws JIDFormatException when the format of the jid is invalid
	 */
	public JID(String id) throws NullPointerException, JIDFormatException
	{
		if (id == null)
		{
			throw new NullPointerException("Id may not be null");
		}
		if (!id.contains("@"))
		{
			throw new JIDFormatException("Invalid jabber id: " + id);
		}
		this.id = id;
		int atpos = id.indexOf('@');
		int slashpos;
		boolean hasResource = id.contains("/");
		if (hasResource)
		{
			slashpos = id.indexOf('/');
		}
		else
		{
			slashpos = id.length();
		}
		node = id.substring(0, atpos);
		domain = id.substring(atpos + 1, slashpos);
		if (hasResource)
		{
			resource = id.substring(slashpos + 1, id.length());
		}
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
	
	/**
	 * Thrown when parsing an invalid jabber id
	 * 
	 * @author Manuel Vögele
	 */
	public static class JIDFormatException extends Exception
	{
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 154339518072140058L;

		/**
		 * Instantiates a new JIDFormatException
		 */
		public JIDFormatException()
		{
			super();
		}

		/**
		 * Instantiates a new JIDFormatException
		 * @param detailMessage the message string
		 */
		public JIDFormatException(String detailMessage)
		{
			super(detailMessage);
		}
	}
}
