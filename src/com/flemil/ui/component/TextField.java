/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;


import com.flemil.control.GlobalControl;
import com.flemil.control.Style;
import com.flemil.ui.Item;
import com.flemil.ui.Scrollable;
import com.flemil.ui.TextItem;
import com.flemil.util.Rectangle;


/**
 * Class that represents an Item that displays text and allows input of text from the user.
 * In order to make sure that there is device portability, the TextField uses a native TextBox 
 * for text input giving the user the ability to use all the powers of the application text
 * entry support.
 * @author Solomon Kariri
 */
public class TextField implements TextItem
{
	private Rectangle displayRect;
	private Item parent;
	private String text;
	private int properties;
	private boolean scrolling;
	private boolean focussed;
	 //the length in pixels of the name
    private int textWidth=1;
    //the variable to keep track of where the title drawing will start
    private int textIndent;
	private Font font;
	private boolean paintBorder=true;
	private int maxSize=255;
	private boolean focusible=true;
	private boolean textWraps=true;
	private byte alignment=TextItem.ALIGN_LEFT;
	private boolean editable=true;
	private Vector splitIndecies=new Vector();
	private boolean fontSet;
	private int lastAvail;
	private int lastWid;
	private Object splitsLock=new Object();
	private transient boolean splitting;
	private int lastCount;
	
	/**
	 * Creates a new TextField with the passed in parameters
	 * @param text the initial Text to be shown by this TextField
	 * @param maxSize the maximum allowed size for this TextField
	 * @param properties the properties of this field which are 
	 * analogous to the javax.microedition.lcdui.TextField properties
	 */
	public TextField(String text,int maxSize, int properties)
	{
		//Initialize display rect
        displayRect=new Rectangle();
		this.text=text;
		this.properties=properties;
		this.maxSize=maxSize;
		this.font=(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textWidth=font.stringWidth(text);
	}
	
	public boolean isTextWraps() {
		return textWraps;
	}
	public void setTextWraps(boolean textWraps) {
		this.textWraps = textWraps;
	}
	public boolean isFocusible() {
		return focusible;
	}
	public void setFocusible(boolean focusible) {
		this.focusible = focusible;
	}
	public void focusGained() 
	{
		font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		focussed=true;
		textWidth=((properties&javax.microedition.lcdui.TextField.PASSWORD)!=0?
				this.font.stringWidth("*")*text.length():font.stringWidth(text))+2;
        repaint(displayRect);
        if(!textWraps)
        {
        	int diff=textWidth-displayRect.width;
            if(diff>0)
            {
                new Thread(new TextScroller(this)).start();
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
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);   
        switch(key)
        {
            case Canvas.DOWN:
            case Canvas.UP:
            case Canvas.LEFT:
            case Canvas.RIGHT:
            {
            	if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
                break;
            }
            default:
            {
            	if(editable)
            	showTextBox();
            	else if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
            		
            }
        }
	}

	private void showTextBox() 
	{
		TextBox box=new TextBox("Enter Text",text,maxSize,properties);
		box.addCommand(new Command("OK",Command.OK,1));
		GlobalControl.getControl().getDisplay().setCurrent(box);
		box.setCommandListener(new BoxListener());
	}
	public void keyPressedEventReturned(int keyCode){}
	public void keyReleasedEvent(int keyCode){}
	public void keyReleasedEventReturned(int keyCode){}
	public void keyRepeatedEvent(int keyCode) 
	{	
		keyPressedEvent(keyCode);
	}
	public void keyRepeatedEventReturned(int keyCode){}
	public synchronized void paint(Graphics g, Rectangle clip) 
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
    				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_BACKGROUND)).intValue());
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
        	g.fillRoundRect(displayRect.x, displayRect.y, 
    				displayRect.width-1, displayRect.height-1,
    				radius, radius);
    		if(paintBorder)
    		{
    			g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue():
        					((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		g.drawRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
    		}
        	g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_FOREGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOREGROUND)).intValue());
            StringBuffer buff=new StringBuffer();
    		if((properties&javax.microedition.lcdui.TextField.PASSWORD)!=0)
    				for(int i=0;i<text.length();i++)buff.append("*");
    		else
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
                    	if(focussed && end==buff.length())
                    	g.drawLine(txtStart+font.stringWidth(buff.toString().substring(start, end)), 
                    			displayRect.y+2+i*(font.getHeight()+4), 
                    			txtStart+font.stringWidth(buff.toString().substring(start, end)),
                    			displayRect.y+1+i*(font.getHeight()+4)+font.getHeight());
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
            	 if(focussed)
            		 g.drawLine(displayRect.x+textIndent+textWidth, 
                 			displayRect.y+1, 
                 			displayRect.x+textIndent+textWidth,
                 			displayRect.y+displayRect.height-2);
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
        textWidth=((properties&javax.microedition.lcdui.TextField.PASSWORD)!=0?
				font.stringWidth("*")*text.length():font.stringWidth(text))+2;
        if(textWidth<rect.width || textWraps)
        {
            scrolling=false;
        }
        else if(focussed && !scrolling)
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
			textWidth=((properties&javax.microedition.lcdui.TextField.PASSWORD)!=0?
					this.font.stringWidth("*")*text.length():font.stringWidth(text))+2;
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
		return paintBorder;
	}
	public void setPaintBorder(boolean paint) {
		paintBorder=paint;
	}
	public Font getFont() {
		return fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
	}
	public void setFont(Font font) {
		this.font=font;
		fontSet=true;
	}
	class BoxListener implements CommandListener
	{

		public void commandAction(Command command, Displayable disp) {
			if(command.getLabel().equals("OK"))
			{
				try
				{
					TextField.this.setText(((TextBox)disp).getString());
					disp=null;
					GlobalControl.getControl().getDisplay().setCurrent(
							GlobalControl.getControl().getMainDisplayCanvas());
					GlobalControl.getControl().getMainDisplayCanvas().setFullScreenMode(true);
					GlobalControl.getControl().refreshLayout();
					if(parent instanceof Scrollable)
						((Scrollable)parent).scrollRectToVisible(displayRect, 
								Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
				}catch(IllegalArgumentException iae){}
				Runtime.getRuntime().gc();
			}
		}
	}
	public byte getAlignment() {
		return this.alignment;
	}
	public void setAlignment(byte alignment) {
		this.alignment=alignment;
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