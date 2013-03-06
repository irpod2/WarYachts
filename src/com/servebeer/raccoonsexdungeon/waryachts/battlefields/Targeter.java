
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class Targeter extends Sprite
{
	protected int row;
	protected int col;
	
	public Targeter(int pRow, int pCol, float pX, float pY, float wid, float ht,
			TextureRegion texRgn, VertexBufferObjectManager vbom)
	{
		super(pX, pY, wid, ht, texRgn, vbom);
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public void setLocation(int pRow, int pCol)
	{
		mX = SpriteFactory.getCellLocation(pCol);
		mY = SpriteFactory.getCellLocation(pRow);
		row = pRow;
		col = pCol;
	}
}
