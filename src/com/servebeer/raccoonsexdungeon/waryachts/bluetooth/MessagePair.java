package com.servebeer.raccoonsexdungeon.waryachts.bluetooth;

import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;

public class MessagePair
{
	public final ControlMessage message;
	public final MessageTimer timer;
	
	public MessagePair(ControlMessage msg, MessageTimer t)
	{
		message = msg;
		timer = t;
	}
}
