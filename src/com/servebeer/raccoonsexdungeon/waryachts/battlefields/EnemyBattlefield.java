
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.GameState;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.GameInstanceScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class EnemyBattlefield extends Battlefield implements ITouchArea
{
	protected Targeter targeter;
	protected Targeter selectedLocation;
	protected GameInstanceScenario gameInstance;

	public EnemyBattlefield(GameState gs, GameInstanceScenario gis)
	{
		super(gs);
		gameInstance = gis;
		fillGrid(gs.getMyShots());
		attachYachts(gs.getOppYachts());
		targeter = SpriteFactory.createTargeter();
		selectedLocation = SpriteFactory.createTargetSelector();
	}

	public boolean isTargeterSet()
	{
		return selectedLocation.hasParent();
	}

	public boolean defeatedAllEnemies()
	{
		for (Yacht y : yachts)
		{
			if (y == null || !y.isDestroyed())
				return false;
		}
		return true;
	}

	protected void attachTargeter(int row, int col)
	{
		setTargeter(row, col);
		if (!targeter.hasParent())
			shotLayer.attachChild(targeter);
	}

	protected void setTargeter(int row, int col)
	{
		if (shots[row][col] == null)
		{
			targeter.setLocation(row, col);
		}
	}

	protected void selectLocation(int row, int col)
	{
		if (shots[row][col] == null)
		{
			selectedLocation.setLocation(row, col);
			gameInstance.setTargeter(row, col);
			if (!selectedLocation.hasParent())
			{
				shotLayer.attachChild(selectedLocation);
			}
		}

	}

	public int getSelectedRow()
	{
		return selectedLocation.getRow();
	}

	public int getSelectedCol()
	{
		return selectedLocation.getCol();
	}

	// ===========================================================
	// For confirmation of hit or miss on enemy battlefield
	public void hit(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.HIT, this);
		gameState.addMyShot(row, col, ShotType.HIT);
		if (selectedLocation.hasParent())
		{
			gameInstance.unsetTargeter();
			shotLayer.detachChild(selectedLocation);
		}
	}

	public void miss(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
		gameState.addMyShot(row, col, ShotType.MISS);
		if (selectedLocation.hasParent())
		{
			gameInstance.unsetTargeter();
			shotLayer.detachChild(selectedLocation);
		}
	}

	// ===========================================================


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
		if (pSceneTouchEvent.isActionDown())
			touchedDown = true;
		if (!touchedDown)
			return false;

		int row = getCellFromPosition(pTouchAreaLocalY);
		int col = getCellFromPosition(pTouchAreaLocalX);
		if (0 <= row && row < GRID_SIZE && 0 <= col && col < GRID_SIZE)
		{
			if (pSceneTouchEvent.isActionDown())
				attachTargeter(row, col);
			else if (pSceneTouchEvent.isActionMove()
					&& (targeter.getRow() != row || targeter.getCol() != col))
			{
				if (!targeter.hasParent())
					shotLayer.attachChild(targeter);

				setTargeter(row, col);
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				touchedDown = false;
				setTargeter(row, col);
				selectLocation(row, col);
				if (targeter.hasParent())
					shotLayer.detachChild(targeter);
			}
		}
		else if (targeter.hasParent())
		{
			shotLayer.detachChild(targeter);
		}
		return true;
	}
}
