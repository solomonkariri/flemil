package com.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


import com.flemil.control.GlobalControl;
import com.flemil.control.Style;
import com.flemil.ui.Item;
import com.flemil.ui.Scrollable;
import com.flemil.util.Rectangle;


public class Grid implements Scrollable 
{
	int rows;
	int cols;
	private Item [][]items=null;
	private Item currentItem;
	private Rectangle displayRect;
	private Item parent;
	private boolean paintBorder;
	private boolean focusible=true;
	private int currentRow;
	private int currentCol;
	private int itemsCount;
	private boolean paintGrid;
	private int colWid;
	private int rowHei;
	private boolean focussed;
	
	/**
	 * Creates a new Grid with the specified number of rows and columns
	 * @param rows the number of rows for this Grid
	 * @param cols the number of columns for this Grid
	 */
	public Grid(int rows,int cols)
	{
		this.rows=rows;
		this.cols=cols;
		items=new Item[rows][cols];
		displayRect=new Rectangle();
	}
	public void focusGained() {
		if(focussed)return;
		focussed=true;
		if(currentItem!=null)
		{
			currentItem.focusGained();
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(currentItem.getDisplayRect(),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
		}
		repaint(displayRect);
	}

	public void focusLost() {
		focussed=false;
		if(currentItem!=null)
		{
			currentItem.focusLost();
		}
	}
	private int getMinWidthForColumn(int col,int availWid)
	{
		int max=0;
		for(int i=0;i<items.length;i++)
		{
			Item tmp=items[i][col];
			if(tmp!=null && tmp.getMinimumDisplayRect(availWid).width>max)
			{
				max=tmp.getMinimumDisplayRect(availWid).width;
			}
		}
		return max;
	}
	private int getMinHeightForRow(int row,int availWid)
	{
		int max=0;
		for(int i=0;i<items[0].length;i++)
		{
			Item tmp=items[row][i];
			if(tmp!=null && tmp.getMinimumDisplayRect(availWid).height>max)
			{
				max=tmp.getMinimumDisplayRect(availWid).height;
			}
		}
		return max;
	}
	private void layoutItems()
	{
		colWid=(displayRect.width-items[0].length-2)/items[0].length;
		rowHei=(displayRect.height-items.length-2)/items.length;
		for(int i=0;i<items.length;i++)
		{
			for(int j=0;j<items[i].length;j++)
			{
				if(items[i][j]!=null)
				{
					items[i][j].setDisplayRect(new Rectangle(displayRect.x+1+j*colWid+j,
							displayRect.y+1+i*rowHei+i,
							colWid,rowHei));
				}
			}
		}
	}
	public Rectangle getDisplayRect() {
		return displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) 
	{
		int wid=availWidth-itemsCount-2;
		int widPerColumn=wid/items[0].length;
		int maxRow=0;
		int maxCol=0;
		for(int i=0;i<items[0].length;i++)
		{
			int test=getMinWidthForColumn(i, widPerColumn);
			if(test>maxCol)
			{
				maxCol=test;
			}
		}
		for(int i=0;i<items.length;i++)
		{
			int test=getMinHeightForRow(i, widPerColumn);
			if(test>maxRow)
			{
				maxRow=test;
			}
		}
		return new Rectangle(0,0,maxCol*items[0].length+items[0].length,
				maxRow*items.length+items.length);
	}
	/**
	 * Returns the currently selected Item in this Grid
	 * @return the currently selected Item in this Grid
	 */
	public Item getCurrentItem()
	{
		return currentItem;
	}
	public Item getParent() {
		return parent;
	}

	public boolean isFocusible() {
		if(currentItem==null)
			return false;
		else
			return focusible;
	}

	public boolean isPaintBorder() {
		return paintBorder;
	}
	/**
	 * Adds an item to this grid. The items are added to the Grid one row at 
	 * a time until the row gets full when the addition progresses to the next row
	 * and so on.
	 * @param item the Item to be added to this Grid
	 */
	public void add(Item item)
	{
		outer: for(int i=0;i<items.length;i++)
		{
			for(int j=0;j<items[i].length;j++)
			{
				if(items[i][j]==null)
				{
					items[i][j]=item;
					itemsCount++;
					items[i][j].setParent(this);
					if(currentItem==null && item.isFocusible())
					{
						currentItem=item;
						currentRow=i;currentCol=j;
					}
					break outer;
				}
			}
		}
		layoutItems();
	}
	/**
	 * Removes the item at the specified row and column from this Grid. 
	 * The items in the Grid are repackaged to fill in the topmost rows of
	 * the Grid. Rows start at index zero and progress to row-count-1 as well as the 
	 * columns
	 * @param row  the row from which to delete a cmponent
	 * @param col the column from which to delete an item
	 */
	public void remove(int row,int col)
	{
		if(currentItem==items[row][col])
		{
			currentItem=null;
			if(col<items[0].length-1)
			{
				currentItem=items[row][col+1];
			}
			else if(col==items[0].length-1 && items[0].length>1)
			{
				currentItem=items[row][col-1];
			}
		}
		items[row][col]=null;
		GlobalControl.getControl().refreshLayout();
	}
	public void keyPressedEvent(int keyCode) {
		if(currentItem!=null)
    	{
    		currentItem.keyPressedEvent(keyCode);
    	}
    	else
    	{
    		if(parent!=null)
    		{
    			parent.keyPressedEventReturned(keyCode);
    		}
    	}
	}
	public void keyPressedEventReturned(int keyCode) {
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.DOWN:
            {
            	while(currentRow<items.length-1 && 
            			items[currentRow+1][currentCol]!=null &&
        				!items[currentRow+1][currentCol].isFocusible())
        		{
        			currentRow++;
        		}
        		if(currentRow<items.length-1 && items[currentRow+1][currentCol]!=null)
        		{
        			if(currentItem!=null)currentItem.focusLost();
        			currentItem=items[currentRow+1][currentCol];
            		if(focussed)currentItem.focusGained();
            		currentRow++;
            		updateFocus();
        		}
        		else
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
                break;
            }
            case Canvas.UP:
            {
            	while(currentRow>0 && items[currentRow-1][currentCol]!=null
        				&& !items[currentRow-1][currentCol].isFocusible())
        		{
        			currentRow--;
        		}
        		if(currentRow>0 && items[currentRow-1][currentCol]!=null)
        		{
        			if(currentItem!=null)currentItem.focusLost();
        			currentItem=items[currentRow-1][currentCol];
            		if(focussed)currentItem.focusGained();
            		currentRow--;
            		updateFocus();
        		}
        		else
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
            	break;
            }
            case Canvas.RIGHT:
            {
            	while(currentCol<items[0].length-1 && items[currentRow][currentCol+1]!=null
        				&& !items[currentRow][currentCol+1].isFocusible())
        		{
        			currentCol++;
        		}
        		if(currentCol<items[0].length-1 && items[currentRow][currentCol+1]!=null)
        		{
        			if(currentItem!=null)currentItem.focusLost();
        			currentItem=items[currentRow][currentCol+1];
            		if(focussed)currentItem.focusGained();
            		currentCol++;
            		updateFocus();
        		}
        		else
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
            	break;
            }
            case Canvas.LEFT:
            {
            	while(currentCol>0 && items[currentRow][currentCol-1]!=null
        				&& !items[currentRow][currentCol-1].isFocusible())
        		{
        			currentCol--;
        		}
        		if(currentCol>0 && items[currentRow][currentCol-1]!=null)
        		{
        			if(currentItem!=null)currentItem.focusLost();
        			currentItem=items[currentRow][currentCol-1];
            		if(focussed)currentItem.focusGained();
            		currentCol--;
            		updateFocus();
        		}
        		else
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
            	break;
            }
            default:
            {
            	parent.keyPressedEventReturned(keyCode);
            }
        }
	}
	
	//fewer code
	private void updateFocus()
	{
        if(parent instanceof Scrollable && focussed)
			((Scrollable)parent).scrollRectToVisible(currentItem.getDisplayRect(), 
					Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
	}
	
	public void keyReleasedEvent(int keyCode) {}

	public void keyReleasedEventReturned(int keyCode) {}

	public void keyRepeatedEvent(int keyCode) {}

	public void keyRepeatedEventReturned(int keyCode) {}

	public void paint(Graphics g, Rectangle clip) {
		Rectangle intersect=null;
        if((intersect=this.displayRect.calculateIntersection(clip))!=null)
        {
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	for(int i=0;i<items.length;i++)
        	{
        		for(int j=0;j<items[i].length;j++)
        		{
        			if(items[i][j]!=null)
        			{
        				items[i][j].paint(g, intersect);
        			}
        		}
        	}
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	if(paintBorder)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		int radius=((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.CURVES_RADIUS)).intValue();
        		g.drawRoundRect(displayRect.x, displayRect.y, 
        				displayRect.width-1, displayRect.height-1,
        				radius, radius);
        	}
        	if(paintGrid)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_OUTLINE_COLOR)).intValue());
        		for(int i=1;i<items.length;i++)
        		{
        			g.drawLine(displayRect.x, displayRect.y+i*rowHei+i, 
        					displayRect.x+displayRect.width, 
        					displayRect.y+i*rowHei+i);
        		}
        		for(int j=1;j<items[0].length;j++)
        		{
        			g.drawLine(displayRect.x+j*colWid+j, 
        					displayRect.y, displayRect.x+j*colWid+j, 
        					displayRect.y+displayRect.height);
        		}
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
		layoutItems();
	}

	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void setPaintBorder(boolean paint) {
		paintBorder=paint;
	}
	public void setParent(Item parent) {
		this.parent=parent;
	}
	/**
	 * Sets whether this grid should paint the lines that mark its grid
	 * @param paintGrid true to paint the grid and false otherwise
	 */
	public void setPaintGrid(boolean paintGrid) {
		this.paintGrid = paintGrid;
	}
	/**
	 * Checks whether this Grid is painting its grid lines
	 * @return true if repainting and false otherwise
	 */
	public boolean isPaintGrid() {
		return paintGrid;
	}
	public boolean isHorScrolling() {
		return false;
	}
	public void scrollRectToVisible(Rectangle rect, int scrollDirection) {
		if(parent instanceof Scrollable)
			((Scrollable)parent).scrollRectToVisible(rect, scrollDirection);
	}
	public void setHorScrolling(boolean horScrolling) {
	}
}
