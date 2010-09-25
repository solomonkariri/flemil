package org.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.util.Rectangle;


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
	private byte alignment=LocaleManager.getTextDirection()==
		LocaleManager.LTOR?TextItem.ALIGN_LEFT:TextItem.ALIGN_RIGHT;
	Vector splitIndecies=new Vector();
	private boolean focussed;
	private boolean fontSet;
	private int textIndent;
	private int textWidth=1;
	private boolean scrolling;
	private int lastAvail;
	private int lastWid;
	
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		if(isFocusible())
		{
			focussed=true;
			textWidth=font.stringWidth(text)+2;
	        repaint(displayRect);
	        if(!textWraps)
	        {
	        	int diff=textWidth-displayRect.width+2;
	            if(diff>0)
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
		if(availWidth<=1)return new Rectangle(); 
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		if(lastAvail==availWidth && lastWid==font.stringWidth(text)
				&& displayRect.width==availWidth)
		{
			return displayRect;
		}
		lastAvail=availWidth;
		lastWid=font.stringWidth(text);
		if(text.equals("") || availWidth<font.getHeight())
		{
			return new Rectangle(0,0,availWidth,font.getHeight()+4);
		}
		splitIndecies.removeAllElements();
		int counts=textWidth/(availWidth-2);
		if(counts>0){
			int index=text.length()/counts;
			int track=0;
			for(int i=0;i<counts;i++){
				int test=track+index-1;
				while(test>text.length())test--;
				String testString=text.substring(track, test);
				while(font.stringWidth(testString)>availWidth-2 && test<text.length()){
					test--;
					testString=text.substring(track, test);
				}
				while(font.stringWidth(testString)<availWidth-2){
					if(test>=text.length())break;
					test++;
					testString=text.substring(track, test);
				}
				if(test==text.length()){
					if(font.stringWidth(testString)>availWidth-2){
						splitIndecies.addElement(new Integer(test-1));
					}
					break;
				}
				else{
					splitIndecies.addElement(new Integer(test-1));
					track=test-1;
				}
			}
		}
		splitIndecies.addElement(new Integer(text.length()));
		Rectangle minRect=new Rectangle();
        minRect.height=textWraps?(font.getHeight()+4)*splitIndecies.size():font.getHeight()+2;
        minRect.width=availWidth;
        return minRect;
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
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
            if(textWraps)
            {
            	if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            		int txtStart=displayRect.x+1;
                	int start=0;
            		int end=0;
                	for(int i=0;i<splitIndecies.size();i++)
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
                        		displayRect.y+2+(i*(font.getHeight()+4)), Graphics.TOP|Graphics.LEFT);
                		start=end;
                	}
            	}
            	else{
            		int start=0;
            		int end=0;
                	for(int i=0;i<splitIndecies.size();i++)
                	{
                		int txtStart=displayRect.x+displayRect.width-1;
                		end=((Integer)splitIndecies.elementAt(i)).intValue();
                		int tmp=end;
                		while(tmp>start && buff.charAt(tmp-1)=='\n')
                		{
                			tmp--;
                		}
                		txtStart-=font.stringWidth(buff.toString().substring(start, tmp));
                		if(end==buff.length() || 
                				font.stringWidth(buff.toString().substring(start, tmp))<displayRect.width-2)
                        {
                        	switch (alignment) {
        					case TextItem.ALIGN_LEFT:
        						txtStart=displayRect.x+1;
        						break;
        					case TextItem.ALIGN_CENTER:
        						txtStart=displayRect.x+
        						(displayRect.width-font.stringWidth(buff.toString().substring(start,tmp)))/2;
        						break;
        					}
                        }
                		g.drawString(buff.toString().substring(start, tmp), txtStart, 
                        		displayRect.y+2+(i*(font.getHeight()+4)), Graphics.TOP|Graphics.LEFT);
                		start=end;
                	}
            	}
            }
            else
            {
            	if(textWidth<displayRect.width-2){
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			int txtStart=displayRect.x+1;
    					switch (alignment) {
    					case TextItem.ALIGN_RIGHT:
    						txtStart=displayRect.x+displayRect.width-
    						font.stringWidth(text)-1;
    						break;
    					case TextItem.ALIGN_CENTER:
    						txtStart=displayRect.x+
    						(displayRect.width-font.stringWidth(text))/2;
    						break;
    					}
    					g.drawString(buff.toString(), txtStart, 
                        		displayRect.y+2, Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			int txtStart=displayRect.x+displayRect.width-1-
            			font.stringWidth(buff.toString());
					switch (alignment) {
					case TextItem.ALIGN_LEFT:
						txtStart=displayRect.x+1;
						break;
					case TextItem.ALIGN_CENTER:
						txtStart=displayRect.x+
						(displayRect.width-font.stringWidth(text))/2;
						break;
					}
					g.drawString(buff.toString(), txtStart, 
                    		displayRect.y+2, Graphics.TOP|Graphics.LEFT);
            		}
            	}
            	else{
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			g.drawString(buff.toString(), displayRect.x+textIndent+1,
                    			displayRect.y+2,
                    			Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			int txtStart=displayRect.x+displayRect.width-1-
            			font.stringWidth(buff.toString())-textIndent;
            			g.drawString(buff.toString(), txtStart,
            					displayRect.y+2,
            					Graphics.TOP|Graphics.LEFT);
            		}
            	}
            }
            g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y){}
	public void pointerDraggedEventReturned(int x, int y){}
	public void pointerPressedEvent(int x, int y){
	}
	public void pointerPressedEventReturned(int x, int y){
	}
	public void pointerReleasedEvent(int x, int y){
		parent.pointerReleasedEventReturned(x, y);
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textWidth=font.stringWidth(text)+2;
		int test=textWidth/(displayRect.width-2);
		if(textWidth%(displayRect.width-2)!=0){
			test++;
		}
		if(test==splitIndecies.size()){
			this.text=text;
			splitIndecies.removeElementAt(splitIndecies.size()-1);
			splitIndecies.addElement(new Integer(this.text.length()));
			if(!textWraps && focussed)
			{
				scrolling=false;
				int diff=textWidth-displayRect.width;
	            if(diff>0)
	            {
	                new Thread(new TextScroller(this)).start();
	            }
			}
			repaint(displayRect);
			return;
		}
		this.text=text;
		if(textWraps && parent!=null)GlobalControl.getControl().refreshLayout();
		else if(focussed)
		{
			scrolling=false;
			int diff=textWidth-displayRect.width;
            if(diff>0)
            {
                new Thread(new TextScroller(this)).start();
            }
		}
		repaint(displayRect);
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
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
	}
}
