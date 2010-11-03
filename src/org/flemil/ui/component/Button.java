package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.ButtonListener;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.util.Rectangle;




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
	private boolean textChanged;
	public ButtonListener getListener() {
		return listener;
	}
	public void setListener(ButtonListener listener) {
		this.listener = listener;
	}

	private ButtonListener listener;
	private Font font;
	private boolean fontSet;
	private int reqHeight;
	private int diff;
	
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
        nameDisplayer.setAlignment(TextItem.ALIGN_CENTER);
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
		return displayRect;
	}

	public synchronized Rectangle getMinimumDisplayRect(int availWidth) 
	{
		nameDisplayer.setFont((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.BUTTON_FONT));
		Rectangle min=nameDisplayer.getMinimumDisplayRect(availWidth);
		min.width+=((Integer)GlobalControl.getControl().getStyle().getProperty(
                Style.CURVES_RADIUS)).intValue()*2;
		min.height+=2;
		return min;
	}

	public Item getParent() 
	{
		return this.nameDisplayer.getParent();
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
        		Rectangle edge=new Rectangle(displayRect.x,displayRect.y+diff,radius,reqHeight);
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
            			displayRect.y+diff,radius,reqHeight);
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		g.fillRoundRect(interDisp.x-radius, interDisp.y, interDisp.width+radius, interDisp.height,
            				radius,radius);
            		g.drawImage(GlobalControl.getControl().getButtonEdgeBGround(),
            				edge.x-radius, edge.y+1, Graphics.TOP|Graphics.LEFT);
            	}
            	edge=new Rectangle(displayRect.x+radius,
            			displayRect.y+diff,displayRect.width-(radius*2),reqHeight);
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
            	g.fillRoundRect(displayRect.x+1, displayRect.y+1+diff, displayRect.width-2,
            			reqHeight-2, 
            			radius,radius);
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_FOREGROUND)).intValue());
        	}
//        	g.setColor(0xffffff);
//        	g.fillRect(nameDisplayer.getDisplayRect().x, nameDisplayer.getDisplayRect().y, 
//        			nameDisplayer.getDisplayRect().width, 
//        			nameDisplayer.getDisplayRect().height);
        	int prev=focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_FOREGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOREGROUND)).intValue();
    		if(focussed){
    			GlobalControl.getControl().getStyle().setProperty(Style.COMPONENT_FOCUS_FOREGROUND, 
    					GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_FOCUS_FOREGROUND));
    		}
    		else{
    			GlobalControl.getControl().getStyle().setProperty(Style.COMPONENT_FOREGROUND, 
    					GlobalControl.getControl().getStyle().
        				getProperty(Style.BUTTON_FOREGROUND));
    		}
        	nameDisplayer.paint(g, clip);
        	if(focussed){
    			GlobalControl.getControl().getStyle().setProperty(Style.COMPONENT_FOCUS_FOREGROUND, 
    					new Integer(prev));
    		}
    		else{
    			GlobalControl.getControl().getStyle().setProperty(Style.COMPONENT_FOREGROUND, 
    					new Integer(prev));
    		}
        	g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y){}
	public void pointerDraggedEventReturned(int x, int y){}
	public void pointerPressedEvent(int x, int y){}
	public void pointerPressedEventReturned(int x, int y){}
	public void pointerReleasedEvent(int x, int y){
		if(displayRect.contains(x, y, 0)){
			keyPressedEvent(GlobalControl.getControl().
					getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
		}
	}
	public void pointerReleasedEventReturned(int x, int y){
		parent.pointerReleasedEventReturned(x, y);
	}
	public void repaint(Rectangle clip) 
	{	
		if(parent!=null)
        {
            this.parent.repaint(clip);
        }
	}
	
	public void setDisplayRect(Rectangle rect) 
	{
        displayRect=rect;
        int rad=((Integer)GlobalControl.getControl().getStyle().getProperty(Style.BUTTON_CURVE_RADIUS)).intValue();
        Rectangle min=nameDisplayer.getMinimumDisplayRect(rect.width);
        reqHeight=min.height+2;
        diff=(displayRect.height-reqHeight)/2;
        Rectangle dispRect=new Rectangle(displayRect.x+rad-1,displayRect.y+diff,displayRect.width-(rad*2)+2,reqHeight);
        nameDisplayer.setDisplayRect(dispRect);
	}

	public void setParent(Item parent) 
	{	
		this.parent=parent;
		nameDisplayer.setParent(this.parent);
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
	public void setFont(Font font) {
		nameDisplayer.setFont(font);
	}
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
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		nameDisplayer.moveRect(dx, dy);
	}
	public void setTextChanged(boolean textChanged) {
		this.textChanged = textChanged;
	}
	public boolean isTextChanged() {
		return textChanged;
	}
}