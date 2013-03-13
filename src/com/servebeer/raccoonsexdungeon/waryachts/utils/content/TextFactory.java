
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

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

	public static Text createWarYachtsBannerText()
	{
		Text t = createText(-2.0f, -2.0f, "War Yachts", 1.0f);
		t.setColor(Color.WHITE);
		return t;
	}

	public static Text createWarYachtsBannerTextShadow()
	{
		Text t = createSimpleText("War Yachts");
		t.setColor(Color.BLACK);
		t.setScale(1.75f);
		t.setX(cameraWidth / 2.0f - t.getWidthScaled() / 2.0f);
		t.setY(cameraHeight / ButtonFactory.NUM_MENU_DIVISIONS
				- t.getHeightScaled() / 2.0f);

		return t;
	}
}
