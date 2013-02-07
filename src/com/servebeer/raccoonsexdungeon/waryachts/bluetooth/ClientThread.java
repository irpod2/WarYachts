
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;

public class ClientThread extends Thread
{
	private final BluetoothSocket btSocket;
	private final BluetoothDevice btDevice;
	private final BluetoothAdapter btAdapter;

	public ClientThread(BluetoothDevice device)
	{
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		btDevice = device;
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try
		{
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = btDevice
					.createRfcommSocketToServiceRecord(ConnectionHandler.uuid);
		}
		catch (IOException e)
		{}
		btSocket = tmp;
	}

	public void run()
	{
		// Cancel discovery because it will slow down the connection
		btAdapter.cancelDiscovery();

		try
		{
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			btSocket.connect();
		}
		catch (IOException connectException)
		{
			// Unable to connect; close the socket and get out
			try
			{
				btSocket.close();
			}
			catch (IOException closeException)
			{}
			return;
		}

		// Do work to manage the connection (in a separate thread)
		WarYachtsActivity.getConnectionHandler().handleConnection(btSocket);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel()
	{
		try
		{
			btSocket.close();
		}
		catch (IOException e)
		{}
	}
}
