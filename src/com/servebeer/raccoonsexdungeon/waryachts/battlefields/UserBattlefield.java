
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.GameState;

public class UserBattlefield extends Battlefield
{

	public UserBattlefield(GameState gs)
	{
		super(gs);
		attachYachts(gs.getMyYachts());
		fillGrid(gs.getOppShots());
	}

	public boolean isDefeated()
	{
		for (Yacht y : yachts)
		{
			if (!y.isDestroyed())
				return false;
		}
		return true;
	}

	// ===========================================================
	// For when enemy shoots our battlefield
	public YachtInfo shoot(int row, int col)
	{
		for (Yacht y : yachts)
		{
			if (y.shoot(row, col))
			{
				return y.getInfo();
			}
		}
		return null;
	}

	public void hit(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.HIT, this);
		gameState.addOppShot(row, col, ShotType.HIT);
	}

	public void miss(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
		gameState.addOppShot(row, col, ShotType.MISS);
	}

	// ===========================================================

	public Yacht getYacht(float pX, float pY)
	{
		for (Yacht y : yachts)
		{
			if (y.getSprite().contains(pX, pY))
			{
				yachts.remove(y);
				return y;
			}
		}
		return null;
	}

	public boolean contains(float pX, float pY)
	{
		return gridSprite.contains(pX, pY);
	}
}
