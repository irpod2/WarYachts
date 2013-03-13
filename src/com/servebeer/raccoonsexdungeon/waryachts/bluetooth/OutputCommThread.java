
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage.ControlType;

public class OutputCommThread extends Thread
{
	private final BluetoothDevice btDevice;
	private final BluetoothAdapter btAdapter;
	private final ControlMessage outMsg;
	private BluetoothSocket outputSocket;
	private boolean invalid;

	public OutputCommThread(BluetoothDevice device, ControlMessage msg,
			BluetoothAdapter adapter)
	{
		setDaemon(true);
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		btDevice = device;
		btAdapter = adapter;
		invalid = false;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try
		{
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = btDevice
					.createRfcommSocketToServiceRecord(ConnectionHandler.uuid);
		}
		catch (IOException e)
		{
			invalid = true;
		}
		outputSocket = tmp;

		outMsg = msg;
	}

	public void run()
	{
		// Cancel discovery because it will slow down the connection
		btAdapter.cancelDiscovery();

		if (invalid)
		{
			// WarYachtsActivity.getConnectionHandler().listen();
			Thread.currentThread().interrupt();
			return;
		}

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
				outputSocket = null;
			}
			catch (IOException closeException)
			{
				outputSocket = null;
			}

			// WarYachtsActivity.getConnectionHandler().listen();

			outMsg.onFail();

			Thread.currentThread().interrupt();
			return;
		}


		// Don't queue up acks
		if (outMsg.getType() != ControlType.ACK)
			WarYachtsActivity.getConnectionHandler().queueMessage(outMsg);

		// write to socket
		DataOutputStream outStream;
		try
		{
			outStream = new DataOutputStream(outputSocket.getOutputStream());
		}
		catch (IOException e)
		{
			outStream = null;
			e.printStackTrace();
			// WarYachtsActivity.getConnectionHandler().listen();
			Thread.currentThread().interrupt();
			return;
		}

		try
		{
			outStream.write(outMsg.getMessage().getBytes());

			outStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				outStream.close();
				outStream = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				outStream = null;
			}

			try
			{
				outputSocket.close();
				outputSocket = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				outputSocket = null;
			}
		}

		// WarYachtsActivity.getConnectionHandler().listen();
		Thread.currentThread().interrupt();
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
		{
			outputSocket = null;
		}
	}

}
