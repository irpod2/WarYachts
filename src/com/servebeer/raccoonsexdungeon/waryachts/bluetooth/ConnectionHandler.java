
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.R;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class ConnectionHandler
{
	public static final int REQUEST_ENABLE_BT = 1;

	public static final String serviceName = "WarYachtsConnection";
	public static final UUID uuid = UUID.nameUUIDFromBytes("WarYachts"
			.getBytes());

	private BaseGameActivity activity;
	private BluetoothAdapter btAdapter;
	private BroadcastReceiver deviceReceiver;
	private BluetoothSocket socket;
	private CallbackVoid connectionEstablishedCallback;
	private ArrayList<String> discoveredDevices;
	private boolean discovering;
	private GameType gameType;
	private HostThread hostThread;
	private ClientThread clientThread;

	public enum GameType
	{
		HOST, CLIENT
	}

	public ConnectionHandler(BaseGameActivity bga, CallbackVoid connectionCB)
	{
		activity = bga;
		discovering = false;
	}

	public boolean isDiscovering()
	{
		return discovering;
	}

	public void setGameType(GameType type)
	{
		gameType = type;
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
				onBtEnabled(Activity.RESULT_OK);
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
		switch (gameType)
		{
		case HOST:
		{
			hostThread = new HostThread();
			hostThread.run();
			break;
		}
		case CLIENT:
		{
			findDevices();
			break;
		}
		default:
		{
			Toast.makeText(activity, "No game type specified, exiting",
					Toast.LENGTH_SHORT).show();
			activity.finish();
		}
		}
	}

	public void findDevices()
	{
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		if (!pairedDevices.isEmpty())
		{
			final String[] deviceArray = pairedDevices
					.toArray(new String[pairedDevices.size()]);

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

			builder.setNegativeButton(R.string.cancel, null);

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
			discovering = false;
		}
		else
		{
			Toast.makeText(activity, "No devices found in range",
					Toast.LENGTH_SHORT).show();
			discovering = false;
		}
	}

	protected void connectToDevice(BluetoothDevice dev)
	{
		Toast.makeText(
				activity,
				"Connecting to device " + dev.getName() + " at MAC addr "
						+ dev.getAddress(), Toast.LENGTH_SHORT).show();
		clientThread = new ClientThread(dev);
		clientThread.run();
	}

	public void handleConnection(BluetoothSocket sock)
	{
		socket = sock;
		connectionEstablishedCallback.onCallback();
	}

	public void kill()
	{
		if (btAdapter != null && btAdapter.isDiscovering())
		{
			btAdapter.cancelDiscovery();
			activity.unregisterReceiver(deviceReceiver);
		}
	}

}
