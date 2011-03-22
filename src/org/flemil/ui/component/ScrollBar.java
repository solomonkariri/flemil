package org.flemil.ui.component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.ui.Item;
import org.flemil.ui.Scrollable;
import org.flemil.util.ImageFactory;
import org.flemil.util.Rectangle;




/**
 * Class that represents an Item that can be used to show the extent of the current scroll
 * and the remaining scroll distance for a Scrollable Item
 * @author Solomon Kariri
 *
 */
public class ScrollBar implements Item 
{
	/**
	 * Denotes a horizontal ScrollBar
	 */
	public static final byte HORIZONTAL_ORIENTATION=1;
	/**
	 * Denotes a vertical ScrollBar
	 */
	public static final byte VERTICAL_ORIENTATION=2;
	private byte orientation=ScrollBar.VERTICAL_ORIENTATION;
	private int availableSize=1;
	private int requiredSize=1;
	private int currentPoint=0;
	private Rectangle displayRect;
	private Item parent;
	private int knobSize=1;
	private boolean paintBorder=true;
	private Image fgImage;
	private int lastPointY;
	
	
	public boolean isPaintBorder() {
		return paintBorder;
	}

	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}

	/**
	 * Creates a ScrollBar with the passed in attributes
	 * @param availableSize the size that is available for 
	 * display in the item that wants to use this ScrollBar
	 * @param requiredSize the size that is required to 
	 * show all the contents of the Scrollable that uses this Scrollbar
	 * @param orientation the Orientation of this ScrollBar which can be
	 * either HORIZONTAL_ORIENTATION or VERTICAL_ORIENTATION
	 */
	public ScrollBar(int availableSize,int requiredSize,byte orientation) 
	{
		setAvailableSize(availableSize);
		setRequiredSize(requiredSize);
		this.orientation=orientation;
		displayRect=new Rectangle();
	}
	
	private void initItemSizes()
	{
		synchronized (this) {
			if(orientation==ScrollBar.VERTICAL_ORIENTATION)
			{
				knobSize=(availableSize*displayRect.height)/requiredSize;
				if(knobSize<3)
				{
					knobSize=3;
				}
				else if(knobSize>displayRect.height)
				{
					knobSize=displayRect.height;
				}
			}
			else
			{
				knobSize=(availableSize*displayRect.width)/requiredSize;
				if(knobSize<3)
				{
					knobSize=3;
				}
				else if(knobSize>displayRect.width)
				{
					knobSize=displayRect.width;
				}
			}
			requiredSize+=(knobSize*requiredSize)/availableSize;
			byte[] tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(
					knobSize, orientation==ScrollBar.VERTICAL_ORIENTATION?
							displayRect.width:displayRect.height, 
					((Integer)GlobalControl.getControl().getStyle().
							getProperty(Style.SCROLLBAR_FOREGROUND)).intValue(), 
							255, 255, ImageFactory.LIGHT_BEHIND, true,0);
			fgImage=Image.createImage(tmpStore, 0, tmpStore.length);
		}
	}
	/**
	 * Returns the current point of the scroll bar in which is a value between
	 * 0 and required height attribute of this ScrollBar
	 * @return the current point of the scroll bar in which is a value between
	 * 0 and required height attribute of this ScrollBar
	 */
	public int getCurrentPoint() {
		return currentPoint;
	}
	/**
	 * Sets the current Point of this ScrollBar
	 * @param currentPoint the current point of this ScrollBar
	 */
	public void setCurrentPoint(int currentPoint) {
		this.currentPoint = currentPoint;
	}
	public void focusGained() {}
	public void focusLost() {}
	public Rectangle getDisplayRect() 
	{
		return this.displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) 
	{
		return displayRect;
	}

	public Item getParent() 
	{
		return this.parent;
	}

	public void keyPressedEvent(int keyCode) {}
	public void keyPressedEventReturned(int keyCode) {}
	public void keyReleasedEvent(int keyCode) {}
	public void keyReleasedEventReturned(int keyCode) {}
	public void keyRepeatedEvent(int keyCode) {}
	public void keyRepeatedEventReturned(int keyCode) {}
	public void paint(Graphics g, Rectangle clip) 
	{
		if(fgImage==null)return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.setColor(((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.SCROLLBAR_BACKGROUND)).intValue());
        	if(orientation==VERTICAL_ORIENTATION)
        	{
        		g.fillRect(displayRect.x+(displayRect.width/2)-1, displayRect.y,
        				2, displayRect.height);
        		int knobCenter=(currentPoint*displayRect.height)/(requiredSize-availableSize);
        		g.drawRegion(fgImage, 0,0,fgImage.getWidth(),fgImage.getHeight(),
        				Sprite.TRANS_ROT90, 
        				displayRect.x+1,
        				displayRect.y+knobCenter,
        				Graphics.TOP|Graphics.LEFT);
        	}
        	else
        	{
        		g.fillRect(displayRect.x, displayRect.y+(displayRect.height/2)-1,
        				displayRect.width, 1);
        		int knobCenter=(currentPoint*displayRect.width)/(requiredSize-availableSize);
        		g.drawImage(fgImage, displayRect.x+knobCenter, 
        				displayRect.y+1, Graphics.TOP|Graphics.LEFT);
        	}
            g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}
	public void pointerDraggedEvent(int x, int y) {
		int knobTop=(currentPoint*displayRect.height)/(this.requiredSize-availableSize);
		Rectangle knobRect=new Rectangle(displayRect.x, 
				displayRect.y+knobTop, displayRect.width, 
				knobSize);
		if(knobRect.contains(x, y, 0)){
			int diff=y-lastPointY;
			lastPointY=y;
			if(parent!=null){
				int displacement=diff*requiredSize/availableSize;
				((Scrollable)parent).scrollContentsVertically(-displacement);
			}
		}
	}
	public void pointerDraggedEventReturned(int x, int y) {}
	public void pointerPressedEvent(int x, int y) {
		lastPointY=y;
	}
	public void pointerPressedEventReturned(int x, int y) {}
	public void pointerReleasedEvent(int x, int y) {
		if(displayRect.contains(x, y, 0)){
			if(orientation==ScrollBar.VERTICAL_ORIENTATION){
				int requiredSize=this.requiredSize-availableSize;
				int knobTop=(currentPoint*displayRect.height)/(this.requiredSize-availableSize);
				Rectangle knobRect=new Rectangle(displayRect.x, 
						displayRect.y+knobTop, displayRect.width, 
						knobSize);
				if(knobRect.contains(x, y, 0)){
					return;
				}
				else{
					if(parent!=null && (parent instanceof Scrollable)){
						int scrollablePanelDistance=(requiredSize-availableSize);
						int scrollableBarDistance=displayRect.height-knobSize;
						if(y<=displayRect.y+knobTop){
							int topGap=y-displayRect.y;
							int remainingTop=knobRect.y-displayRect.y;
							int unscrolledTop=((remainingTop)*scrollablePanelDistance)/scrollableBarDistance;
													int scrollHeight=((remainingTop-topGap)*unscrolledTop)/remainingTop+2;
													((Scrollable)parent).scrollContentsVertically(scrollHeight);
						}
						else{
							int bottomGap=displayRect.y+displayRect.height-y;
							int remainingBottom=((displayRect.y+displayRect.height)-(knobRect.y+knobRect.height));
							int unscrolledBottom=(remainingBottom*scrollablePanelDistance)/scrollableBarDistance;
													int scrollHeight=((remainingBottom-bottomGap)*unscrolledBottom)/remainingBottom+2;
													((Scrollable)parent).scrollContentsVertically(-scrollHeight);
						}
					}
				}
			}
		}
	}
	public void pointerReleasedEventReturned(int x, int y) {
		parent.pointerReleasedEventReturned(x, y);
	}
	public void repaint(Rectangle clip) {
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}
	public void setDisplayRect(Rectangle rect) {
		this.displayRect=rect;
		initItemSizes();
	}
	public void setParent(Item parent) {
		this.parent=parent;
	}

	/**
	 * Sets the available size of the Scrollable that uses this ScrollBar
	 * @param availableSize the available size for 
	 * the Item that uses this ScrollBar
	 */
	public void setAvailableSize(int availableSize) {
		if(availableSize==0)availableSize=1;
		this.availableSize = availableSize;
	}

	/**
	 * Returns the available size of the Scrollable that uses this ScrollBar
	 * @return the available size of the Scrollable that uses this ScrollBar
	 */
	public int getAvailableSize() {
		return availableSize;
	}

	/**
	 * 
	 * Sets the required size available to the Scrollable 
	 * that uses this ScrollBar
	 * @param requiredSize required size available to the 
	 * Scrollable that uses this ScrollBar
	 */
	public void setRequiredSize(int requiredSize) {
		if(requiredSize==0)requiredSize=1;
		this.requiredSize = requiredSize;
	}

	/**
	 * Returns the required size for the Scrollable that uses this ScrollBar
	 * @return the required size for the Scrollable that uses this ScrollBar
	 */
	public int getRequiredSize() {
		return requiredSize;
	}

	public boolean isFocusible() {
		return false;
	}

	public void setFocusible(boolean focusible) {
	}

	public boolean isFocussed() {
		return false;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
	}
}
