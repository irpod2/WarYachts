
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.text.Text;

public class TextFactory extends ContentFactory
{
	public static int MAX_CHARACTERS = 50;

	public static Text createText(float pX, float pY, String message,
			float scale)
	{
		Text txt = null;
		try
		{
			txt = new Text(pX, pY, deadHand, message, MAX_CHARACTERS,
					activity.getVertexBufferObjectManager());
		}
		catch (Exception e)
		{
			txt = new Text(pX, pY, deadHand, message.substring(0,
					MAX_CHARACTERS), MAX_CHARACTERS,
					activity.getVertexBufferObjectManager());
		}
		txt.setScaleCenter(0, 0);
		txt.setScale(scale);
		return txt;
	}

	public static Text createSimpleText(String message)
	{
		return createText(0, 0, message, 1.0f);
	}
}
