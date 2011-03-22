/*
 * Copyright (c) 2010 Kenya Mobile World Consulting Ltd
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed AS IS WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.flemil.control;


import java.util.Calendar;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.MIDlet;

import org.flemil.ui.component.ScreenWindow;
import org.flemil.util.ImageFactory;
import org.flemil.util.Rectangle;




/**
 * Class that provide a global control for different features of the application
 * such as overall styling, currently displayed component and display of
 * notification messages.
 * @author Solomon Kariri
 */
public class GlobalControl
{
    //Constants for screen layout
    /**
     * Landscape layout of the whole application. Pass this value to the setLayout() 
     * method to switch the applications layout to landscape
     */
    public static final byte LANDSCAPE_LAYOUT=0;
    /**
     * Portrait layout of the whole application.  Pass this value to the setLayout() 
     * method to switch the applications layout to portrait
     */
    public static final byte PORTRAIT_LAYOUT=1;
    //The global style
    private static Style globalStyle=null;
    //The application layout
    private byte layout=PORTRAIT_LAYOUT;
    //Applications MIDlet
    private static MIDlet currentMIDlet;
	//The display for this application
    private Display display;
    //The main canvas used for drawing the whole application
    private Canvas mainCanvas;
    //The currently active component
    private ScreenWindow currentWindow;
    //Image for double buffering
    private Image bufferImg;
    
    private static int[] softKeys={-6,-7};
    
    private boolean editingText;
    /**
     * The ImageFactory instance that is used throughout the whole of the application.
     * You should avoid initializing the ImageFactory class every time you want to use 
     * it and instead, call this method to get an ImageFactory to work with
     */
    private static ImageFactory imageFactory=new ImageFactory();
    //The rectangle for the available display area
    private static Rectangle displayArea=new Rectangle();
    //background image for menu items
    private static Image menuItemBGround;
    //background edge image for titles
    private static Image titleBGround;
    //background edge image for menu items
    private static Image menuItemEdgeBGround;
    //background edge image for titles
    private static Image tabBGround;
    private static Image tabEdgeBGround;
    private static Image buttonGBround;
    private static Image buttonEdgeBGround;
    private static Image menuPopImage;
    
    private boolean showTime;
    
    public Image getMenuPopImage() {
		return menuPopImage;
	}
	public Image getTabBGround() {
		return tabBGround;
	}
	public Image getTabEdgeBGround() {
		return tabEdgeBGround;
	}
	private static Image titleEdgeBGround;
	public Image getMenuItemEdgeBGround() {
		return menuItemEdgeBGround;
	}
	public Image getTitleEdgeBGround() {
		return titleEdgeBGround;
	}
	public Image getThemeEdgeForeground() {
		return themeEdgeForeground;
	}
	//menu bar bg image
    private static Image menuBarBGround;
    //variable to be used for fade color
    private Image fadeImage;
    //variable for theme foreground
    private static Image themeForeground;
    //variable for theme edge foreground image
    private static Image themeEdgeForeground;
	//the global control for this application
    private static GlobalControl control;
    //the variable to keep track of the scrolling speed for elements
    private static int itemTextScrollSpeed=3;
    //variable to manipulate scroll speeds of items in a panel
    private static int panelScrollSpeed=14;
    
    //variable for whether fading is enabled
    private boolean fading;
	private boolean refreshingStyle;
	private boolean settingLayout;
	private boolean inited;

    /**
     * Creates a new GlobalControl for the given Midlet having the given default
     * style
     * @param midlet the Midlet associated with this global control is used
     * @param globStyle the style to be used as default by components if non has
     * been specified for them specifically
     */
    private GlobalControl(MIDlet midlet,Style globStyle)
    {
        currentMIDlet=midlet;
        globalStyle=globStyle;
        display=Display.getDisplay(midlet);
        //Initialize main canvas
        mainCanvas=new MainCanvas();
        //Initialize display area rect
        displayArea=new Rectangle();
        displayArea.width=mainCanvas.getWidth();
        displayArea.height=mainCanvas.getHeight();
        initBGrounds();
        initControlsBGrounds();
        inited=true;
    }
    /**
     * Creates a new GlobalControl for the given MIDlet having a default style
     * obtained from Style.getDefault() method call
     * @param midlet the MIDlet associated with this GlobalControl
     */
    private GlobalControl(MIDlet midlet)
    {
        this(midlet,globalStyle);
    }
    /**
     * Returns the image that is to be used for fading components that are blurred so that
     * some items can be more conspicuous and the user can easily focus on them. The height 
     * of the image returned is equal to the height of current application's display area. 
     * Height here refers to the windows title bar to menu bar length irrespective of the 
     * applications current orientation landscape or portrait
     * @return the image that is to be used for fading components that are blurred
     */
    public Image getFadeImage() {
    	if(fadeImage==null)setFading(true);
		return fadeImage;
	}
    /**
     * Returns the rectangle that a full screen display should occupy. The dimensions of this
     * Rectangle varies depending on the current orientation of the application
     * @return the fullscreen rectngle possible for any component on the application for the 
     * current orientation
     */
    public Rectangle getFullScreenRect()
    {
    	return new Rectangle(displayArea.x,displayArea.y,displayArea.width,displayArea.height);
    }
    /**
     * Called to initialize and setup variables in this GlobalControl instance so
     * that the application can be able to make use of most the Flemil library components.
     * This method should be the first call in an application. You can also 
     * make a call to the init(MIDlet)
     * method instead of this one if you want to start off with the default Style for this application.
     * This is the approach used in the demo application that comes with this library and also available at 
     * <a href="http://flemil.com/">Flemil Web Site</a>No calls to any other 
     * component should be done before this call is made. A good place to make the call
     * to this method is at the constructor of the MIDlet which will be using the Flemil
     * library. See the example application for details on how this should be done.
     * @param midlet the MIDlet in which the library is going to be used.
     * @param style a Style to be used as the globalStyle for the application.
     */
    public static void init(MIDlet midlet,Style style)
    {
    	if(System.getProperty("com.mot.carrier.URL")!=null){
    		softKeys=new int[]{-21,-22};
    	}
        control=new GlobalControl(midlet, style);
    }
    /**
     * * Called to initialize and setup variables in this GlobalControl instance so
     * that the application can be able to make use of most the Flemil library components.
     * This method should be the first call in an application. No calls to any other 
     * component should be done before this call is made. A good place to make the call
     * to this method is at the constructor of the MIDlet which will be using the Flemil
     * library. See the example application for details on how this should be done.
     * @param midlet the MIDlet in which the library is going to be used.
     */
    public static void init(MIDlet midlet)
    {
        init(midlet,Style.getDefault());
    }
    /**
     * Returns the current MIDlet that this GlobalControl is being used with
     * @return current MIDlet associated with this GlobalControl
     */
    public static MIDlet getCurrentMIDlet() {
		return currentMIDlet;
	}
    /**
     * Returns the currently initialized GlobalControl for this application. This 
     * method should not be called before one of the init() group of methods has been 
     * called .
     * @see #init(MIDlet)
     * @see #init(MIDlet, Style)
     * @return the currently initialized GlobalControl for this application
     * @throws IllegalStateException if none of the init() group of methods has been called.
     */
    public static GlobalControl getControl()throws IllegalStateException
    {
        if(currentMIDlet==null)
        {
            throw new IllegalStateException("The GlobalControl is not initialized. " +
            		"Please call GlobalControl.init(MIDlet) before calling getControl()");
        }
        else
        {
            return control;
        }
    }
    /**
     * Returns the image that is being currently used by the titles as background for all windows 
     * in the application that display titles
     * @return Image being used as the background for titles in Windows
     */
    public Image getTitleBGround()
    {
        return titleBGround;
    }
    /**
     * Returns the Image that is being currently used as background for MenuItems
     * @return Image that is being currently used as background for MenuItems
     */
    public Image getMenuItemBGround()
    {
        return menuItemBGround;
    }
    /**
     * Returns the Image that is being currently used as foreground for windows Theme
     * @return Image that is being currently used as foreground for windows themes
     */
    public Image getThemeForeground()
    {
        return themeForeground;
    }
    /**
     * Returns the Image that is being used for the menu bar by this application.
     * @return Image that is being used for the menu bar by this application
     */
    public Image getMenuBarBGround()
    {
    	return menuBarBGround;
    }
    private static void initBGrounds()
    {
    	int wid=10;
        //Initialize the exit menu bar image
    	byte[] tmpStore=null;
        tmpStore=imageFactory.createTextureImageBytes(wid,
                ((Font)globalStyle.getProperty(Style.MENU_BAR_FONT)).getHeight()+2, 
                ((Integer)globalStyle.getProperty(Style.MENU_BAR_BACKGROUND)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.MENU_BAR_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.MENU_BAR_BOTTOM_OPACITY)).
                    intValue(),
                    ((Byte)globalStyle.getProperty(Style.MENU_BAR_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.MENU_BAR_SHADING)).booleanValue(),
                    0);
        menuBarBGround=Image.createImage(tmpStore, 0, tmpStore.length);
        tmpStore=imageFactory.createTextureImageBytes(wid,
                ((Font)globalStyle.getProperty(Style.WINDOW_TITLE_FONT)).getHeight()+2, 
                ((Integer)globalStyle.getProperty(Style.TITLE_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.TITLE_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.TITLE_BOTTOM_OPACITY)).
                    intValue(),
                    ((Byte)globalStyle.getProperty(Style.TITLE_BAR_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.TITLE_BAR_SHADING)).booleanValue(),
                    0);
        titleBGround=Image.createImage(tmpStore, 0, tmpStore.length);
        tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(wid,
                ((Font)globalStyle.getProperty(Style.MENU_ITEM_FONT)).getHeight()+2,
                ((Integer)globalStyle.getProperty(Style.MENU_HIGHLIGHT_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.MENU_ITEM_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.MENU_ITEM_BOTTOM_OPACITY)).
                    intValue(),
                    ((Byte)globalStyle.getProperty(Style.MENU_ITEM_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.MENU_ITEM_SHADING)).booleanValue(),
                    0);
        menuItemBGround=Image.createImage(tmpStore, 0, tmpStore.length);
        tmpStore=imageFactory.createTextureImageBytes(
        		((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue()*2,
                ((Font)globalStyle.getProperty(Style.WINDOW_TITLE_FONT)).getHeight()+2, 
                ((Integer)globalStyle.getProperty(Style.TITLE_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.TITLE_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.TITLE_BOTTOM_OPACITY)).
                    intValue(),
                    ((Byte)globalStyle.getProperty(Style.TITLE_BAR_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.TITLE_BAR_SHADING)).booleanValue(),
                    ((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue());
        titleEdgeBGround=Image.createImage(tmpStore, 0, tmpStore.length);
        tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(
        		((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue()*2,
                ((Font)globalStyle.getProperty(Style.MENU_ITEM_FONT)).getHeight()+2,
                ((Integer)globalStyle.getProperty(Style.MENU_HIGHLIGHT_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.MENU_ITEM_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.MENU_ITEM_BOTTOM_OPACITY)).
                    intValue(),
                    ((Byte)globalStyle.getProperty(Style.MENU_ITEM_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.MENU_ITEM_SHADING)).booleanValue(),
                    ((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue());
        menuItemEdgeBGround=Image.createImage(tmpStore, 0, tmpStore.length);
    }
    private void initControlsBGrounds()
    {
    	byte[] tmpStore=null;
    	tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(20,
    			((Font)globalStyle.getProperty(Style.TAB_FONT)).getHeight()+4,
                ((Integer)globalStyle.getProperty(Style.TAB_FOCUS_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.TAB_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.TAB_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)globalStyle.getProperty(Style.TAB_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.TAB_SHADING)).booleanValue(),
                    0);
    	tabBGround=Image.createImage(tmpStore, 0, tmpStore.length);
    	tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(
    			((Integer)globalStyle.getProperty(Style.TAB_CURVE_RADIUS)).intValue()*2,
    			((Font)globalStyle.getProperty(Style.TAB_FONT)).getHeight()+4,
                ((Integer)globalStyle.getProperty(Style.TAB_FOCUS_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.TAB_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.TAB_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)globalStyle.getProperty(Style.TAB_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.TAB_SHADING)).booleanValue(),
                    ((Integer)globalStyle.getProperty(Style.TAB_CURVE_RADIUS)).intValue());
    	tabEdgeBGround=Image.createImage(tmpStore, 0, tmpStore.length);
    	tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(20,
    			((Font)globalStyle.getProperty(Style.BUTTON_FONT)).getHeight()+2,
                ((Integer)globalStyle.getProperty(Style.BUTTON_FOCUS_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.BUTTON_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.BUTTON_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)globalStyle.getProperty(Style.BUTTON_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.BUTTON_SHADING)).booleanValue(),
                    0);
    	buttonGBround=Image.createImage(tmpStore, 0, tmpStore.length);
    	tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(
    			((Integer)globalStyle.getProperty(Style.BUTTON_CURVE_RADIUS)).intValue()*2,
    			((Font)globalStyle.getProperty(Style.BUTTON_FONT)).getHeight()+2,
                ((Integer)globalStyle.getProperty(Style.BUTTON_FOCUS_BACKGROUND)).
                    intValue(), 
                ((Integer)globalStyle.getProperty(Style.BUTTON_TOP_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.BUTTON_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)globalStyle.getProperty(Style.BUTTON_LIGHTING)).byteValue(),
                    ((Boolean)globalStyle.getProperty(Style.BUTTON_SHADING)).booleanValue(),
                    ((Integer)globalStyle.getProperty(Style.BUTTON_CURVE_RADIUS)).intValue());
    	buttonEdgeBGround=Image.createImage(tmpStore, 0, tmpStore.length);
    	int imgWidth=((Font)globalStyle.getProperty(
                Style.MENU_ITEM_FONT)).getHeight()/2;
    	try {
			menuPopImage=GlobalControl.getImageFactory().scaleImage(Image.createImage("/arrow.png"), imgWidth,
			        ((Font)globalStyle.getProperty(
			                Style.MENU_ITEM_FONT)).getHeight(), Sprite.TRANS_NONE);
		} catch (Exception e) {
//			e.printStackTrace();
		}
    }
    private void initThemeForeground()
    {
    	byte[] tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(5,
                displayArea.height,
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_FOREGROUND)).
                    intValue(), 
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_TOP_OPACITY)).
                    intValue(),
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)GlobalControl.getControl().getStyle().getProperty(Style.THEME_LIGHTING)).byteValue(),
                    ((Boolean)GlobalControl.getControl().getStyle().getProperty(Style.THEME_SHADING)).booleanValue(),
                    0);
    	themeForeground=Image.createImage(tmpStore, 0, tmpStore.length);
    	tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(
    			((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue()*2,
                displayArea.height,
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_FOREGROUND)).
                    intValue(), 
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_TOP_OPACITY)).
                    intValue(),
                ((Integer)GlobalControl.getControl().getStyle().getProperty(Style.THEME_BOTTOM_OPACITY)).
                    intValue(), 
                    ((Byte)GlobalControl.getControl().getStyle().getProperty(Style.THEME_LIGHTING)).byteValue(),
                    ((Boolean)GlobalControl.getControl().getStyle().getProperty(Style.THEME_SHADING)).booleanValue(),
                    ((Integer)globalStyle.getProperty(Style.CURVES_RADIUS)).intValue());
    	themeEdgeForeground=Image.createImage(tmpStore, 0, tmpStore.length);
    }
    
    public Image getButtonGBround() {
		return buttonGBround;
	}
	public Image getButtonEdgeBGround() {
		return buttonEdgeBGround;
	}
	/**
     * Returns the global Style for this application. This is the Style
     *  that us being currently used by the application.
     *  @see #setStyle(Style)
     * @return the global Style for this application
     */
    public Style getStyle()
    {
        return globalStyle;
    }
    /**
     * Sets the global Style for this application. This is the Style that is
     *  to be used by the application. Calling this method with a style updates 
     *  the whole application to use the Style that was passed in.
     *  @see #getStyle()
     * @param style the global Style for this application
     */
    public void setStyle(Style style)
    {
        globalStyle=style;
        refreshStyle();
    }
    /**
     * Returns the Canvas that is being used to display all the items in the application.
     * Flemil uses an architecture where all the components are painted on a single Canvas 
     * for the lifetime of the application making it more resource friendly.
     * @return Canvas that is being used to display all the items in the application.
     */
    public Canvas getMainDisplayCanvas()
    {
        return mainCanvas;
    }
    /**
     * Repaints the display area of the application. You should not 
     * call this method in your code but should call the repaint(Rectangle) method instead
     * @see #repaint(Rectangle)
     */
    public synchronized void repaint()
    {
    	repaint(displayArea);
    }
    /**
     * Repaints the section of the application that intersects with the Rectangle that is passed to
     * this method. You should avoid calling this method and instead all the repaint(Rectangle) method
     *  in the item you want to repaint since that call will propagate to this call in a clean way.
     * 
     * @param rect the section or Rectangle to be repainted
     */
    public synchronized void repaint(Rectangle rect)
    {
    	if(refreshingStyle || settingLayout)return;
    	if(layout==GlobalControl.LANDSCAPE_LAYOUT)
    	{
    		mainCanvas.repaint(getWidth()-rect.y-rect.height,rect.x,rect.height,rect.width);
    	}
    	else
    	{
    		mainCanvas.repaint(rect.x,rect.y,rect.width,rect.height);
    	}
    }
    /**
     * Returns the ImageRectangle in use for this application. You should make 
     * this call and avoid initializing ImageFactory in your application in order
     *  to be more resource friendly
     * @return the ImageFactory being used by this application
     */
    public static ImageFactory getImageFactory()
    {
        return imageFactory;
    }
    /**
     * Returns the width of the canvas being used to draw items for this application. 
     * This is not to be confused with getDisplayArea() method call return values.
     * @return width of the canvas being used to draw items for this application.
     */
    public int getWidth()
    {
        return mainCanvas.getWidth();
    }
    /**
     * Returns the height of the canvas being used to draw items for this application. 
     * This is not to be confused with getDisplayArea() method call return values.
     * @return height of the canvas being used to draw items for this application.
     */
    public int getHeight()
    {
        return mainCanvas.getHeight();
    }
    private void paint(Graphics g)
    {
		Rectangle currentClip=new Rectangle();
    	if(layout==LANDSCAPE_LAYOUT)
    	{
    		currentClip.x=g.getClipY();
    		currentClip.y=displayArea.height-g.getClipX()-g.getClipWidth();
    		currentClip.width=g.getClipHeight();
    		currentClip.height=g.getClipWidth();
    	}
    	else
    	{
    		currentClip.x=g.getClipX();
    		currentClip.y=g.getClipY();
    		currentClip.width=g.getClipWidth();
    		currentClip.height=g.getClipHeight();
    		if(g.getClipHeight()>displayArea.height){
    			currentClip.height=displayArea.height;
    		}
    	}
    	//Clean up memory first
    	Runtime.getRuntime().gc();
    	//Draw on the offs screen image
    	//paint the image on graphics accordingly
    	Graphics gImg=bufferImg.getGraphics();

    	//Draw the main WindowImpl
    	if(currentWindow!=null)
    	{
    		currentWindow.paint(gImg,currentClip); 
    	}
    	//Draw alerts if any is active
    	//Draw notification if any is active
    	try
    	{
    		if(layout==LANDSCAPE_LAYOUT)
    		{
    			g.drawRegion(bufferImg, currentClip.x, currentClip.y, currentClip.width, currentClip.height,
    					Sprite.TRANS_ROT90, 
    					g.getClipX(), g.getClipY(), Graphics.TOP|Graphics.LEFT);
    		}
    		else
    		{
    			g.drawRegion(bufferImg, currentClip.x, currentClip.y, currentClip.width, currentClip.height,
    					Sprite.TRANS_NONE, 
    					currentClip.x, currentClip.y, Graphics.TOP|Graphics.LEFT);
    		}
    	}catch(IllegalArgumentException iae){
//    		iae.printStackTrace();
    	}
    	//Clean up memory after
    	Runtime.getRuntime().gc();
    }
    public void setDisplayedWindow(ScreenWindow window){
    	//Send a focus lost signal to the current component if any
        if(currentWindow!=null)
        {
            currentWindow.focusLost();
            //Clean up any previously used memory
            Runtime.getRuntime().gc();
        }
        currentWindow=window;
    	currentWindow.focusGained();
    	repaint(displayArea);
    }
    /**
     * Sets the ScreenWindow that is being currently displayed to the user.
     * @param window the ScreenWindow to be displayed to the user
     */
    public void setCurrent(ScreenWindow window)
    {
        //Initialize width and height for display
        displayArea.width=layout==GlobalControl.LANDSCAPE_LAYOUT?mainCanvas.getHeight():mainCanvas.getWidth();
        displayArea.height=layout==GlobalControl.LANDSCAPE_LAYOUT?mainCanvas.getWidth():mainCanvas.getHeight();
        //Initialize the off screen image
        bufferImg=Image.createImage(displayArea.width, displayArea.height);
        setLayout(layout);
        //Set the default layout
        window.setDisplayRect(displayArea);
      //Send a focus lost signal to the current component if any
        if(currentWindow!=null)
        {
            currentWindow.focusLost();
            //Clean up any previously used memory
            Runtime.getRuntime().gc();
        }
        if(display.getCurrent()!=mainCanvas){
        	display.setCurrent(mainCanvas);
        }
        currentWindow=window;
        //Send a focus alert to component to initiate auto scrolling
        currentWindow.focusGained();
    }
    /**
     * Returns the ScreenWindow that is being currently displayed to the user.
     * @return ScreenWindow that is being currently displayed to the user.
     */
    public ScreenWindow getCurrent()
    {
        return currentWindow;
    }
    /**
     * Refreshes or reloads the currently displayed style
     */
    private void refreshStyle()
    {
    	boolean initalFocus=currentWindow!=null?currentWindow.isFocusible():false;
    	if(currentWindow!=null && currentWindow.isFocussed())
    	{
    		currentWindow.focusLost();
    	}
    	refreshingStyle=true;
    	synchronized (this) {
    		initBGrounds();
    		initControlsBGrounds();
        	setLayout(layout);
		}
    	if(currentWindow!=null && initalFocus)
    	{
    		currentWindow.focusGained();
    	}
    	if(currentWindow!=null){
    		currentWindow.setDisplayRect(displayArea);
    	}
    	refreshingStyle=false;
    	repaint(displayArea);
    }
    public void keyRepeatedEventReturned(int keyCode)
    {
    }
    public void keyRepeatedEvent(int keyCode)
    {
        keyPressedEvent(keyCode);
    }
    public void keyPressedEventReturned(int keyCode)
    {
    }
    public void keyPressedEvent(int keyCode)
    {
        if(layout==LANDSCAPE_LAYOUT)
        {
            if(currentWindow!=null)
            {
                //swap the keys accordingly
                if(mainCanvas.getGameAction(keyCode)==Canvas.LEFT)
                {
                    currentWindow.keyPressedEvent(
                            mainCanvas.getKeyCode(Canvas.DOWN));
                }
                else if(mainCanvas.getGameAction(keyCode)==Canvas.RIGHT)
                {
                    currentWindow.keyPressedEvent(
                            mainCanvas.getKeyCode(Canvas.UP));
                }
                else if(mainCanvas.getGameAction(keyCode)==Canvas.DOWN)
                {
                    currentWindow.keyPressedEvent(
                            mainCanvas.getKeyCode(Canvas.RIGHT));
                }
                else if(mainCanvas.getGameAction(keyCode)==Canvas.UP)
                {
                    currentWindow.keyPressedEvent(
                            mainCanvas.getKeyCode(Canvas.LEFT));                        
                }
                else if(keyCode==softKeys[1])
                {
                	currentWindow.keyPressedEvent(softKeys[0]);
                }
                else if(keyCode==softKeys[0])
                {
                	currentWindow.keyPressedEvent(softKeys[1]);
                }
                else
                {
                    currentWindow.keyPressedEvent(keyCode);
                }
            }
        }
        else
        {
            if(currentWindow!=null)
            {
                currentWindow.keyPressedEvent(keyCode);
            }
        }
    }
    public void keyReleasedEventReturned(int keyCode)
    {
    }
    public void keyReleasedEvent(int keyCode)
    {
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
        if(layout==LANDSCAPE_LAYOUT)
        {
            int tmp=x;
            x=y;
            y=displayArea.height-tmp;
        }
        if(currentWindow!=null)
        {
            currentWindow.pointerPressedEvent(x, y);
        }
    }
    public void pointerReleasedEvent(int x,int y)
    {
    	if(layout==LANDSCAPE_LAYOUT)
        {
            int tmp=x;
            x=y;
            y=displayArea.height-tmp;
        }
        if(currentWindow!=null)
        {
            currentWindow.pointerReleasedEvent(x, y);
        }
    }
    public void pointerDraggedEvent(int x,int y)
    {
    	if(layout==LANDSCAPE_LAYOUT)
        {
            int tmp=x;
            x=y;
            y=displayArea.height-tmp;
        }
        if(currentWindow!=null)
        {
            currentWindow.pointerDraggedEvent(x, y);
        }
    }
    /**
     * Returns the current layout or orientation being used by the application. The return value is either
     * GlobalControl.LANDSCAPE_LAYOUT or GlobalControl.PORTRAIT_LAYOUT
     * @see #LANDSCAPE_LAYOUT
     * @see #PORTRAIT_LAYOUT
     * @return current layout or orientation being used by the application.
     */
    public byte getLayout()
    {
        return layout;
    }
    /**
     * Sets the current layout or orientation being used by the application.The parameter value is either
     * GlobalControl.LANDSCAPE_LAYOUT or GlobalControl.PORTRAIT_LAYOUT
     * @see #LANDSCAPE_LAYOUT
     * @see #PORTRAIT_LAYOUT
     * @param layout current layout or orientation to be used by the application.
     */
    public void setLayout(byte layout)
    {
    	boolean switched=false;
    	settingLayout=true;
    	byte oldLayout=this.layout;
		this.layout=layout;
		if(layout!=oldLayout)
        {
            int temp=displayArea.width;
            displayArea.width=displayArea.height;
            displayArea.height=temp;
            bufferImg=Image.createImage(displayArea.width, displayArea.height);
            switched=true;
        }
		setFading(fading);
		initThemeForeground();
        if(currentWindow!=null)
        {
        	if(!displayArea.equals(currentWindow.getDisplayRect()) ||
        			switched){
        		//Update the current windows display rect
                Rectangle windowRect=new Rectangle();
                windowRect.x=displayArea.x;
                windowRect.y=displayArea.y;
                windowRect.width=displayArea.width;
                windowRect.height=displayArea.height;
                currentWindow.setDisplayRect(windowRect);
        	}
        }
        //Clean up any previously used memory
        Runtime.getRuntime().gc();
        settingLayout=false;
        if(switched){
        	new Thread(new Runnable() {
				public void run() {
					for(int i=0;i<layoutSwitchListeners.size();i++){
						((LayoutSwitchListener)layoutSwitchListeners.elementAt(i)).layoutChanged();
					}
				}
			}).start();
        }
        repaint(displayArea);
    }
    /**
     * Returns the Display instance being currently used by the application.
     * @return Display instance being currently used by the application.
     */
    public Display getDisplay()
    {
        return display;
    }
    /**
     * Returns the currently available display area to be used by the items for drawing. 
     * You should call this method instead of the getWidth() and getHeight() methods to 
     * acquire the available space since this method takes care of the application orientation
     *  which the getWidth() and getHeight() methods do not
     *  @see #getWidth()
     *  @see #getHeight()
     * @return currently available display area to be used by the items for drawing.
     */
    public Rectangle getDisplayArea()
    {
        return displayArea;
    }
    /**
     * Sets the text scroll speed by setting the number of pixels the test should shift for
     * auto scrolling text in the application. A small value results in a slower scrolling 
     * and a higher value results in faster scrolling. The minimum allowed value is 1.
     * @see #getTextScrollSpeed()
     * @param scrollSpeed text scroll speed by setting the number of pixels the test should shift for
     * auto scrolling text in the application.
     */
    public static void setTextScrollSpeed(int scrollSpeed) {
    	scrollSpeed=scrollSpeed<=0?1:scrollSpeed;
		GlobalControl.itemTextScrollSpeed = scrollSpeed;
	}
    /**
     * Returns the scroll speed for text
     * @see #setTextScrollSpeed(int)
     */
	public static int getTextScrollSpeed() {
		return itemTextScrollSpeed;
	}
	/**
	 * Sets the amount of pixels that the items should be scrolled by if there are currently 
	 * focused item or the currently focused item spans more area than available and requires
	 *  some scrolling for all contents to be visible. The minimum allowed value is 1.
	 *  This value has similar effect as the textScrollingSpeed.
	 *  @see #setTextScrollSpeed(int)
	 * @param panelScrollSpeed the amount of pixels that the items should be scrolled by if there are currently 
	 * focused item or the currently focused item spans more area than available and requires
	 *  some scrolling for all contents to be visible.
	 */
	public static void setPanelScrollSpeed(int panelScrollSpeed) {
		panelScrollSpeed=panelScrollSpeed<=0?1:panelScrollSpeed;
		GlobalControl.panelScrollSpeed = panelScrollSpeed;
	}
	/**
	 * Returns the scroll speed being used by panels in teh application
	 * @return the scroll speed being used by panels in teh application
	 */
	public static int getPanelScrollSpeed() {
		return panelScrollSpeed;
	}
	/**
	 * Sets whether fading/blurring should occur in the application for example when a PopupWindow is
	 * displayed or a sub menu os shown
	 * @param fading true for fading to occur and false otherwise
	 */
	public void setFading(boolean fading) {
		this.fading = fading;
		if(this.fading)
		{
			byte[] tmpStore=GlobalControl.getImageFactory().createTextureImageBytes(10,displayArea.height, 
					((Integer)globalStyle.getProperty(Style.FADE_COLOR)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.FADE_OPACITY)).
                    intValue(),
                ((Integer)globalStyle.getProperty(Style.FADE_OPACITY)).
                    intValue(),ImageFactory.LIGHT_FRONT,
                    false,4);
			fadeImage=Image.createImage(tmpStore, 0, tmpStore.length);
		}
		else
		{
			fadeImage=null;
			Runtime.getRuntime().gc();
		}
	}
	/**
	 * Returns a boolean value showing whether fading is allowed for this application
	 * @return true if fading is available and false otherwise
	 */
	public boolean isFading() {
		return fading;
	}
	
	private void sizeChanged(int newWidth,int newHeight){
		if(layout==GlobalControl.LANDSCAPE_LAYOUT){
			if(displayArea.width==newHeight && displayArea.height==newWidth)return;
    		displayArea=new Rectangle(0, 0, newHeight, newWidth);
    	}
    	else{
    		if(displayArea.width==newWidth && displayArea.height==newHeight)return;
    		displayArea=new Rectangle(0, 0, newWidth, newHeight);
    	}
    	bufferImg=Image.createImage(displayArea.width, displayArea.height);
    	refreshLayout();
	}
	public class MainCanvas extends Canvas
    {
        public MainCanvas()
        {
            setFullScreenMode(true);
        }
        public void paint(Graphics g)
        {
        	if(currentWindow!=null)
    		{
    			GlobalControl.this.paint(g);
    		}
        }
        public void keyPressed(int keyCode)
        {
            keyPressedEvent(keyCode);
        }
        public void keyReleased(int keyCode)
        {
            keyReleasedEvent(keyCode);
        }
        public void keyRepeated(int keyCode)
        {
        	if(getGameAction(keyCode)==Canvas.FIRE)
        	{
        		Runtime.getRuntime().gc();
        	}
        	else
        	{
        		keyRepeatedEvent(keyCode);
        	}
        }
        public void pointerPressed(int x,int y)
        {
            pointerPressedEvent(x, y);
        }
        public void pointerReleased(int x,int y)
        {
            pointerReleasedEvent(x, y);
        }
        public void pointerDragged(int x,int y)
        {
            pointerDraggedEvent(x, y);
        }
        public void sizeChanged(final int newWidth,final int newHeight)
        {
        	if(!inited || editingText)return;
        	new Thread(new Runnable() {
				public void run() {
					GlobalControl.this.sizeChanged(newWidth, newHeight);
				}
			}).start();
        }
    }
	public void refreshLayout() {
		setLayout(layout);
	}
	
	private Vector layoutSwitchListeners=new Vector();
	
	public void addLayoutSwitchListener(LayoutSwitchListener listener){
		layoutSwitchListeners.addElement(listener);
	}
	public void removeLayoutSwitchListener(LayoutSwitchListener listener){
		layoutSwitchListeners.removeElement(listener);
	}
	
	public void setEditingText(boolean editingText) {
		this.editingText = editingText;
	}
	public boolean isEditingText() {
		return editingText;
	}
	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
		if(showTime){
			new Thread(new TimeUpdater()).start();
		}
	}
	public boolean isShowTime() {
		return showTime;
	}
	public static int[] getSoftKeys() {
		return softKeys;
	}
	public interface LayoutSwitchListener{
		public void layoutChanged();
	}
	private static boolean updatinTime;
	class TimeUpdater implements Runnable{
		public void run() {
			synchronized (this) {
				if(updatinTime)return;
				updatinTime=true;
				this.notifyAll();
			}
			Calendar cal=Calendar.getInstance();
			StringBuffer timeString=new StringBuffer();
			timeString.append(cal.get(Calendar.HOUR)==0?"12":""+cal.get(Calendar.HOUR));
			timeString.append(":");
			timeString.append(cal.get(Calendar.MINUTE)<10?("0"+cal.get(Calendar.MINUTE)):
				(""+cal.get(Calendar.MINUTE)));
			cal=null;
			String lastString=timeString.toString();
			timeString=null;
			while(updatinTime && showTime){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
//					e.printStackTrace();
				}
				if(currentWindow!=null && !currentWindow.isFullScreenOn()){
					cal=Calendar.getInstance();
					timeString=new StringBuffer();
    				timeString.append(cal.get(Calendar.HOUR)==0?"12":""+cal.get(Calendar.HOUR));
    				timeString.append(":");
    				timeString.append(cal.get(Calendar.MINUTE)<10?("0"+cal.get(Calendar.MINUTE)):
    					(""+cal.get(Calendar.MINUTE)));
					if(!lastString.equals(timeString.toString())){
						lastString=timeString.toString();
						timeString=null;
						repaint(currentWindow.getMenuBarRect());
					}
				}
			}
			synchronized (this) {
				updatinTime=false;
				this.notifyAll();
			}
		}
	}
}