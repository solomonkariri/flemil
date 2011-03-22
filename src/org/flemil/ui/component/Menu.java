package org.flemil.ui.component;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector; 

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.MenuListener;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.Window;
import org.flemil.util.Rectangle;




/**
 * Class that represents a push up menu that appears when a user presses a 
 * certain key. A menu can have sub-menu by adding other menus to it. The 
 * application contains one main menu.
 * @author Solomon Kariri
 */
public class Menu implements Item
{
    //the variable used to signify a left aligned menu
    static final byte ALIGN_LEFT=1;
    //the variable used to signify a right aligned menu
    static final byte ALIGN_RIGHT=2;
    //variable used to show that a menu starts drawing its items from the top
    static final byte START_TOP=3;
    //variable used to show that a menu starts to draw its items from the bottom
    static final byte START_BOTTOM=4;
    //The name of this menu
    private String name;
    //The currently highlighted MenuItem
    private MenuItem currentItem;
    //The Left selection default Item
    private MenuItem leftItem;
    //The right selection default item
    private MenuItem rightItem;
    //The rect available for this menus display
    private Rectangle displayRect;
    //Variable to keep track of whether the menu is displayed
    private boolean displaying;
    //Variable for entries in this menu
    private Vector entries;
    //Variable to keep track of the current scroll index
    private int scrIndex;
    //Variable to keep track of the item at the top of the popup list
    private int topIndex;
    //Variable to determine whether scrolling of items is necessary
    private boolean scroll;
    //Variable for mapping pop ups
    private Hashtable mappings;
    //Variable to keep track of the possible number of displayable items
    private int displayable;
    //menu items for scrolling. Initialized only when needed
    private MenuItem[] scrolls;
    //The parent menu to this menu. Useful for sub menus
    private Item parent;
    //The variable that determines the alignment of sub menus
    byte alignment=ALIGN_RIGHT;
    //The variable that determines  where drawing will start w.r.t. the top of the menu
    private byte startPoint=START_BOTTOM;
    //The rectangle that represents the whole rectangle that the menu can use height wise
    private static Rectangle spanRect;
    //The currently displaying subMenu
    private Menu activeSubmenu;
    //item types for scroll direction
    private static final byte UPSCROLL=5;
    private static final byte DOWNSCROLL=6;
    //variable containing the number of currently visible menu items
    private int currentlyVisible;
    //variable for where to start the drawing and then to extend further if 
    //more items than can be displayed in the sub span rect are available
    private int drawStart;
    //The actual rectangle being used for displaying the available items
    private Rectangle currentView;
    private boolean paintBorder=true;
    private boolean focusible=true;
    private MenuListener menuListener;
    
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
     * Creates a new empty menu with the specified name. The name of the menu is 
     * the one that is displayed on the screen. 
     * @param name The name of the menu
     */
    public Menu(String name)
    {
        this.name=name;
        //Initialize the entries vector
        this.entries=new Vector();
        //Initialize the mappings table
        this.mappings=new Hashtable();
        //Initialize the display rect
        this.displayRect=new Rectangle();
//        //initialize the span rect
//        this.spanRect=new Rectangle();
        //initialize the current view rect
        this.currentView=new Rectangle();
    }
    public Item getParent()
    {
        return parent;
    }
    /*Method that organizes the menus to suit the menu parent so that they can be
     * correctly shown with the default left item. This method organizes items as follows
     * It first checks the type of parent for this menu.
     * Then it checks the count of items available in the menu.
     * If the items are less than three, i.e one or two, then
     * It checks to see whether there one of the item is a pop up
     * if it is then it gives it the special appearance of being like the main menu
     * else it sets the items as the left and the right items
     */
    private void organizeItems()
    {
    	synchronized (this) {
    		//this method checks the parent to see if its a window
        	//if the parent is a window, then it sets the left item, else it leaves the items intact
        	if(parent!=null && parent instanceof Window)
        	{
        		//first we  move the left and right items back to the list
        		if(rightItem!=null && !entries.contains(rightItem))
        		{
        			//we set the alignment for the sub menu back to default
        			if(rightItem.getType()==MenuItem.TYPE_POPUP_ITEM)
        			{
        				switch (this.alignment) {
    					case Menu.ALIGN_LEFT:
    						((Menu)mappings.get(rightItem)).setAlignment(Menu.ALIGN_RIGHT);		
    						break;
    					case Menu.ALIGN_RIGHT:
    						((Menu)mappings.get(rightItem)).setAlignment(Menu.ALIGN_LEFT);		
    						break;
    					}
        			}
        			entries.addElement(rightItem);
        			rightItem=null;
        		}
        		if(leftItem!=null && !entries.contains(leftItem))
        		{
        			entries.addElement(leftItem);
        			leftItem=null;
        		}
        		//if the menu is displaying then we add the select and cancel options and make them the right
        		//and left items respectively
        		if(entries.size()<3)
        		{
        			if(entries.size()==2)
        			{
        				//if both items are popup items then they will have to go the right both of them
            			if(((MenuItem)entries.elementAt(1)).getType()==MenuItem.TYPE_POPUP_ITEM &&
            					((MenuItem)entries.elementAt(0)).getType()==MenuItem.TYPE_POPUP_ITEM)
            			{
            				leftItem=null;
            				rightItem=null;
            			}
            			//else we put the pop up item to the right of the menu and give it the fake main menu property
            			else
            			{
            				if(GlobalControl.getControl().getLayout()==GlobalControl.PORTRAIT_LAYOUT)
            				{
            					if(((MenuItem)entries.elementAt(1)).getType()==MenuItem.TYPE_POPUP_ITEM)
                				{
                					leftItem=(MenuItem)entries.elementAt(0);
                					rightItem=(MenuItem)entries.elementAt(1);
                					entries.removeElement(rightItem);
                					entries.removeElement(leftItem);
                					((Menu)mappings.get(rightItem)).setAlignment(this.alignment);
                				}
                				else if(((MenuItem)entries.elementAt(0)).getType()==MenuItem.TYPE_POPUP_ITEM)
                				{
                					leftItem=(MenuItem)entries.elementAt(1);
                					rightItem=(MenuItem)entries.elementAt(0);
                					entries.removeElement(rightItem);
                					entries.removeElement(leftItem);
                				}
                				else
                				{
                					leftItem=(MenuItem)entries.elementAt(1);
                					rightItem=(MenuItem)entries.elementAt(0);
                					entries.removeElement(rightItem);
                					entries.removeElement(leftItem);
                				}
            				}
            			}
        			}
        			else if(entries.size()==1)
        			{
        				rightItem=(MenuItem)entries.elementAt(0);
    					entries.removeElement(rightItem);
    					currentItem=rightItem;
        			}
        		}
        		//if there are more than two items then we set the topmost non popup item as the left item
        		else
        		{
        			if(GlobalControl.getControl().getLayout()==GlobalControl.PORTRAIT_LAYOUT)
        			{
        				for(int i=entries.size()-1;i>=0;i--)
            			{
            				if(((MenuItem)entries.elementAt(i)).getType()!=MenuItem.TYPE_POPUP_ITEM)
            				{
            					//we set it as the left and remove it from the items list
            					leftItem=(MenuItem)entries.elementAt(i);
            					entries.removeElement(leftItem);
            					break;
            				}
            			}
        			}
        			rightItem=null;
        		}
        	}
		}
    }
    public void setParent(Item parent)
    {
        this.parent=parent;
        try {
			for(int i=0;i<entries.size();i++){
				MenuItem itm=(MenuItem)entries.elementAt(i);
				itm.setParent(this);
				if(itm.getType()==MenuItem.TYPE_POPUP_ITEM){
					((Menu)mappings.get(itm)).setParent(this);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
        organizeItems();
    }
    void setDrawStart(int start)
    {
        drawStart=start;
    }
    /**
     * Returns the name of this menu
     * @return the name of this menu
     */
    public String getName()
    {
        return name;
    }
    /**
     * Used to check whether this menu is currently displayed, that is, popped up
     * @return true if displaying and false otherwise
     */
    public boolean isDisplaying()
    {
        return displaying;
    }
    /**
     * Removes the MenuItem passed to this method from this Menu
     * @param item the MenuItem to be removed from this menu
     */
    public void remove(MenuItem item)
    {
		//first we move the left and right items back to entries
    	if(rightItem!=null)
    	{
    		entries.addElement(rightItem);
    		rightItem=null;
    	}
    	if(leftItem!=null)
    	{
    		entries.addElement(leftItem);
    		leftItem=null;
    	}
    	//remove the item and then reorganize the items
        entries.removeElement(item);
        item=null;
        organizeItems();
        //make the topmost item currently focused and visible
        scrollToTop();
        //recalculate the display item rectangles for the items
        resetItemRects();
        Runtime.getRuntime().gc();
        //if the menu was displaying then repaint it
        if(displaying)
        {
            repaint(currentView);
        }
        if(this.parent instanceof Window)
        {
        	repaint(((Window)this.parent).getMenuBarRect());
        }
    }
    
    /**
     * Removes all the menus and submenus that had been previous added to this menu
     */
    public void removeAll()
    {
    	if(currentItem!=null)
    	{
    		currentItem.focusLost();
    		currentItem=null;
    	}
    	entries.removeAllElements();
    	leftItem=null;
    	rightItem=null;
        organizeItems();
        //make the topmost item currently focused and visible
        scrollToTop();
        //recalculate the display item rectangles for the items
        resetItemRects();
        Runtime.getRuntime().gc();
        //if the menu was displaying then repaint it
        if(displaying)
        {
            repaint(currentView);
        }
        if(this.parent instanceof Window)
        {
        	repaint(((Window)this.parent).getMenuBarRect());
        }
    }
    
    
    /**
     * Removes the sub Menu passed to this Item from this Menu
     * @param menu the sub Menu to be removed from this Menu
     */
    public void remove(Menu menu)
    {
    	//first we move the left and right items back to entries
    	if(rightItem!=null)
    	{
    		entries.addElement(rightItem);
    		rightItem=null;
    	}
    	if(leftItem!=null)
    	{
    		entries.addElement(leftItem);
    		leftItem=null;
    	}
    	//A menu can only have a mapping and in the entries
        Enumeration en=mappings.keys();
        while(en.hasMoreElements())
        {
            MenuItem it=(MenuItem)en.nextElement();
            if(mappings.get(it).equals(menu))
            {
                mappings.remove(it);
                entries.removeElement(it);
                organizeItems();
                scrollToTop();
                resetItemRects();
                break;
            }
        }
        menu=null;
        Runtime.getRuntime().gc();
        if(displaying)
        {
            repaint(currentView);
            if(this.parent instanceof Window)
            {
            	repaint(((Window)this.parent).getMenuBarRect());
            }
        }
    }
    /**
     Adds a submenu to this menu
     * @param menu the submenu to be added to this menu
     * @throws java.lang.IllegalArgumentException if a sub menu with the 
     * specified name already exists under this menu
     */
    public void add(Menu menu)throws IllegalArgumentException
    {
    	synchronized (this) {
    		if(currentItem!=null)currentItem.focusLost();
    		if(parent==null)
        	{
        		throw new IllegalStateException("You cannot add a submenu " +
        				"to a menu before setting the parent menus parent");
        	}
        	//first we move the left and right items back to entries
        	if(rightItem!=null)
        	{
        		entries.addElement(rightItem);
        		rightItem=null;
        	}
        	if(leftItem!=null)
        	{
        		entries.addElement(leftItem);
        		leftItem=null;
        	}
        	if(parent!=null)
            menu.setParent(this);
            //set the alignment of the incoming menu
            switch(this.alignment)
            {
                case Menu.ALIGN_LEFT:
                    menu.setAlignment(Menu.ALIGN_RIGHT);
                    break;
                case Menu.ALIGN_RIGHT:
                    menu.setAlignment(Menu.ALIGN_LEFT);
                    break;
            }
            //Add a new sub menu item to the main menu
            MenuItem it=new MenuItem(menu.getName(),MenuItem.TYPE_POPUP_ITEM);
            //add a mapping to the mappings table
            mappings.put(it, menu);
            if(parent!=null)
            it.setParent(this);
            it.setAlignment(this.alignment);
            entries.addElement(it);
            //rearrange the current menu contents
            organizeItems();
            //scroll to the topmost item
            scrollToTop();
            //Recalculate the display rects for items if redisplaying
            resetItemRects();
		}
    	if(displaying)
        {
            repaint(currentView);
            if(this.parent instanceof Window)
            {
            	repaint(((Window)this.parent).getMenuBarRect());
            }
        }
        if(parent!=null)parent.repaint(parent.getDisplayRect());
    }
    /**
     * Adds a menu item into this menu
     * @param item the item to be added to this menu
     */
    public void add(MenuItem item)
    {
    	synchronized (this) {
    		if(currentItem!=null)currentItem.focusLost();
    		if(parent instanceof Window && this.name.equals(item.getName()))
    		{
    			throw new IllegalArgumentException("You cannot have a menu item with the same name as the menu for the main window");
    		}
    		//first we move the left and right items back to entries
    		if(rightItem!=null)
    		{
    			entries.addElement(rightItem);
    			rightItem=null;
    		}
    		if(leftItem!=null)
    		{
    			entries.addElement(leftItem);
    			leftItem=null;
    		}
    		if(parent!=null)
    		item.setParent(this);
    		//Add the item to the entries
    		entries.addElement(item);
    		organizeItems();
    		scrollToTop();
    		//Recalculate the display rects for items if redisplaying
    		resetItemRects();
		}
		if(displaying)
		{
			repaint(currentView);
		}
		if(this.parent instanceof Window)
		{
			repaint(((Window)this.parent).getMenuBarRect());
		}
		if(parent!=null)parent.repaint(parent.getDisplayRect());
    }
    void setAlignment(byte align)
    {
        this.alignment=align;
    }
    void setStartPoint(byte startP)
    {
        this.startPoint=startP;
    }
    //method called when an item is selected to hide this menu
    void itemSelected()
    {
        focusLost();
        if(parent!=null && parent instanceof Menu)
        {
            ((Menu)parent).itemSelected();
        }
        if(currentItem.getType()==MenuItem.TYPE_DEFAULT_ITEM && currentItem.getListener()!=null)
        {
        	currentItem.getListener().commandAction(currentItem);
        }
    }
    /*
     * Scrolls until it reaches the top item 
     */
    private void scrollToTop()
    {
    	if(!entries.isEmpty())
    	{
    		if(currentItem!=null)
    		{
    			currentItem.focusLost();
    		}
    		scrIndex=0;
            currentItem=(MenuItem)entries.elementAt(entries.size()-1);
            topIndex=entries.size()-1;
            if(isDisplaying())  
            {
            	currentItem.focusGained();
            }
    	}
    }
    /*
     * Scrolls until it reaches the bottom item
     */
    private void scrollToBottom()
    {
    	if(!entries.isEmpty())
    	{
    		if(currentItem!=null)
    		{
    			currentItem.focusLost();
    		}
    		scrIndex=currentlyVisible-1;
            currentItem=(MenuItem)entries.elementAt(0);
            topIndex=currentlyVisible-1;
            currentItem.focusGained();
    	}
    }
    /* Method for scrolling items up or down
     * This method scrolls only if it is possible to scroll. Otherwise it does nothing 
     */
    private void scrollItems(byte direction)
    {
    	if(!entries.isEmpty())
    	{
    		//verify that it is possible to scroll
            switch(direction)
            {
                case Menu.UPSCROLL:
                {
                	if(scroll)
                	{
                		boolean scrolling=false;
                		boolean resetting=false;
                		//check whether upward scrolling is possible
                        if(topIndex>currentlyVisible-1)
                        {
                        	scrolling=scrIndex<currentlyVisible-1?true:false;
                        	resetting=!scrolling;
                        	if(resetting)topIndex--;
                        }
                        if(resetting)
                        {
                        	currentItem.focusLost();
                            currentItem=(MenuItem)entries.elementAt(entries.indexOf(currentItem)-1);
                            resetItemRects();
                            currentItem.focusGained();
                            repaint(currentView);
                            return;
                        }
                        if(scrolling || scrIndex<currentlyVisible-1)
                        {
                        	currentItem.focusLost();
                    		int index=entries.indexOf(currentItem);
                    		currentItem=(MenuItem)entries.elementAt(index-1);
                    		scrIndex++;
                    		currentItem.focusGained();
                    		return;
                        }
                        else
                		{
                			scrollToTop();
                			resetItemRects();
                			repaint(currentView);
                			return;
                		}
                	}
                	else
                	{
                		if(scrIndex<currentlyVisible-1)
                		{
                			currentItem.focusLost();
                    		int index=entries.indexOf(currentItem);
                    		currentItem=(MenuItem)entries.elementAt(index-1);
                    		scrIndex++;
                    		currentItem.focusGained();
                		}
                		else
                		{
                			scrollToTop();
                			resetItemRects();
                		}
                	}
                    break;
                }
                case Menu.DOWNSCROLL:
                {
                	if(scroll)
                	{
                		//check whether downward scrolling is possible
                		boolean scrolling=false;
                		boolean resetting=false;
                        if(topIndex<entries.size()-1)
                        {
                        	scrolling=scrIndex>0?true:false;
                        	resetting=!scrolling;
                        	if(resetting)topIndex++;
                        }
                        if(resetting)
                        {
                        	currentItem.focusLost();
                        	/*
                        	 * this operation set the rect of the previous bottom most item to zero to avoid its
                        	 * repainting due to intersection with the clip
                        	 */
                        	((MenuItem)entries.elementAt(topIndex-currentlyVisible)).getDisplayRect().y=1;
                            currentItem=(MenuItem)entries.elementAt(entries.indexOf(currentItem)+1);
                            resetItemRects();
                            currentItem.focusGained();
                            repaint(currentView);
                            return;
                        }
                        if(scrolling || scrIndex>0)
                        {
                        	currentItem.focusLost();
                			currentItem=(MenuItem)entries.elementAt(entries.indexOf(currentItem)+1);
                    		scrIndex--;
                    		currentItem.focusGained();
                    		return;
                        }
                        else
                		{
                			scrollToBottom();
                			resetItemRects();
                			repaint(currentView);
                			return;
                		}
                	}
                	
                	else
                	{
                		if(scrIndex>0)
                		{
                			currentItem.focusLost();
                    		int index=entries.indexOf(currentItem);
                    		currentItem=(MenuItem)entries.elementAt(index+1);
                    		scrIndex--;
                    		currentItem.focusGained();
                		}
                		else
                		{
                			scrollToBottom();
                			resetItemRects();
                		}
                	}
                	break;
                }
            }
    	}
    }
    //Method that recalculates the display rects for items
    private void resetItemRects()
    {
    	synchronized (this) {
    		//if display rect is not send then return
            if(displayRect.width<2 || entries.isEmpty() || spanRect.height<=2)
            {
                return;
            }
            //Set the display rects for the menu items respecively
            Rectangle tmpRect=new Rectangle();
            tmpRect.x=displayRect.x+1;
            tmpRect.height=GlobalControl.getControl().getMenuItemBGround().getHeight();
            tmpRect.width=displayRect.width-2;
            //Set the number of displayable menu items
            displayable=(spanRect.height-2)/(tmpRect.height);
            //The starting point x for all items
            //Set whether scrolling is necessary
            if(entries.size()>displayable)
            {
            	scroll=true;
                scrolls=new MenuItem[2];
                scrolls[0]=new MenuItem("", MenuItem.TYPE_UP_SCROLL_ITEM);
                scrolls[1]=new MenuItem("", MenuItem.TYPE_DOWN_SCROLL_ITEM);
                currentlyVisible=displayable-1;
                //set the display rect for scroll items
                Rectangle tmpR=new Rectangle();
                tmpR.x=tmpRect.x;
                tmpR.width=tmpRect.width;
                tmpR.height=tmpRect.height/2;
                tmpR.y=spanRect.y+spanRect.height-(tmpRect.height*displayable);
                scrolls[0].setDisplayRect(tmpR);
                tmpR=new Rectangle();
                tmpR.x=tmpRect.x;
                tmpR.width=tmpRect.width;
                tmpR.height=tmpRect.height/2;
                tmpR.y=spanRect.y+spanRect.height-tmpR.height;
                scrolls[1].setDisplayRect(tmpR);
            }
            if(entries.size()<=displayable)
            {
            	currentlyVisible=entries.size();
            	if(scroll)
            	{
            		scroll=false;
                    scrolls=null;
                    Runtime.getRuntime().gc();
            	}
            }
            currentView.x=displayRect.x;
            currentView.width=displayRect.width;
            //if scrolling simply set the rects
            int startY=spanRect.y+spanRect.height-tmpRect.height;
            if(scroll)
            {
                startY=scrolls[0].getDisplayRect().y+scrolls[0].getDisplayRect().height;
                currentView.y=scrolls[0].getDisplayRect().y-1;
                currentView.height=tmpRect.height*displayable+2;
            }
            else
            {
            	int reqHei=currentlyVisible*tmpRect.height;
                //calculate the index of the start element
                switch(startPoint)
                {
                    case Menu.START_TOP:
                    {
                    	int diff=(spanRect.y+spanRect.height)-(drawStart-tmpRect.height);
                        if(reqHei>diff)
                        {
                            //start from the bottom and move upwards
                            startY=spanRect.y+spanRect.height-tmpRect.height*currentlyVisible;
                        }
                        else
                        {
                            //start from the top an move upwards
                            startY=drawStart-tmpRect.height;
                        }
                        break;
                    }
                    case Menu.START_BOTTOM:
                    {
                    	int diff=drawStart-spanRect.y;
                        if(reqHei>diff)
                        {
                            //start from the top an move downwards
                            startY=spanRect.y+1;
                        }
                        else
                        {
                            //start from the bottom an move upwards
                            startY=drawStart-reqHei;
                        }
                        break;
                    }
                }
                currentView.y=startY-1;
                currentView.height=reqHei+2;
            }
            //set the rects for all the visible items
            int test=topIndex-currentlyVisible;
            int i;
            for(i=entries.size()-1;i>topIndex;i--)
            {
            	((MenuItem)entries.elementAt(i)).getDisplayRect().height=0;
            }
            for(i=topIndex;i>test;i--)
            {
            	Rectangle itmRect=new Rectangle();
            	itmRect.x=tmpRect.x;
            	itmRect.width=tmpRect.width;
            	itmRect.height=tmpRect.height;
            	itmRect.y=startY+(topIndex-i)*tmpRect.height;
            	((MenuItem)entries.elementAt(i)).setDisplayRect(itmRect);
            }
            for(i=test;i>=0;i--)
            {
            	((MenuItem)entries.elementAt(i)).getDisplayRect().height=0;
            }
		}
    }
    public void keyPressedEvent(int keyCode)
    {
    	if(activeSubmenu!=null && activeSubmenu.isDisplaying())
    	{
    		activeSubmenu.keyPressedEvent(keyCode);
    	}
    	else
    	{
    		processKey(keyCode);
    	}
    }
    void setSpanRect(Rectangle rectangle)
    {
        spanRect=rectangle;
    }
    public void keyPressedEventReturned(int keyCode)
    {
        processKey(keyCode);
    }
    public void keyReleasedEventReturned(int keyCode)
    {
    }
    public void keyRepeatedEventReturned(int keyCode)
    {
    }
    public void keyReleasedEvent(int keyCode)
    {
    }
    public void keyRepeatedEvent(int keyCode)
    {
        keyPressedEvent(keyCode);
    }
    public void pointerPressedEventReturned(int x,int y)
    {
    	
    }
    public void pointerPressedEvent(int x,int y)
    { 
    }
    public void pointerReleasedEvent(int x,int y)
    {
    	if(activeSubmenu!=null)
        {
            activeSubmenu.pointerReleasedEvent(x, y);
        }
        else
        {
            pointerReleasedEventReturned(x, y);
        }
    }
    public void pointerDraggedEvent(int x,int y)
    {
    }
    public void pointerDraggedEventReturned(int x,int y)
    {
    }
    public void pointerReleasedEventReturned(int x,int y)
    {
    	if(currentView.contains(x, y, 0)){
    		if(scrolls!=null && scrolls.length==2 && scroll){
    			if(scrolls[0]!=null && scrolls[0].getDisplayRect().contains(x, y, 0)){
        			keyPressedEvent(
        					GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(Canvas.UP));
        			return;
        		}
        		else if(scrolls[1]!=null && scrolls[1].getDisplayRect().contains(x, y, 0)){
        			keyPressedEvent(
        					GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(Canvas.DOWN));
        			return;
        		}
    		}
    		currentItem.focusLost();
    		for(int i=topIndex;i>=0;i--){
    			MenuItem item=(MenuItem)entries.elementAt(i);
    			if(item.getDisplayRect().contains(x, y, 0)){
    				currentItem=item;
    				currentItem.focusGained();
    				int itemIndex=entries.indexOf(currentItem);
    				scrIndex=topIndex-itemIndex;
    				keyPressedEvent(
        					GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
    				return;
    			}
    		}
    	}
    	else{
    		focusLost();
    		displaying=false;
    		if(parent!=null && parent instanceof Menu){
    			((Menu)parent).activeSubmenu=null;
    		}
    		repaint(GlobalControl.getControl().getDisplayArea());
    	}
    }
    public Rectangle getDisplayRect()
    {
        return displayRect;
    }
    public void repaint(Rectangle clip)
    {
        GlobalControl.getControl().repaint(clip);
    }
    private void showSubMenu(MenuItem it)
    {
        Menu men=(Menu)mappings.get(it);
        if(men!=null)
        {
            //Set the display rect for the menu
            //Set the display rects for all added submenus
            //The width to be used for this sub menu
            //set the menus span rect
            int widSub=displayRect.width;
            int subY=displayRect.y;
            Rectangle menuRect=new Rectangle();
            if(alignment==ALIGN_RIGHT)
            {
                menuRect.x=displayRect.x-widSub;
            }
            else
            {
                menuRect.x=displayRect.x+displayRect.width;
            }
            menuRect.width=displayRect.width;
            subY=it.getDisplayRect().y;
            if(subY>spanRect.y+spanRect.height/2)
            {
                menuRect.y=spanRect.y;
                men.setStartPoint(START_BOTTOM);
            }
            else
            {
                menuRect.y=subY-1;
                menuRect.height=(spanRect.height+spanRect.y)-subY;
                men.setStartPoint(START_TOP);
            }
            men.setDrawStart(it.getDisplayRect().y+it.getDisplayRect().height);
            men.setDisplayRect(menuRect);
            activeSubmenu=men;
            men.focusGained();
            repaint(currentView);
        }
    }
    public void paint(Graphics g,Rectangle clip)
    {
    	Rectangle intersect=null;
    	//draw the menu bar rect if required
    	if(this.parent instanceof Window)
        {
    		if((intersect=clip.calculateIntersection(((Window)this.parent).getMenuBarRect()))!=null)
    		{
    			//acquire the menu bar rect
    			Rectangle barRect=((Window)this.parent).getMenuBarRect();
    			g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
    			g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
    					Style.MENU_BAR_FOREGROUND)).intValue());
    			g.setFont((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_BAR_FONT));
    			if(!isDisplaying())
    			{
    				//draw the left item if any is available
        			if(leftItem!=null)
        			{
        				g.setClip(barRect.x, barRect.y, barRect.width/2, barRect.height);
        				g.drawString(leftItem.getName(), barRect.x+1, barRect.y+1, Graphics.TOP|Graphics.LEFT);
        			}
        			if(rightItem!=null)
        			{
        				int wid=((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_BAR_FONT))
        				.stringWidth(rightItem.getName());
        				int diff=wid-barRect.width/2-3;
        				g.setClip(barRect.x+barRect.width/2+2, barRect.y, barRect.width/2, barRect.height);
        				if(diff>0)
        				{
        					g.drawString(rightItem.getName(), barRect.x+barRect.width/2+2, 
        							barRect.y+1, Graphics.TOP|Graphics.LEFT);
        				}
        				else
        				{
        					g.drawString(rightItem.getName(), barRect.x+barRect.width-2-wid, 
        							barRect.y+1, Graphics.TOP|Graphics.LEFT);
        				}
        			}
        			//if there is no right item and this menu has elements, we draw the
        			//menu string on the right
        			else if(!entries.isEmpty())
        			{
        				int wid=((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_BAR_FONT))
        				.stringWidth(this.name);
        				int diff=wid-barRect.width/2-3;
        				g.setClip(barRect.x+barRect.width/2+2, barRect.y, barRect.width/2, barRect.height);
        				if(diff>0)
        				{
        					g.drawString(this.name, barRect.x+barRect.width/2+2, 
        							barRect.y+1, Graphics.TOP|Graphics.LEFT);
        				}
        				else
        				{
        					g.drawString(this.name, barRect.x+barRect.width-2-wid, 
        							barRect.y+1, Graphics.TOP|Graphics.LEFT);
        				}
        			}
    			}
    			else
    			{
    				if(GlobalControl.getControl().getLayout()==GlobalControl.PORTRAIT_LAYOUT)
    				{
    					g.setClip(barRect.x, barRect.y, barRect.width/2, barRect.height);
        				g.drawString(LocaleManager.getTranslation("flemil.cancel"),
        						barRect.x+1, barRect.y+1, Graphics.TOP|Graphics.LEFT);
    				}
    				int wid=((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_BAR_FONT))
    				.stringWidth(LocaleManager.getTranslation("flemil.select"));
    				int diff=wid-barRect.width/2-3;
    				g.setClip(barRect.x+barRect.width/2+2, barRect.y, barRect.width/2, barRect.height);
    				if(diff>0)
    				{
    					g.drawString(LocaleManager.getTranslation("flemil.select"), barRect.x+barRect.width/2+2, 
    							barRect.y+1, Graphics.TOP|Graphics.LEFT);
    				}
    				else
    				{
    					g.drawString(LocaleManager.getTranslation("flemil.select"),
    							barRect.x+barRect.width-2-wid, 
    							barRect.y+1, Graphics.TOP|Graphics.LEFT);
    				}
    			}
    			if(GlobalControl.getControl().isShowTime()){
    				g.setClip(barRect.x, barRect.y, barRect.width, barRect.height);
    				Calendar cal=Calendar.getInstance();
    				StringBuffer timeString=new StringBuffer();
    				timeString.append(cal.get(Calendar.HOUR)==0?"12":""+cal.get(Calendar.HOUR));
    				timeString.append(":");
    				timeString.append(cal.get(Calendar.MINUTE)<10?("0"+cal.get(Calendar.MINUTE)):
    					(""+cal.get(Calendar.MINUTE)));
    				Font timeFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, 
    						Font.SIZE_SMALL);
    				int timeWidth=timeFont.stringWidth(timeString.toString());
    				g.setFont(timeFont);
    				g.setColor(((Integer)GlobalControl.getControl().getStyle().
    						getProperty(Style.MENU_BAR_FOREGROUND)).intValue());
    				g.drawString(timeString.toString(), barRect.width/2-timeWidth/2, 
    						barRect.y+barRect.height-timeFont.getHeight()-2,
    						Graphics.TOP|Graphics.LEFT);
    			}
    		}
    		if(GlobalControl.getControl().getLayout()==GlobalControl.LANDSCAPE_LAYOUT)
    		{
    			if(isDisplaying())
    			{
    				Rectangle cancItemRect=new Rectangle();
        			cancItemRect.x=GlobalControl.getControl().getDisplayArea().x; 
        			cancItemRect.y=GlobalControl.getControl().getDisplayArea().y; 
        			cancItemRect.width=GlobalControl.getControl().getDisplayArea().width;
        			cancItemRect.height=((Window)parent).getMenuBarRect().height;
            		if((intersect=clip.calculateIntersection(cancItemRect))!=null)
            		{
            			g.setClip(intersect.x,intersect.y, intersect.width, intersect.height);
            			int imgWidth=GlobalControl.getControl().getMenuBarBGround().getWidth();
            			for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
            			{
            				g.drawImage(GlobalControl.getControl().getMenuBarBGround(),
            						i, intersect.y, Graphics.TOP|Graphics.LEFT);
            			}
            			int wid=((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_BAR_FONT))
            			.stringWidth(LocaleManager.getTranslation("flemil.cancel"));
            			int diff=wid-cancItemRect.width-3;
            			if(diff>0)
            			{
            				g.drawString(LocaleManager.getTranslation("flemil.cancel"),
            						cancItemRect.x+2, 
            						cancItemRect.y+1, Graphics.TOP|Graphics.LEFT);
            			}
            			else
            			{
            				g.drawString(LocaleManager.getTranslation("flemil.cancel"),
            						cancItemRect.x+cancItemRect.width-2-wid, 
            						cancItemRect.y+1, Graphics.TOP|Graphics.LEFT);
            			}
            		}
    			}
    		}
    		g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
        //Draw the current menu appearance if displaying
        if(displaying)
        {
            if(entries.isEmpty())
            {
            	g.setClip(clip.x, clip.y, clip.width, clip.height);
                this.focusLost();
                return;
            }
            if((intersect=clip.calculateIntersection(currentView))!=null)
            {
            	//draw the outline
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.MENU_BACKGROUND)).intValue());
            	int radius=((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.CURVES_RADIUS)).intValue();
            	g.fillRoundRect(intersect.x, intersect.y, intersect.width-1, intersect.height,
        				radius, radius);
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		g.drawRoundRect(currentView.x, currentView.y, currentView.width-1, currentView.height-1,
        				radius, radius);
            	//draw the scrolls if they are visible
            	if(scroll)
            	{
            		scrolls[0].focusGained();
            		scrolls[1].focusGained();
            		scrolls[0].paint(g, clip);
            		scrolls[1].paint(g, clip);
            		
            	}
            	//draw any visible item
            	for(int i=topIndex;i>=0;i--)
            	{
            		((MenuItem)entries.elementAt(i)).paint(g, currentView);
            	}
            	if(activeSubmenu!=null && GlobalControl.getControl().isFading()
            			&& GlobalControl.getControl().getFadeImage()!=null)
            	{
            		int track=intersect.x+intersect.width;
            		int imgWidth=GlobalControl.getControl().getFadeImage().getWidth();
                    for(int i=displayRect.x-1;i<track+1;i+=imgWidth-1)
                    {
                    	g.drawRegion(GlobalControl.getControl().getFadeImage(),
                        		0,0,imgWidth-1, 
                        		GlobalControl.getControl().getFadeImage().getHeight(),
                        		Sprite.TRANS_NONE, 
                        		i, intersect.y,  Graphics.TOP|Graphics.LEFT);
                    }
            	}
            	g.setClip(clip.x, clip.y, clip.width, clip.height);
            }
            //Draw a pop up menu if any is active
            if(activeSubmenu!=null)
            {
                activeSubmenu.paint(g,clip);
            }
        }
        
    }
    public Rectangle getMinimumDisplayRect(int availWidth)
    {
        Rectangle rect=new Rectangle();
        int minWid=rect.width;
        int minHeight=entries.size()>0?
            ((MenuItem)entries.elementAt(0)).getMinimumDisplayRect(displayRect.width).height:
            rect.height;
        rect.width=minWid>displayRect.width?minWid:displayRect.width;
        rect.height=minHeight*2;
        return rect;
    }
    public void setDisplayRect(Rectangle rect)
    {
    	if(rect.width<=1){
    		return;
    	}
        displayRect=rect;
        if(entries.isEmpty())
        {
            return;
        }
        if(activeSubmenu!=null)
        {
            activeSubmenu.focusLost();
            activeSubmenu=null;
        }
        if(displaying)
        {
            focusLost();
        }
        organizeItems();
        scrollToTop();
        resetItemRects();
    }
    public void focusLost()
    {
    	if(activeSubmenu!=null)
        {
            activeSubmenu.focusLost();
            activeSubmenu=null;
        }
        if(currentItem!=null)
        {
            currentItem.focusLost();
        }
        displaying=false;
        if(parent instanceof Window)
		{
			repaint(((Window)parent).getMenuBarRect());
		}
        repaint(currentView);
        if(GlobalControl.getControl().getLayout()==GlobalControl.LANDSCAPE_LAYOUT
        		&& parent instanceof Window)
        {
        	Rectangle cancItemRect=new Rectangle();
        	cancItemRect.x=GlobalControl.getControl().getDisplayArea().x; 
        	cancItemRect.y=GlobalControl.getControl().getDisplayArea().y; 
        	cancItemRect.width=GlobalControl.getControl().getDisplayArea().width;
        	cancItemRect.height=((Window)parent).getMenuBarRect().height;
        	repaint(cancItemRect);
        }
    }
    public void focusGained()
    {
        if(entries.isEmpty())
        {
        	return;
        }
        displaying=true;
        if(currentItem!=null)
        {
            currentItem.focusGained();
        }
        repaint(currentView);
        if(menuListener!=null){
    		menuListener.highlightChanged(this, currentItem);
    	}
    }
    private void processKey(int keyCode)
    {
    	if(keyCode==GlobalControl.getSoftKeys()[1])//this is the right soft key
    	{	
    		if(entries.isEmpty()){
    			if(rightItem!=null){
    				if(rightItem!=null && rightItem.getListener()!=null)
        			{
        				rightItem.getListener().commandAction(rightItem);
        			}
    			}
    			return;
    		}
    		if(!displaying)
    		{
    			focusGained();
    			if(rightItem!=null && rightItem.getName()
    					!=LocaleManager.getTranslation("flemil.options"))
    			{
    				keyPressedEvent(GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(
        					Canvas.FIRE));
    			}
    		}
    		else
    		{
    			keyPressedEvent(GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(
    					Canvas.FIRE));
    		}
    		if(parent instanceof Window)
    		{
    			repaint(((Window)parent).getMenuBarRect());
    			repaint(currentView);
    			if(GlobalControl.getControl().getLayout()==GlobalControl.LANDSCAPE_LAYOUT)
    			{
    					Rectangle cancItemRect=new Rectangle();
    					cancItemRect.x=GlobalControl.getControl().getDisplayArea().x; 
    					cancItemRect.y=GlobalControl.getControl().getDisplayArea().y; 
    					cancItemRect.width=GlobalControl.getControl().getDisplayArea().width;
    					cancItemRect.height=((Window)parent).getMenuBarRect().height;
    					repaint(cancItemRect);
    			}
    		}
    		return;
    	}
    	else if(keyCode==GlobalControl.getSoftKeys()[0])//this is the left soft key
    	{
    		if(isDisplaying())
    		{
    			focusLost();
    			if(parent instanceof Menu)
    			{
    				((Menu)parent).activeSubmenu=null;
    				repaint(parent.getDisplayRect());
    			}
    			if(parent instanceof Window)
        		{
        			repaint(((Window)parent).getMenuBarRect());
        			repaint(currentView);
        			if(GlobalControl.getControl().getLayout()==GlobalControl.LANDSCAPE_LAYOUT)
        			{
        					Rectangle cancItemRect=new Rectangle();
        					cancItemRect.x=GlobalControl.getControl().getDisplayArea().x; 
        					cancItemRect.y=GlobalControl.getControl().getDisplayArea().y; 
        					cancItemRect.width=GlobalControl.getControl().getDisplayArea().width;
        					cancItemRect.height=((Window)parent).getMenuBarRect().height;
        					repaint(cancItemRect);
        			}
        		}
    			return;
    		}
    		else
    		{
    			if(leftItem!=null && leftItem.getListener()!=null)
    			{
    				leftItem.getListener().commandAction(leftItem);
    			}
    			return;
    		}
    	}
        int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.DOWN:
            {
            	scrollItems(Menu.UPSCROLL);
            	if(menuListener!=null){
            		menuListener.highlightChanged(this, currentItem);
            	}
                break;
            }
            case Canvas.UP:
            {
            	scrollItems(Menu.DOWNSCROLL);
            	if(menuListener!=null){
            		menuListener.highlightChanged(this, currentItem);
            	}
            	break;
            }
            case Canvas.LEFT:
                if(!entries.isEmpty())
                {
                    if(currentItem.getType()==MenuItem.TYPE_POPUP_ITEM)
                    {
                        if(activeSubmenu==null && alignment==ALIGN_RIGHT)
                        {
                            showSubMenu(currentItem);
                        }
                        //resolve ambiguity for arrowed items
                        else if(activeSubmenu==null && alignment==ALIGN_LEFT
                                &&parent!=null && parent instanceof Menu)
                        {
                            focusLost();
                            ((Menu)parent).activeSubmenu=null;
                            repaint(((Menu)parent).currentView);
                        }
                        else if(activeSubmenu!=null && alignment==ALIGN_LEFT)
                        {
                            activeSubmenu.keyPressedEvent(keyCode);
                        }
                    }
                    else if(alignment==ALIGN_RIGHT && parent!=null && 
                            parent instanceof Menu)
                    {
                        focusLost();
                        ((Menu)parent).activeSubmenu=null;
                        repaint(((Menu)parent).currentView);
                    }
                }
                break;
            case Canvas.RIGHT:
                if(!entries.isEmpty())
                {
                    MenuItem it=currentItem;
                    if(it.getType()==MenuItem.TYPE_POPUP_ITEM)
                    {
                        if(activeSubmenu==null && alignment==ALIGN_LEFT)
                        {
                            showSubMenu(it);
                        }
                        //resolve ambiguity for arrowed items
                        else if(activeSubmenu==null && alignment==ALIGN_RIGHT
                                &&parent!=null && parent instanceof Menu)
                        {
                            focusLost();
                            ((Menu)parent).activeSubmenu=null;
                            repaint(((Menu)parent).currentView);
                        }
                        else if(activeSubmenu!=null && alignment==ALIGN_RIGHT)
                        {
                            activeSubmenu.keyPressedEvent(keyCode);
                        }
                        else if(alignment==ALIGN_RIGHT && parent==null)
                        {
                            focusLost();
                        }
                    }
                    else if(alignment==ALIGN_LEFT && parent!=null
                            && parent instanceof Menu)
                    {
                        focusLost();
                        ((Menu)parent).activeSubmenu=null;
                        repaint(((Menu)parent).currentView);
                    }
                    else if(alignment==ALIGN_RIGHT && parent==null)
                    {
                        focusLost();
                    }
                }
                break;
            case Canvas.FIRE:
                if(currentItem.getType()==MenuItem.TYPE_POPUP_ITEM)
                {
                    if(activeSubmenu==null)
                    {
                        showSubMenu(currentItem);
                    }
                    else
                    {
                        activeSubmenu.focusLost();
                        activeSubmenu=null;
                    }
                    break;
                }
                else
                {
                    itemSelected();
                }
                break;
        }
    }
	public void setMenuListener(MenuListener menuListener) {
		this.menuListener = menuListener;
	}
	public MenuListener getMenuListener() {
		return menuListener;
	}
	public boolean isFocussed() {
		return displaying;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		for(int i=0;i<entries.size();i++){
			((Item)entries.elementAt(i)).moveRect(dx, dy);
		}
	}
}
