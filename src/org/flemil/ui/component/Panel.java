package org.flemil.ui.component;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.ui.Item;
import org.flemil.ui.Scrollable;
import org.flemil.util.Rectangle;




/**
 * Class that represents a collections of items that are treated as 
 * a group within the display if the application. A Panel
 * can have a number of propereties that can be set by calling its methods.
 * Such properties include things such the layout of the items 
 * within the Panel and whether the Panel should allow its contents
 * to be scrolled horizontally or not. Panels can contain any collection of 
 * any items including other Panels, Lists and other basic Items  
 * @author Solomon Kariri
 */
public class Panel implements Scrollable
{  
    //Define the items alignment constants
    public static final byte CENTER_VERTICAL_ALIGN=0x01;
    public static final byte CENTER_HORIZONTAL_ALIGN=0x02;
    public static final byte LEFT_HORIZONTAL_ALIGN=0x04;
    public static final byte RIGHT_HORIZONTAL_ALIGN=0x08;
    public static final byte SPAN_FULL_WIDTH=0x10;
    //The parent to this item
    private Item parent;
    //The currently focussed item in this panel
    private Item currentlyFocussed;
    //Currently Added Items
    private Vector children;
    //The currently available display Rect
    private Rectangle displayRect;
    //flag for whether content is scrollable horizontally
    private boolean hScrolling;    
    //The scrollbars to be used when content cannot fit
    private ScrollBar horizontalScrollBar;
    private ScrollBar verticalScrollBar;
    //the variable for the right margin
    private int rightMargin=0;
    public int getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}
	public int getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}
	public int getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}
	public int getBottomMargin() {
		return bottomMargin;
	}
	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	//the variable for the left margin
    private int leftMargin=0;
    //the variable for the top margin
    private int topMargin=0;
    //the variable for the left margin
    private int bottomMargin=0;
    //variable for alignment
    private int alignment=Panel.LEFT_HORIZONTAL_ALIGN;
    //variables for keeping track of scrolling
    private int currentYStart=1;
    private int currentXStart=1;
    private boolean paintBorder;
    private boolean focussed;
    private boolean focusible=true;
	private boolean vScrolling;
	private boolean horScrolling;
	private int availableWidth=1;
	private int availableHeight=1;
	int scrWid;
	private boolean focusAll;
	private Item defaultFocusItem;
	private boolean dragged;
	private int lastPointY;
    
    public boolean isHorScrolling() {
		return horScrolling;
	}
	public void setHorScrolling(boolean horScrolling) {
		this.horScrolling = horScrolling;
	}
	public boolean isFocusible() {
		return focusible;
	}
	public void setFocusible(boolean focusible) {
		this.focusible = focusible;
	}
	public boolean isPaintBorder() {
		return paintBorder;
	}
	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}
	/**
     * Creates a new Panel with the specified parent Item
     */
    public Panel()
    {
        children=new Vector();
        displayRect=new Rectangle();
    }
    /**
     * Retuens the count of Items that has been added to this Panel. A Panel or
     * List added to this Panel will be counted as s single Item. Items that 
     * contains a collection of other elements are also treated as such where
     * the Item is counted as a single Item by its containing parent Item
     * @return the number of Items that have been added to this Panel
     */
    public int getItemsCount()
    {
    	return children.size();
    }
    /**
     * Sets whether this Panel should have all of its contained Items gain focus when 
     * it receives focus. This is handly when using Pnales with a collection of 
     * elements as List components where all the Items within the Panel will
     * be required to gain focus
     * @param focus true to focus on all Items on focus and false otherwise
     */
    public void setFocusAllOnFocus(boolean focus,Item defaultItem)
    {
    	focusAll=focus; 
    	if(focus)
    	{
    		defaultFocusItem=defaultItem;
    	}
    	if(focussed && focusAll)
    	{
    		focusAll();
    		currentlyFocussed=defaultFocusItem;
    	}
    }
    private void focusAll()
    {
    	int size=children.size();
		for(int i=0;i<size;i++)
		{
			if(((Item)children.elementAt(i)).isFocusible())
			{
				((Item)children.elementAt(i)).focusGained();
			}
		}
    }
    private void unFocusAll()
    {
    	int size=children.size();
		for(int i=0;i<size;i++)
		{
			if(((Item)children.elementAt(i)).isFocusible())
			{
				((Item)children.elementAt(i)).focusLost();
			}
		}
    }
    public Rectangle getDisplayRect()
    {
        return displayRect;
    }
    public void keyReleasedEventReturned(int keyCode)
    {
    }
    public void keyReleasedEvent(int keyCode)
    {
    }
    public void keyRepeatedEventReturned(int keyCode)
    {
    }
    public void keyRepeatedEvent(int keyCode)
    {
        if(currentlyFocussed!=null)
        {
            currentlyFocussed.keyRepeatedEvent(keyCode);
        }
        else
        {
            //Process the key yourself
        }
    }
    public void keyPressedEventReturned(int keyCode)
    {
    	int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.DOWN:
            {
            	if(focusAll)parent.keyPressedEventReturned(keyCode);
            	if(currentlyFocussed!=null && 
            			currentlyFocussed.getDisplayRect().height>displayRect.height-topMargin-bottomMargin)
            	{
            		if(currentlyFocussed.getDisplayRect().y+currentlyFocussed.getDisplayRect().height>
            		displayRect.y+displayRect.height-topMargin-bottomMargin)
            		{
            			scrollItems(0,-GlobalControl.getPanelScrollSpeed());
            			break;
            		}
            	}
            	int position=currentlyFocussed!=null?children.indexOf(currentlyFocussed):0;
        		while(position<children.size()-1
        				&& focussed)
        		{
        			Item tst=(Item)children.elementAt(position+1);
        			if(tst.isFocusible())
        			{
        				currentlyFocussed.focusLost();
            			currentlyFocussed=tst;
            			currentlyFocussed.focusGained();
            			if(!(currentlyFocussed instanceof Scrollable))
            			scrollRectToVisible(currentlyFocussed.getDisplayRect(), Scrollable.DIRECTION_Y);
            			break;
        			}
        			position++;
        		}
        		if(position==children.size()-1)
        		{
        			if(currentYStart>displayRect.y-availableHeight-topMargin+displayRect.height+bottomMargin
        					&& !(parent instanceof Scrollable))
        			{
        				scrollItems(0,-GlobalControl.getPanelScrollSpeed());
        			}
        			else if(parent!=null)
        			{
        				parent.keyPressedEventReturned(keyCode);
        			}
        			break;
        		}
        		break;
            }
            case Canvas.UP:
            {
            	if(focusAll)parent.keyPressedEventReturned(keyCode);
            	if(currentlyFocussed!=null && 
            			currentlyFocussed.getDisplayRect().height>displayRect.height-topMargin-bottomMargin)
            	{
            		if(currentlyFocussed.getDisplayRect().y<
            		displayRect.y+topMargin)
            		{
            			scrollItems(0,GlobalControl.getPanelScrollSpeed());
            			break;
            		}
            	}
            	int position=currentlyFocussed!=null?children.indexOf(currentlyFocussed):
            		children.size()-1;
        		while(position>0
        				&& focussed)
        		{
        			Item tst=(Item)children.elementAt(position-1);
        			if(tst.isFocusible())
        			{
        				currentlyFocussed.focusLost();
            			currentlyFocussed=tst;
            			currentlyFocussed.focusGained();
            			if(!(currentlyFocussed instanceof Scrollable))
            			scrollRectToVisible(currentlyFocussed.getDisplayRect(), Scrollable.DIRECTION_Y);
            			break;
        			}
        			position--;
        		}
        		if(position==0)
        		{
        			if(currentYStart<displayRect.y+topMargin && !(parent instanceof Scrollable))
        			{
        				scrollItems(0,GlobalControl.getPanelScrollSpeed());
        			}
        			else if(parent!=null)
        			{
        				parent.keyPressedEventReturned(keyCode);
        			}
        			break;
        		}
            	break;
            }
            case Canvas.LEFT:
            {
            	if(focusAll)parent.keyPressedEventReturned(keyCode);
            	if(hScrolling)
            	{
            		scrollItems(GlobalControl.getPanelScrollSpeed(), 0);
            	}
            	else
            	{
            		parent.keyPressedEventReturned(keyCode);
            	}
            	break;
            }
            case Canvas.RIGHT:
            {
            	if(focusAll)parent.keyPressedEventReturned(keyCode);
            	if(hScrolling)
            	{
            		scrollItems(-GlobalControl.getPanelScrollSpeed(), 0);
            	}
            	else
            	{
            		parent.keyPressedEventReturned(keyCode);
            	}
            	break;
            }
            default:{
            	if(parent!=null){
            		parent.keyPressedEventReturned(keyCode);
            	}
            }
        }
    }
    public void keyPressedEvent(int keyCode)
    {
    	if(currentlyFocussed!=null)
    	{
    		currentlyFocussed.keyPressedEvent(keyCode);
    	}
    	else
    	{
    		if(parent!=null)
    		{
    			parent.keyPressedEventReturned(keyCode);
    		}
    	}
    }
    public void pointerPressedEventReturned(int x,int y)
    {
        
    }
    public void pointerReleasedEventReturned(int x,int y)
    {
    	parent.pointerReleasedEventReturned(x, y);
    }
    public void pointerDraggedEventReturned(int x,int y)
    {
        
    }
    public void pointerPressedEvent(int x,int y)
    {
    	dragged=false;
    	if(vScrolling && verticalScrollBar!=null){
    		if(verticalScrollBar.getDisplayRect().contains(x, y, 0)){
    			verticalScrollBar.pointerPressedEvent(x, y);
    		}
    		else{
    			lastPointY=y;
        	}
    	}
    }
    public void pointerReleasedEvent(int x,int y)
    {
    	if(dragged)return;
    	if(vScrolling && verticalScrollBar!=null){
    		if(verticalScrollBar.getDisplayRect().contains(x, y, 0)){
    			verticalScrollBar.pointerReleasedEvent(x, y);
    			return;
    		}
    	}
    	for(int i=0;i<children.size();i++){
    		Item testItem=(Item)children.elementAt(i);
    		if(testItem.getDisplayRect().contains(x, y, 0)){
    			if(testItem.isFocusible()){
    				currentlyFocussed.focusLost();
        			currentlyFocussed=testItem;
        			currentlyFocussed.focusGained();
    			}
    			testItem.pointerReleasedEvent(x, y);
    		}
    	}
    }
    public void pointerDraggedEvent(int x,int y)
    {
    	if(!vScrolling)return;
    	dragged=true;
    	if(!verticalScrollBar.getDisplayRect().contains(x, y, 0)){
    		int diff=y-lastPointY;
        	lastPointY=y;
        	if(vScrolling){
        		scrollItems(0, diff);
        	}
    	}
    	else{
    		verticalScrollBar.pointerDraggedEvent(x, y);
    	}
    }
    public  void repaint(Rectangle clip)
    {
    	if(parent!=null)
    	{
    		parent.repaint(clip);
    	}
    }
    public  void paint(Graphics g,Rectangle clip)
    {
    	Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	if(focusAll && focussed)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_FOCUS_BACKGROUND)).intValue());
        		g.fillRect(intersect.x, intersect.y, intersect.width, intersect.height);
        	}
        	int size=children.size();
        	for(int i=0;i<size;i++)
        	{
        		Item tempChild=(Item)children.elementAt(i);
        		tempChild.paint(g, intersect);
        	}
        	if(hScrolling && horizontalScrollBar!=null)
        	{
        		horizontalScrollBar.paint(g, intersect);
        	}
        	if(vScrolling && verticalScrollBar!=null)
    		{
    			verticalScrollBar.paint(g, intersect);
    		}
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	if(paintBorder)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		int radius=((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.CURVES_RADIUS)).intValue();
        		g.drawRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
        	}
        	g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
    }
    public Rectangle getMinimumDisplayRect(int availWidth)
    {
        //Initialize the minimum possible width and height
        int minWid=1;
        int minHei=1;
        //Get the number of children already added to this panel
        int size=children.size();
        int scrw=(availWidth/30);
        for(int i=0;i<size;i++)
        {
            Rectangle minRect=(
                    (Item)children.elementAt(i)).getMinimumDisplayRect(availWidth-scrw);
            minWid=(minRect.width>minWid)?minRect.width:minWid;
            minHei+=minRect.height;
        }
        Rectangle result=new Rectangle();
        //Add 2 to width and height to account for border
        result.width=minWid+3+leftMargin+rightMargin;
        result.height=minHei+topMargin+bottomMargin+children.size();
        return result;
    }
    public synchronized void setDisplayRect(Rectangle rect)
    {
        displayRect=rect;
        layoutItems();
    }
    public void focusLost()
    {
    	focussed=false;
    	if(!focusAll)
    	{
    		if(currentlyFocussed!=null)
            {
                currentlyFocussed.focusLost();
            }
    	}
    	else
    	{
    		unFocusAll();
    	}
    	repaint(displayRect);
    }
    public void focusGained()
    {
    	focussed=true;
        if(children.isEmpty())return;
        if(!focusAll)
        {
        	if(currentlyFocussed!=null)
    		{
    			if(displayRect.width>1)
    		        this.scrollRectToVisible(currentlyFocussed.getDisplayRect(),
    		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
    			currentlyFocussed.focusGained();
    		}
        }
        else
        {
        	focusAll();
        	currentlyFocussed=defaultFocusItem;
        }
		repaint(displayRect);
    }
    public void setParent(Item parent)  
    {
        this.parent=parent;
    }
    public Item getParent()
    {
        return this.parent;
    }
    /**
     * Returns the Item at the given index in this Panel.
     * @param index the index for which to return the Item at
     * @return Item at the index or null if the index value is not 
     * in the allowed values.
     */
    public Item getItem(int index)
    {
    	if(index>-1 && index<children.size())return (Item)children.elementAt(index);
    	return null;
    }
    /**
     * Adds an Item to the end of this Panel
     * @param item the Item to be added to this Panel
     */
    public synchronized void add(Item item)
    {
    	if(currentlyFocussed!=null && !currentlyFocussed.isFocusible())currentlyFocussed=null;
    	if(currentlyFocussed==null && item.isFocusible())
    	{
    		currentlyFocussed=item;
    	}
    	children.addElement(item);
    	item.setParent(this);
    	if(focussed && currentlyFocussed!=null){currentlyFocussed.focusGained();}
    	if(currentlyFocussed==null){
    		currentlyFocussed=item;
    	}
    	layoutItems();
    }
    /**
     * Removes all the items that have been added to this Panel
     */
    public synchronized void removeAll()
    {
    	if(currentlyFocussed!=null){
    		currentlyFocussed.focusLost();
    	}
    	currentlyFocussed=null;
    	for(int i=0;i<children.size();i++)
    	{
    		((Item)children.elementAt(i)).setParent(null);
    	}
    	children.removeAllElements();
    	layoutItems();
//    	new Thread(new Runnable() {
//			public void run() {
//				repaint(displayRect);				
//			}
//		}).start();
    }
    /**
     * Removes the Item passed to this method from this Panel
     * @param item the Item to be removed from this Panel
     */
    public synchronized void remove(Item item)
    {
    	if(item==currentlyFocussed)
    	{
    		currentlyFocussed=null;
    		int index=children.indexOf(item);
    		if(index>0)
    		{
    			currentlyFocussed=(Item)children.elementAt(index-1);
    			if(focussed)currentlyFocussed.focusGained();
    		}
    		else if(index==0 && children.size()>1)
    		{
    			currentlyFocussed=(Item)children.elementAt(index+1);
    			if(focussed)currentlyFocussed.focusGained();
    		}
    	}
    	children.removeElement(item);
    	item.setParent(null);
    	GlobalControl.getControl().refreshLayout();
    }
    /**
     * Remove the item at the given index from this Panel
     * @param index the index at which to remove an Item
     */
    public synchronized void remove(int index)
    {
    	if(index<children.size() && index>-1)
    	{
    		remove((Item)children.elementAt(index));
    	}
    }
    private  void layoutItems()
    {
    	if(children.isEmpty() || displayRect.width<=1)return;
    	hScrolling=vScrolling=false;
    	verticalScrollBar=horizontalScrollBar=null;
    	if(displayRect.width<=1)return;
    	int largestItemWidth=1;
    	scrWid=0;
    	Vector tempStr=new Vector();
    	/*
    	 * if horizontal scrolling is enabled and horizontal alignment is centered
    	 * then we get the largest with to use to center the other items in the panel 
    	 */
    	if(horScrolling &&
    			(alignment&Panel.SPAN_FULL_WIDTH)==0)
    	{
    		Enumeration elements=children.elements();
        	while(elements.hasMoreElements())
        	{
        		Item tmpItm=(Item)elements.nextElement();
        		if(tmpItm.getMinimumDisplayRect(displayRect.width*3).width>largestItemWidth)
        		{
        			largestItemWidth=tmpItm.getMinimumDisplayRect(displayRect.width*3).width;
        		}
        	}
    	}
    	int trackY=getDisplayRect().y+topMargin;
    	int size=children.size();
    	int i=0;
    	while(i<size)
    	{
    		Item tempItem=(Item)children.elementAt(i);
    		Rectangle tempRect=tempItem.getMinimumDisplayRect(
    				(horScrolling && (alignment&Panel.SPAN_FULL_WIDTH)==0)?
    						displayRect.width*3-scrWid:displayRect.width-scrWid-leftMargin-rightMargin-2);
    		tempRect.y=trackY;
			if(horScrolling && (alignment&Panel.SPAN_FULL_WIDTH)==0)
			{
				availableWidth=largestItemWidth+scrWid+leftMargin+rightMargin<displayRect.width*3?
						(largestItemWidth+scrWid+leftMargin+rightMargin)<displayRect.width?
								displayRect.width:(largestItemWidth+scrWid+leftMargin+rightMargin)
								:displayRect.width*3;
				if(availableWidth>displayRect.width && !hScrolling)
				{
					if(availableWidth>displayRect.width*3)
					{
						availableWidth=displayRect.width*3;
					}
					hScrolling=true;
				}
			}
			else
			{
				availableWidth=displayRect.width;
			}
			if(!horScrolling)
			{
				if(tempRect.width>availableWidth-rightMargin-leftMargin)
				{
					tempRect.width=availableWidth-rightMargin-leftMargin;
				}
			}
			if((alignment&Panel.CENTER_HORIZONTAL_ALIGN)!=0)
			{
				tempRect.x=displayRect.x-scrWid+(availableWidth/2)-tempRect.width/2;
			}
			else if((alignment&Panel.RIGHT_HORIZONTAL_ALIGN)!=0)
			{
				tempRect.x=displayRect.x+availableWidth-rightMargin-scrWid-tempRect.width;
			}
			else if((alignment&Panel.LEFT_HORIZONTAL_ALIGN)!=0)
			{
				tempRect.x=displayRect.x+leftMargin;
			}
			else if((alignment&Panel.SPAN_FULL_WIDTH)!=0)
			{
				tempRect.x=displayRect.x+leftMargin;
				tempRect.width=displayRect.width-rightMargin-leftMargin-scrWid;
			}	
			if(tempRect.width>availableWidth){
				tempRect.width=availableWidth-scrWid;
			}
			tempStr.addElement(tempRect);
			trackY+=tempRect.height+1;
			if(tempRect.y+tempRect.height>displayRect.y+displayRect.height-topMargin-bottomMargin)
			{
				if(!vScrolling)
				{
					vScrolling=true;
					scrWid=displayRect.width/30;
					i=0;
					trackY=getDisplayRect().y+topMargin;
					tempStr.removeAllElements();
					continue;
				}
			}
			if(i==size-1)
			{
				availableHeight=tempRect.y+tempRect.height+
					topMargin+bottomMargin-((Rectangle)tempStr.elementAt(0)).y;
			}
			i++;
    	}
    	//check the alignment for vertical centering
    	if(!children.isEmpty() && (alignment&Panel.CENTER_VERTICAL_ALIGN)!=0)
    	{
    		Rectangle tstRect=(Rectangle)tempStr.elementAt(size-1);
    		if(tstRect.y+tstRect.
    				height+bottomMargin<displayRect.y+displayRect.height)
    		{
    			int diff=((displayRect.y+displayRect.height)-
    				(tstRect.y+tstRect.
    	    				height+bottomMargin))/2;
    			for(int j=0;j<size;j++)
    			{
    				((Rectangle)tempStr.elementAt(j)).y+=diff;
    			}
    		}
    	}
    	for(int k=0;k<size;k++)
    	{
    		((Item)children.elementAt(k)).setDisplayRect((Rectangle)tempStr.elementAt(k));
    	}
    	tempStr.removeAllElements();
    	Runtime.getRuntime().gc();
    	layoutScrollers();
    	repaint(displayRect);
    }
    private void layoutScrollers()
    {
    	verticalScrollBar=null;
    	horizontalScrollBar=null;
    	if(displayRect.width<=1)return;
    	currentXStart=displayRect.x+leftMargin;
    	currentYStart=displayRect.y+topMargin;
    	if(vScrolling)
    	{
    		int scrheight=hScrolling?displayRect.height-scrWid:displayRect.height;
    		verticalScrollBar=new ScrollBar(displayRect.height-(topMargin+bottomMargin),
    				availableHeight,
    				ScrollBar.VERTICAL_ORIENTATION);
    		verticalScrollBar.setParent(this);
    		Rectangle rect=new Rectangle();
    		rect.x=displayRect.x+displayRect.width-scrWid;
    		rect.width=scrWid;
    		rect.y=displayRect.y;
    		rect.height=scrheight;
    		verticalScrollBar.setDisplayRect(rect);
    		verticalScrollBar.setCurrentPoint(0);
    	}
    	if(hScrolling)
    	{
    		int scrWidth=vScrolling?displayRect.width-7:displayRect.width;
    		horizontalScrollBar=new ScrollBar(displayRect.width-(leftMargin+rightMargin),
    				availableWidth,
    				ScrollBar.HORIZONTAL_ORIENTATION);
    		horizontalScrollBar.setParent(this);
    		Rectangle rect=new Rectangle();
    		rect.x=displayRect.x;
    		rect.width=scrWidth;
    		rect.y=displayRect.y+displayRect.height-displayRect.height/30;
    		rect.height=displayRect.height/30;
    		horizontalScrollBar.setDisplayRect(rect);
    		horizontalScrollBar.setCurrentPoint(0);
    	}
    }
	private void scrollItems(int dx,int dy)
    {
		if(dx!=0 && hScrolling)
		{
			if((displayRect.x-(currentXStart+dx))>(availableWidth-(displayRect.width)))
			{
				dx=(displayRect.x-currentXStart)-(availableWidth-(displayRect.width));
			}
			if(currentXStart+dx>displayRect.x+leftMargin)
			{
				dx=displayRect.x+leftMargin-currentXStart;
			}  
		}
		if(dy!=0 && vScrolling)
		{
			if((displayRect.y-(currentYStart+dy))>(availableHeight-(displayRect.height)))
			{
				dy=(displayRect.y-currentYStart)-(availableHeight-(displayRect.height));
			}
			if(currentYStart+dy>displayRect.y+topMargin)
			{
				dy=displayRect.y+topMargin-currentYStart;
			}
		}
    	if(displayRect.width>1)
    	{
    		Enumeration elements=children.elements();
        	while(elements.hasMoreElements())
        	{
        		Item tmpItem=(Item)elements.nextElement();
        		if(hScrolling)
        		{
        			tmpItem.moveRect(dx, 0);
        		}
        		if(vScrolling)
        		{
        			tmpItem.moveRect(0, dy);
        		}
        	}
        	if(vScrolling && verticalScrollBar!=null)
    		{
    			currentYStart+=dy;
    			verticalScrollBar.setCurrentPoint(-(currentYStart-(displayRect.y+topMargin)));
    		}
    		if(hScrolling && horizontalScrollBar!=null)
    		{
    			currentXStart+=dx;
    			horizontalScrollBar.setCurrentPoint(-(currentXStart-(displayRect.x+leftMargin)));
    		}
    	}
    	repaint(displayRect);
    }
	/**
	 * Sets the alignment to be used by this Panel to layout its Items. The value
	 * of this parameter is a binary or (|) operation on the values
	 * CENTER_VERTICAL_ALIGN ,CENTER_HORIZONTAL_ALIGN,
	 * LEFT_HORIZONTAL_ALIGN,RIGHT_HORIZONTAL_ALIGN,
	 * SPAN_FULL_WIDTH
	 * The resultant layout is quite straightforward from the names of the constants.
	 * for example passing the value CENTER_HORIZONTAL_ALIGN|CENTER_VERTICAL_ALIGN
	 * centers the contents of this Panel both vertically and horizontally.
	 * The values that determine the horizontal layout should be displayed
	 * only once for example its not recommended to use a value such as
	 * CENTER_HORIZONTAL_ALIGN|LEFT_HORIZONTAL_ALIGN will results in unpredictable
	 * results. Only one of the directives will be taken into consideration 
	 * @param alignment the alignmen to be used to display items in this Panel.
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
		layoutItems();
	}
	/**
	 * Returns the alignment being currently used by this Panel to layout its Items
	 * @return the alignment being currently used by this Panel
	 */
	int getAlignment() { 
		return alignment;
	}
	public void scrollRectToVisible(Rectangle rect, int scrollDirection) 
	{
		if((scrollDirection&Scrollable.DIRECTION_X)!=0)
		{
			if(!hScrolling && parent instanceof Scrollable)
			{
				((Scrollable)parent).scrollRectToVisible(rect, Scrollable.DIRECTION_X);
			}
			else if(hScrolling)
			{
				int diff=0;
				if(rect.x<displayRect.x+leftMargin)
				{
					diff=(displayRect.x+leftMargin)-rect.x;
					scrollItems(diff, 0);
				}
				else if(rect.x+rect.width>displayRect.x+displayRect.width-rightMargin)
				{
					diff=(rect.x+rect.width)-(displayRect.x+displayRect.width-rightMargin);
					if(rect.x-diff<displayRect.x+leftMargin)
					{
						diff-=(displayRect.x+leftMargin)-(rect.x-diff);
					}
					scrollItems(-diff, 0);
				}
				if(parent instanceof Scrollable && diff!=0)
				{
					Rectangle tmp=new Rectangle(rect.x,rect.y,rect.width,rect.height);
					tmp.x+=diff;
					((Scrollable)parent).scrollRectToVisible(tmp, Scrollable.DIRECTION_X);
				}
			}
		}
		if((scrollDirection&Scrollable.DIRECTION_Y)!=0)
		{
			if(!vScrolling && parent instanceof Scrollable)
			{
				((Scrollable)parent).scrollRectToVisible(rect, Scrollable.DIRECTION_Y);
			}
			else if(vScrolling)
			{
				int diff=0;
				if(rect.y<displayRect.y+topMargin)
				{
					diff=(displayRect.y+topMargin)-rect.y;
					scrollItems(0, diff);
				}
				else if(rect.y+rect.height>displayRect.y+displayRect.height-bottomMargin-2)
				{
					diff=(rect.y+rect.height)-(displayRect.y+displayRect.height-bottomMargin-2);
					scrollItems(0, -diff);
				}
				if(parent instanceof Scrollable && diff!=0)
				{
					Rectangle tmp=new Rectangle(rect.x,rect.y,rect.width,rect.height);
					tmp.y+=diff;
					((Scrollable)parent).scrollRectToVisible(tmp, Scrollable.DIRECTION_Y);
				}
			}
		}
	}
	public boolean isFocussed() {
		return focussed;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		for(int i=0;i<children.size();i++){
			((Item)children.elementAt(i)).moveRect(dx, dy);
		}
	}
	public void acrollContentsHorizontally(int change) {
		// TODO Auto-generated method stub
		
	}
	public void scrollContentsVertically(int change) {
		scrollItems(0, change);
	}
}
