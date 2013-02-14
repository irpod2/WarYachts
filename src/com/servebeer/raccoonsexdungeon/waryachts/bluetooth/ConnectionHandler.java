
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.R;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class ConnectionHandler
{
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_ENABLE_DISCOVERY = 2;

	public static final String serviceName = "WarYachtsConnection";
	public static final UUID uuid = UUID.nameUUIDFromBytes("WarYachts"
			.getBytes());
	public static final int DEFAULT_DISCOVERY_TIME = 120;
	
	public enum BusyType
	{
		NOT_BUSY,
		DISCOVERING,
		HOSTING,
		CONNECTING		
	}

	private BaseGameActivity activity;
	private BluetoothAdapter btAdapter;
	private BroadcastReceiver deviceReceiver;
	private BluetoothSocket socket;
	private CallbackVoid connectionEstablishedCallback;
	private CallbackVoid noConnectionEstablishedCallback;
	private ArrayList<String> discoveredDevices;
	private BusyType busy;
	private HostThread hostThread;
	private ClientThread clientThread;
	

	public ConnectionHandler(BaseGameActivity bga, CallbackVoid connectionCB,
			CallbackVoid noConnectionCB)
	{
		activity = bga;
		connectionEstablishedCallback = connectionCB;
		noConnectionEstablishedCallback = noConnectionCB;
		busy = BusyType.NOT_BUSY;
	}

	public BusyType getBusyType()
	{
		return busy;
	}

	public void requestEnableBluetooth()
	{
		busy = BusyType.DISCOVERING;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter != null)
		{
			if (!btAdapter.isEnabled())
			{
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				activity.startActivityForResult(enableBtIntent,
						REQUEST_ENABLE_BT);
			}
			else
			{
				onBtEnabled();
			}
		}
		else
		{
			// Device does not support Bluetooth
			Toast.makeText(
					activity,
					"Your phone does not seem to support bluetooth, War Yachts will now exit.",
					Toast.LENGTH_LONG).show();
			activity.finish();
		}
	}

	public void requestEnableDiscovery()
	{
		busy = BusyType.HOSTING;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter != null)
		{
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			activity.startActivityForResult(enableBtIntent,
					REQUEST_ENABLE_DISCOVERY);
		}
		else
		{
			// Device does not support Bluetooth
			Toast.makeText(
					activity,
					"Your phone does not seem to support bluetooth, War Yachts will now exit.",
					Toast.LENGTH_LONG).show();
			activity.finish();
		}
	}

	// Called as a response to requestEnableBluetooth()
	public void onBtEnabled()
	{
		findDevices();
	}

	public void onDiscoveryEnabled()
	{
		hostThread = new HostThread();
		hostThread.start();
	}
	
	public BluetoothSocket getSocket()
	{
		return socket;
	}

	public void findDevices()
	{
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		if (!pairedDevices.isEmpty())
		{
			// Put devices in string array of Name\nAddress pairs
			final String[] deviceArray = new String[pairedDevices.size()];
			int i = 0;
			for (BluetoothDevice device : pairedDevices)
			{
				deviceArray[i] = device.getName() + "\n" + device.getAddress();
				i++;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Choose an Opponent");

			builder.setItems(deviceArray, new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					String mac = deviceArray[which]
							.substring(deviceArray[which].lastIndexOf("\n") + 1);
					BluetoothDevice dev = btAdapter.getRemoteDevice(mac);
					connectToDevice(dev);
				}
			});

			builder.setPositiveButton(R.string.scan_for_devices,
					new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Toast.makeText(activity,
									"Scanning for more devices, please wait",
									Toast.LENGTH_SHORT).show();
							scan();
						}
					});

			builder.setNegativeButton(R.string.cancel, new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					busy = BusyType.NOT_BUSY;
				}
			});

			builder.setOnCancelListener(new OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialog)
				{
					busy = BusyType.NOT_BUSY;
				}
			});

			builder.show();
		}
		else
		{
			Toast.makeText(activity, "Scanning for devices, please wait",
					Toast.LENGTH_SHORT).show();
			scan();
		}
	}

	public void scan()
	{
		discoveredDevices = new ArrayList<String>();
		// Create a BroadcastReceiver for ACTION_FOUND
		deviceReceiver = new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction(); // When discovery finds a
													// device
				if (BluetoothDevice.ACTION_FOUND.equals(action))
				{ // Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a
					// ListView
					discoveredDevices.add(device.getName() + "\n"
							+ device.getAddress());
				}
				else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action))
				{
					activity.unregisterReceiver(deviceReceiver);
					chooseDevice();
				}
			}
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		activity.registerReceiver(deviceReceiver, filter);
		btAdapter.startDiscovery();
	}

	protected void chooseDevice()
	{
		if (!discoveredDevices.isEmpty())
		{
			final String[] deviceArray = discoveredDevices
					.toArray(new String[discoveredDevices.size()]);

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Choose an Opponent");

			builder.setItems(deviceArray, new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					String mac = deviceArray[which]
							.substring(deviceArray[which].lastIndexOf("\n") + 1);
					BluetoothDevice dev = btAdapter.getRemoteDevice(mac);
					connectToDevice(dev);
					
				}
			});

			builder.setNegativeButton(R.string.cancel, null);

			builder.setCancelable(true);

			builder.show();
			busy = BusyType.NOT_BUSY;
		}
		else
		{
			Toast.makeText(activity, "No devices found in range",
					Toast.LENGTH_SHORT).show();
			busy = BusyType.NOT_BUSY;
		}
	}

	protected void connectToDevice(BluetoothDevice dev)
	{
		busy = BusyType.CONNECTING;
		
		Toast.makeText(
				activity,
				"Connecting to device " + dev.getName() + " at MAC addr "
						+ dev.getAddress(), Toast.LENGTH_SHORT).show();
		clientThread = new ClientThread(dev);
		clientThread.start();
	}

	public void handleConnection(BluetoothSocket sock)
	{
		socket = sock;
		busy = BusyType.NOT_BUSY;
		connectionEstablishedCallback.onCallback();
	}

	public void noConnection()
	{
		busy = BusyType.NOT_BUSY;
		noConnectionEstablishedCallback.onCallback();
	}

	public void reset()
	{
		switch (busy)
		{
		// If not busy, no need to do anything
		case NOT_BUSY:
			break;
			
		// If discovering, cancel discovery and unregister the receiver	
		case DISCOVERING:
			if (btAdapter != null && btAdapter.isDiscovering())
			{
				btAdapter.cancelDiscovery();
				try{
					activity.unregisterReceiver(deviceReceiver);
				}catch(Exception e)
				{
					Log.e("Reset during discovering", "Could not unregister device reciever.");
				}
			}
			break;

		// If connecting, 	
		case CONNECTING:
			if( clientThread != null)
			{
				clientThread.cancel();
			}
			break;
			
		// If Hosting, kill host thread mercilessly 	
		case HOSTING:
			if ( hostThread != null )
			{
				hostThread.cancel();
				hostThread = null;
			}
			break;
			
		}
		
		// Ensure the busy type is NOT_BUSY
		busy = BusyType.NOT_BUSY;
	}

}
