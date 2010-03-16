package com.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


import com.flemil.control.GlobalControl;
import com.flemil.control.Style;
import com.flemil.event.ImageItemListener;
import com.flemil.ui.Item;
import com.flemil.util.Rectangle;


/**
 * Class that represents an Item that can be used to display an image
 * @author Solomon Kariri
 */
public class ImageItem implements Item
{
	private boolean focusible=true;
	private Image image;
	private Rectangle displayRect;
	private Item parent;
	private boolean paintBorder=true;
	private boolean focussed;
	private ImageItemListener listener;
	private boolean resizeToFit;
	private Image drawnImage;
	
	/**
	 * Creates a new ImageItem that displays the Image passed to the constructor
	 * @param image the Omage to be displayed by this ImageItem
	 */
	public ImageItem(Image image)
	{
		this.image=image;
		displayRect=new Rectangle();
	}
	public boolean isFocusible() {
		return focusible;
	}

	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void focusGained() {
		focussed=true;
		repaint(displayRect);
		if(listener!=null)
		{
			listener.eventFired(this, ImageItemListener.FOCUSS_GAINED);
		}
	}

	public void focusLost() {
		focussed=false;
		repaint(displayRect);
		if(listener!=null)
		{
			listener.eventFired(this, ImageItemListener.FOCUS_LOST);
		}
	}

	public Rectangle getDisplayRect() {
		return this.displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) {
		if(this.image!=null)
		{
			if(availWidth>image.getWidth())
				availWidth=image.getWidth();
			int height=(image.getHeight()*availWidth)/image.getWidth();
			return new Rectangle(0,0,availWidth,height);
		}
		else
		{
			return new Rectangle();
		}
	}

	public Item getParent() {
		return this.parent;
	}

	public void keyPressedEvent(int keyCode){
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.FIRE:
            {
            	if(listener!=null)
        		{
        			listener.eventFired(this, ImageItemListener.FIRE_KEY_PRESSED);
        		}
                break;
            }
            default:
            {
            	if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}	
            }
        }
	}

	public void keyPressedEventReturned(int keyCode){}

	public void keyReleasedEvent(int keyCode){}

	public void keyReleasedEventReturned(int keyCode){}

	public void keyRepeatedEvent(int keyCode){}

	public void keyRepeatedEventReturned(int keyCode){}

	public void paint(Graphics g, Rectangle clip) 
	{
		if(displayRect.width<=1)return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_BACKGROUND)).intValue());
        	if(paintBorder && !focussed)
    		{
        		g.drawRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
    		}
    		else
    		{
    			g.fillRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
    		}
        	if(drawnImage!=null)
        	{
        		g.setClip(intersect.x>displayRect.x+2?intersect.x:displayRect.x+2, 
        				intersect.y>displayRect.y+2?intersect.y:displayRect.y+2,
        				intersect.width<displayRect.width-radius?intersect.width:displayRect.width-radius, 
        				intersect.height<displayRect.height-radius?intersect.height:displayRect.height-radius);
        		g.drawImage(drawnImage, displayRect.x+displayRect.width/2-drawnImage.getWidth()/2,
            			displayRect.y+displayRect.height/2-drawnImage.getHeight()/2, 
            			Graphics.TOP|Graphics.LEFT);
        	}
            g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y) 
	{	
	}

	public void pointerDraggedEventReturned(int x, int y) 
	{	
	}

	public void pointerPressedEvent(int x, int y) 
	{
	}

	public void pointerPressedEventReturned(int x, int y) 
	{	
	}

	public void pointerReleasedEvent(int x, int y) 
	{	
	}

	public void pointerReleasedEventReturned(int x, int y) 
	{
	}

	public void repaint(Rectangle clip) 
	{
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}

	public synchronized void setDisplayRect(Rectangle rect){
		if((rect.height!=displayRect.height || rect.width!=displayRect.width)
				&& resizeToFit && image!=null)
		{
			drawnImage=GlobalControl.getImageFactory().scaleImage(image, 
					rect.width-2, rect.height-2, Sprite.TRANS_NONE);
		}
		else if(drawnImage==null)
		{
			drawnImage=image;
		}
		this.displayRect=rect;
	}

	public void setParent(Item parent) 
	{
		this.parent=parent;
	}

	public boolean isPaintBorder() {
		return this.paintBorder;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}
	/**
	 * Sets the class that will be invoked when events occur on this ImageItem
	 * @param listener class that will be invoked when events occur on this ImageItem.
	 */
	public void setListener(ImageItemListener listener) {
		this.listener = listener;
	}
	/**
	 * Returns the class that is set as the listener for events originating from this ImageItem
	 * @return class that is set as the listener for events originating from this ImageItem.
	 */
	public ImageItemListener getListener() {
		return listener;
	}
	/**
	 * Sets whether this ImageItem should create a resized copy of its Image so that
	 *  it fills this ImageItems displayRect. Note that this method creates a scaled 
	 *  copy of the passed in Image for display purposes. 
	 * @param resizeToFit true to create a display copy and false otherwise
	 */
	public void setResizeToFit(boolean resizeToFit) {
		this.resizeToFit = resizeToFit;
		setDisplayRect(displayRect);
	}
	/**
	 * Returns whether this ImageItems generates a scaled copy of its Image for display purposes 
	 * @return true if this ImageItem generates a scaled copy or not
	 */
	public boolean isResizeToFit() {
		return resizeToFit;
	}
}
