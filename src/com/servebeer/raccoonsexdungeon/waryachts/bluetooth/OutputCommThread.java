package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public class OutputCommThread extends Thread
{
	private final BluetoothSocket outputSocket;
	private final String outMsg;
	

	public OutputCommThread(BluetoothSocket socket, String msg)
	{
		outputSocket = socket;
		
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
				outStream.write(outMsg.getBytes());

				outStream.flush();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
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
