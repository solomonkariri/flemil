package org.flemil.ui.component;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.TabListener;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.util.Rectangle;




/**
 * A class that represents a Tabs Control
 * @author Solomon Kariri
 *
 */
public class TabsControl implements Item {

	private boolean focusible=true;
	private Vector tabItems;
	private Item currentItem;
	private String currentTitle="";
	private boolean focussed;
	private Rectangle displayRect;
	private Item parent;
	private boolean titlesFocussed;
	private boolean paintBorder;
	private int currentIndex=-1;
	private int drawIndex;
	private Image arrow;
	private boolean scrolling;
	private int textWidth;
	private int textIndent;
	private Rectangle currentTitleRect;
	private Vector titles;
	private TabListener listener;
	
	/**
	 * Creats a tab control that contains no tabs
	 */
	public TabsControl()
	{
		displayRect=new Rectangle();
		tabItems=new Vector();
		titles=new Vector();
		currentTitleRect=new Rectangle();
	}
	/**
	 * moves to the next tab in this TabsControl
	 */
	public void moveToNext()
	{
		keyPressedEventReturned(
				GlobalControl.getControl().getMainDisplayCanvas().
				getKeyCode(Canvas.RIGHT));
	}
	/**
	 * Moves to the previous tab in this TabsControl
	 */
	public void moveToPrevious()
	{
		keyPressedEventReturned(
				GlobalControl.getControl().getMainDisplayCanvas().
				getKeyCode(Canvas.LEFT));
	}
	/**
	 * Sets the selected index for this TabsControl. he indecies starts at zero
	 * @param index the index to be set as the currently selected/focussed tab 
	 * for this TabsControl
	 */
	public void setSelectedIndex(int index)
	{
		if(index>tabItems.size()-1 || index<0)return;
		if(index>currentIndex)
		{
			drawIndex=2;
			currentIndex=index;
		}
		else if(index<currentIndex-2)
		{
			drawIndex=0;
			currentIndex=index+2;
		}
		else
		{
			drawIndex=2-(currentIndex-index);
		}
		currentItem=(Item)tabItems.elementAt(index);
		currentTitle=titles.elementAt(index).toString();
		refreshScroller();
		repaint(displayRect);
		if(listener!=null){
			listener.tabSelectionChanged(this);
		}
	}
	/**
	 * Sets the selected Tab Item for this TabsControl
	 * @param item the Item to be set as the currently  
	 *selected for this TabsControl
	 */
	public void setSelectedItem(Item item)
	{
		if(tabItems.contains(item))
		{
			setSelectedIndex(tabItems.indexOf(item));
		}
	}
	/**
	 * Adds an Item to this TabsControl which is added ass a new Tab
	 * @param item the Item to be added. 
	 * @param title the title to be displayed for that tab
	 */
	public void add(Panel item,String title)
	{
    	tabItems.addElement(item);
    	titles.addElement(title);
    	item.setParent(this);
    	if(currentItem==null){
    		currentItem=item;
    		currentTitle=title;
    	}
    	if(currentIndex<2)currentIndex++;
    	if(focussed){
    		refreshScroller();
    		currentItem.focusGained();
    	}
    	if(parent!=null)parent.repaint(getDisplayRect());
	}
	/**
	 * Removes all the tabs that have been added to this TabsControl
	 */
	public void removeAll()
	{
		tabItems.removeAllElements();
		titles.removeAllElements();
		drawIndex=0;
		currentIndex=-1;
		if(focussed)repaint(displayRect);
	}
	/**
	 * Returns the index of the currently selected tab intem in this TabControl
	 * @return index of the currently selected tab intem in this TabControl
	 */
	public int getSelectedIndex()
	{
		return tabItems.indexOf(currentItem);
	}
	/**
	 * Removes the specified Item from this TabsControl
	 * @param item the Item to be removed from this TabsControl
	 */
	public void remove(Item item)
	{
		if(item==currentItem)
    	{
    		currentItem=null;currentTitle="";
    		int index=tabItems.indexOf(item);
    		if(index>0)
    		{
    			tabItems.removeElementAt(index);
    			titles.removeElementAt(index);
    		}
    		else if(index==0)
    		{
    			tabItems.removeElementAt(index);
    			titles.removeElementAt(index);
    		}
    		if(index<=currentIndex && currentIndex>tabItems.size()-1)
    		{
    			currentIndex--;
    		}
    		if(drawIndex>currentIndex)
    		{
    			drawIndex=currentIndex;
    		}
    	}
		else
		{
			if(tabItems.contains(item))
			{
				int index=tabItems.indexOf(item);
				tabItems.removeElementAt(index);
				titles.removeElementAt(index);
				if(index<=currentIndex  && currentIndex>tabItems.size()-1)
				{
					currentIndex--;
				}
				if(drawIndex>currentIndex)
	    		{
	    			drawIndex=currentIndex;
	    		}
			}
		}
		if(!tabItems.isEmpty())
		{
			currentItem=(Item)tabItems.elementAt(currentIndex>=2?currentIndex-2+drawIndex:drawIndex);
			currentTitle=titles.elementAt(currentIndex>=2?currentIndex-2+drawIndex:drawIndex).toString();
			refreshScroller();
		}
		else
		{
			drawIndex=0;
			currentIndex=-1;
			currentItem=null;
		}
		if(focussed && currentItem!=null)currentItem.focusGained();
		if(parent!=null)parent.repaint(getDisplayRect());
	}
	/**
	 * Removes the item at the specified index from this TabsControl
	 * @param index the index of the Item to be removed from this TabsControl
	 */
	public void remove(int index)
	{
		if(index<tabItems.size() && index>-1)
		{
			remove((Item)tabItems.elementAt(index));
		}
	}
	public boolean isFocusible() {
		return focusible;
	}

	public void setFocusible(boolean focusible) {
		this.focusible = focusible;
	}

	public void focusGained() {
		if(focussed || tabItems.isEmpty())return;
		focussed=true;
		titlesFocussed=true;
	}

	public void focusLost() {
		focussed=false;
		titlesFocussed=false;
        if(currentItem!=null)
        {
            currentItem.focusLost();
        }
        repaint(displayRect);
	}

	public Rectangle getDisplayRect() {
		return displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) {
		Rectangle rect=new Rectangle();
		if(GlobalControl.getControl().getCurrent()!=null &&
				GlobalControl.getControl().getCurrent().isFullScreenOn())
		{
			rect=GlobalControl.getControl().getFullScreenRect();
			rect.height-=4;
		}
		else
		{
			rect.width=GlobalControl.getControl().getDisplayArea().width;
			rect.height=GlobalControl.getControl().getDisplayArea().height-
				GlobalControl.getControl().getTitleBGround().getHeight()-
				GlobalControl.getControl().getMenuBarBGround().getHeight()-5;
		}
		return rect;
	}

	public Item getParent() {
		return this.parent;
	}
	/**
	 * Returns the Item at the index passed to this method
	 * @param index the index of the Item to be returned
	 * @return the Item at the specified index in this TabsControl or null if no 
	 * Item exists at the specified index
	 */
	public Item getItem(int index)
    {
    	if(index>-1 && index<tabItems.size())return (Item)tabItems.elementAt(index);
    	return null;
    }
	public void keyPressedEvent(int keyCode) {
		if(tabItems.isEmpty())return;
		if(titlesFocussed)
		{
			int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);
			if(key==Canvas.DOWN && currentItem.isFocusible())
			{
				titlesFocussed=false;
				currentItem.focusGained();
			}
			else if(key==Canvas.UP)
			{
				if(parent!=null)parent.keyPressedEventReturned(keyCode);
			}
			else{
				keyPressedEventReturned(keyCode);
			}
			repaint(displayRect);
			return;
		}
		else if(currentItem!=null && currentItem.isFocusible())
    	{
    		currentItem.keyPressedEvent(keyCode);
    	}
    	else
    	{
    		if(parent!=null)
    		{
    			parent.keyPressedEventReturned(keyCode);
    		}
    	}
	}

	public void keyPressedEventReturned(int keyCode) {
		if(tabItems.isEmpty())return;
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
	        case Canvas.RIGHT:
	        {
	        	if(drawIndex>=2 && drawIndex<tabItems.size()-1)
	        	{
	        		if(currentIndex<tabItems.size()-1)
		        	{
	        			titlesFocussed=true;
		        		currentIndex++;
		        		if(focussed)currentItem.focusLost();
		        		currentItem=(Item)tabItems.elementAt(currentIndex);
		        		currentTitle=titles.elementAt(currentIndex).toString();
		        		refreshScroller();
		        		if(listener!=null){
		        			listener.tabSelectionChanged(this);
		        		}
		        	}
	        		else if(parent!=null)parent.keyPressedEventReturned(keyCode);
	        			
	        	}
	        	else if(drawIndex<tabItems.size()-1)
	        	{
	        		titlesFocussed=true;
	        		drawIndex++;
	        		int index=tabItems.indexOf(currentItem);
	        		if(focussed)currentItem.focusLost();
	        		currentItem=(Item)tabItems.elementAt(index+1);
	        		currentTitle=titles.elementAt(index+1).toString();
	        		refreshScroller();
//	        		currentItem.focusGained();
	        		if(listener!=null){
	        			listener.tabSelectionChanged(this);
	        		}
	        	}
	        	if(focussed)
	        	{
	        		repaint(displayRect);
	        	}
	        	break;
	        }
	        case Canvas.LEFT:
	        {
	        	if(drawIndex<=0)
	        	{
	        		if(currentIndex>2)
		        	{
	        			titlesFocussed=true;
		        		currentIndex--;
		        		if(focussed)currentItem.focusLost();
		        		currentItem=(Item)tabItems.elementAt(currentIndex-2);
		        		currentTitle=titles.elementAt(currentIndex-2).toString();
		        		refreshScroller();
//		        		currentItem.focusGained();
		        		if(listener!=null){
		        			listener.tabSelectionChanged(this);
		        		}
		        	}
	        		else if(parent!=null)parent.keyPressedEventReturned(keyCode);
	        	}
	        	else
	        	{
	        		titlesFocussed=true;
	        		drawIndex--;
	        		if(focussed)currentItem.focusLost();
	        		int index=tabItems.indexOf(currentItem);
	        		currentItem=(Item)tabItems.elementAt(index-1);
	        		currentTitle=titles.elementAt(index-1).toString();
	        		refreshScroller();
//	        		currentItem.focusGained();
	        		if(listener!=null){
	        			listener.tabSelectionChanged(this);
	        		}
	        	}
	        	if(focussed)
	        	{
	        		repaint(displayRect);
	        	}
	        	break;
	        }
	        case Canvas.UP:
	        {
	        	if(currentItem.isFocusible())
	        	{
	        		currentItem.focusLost();
	        	}
	        	if(titlesFocussed && parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
        		else
        		{
        			titlesFocussed=true;
        			refreshScroller();
        		}
	        	repaint(displayRect);
	        	break;
	        }
	        default:
	        	if(parent!=null)
	        	{
	        		parent.keyPressedEventReturned(keyCode);
	        	}
        }
	}

	public void keyReleasedEvent(int keyCode) {}

	public void keyReleasedEventReturned(int keyCode) {}

	public void keyRepeatedEvent(int keyCode) {
		keyPressedEvent(keyCode);
	}

	public void keyRepeatedEventReturned(int keyCode) {}

	public void paint(Graphics g, Rectangle clip) {
		if(displayRect.width<2 || tabItems.isEmpty())return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.BUTTON_CURVE_RADIUS)).intValue();
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	Rectangle titlesRect=new Rectangle(displayRect.x,displayRect.y,displayRect.width,
        			GlobalControl.getControl().getTabBGround().getHeight());
        	Rectangle intersect2=null;
        	if((intersect2=titlesRect.calculateIntersection(intersect))!=null)
        	{
        		g.setClip(intersect2.x, intersect2.y, intersect2.width, intersect2.height);
        		if(currentIndex>2 || tabItems.size()-1>currentIndex)
            	{
            		if(currentIndex>2)
            		{
            			g.drawImage(arrow, displayRect.x+3, 
            					intersect2.y+intersect2.height/2-arrow.getHeight()/2,
            					Graphics.TOP|Graphics.LEFT);
            		}
            		if(tabItems.size()-1>currentIndex && tabItems.size()>3)
            		{
            			g.drawRegion(arrow, 0,0,arrow.getWidth(),arrow.getHeight(),Sprite.TRANS_MIRROR,
            					displayRect.x+displayRect.width-arrow.getWidth()-2,
            					intersect2.y+intersect2.height/2-arrow.getHeight()/2,
            					Graphics.TOP|Graphics.LEFT);
            		}
            	}
        		int availWid=displayRect.width-(arrow.getWidth()+3)*2;
        		for(int i=0;i<drawIndex;i++)
        		{
            		g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.TAB_BACKGROUND)).intValue());
            		g.fillRoundRect(displayRect.x+arrow.getWidth()+4+(i*availWid)/3,
            				titlesRect.y+2, availWid/3-1, titlesRect.height+4, radius, radius);
            		g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
        			g.drawRoundRect(displayRect.x+arrow.getWidth()+3+(i*availWid)/3,
        					titlesRect.y+1, availWid/3, titlesRect.height+4, radius, radius);
            		g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.TAB_FOREGROUND)).intValue());
            		g.setFont((Font)GlobalControl.getControl().getStyle().
    					getProperty(Style.TAB_FONT));
            		g.setClip(displayRect.x+arrow.getWidth()+3+(i*availWid)/3,
            				titlesRect.y+1, availWid/3, titlesRect.height+4);
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			g.drawString(titles.elementAt(currentIndex>=2?currentIndex-2+i:i).toString(),
                				displayRect.x+arrow.getWidth()+4+(i*availWid)/3+1,
                				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			String title=titles.elementAt(currentIndex>=2?currentIndex-2+i:i).toString();
            			int titleWidth=((Font)GlobalControl.getControl().getStyle().
            					getProperty(Style.TAB_FONT)).stringWidth(title);
            			g.drawString(title,
                				displayRect.x+arrow.getWidth()+4+(i*availWid)/3+1+availWid/3-titleWidth,
                				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            		g.setClip(intersect2.x, intersect2.y, intersect2.width, intersect2.height);
        		}
        		for(int i=drawIndex+1;i<3 && i<tabItems.size();i++)
        		{
        			g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.TAB_BACKGROUND)).intValue());
            		g.fillRoundRect(displayRect.x+arrow.getWidth()+4+(i*availWid)/3,
            				titlesRect.y+2, availWid/3-1, intersect2.height+4, radius,radius);
        			g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
        			g.drawRoundRect(displayRect.x+arrow.getWidth()+3+(i*availWid)/3,
            				titlesRect.y+1, availWid/3, intersect2.height+4, radius, radius);
            		g.setColor(((Integer)GlobalControl.getControl().getStyle().
            				getProperty(Style.TAB_FOREGROUND)).intValue());
            		g.setFont((Font)GlobalControl.getControl().getStyle().
    					getProperty(Style.TAB_FONT));
            		g.setClip(displayRect.x+arrow.getWidth()+3+(i*availWid)/3,
            				intersect2.y+1, availWid/3, intersect2.height+4);
            		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
            			g.drawString(titles.elementAt(currentIndex>=2?currentIndex-2+i:i).toString(),
                				displayRect.x+arrow.getWidth()+4+(i*availWid)/3+1,
                				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            		else{
            			String title=titles.elementAt(currentIndex>=2?currentIndex-2+i:i).toString();
            			int titleWidth=((Font)GlobalControl.getControl().getStyle().
            					getProperty(Style.TAB_FONT)).stringWidth(title);
            			g.drawString(title,
                				displayRect.x+arrow.getWidth()+4+(i*availWid)/3+1+availWid/3-titleWidth,
                				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            		g.setClip(intersect2.x, intersect2.y, intersect2.width, intersect2.height);
        		}
        		Rectangle edge=new Rectangle(currentTitleRect.x,currentTitleRect.y,radius,currentTitleRect.height);
        		Rectangle interDisp=null;
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.THEME_BACKGROUND)).intValue());
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		g.fillRoundRect(interDisp.x, interDisp.y, interDisp.width+radius,
            				interDisp.height,radius,radius);
            		g.drawRegion(GlobalControl.getControl().getTabEdgeBGround(),0,0,
            				GlobalControl.getControl().getTabEdgeBGround().getWidth(),
            				GlobalControl.getControl().getTabEdgeBGround().getHeight(),
            				Sprite.TRANS_MIRROR,
            				edge.x+1, edge.y+1, Graphics.TOP|Graphics.LEFT);
            	}
            	edge=new Rectangle(currentTitleRect.x+currentTitleRect.width-radius,
            			currentTitleRect.y,radius,currentTitleRect.height);
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		g.fillRoundRect(interDisp.x-radius, interDisp.y, interDisp.width+radius,
            				interDisp.height,radius,radius);
            		g.drawImage(GlobalControl.getControl().getTabEdgeBGround(),
            				edge.x-radius, edge.y+1, Graphics.TOP|Graphics.LEFT);
            	}
            	edge=new Rectangle(currentTitleRect.x+radius,
            			currentTitleRect.y,currentTitleRect.width-(radius*2),currentTitleRect.height);
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		int imgWidth=GlobalControl.getControl().getTabBGround().getWidth();
            		for(int i=interDisp.x-1;i<interDisp.x+interDisp.width+1;i+=imgWidth-1)
            		{
            			g.fillRect(i, interDisp.y, imgWidth, interDisp.height);
            			g.drawImage(GlobalControl.getControl().getTabBGround(),
                				i, edge.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            	}
            	g.setClip(currentTitleRect.x, currentTitleRect.y, currentTitleRect.width, 
            			currentTitleRect.height);
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue());
    			g.drawRoundRect(currentTitleRect.x+1,currentTitleRect.y+1, currentTitleRect.width-2,
    					currentTitleRect.height-4, radius, radius);
            	edge=new Rectangle(displayRect.x,
            			currentTitleRect.y+((Font)GlobalControl.getControl().getStyle().
    					getProperty(Style.TAB_FONT)).getHeight(),displayRect.width,
    					currentTitleRect.height-(((Font)GlobalControl.getControl().getStyle().
    					getProperty(Style.TAB_FONT)).getHeight()));
            	if((interDisp=edge.calculateIntersection(clip))!=null)
            	{
            		g.setClip(interDisp.x, interDisp.y, interDisp.width, interDisp.height);
            		int imgWidth=GlobalControl.getControl().getTabBGround().getWidth();
            		for(int i=interDisp.x-1;i<interDisp.x+interDisp.width+1;i+=imgWidth-1)
            		{
            			g.fillRect(i, interDisp.y, imgWidth, interDisp.height);
            			g.drawImage(GlobalControl.getControl().getTabBGround(),
                				i, titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
            		}
            	}
        		
        		
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.TAB_FOCUS_FOREGROUND)).intValue());
        		g.setFont((Font)GlobalControl.getControl().getStyle().
					getProperty(Style.TAB_FONT));
        		g.setClip(currentTitleRect.x+1, currentTitleRect.y+1, 
        				currentTitleRect.width-2, currentTitleRect.height);
        		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
        			int start=textWidth>currentTitleRect.width-2?currentTitleRect.x+textIndent+1:
            			currentTitleRect.x+currentTitleRect.width/2-textWidth/2;
            		g.drawString(currentTitle, start,
            				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
        		}
        		else{
        			int start=textWidth>currentTitleRect.width-2?currentTitleRect.x+currentTitleRect.width-textWidth-textIndent:
            			currentTitleRect.x+currentTitleRect.width/2-textWidth/2;
            		g.drawString(currentTitle, start,
            				titlesRect.y+1, Graphics.TOP|Graphics.LEFT);
        		}
        	}
        	Rectangle cont=clip.calculateIntersection(new Rectangle(displayRect.x,
        			displayRect.y+titlesRect.height,displayRect.width,
        			displayRect.height-titlesRect.height));
        	if(cont!=null)currentItem.paint(g, cont);
        }
        g.setClip(clip.x, clip.y, clip.width, clip.height);
	}

	public void pointerDraggedEvent(int x, int y) {}

	public void pointerDraggedEventReturned(int x, int y) {}

	public void pointerPressedEvent(int x, int y) {
	}

	public void pointerPressedEventReturned(int x, int y) {}

	public void pointerReleasedEvent(int x, int y) {
		if(currentItem.getDisplayRect().contains(x, y, 0)){
			titlesFocussed=false;
			currentItem.pointerReleasedEvent(x, y);
		}
		else{
			if(tabItems.isEmpty())return;
			Rectangle testRect=new Rectangle(displayRect.x, displayRect.y, arrow.getWidth()+4, 
					arrow.getHeight()+4);
			if(testRect.contains(x, y, 0)){
				keyPressedEventReturned(GlobalControl.getControl().
						getMainDisplayCanvas().getKeyCode(Canvas.LEFT));
				return;
			}
			testRect=new Rectangle(displayRect.x+displayRect.width-arrow.getWidth()-2,
					displayRect.y, arrow.getWidth()+4, 
					arrow.getHeight()+4);
			if(testRect.contains(x, y, 0)){
				keyPressedEventReturned(GlobalControl.getControl().
						getMainDisplayCanvas().getKeyCode(Canvas.RIGHT));
				return;
			}
			int testWidth=(displayRect.width-(arrow.getWidth()+4)*2)/3;
			int start=displayRect.x+arrow.getWidth()+4;
			for(int i=0;i<3;i++){
				testRect=new Rectangle(start+(i*testWidth), displayRect.y, testWidth, 
						GlobalControl.getControl().getTabBGround().getHeight()+2);
				if(testRect.contains(x, y, 0)){
					if(drawIndex==i)break;
					if(tabItems.size()>i){ 
						if(focussed)currentItem.focusLost();
						if(currentIndex<3){
							currentItem=(Item)tabItems.elementAt(i);
			        		currentTitle=titles.elementAt(i).toString();
						}
						else{
							currentItem=(Item)tabItems.elementAt(currentIndex-(2-i));
			        		currentTitle=titles.elementAt(currentIndex-(2-i)).toString();
						}
		        		drawIndex=i;
		        		refreshScroller();
		        		if(listener!=null){
		        			listener.tabSelectionChanged(this);
		        		}
		        		currentItem.focusGained();
		        		repaint(displayRect);
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
	private void refreshScroller()
	{
		setCurrentItemsRect();
		int imgWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
				Style.WINDOW_TITLE_FONT)).stringWidth("M")+3;
		int availWid=displayRect.width-imgWidth*2;
		currentTitleRect=new Rectangle(displayRect.x+imgWidth+(drawIndex*availWid)/3,
				displayRect.y,availWid/3,
				GlobalControl.getControl().getTitleBGround().getHeight()+4);
		textWidth=((Font)GlobalControl.getControl().getStyle().
				getProperty(Style.WINDOW_TITLE_FONT)).stringWidth(currentTitle)+2;
		int diff=textWidth-currentTitleRect.width-2;
        if(diff>0)
        {
            new Thread(new TitleScroller()).start();
        }
        else
        {
        	scrolling=false;
        	textIndent=0;
        }
	}
	public synchronized void setDisplayRect(Rectangle rect) {
		this.displayRect=rect;
		int imgWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
				Style.WINDOW_TITLE_FONT)).stringWidth("M");
		try
		{
			arrow=GlobalControl.getImageFactory().scaleImage(Image.createImage("/arrow.png"), imgWidth,
					((Font)GlobalControl.getControl().getStyle().getProperty(
							Style.WINDOW_TITLE_FONT)).getHeight()-2, Sprite.TRANS_NONE);
		}catch(IOException ioe){ioe.printStackTrace();}
		refreshScroller();
	}
	private void setCurrentItemsRect()
	{
		int unitHeight=GlobalControl.getControl().getTabBGround().getHeight();
		if(displayRect.width<=1 ||
				displayRect.height<unitHeight)return;
		Rectangle contentRect=new Rectangle(displayRect.x+1,displayRect.y+unitHeight
				,displayRect.width-2,
				displayRect.height-unitHeight);
		if(currentItem!=null)
		{
			currentItem.setDisplayRect(contentRect);
		}
	}

	public void setParent(Item parent) {
		this.parent=parent;
	}

	public boolean isPaintBorder() {
		return this.paintBorder;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}
	private boolean running;
	private class TitleScroller implements Runnable
    {
        public void run()
        {
            if(scrolling || displayRect.width<=1 ||
            		currentTitleRect.width<=1 || running)
            {
                return;
            }
            running=true;
            //for as long as this window is focussed
            scrolling=true;
            //the variable for the increment
            int increment=-GlobalControl.getTextScrollSpeed();
            while(focussed && scrolling && titlesFocussed)
            {
                //calculate the between name and available display area
                int diff=textWidth-currentTitleRect.width;
                try
                {
                    Thread.sleep(100);
                    if(!scrolling)break;
                }catch(InterruptedException ie){}
                if(textIndent<-diff || textIndent>=0)
                {
                	repaint(currentTitleRect);
                	try{Thread.sleep(1200);}catch(InterruptedException ie){}
                	refreshScroller();
                	if(!scrolling)break;
                	textIndent=0;
                	repaint(currentTitleRect);
                	try{Thread.sleep(1500);}catch(InterruptedException ie){}
                	refreshScroller();
                	if(!scrolling)break;
                }
                textIndent+=increment;
                repaint(currentTitleRect);
            }
            textIndent=0;
            scrolling=false;
            running=false;
        }
    }
	public boolean isFocussed() {
		return focussed;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		currentTitleRect.x+=dx;
		currentTitleRect.y+=dy;
		
		if(currentItem!=null){
			currentItem.moveRect(dx, dy);
		}
	}
	public void setListener(TabListener listener) {
		this.listener = listener;
	}
	public TabListener getListener() {
		return listener;
	}
}