package com.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;


import com.flemil.control.GlobalControl;
import com.flemil.control.Style;
import com.flemil.event.ButtonListener;
import com.flemil.ui.Item;
import com.flemil.ui.TextItem;
import com.flemil.util.Rectangle;


/**
 * Class that represents an Item that displays text to the user. This control
 * does not support inout of text from the user.
 * @author Solomon Kariri
 */
public class Button implements TextItem
{
	private Rectangle displayRect;
	private Item parent;
	private Label nameDisplayer;
	private boolean focussed;
	private boolean paintBorder=true;
	public ButtonListener getListener() {
		return listener;
	}
	public void setListener(ButtonListener listener) {
		this.listener = listener;
	}

	private ButtonListener listener;
	private Font font;
	private boolean fontSet;
	
	public boolean isFocusible() {
		return nameDisplayer.isFocusible();
	}
	public void setFocusible(boolean focusible) {
		nameDisplayer.setFocusible(focusible);
	}
	/**
	 * Sets the text being displayed by this Button
	 * @param text the text to be displayed by this Button
	 */
	public Button(String text)
	{
        displayRect=new Rectangle();
        //Initialize display rect
        displayRect=new Rectangle();
        nameDisplayer=new Label(text);
        nameDisplayer.setTextWraps(false);
        nameDisplayer.setFocusible(true);
        nameDisplayer.setParent(this);
        nameDisplayer.setFont((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.BUTTON_FONT));
	}
	public void focusGained() 
	{
		if(focussed)return;
		focussed=true;
		nameDisplayer.focusGained();
	}

	public void focusLost() 
	{
		focussed=false;
		nameDisplayer.focusLost();
	}

	public Rectangle getDisplayRect() 
	{
		return nameDisplayer.getDisplayRect();
	}

	public synchronized Rectangle getMinimumDisplayRect(int availWidth) 
	{
		nameDisplayer.setFont((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.BUTTON_FONT));
		Rectangle min=nameDisplayer.getMinimumDisplayRect(availWidth);
		min.width+=6;
		min.height+=2;
		return min;
	}

	public Item getParent() 
	{
		return this.parent;
	}

	public void keyPressedEvent(int keyCode) 
	{    
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.FIRE:
            {
            	if(listener!=null)
            	{
            		listener.buttonPressed(this);
            	}
            }
            default:
            {
            	if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
                break;
            }
        }
	}
	public void keyPressedEventReturned(int keyCode){}
	public void keyReleasedEvent(int keyCode){}
	public void keyReleasedEventReturned(int keyCode){}
	public void keyRepeatedEvent(int keyCode) 
	{	
		keyPressedEvent(keyCode);
	}
	public void keyRepeatedEventReturned(int keyCode){}
	public void paint(Graphics g, Rectangle clip) 
	{	
		if(displayRect.width<=1)return;
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.BUTTON_FONT);
		int radius=((Integer)GlobalControl.getControl().getStyle().
				getProperty(Style.BUTTON_CURVE_RADIUS)).intValue();
        if((this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setFont(font);
        	if(focussed)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
                        Style.THEME_BACKGROUND)).intValue());
        		Rectangle edge=new Rectangle(displayRect.x,displayRect.y,radius,displayRect.height);
        		Rectangle interDisp=null;
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		g.fillRoundRect(interDisp.x, interDisp.y, interDisp.width+radius, interDisp.height,
            				radius,radius);
            		g.drawRegion(GlobalControl.getControl().getButtonEdgeBGround(),0,0,
            				GlobalControl.getControl().getButtonEdgeBGround().getWidth(),
            				GlobalControl.getControl().getButtonEdgeBGround().getHeight(),
            				Sprite.TRANS_MIRROR,
            				edge.x+1, edge.y+1, Graphics.TOP|Graphics.LEFT);
            	}
            	edge=new Rectangle(displayRect.x+displayRect.width-radius,
            			displayRect.y,radius,displayRect.height);
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		g.fillRoundRect(interDisp.x-radius, interDisp.y, interDisp.width+radius, interDisp.height,
            				radius,radius);
            		g.drawImage(GlobalControl.getControl().getButtonEdgeBGround(),
            				edge.x-radius, edge.y+1, Graphics.TOP|Graphics.LEFT);
            	}
            	edge=new Rectangle(displayRect.x+radius,
            			displayRect.y,displayRect.width-(radius*2),displayRect.height);
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		int imgWidth=GlobalControl.getControl().getButtonGBround().getWidth();
            		for(int i=interDisp.x-1;i<interDisp.x+interDisp.width+1;i+=imgWidth-1)
            		{
            			g.fillRect(i, interDisp.y, imgWidth, interDisp.height);
            			g.drawImage(GlobalControl.getControl().getButtonGBround(),
                				i, edge.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            	}
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_FOCUS_FOREGROUND)).intValue());
        	}
        	else
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_BACKGROUND)).intValue());
            	g.fillRoundRect(displayRect.x+1, displayRect.y+1, displayRect.width-2,
            			displayRect.height-2, 
            			radius,radius);
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_FOREGROUND)).intValue());
        	}
        	Rectangle interDisp=null;
        	if((interDisp=nameDisplayer.getDisplayRect().calculateIntersection(clip))!=null)
            {
        		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
        		int txtStart=0;
        		if(nameDisplayer.getTextWidth()>nameDisplayer.getDisplayRect().width)
        		{
            		g.drawString(nameDisplayer.getText(), nameDisplayer.getDisplayRect().x+nameDisplayer.getTextIndent()+1,
                            nameDisplayer.getDisplayRect().y+2,
                            Graphics.TOP|Graphics.LEFT);
        		}
        		else
        		{
        			switch (nameDisplayer.getAlignment()) {
					case TextItem.ALIGN_RIGHT:
						txtStart=nameDisplayer.getDisplayRect().x+nameDisplayer.getDisplayRect().width-
						font.stringWidth(nameDisplayer.getText())-1;
						break;
					case TextItem.ALIGN_CENTER:
						txtStart=nameDisplayer.getDisplayRect().x+
						(nameDisplayer.getDisplayRect().width-font.
								stringWidth(nameDisplayer.getText()))/2;
						break;
					}
        			g.drawString(nameDisplayer.getText(), txtStart, 
        					nameDisplayer.getDisplayRect().y+2, Graphics.TOP|Graphics.LEFT);
        		}
            }
        	g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y){}
	public void pointerDraggedEventReturned(int x, int y){}
	public void pointerPressedEvent(int x, int y){}
	public void pointerPressedEventReturned(int x, int y){}
	public void pointerReleasedEvent(int x, int y){}
	public void pointerReleasedEventReturned(int x, int y){}
	public void repaint(Rectangle clip) 
	{	
		if(parent!=null)
        {
            this.parent.repaint(displayRect);
        }
	}
	
	public void setDisplayRect(Rectangle rect) 
	{
        displayRect=rect;
        int rad=((Integer)GlobalControl.getControl().getStyle().getProperty(Style.BUTTON_CURVE_RADIUS)).intValue();
        Rectangle dispRect=new Rectangle(displayRect.x+rad,displayRect.y-1,displayRect.width-(rad*2),displayRect.height);
        nameDisplayer.setDisplayRect(dispRect);
	}

	public void setParent(Item parent) 
	{	
		this.parent=parent;
	}
	public void setText(String text) {
		nameDisplayer.setText(text);
	}
	public String getText() {
		return nameDisplayer.getText();
	}
	public boolean isPaintBorder() {
		return paintBorder;
	}
	public void setPaintBorder(boolean paint) {}
	public Font getFont() {
		return nameDisplayer.getFont();
	}
	public void setFont(Font font) {}
	public byte getAlignment() {
		return nameDisplayer.getAlignment();
	}
	public boolean isTextWraps() {
		return false;
	}
	public void setAlignment(byte alignment) {
		nameDisplayer.setAlignment(alignment);
	}
	public void setTextWraps(boolean textWraps) {}
	public void resetFont() {}
	public int getTextIndent() {
		return nameDisplayer.getTextIndent();
	}

	public int getTextWidth() {
		return nameDisplayer.getTextWidth();
	}

	public boolean isFocussed() {
		return focussed;
	}

	public synchronized boolean isScrolling() {
		return nameDisplayer.isScrolling();
	}

	public synchronized void setScrolling(boolean scrolling) {
		nameDisplayer.setScrolling(scrolling);
	}

	public void setTextIndent(int indent) {
		nameDisplayer.setTextIndent(indent);
	}
}
