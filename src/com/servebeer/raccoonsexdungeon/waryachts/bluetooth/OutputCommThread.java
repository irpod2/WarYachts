package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

public class OutputCommThread extends Thread
{
	private final BluetoothAdapter btAdapter;
	private final BluetoothSocket outputSocket;
	private final String outMsg;
	

	public OutputCommThread(BluetoothSocket socket, String msg)
	{
		outputSocket = socket;
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		outMsg = msg;
	}
	
	public void run()
	{
		// write to socket
		
		DataOutputStream outStream = null;
		try
		{
			outStream = new DataOutputStream(outputSocket.getOutputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		int bytesToTransfer = 1;
		while (bytesToTransfer > 0) {
		    try
			{
				outStream.writeChars(outMsg);

				outStream.flush();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		    bytesToTransfer -= 1;
		}
		
		try
		{
			outStream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

}
