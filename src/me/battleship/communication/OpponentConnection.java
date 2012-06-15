package me.battleship.communication;

import java.util.Map;
import java.util.Random;

import me.battleship.communication.messages.BattleshipPacketExtension;
import me.battleship.communication.messages.DicerollMessage;
import me.battleship.communication.messages.ExtensionElements;
import me.battleship.communication.messages.MessageUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;

public class OpponentConnection implements MessageListener
{
	public static final String LOG_TAG = "OpponentConnection";

	int diceroll;

	int opponentDiceroll;

	Random random;

	private Chat chat;
	
	private GameStartListener listener;

	public OpponentConnection(String opponentJID)
	{
		diceroll = 0;
		opponentDiceroll = 0;
		random = new Random();
		Log.i(LOG_TAG, "Establishing connection to " + opponentJID);
		chat = Connection.INSTANCE.connection.getChatManager().createChat(opponentJID, this);
	}

	public void sendDiceroll(GameStartListener listener)
	{
		this.listener = listener;
		diceroll = random.nextInt(5) + 1;
		Log.i(LOG_TAG, "Sending diceroll: " + diceroll);
		try
		{
			chat.sendMessage(new DicerollMessage(diceroll));
			checkDicerolls();
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An error occured while sending the diceroll", e);
		}
	}

	private void checkDicerolls()
	{
		if (diceroll != 0 && opponentDiceroll != 0)
		{
			Log.i(LOG_TAG, "Comparing rolls. Own: " + diceroll + " Opponent: " + opponentDiceroll);
			if (diceroll == opponentDiceroll)
			{
				Log.i(LOG_TAG, "Rerolling...");
				opponentDiceroll = 0;
				diceroll = 0;
				sendDiceroll(listener);
				return;
			}
			listener.onGameStart(diceroll > opponentDiceroll);
			listener = null;
		}
	}

	@Override
	public void processMessage(Chat chat, Message message)
	{
		BattleshipPacketExtension extension = MessageUtil.getPacketExtension(message, ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension dicerollExtension = extension.getSubElement(ExtensionElements.DICEROLL);
		if (dicerollExtension != null)
		{
			Map<String, String> attributes = dicerollExtension.getAttributes();
			opponentDiceroll = Integer.parseInt(attributes.get("dice"));
			Log.i(LOG_TAG, "Received diceroll from opponent: " + opponentDiceroll);
			checkDicerolls();
		}
	}

	public interface GameStartListener
	{
		/**
		 * Indicates that both players have placed their ships and the game can start.
		 * 
		 * @param yourturn whether you have the first turn
		 */
		public void onGameStart(boolean yourturn);
	}
}
