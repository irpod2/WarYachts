
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import org.andengine.ui.activity.BaseGameActivity;

import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

public class InputCommThread extends Thread
{
	private final int MAX_BUF_SIZE = 1024;
	private final BluetoothSocket inputSocket;
	private final BaseGameActivity activity;
	private boolean killMe;
	private String inMsg;

	public InputCommThread(BluetoothSocket socket, BaseGameActivity bga)
	{
		inputSocket = socket;

		activity = bga;

		inMsg = null;

		killMe = false;


	}

	public void run()
	{
		// write to socket

		DataInputStream inStream = null;
		try
		{
			inStream = new DataInputStream(inputSocket.getInputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}


		while (true)
		{
			int bytesRead = 0;
			byte[] buf = new byte[MAX_BUF_SIZE];
			try
			{
				bytesRead = inStream.read(buf);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				killMe = true;
			}
			inMsg = new String(buf);
			inMsg = inMsg.substring(0, bytesRead);

			// If we've received a message
			if (inMsg != null)
			{
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(activity, inMsg, Toast.LENGTH_SHORT)
								.show();

					}


				});
			}

			if (killMe)
			{
				break;
			}

		}

		try
		{
			inStream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	public void kill()
	{
		killMe = true;
	}


}
