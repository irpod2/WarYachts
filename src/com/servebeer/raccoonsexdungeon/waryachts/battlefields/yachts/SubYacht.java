
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.sprite.Sprite;

public class SubYacht extends Yacht
{
	public final static int UNITS = 3;

	public SubYacht(int r, int c, Orientation o, Sprite sprite)
	{
		super("The\nPoseidon", r, c, o, UNITS, sprite);
	}
}
