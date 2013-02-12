
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

public class BackgroundFactory extends ContentFactory
{
	public static Background createStartBackground()
	{
		if (LOAD_COMPLETE)
		{
			BitmapTextureAtlas oceanAtlas = new BitmapTextureAtlas(
					activity.getTextureManager(), 1024, 2048,
					TextureOptions.DEFAULT);
			TextureRegion oceanRegion = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(oceanAtlas, activity,
							"backgrounds/ocean.png", 0, 0);
			oceanAtlas.load();
			SpriteBackground oceanSprite = new SpriteBackground(new Sprite(0,
					0, oceanRegion, activity.getVertexBufferObjectManager()));
			return oceanSprite;
		}
		else
			return new Background(0, 0.5f, 0.8f);
	}
}
