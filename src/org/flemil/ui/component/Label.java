package org.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Container;
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
	private boolean textChanged;
	
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
        textWidth=font.stringWidth(text)+2;
	}
	public void focusGained() 
	{
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textWidth=font.stringWidth(text)+2;
		if(isFocusible())
		{
			focussed=true;
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
				&& displayRect.width==availWidth && !splitIndecies.isEmpty())
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
		
		if(textWraps){
			Vector newLineSplits=new Vector();
			String temp=new String(text);
			int index=temp.indexOf('\n');
			while(index!=-1){
				newLineSplits.addElement(temp.substring(0, index).trim());
				temp=temp.substring(index+1);
				index=temp.indexOf('\n');
			}
			if(temp.length()>0)
			newLineSplits.addElement(temp.trim());
			
			int size=newLineSplits.size();
			for(int i=0;i<size;i++){
				String current=newLineSplits.elementAt(i).toString();
				int textWid=font.stringWidth(current);
				if(textWid>=availWidth-2){ 
					while(textWid>=availWidth-2){
						int test=(current.length()*availWidth)/textWid;
						while(test>current.length())test--;
						String testString=current.substring(0, test);
						while(font.stringWidth(testString)>=availWidth-2){
							test--;
							testString=current.substring(0, test);
						}
						textWid=font.stringWidth(testString);
						while(textWid<availWidth-2){
							test++;
							if(test>current.length())break;
							testString=current.substring(0, test);
							textWid=font.stringWidth(testString);
						}
						if(test>current.length())break;
						if(current.charAt(test-1)!=' '){
							int spaceIndex=current.substring(0, test-1).lastIndexOf(' ');
							if(spaceIndex!=-1){
								test=spaceIndex+1;
							}
						}
						test--;
						splitIndecies.addElement(current.substring(0, test).trim());
						current=current.substring(test).trim();
						textWid=font.stringWidth(current);
					}
					if(current.length()>0)splitIndecies.addElement(current.trim());
				}
				else{
					splitIndecies.addElement(current);
				}
			}
		}
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
            if(textWraps)
            {
            	if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            		int txtStart=displayRect.x+1;
            		int size=splitIndecies.size();
                	for(int i=0;i<size;i++)
                	{
                		String currentString=splitIndecies.elementAt(i).toString();
                		if(i==size-1 || 
                				font.stringWidth(currentString)<displayRect.width-2)
                        {
                        	switch (alignment) {
        					case TextItem.ALIGN_RIGHT:
        						txtStart=displayRect.x+displayRect.width-
        						font.stringWidth(currentString)-1;
        						break;
        					case TextItem.ALIGN_CENTER:
        						txtStart=displayRect.x+
        						(displayRect.width-font.stringWidth(currentString))/2;
        						break;
        					}
                        }
                		g.drawString(currentString, txtStart, 
                        		displayRect.y+2+(i*(font.getHeight()+4)), Graphics.TOP|Graphics.LEFT);
                	}
            	}
            	else{
            		int size=splitIndecies.size();
                	for(int i=0;i<size;i++)
                	{
                		String currentString=splitIndecies.elementAt(i).toString();
                		int txtStart=displayRect.x+displayRect.width-1;
                		txtStart-=font.stringWidth(currentString);
                		if(i==size-1 || 
                				font.stringWidth(currentString)<displayRect.width-2)
                        {
                        	switch (alignment) {
        					case TextItem.ALIGN_LEFT:
        						txtStart=displayRect.x+1;
        						break;
        					case TextItem.ALIGN_CENTER:
        						txtStart=displayRect.x+
        						(displayRect.width-font.stringWidth(currentString))/2;
        						break;
        					}
                        }
                		g.drawString(currentString, txtStart, 
                        		displayRect.y+2+(i*(font.getHeight()+4)), Graphics.TOP|Graphics.LEFT);
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
    					g.drawString(text, txtStart, 
                        		displayRect.y+2, Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			int txtStart=displayRect.x+displayRect.width-1-
            			font.stringWidth(text.toString());
					switch (alignment) {
					case TextItem.ALIGN_LEFT:
						txtStart=displayRect.x+1;
						break;
					case TextItem.ALIGN_CENTER:
						txtStart=displayRect.x+
						(displayRect.width-font.stringWidth(text))/2;
						break;
					}
					g.drawString(text, txtStart, 
                    		displayRect.y+2, Graphics.TOP|Graphics.LEFT);
            		}
            	}
            	else{
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			g.drawString(text, displayRect.x+textIndent+1,
                    			displayRect.y+2,
                    			Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			int txtStart=displayRect.x+displayRect.width-1-
            			font.stringWidth(text)-textIndent;
            			g.drawString(text, txtStart,
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
		textWidth=font.stringWidth(text)+2;
		textIndent=0;
        textWidth=font.stringWidth(text)+2;
        if(!textWraps && focusible && focussed)
        {
        	textChanged=true;
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
		splitIndecies.removeAllElements();
		this.text=text;
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textWidth=font.stringWidth(text)+2;
		if(textWraps && parent!=null){
			new Thread(new Runnable() {
				public void run() {
					int currentHeight=displayRect.height;
					int newHeight=getMinimumDisplayRect(displayRect.width).height;
					int diff=Math.abs(newHeight-currentHeight);
					Font font=fontSet?Label.this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
			                Style.ITEM_FONT);
					if(diff>font.getHeight()/2){
						diff=newHeight-currentHeight;
						displayRect.height=newHeight;
						((Container)parent).itemHeightChanged(Label.this, diff);
					}
				}
			}).start(); 
		}
		else if(focussed)
		{
			textChanged=true;
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
	public void setTextChanged(boolean textChanged) {
		this.textChanged = textChanged;
	}
	public boolean isTextChanged() {
		return textChanged;
	}
}
