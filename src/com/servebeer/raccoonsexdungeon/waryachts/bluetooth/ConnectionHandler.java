
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.call.Callback;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
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
		NOT_BUSY, DISCOVERING, HOSTING, CONNECTING
	}

	private BaseGameActivity activity;
	private BluetoothAdapter btAdapter;
	private BroadcastReceiver deviceReceiver;
	private BluetoothDevice opponentDevice;
	private ArrayList<String> discoveredDevices;
	private BusyType busy;
	private Callback<ControlMessage> messageHandlerCallback;
	private CallbackVoid foundGameCallback;

	private InputCommThread inThread;
	private OutputCommThread outThread;
	private ArrayList<MessagePair> unackedMessages;


	public ConnectionHandler(BaseGameActivity bga,
			Callback<ControlMessage> msgHandlerCB, CallbackVoid foundGameCB)
	{
		activity = bga;
		busy = BusyType.NOT_BUSY;
		messageHandlerCallback = msgHandlerCB;
		foundGameCallback = foundGameCB;
		unackedMessages = new ArrayList<MessagePair>();
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
		listen();
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
					opponentDevice = dev;
					Toast.makeText(activity, "Device is set",
							Toast.LENGTH_SHORT).show();
					foundGameCallback.onCallback();
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
					opponentDevice = dev;
					Toast.makeText(activity, "Device is set",
							Toast.LENGTH_SHORT).show();
					foundGameCallback.onCallback();
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

	public void setDevice(BluetoothDevice dev)
	{
		if (opponentDevice == null)
			opponentDevice = dev;
	}

	public void listen()
	{
		busy = BusyType.HOSTING;
		// Go back to listening
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Create Input Comm Thread
				inThread = new InputCommThread(messageHandlerCallback);
				inThread.start();

				// Notify Current Scenario that it can use the network
				messageHandlerCallback.onCallback(null);
			}
		});
	}

	public void sendMsg(ControlMessage ctrlMsg)
	{
		busy = BusyType.CONNECTING;
		outThread = new OutputCommThread(activity, opponentDevice, ctrlMsg);
		outThread.start();
	}

	// Called when message sending fails due to inability to connect
	public void queueMessage(final ControlMessage ctrlMsg)
	{
		// Always run on update thread to prevent concurrency problems
		// (thread-safe)
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				MessageTimer t = new MessageTimer(activity, ctrlMsg);
				unackedMessages.add(new MessagePair(ctrlMsg, t));
				activity.getEngine().registerUpdateHandler(t);
			}
		});
	}

	public void unqueueMessage(final ControlMessage ctrlMsg)
	{
		// Always run on update thread to prevent concurrency problems
		// (thread-safe)
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				final String targetString = ctrlMsg.getSanitizedMessage();
				for (MessagePair msgPair : unackedMessages)
				{
					// If message is found to be unacked
					if (targetString.equals(msgPair.message.getMessage()))
					{
						// Cancel the timer for the entry
						msgPair.timer.cancel();
						// Remove the entry from the list
						unackedMessages.remove(msgPair);

						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(activity,
										"Unqueueing:(" + targetString + ")",
										Toast.LENGTH_SHORT).show();
							}
						});
						// Stop processing (since we just screwed up the
						// iterator by removing an entry)
						return;
					}
				}
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(activity,
								"Did not find:(" + targetString + ")",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	public void unqueueMessages()
	{
		// Always run on update thread to prevent concurrency problems
		// (thread-safe)
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				unackedMessages.clear();
			}
		});
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
				try
				{
					activity.unregisterReceiver(deviceReceiver);
				}
				catch (Exception e)
				{
					Log.e("Reset during discovering",
							"Could not unregister device reciever.");
				}
			}
			break;

		// If connecting,
		case HOSTING:
			if (inThread != null)
			{
				inThread.kill();
				inThread = null;
			}
			break;

		// If Hosting, kill host thread mercilessly
		case CONNECTING:
			if (outThread != null)
			{
				outThread.kill();
				outThread = null;
			}
			break;

		}

		unqueueMessages();

		// Ensure the busy type is NOT_BUSY
		busy = BusyType.NOT_BUSY;
	}

}
