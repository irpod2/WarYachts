
package com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages;

public class ControlMessage
{
	protected String messageString;
	protected ControlType type;
	protected int row;
	protected int col;

	public enum ControlType
	{
		CHAT, SHOOT, HIT, MISS, ACK, ERROR
	}


	protected ControlMessage(ControlType t)
	{
		type = t;
	}

	protected ControlMessage()
	{
		type = ControlType.ERROR;
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

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}

	public String getMessage()
	{
		return messageString;
	}

	public ControlType getType()
	{
		return type;
	}

	public static ControlMessage parseMessage(String ctrlStr)
	{
		ControlMessage ctrlMsg = new ControlMessage();
		String firstTwo = ctrlStr.substring(0, 2);
		String theRest = ctrlStr.substring(2);
		ctrlMsg.setMessage(theRest);
		// ACK message
		if (firstTwo.equals("A:"))
		{
			ctrlMsg.setType(ControlType.ACK);
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
		// Hit message
		else if (firstTwo.equals("H:"))
		{
			if (ctrlStr.length() == 4)
			{
				int col = theRest.charAt(0) - '0';
				int row = theRest.charAt(1) - '0';
				ctrlMsg.setCol(col);
				ctrlMsg.setRow(row);
				ctrlMsg.setType(ControlType.HIT);
			}
			// ERROR
			else
			{
				ctrlMsg.setMessage("Message received was not of proper length");
			}
		}
		// Miss message
		else if (firstTwo.equals("M:"))
		{
			if (ctrlStr.length() == 4)
			{
				int col = theRest.charAt(0) - '0';
				int row = theRest.charAt(1) - '0';
				ctrlMsg.setCol(col);
				ctrlMsg.setRow(row);
				ctrlMsg.setType(ControlType.MISS);
			}
			// ERROR
			else
			{
				ctrlMsg.setMessage("Message received was not of proper length");
			}
		}
		// Chat Message
		else if (firstTwo.equals("C:"))
		{
			ctrlMsg.setType(ControlType.CHAT);
		}
		else
		{
			ctrlMsg.setMessage("Message did not match any known pattern");
		}
		return ctrlMsg;
	}

	public static ControlMessage createShootMessage(int row, int col)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.SHOOT);
		ctrlMsg.setMessage("S:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}

	public static ControlMessage createHitMessage(int row, int col)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.HIT);
		ctrlMsg.setMessage("H:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}

	public static ControlMessage createMissMessage(int row, int col)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.MISS);
		ctrlMsg.setMessage("M:" + String.valueOf(col) + String.valueOf(row));
		return ctrlMsg;
	}

	public static ControlMessage createChatMessage(String message)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.CHAT);
		ctrlMsg.setMessage("C:" + message);
		return ctrlMsg;
	}

	public static ControlMessage createAckMessage(String prevMessage)
	{
		ControlMessage ctrlMsg = new ControlMessage(ControlType.ACK);
		ctrlMsg.setMessage("A:" + prevMessage);
		return ctrlMsg;
	}
	
	public String getSanitizedMessage()
	{
		if(messageString.charAt(1) == ':')
		{	
			return messageString.substring(2);	
		}
		
		return messageString;
			
	}
	/*
	 * public static char getColCoord(int col) { return (char) (col + 'A'); }
	 * 
	 * public static int getRowCoord(int row) { return row + 1; }
	 * 
	 * public static int getColIndex(char col) { return (int) (col - 'A'); }
	 * 
	 * public static int getRowIndex(String row) { return Integer.parseInt(row)
	 * - 1; }
	 */
}
