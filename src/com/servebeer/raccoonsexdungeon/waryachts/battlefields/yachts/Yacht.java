
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.YachtType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ContentFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public abstract class Yacht
{
	protected Sprite yachtSprite;
	protected YachtInfo yachtInfo;
	protected Rectangle badPlacementRectangle;

	public Yacht(YachtInfo yi, Sprite sprite)
	{
		yachtInfo = yi;
		yachtSprite = sprite;
	}

	public boolean isDestroyed()
	{
		return yachtInfo.numHits <= 0;
	}

	public void setBadPlacementRectangle()
	{
		if (badPlacementRectangle == null)
		{
			badPlacementRectangle = new Rectangle(0, 0, yachtSprite.getWidth(),
					yachtSprite.getHeight(),
					ContentFactory.getVertexBufferObjectManager());
			badPlacementRectangle.setColor(1.0f, 0.0f, 0.0f, 0.4f);
		}
		if (!badPlacementRectangle.hasParent())
		{
			yachtSprite.attachChild(badPlacementRectangle);
		}
	}

	public void removeBadPlacementRectangle()
	{
		if (badPlacementRectangle != null && badPlacementRectangle.hasParent())
		{
			yachtSprite.detachChild(badPlacementRectangle);
		}
	}

	public YachtInfo getInfo()
	{
		return yachtInfo;
	}

	public YachtType getType()
	{
		return yachtInfo.yachtType;
	}

	public String getName()
	{
		return yachtInfo.name;
	}

	public String getShortName()
	{
		return yachtInfo.shortName;
	}

	public int getRow()
	{
		return yachtInfo.row;
	}

	public void setRow(int r)
	{
		yachtInfo.row = r;
		yachtSprite.setY(YachtFactory.getCellLocation(yachtInfo.row
				- (yachtInfo.orientation == Orientation.VERTICAL ? 1 : 0)));
	}

	public int getColumn()
	{
		return yachtInfo.col;
	}

	public void setColumn(int c)
	{
		yachtInfo.col = c;
		yachtSprite.setX(YachtFactory.getCellLocation(yachtInfo.col));
	}

	public Orientation getOrientation()
	{
		return yachtInfo.orientation;
	}

	public void toggleOrientation()
	{
		if (yachtInfo.orientation == Orientation.HORIZONTAL)
		{
			// Make yacht vertical, rotate, then account for rotation's
			// displacement
			yachtInfo.orientation = Orientation.VERTICAL;
			yachtSprite.setRotation(90.0f);
			yachtSprite.setY(YachtFactory.getCellLocation(yachtInfo.row - 1));
		}
		else
		{
			// Make yacht horizontal, rotate back, then account for rotation's
			// displacement
			yachtInfo.orientation = Orientation.HORIZONTAL;
			yachtSprite.setRotation(0.0f);
			yachtSprite.setY(YachtFactory.getCellLocation(yachtInfo.row));
		}

	}

	public int getUnits()
	{
		return yachtInfo.units;
	}

	public int getLeft()
	{
		return yachtInfo.col;
	}

	public int getRight()
	{
		int offset = 0;
		switch (yachtInfo.orientation)
		{
		case VERTICAL:
			offset = 0;
			break;
		case HORIZONTAL:
			offset = yachtInfo.units - 1;
			break;
		}
		return yachtInfo.col + offset;
	}

	public int getTop()
	{
		return yachtInfo.row;
	}

	public int getBottom()
	{
		int offset = 0;
		switch (yachtInfo.orientation)
		{
		case VERTICAL:
			offset = yachtInfo.units - 1;
			break;
		case HORIZONTAL:
			offset = 0;
			break;
		}
		return yachtInfo.row + offset;
	}

	public Sprite getSprite()
	{
		return yachtSprite;
	}

	public boolean intersects(Yacht y)
	{
		switch (y.getOrientation())
		{
		case VERTICAL:
			switch (yachtInfo.orientation)
			{
			case VERTICAL:
				// Columns must be equal
				return ((getColumn() == y.getColumn()) && (
				// If y on top of this
				(y.getTop() <= getTop() && getTop() <= y.getBottom()) ||
				// If this on top of y
				(getTop() <= y.getTop() && y.getTop() <= getBottom())));
			case HORIZONTAL:
				// Intersecting in a + shape
				return (getLeft() <= y.getLeft() && y.getLeft() <= getRight()
						&& y.getTop() <= getTop() && getTop() <= y.getBottom());
			}
			break;
		case HORIZONTAL:
			switch (yachtInfo.orientation)
			{
			case VERTICAL:
				// Intersecting in a + shape
				return (y.getLeft() <= getLeft() && getLeft() <= y.getRight()
						&& getTop() <= y.getTop() && y.getTop() <= getBottom());
			case HORIZONTAL:
				// Rows must be equal
				return ((yachtInfo.row == y.getRow()) && (
				// If y to the left of this
				(y.getLeft() <= getLeft() && getLeft() <= y.getRight()) ||
				// If this to the left of y
				(getLeft() <= y.getLeft() && y.getLeft() <= getRight())));
			}
			break;
		}

		// Never gets here, always either HORIZONTAL or VERTICAL
		return false;
	}

	public boolean shoot(int r, int c)
	{
		switch (yachtInfo.orientation)
		{
		case HORIZONTAL:
			return ((yachtInfo.row == r) && (yachtInfo.col <= c) && (c < yachtInfo.col
					+ yachtInfo.units));
		case VERTICAL:
			return ((yachtInfo.col == c) && (yachtInfo.row <= r) && (r < yachtInfo.row
					+ yachtInfo.units));
		}
		// Will never reach here
		return false;
	}
}
