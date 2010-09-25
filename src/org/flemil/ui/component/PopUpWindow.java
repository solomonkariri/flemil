package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.Window;
import org.flemil.util.Rectangle;




/**
 * Class that represent s a Popup window that is displayed on top of 
 * a ScreenWindow. The popup window will take all the events being
 * propagated to its Parent window when it is being displayed.
 * @author Solomon Kariri
 *
 */
public class PopUpWindow implements Window {

	//The defaultPanel for this WindowImpl
    private Panel contentPane;
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
    //the rectangle to be used as the title bar ect
    private Rectangle titlebarRect;
    //variable to keep track of title changes
    private transient boolean titleChanged;
    //the rectangle to be used as the body of the window
    private Rectangle bodyRect;
    private boolean paintBorder=true;
    private boolean focusible=true;
	private Window parent;
	private boolean showTitleBar;
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
     * Creates a new PopUpWindow with the given title.
     * @param title the title of this WindowImpl
     */
    public PopUpWindow(String title,boolean showTitleBar)
    {
    	displayRect=new Rectangle();
        titlebarRect=new Rectangle();
        bodyRect=new Rectangle();
    	this.contentPane=new Panel();
    	//initialize the windows menu
        this.menu=new Menu(LocaleManager.getTranslation("flemil.options"));
        this.title=title;
        this.showTitleBar=showTitleBar;
        titleWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.WINDOW_TITLE_FONT)).stringWidth(title);
        this.contentPane.setParent(this);
        menu.setParent(this);
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
    	GlobalControl.getControl().keyPressedEventReturned(keyCode);
    }
    public void keyPressedEvent(int keyCode)
    {
    	contentPane.keyPressedEvent(keyCode);
    }
    public void pointerPressedEventReturned(int x,int y)
    {
        
    }
    public void pointerReleasedEventReturned(int x,int y)
    {
    	if(displayRect.contains(x, y, 0)){
    		((GlobalControl.MainCanvas)GlobalControl.getControl().
    				getMainDisplayCanvas()).keyPressed(
    						GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
    	}
    }
    public void pointerDraggedEventReturned(int x,int y)
    {
        
    }
    public void pointerPressedEvent(int x,int y)
    {
        contentPane.pointerPressedEvent(x, y);
    }
    public void pointerReleasedEvent(int x,int y)
    {
        contentPane.pointerReleasedEvent(x, y);
    }
    public void pointerDraggedEvent(int x,int y)
    {
        contentPane.pointerDraggedEvent(x, y);
    }
    public void repaint(Rectangle clip)
    {
        if(parent!=null)
        {
        	parent.repaint(clip);
        }
    }
    public Menu getMenu()
    {
        return menu;
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
        	g.fillRoundRect(edgeRect.x, displayRect.y, curRadius*2, displayRect.height,
    				curRadius,curRadius);
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
        if(showTitleBar)
        {
        	//draw title bar
            edgeRect=new Rectangle(displayRect.x,displayRect.y,curRadius,
            		GlobalControl.getControl().getTitleBGround().getHeight());
            if((intersect=clip.calculateIntersection(edgeRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            	g.fillRoundRect(intersect.x, intersect.y, intersect.width, intersect.height,
            			curRadius,curRadius);
            	g.drawImage(GlobalControl.getControl().getTitleEdgeBGround(),
                        displayRect.x, displayRect.y, Graphics.TOP|Graphics.LEFT);
            	g.setClip(intersect.x, intersect.y+curRadius, intersect.width, 
            			intersect.height-curRadius);
            	g.drawImage(GlobalControl.getControl().getTitleBGround(),
                        displayRect.x, displayRect.y, Graphics.TOP|Graphics.LEFT);
            }
            edgeRect=new Rectangle(displayRect.x+displayRect.width-curRadius,displayRect.y,
            		curRadius,GlobalControl.getControl().getTitleBGround().getHeight());
            if((intersect=clip.calculateIntersection(edgeRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            	g.fillRoundRect(intersect.x, intersect.y, intersect.width, intersect.height,
            			curRadius,curRadius);
            	g.drawRegion(GlobalControl.getControl().getTitleEdgeBGround(),
                        0,0,edgeRect.width,curRadius,Sprite.TRANS_MIRROR,
                        edgeRect.x, edgeRect.y, Graphics.TOP|Graphics.LEFT);
            	g.setClip(intersect.x, intersect.y+curRadius, intersect.width, 
            			intersect.height-curRadius);
            	g.drawImage(GlobalControl.getControl().getTitleBGround(),
            			edgeRect.x, edgeRect.y, Graphics.TOP|Graphics.LEFT);
            }
            edgeRect=new Rectangle(displayRect.x+curRadius,displayRect.y,
            		displayRect.width-(curRadius*2),
            		GlobalControl.getControl().getTitleBGround().getHeight());
            if((intersect=clip.calculateIntersection(edgeRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
            	int imgWidth=GlobalControl.getControl().getTitleBGround().getWidth();
                for(int i=intersect.x-1;i<intersect.x+intersect.width+1;i+=imgWidth-1)
                {
                	g.fillRect(i, intersect.y, imgWidth, intersect.height);
                    g.drawImage(GlobalControl.getControl().getTitleBGround(),
                            i, edgeRect.y, Graphics.TOP|Graphics.LEFT);
                }
            }
            edgeRect=new Rectangle(displayRect.x,displayRect.y,
            		displayRect.width,
            		GlobalControl.getControl().getTitleBGround().getHeight());
            if((intersect=clip.calculateIntersection(edgeRect))!=null)
            {
            	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
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
        }
        contentPane.paint(g,clip);
        if(paintBorder)
        {
        	g.setClip(displayRect.x, displayRect.y, displayRect.width, displayRect.height);
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
            //Draw the outline
            //Set the color to outline color
            g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(Style.COMPONENT_OUTLINE_COLOR)).
                    intValue());
            g.drawRoundRect(displayRect.x, displayRect.y, 
            		displayRect.width-1, displayRect.height+radius, radius,
            		radius);
            g.drawLine(displayRect.x, displayRect.y+displayRect.height-1, displayRect.x+displayRect.width, displayRect.y+displayRect.height-1);
        }
    }
    public Rectangle getMinimumDisplayRect(int availWidth)
    {
        Rectangle minRect=contentPane.getMinimumDisplayRect(availWidth);
        minRect.width=availWidth;
        minRect.height=minRect.height;
        if(showTitleBar)
        {
        	minRect.height+=GlobalControl.getControl().getTitleBGround().getHeight();
        }
        return minRect;
    }
    public Panel getContentPane() {
		return contentPane;
	}
	public void setDisplayRect(Rectangle rect)
    {
		if(!displayRect.equals(rect))
		{
			displayRect=rect;
		}
        //set the three rects respectively
        //title bar rect
        if(showTitleBar)
        {
        	titlebarRect=new Rectangle();
            titlebarRect.x=this.displayRect.x;
            titlebarRect.y=this.displayRect.y;
            titlebarRect.width=this.displayRect.width;
            titlebarRect.height=GlobalControl.getControl().getTitleBGround().getHeight();
        }
        //body area rect
        bodyRect=new Rectangle();
        bodyRect.x=this.displayRect.x+1;
        bodyRect.width=this.displayRect.width-2;
        bodyRect.y=showTitleBar?titlebarRect.y+titlebarRect.height:displayRect.y;
        bodyRect.height=showTitleBar?this.displayRect.height-
        	(titlebarRect.height):this.displayRect.height;
        contentPane.setDisplayRect(bodyRect);
        setTitle(getTitle());
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
    	repaint(displayRect);
    	focussed=true;
    	contentPane.focusGained();
        int diff=titleWidth-displayRect.width;
        if(diff>0)
        {
            new Thread(new TitleScroller()).start();
        }
    }
    public void setParent(Item parent)
    {
    	this.parent=(Window)parent;
    }
    public Item getParent()
    {
        return parent;
    }
	public Rectangle getMenuBarRect() 
	{
		if(parent!=null)
		{
			return ((Window)parent).getMenuBarRect();
		}
		return new Rectangle();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title=title;
		titleChanged=true;
		scrolling=false;
		this.titleWidth=((Font)GlobalControl.getControl().getStyle().getProperty(
                Style.WINDOW_TITLE_FONT)).stringWidth(this.title);
		if(showTitleBar)
		{
			if(titleWidth>titlebarRect.width-2)
	        {
	            new Thread(new TitleScroller()).start();
	        }
	        else
	        {
	        	repaint(titlebarRect);
	        }
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
            //for as long as this window is focussed
            if(focussed)
            {
            	scrolling=true;
            }
            //the variable for the increment
            int increment=-2;
            while(focussed && scrolling)
            {
            	//calculate the between name and available display area
            	int diff=titleWidth-titlebarRect.width+2;
            	if(titleChanged)
            	{
            		titleChanged=false;
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
                if(!showTitleBar)
                {
                	break;
                }
                if(titleIndent<-diff || titleIndent>=0)
                {
                	repaint(PopUpWindow.this.titlebarRect);
                	try{Thread.sleep(1200);}catch(InterruptedException ie){}
                	titleIndent=0;
                	repaint(PopUpWindow.this.titlebarRect);
                	try{Thread.sleep(1500);}catch(InterruptedException ie){}
                }
                titleIndent+=increment;
                repaint(PopUpWindow.this.titlebarRect);
            }
            titleIndent=0;
        }
    }
	public void menuRightSelected() {
		
	}
	public void menuLeftSelected() {
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
