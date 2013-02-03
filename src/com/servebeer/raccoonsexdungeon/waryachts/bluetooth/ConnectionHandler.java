
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import org.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.R;

public class ConnectionHandler
{
	public static final int REQUEST_ENABLE_BT = 1;

	protected BaseGameActivity activity;
	private BluetoothAdapter btAdapter;
	private BroadcastReceiver deviceReceiver;
	private ArrayList<String> discoveredDevices;
	private boolean discovering;

	public ConnectionHandler(BaseGameActivity bga)
	{
		activity = bga;
		discovering = false;
	}

	public boolean isDiscovering()
	{
		return discovering;
	}

	public void requestEnableBluetooth()
	{
		discovering = true;
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
				findDevices();
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

	// Called as a response to requestEnableBluetooth()
	public void onBtEnabled(int resultCode)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			Toast.makeText(activity,
					"Successfully enabled bluetooth, searching for devices.",
					Toast.LENGTH_SHORT).show();
			findDevices();
		}
		else
		{
			Toast.makeText(activity,
					"Could not enable bluetooth, War Yachts will now exit.",
					Toast.LENGTH_LONG).show();
			activity.finish();
		}
	}

	protected void findDevices()
	{
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		final String[] deviceArray = pairedDevices
				.toArray(new String[pairedDevices.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Choose an Opponent");

		builder.setItems(deviceArray, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String mac = deviceArray[which].substring(deviceArray[which]
						.lastIndexOf("\n") + 1);
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

		builder.setNegativeButton(R.string.cancel, null);

		builder.show();
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
		final String[] deviceArray = discoveredDevices
				.toArray(new String[discoveredDevices.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Choose an Opponent");

		builder.setItems(deviceArray, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String mac = deviceArray[which].substring(deviceArray[which]
						.lastIndexOf("\n") + 1);
				BluetoothDevice dev = btAdapter.getRemoteDevice(mac);
				connectToDevice(dev);
			}
		});

		builder.setCancelable(true);

		builder.show();
		discovering = false;
	}

	protected void connectToDevice(BluetoothDevice dev)
	{
		Toast.makeText(
				activity,
				"Connecting to device " + dev.getName() + " at MAC addr "
						+ dev.getAddress(), Toast.LENGTH_SHORT).show();
	}

	public void kill()
	{
		if (btAdapter.isDiscovering())
		{
			btAdapter.cancelDiscovery();
			activity.unregisterReceiver(deviceReceiver);
		}
	}

}
