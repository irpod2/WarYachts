
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.ui.activity.BaseGameActivity;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

public class OutputCommThread extends Thread
{
	private final BluetoothDevice btDevice;
	private final BluetoothAdapter btAdapter;
	private final String outMsg;
	private BluetoothSocket outputSocket;
	private BaseGameActivity activity;

	public OutputCommThread(BaseGameActivity bga, BluetoothDevice device,
			String msg)
	{
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		activity = bga;
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
		outputSocket = tmp;

		outMsg = msg;
	}

	public void run()
	{
		// Cancel discovery because it will slow down the connection
		btAdapter.cancelDiscovery();

		try
		{
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			outputSocket.connect();
		}
		catch (IOException connectException)
		{
			// Unable to connect; close the socket and get out
			try
			{
				outputSocket.close();
			}
			catch (IOException closeException)
			{}

			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity,
							"Could not connect to opponent. Try again later",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		// write to socket
		try
		{
			DataOutputStream outStream = new DataOutputStream(
					outputSocket.getOutputStream());

			outStream.write(outMsg.getBytes());

			outStream.flush();

			outStream.close();

			outputSocket.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WarYachtsActivity.getConnectionHandler().listen();
		
		return;
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void kill()
	{
		try
		{
			outputSocket.close();
		}
		catch (IOException e)
		{}
		WarYachtsActivity.getConnectionHandler().listen();
	}

}
