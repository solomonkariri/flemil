package com.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


import com.flemil.control.GlobalControl;
import com.flemil.control.Style;
import com.flemil.ui.Item;
import com.flemil.ui.TextItem;
import com.flemil.util.Rectangle;


/**
 * Class that represents an Item that displays text to the user. This control
 * does not support inout of text from the user.
 * @author Solomon Kariri
 */
public class Label implements TextItem
{
	private Rectangle displayRect;
	private Item parent;
	private String text;
	private Font font;
	private boolean focusible=false;
	private boolean textWraps=true;
	private byte alignment=TextItem.ALIGN_LEFT;
	Vector splitIndecies=new Vector();
	private boolean focussed;
	private boolean fontSet;
	private int textIndent;
	private int textWidth=1;
	private boolean scrolling;
	private int lastAvail;
	private int lastWid;
	private int lastCount;
	private Object splitsLock=new Object();
	private transient boolean splitting;
	
	public boolean isFocusible() {
		return focusible;
	}
	public void setFocusible(boolean focusible) {
		this.focusible = focusible;
	}
	/**
	 * Sets the text being diplayed by this Label
	 * @param text the text to be displayed by this Label
	 */
	public Label(String text)
	{
		this.text=text;
		this.font=(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
        //Initialize display rect
        displayRect=new Rectangle();
        textWidth=font.stringWidth(text);
	}
	public void focusGained() 
	{
		font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		if(isFocusible())
		{
			focussed=true;
			textWidth=font.stringWidth(text)+2;
	        repaint(displayRect);
	        if(!textWraps)
	        {
	        	int diff=textWidth-displayRect.width+2;
	            if(diff>0 && !scrolling)
	            {
	                new Thread(new TextScroller(this)).start();
	            }
	        }
		}
	}

	public void focusLost() 
	{
		focussed=false;
		repaint(displayRect);
	}

	public Rectangle getDisplayRect() 
	{
		return displayRect;
	}

	public synchronized Rectangle getMinimumDisplayRect(int availWidth) 
	{
		font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		if(lastAvail==availWidth && lastWid==font.stringWidth(text)
				&& displayRect.width==availWidth && lastCount==text.length())
		{
			return displayRect;
		}
		lastAvail=availWidth;
		lastWid=font.stringWidth(text);
		lastCount=text.length();
		if(text.equals("") || availWidth<font.getHeight())
		{
			return new Rectangle(0,0,availWidth,font.getHeight()+4);
		}
		splitText(availWidth);
		Rectangle minRect=new Rectangle();
        minRect.height=textWraps?(font.getHeight()+4)*splitIndecies.size():font.getHeight()+4;
        minRect.width=availWidth;
        return minRect;
	}
	private void splitText(int availWidth)
	{
		splitIndecies.removeAllElements();
		Vector tempVect=new Vector();
		StringBuffer buff=new StringBuffer(text);
		for(int i=0;i<buff.length();i++)
		{
			if(buff.charAt(i)=='\n')
			{
				if(textWraps)
				{
					splitIndecies.addElement(new Integer(i+1));
				}
				else
				{
					buff.deleteCharAt(i);
				}
			}
		}
		text=buff.toString();
		splitIndecies.addElement(new Integer(buff.length()));
		int trk=0;
		int sizeSp=splitIndecies.size();
		for(int i=0;i<sizeSp;i++)
		{
			Integer test=(Integer)splitIndecies.elementAt(i);
			int endT=trk;
			while(endT<test.intValue())
			{
				int end=trk+1;
	            while(end<test.intValue() &&
	            		font.stringWidth(buff.toString().substring(trk, end))<availWidth-10)
	            {
	            	end++;
	            }
	            tempVect.addElement(new Integer(end));
	            endT=end;trk=end;
			}
			trk=test.intValue();
		}
		splitIndecies.removeAllElements();
		splitIndecies=tempVect;
	}

	public Item getParent() 
	{
		return this.parent;
	}

	public void keyPressedEvent(int keyCode) 
	{    
		if(parent!=null)
		{
			parent.keyPressedEventReturned(keyCode);
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
		font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		if(displayRect.width<=1)return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            g.setFont(font);
            g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_FOREGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOREGROUND)).intValue());
            StringBuffer buff=new StringBuffer();
    		buff.append(text);
    		synchronized (splitsLock) {
    			if(splitting)
    			{
    				return;
    			}
			}
            if(textWraps)
            {
            	int txtStart=displayRect.x+1;
            	int start=0;
        		int end=0;
        		int size=splitIndecies.size();
            	for(int i=0;i<size;i++)
            	{
            		end=((Integer)splitIndecies.elementAt(i)).intValue();
            		if(end==buff.length() || 
            				font.stringWidth(buff.toString().substring(start, end))<displayRect.width-2)
                    {
                    	switch (alignment) {
    					case TextItem.ALIGN_RIGHT:
    						txtStart=displayRect.x+displayRect.width-
    						font.stringWidth(buff.toString().substring(start,end))-1;
    						break;
    					case TextItem.ALIGN_CENTER:
    						txtStart=displayRect.x+
    						(displayRect.width-font.stringWidth(buff.toString().substring(start,end)))/2;
    						break;
    					}
                    }
            		int tmp=end;
            		while(tmp>start && buff.charAt(tmp-1)=='\n')
            		{
            			tmp--;
            		}
            		g.drawString(buff.toString().substring(start, tmp), txtStart, 
                    		displayRect.y+1+(i*(font.getHeight()+4)), Graphics.TOP|Graphics.LEFT);
            		start=end;
            	}
            }
            else
            {
            	 g.drawString(buff.toString(), displayRect.x+textIndent+1,
                         displayRect.y+1,
                         Graphics.TOP|Graphics.LEFT);
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
            this.parent.repaint(clip);
        }
	}
	
	public void setDisplayRect(Rectangle rect) 
	{	
		font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textIndent=0;
        textWidth=font.stringWidth(text)+2;
        if(textWidth<rect.width || textWraps)
        {
            scrolling=false;
        }
        else if(focusible && focussed && !scrolling)
        {
        	displayRect=rect;
            new Thread(new TextScroller(this)).start();
            return;
        }
        displayRect=rect;
	}

	public void setParent(Item parent) 
	{	
		this.parent=parent;
	}
	public void setText(String text) {
		synchronized (splitsLock) {
			splitIndecies.removeAllElements();
			splitting=true;
			this.text = text;
			font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
	                Style.ITEM_FONT);
			textWidth=font.stringWidth(text)+2;
			if(textWraps && parent!=null)splitText(displayRect.width);
			else if(focussed)
			{
				scrolling=false;
				int diff=textWidth-displayRect.width;
	            if(diff>0)
	            {
	                new Thread(new TextScroller(this)).start();
	            }
			}
			splitting=false;
			splitsLock.notifyAll();
		}
	}
	public String getText() {
		return text;
	}
	public boolean isPaintBorder() {
		return false;
	}
	public void setPaintBorder(boolean paint) {}
	public Font getFont() {
		return fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
	}
	public void setFont(Font font) {
		this.font=font;
		fontSet=true;
	}
	public byte getAlignment() {
		return this.alignment;
	}
	public boolean isTextWraps() {
		return textWraps;
	}
	public void setAlignment(byte alignment) {
		this.alignment=alignment;
	}
	public void setTextWraps(boolean textWraps) {
		this.textWraps=textWraps;
	}
	public void resetFont() {
		this.font=(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		fontSet=false;
	}
	public int getTextIndent() {
		return textIndent;
	}

	public int getTextWidth() {
		return textWidth;
	}

	public boolean isFocussed() {
		return focussed;
	}

	public synchronized boolean isScrolling() {
		return scrolling;
	}

	public synchronized void setScrolling(boolean scrolling) {
		this.scrolling=scrolling;
	}

	public void setTextIndent(int indent) {
		this.textIndent=indent;
	}
}
