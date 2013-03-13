
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
	protected static final int NUM_MENU_DIVISIONS = 6;

	protected static final float MENU_BUTTON_HEIGHT = 100.0f * SIZE_RATIO;


	public static ButtonSprite createButton(OnClickListener listener)
	{
		BitmapTextureAtlas buttonAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 512, 512);
		TiledTextureRegion buttonRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(buttonAtlas, activity,
						"sprites/Button.png", 0, 0, 1, 3);
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
		b.setScaleX((t.getWidthScaled() + 40.0f) / b.getWidthScaled());
		t.setScaleX(t.getScaleX() / b.getScaleX());
		t.setScaleY(t.getScaleY() / b.getScaleY());
		t.setX(b.getWidth() / 2.0f - t.getWidthScaled() / 2.0f);
		t.setY(b.getHeight() / 2.0f - t.getHeightScaled() / 2.0f);
	}

	public static ButtonSprite createReadyButton(float height,
			OnClickListener listener)
	{
		// Create a basic button
		ButtonSprite readyButton = createButton(listener);

		// Scale it like a ready button (vertically)
		readyButton.setScaleCenter(0, 0);
		readyButton.setScaleY(height / readyButton.getHeightScaled());

		// Create button text
		Text readyText = TextFactory.createSimpleText("Ready");
		centerTextOnButton(readyText, readyButton);
		readyText.setColor(Color.BLACK);

		// Set button on bottom of screen
		readyButton.setY(cameraHeight - readyButton.getHeightScaled());

		// Center the button on the screen horizontally
		readyButton.setX(cameraWidth / 2.0f - readyButton.getWidthScaled()
				/ 2.0f);

		// Attach text and return button
		readyButton.attachChild(readyText);
		return readyButton;
	}

	public static ButtonSprite createFireButton(OnClickListener listener)
	{
		// Create a basic button
		ButtonSprite readyButton = createButton(listener);

		// Create button text
		Text readyText = TextFactory.createSimpleText("Fire!");

		// Scale it like a fire button (vertically)
		readyButton.setScaleCenter(0, 0);
		readyButton.setScaleY((readyText.getHeight() + 40.0f)
				/ readyButton.getHeightScaled());

		// Scale text
		centerTextOnButton(readyText, readyButton);
		readyText.setColor(Color.BLACK);

		// Set button on bottom of screen
		readyButton.setY(cameraHeight - readyButton.getHeightScaled());

		// Center the button on the screen horizontally
		readyButton.setX(cameraWidth / 2.0f - readyButton.getWidthScaled()
				/ 2.0f);

		// Attach text and return button
		readyButton.attachChild(readyText);
		return readyButton;
	}

	public static ButtonSprite createMenuButton(String name, int divNo,
			OnClickListener listener)
	{
		// Create a basic button
		ButtonSprite menuButton = createButton(listener);

		// Scale it like a menu button (vertically)
		menuButton.setScaleCenter(0, 0);
		menuButton.setScaleY(MENU_BUTTON_HEIGHT / menuButton.getHeightScaled());

		// Create button text
		Text menuText = TextFactory.createSimpleText(name);
		centerTextOnButton(menuText, menuButton);
		menuText.setColor(Color.BLACK);

		// Set button to height according to which division of the screen it
		// should be in
		menuButton.setY(divNo * (float) cameraHeight / NUM_MENU_DIVISIONS
				- menuButton.getHeightScaled() / 2.0f);

		// Center the button on the screen horizontally
		menuButton
				.setX(cameraWidth / 2.0f - menuButton.getWidthScaled() / 2.0f);

		// Attach text and return button
		menuButton.attachChild(menuText);
		return menuButton;
	}
}
