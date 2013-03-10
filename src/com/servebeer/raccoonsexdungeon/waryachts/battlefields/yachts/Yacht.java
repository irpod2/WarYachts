
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ContentFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public abstract class Yacht
{
	protected Sprite yachtSprite;
	protected String name;
	protected int units;
	protected int row;
	protected int col;
	protected Orientation orientation;
	protected Rectangle badPlacementRectangle;

	public enum Orientation
	{
		HORIZONTAL, VERTICAL
	}

	public Yacht(String yachtName, int r, int c, Orientation o, int u,
			Sprite sprite)
	{
		name = yachtName;
		row = r;
		col = c;
		orientation = o;
		units = u;
		yachtSprite = sprite;
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

	public String getName()
	{
		return name;
	}

	public int getRow()
	{
		return row;
	}

	public void setRow(int r)
	{
		row = r;
		yachtSprite.setY(YachtFactory.getCellLocation(row
				- (orientation == Orientation.VERTICAL ? 1 : 0)));
	}

	public int getColumn()
	{
		return col;
	}

	public void setColumn(int c)
	{
		col = c;
		yachtSprite.setX(YachtFactory.getCellLocation(col));
	}

	public Orientation getOrientation()
	{
		return orientation;
	}

	public void toggleOrientation()
	{
		if (orientation == Orientation.HORIZONTAL)
		{
			// Make yacht vertical, rotate, then account for rotation's
			// displacement
			orientation = Orientation.VERTICAL;
			yachtSprite.setRotation(90.0f);
			yachtSprite.setY(YachtFactory.getCellLocation(row - 1));
		}
		else
		{
			// Make yacht horizontal, rotate back, then account for rotation's
			// displacement
			orientation = Orientation.HORIZONTAL;
			yachtSprite.setRotation(0.0f);
			yachtSprite.setY(YachtFactory.getCellLocation(row));
		}

	}

	public int getUnits()
	{
		return units;
	}

	public int getLeft()
	{
		return col;
	}

	public int getRight()
	{
		int offset = 0;
		switch (orientation)
		{
		case VERTICAL:
			offset = 0;
			break;
		case HORIZONTAL:
			offset = units - 1;
			break;
		}
		return col + offset;
	}

	public int getTop()
	{
		return row;
	}

	public int getBottom()
	{
		int offset = 0;
		switch (orientation)
		{
		case VERTICAL:
			offset = units - 1;
			break;
		case HORIZONTAL:
			offset = 0;
			break;
		}
		return row + offset;
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
			switch (orientation)
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
			switch (orientation)
			{
			case VERTICAL:
				// Intersecting in a + shape
				return (y.getLeft() <= getLeft() && getLeft() <= y.getRight()
						&& getTop() <= y.getTop() && y.getTop() <= getBottom());
			case HORIZONTAL:
				// Rows must be equal
				return ((row == y.getRow()) && (
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
		switch (orientation)
		{
		case HORIZONTAL:
			return ((row == r) && (col <= c) && (c < col + units));
		case VERTICAL:
			return ((col == c) && (row <= r) && (r < row + units));
		}
		// Will never reach here
		return false;
	}
}
