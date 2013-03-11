
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.ui.activity.BaseGameActivity;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;

public class MessageTimer implements IUpdateHandler
{
	public static float TIMEOUT_TIME;
	private final ControlMessage ctrlMsg;
	private final BaseGameActivity activity;
	private float totalTimeElapsed;

	public MessageTimer(BaseGameActivity bga, ControlMessage msg)
	{
		TIMEOUT_TIME = (10.0f + ((float) Math.random() * 2.0f));
		activity = bga;
		ctrlMsg = msg;
		totalTimeElapsed = 0.0f;
	}

	public void cancel()
	{
		activity.getEngine().unregisterUpdateHandler(this);
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		totalTimeElapsed += pSecondsElapsed;
		if (totalTimeElapsed > TIMEOUT_TIME)
		{
			activity.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{

					WarYachtsActivity.getConnectionHandler().unqueueMessage(
							ctrlMsg, false);

					WarYachtsActivity.getConnectionHandler().sendMsg(ctrlMsg);
				}
			});
		}
	}


	@Override
	public void reset()
	{
		totalTimeElapsed = 0;
	}
}
