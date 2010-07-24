package org.flemil.ui.component;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.MenuCommandListener;
import org.flemil.ui.Item;
import org.flemil.util.Rectangle;




/**
 * Class that represents a MenuItem that can be added to a menu and selectible
 *  from the menu.
 * @author Solomon Kariri
 */
public class MenuItem implements Item
{
    static final byte TYPE_SEPARATOR=4;
    static final byte TYPE_POPUP_ITEM=3;
    static final byte TYPE_DOWN_SCROLL_ITEM=2;
    static final byte TYPE_UP_SCROLL_ITEM=1;
    static final byte TYPE_DEFAULT_ITEM=0;
    //The rect available for this menus display
    private Rectangle displayRect;
    //The parent Menu to this item
    private Item parent;
    //The name of this MenuItem
    private String name;
    //Variable to keep track of whether this item is focussed/highlighted
    private boolean focussed;
    //variable to keep track of whether this item already has a running scrolling thread
    private boolean scrolling;
    //The command listener for this item
    private MenuCommandListener listener;
    //byte to tell alignment of this item
    private byte alignment=Menu.ALIGN_RIGHT;
    //the length in pixels of the name
    private int nameWidth=1;
    //the variable to keep track of where the titledrawing will start
    private int nameIndent;
    //variable to denote the type for this item
    private byte type;
    private boolean paintBorder=true;
    private boolean focusible=true;

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
	MenuItem(String name,byte type)
    {
        this.name=name;
        this.type=type;
        nameWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).stringWidth(name);
        //Initialize display rect
        displayRect=new Rectangle();
    }
	/**
	 * Creates a new MenuItem with the given name
	 * @param name the name of the MenuItem being constructed
	 */
    public MenuItem(String name)
    {
        this(name, MenuItem.TYPE_DEFAULT_ITEM);
    }
    /**
     * Returns the class that has been registered as 
     * the listener for selection events on this MenuItem.
     *  The class must implement the  MenuCommandListener interface
     * @return the class registered as the listener for selection
     *  events on this MenuItem
     */
    public MenuCommandListener getListener()
    {
        return listener;
    }
    /**
     * Sets the class that is going to be notified if selection events occur
     *  on this MenuItem
     * @param listener the class whose callback method is invoked when a 
     * selection event happens on this MenuItem
     */
    public void setListener(MenuCommandListener listener)
    {
        this.listener=listener;
    }
    byte getType()
    {
        return this.type;
    }
    /**
     * Sets the name of this MenuItem
     * @param name the name for this MenuItem
     */
    public void setName(String name)
    {
        this.name=name;
        nameWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).stringWidth(name);
        if(nameWidth<displayRect.width)
        {
            scrolling=false;
            return;
        }
        if(!scrolling)
        {
            nameWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).stringWidth(name);
            new Thread(new TitleScroller()).start();
        }
    }
    public void repaint(Rectangle clip)
    {
        if(parent!=null)
        {
            this.parent.repaint(clip);
        }
    }
    public void paint(Graphics g,Rectangle clip)
    {
    	Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            //if its focussed, then fill with the bg image
            if(focussed && type!=MenuItem.TYPE_UP_SCROLL_ITEM && type!=MenuItem.TYPE_DOWN_SCROLL_ITEM)
            {
                //set the clip first
                g.setClip(displayRect.x, displayRect.y, displayRect.width, displayRect.height);
                //fill the background
                int track=displayRect.x+displayRect.width;
                int imgWidth=GlobalControl.getControl().getMenuItemBGround().getWidth();
                g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.MENU_BACKGROUND)).intValue());
                for(int i=displayRect.x-1;i<track+1;i+=imgWidth-1)
                {
                	g.fillRect(i, intersect.y, imgWidth, intersect.height);
                    g.drawImage(GlobalControl.getControl().getMenuItemBGround(), i,
                            displayRect.y, Graphics.TOP|Graphics.LEFT);
                }
                g.setClip(clip.x, clip.y, clip.width, clip.height);
            }
            //initialize arrow image depending on type
            switch (type)
            {
                case MenuItem.TYPE_POPUP_ITEM:
                {
                    //set the clip as necessary and draw the string
                    if(alignment==Menu.ALIGN_LEFT)
                    {
                        g.setClip(displayRect.x, displayRect.y, 
                                displayRect.width-GlobalControl.getControl().getMenuPopImage().getWidth(),
                                displayRect.height);
                        if(focussed)
                        {
                            g.setColor(((Integer)GlobalControl.getControl().getStyle().
                                getProperty(Style.MENU_HIGHLIGHT_FOREGROUND)).intValue());
                        }
                        else
                        {
                            g.setColor(((Integer)GlobalControl.getControl().getStyle().
                                getProperty(Style.MENU_ITEM_FOREGROUND)).intValue());
                        }
                        g.setFont((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_ITEM_FONT));
                        g.drawString(name, displayRect.x+nameIndent, displayRect.y+1,
                                Graphics.TOP|Graphics.LEFT);
                        g.setClip(displayRect.x, displayRect.y, 
                                displayRect.width, displayRect.height);
                        g.drawRegion(GlobalControl.getControl().getMenuPopImage(), 
                        		0, 0, GlobalControl.getControl().getMenuPopImage().getWidth(), 
                        		GlobalControl.getControl().getMenuPopImage().getHeight(), 
                                Sprite.TRANS_MIRROR, displayRect.x+displayRect.width-
                                	GlobalControl.getControl().getMenuPopImage().getWidth(),
                                displayRect.y+displayRect.height/2-
                                	GlobalControl.getControl().getMenuPopImage().getHeight()/2,Graphics.TOP|Graphics.LEFT);
                    }
                    else
                    {
                        g.setClip(displayRect.x+
                        		GlobalControl.getControl().getMenuPopImage().getWidth(), displayRect.y,
                                displayRect.width-
                                	GlobalControl.getControl().getMenuPopImage().getWidth(), displayRect.height);
                        if(focussed)
                        {
                            g.setColor(((Integer)GlobalControl.getControl().getStyle().
                                getProperty(Style.MENU_HIGHLIGHT_FOREGROUND)).intValue());
                        }
                        else
                        {
                            g.setColor(((Integer)GlobalControl.getControl().getStyle().
                                getProperty(Style.MENU_ITEM_FOREGROUND)).intValue());
                        }
                        g.setFont((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_ITEM_FONT));
                        g.drawString(name, displayRect.x+nameIndent+
                        		GlobalControl.getControl().getMenuPopImage().getWidth(),
                                displayRect.y+1,
                                Graphics.TOP|Graphics.LEFT);
                        g.setClip(displayRect.x, displayRect.y, 
                                displayRect.width, displayRect.height);
                        g.drawImage(GlobalControl.getControl().getMenuPopImage(), 
                        		displayRect.x+1, displayRect.y+displayRect.height/2-
                        			GlobalControl.getControl().getMenuPopImage().getHeight()/2,
                                Graphics.TOP|Graphics.LEFT);
                        
                    }
                    g.setClip(clip.x, clip.y, clip.width, clip.height);
                    //draw the popup image
                    break;
                }
                case MenuItem.TYPE_DOWN_SCROLL_ITEM:
                {
                    //draw the scroll image if focussed
                    if(focussed)
                    {
                        g.drawRegion(GlobalControl.getControl().getMenuPopImage(),
                        		0,0,GlobalControl.getControl().getMenuPopImage().getWidth(),
                        		GlobalControl.getControl().getMenuPopImage().getHeight(),
                        		Sprite.TRANS_ROT270,
                                displayRect.x+displayRect.width/2-
                                	GlobalControl.getControl().getMenuPopImage().getHeight()/2,
                                displayRect.y+displayRect.height, Graphics.BOTTOM|Graphics.LEFT);
                    }
                    break;
                }
                case MenuItem.TYPE_UP_SCROLL_ITEM:
                {
                    //draw the scroll image if focussed
                    if(focussed)
                    {
                        g.drawRegion(GlobalControl.getControl().getMenuPopImage(),
                        		0,0,GlobalControl.getControl().getMenuPopImage().getWidth(),
                        		GlobalControl.getControl().getMenuPopImage().getHeight(),
                        		Sprite.TRANS_ROT90,
                        		displayRect.x+displayRect.width/2-
                            		GlobalControl.getControl().getMenuPopImage().getHeight()/2,
                            	displayRect.y, Graphics.TOP|Graphics.LEFT);
                    }
                    break;
                }
                default:
                {
                	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
                    if(focussed)
                    {
                        g.setColor(((Integer)GlobalControl.getControl().getStyle().
                            getProperty(Style.MENU_HIGHLIGHT_FOREGROUND)).intValue());
                    }
                    else
                    {
                        g.setColor(((Integer)GlobalControl.getControl().getStyle().
                            getProperty(Style.MENU_ITEM_FOREGROUND)).intValue());
                    }
                    g.setFont((Font)GlobalControl.getControl().getStyle().getProperty(Style.MENU_ITEM_FONT));
                    g.drawString(name, displayRect.x+nameIndent,
                            displayRect.y+1,
                            Graphics.TOP|Graphics.LEFT);
                    g.setClip(clip.x, clip.y, clip.width, clip.height);
                }
            }
        }
    }
    public Rectangle getMinimumDisplayRect(int availWidth)
    {
        Rectangle minRect=new Rectangle();
        minRect.height=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).getHeight()+2;
        minRect.width=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).stringWidth(name)+2;
        return minRect;
    }
    public void setDisplayRect(Rectangle rect)
    {
    	displayRect=rect;
    }
    public Rectangle getDisplayRect()
    {
        return displayRect;
    }
    public void focusLost()
    {
        focussed=false;
        repaint(displayRect);
    }
    public void focusGained()
    {
        focussed=true;
        nameWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.MENU_ITEM_FONT)).stringWidth(name);
        int diff=nameWidth-displayRect.width;
        if(type==MenuItem.TYPE_POPUP_ITEM)
        {
            diff+=GlobalControl.getControl().getMenuPopImage().getWidth();
        }
        if(diff>0)
        {
            new Thread(new TitleScroller()).start();
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
    void setAlignment(byte align){this.alignment=align;}
    /**
     * Returns the name of this MenuItem
     * @return the name of this MenuItem
     */
    public String getName(){return name;}
    public void keyPressedEvent(int keyCode){}
    public void keyReleasedEvent(int keyCode){}
    public void keyPressedEventReturned(int keyCode){}
    public void keyReleasedEventReturned(int keyCode){}
    public void keyRepeatedEvent(int keyCode){}
    public void keyRepeatedEventReturned(int keyCode){}
    public void pointerPressedEventReturned(int x,int y){}
    public void pointerReleasedEventReturned(int x,int y){}
    public void pointerDraggedEventReturned(int x,int y){}
    public void pointerPressedEvent(int x,int y){}
    public void pointerReleasedEvent(int x,int y){}
    public void pointerDraggedEvent(int x,int y){}
    //The class that is responsible for title scrolling if its too long
    private class TitleScroller implements Runnable
    {
        public void run()
        {
            if(scrolling || displayRect.width<=1)
            {
                return;
            }
            //for as long as this window is focussed
            scrolling=true;
            //the variable for the increment
            int increment=-GlobalControl.getTextScrollSpeed();
            while(focussed)
            {
                //calculate the between name and available display area
                int diff=nameWidth-displayRect.width;
                if(type==MenuItem.TYPE_POPUP_ITEM)
                {
                    diff+=GlobalControl.getControl().getMenuPopImage().getWidth();
                }
                try
                {
                    Thread.sleep(100);
                }catch(InterruptedException ie){}
                if(nameIndent<-diff || nameIndent>=0)
                {
                	repaint(MenuItem.this.getDisplayRect());
                	try{Thread.sleep(900);}catch(InterruptedException ie){}
                	nameIndent=0;
                	repaint(MenuItem.this.getDisplayRect());
                	try{Thread.sleep(900);}catch(InterruptedException ie){}
                }
                nameIndent+=increment;
                repaint(MenuItem.this.getDisplayRect());
            }
            nameIndent=0;
            scrolling=false;
        }
    }
	public boolean isFocussed() {
		return focussed;
	}
}