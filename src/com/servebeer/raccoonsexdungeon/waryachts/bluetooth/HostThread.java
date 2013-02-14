
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.IOException;

import org.andengine.ui.activity.BaseGameActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;

public class HostThread extends Thread
{
	private final BluetoothServerSocket serverSocket;
	private final BluetoothAdapter btAdapter;

	public HostThread()
	{
		// Use a temporary object that is later assigned to serverSocket,
		// because serverSocket is final
		BluetoothServerSocket tmp = null;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		try
		{
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = btAdapter.listenUsingRfcommWithServiceRecord(
					ConnectionHandler.serviceName, ConnectionHandler.uuid);
		}
		catch (IOException e)
		{}
		serverSocket = tmp;
	}

	public void run()
	{
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true)
		{
			try
			{
				socket = serverSocket.accept();
			}
			catch (IOException e)
			{
				break;
			}
			// If a connection was accepted
			if (socket != null)
			{
				// Do work to manage the connection (in a separate thread)
				WarYachtsActivity.getConnectionHandler()
						.handleConnection(socket);
				try
				{
					serverSocket.close();
				}
				catch (IOException e)
				{
					// dunno why this should ever really fail, but ookay
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{}
	}
	
}
