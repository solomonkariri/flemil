package org.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.ListSelectionListener;
import org.flemil.ui.Item;
import org.flemil.ui.Scrollable;
import org.flemil.util.Rectangle;




/**
 * Class that represents a list of Items that can be selected. A list allows a user to use
 * a collection of items in a panel as a single list Item. You can make use of the setSelectsAll
 * attribute of the Panel to make sure that all the items in the Panel are focussed when the 
 * Panel is selected as an item in the List
 * @author Solomon Kariri
 *
 */
public class List implements Item {
	private Vector elements;
	private Item currentItem;
	private boolean focussed;
	private Item parent;
	private Rectangle displayRect;
	private int topMargin=2;
	private int bottomMargin=0;
	private int leftMargin=0;
	private int rightMargin=0;
	private boolean paintBorder=true;
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
	public int getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}
	public int getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}
	private int availableWidth;
	private boolean focusible=true;
	private ListSelectionListener listener;
	private int visualIndex=-1;
	private int topIndex=-1;
	private int displayable;
	private int maxHeight;
	
	/**
	 * Creates a default list with no elements
	 */
	public List()
	{
		displayRect=new Rectangle();
		elements=new Vector();
	}
	/**
	 * Adds an Item to the end of the list
	 * @param item the Item to be added to the List
	 */
	public synchronized void add(Item item)
	{
		if(currentItem==null)
    	{
			topIndex=0;
			visualIndex=0;
    	}
    	elements.addElement(item);
    	currentItem=(Item)elements.elementAt(0);
    	item.setParent(this);
    	layoutItems();
    	if(focussed && currentItem!=null && currentItem.getDisplayRect()!=null){
    		visualizeRect(currentItem.getDisplayRect());
    		currentItem.focusGained();
    	}
	}
	/**
	 * Returns the index of the currently selected Item in the List. The 
	 * indecies starts at 0 and goes up to n-1 where n is the current count 
	 * of the items in the list
	 * @return the index of the currently selected item in this List
	 */
	public int getSelectedIndex() {
		return elements.indexOf(currentItem);
	}
	/**
	 * Removes the item passed to this method from this List
	 * @param item the Item to be removed from this List
	 */
	public synchronized void remove(Item item)
	{
    	elements.removeElement(item);
    	item.setParent(null);
    	topIndex=0;
    	visualIndex=0;
    	displayable=0;
    	if(!elements.isEmpty())
    	{
    		if(currentItem!=null)currentItem.focusLost();
    		currentItem=(Item)elements.elementAt(0);
    		if(focussed)currentItem.focusGained();
    	}
    	else
    	{
    		currentItem=null;
    	}
    	GlobalControl.getControl().refreshLayout();
	}
	public synchronized void removeAll()
	{
		topIndex=0;
		visualIndex=0;
		displayable=0;
		if(currentItem!=null)currentItem.focusLost();
		currentItem=null;
    	for(int i=0;i<elements.size();i++)
    	{
    		((Item)elements.elementAt(i)).setParent(null);
    	}
    	elements.removeAllElements();
    	layoutItems();
	}
	/**
	 * Removes the Item at the index passed to this method from this List
	 * @param index the index of the Item to be removed from this List
	 */
	public synchronized void remove(int index)
	{
		remove((Item)elements.elementAt(index));
	}
	/**
	 * Sets the entry passed to this method as the currently selected Item in this List.
	 * @param entry the entry to be set as the currently selected Item in this List
	 */
	public void setSelectedItem(Object entry)
	{
		if(elements.contains(entry))
		{
			if(currentItem!=null)
				currentItem.focusLost();
			int index=elements.indexOf(entry);
			if(index<topIndex)
			{
				topIndex=index;
				visualIndex=0;
				layoutItems();
			}
			else if(index>topIndex+displayable-1)
			{
				topIndex=index-displayable+1;
				visualIndex=displayable-1;
				layoutItems();
			}
			else
			{
				visualIndex=index-topIndex;
			}
			currentItem=(Item)elements.elementAt(index);
			if(focussed)
			{
				visualizeRect(currentItem.getDisplayRect());
				currentItem.focusGained();
				repaint(displayRect);
			}
		}
	}
	/**
	 * Sets the item at the index passed to this method as the currently selected Item
	 * @param index the index of the Item in this List to be set as the currently selected Item
	 */
	public void setSelectedIndex(int index)
	{
		setSelectedItem(elements.elementAt(index));
	}

	public boolean isHorScrolling() {
		return false;
	}
	
	
	private synchronized void layoutItems()
    {
    	if(displayRect.width<=1 || elements.isEmpty())return;
    	int trackY=getDisplayRect().y+(topIndex*(maxHeight+1))+topMargin;
    	int i=topIndex;
    	while(i<topIndex+displayable+1 && i<elements.size())
    	{
    		Item tempItem=(Item)elements.elementAt(i);
    		Rectangle tempRect=new Rectangle();
    		tempRect.y=trackY;
    		availableWidth=displayRect.width;
    		tempRect.x=displayRect.x+leftMargin;
			tempRect.width=availableWidth-rightMargin-leftMargin;	
			tempRect.height=maxHeight;
			tempItem.setDisplayRect(tempRect);
			trackY+=maxHeight+1;
			i++;
    	}
    }
	/**
	 * Returns the Item that is currently selected in this List
	 * @return the currently selected item in this List
	 */
	public Item getSelectedItem()
	{
		return currentItem;
	}
	public void focusGained() {
		focussed=true;
		if(!elements.isEmpty()){
			if(currentItem!=null)
			{
				currentItem.focusGained();
			}
			if(parent instanceof Scrollable)
			{
				Rectangle visualRect=null;
				if(topIndex==0){
					visualRect=new Rectangle(((Item)elements.elementAt(
							topIndex)).getDisplayRect());
					visualRect.y+=2;
				}
				else{
					visualRect=new Rectangle(((Item)elements.elementAt(
							topIndex+displayable-1)).getDisplayRect());
					visualRect.y-=2;
				}
				((Scrollable) parent).scrollRectToVisible(
						visualRect, Scrollable.DIRECTION_Y);
			}
			repaint(displayRect);
		}
	}

	public void focusLost() {
		focussed=false;
		if(currentItem!=null)currentItem.focusLost();
	}

	public Rectangle getDisplayRect() {
		return displayRect;
	}
	private int getMaxHeight(int availWidth)
	{
		int maxHeight=0;
		for(int i=0;i<elements.size();i++)
        {
            Rectangle minRect=(
                    (Item)elements.elementAt(i)).getMinimumDisplayRect(availWidth);
            if(minRect.height>maxHeight)
            {
            	maxHeight=minRect.height;
            }
        }
		return maxHeight;
	}
	public Rectangle getMinimumDisplayRect(int availWidth) {
        int maxHeight=getMaxHeight(availWidth-leftMargin-rightMargin);
        Rectangle result=new Rectangle();
        result.width=availWidth;
        result.height=elements.size()*(maxHeight+1)+topMargin+bottomMargin;
        return result;
	}

	public Item getParent() {
		return parent;
	}
	/**
	 * Returns the item at the index passed to this method. If 
	 * the index is less than zero or greator than the item count in this List, 
	 * then null is returned. You can get the number of Items in this list using the 
	 * getItemsCount() method
	 * @param index the index of the Item to be retrieved
	 * @return the Item at the specified index or null if the index is invalid
	 */
	public Item getItem(int index)
    {
    	if(index>-1 && index<elements.size())return (Item)elements.elementAt(index);
    	return null;
    }
	public boolean isFocusible() {
		return focusible;
	}

	public boolean isPaintBorder() {
		return paintBorder;
	}

	public void keyPressedEvent(int keyCode) {
		this.keyPressedEventReturned(keyCode);
	}
	
	private void visualizeRect(Rectangle rect)
	{
		if(currentItem!=null && parent!=null)
		{
			if(rect.y<parent.getDisplayRect().y)
			{
				if(parent instanceof Scrollable)
				{
					((Scrollable)parent).scrollRectToVisible(rect, Scrollable.DIRECTION_Y);
				}
			}
			else if(rect.y+rect.height+2>
				parent.getDisplayRect().y+parent.getDisplayRect().height)
			{
				if(parent instanceof Scrollable)
				{
					((Scrollable)parent).scrollRectToVisible(rect, Scrollable.DIRECTION_Y);
				}
			}
		}
	}
	
	public void keyPressedEventReturned(int keyCode) {
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode); 
        switch(key)
        {
            case Canvas.DOWN:
            {
            	if(visualIndex<displayable-1 && visualIndex<elements.size()-1)
            	{
            		visualIndex++;
            		currentItem.focusLost();
            		currentItem=(Item)elements.elementAt(topIndex+visualIndex);
            		currentItem.focusGained();
            		visualizeRect(currentItem.getDisplayRect());
            	}
            	else if(topIndex+displayable-1<elements.size()-1)
            	{
            		topIndex++;
            		Rectangle tmpRect=currentItem.getDisplayRect();
            		Rectangle newRect=new Rectangle(tmpRect.x, tmpRect.y+tmpRect.height+1, tmpRect.width, tmpRect.height);
            		Item it=((Item)elements.elementAt(elements.indexOf(currentItem)+1));
            		currentItem.focusLost();
            		currentItem=it;
            		currentItem.setDisplayRect(newRect);
            		currentItem.focusGained();
            		visualizeRect(newRect);
            	}
            	else
            	{
            		if(parent!=null)
                    	parent.keyPressedEventReturned(keyCode);
            	}
                break;
            }
            case Canvas.UP:
            {
            	if(visualIndex>0)
            	{
            		visualIndex--;
            		currentItem.focusLost();
            		currentItem=(Item)elements.elementAt(topIndex+visualIndex);
            		currentItem.focusGained();
            		visualizeRect(currentItem.getDisplayRect());
            	}
            	else if(topIndex>0)
            	{
            		topIndex--;
            		Rectangle tmpRect=currentItem.getDisplayRect();
            		Rectangle newRect=new Rectangle(tmpRect.x, tmpRect.y-tmpRect.height+1, tmpRect.width, tmpRect.height);
            		Item it=((Item)elements.elementAt(elements.indexOf(currentItem)-1));
            		currentItem.focusLost();
            		currentItem=it;
            		currentItem.setDisplayRect(newRect);
            		currentItem.focusGained();
            		visualizeRect(newRect);
            	}
            	else
            	{
            		if(parent!=null)
                    	parent.keyPressedEventReturned(keyCode);
            	}
            	break;
            }
            case Canvas.FIRE:
            {
            	if(listener!=null)
            	{
            		listener.itemSelected(this);
            	}
            	break;
            }
            default:
            {
            	if(parent!=null)
            	parent.keyPressedEventReturned(keyCode);
            }
        }
	}

	public void keyReleasedEvent(int keyCode) {
	}

	public void keyReleasedEventReturned(int keyCode) {
	}

	public void keyRepeatedEvent(int keyCode) {
	}

	public void keyRepeatedEventReturned(int keyCode) {
	}

	public void paint(Graphics g, Rectangle clip) {
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	for(int i=topIndex;i<topIndex+displayable+1&&i<elements.size();i++)
        	{
        		((Item)elements.elementAt(i)).paint(g, clip);
        	}
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	if(paintBorder)
        	{
        		int radius=((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.CURVES_RADIUS)).intValue();
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		g.drawRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
        	}
        	g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y) {
	}

	public void pointerDraggedEventReturned(int x, int y) {
	}

	public void pointerPressedEvent(int x, int y) {
		
	}

	public void pointerPressedEventReturned(int x, int y) {
		if(listener!=null)
    	{
    		listener.itemSelected(this);
    	}
	}

	public void pointerReleasedEvent(int x, int y) {
		for(int i=topIndex;i<topIndex+displayable;i++){
			Item testItem=(Item)elements.elementAt(i);
			if(testItem.getDisplayRect().contains(x, y, 0)){
				if(testItem.isFocusible()){
					currentItem.focusLost();
					currentItem=testItem;
					visualIndex=i-topIndex;
					currentItem.focusGained();
				}
				testItem.pointerReleasedEvent(x, y);
				return;
			}
		}
	}

	public void pointerReleasedEventReturned(int x, int y) {
		if(displayRect.contains(x, y, 0)){
			keyPressedEvent(GlobalControl.getControl().
					getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
		}
	}

	public void repaint(Rectangle clip) {
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}

	public synchronized void setDisplayRect(Rectangle rect) {
		int initial=displayable;
		displayRect=rect;
		maxHeight=getMaxHeight(displayRect.width);
		displayable=(parent.getDisplayRect().height)/(maxHeight+1);
		displayable=displayable*(maxHeight+1)<parent.getDisplayRect().height?displayable+1:displayable;
		if(displayable!=initial)
		{
			topIndex=0;
			visualIndex=0;
			currentItem=elements.isEmpty()?null:(Item)elements.elementAt(0);
			if(focussed)
				new Thread(new Runnable() {
					
					public void run() {
						currentItem.focusGained();
						visualizeRect(currentItem.getDisplayRect());
					}
				}).start();
		}
        layoutItems();
	}

	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}

	public void setParent(Item parent) {
		this.parent=parent;
	}

	public void setListener(ListSelectionListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Returns the class that has been registered to listen for selection events in this.
	 * This class must have implemented the ListSelectionListener interface
	 * @return class that has been registered to listen for selection events in this.
	 */
	public ListSelectionListener getListener() {
		return listener;
	}
	/**
	 * Returns the size of entries in this List
	 * @return the size of entries in this List
	 */
	public int getItemsCount()
	{
		return elements.size();
	}
	public boolean isFocussed() {
		return focussed;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		for(int i=0;i<elements.size();i++){
			((Item)elements.elementAt(i)).moveRect(dx, dy);
		}
	}
}
