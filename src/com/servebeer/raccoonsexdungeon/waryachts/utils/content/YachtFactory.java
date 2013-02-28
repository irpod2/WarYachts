
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Carrier;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Destroyer;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Skunker;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.SubYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.WarYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht.Orientation;

public class YachtFactory extends SpriteFactory
{
	protected static ArrayList<TextureRegion> blueYachtRegions;
	protected static ArrayList<TextureRegion> redYachtRegions;

	public static final int SHIPNO_CARRIER = 0;
	public static final int SHIPNO_DESTROYER = 1;
	public static final int SHIPNO_SUBYACHT = 2;
	public static final int SHIPNO_WARYACHT = 3;
	public static final int SHIPNO_SKUNKER = 4;

	public static void loadContent()
	{
		blueYachtRegions = new ArrayList<TextureRegion>(5);
		redYachtRegions = new ArrayList<TextureRegion>(5);

		BitmapTextureAtlas yachtAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 512, 512);

		// Blue Team
		TextureRegion blueCarrier = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueCarrier.setTextureWidth(320);
		blueCarrier.setTextureHeight(64);
		blueYachtRegions.add(blueCarrier);
		TextureRegion blueDestroyer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueDestroyer.setTextureWidth(256);
		blueDestroyer.setTextureHeight(64);
		blueDestroyer.setTextureY(64);
		blueYachtRegions.add(blueDestroyer);
		TextureRegion blueWarYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueWarYacht.setTextureWidth(192);
		blueWarYacht.setTextureHeight(64);
		blueWarYacht.setTextureY(128);
		blueYachtRegions.add(blueWarYacht);
		TextureRegion blueSubYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueSubYacht.setTextureWidth(192);
		blueSubYacht.setTextureHeight(64);
		blueSubYacht.setTextureY(192);
		blueYachtRegions.add(blueSubYacht);
		TextureRegion blueSkunker = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueSkunker.setTextureWidth(128);
		blueSkunker.setTextureHeight(64);
		blueSkunker.setTextureY(256);
		blueYachtRegions.add(blueSkunker);

		// Red Team
		TextureRegion redCarrier = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redCarrier.setTextureWidth(320);
		redCarrier.setTextureHeight(64);
		redCarrier.setTextureX(192);
		redCarrier.setTextureY(256);
		redYachtRegions.add(redCarrier);
		TextureRegion redDestroyer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redDestroyer.setTextureWidth(256);
		redDestroyer.setTextureHeight(64);
		redDestroyer.setTextureX(256);
		redDestroyer.setTextureY(192);
		redYachtRegions.add(redDestroyer);
		TextureRegion redSubYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redSubYacht.setTextureWidth(192);
		redSubYacht.setTextureHeight(64);
		redSubYacht.setTextureX(320);
		redSubYacht.setTextureY(128);
		redYachtRegions.add(redSubYacht);
		TextureRegion redWarYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redWarYacht.setTextureWidth(192);
		redWarYacht.setTextureHeight(64);
		redWarYacht.setTextureX(320);
		redWarYacht.setTextureY(64);
		redYachtRegions.add(redWarYacht);
		TextureRegion redSkunker = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redSkunker.setTextureWidth(128);
		redSkunker.setTextureHeight(64);
		redSkunker.setTextureX(384);
		redYachtRegions.add(redSkunker);
		yachtAtlas.load();
	}

	protected static Sprite createBlueYacht(int row, int col, Orientation or,
			int shipNo)
	{

		Sprite yachtSprite = new Sprite(getCellLocation(col),
				getCellLocation(row - (or == Orientation.VERTICAL ? 1 : 0)),
				blueYachtRegions.get(shipNo),
				activity.getVertexBufferObjectManager());
		yachtSprite.setScaleCenter(0, 0);
		yachtSprite.setScale(GRID_CELL_SIZE / yachtSprite.getHeight());
		switch (or)
		{
		case HORIZONTAL:
			break;
		case VERTICAL:
			// Rotate from lower left-corner as anchor point
			yachtSprite.setRotationCenter(0, yachtSprite.getHeightScaled());
			yachtSprite.setRotation(90.0f);
			// Sprite appears displaced vertically by 1 row, so counter this
			// by adding 1 row
			break;
		}
		return yachtSprite;
	}

	public static Carrier createCarrierYacht(int row, int col, Orientation or)
	{
		Carrier y = new Carrier(row, col, or, createBlueYacht(row, col, or,
				SHIPNO_CARRIER));
		return y;
	}

	public static Destroyer createDestroyerYacht(int row, int col,
			Orientation or)
	{
		Destroyer y = new Destroyer(row, col, or, createBlueYacht(row, col, or,
				SHIPNO_DESTROYER));
		return y;
	}

	public static SubYacht createSubYacht(int row, int col, Orientation or)
	{
		SubYacht y = new SubYacht(row, col, or, createBlueYacht(row, col, or,
				SHIPNO_SUBYACHT));
		return y;
	}

	public static WarYacht createWarYacht(int row, int col, Orientation or)
	{
		WarYacht y = new WarYacht(row, col, or, createBlueYacht(row, col, or,
				SHIPNO_WARYACHT));
		return y;
	}

	public static Skunker createSkunkerYacht(int row, int col, Orientation or)
	{
		Skunker y = new Skunker(row, col, or, createBlueYacht(row, col, or,
				SHIPNO_SKUNKER));
		return y;
	}
}
