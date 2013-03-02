
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import org.andengine.util.call.Callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;

public class InputCommThread extends Thread
{
	private final int MAX_BUF_SIZE = 1024;
	private final BluetoothAdapter btAdapter;

	private BluetoothServerSocket serverSocket;
	private BluetoothSocket inputSocket;
	private boolean invalid = false;

	private String inMsg;
	private Callback<ControlMessage> messageHandlerCallback;

	public InputCommThread(Callback<ControlMessage> msgHandlerCB)
	{
		// Use a temporary object that is later assigned to serverSocket,
		// because serverSocket is final
		messageHandlerCallback = msgHandlerCB;
		BluetoothServerSocket tmp = null;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		try
		{
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = btAdapter.listenUsingRfcommWithServiceRecord(
					ConnectionHandler.serviceName, ConnectionHandler.uuid);
		}
		catch (IOException e)
		{
			invalid = true;
		}
		serverSocket = tmp;

		inMsg = null;
	}

	public void run()
	{
		if (invalid)
		{
			// Go back to listening
			WarYachtsActivity.getConnectionHandler().listen();
			return;
		}
		// Keep listening until exception occurs or a socket is returned
		while (true)
		{
			try
			{
				inputSocket = serverSocket.accept();
			}
			catch (IOException e)
			{
				// Could not create server socket
				return;
			}
			// If a connection was accepted
			if (inputSocket != null)
			{
				// Set device for future use
				WarYachtsActivity.getConnectionHandler().setDevice(
						inputSocket.getRemoteDevice());
				try
				{
					serverSocket.close();
					serverSocket = null;
				}
				catch (IOException e)
				{
					// dunno why this should ever really fail, but ookay
					e.printStackTrace();
				}
				break;
			}
		}

		// read from socket
		DataInputStream inStream = null;
		try
		{
			inStream = new DataInputStream(inputSocket.getInputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			try
			{
				inputSocket.close();
				inputSocket = null;
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

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
			try
			{
				inputSocket.close();
				inputSocket = null;
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

		// Create string for message
		inMsg = new String(buf);
		inMsg = inMsg.substring(0, bytesRead);
		ControlMessage ctrl = ControlMessage.parseMessage(inMsg);

		// If we've received a message
		if (inMsg.length() != 0)
		{
			// Acks serve to prevent timeouts only
			// Hits and Misses also should be treated as shot acks
			switch(ctrl.getType())
			{
			case ACK:
				// ONLY prevent timeout
				WarYachtsActivity.getConnectionHandler().unqueueMessage(ctrl);
				break;
			case HIT:
			case MISS:
				// Handle timeout AND let scenario handle message
				WarYachtsActivity.getConnectionHandler().unqueueMessage(ctrl);
			default:
				// Everything else is JUST handled by scenario
				messageHandlerCallback.onCallback(ctrl);
			}
		}


		try
		{
			inStream.close();
			inputSocket.close();
			inputSocket = null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Go back to listening
		WarYachtsActivity.getConnectionHandler().listen();

		return;
	}

	public void kill()
	{
		try
		{
			if (serverSocket != null)
				serverSocket.close();
			if (inputSocket != null)
				inputSocket.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
