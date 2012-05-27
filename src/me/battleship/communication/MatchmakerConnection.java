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

import android.util.Log;

/**
 * The connection to the matchmaker 
 *
 * @author Manuel Vögele
 */
public class MatchmakerConnection extends TimerTask implements MessageListener
{	
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "MatchmakerConnection";
	
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
	 */
	public MatchmakerConnection()
	{
		this.chat = Connection.INSTANCE.connection.getChatManager().createChat(MATCHMAKER_JID, null);
		timer = new Timer();
	}
	
	/**
	 * Adds the client to the queue on the matchmaker
	 * 
	 * @param assignedListener the listener that will be called when an opponent was assigned
	 */
	public void queue(OpponentAssignedListener assignedListener)
	{
		Log.i(LOG_TAG, "Queuing at matchmaker");
		if (queueId != null)
		{
			Log.i(LOG_TAG, "Allready queued. Queuing aborted.");
			return;
		}
		this.listener = assignedListener;
		chat.addMessageListener(this);
		try
		{
			chat.sendMessage(new QueueMessage());
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An error occured while queuing.", e);
		}
	}
	
	@Override
	public void processMessage(Chat arg0, Message arg1)
	{
		BattleshipPacketExtension extension = MessageUtil.getPacketExtension(arg1, ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension queueing = extension.getSubElement(ExtensionElements.QUEUEING);
		Map<String, String> attributes = queueing.getAttributes();
		String action = attributes.get("action");
		if (action == null)
		{
			return;
		}
		if (action.equals("success"))
		{
			queueId = attributes.get("id");
			Log.i(LOG_TAG, "Received queue id " + queueId);
			timer.scheduleAtFixedRate(this, 15000, 15000);
		}
		else if (action.equals("ping"))
		{
			Log.i(LOG_TAG, "Received ping from Matchmaker");
		}
		else if (action.equals("assign"))
		{
			timer.cancel();
			String opponentJID = attributes.get("jid");
			String matchId = attributes.get("mid");
			Log.i(LOG_TAG, "Assigned to: " + opponentJID + " mid: " + matchId);
			try
			{
				chat.sendMessage(new QueueMessage(opponentJID, matchId));
			}
			catch (XMPPException e)
			{
				Log.w(LOG_TAG, "Error while confirming assignment", e);
			}
			chat.removeMessageListener(this);
			queueId = null;
			listener.onOpponentAssigned(opponentJID, matchId);
		}
		else
		{
			Log.w(LOG_TAG, "Unknown message type: " + action);
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			Log.i(LOG_TAG, "Sending ping to Matchmaker");
			chat.sendMessage(new QueueMessage(queueId));
		}
		catch (XMPPException e)
		{
			Log.w(LOG_TAG, "Error while sending ping to Matchmaker", e);
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
