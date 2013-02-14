package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.io.IOException;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

public class OutputCommThread extends Thread
{
	private final BluetoothAdapter btAdapter;
	private final BluetoothSocket outputSocket;
	
	private boolean killMe = false;

	public OutputCommThread(BluetoothSocket socket, String msg)
	{
		outputSocket = socket;
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public void run()
	{
		// write to socket
	}

	public void kill()
	{
		killMe = true;
	}
	
}
