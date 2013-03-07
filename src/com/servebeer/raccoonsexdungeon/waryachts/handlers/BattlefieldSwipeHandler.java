
package com.servebeer.raccoonsexdungeon.waryachts.handlers;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;

public class BattlefieldSwipeHandler implements IUpdateHandler
{
	private final float TRANSITION_TIME = 0.5f;
	private final float ABSOLUTE_VELOCITY;
	private float xL;
	private float xM;
	private float xR;
	private float xMin;
	private float xMax;
	private float cameraWidth;
	private boolean enabled;
	private Entity battlefieldA;
	private Entity battlefieldB;

	public BattlefieldSwipeHandler(Entity b1, Entity b2)
	{
		battlefieldA = b1;
		battlefieldB = b2;
		// Left, Middle, and Right anchor positions
		// Assumes handler is instantiated with b1 as the battlefield in view
		xM = battlefieldA.getX();
		xR = battlefieldB.getX();
		cameraWidth = xR - xM;
		xL = xM - cameraWidth;

		// Minimum X value of battlefield A
		xMin = xL - (cameraWidth / 2.0f);
		xMax = xM + (cameraWidth / 2.0f);
		enabled = true;
		ABSOLUTE_VELOCITY = cameraWidth / TRANSITION_TIME;
	}

	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	public void moveByOffset(float offset)
	{
		float newXA = battlefieldA.getX() + offset;
		if (newXA < xMin)
		{
			newXA = xMin;
		}
		else if (newXA > xMax)
		{
			newXA = xMax;
		}
		float newXB = newXA + cameraWidth;
		battlefieldA.setX(newXA);
		battlefieldB.setX(newXB);
	}

	protected float getClosestAnchor()
	{
		float lDist = (battlefieldA.getX() - xL);
		if (lDist * 2.0f < cameraWidth)
			return xL;
		else
			return xM;
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		if (enabled)
		{
			float targetAnchor = getClosestAnchor();
			float lDist = (battlefieldA.getX() - targetAnchor);
			float offset = ABSOLUTE_VELOCITY * pSecondsElapsed;
			if (lDist == 0.0f)
			{
				return;
			}
			else if (Math.abs(lDist) <= offset)
			{
				battlefieldA.setX(targetAnchor);
				battlefieldB.setX(targetAnchor + cameraWidth);
				return;
			}
			else if (lDist > 0)
			{
				offset *= -1;
			}
			battlefieldA.setX(battlefieldA.getX() + offset);
			battlefieldB.setX(battlefieldB.getX() + offset);
		}
	}

	@Override
	public void reset()
	{
		enabled = true;
	}

}
