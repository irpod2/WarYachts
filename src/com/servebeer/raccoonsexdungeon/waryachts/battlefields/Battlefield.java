
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class Battlefield implements ITouchArea
{
	public static final int GRID_SIZE = 10;
	protected Shot[][] shots;
	protected Sprite gridSprite;
	protected ArrayList<Yacht> yachts;
	protected Targeter targeter;

	public Battlefield()
	{
		gridSprite = SpriteFactory.createGrid();
		yachts = new ArrayList<Yacht>();
		shots = new Shot[GRID_SIZE][GRID_SIZE];
		targeter = SpriteFactory.createTargeter();
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

	public void attachTargeter(int row, int col)
	{
		targeter.setLocation(row, col);
		gridSprite.attachChild(targeter);
	}

	public void setTargeter(int row, int col)
	{
		targeter.setLocation(row, col);
	}

	public void shoot(int row, int col)
	{
		gridSprite.detachChild(targeter);
		for (Yacht y : yachts)
		{
			if (y.shoot(row, col))
			{
				shots[row][col] = new Shot(row, col, ShotType.HIT, this);
				return;
			}
		}
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
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

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY)
	{

		int row = getCellFromPosition(pTouchAreaLocalY);
		int col = getCellFromPosition(pTouchAreaLocalX);
		if (0 <= row && row < GRID_SIZE && 0 <= col && col < GRID_SIZE)
		{
			if (pSceneTouchEvent.isActionDown())
				attachTargeter(row, col);
			else if (pSceneTouchEvent.isActionMove()
					&& (targeter.getRow() != row || targeter.getCol() != col))
				setTargeter(row, col);
			else if (pSceneTouchEvent.isActionUp())
			{
				if(shots[row][col] == null)
					shoot(row, col);
				else
					gridSprite.detachChild(targeter);
			}
		}
		return true;
	}
}
