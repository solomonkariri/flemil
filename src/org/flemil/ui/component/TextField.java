/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.util.Rectangle;


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
	private byte alignment=LocaleManager.getTextDirection()==
		LocaleManager.LTOR?TextItem.ALIGN_LEFT:TextItem.ALIGN_RIGHT;
	private boolean editable=true;
	private Vector splitIndecies=new Vector();
	private boolean fontSet;
	private int lastAvail;
	private int lastWid;
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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
		TextBox box=new TextBox(
				LocaleManager.getTranslation("flemil.entertext"),text,maxSize,properties);
		box.addCommand(new Command(
				LocaleManager.getTranslation("flemil.ok"),Command.OK,1));
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
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
                        	if(editable && focussed && end==buff.length())
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
                        	if(editable && focussed && end==buff.length())
                        	g.drawLine(txtStart-1, 
                        			displayRect.y+2+i*(font.getHeight()+4), 
                        			txtStart-1,
                        			displayRect.y+1+i*(font.getHeight()+4)+font.getHeight());
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
    					if(editable && focussed)
                        	g.drawLine(txtStart+font.stringWidth(text), 
                        			displayRect.y+2, 
                        			txtStart+font.stringWidth(text),
                        			displayRect.y+1+font.getHeight());
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
					if(editable && focussed)
                    	g.drawLine(txtStart-1, 
                    			displayRect.y+2, 
                    			txtStart-1,
                    			displayRect.y+1+font.getHeight());
            		}
            	}
            	else{
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			g.drawString(buff.toString(), displayRect.x+textIndent+1,
                    			displayRect.y+2,
                    			Graphics.TOP|Graphics.LEFT);
                    	if(focussed && editable)
                    		g.drawLine(displayRect.x+textIndent+textWidth, 
                    				displayRect.y+1, 
                    				displayRect.x+textIndent+textWidth,
                    				displayRect.y+displayRect.height-2);
            		}
            		else{
            			int txtStart=displayRect.x+displayRect.width-1-
            			font.stringWidth(buff.toString())-textIndent;
            			g.drawString(buff.toString(), txtStart,
            					displayRect.y+2,
            					Graphics.TOP|Graphics.LEFT);
            			if(focussed && editable){
            				int cursorX=displayRect.x+displayRect.width-textWidth-2-textIndent;
            				g.drawLine(cursorX, 
            						displayRect.y+1,cursorX,
            						displayRect.y+displayRect.height-2);
            			}
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
		if(displayRect.contains(x, y, 0)){
			if(editable){
				showTextBox();
			}
			else{
				parent.pointerReleasedEventReturned(x, y);
			}
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
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
		Font font=fontSet?this.font:(Font)GlobalControl.getControl().getStyle().getProperty(
                Style.ITEM_FONT);
		textWidth=((properties&javax.microedition.lcdui.TextField.PASSWORD)!=0?
				this.font.stringWidth("*")*text.length():font.stringWidth(text))+2;
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
			if(command.getLabel().equals(LocaleManager.getTranslation("flemil.ok")))
			{
				try
				{
					GlobalControl.getControl().getDisplay().setCurrent(
							GlobalControl.getControl().getMainDisplayCanvas());
					TextField.this.setText(((TextBox)disp).getString());
					new Thread(new Runnable() {
						public void run() {
							while(!GlobalControl.getControl().getMainDisplayCanvas().isShown()){
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							GlobalControl.getControl().getMainDisplayCanvas().setFullScreenMode(true);
						}
					}).start();
					disp=null;
				}catch(IllegalArgumentException iae){
				}
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
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
	}
}
