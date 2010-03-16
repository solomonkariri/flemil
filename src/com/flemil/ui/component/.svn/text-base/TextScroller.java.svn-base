package com.flemil.ui.component;


import com.flemil.control.GlobalControl;
import com.flemil.ui.TextItem;


public class TextScroller implements Runnable{
	private TextItem textItem;
	public TextScroller(TextItem textItem)
	{
		this.textItem=textItem;
	}
	public void run()
	{
		if(textItem.isScrolling() || textItem.getDisplayRect().width<=1)
        {
            return;
        }
        //for as long as this window is focussed
        textItem.setScrolling(true);
        //the variable for the increment
        int increment=-GlobalControl.getTextScrollSpeed();
        while(textItem.isFocussed() && textItem.isScrolling())
        {
            //calculate the between name and available display area
            int diff=textItem.getTextWidth()-textItem.getDisplayRect().width;
            try
            {
                Thread.sleep(100);
            }catch(InterruptedException ie){}
            if(textItem.getTextIndent()<-diff || textItem.getTextIndent()>=0)
            {
            	textItem.repaint(textItem.getDisplayRect());
            	try{Thread.sleep(1200);}catch(InterruptedException ie){}
            	textItem.setTextIndent(0);
            	textItem.repaint(textItem.getDisplayRect());
            	try{Thread.sleep(1500);}catch(InterruptedException ie){}
            }
            textItem.setTextIndent(textItem.getTextIndent()+increment);
            textItem.repaint(textItem.getDisplayRect());
        }
        textItem.setTextIndent(0);
        textItem.setScrolling(false);
	}
}
