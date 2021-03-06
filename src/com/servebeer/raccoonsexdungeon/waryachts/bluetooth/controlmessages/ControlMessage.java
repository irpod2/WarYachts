
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages;


import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.YachtType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class ControlMessage
{
	protected String messageString;
	protected ControlType type;
	protected int row;
	protected int col;
	protected CallbackVoid onFailCallback;
	protected YachtInfo yachtInfo;

	public enum ControlType
	{
		SHOOT, HIT, MISS, DESTROYED, ACK, READY, ERROR
	}


	protected ControlMessage(ControlType t, CallbackVoid onFailCB)
	{
		type = t;
		onFailCallback = onFailCB;
	}

	protected ControlMessage()
	{
		type = ControlType.ERROR;
		onFailCallback = new CallbackVoid()
		{
			public void onCallback()
			{

			}
		};
	}

	protected void setType(ControlType t)
	{
		type = t;
	}

	public void setMessage(String str)
	{
		messageString = str;
	}

	protected void setRow(int r)
	{
		row = r;
	}

	protected void setCol(int c)
	{
		col = c;
	}

	protected void setYachtInfo(YachtInfo yi)
	{
		yachtInfo = yi;
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}

	public YachtInfo getYachtInfo()
	{
		return yachtInfo;
	}

	public String getMessage()
	{
		return messageString;
	}

	public ControlType getType()
	{
		return type;
	}

	public void onFail()
	{
		onFailCallback.onCallback();
	}

	public static void parseAckRowCol(String ctrlStr, ControlMessage ctrlMsg)
	{
		char type = ctrlStr.charAt(2);
		if (type == 'M' || type == 'H' || type == 'D')
		{
			int col = ctrlStr.charAt(6) - '0';
			int row = ctrlStr.charAt(7) - '0';
			ctrlMsg.setCol(col);
			ctrlMsg.setRow(row);
		}
	}

	public static ControlMessage parseMessage(String ctrlStr)
	{
		ControlMessage ctrlMsg = new ControlMessage();
		String firstTwo = ctrlStr.substring(0, 2);
		String theRest = ctrlStr.substring(2);
		ctrlMsg.setMessage(ctrlStr);
		// ACK message
		if (firstTwo.equals("A:"))
		{
			ctrlMsg.setType(ControlType.ACK);
			parseAckRowCol(ctrlStr, ctrlMsg);
		}
		// Shoot message
		else if (firstTwo.equals("S:"))
		{
			if (ctrlStr.length() == 4)
			{
				int col = theRest.charAt(0) - '0';
				int row = theRest.charAt(1) - '0';
				ctrlMsg.setCol(col);
				ctrlMsg.setRow(row);
				ctrlMsg.setType(ControlType.SHOOT);
			}
			// ERROR
			else
			{
				ctrlMsg.setMessage("Message received was not of proper length");
			}
		}
		// Destroyed message
		else if (firstTwo.equals("D:"))
		{
			int hitCol = theRest.charAt(2) - '0';
			int hitRow = theRest.charAt(3) - '0';
			ctrlMsg.setCol(hitCol);
			ctrlMsg.setRow(hitRow);
			int delimiter = theRest.indexOf('|');
			String yachtType = theRest.substring(4, delimiter);
			YachtType t = YachtType.valueOf(yachtType);
			int shipCol = theRest.charAt(delimiter + 1) - '0';
			int shipRow = theRest.charAt(delimiter + 2) - '0';
			Orientation o = Orientation.valueOf(theRest
					.substring(delimiter + 3));
			YachtInfo info = new YachtInfo(t, o, shipRow, shipCol);
			ctrlMsg.setYachtInfo(info);
			ctrlMsg.setType(ControlType.DESTROYED);
		}
		// Hit message
		else if (firstTwo.equals("H:"))
		{
			if (ctrlStr.length() == 6)
			{
				int col = theRest.charAt(2) - '0';
				int row = theRest.charAt(3) - '0';
				ctrlMsg.setCol(col);
				ctrlMsg.setRow(row);
				ctrlMsg.setType(ControlType.HIT);
			}
			// ERROR
			else
			{
				ctrlMsg.setMessage("E:Message received was not of proper length");
			}
		}
		// Miss message
		else if (firstTwo.equals("M:"))
		{
			if (ctrlStr.length() == 6)
			{
				int col = theRest.charAt(2) - '0';
				int row = theRest.charAt(3) - '0';
				ctrlMsg.setCol(col);
				ctrlMsg.setRow(row);
				ctrlMsg.setType(ControlType.MISS);
			}
			// ERROR
			else
			{
				ctrlMsg.setMessage("E:Message received was not of proper length");
			}
		}
		// Ready Message
		else if (firstTwo.equals("R:"))
		{
			ctrlMsg.setType(ControlType.READY);
		}
		else
		{
			ctrlMsg.setMessage("E:Message did not match any known pattern");
		}
		return ctrlMsg;
	}

	public static ControlMessage createShootMessage(int row, int col,
			CallbackVoid failCallback)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.SHOOT,
				failCallback);
		ctrlMsg.setMessage("S:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}

	public static ControlMessage createHitMessage(int row, int col)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.HIT,
				new CallbackVoid()
				{
					public void onCallback()
					{

					}
				});
		ctrlMsg.setMessage("H:S:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}

	public static ControlMessage createMissMessage(int row, int col)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.MISS,
				new CallbackVoid()
				{
					public void onCallback()
					{

					}
				});
		ctrlMsg.setMessage("M:S:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}


	// NOT DONE
	public static ControlMessage createDestroyedMessage(int row, int col,
			YachtInfo yi)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.DESTROYED,
				new CallbackVoid()
				{
					public void onCallback()
					{

					}
				});

		ctrlMsg.setMessage("D:S:" + String.valueOf(col) + String.valueOf(row)
				+ yi.yachtType.toString() + "|" + String.valueOf(yi.col)
				+ String.valueOf(yi.row) + yi.orientation.toString());
		return ctrlMsg;
	}

	public static ControlMessage createReadyMessage(CallbackVoid failCallback)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.READY,
				failCallback);
		ctrlMsg.setMessage("R:");
		return ctrlMsg;
	}

	public static ControlMessage createAckMessage(String prevMessage)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.ACK,
				new CallbackVoid()
				{
					public void onCallback()
					{

					}
				});
		ctrlMsg.setMessage("A:" + prevMessage);
		return ctrlMsg;
	}

	public String getSanitizedMessage()
	{
		if (messageString.charAt(1) == ':')
		{
			return messageString.substring(2);
		}
		return messageString;
	}
}
