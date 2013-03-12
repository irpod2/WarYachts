
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import java.util.ArrayList;
import java.util.Stack;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Carrier;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Destroyer;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Skunker;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.SubYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.WarYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.YachtType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ContentFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.TextFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public class PlacementMenu implements IOnSceneTouchListener
{
	private Stack<Yacht> unplacedYachts;
	private UserBattlefield userBattlefield;
	private Rectangle boundingBox;
	private Rectangle displayBox;
	private Entity layer;
	private Scene scene;
	private Yacht displayedYacht;
	private Text yachtDescription;
	private CallbackVoid onShipsPlacedCallback;
	private ButtonSprite readyButton;
	private ArrayList<YachtInfo> placedYachts;

	private boolean dragging;
	private float dragAnchorX;
	private float dragAnchorY;

	public PlacementMenu(Entity lyr, UserBattlefield ubat,
			CallbackVoid onShipsPlacedCB)
	{
		float cameraWidth = WarYachtsActivity.getCameraWidth();
		float cameraHeight = WarYachtsActivity.getCameraHeight();
		layer = lyr;
		userBattlefield = ubat;
		onShipsPlacedCallback = onShipsPlacedCB;
		dragAnchorX = 0;
		dragAnchorY = 0;
		
		placedYachts = new ArrayList<YachtInfo>(5);
		placedYachts.add(new YachtInfo(YachtType.HEL_CAR,
				Orientation.HORIZONTAL, 0, 0, Carrier.UNITS, Carrier.NAME));
		placedYachts.add(new YachtInfo(YachtType.OLD_REL,
				Orientation.HORIZONTAL, 0, 0, Destroyer.UNITS, Destroyer.NAME));
		placedYachts.add(new YachtInfo(YachtType.POSI,
				Orientation.HORIZONTAL, 0, 0, SubYacht.UNITS, SubYacht.NAME));
		placedYachts.add(new YachtInfo(YachtType.WAR_YAT,
				Orientation.HORIZONTAL, 0, 0, WarYacht.UNITS, WarYacht.NAME));
		placedYachts.add(new YachtInfo(YachtType.SKUNK,
				Orientation.HORIZONTAL, 0, 0, Skunker.UNITS, Skunker.NAME));
		
		// Yachts
		unplacedYachts = new Stack<Yacht>();
		
		unplacedYachts.push(YachtFactory.createSkunkerYacht(placedYachts.get(4)));
		unplacedYachts.push(YachtFactory.createSubYacht(placedYachts.get(2)));
		unplacedYachts.push(YachtFactory.createWarYacht(placedYachts.get(3)));
		unplacedYachts.push(YachtFactory.createDestroyerYacht(placedYachts.get(1)));
		unplacedYachts.push(YachtFactory.createCarrierYacht(placedYachts.get(0)));


		
		displayedYacht = unplacedYachts.pop();

		// Boxes
		float verticalSpaceLeft = cameraHeight
				- userBattlefield.getGridHeight();
		boundingBox = new Rectangle(0.05f * cameraWidth, cameraHeight
				- verticalSpaceLeft * 0.9f, 0.9f * cameraWidth,
				verticalSpaceLeft * 0.6f,
				ContentFactory.getVertexBufferObjectManager());
		boundingBox.setColor(Color.BLACK);
		displayBox = new Rectangle(boundingBox.getX() + 0.01f
				* boundingBox.getWidth(), boundingBox.getY() + 0.01f
				* boundingBox.getHeight(), boundingBox.getWidth() * 0.98f,
				boundingBox.getHeight() * 0.98f,
				ContentFactory.getVertexBufferObjectManager());
		displayBox.setColor(0.1f, 0.3f, 0.6f);

		// Text
		yachtDescription = TextFactory.createSimpleText("\""
				+ displayedYacht.getName() + "\"\nUnits: "
				+ displayedYacht.getUnits());
		displayBox.attachChild(yachtDescription);

		// Button to confirm placement of yachts
		readyButton = ButtonFactory.createReadyButton(verticalSpaceLeft * 0.2f,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{
						readyButton.setEnabled(false);
						onShipsPlacedCallback.onCallback();
					}
				});
		readyButton.setEnabled(false);

		resetYachtPosition();
	}
	
	public ArrayList<YachtInfo> getPlacedYachts()
	{
		return placedYachts;
	}

	private void changeDescription()
	{
		yachtDescription.setText("\"" + displayedYacht.getName()
				+ "\"\nUnits: " + displayedYacht.getUnits());
	}

	public void attachSprites()
	{
		layer.attachChild(boundingBox);
		layer.attachChild(displayBox);
		layer.attachChild(displayedYacht.getSprite());
		layer.attachChild(readyButton);
	}

	public void registerButton(Scene scn)
	{
		scene = scn;
		scene.registerTouchArea(readyButton);
	}
	
	public void setReadyButtonEnabled(boolean enabled)
	{
		readyButton.setEnabled(enabled);
	}
	
	public void onSuccess()
	{
		displayedYacht = null;
		
		if(yachtDescription != null)
		{
			if(yachtDescription.hasParent())
				yachtDescription.detachSelf();
			yachtDescription = null;
		}
		
		if(boundingBox != null)
		{
			layer.detachChild(boundingBox);
			boundingBox = null;
		}
		
		if(displayBox != null)
		{
			layer.detachChild(displayBox);
			displayBox = null;
		}
		
		if(readyButton != null)	
		{
			readyButton.detachSelf();
			scene.unregisterTouchArea(readyButton);
			readyButton = null;
		}
	}

	private void resetYachtPosition()
	{

		
		switch (displayedYacht.getOrientation())
		{
		case VERTICAL:
			displayedYacht.getSprite().setPosition(
					displayBox.getX() + displayBox.getWidth()
							- SpriteFactory.getCellSpacing() * 1.5f,
					displayBox.getY() - SpriteFactory.getCellSpacing() * 0.5f);
			break;
		case HORIZONTAL:
			displayedYacht.getSprite().setPosition(
					displayBox.getX() + displayBox.getWidth()
							- displayedYacht.getSprite().getWidthScaled()
							- SpriteFactory.getCellSpacing() * 0.5f,
					displayBox.getY() + SpriteFactory.getCellSpacing() * 0.5f);
			break;
		}
	}

	private void positionYachtOnGrid(float pX, float pY)
	{
		int r = Battlefield.getCellFromPosition(pY);
		int c = Battlefield.getCellFromPosition(pX);
		
		displayedYacht.setRow(r);
		displayedYacht.setColumn(c);		
	}


	private void placeYacht(float pX, float pY)
	{
		layer.detachChild(displayedYacht.getSprite());
		userBattlefield.addYacht(displayedYacht);
		
		int r = Battlefield.getCellFromPosition(pY);
		int c = Battlefield.getCellFromPosition(pX);
		
		YachtType yt = displayedYacht.getType();
		
		switch(yt)
		{
		case HEL_CAR:
			placedYachts.get(0).col = c;
			placedYachts.get(0).row = r;
			placedYachts.get(0).orientation = displayedYacht.getOrientation();
			break;
		case OLD_REL:
			placedYachts.get(1).col = c;
			placedYachts.get(1).row = r;
			placedYachts.get(1).orientation = displayedYacht.getOrientation();
			break;
		case POSI:
			placedYachts.get(2).col = c;
			placedYachts.get(2).row = r;
			placedYachts.get(2).orientation = displayedYacht.getOrientation();
			break;
		case WAR_YAT:
			placedYachts.get(3).col = c;
			placedYachts.get(3).row = r;
			placedYachts.get(3).orientation = displayedYacht.getOrientation();
			break;
		case SKUNK:
			placedYachts.get(4).col = c;
			placedYachts.get(4).row = r;
			placedYachts.get(4).orientation = displayedYacht.getOrientation();
			break;
		
		}
		
		
		
		if (!unplacedYachts.isEmpty())
		{
			displayedYacht = unplacedYachts.pop();
			resetYachtPosition();
			changeDescription();
			layer.attachChild(displayedYacht.getSprite());
		}
		else
		{
			displayedYacht = null;
			displayBox.detachChild(yachtDescription);
			readyButton.setEnabled(true);
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		float pX = pSceneTouchEvent.getX() - dragAnchorX;
		float pY = pSceneTouchEvent.getY() - dragAnchorY;
		// On start of touch action
		if (pSceneTouchEvent.isActionDown())
		{
			if (displayedYacht != null
					&& displayedYacht.getSprite().contains(pX, pY))
			{
				// If you want to drag, start draggin'
				dragging = true;
				dragAnchorX = pX - displayedYacht.getSprite().getX();
				dragAnchorY = pY - displayedYacht.getSprite().getY();
			}
			else
			{
				Yacht dragYacht = userBattlefield.getYacht(pX, pY);
				if (dragYacht != null)
				{
					readyButton.setEnabled(false);
					if (displayedYacht != null)
					{
						unplacedYachts.push(displayedYacht);
						layer.detachChild(displayedYacht.getSprite());
					}
					else
					{
						displayBox.attachChild(yachtDescription);
					}
					displayedYacht = dragYacht;
					changeDescription();
					displayedYacht.getSprite().detachSelf();
					layer.attachChild(displayedYacht.getSprite());
					dragging = true;
					dragAnchorX = pX - displayedYacht.getSprite().getX();
					dragAnchorY = pY - displayedYacht.getSprite().getY();
				}
			}

		}
		// While dragging, drag. If not dragging, no idea what you're doing
		// wiggling your fingers, buddy.
		else if (pSceneTouchEvent.isActionMove() && dragging)
		{
			if (userBattlefield.contains(pX + dragAnchorX, pY + dragAnchorY))
			{
				positionYachtOnGrid(pX, pY);
				if (!userBattlefield.isValidPlacement(displayedYacht))
					displayedYacht.setBadPlacementRectangle();
				else
					displayedYacht.removeBadPlacementRectangle();
			}
			else
				displayedYacht.getSprite().setPosition(pX, pY);
		}
		// Letting go, we're no longer dragging
		else if (pSceneTouchEvent.isActionUp())
		{
			if (displayedYacht == null)
				return true;
			displayedYacht.removeBadPlacementRectangle();
			positionYachtOnGrid(pX, pY);
			// Either you're placing the yacht on the battlefield,
			if (dragging && userBattlefield.isValidPlacement(displayedYacht))
			{
				placeYacht(pX, pY);
			}
			// If not dragging, rotate yacht
			else if (!dragging
					&& displayBox.contains(pX + dragAnchorX, pY + dragAnchorX))
			{
				displayedYacht.toggleOrientation();
				resetYachtPosition();
			}
			// Or you did something dumb and want to rethink your choices
			else
			{
				resetYachtPosition();
			}
			dragging = false;
			dragAnchorX = 0;
			dragAnchorY = 0;
		}
		return true;
	}
}
