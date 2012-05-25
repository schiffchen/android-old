package me.battleship.communication;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.battleship.communication.messages.BattleshipPacketExtension;
import me.battleship.communication.messages.ExtensionElements;
import me.battleship.communication.messages.MessageUtil;
import me.battleship.communication.messages.QueueMessage;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * The connection to the matchmaker 
 *
 * @author Manuel Vögele
 */
public class MatchmakerConnection extends TimerTask implements MessageListener
{
	/**
	 * The jabber id of the matchmaker
	 */
	public static final String MATCHMAKER_JID = "matchmaker@battleship.me";
	
	/**
	 * The connection
	 */
	private Chat chat;
	
	/**
	 * The queueid
	 */
	private String queueId;
	
	/**
	 * A timer
	 */
	private Timer timer;
	
	/**
	 * A listener used when an opponent was assigned
	 */
	private OpponentAssignedListener listener;
	
	/**
	 * Creates a new instance
	 * @param connection the connection to use for the matchmaker connection
	 */
	public MatchmakerConnection(Connection connection)
	{
		this.chat = connection.getChatManager().createChat(MATCHMAKER_JID, null);
		timer = new Timer();
	}
	
	/**
	 * Adds the client to the queue on the matchmaker
	 * 
	 * @param assignedListener the listener that will be called when an opponent was assigned
	 * @throws XMPPException when an exception occurs while sending messages
	 */
	public void queue(OpponentAssignedListener assignedListener) throws XMPPException
	{
		if (queueId != null)
		{
			return;
		}
		this.listener = assignedListener;
		chat.addMessageListener(this);
		chat.sendMessage(new QueueMessage());
	}
	
	@Override
	public void processMessage(Chat arg0, Message arg1)
	{
		BattleshipPacketExtension extension = MessageUtil.getPacketExtension(arg1, ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = extension.getSubElement(ExtensionElements.QUEUEING);
		Map<String, String> attributes = queueing.getAttributes();
		String action = attributes.get("action");
		if (action.equals("success"))
		{
			queueId = attributes.get("id");
			timer.scheduleAtFixedRate(this, 15000, 15000);
		}
		else if (action.equals("ping"))
		{
			System.out.println("Received ping.");
		}
		else if (action.equals("assign"))
		{
			timer.cancel();
			String opponentJID = attributes.get("jid");
			String matchId = attributes.get("mid");
			try
			{
				chat.sendMessage(new QueueMessage(opponentJID, matchId));
			}
			catch (XMPPException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chat.removeMessageListener(this);
			queueId = null;
			listener.onOpponentAssigned(opponentJID, matchId);
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			chat.sendMessage(new QueueMessage(queueId));
			System.out.println("Sending ping to matchmaker...");
		}
		catch (XMPPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This will be sent when an opponent was assigned by the matchmaker
	 *
	 * @author Manuel Vögele
	 */
	public interface OpponentAssignedListener
	{
		/**
		 * Called when an opponent was assigned by the matchmaker
		 * 
		 * @param jid the jabber id of the opponent
		 * @param matchId the matchId
		 */
		public void onOpponentAssigned(String jid, String matchId);
	}
}
