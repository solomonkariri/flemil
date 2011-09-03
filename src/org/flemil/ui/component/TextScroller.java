package org.flemil.ui.component;


import org.flemil.control.GlobalControl;
import org.flemil.ui.TextItem;



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
        if(textItem.getTextWidth()>textItem.getDisplayRect().width-2 && textItem.isFocussed())
        {
            textItem.setScrolling(true);
        }
        //the variable for the increment
        int increment=-GlobalControl.getTextScrollSpeed();
        while(textItem.isFocussed() && textItem.isScrolling())
        {
        	if(textItem.isTextChanged()){
        		textItem.setTextChanged(false);
        	}
        	if(textItem.getTextWidth()<=textItem.getDisplayRect().width-2)
            {
                break;
            }
        	//calculate the between name and available display area
        	int diff=textItem.getTextWidth()-textItem.getDisplayRect().width+2;
        	if(textItem.isTextChanged())
        	{
//        		textItem.setTextIndent(0);
        		if(diff<=0)
        		{
        			textItem.repaint(textItem.getDisplayRect());
        			break;
        		}
        	}
            
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
        textItem.setScrolling(false);
        if(textItem.getTextWidth()>textItem.getDisplayRect().width-2 
        		&& textItem.isFocussed())
        {
            textItem.setTextChanged(true);
            new Thread(new TextScroller(textItem)).start();
        }
        textItem.setTextIndent(0);
	}
}