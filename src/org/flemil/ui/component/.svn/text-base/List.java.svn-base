package org.flemil.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.ListSelectionListener;
import org.flemil.ui.Container;
import org.flemil.ui.Item;
import org.flemil.ui.Scrollable;
import org.flemil.ui.Window;
import org.flemil.util.Rectangle;




/**
 * Class that represents a list of Items that can be selected. A list allows a user to use
 * a collection of items in a panel as a single list Item. You can make use of the setSelectsAll
 * attribute of the Panel to make sure that all the items in the Panel are focussed when the 
 * Panel is selected as an item in the List
 * @author Solomon Kariri
 *
 */
public class List implements Container {
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
	private boolean wraps=false;
	private Panel parentWindowPane;
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
	private boolean focusible=true;
	private ListSelectionListener listener;
	private int maxHeight;
	private int currentIndex;
	
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
	public void add(Item item)
	{
		synchronized (this) {
			if(item instanceof Panel){
				((Panel)item).setVScrollable(false);
			}
			elements.addElement(item);
	    	if(currentItem==null){
	    		currentItem=(Item)elements.elementAt(0);
	    		currentIndex=0;
	    	}
	    	if(parent!=null){
	    		item.setParent(this);
		    	if(parentWindowPane!=null){
		    		if(parentWindowPane.getParent() instanceof ScreenWindow){
		    			((ScreenWindow)parentWindowPane.getParent()).setDisplayRect(
		    					parentWindowPane.getParent().getDisplayRect());
		    		}
		    		else if(parentWindowPane.getParent() instanceof TabsControl){
		    			((TabsControl)parentWindowPane.getParent()).refreshItemsRect(parentWindowPane);
		    		}
		    		else if(parentWindowPane.getParent() instanceof PopUpWindow){
		    			ScreenWindow current=(ScreenWindow)parentWindowPane.getParent().getParent();
		    			if(current!=null){
		    				current.layoutCurrentPopup();
		    			}
		    		}
		    	}
		    	if(focussed && currentItem!=null && currentItem.getDisplayRect()!=null){
		    		visualizeRect(currentItem.getDisplayRect());
		    		currentItem.focusGained();
		    	}
	    	}
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
	public void remove(Item item)
	{
		synchronized (this) {
			if(item.equals(currentItem)){
				currentItem.focusLost();
				int index=elements.indexOf(item);
				if(index>0)
				{
					currentItem=((Item)elements.elementAt(index-1));
					currentIndex=index-1;
				}
				else{
					if(!elements.isEmpty()){
						currentItem=((Item)elements.elementAt(0));
						currentIndex=0;
					}
					else{
						currentItem=null;
						currentIndex=-1;
					}
				}
			}
	    	elements.removeElement(item);
	    	item.setParent(null);
	    	if(parentWindowPane!=null){
	    		if(parentWindowPane.getParent() instanceof ScreenWindow){
	    			((ScreenWindow)parentWindowPane.getParent()).setDisplayRect(
	    					parentWindowPane.getParent().getDisplayRect());
	    		}
	    		else if(parentWindowPane.getParent() instanceof TabsControl){
	    			((TabsControl)parentWindowPane.getParent()).refreshItemsRect(parentWindowPane);
	    		}
	    		else if(parentWindowPane.getParent() instanceof PopUpWindow){
	    			ScreenWindow current=(ScreenWindow)parentWindowPane.getParent().getParent();
	    			if(current!=null){
	    				current.layoutCurrentPopup();
	    			}
	    		}
	    	}
		}
		if(currentItem!=null && focussed)currentItem.focusGained();
    	repaint(displayRect);
	}
	public void removeAll()
	{
		synchronized (this) {
			if(currentItem!=null)currentItem.focusLost();
			currentItem=null;
	    	for(int i=0;i<elements.size();i++)
	    	{
	    		((Item)elements.elementAt(i)).setParent(null);
	    	}
	    	elements.removeAllElements();
	    	if(parentWindowPane!=null){
	    		if(parentWindowPane.getParent() instanceof ScreenWindow){
	    			((ScreenWindow)parentWindowPane.getParent()).setDisplayRect(
	    					parentWindowPane.getParent().getDisplayRect());
	    		}
	    		else if(parentWindowPane.getParent() instanceof TabsControl){
	    			((TabsControl)parentWindowPane.getParent()).refreshItemsRect(parentWindowPane);
	    		}
	    		else if(parentWindowPane.getParent() instanceof PopUpWindow){
	    			ScreenWindow current=(ScreenWindow)parentWindowPane.getParent().getParent();
	    			if(current!=null){
	    				current.layoutCurrentPopup();
	    			}
	    		}
	    	}
		}
	}
	/**
	 * Removes the Item at the index passed to this method from this List
	 * @param index the index of the Item to be removed from this List
	 */
	public void remove(int index)
	{
		synchronized (this) {
			remove((Item)elements.elementAt(index));
		}
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
			currentItem=(Item)elements.elementAt(index);
			currentIndex=index;
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
	
	
	private void layoutItems()
    {
		synchronized (this) {
			if(parentWindowPane==null)return;
	    	int yStart=displayRect.y+2;
	    	for(int i=0;i<elements.size();i++){
	    		Rectangle itemRect=new Rectangle(displayRect.x+leftMargin+1, yStart+i*(maxHeight+1), displayRect.width-leftMargin-rightMargin-2, maxHeight);
	    		((Item)elements.elementAt(i)).setDisplayRect(itemRect);
	    	}
	    	if(focussed && currentItem!=null){
	    		visualizeRect(currentItem.getDisplayRect());
	    	}
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
			visualizeRect(currentItem.getDisplayRect());
			repaint(displayRect);
		}
	}

	public void focusLost() {
		if(!focussed)return;
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
        maxHeight=getMaxHeight(availWidth-leftMargin-rightMargin);
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
		Item testItem=parent;
		while(testItem!=null && !(testItem instanceof Scrollable)){
			testItem=testItem.getParent();
		}
		if(testItem instanceof Scrollable){
			((Scrollable)testItem).scrollRectToVisible(rect);
		}
	}
	
	public void keyPressedEventReturned(int keyCode) {
		if(elements.isEmpty())return;
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode); 
        switch(key)
        {
            case Canvas.DOWN:
            {
            	if(currentIndex==elements.size()-1){
            		if(!wraps){
            			visualizeRect(((Item)elements.elementAt(currentIndex)).getDisplayRect());
                		if(parent!=null){
                			parent.keyPressedEventReturned(keyCode);
                		}
            		}
            		else{
            			if(currentItem!=null)currentItem.focusLost();
            			currentIndex=0;
            			currentItem=(Item)elements.elementAt(currentIndex);
            			Rectangle rect=new Rectangle(currentItem.getDisplayRect());
                		rect.y-=2;
                		rect.height+=2;
                		visualizeRect(rect);
                		if(focussed)currentItem.focusGained();
            		}
            	}
            	else{
            		currentIndex++;
            		currentItem.focusLost();
            		currentItem=(Item)elements.elementAt(currentIndex);
            		visualizeRect(currentItem.getDisplayRect());
            		if(focussed)currentItem.focusGained();
            	}
                break;
            }
            case Canvas.UP:
            {
            	if(currentIndex==0){
            		if(!wraps){
            			Rectangle rect=new Rectangle(((Item)elements.elementAt(currentIndex)).getDisplayRect());
                		rect.y-=2;
                		rect.height+=2;
                		visualizeRect(rect);
                		if(parent!=null){
                			parent.keyPressedEventReturned(keyCode);
                		}
            		}
            		else{
            			if(currentItem!=null)currentItem.focusLost();
            			currentIndex=elements.size()-1;
            			currentItem=(Item)elements.elementAt(currentIndex);
            			Rectangle rect=new Rectangle(currentItem.getDisplayRect());
                		visualizeRect(rect);
                		if(focussed)currentItem.focusGained();
            		}
            	}
            	else{
            		currentIndex--;
            		currentItem.focusLost();
            		currentItem=(Item)elements.elementAt(currentIndex);
            		Rectangle rect=new Rectangle(currentItem.getDisplayRect());
            		rect.y-=2;
            		rect.height+=2;
            		visualizeRect(rect);
            		if(focussed)currentItem.focusGained();
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
        	for(int i=0;i<elements.size();i++)
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
		for(int i=0;i<elements.size();i++){
			Item testItem=(Item)elements.elementAt(i);
			if(testItem.getDisplayRect().contains(x, y, 0)){
				if(testItem.isFocusible()){
					currentItem.focusLost();
					currentItem=testItem;
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

	public void setDisplayRect(Rectangle rect) {
		synchronized (this) {
			if(parentWindowPane==null)return;
			this.displayRect=rect;
	        layoutItems();
		}
		if(focussed && currentItem!=null){
			currentItem.focusGained();
		}
	}

	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}

	public void setParent(Item parent) {
		this.parent=parent;
		Item lastPanel=null;
        if(parent!=null){
    		Item test=parent;
    		while(test!=null && !(test instanceof Window) && 
    				!(test instanceof TabsControl)){
    			if(test instanceof Panel)lastPanel=test;
    			test=test.getParent();
    		}
    		if(test!=null){
    			parentWindowPane=(Panel)lastPanel;
    		}
    		try{
    			for(int i=0;i<elements.size();i++){
    				((Item)elements.elementAt(i)).setParent(this);
    			}
    		}
    		catch(Exception exc){
//    			exc.printStackTrace();
    			}
    	}
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
	public void itemHeightChanged(Item item, int change) {
		if(parent!=null)
			GlobalControl.getControl().refreshLayout();
	}
	public void setWraps(boolean wraps) {
		this.wraps = wraps;
	}
	public boolean isWraps() {
		return wraps;
	}
}
