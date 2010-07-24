package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.GaugeChangeListener;
import org.flemil.ui.Item;
import org.flemil.util.Rectangle;




/**
 * Class that represents a Gauge that gives a graphical display of an entity
 * as a ratio of a possible maximum
 * @author Solomon Kariri
 */
public class Gauge implements Item{
	private boolean focussed;
	private Rectangle displayRect;
	private Item parent;
	private boolean interactive;
	private boolean paintBorder;
	private GaugeChangeListener listener;
	private int stepSize;
	private int min;
	private int max;
	private int value;
	public boolean autosctolling;
	public static final int INDEFINITE=Integer.MIN_VALUE;
	
	/**
	 * Creates a new Gauge with a minimum value of 0, maximum value of 100
	 * a step size of 1 pixel and an initail value of 50.
	 */
	public Gauge()
	{
		this(0, 100, 1,50, true);
		displayRect=new Rectangle();
	}
	/**
	 * Creates a new Gauge with the specified attributes
	 * @param minValue the minimum value for this Gauge
	 * @param maxValue the maximum value for this Gauge
	 * @param stepSize the step size in pixels for this Gauge
	 * @param value the initial value for this Gauge
	 * @param interactive true if this Gauge responds to user actions
	 *  and false otherwise
	 */
	public Gauge(int minValue,int maxValue,int stepSize,int value,boolean interactive)
	{
		this.min=minValue;
		this.max=maxValue==Gauge.INDEFINITE?100:maxValue;
		this.stepSize=stepSize;
		this.interactive=interactive;
		this.value=value;
		if(maxValue==Gauge.INDEFINITE)
		{
			new Thread(new Worker()).start();
		}
	}
	public void focusGained() {
		if(interactive)
		{
			focussed=true;
		}
		repaint(displayRect);
	}

	public void focusLost() {
		if(interactive)
		{
			focussed=false;
		}
		repaint(displayRect);
	}
	/**
	 * Returns the GaugeChangeListener that is registered to listen to changes
	 * in this Gauge's value
	 * @return the GaugeChangeListener that is registered to listen to changes
	 * in this Gauge's value
	 */
	public GaugeChangeListener getListener() {
		return listener;
	}

	public Rectangle getDisplayRect() {
		return this.displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) {
		return new Rectangle(0,0,GlobalControl.getControl().getDisplayArea().width*3/4,
				((Font)GlobalControl.getControl().getStyle().
						getProperty(Style.ITEM_FONT)).getHeight()+2);
	}

	public Item getParent() {
		return this.parent;
	}

	public boolean isFocusible() {
		return interactive;
	}

	public boolean isPaintBorder() {
		return this.paintBorder;
	}

	public void keyPressedEvent(int keyCode) {
		if(!interactive)
		{
			parent.keyPressedEventReturned(keyCode);
			return;
		}
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.RIGHT:
            {
            	if(value>=max)value=max;
            	else
            	{
            		value=value+stepSize>=max?max:value+stepSize;
            		if(listener!=null)
                	{
                		listener.valueChanged(this);
                	}
            		repaint(displayRect);
            	}
            	break;
            }
            case Canvas.LEFT:
            {
            	if(value<min)value=min;
            	else
            	{
            		value=value-stepSize<min?min:value-stepSize;
            		if(listener!=null)
                	{
                		listener.valueChanged(this);
                	}
            		repaint(displayRect);
            	}
            	break;
            }
            default:
            {
            	if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
                break;
            }
        }
	}

	public void keyPressedEventReturned(int keyCode) {}

	public void keyReleasedEvent(int keyCode) {}

	public void keyReleasedEventReturned(int keyCode) {}

	public void keyRepeatedEvent(int keyCode) {
		keyPressedEvent(keyCode);
	}

	public void keyRepeatedEventReturned(int keyCode) {}

	public void paint(Graphics g, Rectangle clip) {
		if(displayRect.width<=1)return;
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	int ratio=(value*(displayRect.width))/max;
        	int ratio2=(value*(displayRect.width-8))/max;
        	g.setColor(((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_FOREGROUND)).intValue());
        	g.fillRoundRect(displayRect.x+1, displayRect.y+2, 
    				ratio, displayRect.height-4,
    				radius, radius);
    		g.setColor(((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue());
    		g.fillRoundRect(displayRect.x+ratio, displayRect.y+2, 
    				displayRect.width-ratio-1, displayRect.height-4,
    				radius, radius);
    		g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
    		g.drawRoundRect(displayRect.x+1, displayRect.y+2, 
    				displayRect.width-2, displayRect.height-4,
    				radius, radius);
    		if(interactive)
    		{
    			g.fillRoundRect(displayRect.x+ratio2, displayRect.y, 8, displayRect.height,
            			radius, radius);
    		}
    		g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y) {}

	public void pointerDraggedEventReturned(int x, int y) {}

	public void pointerPressedEvent(int x, int y) {}

	public void pointerPressedEventReturned(int x, int y) {}

	public void pointerReleasedEvent(int x, int y) {}

	public void pointerReleasedEventReturned(int x, int y) {}

	public void repaint(Rectangle clip) {
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}

	public void setDisplayRect(Rectangle rect) {
		this.displayRect=rect;
	}

	public void setFocusible(boolean focusible) {
		this.interactive=focusible;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}

	public void setParent(Item parent) {
		this.parent=parent;
	}
	/**
	 * Sets the class that is to be called when this Gauge's value changes
	 * @param listener class that is to be called when this Gauge's value changes
	 */
	public void setListener(GaugeChangeListener listener) {
		this.listener = listener;
	}
	/**
	 * Returns the current value of the Gauge which is a value between 
	 * min value and max value inclusive
	 * @return the current value of the Gauge
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Sets the current value of the Gauge. This should be a value 
	 * between minValue and maxValue inclusive
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
		repaint(displayRect);
	}
	public void setMaxValue(int maxValue)
	{
		if(maxValue==Gauge.INDEFINITE)
		{
			interactive=false;
			if(focussed)focusLost();
			new Thread(new Worker()).start();
		}
		else
		{
			autosctolling=false;
			max=maxValue;
		}
		repaint(displayRect);
	}
	class Worker implements Runnable
	{
		public void run()
		{
			if(autosctolling)
			return;
			autosctolling=true;
			while(autosctolling && parent!=null)
			{
				try
				{
					Thread.sleep(300);
				}
				catch(InterruptedException ie){}
				int newValue=value+stepSize>max?0:value+stepSize;
				setValue(newValue);
				repaint(displayRect);
			}
		}
	}
	public boolean isFocussed() {
		return focussed;
	}
}
