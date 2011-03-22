package org.flemil.ui.component;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.Window;
import org.flemil.util.Rectangle;




/**
 * Class that represent a Main application window that spans the whole display
 *  area available to the application
 * @author Solomon Kariri
 */
public class ScreenWindow implements Window 
{
    //The defaultPanel for this WindowImpl
    private Panel contentPane;
    public Panel getContentPane() {
		return contentPane;
	}
	//Flag for full screen display of this item
    private boolean fullScreen;
    //This windows title
    private String title;
    //The available display rectangle
    private Rectangle displayRect;
    //Variable to show whether this window is focused
    private boolean focussed;
    //variable to keep track of whether title scrolling is taking place
    private boolean scrolling;
    //the menu for this window
    private Menu menu;
    //the length in pixels of the title
    private int titleWidth=1;
    //the variable to keep track of where the title drawing will start
    private int titleIndent;
    //the rectangle to be used as the menu bar rect
    private Rectangle menubarRect;
    //the rectangle to be used as the title bar ect
    private Rectangle titlebarRect;
    //variable to keep track of title changes
    private transient boolean titleChanged;
    //the rectangle to be used as the body of the window
    private Rectangle bodyRect;
    //variable to keep track of whether menu is displaying or not
    private boolean isMenuDisplaying;
    private boolean paintBorder=true;
    private boolean focusible=true;
	private Menu currentMenu;
	private Vector popups;
	private PopUpWindow currentPopUp;
	private Rectangle menuArrowRect;
	private Image arrowImage;
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
     * Creates a new WindowImpl with the given title.
     * @param title the title of this WindowImpl
     */
    public ScreenWindow(String title)
    {
        this.title=title;
        menubarRect=new Rectangle();
        titlebarRect=new Rectangle();
        bodyRect=new Rectangle();
        menuArrowRect=new Rectangle();
      //Initialize display rect
        displayRect=new Rectangle();
        titleWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.WINDOW_TITLE_FONT)).stringWidth(title);
        //Set the default panel for this window
        this.contentPane=new Panel();
        //initialize the windows menu
        menu=new Menu(LocaleManager.getTranslation("flemil.options"));
        currentMenu=menu;
        menu.setParent(this);
        popups=new Vector();
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
        contentPane.keyReleasedEvent(keyCode);
    }
    public void keyRepeatedEventReturned(int keyCode)
    {
    }
    public void keyRepeatedEvent(int keyCode)
    {
        contentPane.keyRepeatedEvent(keyCode);
    }
    public void keyPressedEventReturned(int keyCode)
    {
    }
    public void keyPressedEvent(int keyCode)
    {
    	if(keyCode==GlobalControl.getSoftKeys()[1] || keyCode==GlobalControl.getSoftKeys()[0])//this is the right soft key
    	{	
    		if(fullScreen)
    		{
    			setFullScreenMode(false);
    		}
    		else
    		{
    			this.currentMenu.keyPressedEvent(keyCode);
    		}
    	}
    	else if(this.currentMenu.isDisplaying())
    	{
    		this.currentMenu.keyPressedEvent(keyCode);
    	}
    	else if(this.currentPopUp!=null)
    	{
    		this.currentPopUp.keyPressedEvent(keyCode);
    	}
    	else
    	{
    		contentPane.keyPressedEvent(keyCode);
    	}
    }
    public void pointerPressedEventReturned(int x,int y)
    {
        
    }
    public void pointerReleasedEventReturned(int x,int y)
    {
    }
    public void pointerDraggedEventReturned(int x,int y)
    {
        
    }
    public void pointerPressedEvent(int x,int y)
    {
    	if(currentPopUp!=null){
    		currentPopUp.pointerPressedEvent(x, y);
    	}
    	else{
    		contentPane.pointerPressedEvent(x, y);
    	}
    }
    public void pointerReleasedEvent(int x,int y)
    {
    	if(currentPopUp!=null && currentPopUp.getDisplayRect().contains(x, y, 0)
    			&& !currentMenu.isDisplaying()){
    		currentPopUp.pointerReleasedEvent(x, y);
    	}
    	else{
    		if(!fullScreen && getMenuBarRect().contains(x, y, 0)){
        		Rectangle testLeft=new Rectangle(menubarRect.x, menubarRect.y, 
        				menubarRect.width/2, menubarRect.height);
        		if(testLeft.contains(x, y, 0)){
        			keyPressedEvent(GlobalControl.getSoftKeys()[0]);
        		}
        		else{
        			keyPressedEvent(GlobalControl.getSoftKeys()[1]);
        		}
        	}
        	else{
        		if(currentMenu.isDisplaying()){
        			currentMenu.pointerReleasedEvent(x, y);
        		}
        		else{
        			if(currentPopUp!=null){
        				currentPopUp.pointerReleasedEvent(x, y);
        			}
        			else{
        				if(fullScreen && menuArrowRect.contains(x,y,0)){
            				keyPressedEvent(GlobalControl.getSoftKeys()[1]);
            			}
            			else{
            				contentPane.pointerReleasedEvent(x, y);
            			}
        			}
        		}
        	}
    	}
    }
    public void pointerDraggedEvent(int x,int y)
    {
    	if(currentMenu.isDisplaying())return;
    	if(currentPopUp!=null){
    		currentPopUp.pointerDraggedEvent(x, y);
    	}
    	else{
    		contentPane.pointerDraggedEvent(x, y);
    	}
    }
    public void repaint(Rectangle clip)
    {
    	Rectangle intersect=getDisplayRect().calculateIntersection(clip);
    	if(intersect!=null){
    		GlobalControl.getControl().repaint(intersect);
    	}
    }
    public Menu getMenu()
    {
        return menu;
    }
    public Menu getCurrentMenu()
    {
    	return currentMenu;
    }
    public void paint(Graphics g,Rectangle clip)
    {
		//The window draws the section of the graphics passed to it that 
        //corresponds to its current available display rect
        //set the clip before doing anything else
        g.setClip(clip.x,clip.y,clip.width,clip.height);
        Rectangle intersect=null;
        //Draw the title bar
        //Draw the title background Image first. Drawing happens only if intersected
        int curRadius=((Integer)GlobalControl.getControl().getStyle().
        		getProperty(Style.CURVES_RADIUS)).intValue();
        Rectangle edgeRect=new Rectangle(displayRect.x,displayRect.y,curRadius,displayRect.height);
        g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
                Style.THEME_BACKGROUND)).intValue());
        g.fillRoundRect(displayRect.x, displayRect.y, displayRect.width, displayRect.height,
				curRadius,curRadius);
        if((intersect=clip.calculateIntersection(edgeRect))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.drawImage(GlobalControl.getControl().getThemeEdgeForeground(),
                    displayRect.x, displayRect.y, Graphics.TOP|Graphics.LEFT);
        }
        edgeRect=new Rectangle(displayRect.x+displayRect.width-curRadius,displayRect.y,
        		curRadius,displayRect.height);
        if((intersect=clip.calculateIntersection(edgeRect))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.drawRegion(GlobalControl.getControl().getThemeEdgeForeground(),
                    0,0,edgeRect.width,edgeRect.height,Sprite.TRANS_MIRROR,
                    edgeRect.x, edgeRect.y, Graphics.TOP|Graphics.LEFT);
        }
        edgeRect=new Rectangle(displayRect.x+curRadius,displayRect.y,
        		displayRect.width-(curRadius*2),displayRect.height);
        if((intersect=clip.calculateIntersection(edgeRect))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	int imgWidth=GlobalControl.getControl().getThemeForeground().getWidth();
            for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
            {
            	g.fillRect(i, intersect.y, imgWidth, intersect.height);
                g.drawRegion(GlobalControl.getControl().getThemeForeground(),
                        0,intersect.y, imgWidth, intersect.height,Sprite.TRANS_NONE,
                        i,intersect.y, Graphics.TOP|Graphics.LEFT);
            }
        }
        if(!fullScreen)
        {
        	if((intersect=clip.calculateIntersection(titlebarRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            	int imgWidth=GlobalControl.getControl().getTitleBGround().getWidth();
            	for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
            	{
            		g.fillRect(i, intersect.y, imgWidth, intersect.height);
            		g.drawImage(GlobalControl.getControl().getTitleBGround(),
            				i, titlebarRect.y, Graphics.TOP|Graphics.LEFT);
            	}
            	g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(Style.TITLE_FOREGROUND)).
                        intValue());
                g.setFont((Font)GlobalControl.getControl().getStyle().getProperty(
                        Style.WINDOW_TITLE_FONT));
                if(scrolling)
                {
                	if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
                		g.drawString(title, titlebarRect.x+titleIndent+2,
                                titlebarRect.y+1,
                                Graphics.TOP|Graphics.LEFT);
                	}
                	else{
                		g.drawString(title, titlebarRect.x+titlebarRect.width-2-titleIndent-titleWidth,
                                titlebarRect.y+1,
                                Graphics.TOP|Graphics.LEFT);
                	}
                }
                else
                {
                	g.drawString(title, titlebarRect.x+(titlebarRect.width-titleWidth)/2,
                            titlebarRect.y+1,
                            Graphics.TOP|Graphics.LEFT);
                }
            }
            if((intersect=clip.calculateIntersection(menubarRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            	int imgWidth=GlobalControl.getControl().getMenuBarBGround().getWidth();
            	for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
            	{
            		g.fillRect(i, intersect.y, imgWidth, intersect.height);
            		g.drawImage(GlobalControl.getControl().getMenuBarBGround(),
            				i, menubarRect.y, Graphics.TOP|Graphics.LEFT);
            	}
            }
        }
        g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(Style.COMPONENT_OUTLINE_COLOR)).
                intValue());
        g.drawRect(displayRect.x, displayRect.y, 
        		displayRect.width-1, displayRect.height-1);
        contentPane.paint(g,clip);
        if(currentPopUp!=null)
        {
        	Rectangle intTest=new Rectangle(displayRect.x, displayRect.y, 
        			displayRect.width, titlebarRect.height+bodyRect.height);
        	if((intersect=clip.calculateIntersection(intTest))!=null &&
        			GlobalControl.getControl().isFading())
        	{
        		g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        		int imgWidth=GlobalControl.getControl().getFadeImage().getWidth();
                for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
                {
                    g.drawRegion(GlobalControl.getControl().getFadeImage(),
                    		0,0,imgWidth-1, 
                    		GlobalControl.getControl().getFadeImage().getHeight(),
                    		Sprite.TRANS_NONE, 
                    		i, intersect.y,  Graphics.TOP|Graphics.LEFT);
                }
        	}
        	currentPopUp.paint(g, clip);
        }
        if(!fullScreen && currentMenu!=null)
        {
        	currentMenu.paint(g, clip);
        }
        if(fullScreen){
        	if(menuArrowRect.calculateIntersection(clip)!=null){
        		g.drawImage(arrowImage, menuArrowRect.x, menuArrowRect.y,
        				Graphics.TOP|Graphics.LEFT);
        	}
        }
        g.setClip(clip.x, clip.y, clip.width, clip.height);
    }
    public Rectangle getMinimumDisplayRect(int availWidth)
    {
        Rectangle minRect=contentPane.getMinimumDisplayRect(availWidth);
        int minWid=minRect.width;
        int minHeight=minRect.height;
        //perform additional calculations specific to this window
        Rectangle rect=new Rectangle();
        rect.width=minWid;
        rect.height=minHeight;
        return rect;
    }
    public void setDisplayRect(Rectangle rect)
    {
    	synchronized (this) {
    		displayRect=rect;
        	if(rect.width<=2){
        		contentPane.setParent(null);
        		for(int i=0;i<popups.size();i++){
        			((PopUpWindow)popups.elementAt(i)).setParent(null);
        		}
        		return;
        	}
    		contentPane.setParent(this);
    		for(int i=0;i<popups.size();i++){
    			((PopUpWindow)popups.elementAt(i)).setParent(this);
    		}
        	//set the three rects respectively
            //title bar rect
            titlebarRect=new Rectangle();
            titlebarRect.x=this.displayRect.x;
            titlebarRect.y=this.displayRect.y;
            titlebarRect.width=this.displayRect.width;
            titlebarRect.height=GlobalControl.getControl().getTitleBGround().getHeight();
            //menu bar rect
            menubarRect=new Rectangle();
            menubarRect.x=this.displayRect.x;
            menubarRect.width=this.displayRect.width;
            menubarRect.y=displayRect.height+displayRect.y-
            	GlobalControl.getControl().getMenuBarBGround().getHeight();
            menubarRect.height=GlobalControl.getControl().getMenuBarBGround().getHeight();
            //body area rect
            bodyRect=new Rectangle();
            bodyRect.x=this.displayRect.x;
            bodyRect.width=this.displayRect.width;
            if(fullScreen)
            {
            	bodyRect.y=displayRect.y;
            	bodyRect.height=displayRect.height;
            }
            else
            {
                bodyRect.y=titlebarRect.y+titlebarRect.height;
                bodyRect.height=this.displayRect.height-
                	(titlebarRect.height+menubarRect.height);
            }
            setMenuRect(currentMenu);
            contentPane.setDisplayRect(bodyRect);
            
          //set the display rect for any popup showing
            if(currentPopUp!=null)
            {
            	setCurrentPopupRect();
            }
            setTitle(getTitle());
		}
    }
    
    public void layoutCurrentPopup(){
    	setCurrentPopupRect();
    	repaint(displayRect);
    }
    
    private void setMenuRect(Menu menu){
    	//set the display rect for the menu
        Rectangle menuRect=new Rectangle();
        menuRect.x=displayRect.x+
        			displayRect.width/2;
        menuRect.y=displayRect.y+
        			titlebarRect.height;
        menuRect.width=displayRect.width/2;
        menuRect.height=displayRect.height-
        			(titlebarRect.height+menubarRect.height);
        Rectangle spanRect=new Rectangle();
        spanRect.y=menuRect.y;
        spanRect.height=menuRect.height;
        //Display rect should always be after the draw start has been set to avoid conflicts 
//        when calculating the rects for the menu items
        menu.setSpanRect(spanRect);
        menu.setAlignment(Menu.ALIGN_RIGHT);
        menu.setDrawStart(menuRect.y+menuRect.height);
        menu.setDisplayRect(menuRect);
    }
    public void focusLost()
    {
    	scrolling=false;
    	focussed=false;
    	contentPane.focusLost();
        repaint(displayRect);
    }
    public void focusGained()
    {
    	focussed=true;
    	setTitle(title);
    	if(currentPopUp!=null){
    		currentPopUp.focusGained();
    	}
    	contentPane.focusGained();
    	repaint(displayRect);
    }
    public void setParent(Item parent)
    {
    }
    /**
     * Sets whether this Windows should be displayed in fullscreen or not. Setting this
     * window to display in fullscreen mode hides the MenuBar and the title of
     * the window such that the contentPane of this window occupies the whole available
     *  display area for this application
     * @param fullScreen true to switch to fullscreen mode and false otherwise
     */
    public void setFullScreenMode(boolean fullScreen)
    {
    	if(this.fullScreen==fullScreen)return;
    	this.fullScreen=fullScreen;
        if(fullScreen)
        {
        	if(isMenuDisplaying)
        	{
        		isMenuDisplaying=false;
        		currentMenu.focusLost();
        	}
        	menuArrowRect=new Rectangle(getDisplayRect().x+getDisplayRect().width-20,
        			getDisplayRect().y+getDisplayRect().height-10, 
        			10, 10);
        	if(arrowImage==null){
        		try
        		{
        			arrowImage=GlobalControl.getImageFactory().scaleImage(Image.createImage("/arrow.png"), 10,
        					10, Sprite.TRANS_ROT270);
        		}catch(IOException ioe){
//        			ioe.printStackTrace();
        			}
        	}
        }
        setDisplayRect(displayRect);
        repaint(displayRect);
    }
    /**
     * Checks whether this ScreenWindow is currently in fullscreen mode or not
     * @return true if this ScrennWindow is currently in fullscreen mode
     * and false otherwise
     */
    public boolean isFullScreenOn()
    {
        return fullScreen;
    }
    public Item getParent()
    {
        return null;
    }
	public Rectangle getMenuBarRect() 
	{
		return menubarRect;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title=title;
		this.titleWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.WINDOW_TITLE_FONT)).stringWidth(this.title);
		titleChanged=true;
        if(titleWidth>titlebarRect.width-2)
        {
            new Thread(new TitleScroller()).start();
        }
        else
        {
        	repaint(titlebarRect);
        }
	}
	//The class that is responsible for title scrolling if its too long
    private class TitleScroller implements Runnable
    {
        public void run()
        {
            if(scrolling || displayRect.width<=1)
            {
                return;
            }
            if(titleWidth>titlebarRect.width-2 && focussed)
            {
                scrolling=true;
            }
            //the variable for the increment
            int increment=-GlobalControl.getTextScrollSpeed();
            while(focussed && scrolling)
            {
            	if(titleChanged){
            		titleChanged=false;
            	}
            	if(titleWidth<=titlebarRect.width)
                {
                    break;
                }
            	//calculate the between name and available display area
            	int diff=titleWidth-titlebarRect.width+2;
            	if(titleChanged)
            	{
            		titleIndent=0;
            		if(diff<=0)
            		{
            			repaint(titlebarRect);
            			break;
            		}
            	}
                
                try
                {
                    Thread.sleep(100);
                }catch(InterruptedException ie){}
                if(fullScreen)
                {
                	break;
                }
                if(titleIndent<-diff || titleIndent>=0)
                {
                	repaint(ScreenWindow.this.titlebarRect);
                	try{Thread.sleep(1200);}catch(InterruptedException ie){}
                	titleIndent=0;
                	repaint(ScreenWindow.this.titlebarRect);
                	try{Thread.sleep(1500);}catch(InterruptedException ie){}
                }
                titleIndent+=increment;
                repaint(ScreenWindow.this.titlebarRect);
            }
            scrolling=false;
            if(titleWidth>titlebarRect.width-2 && focussed)
            {
                titleChanged=true;
                new Thread(new TitleScroller()).start();
            }
            titleIndent=0;
        }
    }
    /**
     * Show the PopupWindow passed as a parameter to this window on top of this Window.
     * A screen window can have more than one popup added to it but only one can be 
     * displayed at a time. The most recent PopupWindow to be shown is the one that shows
     * and when it disappears the one that had come before it is displayed.
     * In shor the PopupWindows are displayed in a last added currently shown manner
     * @param window the PopupWindow to be displayed
     */
    public void showPopUp(PopUpWindow window)
    {
    	synchronized (this) {
    		if(window!=currentPopUp
        			&& !popups.contains(window) && focussed)
        	{
        		if(displayRect.width>2)
        		if(currentPopUp!=null){
        			currentPopUp.setParent(null);
        			currentPopUp.focusLost();
        		}
        		window.setParent(this);
        		currentPopUp=window;
            	currentMenu=currentPopUp.getMenu();
            	setMenuRect(currentMenu);
            	popups.addElement(window);
            	//set the display rect for any popup showing
                setCurrentPopupRect();
        	}
		}
    	repaint(displayRect);
    }
    /**
     * Removes and hides the specified popup window from the list of popup
     *  windows for this ScreenWindow
     * @param popupWindow the Popup window to be hidden
     */
    public void hidePopup(PopUpWindow popupWindow)
    {
    	synchronized (this) {
    		if(popupWindow==null)return;
        	popups.removeElement(popupWindow);
        	if(currentPopUp!=null && popupWindow==currentPopUp)
        	{
        		currentPopUp.focusLost();
        		currentPopUp.setParent(null);
            	if(!popups.isEmpty())
            	{
            		currentPopUp=(PopUpWindow)popups.elementAt(popups.size()-1);
                	currentMenu=currentPopUp.getMenu();
                	setMenuRect(currentMenu);
                	setCurrentPopupRect();
                	if(focussed)currentPopUp.focusGained();
            	}
            	else
            	{
            		currentPopUp=null;
            		currentMenu=menu;
            		setMenuRect(currentMenu);
            		contentPane.focusGained();
            	}
        	}
        	repaint(displayRect);
		}
    }
	private void setCurrentPopupRect() {
		if(currentPopUp!=null){
			Rectangle popRect=new Rectangle();
	    	popRect.x=bodyRect.x+8;
	    	popRect.width=bodyRect.width-16;
	    	popRect.y=bodyRect.y+8;
	    	popRect.height=bodyRect.height-16;
	    	Rectangle min=currentPopUp.getMinimumDisplayRect(popRect.width);
	    	popRect.width=min.width<popRect.width?min.width:popRect.width;
	    	popRect.height=min.height<popRect.height?min.height:popRect.height;
	    	popRect.x=bodyRect.x+(bodyRect.width-popRect.width)/2;
	    	popRect.y=bodyRect.y+(bodyRect.height-popRect.height)/2;
	    	currentPopUp.setDisplayRect(popRect);
	    	if(focussed)currentPopUp.focusGained();
		}
	}
	public void menuRightSelected() {
		
	}
	public void menuLeftSelected() {
	}
	public PopUpWindow getCurrentPopup() {
		return currentPopUp;
	}
	public boolean isFocussed() {
		return focussed;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		getContentPane().moveRect(dx, dy);
	}
}