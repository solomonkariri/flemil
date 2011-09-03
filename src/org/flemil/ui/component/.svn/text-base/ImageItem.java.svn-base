package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.ImageItemListener;
import org.flemil.ui.Container;
import org.flemil.ui.Item;
import org.flemil.util.Rectangle;




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
		this.displayRect=new Rectangle();
	}
	public boolean isFocusible() {
		return focusible;
	}

	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void focusGained() {
		focussed=true;
		repaint(this.displayRect);
		if(listener!=null)
		{
			listener.eventFired(this, ImageItemListener.FOCUSS_GAINED);
		}
	}

	public void focusLost() {
		focussed=false;
		repaint(this.displayRect);
		if(listener!=null)
		{
			listener.eventFired(this, ImageItemListener.FOCUS_LOST);
		}
	}

	public Rectangle getDisplayRect() {
		return this.displayRect;
	}
	
	public Image getImage(){
		return image;
	}
	
	public Rectangle getMinimumDisplayRect(int availWidth) {
		if(this.image!=null)
		{
			if(resizeToFit){
				int height=(image.getHeight()*availWidth)/image.getWidth();
				return new Rectangle(0,0,availWidth,height);
			}
			else{
				return new Rectangle(0, 0, availWidth, this.image.getHeight()+6);
			}
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
		if(this.displayRect.width<=1)return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
        	if(paintBorder){
        		g.drawRoundRect(this.displayRect.x+1, displayRect.y+1, 
        				displayRect.width-2, displayRect.height-2,
        				radius, radius);
        	}
        	if(focussed){
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue());
        		g.fillRoundRect(this.displayRect.x+2, displayRect.y+2, 
        				displayRect.width-4, displayRect.height-4,
        				radius, radius);
        	}
        	if(drawnImage!=null)
        	{
        		g.setClip(intersect.x, intersect.y,intersect.width,intersect.height);
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
		if(displayRect.contains(x, y, 0)){
			if(listener!=null)
    		{
    			listener.eventFired(this, ImageItemListener.FIRE_KEY_PRESSED);
    		}
			else{
				parent.pointerReleasedEventReturned(x, y);
			}
		}
	}

	public void pointerReleasedEventReturned(int x, int y) 
	{
		parent.pointerReleasedEventReturned(x, y);
	}

	public void repaint(Rectangle clip) 
	{
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}

	public void setDisplayRect(Rectangle rect){
		synchronized (this) {
			if(rect.width<=2)return;
			if(Math.abs(rect.width-displayRect.width)<=2 
					&& Math.abs(rect.height-displayRect.height)<=2)
			{
				this.displayRect.x=rect.x;
				this.displayRect.y=rect.y;
				return;
			}
			if((rect.height!=displayRect.height || rect.width!=displayRect.width)
					&& resizeToFit && image!=null)
			{
				if(drawnImage!=null &&
						drawnImage.getHeight()==rect.height && drawnImage.getWidth()==rect.width){
					this.displayRect=rect;
					return;
				}
				drawnImage=GlobalControl.getImageFactory().scaleImage(image, 
						rect.width, rect.height, Sprite.TRANS_NONE);
			}
			else if(drawnImage==null)
			{
				if(resizeToFit)
					drawnImage=Image.createImage(image);
				else
					drawnImage=image;
			}
			this.displayRect=rect;
		}
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
		if(!resizeToFit){
			drawnImage=image;
		}
		setDisplayRect(displayRect);
	}
	/**
	 * Returns whether this ImageItems generates a scaled copy of its Image for display purposes 
	 * @return true if this ImageItem generates a scaled copy or not
	 */
	public boolean isResizeToFit() {
		return resizeToFit;
	}
	public boolean isFocussed() {
		return focussed;
	}
	public void setImage(Image image) {
		if(!resizeToFit)drawnImage=null;
		this.image=image;
		if(displayRect.width<=1)return;
		this.drawnImage=null;
		int currentHeight=displayRect.height;
		if(resizeToFit && displayRect.width>1){
			int height=(image.getHeight()*displayRect.width)/image.getWidth();
			drawnImage=GlobalControl.getImageFactory().scaleImage(image, 
					displayRect.width, height, Sprite.TRANS_NONE);
		}
		else
		{
			drawnImage=image;
		}
		int newHeight=drawnImage.getHeight()+4;
		if(newHeight!=currentHeight){
			int diff=Math.abs(newHeight-currentHeight);
			diff=newHeight-currentHeight;
			displayRect.height=newHeight;
			((Container)parent).itemHeightChanged(this, diff);
		}
		if(parent!=null){
			repaint(displayRect);
		}
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
	}
}
