
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;

public class ButtonFactory extends ContentFactory
{
	protected static final int NUM_MENU_BUTTONS = 5;
	
	public static ButtonSprite createButton(OnClickListener listener)
	{
		BitmapTextureAtlas buttonAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 256);
		TiledTextureRegion buttonRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(buttonAtlas, activity,
						"sprites/WarYachtsButton.png", 0, 0, 1, 4);
		buttonAtlas.load();
		ButtonSprite buttonSprite = new ButtonSprite(0, 0,
				buttonRegion.getTextureRegion(0),
				buttonRegion.getTextureRegion(1),
				buttonRegion.getTextureRegion(2),
				activity.getVertexBufferObjectManager(), listener);
		return buttonSprite;
	}

	public static void centerTextOnButton(Text t, ButtonSprite b)
	{
		t.setX(b.getWidthScaled() / 2.0f - t.getWidthScaled() / 2.0f);
		t.setY(b.getHeightScaled() / 2.0f - t.getHeightScaled() / 2.0f);
	}

	public static ButtonSprite createMenuButton(String name, int divNo,
			OnClickListener listener)
	{
		// Create a basic button
		ButtonSprite menuButton = createButton(listener);

		// Center the button on the screen horizontally
		menuButton
				.setX(cameraWidth / 2.0f - menuButton.getWidthScaled() / 2.0f);

		// Set button to height according to which division of the screen it
		// should be in
		menuButton.setY(divNo * (float)cameraHeight / NUM_MENU_BUTTONS
				- menuButton.getHeightScaled() / 2.0f);

		// Create button text
		Text menuText = TextFactory.createSimpleText(name);
		centerTextOnButton(menuText, menuButton);
		menuText.setColor(Color.BLACK);

		// Attach text and return button
		menuButton.attachChild(menuText);
		return menuButton;
	}
}
