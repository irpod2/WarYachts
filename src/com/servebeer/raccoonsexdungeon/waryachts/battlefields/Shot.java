
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.entity.sprite.Sprite;

import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class Shot
{
	protected Sprite shotSprite;
	protected ShotType type;
	protected int row;
	protected int col;

	public enum ShotType
	{
		NONE, HIT, MISS
	}

	public Shot(int r, int c, ShotType t, Battlefield b)
	{
		row = r;
		col = c;
		type = t;
		switch (type)
		{
		case NONE:
			shotSprite = null;
			break;
		case HIT:
			shotSprite = SpriteFactory.createHitSprite(row, col);
			b.attachShot(shotSprite);
			break;
		case MISS:
			shotSprite = SpriteFactory.createMissSprite(row, col);
			b.attachShot(shotSprite);
			break;
		}
	}

	public ShotType getType()
	{
		return type;
	}
}
