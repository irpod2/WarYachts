
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.GameState;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public abstract class Battlefield
{

	public static final int NUM_YACHTS = 5;

	public static final int GRID_SIZE = 10;

	protected GameState gameState;
	protected Shot[][] shots;
	protected Sprite gridSprite;
	protected ArrayList<Yacht> yachts;
	protected boolean touchedDown;

	public Battlefield(GameState gs)
	{
		gameState = gs;
		gridSprite = SpriteFactory.createGrid();
		yachts = new ArrayList<Yacht>();
		shots = new Shot[GRID_SIZE][GRID_SIZE];
		touchedDown = false;
	}

	protected void attachYachts(ArrayList<Yacht> ys)
	{
		for(Yacht y : ys)
		{
			String name = y.getName();
			
			if(name.equals("Helicopter\nCarrier"))
			{
				addYacht(YachtFactory.createCarrierYacht(y.getRow(), y.getColumn(), y.getOrientation()));
			}
			else if(name.equals("Old\nReliable"))
			{
				addYacht(YachtFactory.createDestroyerYacht(y.getRow(), y.getColumn(), y.getOrientation()));
			}
			else if(name.equals("War\nYacht"))
			{
				addYacht(YachtFactory.createWarYacht(y.getRow(), y.getColumn(), y.getOrientation()));
			}
			else if(name.equals("The\nPoseidon"))
			{
				addYacht(YachtFactory.createSubYacht(y.getRow(), y.getColumn(), y.getOrientation()));
			}
			else if(name.equals("The\nSkunker"))
			{
				addYacht(YachtFactory.createSkunkerYacht(y.getRow(), y.getColumn(), y.getOrientation()));
			}
		}
	}

	protected void fillGrid(ShotType[][] shotTypes)
	{
		for (int row = 0; row < GRID_SIZE; row++)
		{
			for (int col = 0; col < GRID_SIZE; col++)
			{
				if (shotTypes[row][col] != ShotType.NONE)
					shots[row][col] = new Shot(row, col, shotTypes[row][col],
							this);
			}
		}
	}

	public float getGridHeight()
	{
		return gridSprite.getHeightScaled();
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

	private boolean checkIntersections(Yacht y)
	{
		for (Yacht intersectionYacht : yachts)
		{
			if (y.intersects(intersectionYacht))
				return true;
		}
		return false;
	}

	public boolean isValidPlacement(Yacht y)
	{
		int row = y.getRow();
		int col = y.getColumn();
		if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE
				|| checkIntersections(y))
			return false;
		switch (y.getOrientation())
		{
		case VERTICAL:
			return (row + y.getUnits() <= GRID_SIZE);
		case HORIZONTAL:
			return (col + y.getUnits() <= GRID_SIZE);
		}
		return false;
	}

	public static int getCellFromPosition(float pos)
	{
		return (int) (pos / SpriteFactory.GRID_CELL_SIZE);
	}
}
