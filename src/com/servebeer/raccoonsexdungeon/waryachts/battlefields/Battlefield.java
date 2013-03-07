
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public abstract class Battlefield implements ITouchArea
{
	public static final int GRID_SIZE = 10;
	protected Shot[][] shots;
	protected Sprite gridSprite;
	protected ArrayList<Yacht> yachts;
	protected boolean touchedDown;

	public Battlefield()
	{
		gridSprite = SpriteFactory.createGrid();
		yachts = new ArrayList<Yacht>();
		shots = new Shot[GRID_SIZE][GRID_SIZE];
		touchedDown = false;
	}

	public boolean addYacht(Yacht y)
	{
		if (yachts.add(y))
		{
			gridSprite.attachChild(y.getSprite());
			return true;
		}
		else
			return false;
	}

	public void attachShot(Sprite shot)
	{
		gridSprite.attachChild(shot);
	}

	public Sprite getSprite()
	{
		return gridSprite;
	}

	public int getCellFromPosition(float pos)
	{
		return (int) (pos / SpriteFactory.GRID_CELL_SIZE);
	}

	@Override
	public boolean contains(float pX, float pY)
	{
		return gridSprite.contains(pX, pY);
	}

	@Override
	public float[] convertSceneToLocalCoordinates(float pX, float pY)
	{
		float[] coords = new float[2];
		coords[0] = pX - gridSprite.getX() - SpriteFactory.GRID_PADDING;
		coords[1] = pY - gridSprite.getY() - SpriteFactory.GRID_PADDING;
		return coords;
	}

	@Override
	public float[] convertLocalToSceneCoordinates(float pX, float pY)
	{
		float[] coords = new float[2];
		coords[0] = pX + gridSprite.getX() + SpriteFactory.GRID_PADDING;
		coords[1] = pY + gridSprite.getY() + SpriteFactory.GRID_PADDING;
		return coords;
	}
}
