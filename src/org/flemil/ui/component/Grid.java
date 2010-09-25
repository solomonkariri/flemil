package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.Scrollable;
import org.flemil.util.Rectangle;





public class Grid implements Item
{
	public static final byte ROW_SELECTION=1;
	public static final byte COL_SELECTION=2;
	public static final byte CELL_SELECTION=3;
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
	private int[] distrib;
	private byte selectionMode=Grid.CELL_SELECTION;
	private boolean colWidsSet;
	
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
		distrib=new int[cols];
	}
	public void focusGained() {
		if(focussed)return;
		focussed=true;
		switch (selectionMode) {
		case Grid.CELL_SELECTION:{
			if(currentItem!=null){
				currentItem.focusGained();
			}
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(items[currentRow][currentCol].getDisplayRect(),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
			break;
		}
		case Grid.COL_SELECTION:
		{
			for(int i=0;i<rows;i++)
			{
				if(items[i][currentCol]!=null)
				{
					items[i][currentCol].focusGained();
				}
			}
			int curX=displayRect.x+1;
			for(int i=0;i<currentCol;i++)
			{
				curX+=(distrib[i]*(displayRect.height-items[0].length-2))/100;
			}
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(
		        		new Rectangle(curX,displayRect.y , 
		        				(distrib[currentCol]*(displayRect.height-items[0].length-2))/100, 
		        				displayRect.height),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
			break;
		}
		case Grid.ROW_SELECTION:
		{
			for(int i=0;i<cols;i++)
			{
				if(items[currentRow][i]!=null)
				{
					items[currentRow][i].focusGained();
				}
			}
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(
		        		new Rectangle(displayRect.x,displayRect.y+(rowHei*currentRow) ,
		        				displayRect.width,rowHei),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
		}
		break;
		}
		repaint(displayRect);
	}

	public void focusLost() {
		if(!focussed)return;
		focussed=false;
		switch (selectionMode) {
		case Grid.CELL_SELECTION:{
			if(currentItem!=null){
				currentItem.focusLost();
			}
			break;
		}
		case Grid.COL_SELECTION:
		{
			for(int i=0;i<rows;i++)
			{
				if(items[i][currentCol]!=null)
				{
					items[i][currentCol].focusLost();
				}
			}
		}
		case Grid.ROW_SELECTION:
		{
			for(int i=0;i<cols;i++)
			{
				if(items[currentRow][i]!=null)
				{
					items[currentRow][i].focusLost();
				}
			}
		}
		}
		repaint(displayRect);
	}
	private int getMinHeightForRow(int row,int availWid)
	{
		int max=0;
		for(int i=0;i<items[0].length;i++)
		{
			Item tmp=items[row][i];
			if(tmp!=null && tmp.getMinimumDisplayRect(distrib[i]*availWid/100).height>max)
			{
				max=tmp.getMinimumDisplayRect(distrib[i]*availWid/100).height;
			}
		}
		return max;
	}
	private void layoutItems()
	{
		if(displayRect.width<=1)return;
		rowHei=(displayRect.height-items.length-2)/items.length;
		int val=displayRect.width-items[0].length-2;
		int cumulative=0;
		for(int j=0;j<items[0].length;j++)
		{
			cumulative+=distrib[j]*val/100;
		}
		int diff=val-cumulative;
		for(int i=0;i<items.length;i++)
		{
			int cum=0;
			for(int j=0;j<items[i].length;j++)
			{
				if(items[i][j]!=null)
				{
					items[i][j].setDisplayRect(new Rectangle(displayRect.x+2+cum+j,
							displayRect.y+1+i*rowHei+i,
							distrib[j]*val/100,rowHei));
				}
				cum+=distrib[j]*val/100;
			}
			int track=items[i].length-1;
			int modulo=diff;
			while(modulo>0){
				for(int k=track+1;k<items[i].length;k++){
					items[i][k].getDisplayRect().x++;
				}
				items[i][track--].getDisplayRect().width++;
				if(track==-1)track=items[i].length-1;
				modulo--;
			}
		}
	}
	public Rectangle getDisplayRect() {
		return displayRect;
	}
	public void setColumnsDistribution(int[] distrib)
	{
		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
			this.distrib=distrib;
		}
		else{
			this.distrib=new int[distrib.length];
			for(int i=distrib.length-1;i>=0;i--){
				this.distrib[distrib.length-i-1]=distrib[i];
			}
		}
		colWidsSet=true;
		layoutItems();
	}
	public int[] getColumnsDistribution()
	{
		return distrib;
	}
	public int getSelectedRow()
	{
		return currentRow;
	}
	public int getSelectedColumn()
	{
		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
			return currentCol;
		}
		else{
			return cols-currentCol-1;
		}
	}
	public void setSelectedCell(int row,int col){
		if(currentItem!=null)currentItem.focusLost();
		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
			currentItem=items[row][col];
			currentRow=row;
			currentCol=col;
		}
		else{
			currentItem=items[row][cols-col-1];
			currentRow=row;
			currentCol=cols-col-1;
		}
		if(focussed){
			currentItem.focusGained();
		}
	}
	public void setSelectionMode(byte mode)
	{
		this.selectionMode=mode;
	}
	public byte getSelectionMode()
	{
		return this.selectionMode;
	}
	public Rectangle getMinimumDisplayRect(int availWidth) 
	{
		int wid=availWidth-items[0].length-2;
		if(!colWidsSet)
		{
			for(int i=0;i<cols;i++)
			{
				distrib[i]=100/items[0].length;
			}
			int modulo=100%items[0].length;
			int track=0;
			while(modulo>0){
				distrib[track++]++;
				track=track%items[0].length;
				modulo--;
			}
		}
		int maxRow=0;
		for(int i=0;i<items.length;i++)
		{
			int test=getMinHeightForRow(i, wid);
			if(test>maxRow)
			{
				maxRow=test;
			}
		}
		return new Rectangle(0,0,availWidth,
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
	
	public Item getItemAt(int row,int col){
		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
			return items[row][col];
		}
		else{
			return items[row][cols-col-1];
		}
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
	public synchronized void add(Item item)
	{
		outer: for(int i=0;i<items.length;i++)
		{
			if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
				for(int j=0;j<items[i].length;j++)
				{
					if(items[i][j]==null)
					{
						items[i][j]=item;
						itemsCount++;
						items[i][j].setParent(this);
						if(currentItem==null)
						{
							switch (selectionMode) {
							case Grid.CELL_SELECTION:
								if(item.isFocusible())
								{
									currentItem=item;
									currentRow=i;currentCol=j;
								}
								break;
							case Grid.ROW_SELECTION:
							case Grid.COL_SELECTION:
								currentItem=item;
								currentRow=i;currentCol=j;
								break;
							}
						}
						break outer;
					}
				}
			}
			else{
				for(int j=cols-1;j>=0;j--)
				{
					if(items[i][j]==null)
					{
						items[i][j]=item;
						itemsCount++;
						items[i][j].setParent(this);
						if(currentItem==null)
						{
							switch (selectionMode) {
							case Grid.CELL_SELECTION:
								if(item.isFocusible())
								{
									currentItem=item;
									currentRow=i;currentCol=j;
								}
								break;
							case Grid.ROW_SELECTION:
							case Grid.COL_SELECTION:
								currentItem=item;
								currentRow=i;currentCol=j;
								break;
							}
						}
						break outer;
					}
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
	public synchronized void remove(int row,int col)
	{
		if(LocaleManager.getTextDirection()==LocaleManager.getTextDirection()){
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
		else{
			if(currentItem==items[row][cols-col-1])
			{
				currentItem=null;
				if(cols-col-1<items[0].length-1)
				{
					currentItem=items[row][col+col];
				}
			}
			items[row][cols-col-1]=null;
			GlobalControl.getControl().refreshLayout();
		}
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
            	switch (selectionMode) {
				case Grid.CELL_SELECTION:
				{
					int previousRow=currentRow;
					while(currentRow<items.length-1 && 
	            			items[currentRow+1][currentCol]!=null)
	        		{
						 if(!items[currentRow+1][currentCol].isFocusible()){
							 boolean test=false;
							 for(int i=currentCol-1;i>=0;i--){
								 if(items[currentRow+1][i].isFocusible()){
									 currentCol=i;
									 test=true;
									 break;
								 }
							 }
							 if(!test){
								 for(int i=currentCol+1;i<items[0].length;i++){
									 if(items[currentRow+1][i].isFocusible()){
										 currentCol=i;
										 test=true;
										 break;
									 }
								 } 
							 }
							 if(!test){
								 currentRow++; 
							 }
							 else{
								 break;
							 }
						 }
						 else{
							 break;
						 }
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
	        			currentRow=previousRow;
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.ROW_SELECTION:
				{
					if(currentRow<items.length-1)
					{
						for(int i=0;i<cols;i++)
						{
							if(items[currentRow][i]!=null)
							{
								items[currentRow][i].focusLost();
							}
							if(focussed){
								if(items[currentRow+1][i]!=null)
								{
									items[currentRow+1][i].focusGained();
								}
		            		}
						}
						currentRow++;
	        			currentItem=items[currentRow][0];
	            		updateFocus();
					}
	        		else
	        		{
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.COL_SELECTION:
				{
					parent.keyPressedEventReturned(keyCode);
	        		break;
				}
				}
                break;
            }
            case Canvas.UP:
            {
            	switch (selectionMode) {
				case Grid.CELL_SELECTION:
				{
					int previousRow=currentRow;
					while(currentRow>0 && items[currentRow-1][currentCol]!=null)
	        		{
						if(!items[currentRow-1][currentCol].isFocusible()){
							 boolean test=false;
							 for(int i=currentCol-1;i>=0;i--){
								 if(items[currentRow-1][i].isFocusible()){
									 currentCol=i;
									 test=true;
									 break;
								 }
							 }
							 if(!test){
								 for(int i=currentCol+1;i<items[0].length;i++){
									 if(items[currentRow-1][i].isFocusible()){
										 currentCol=i;
										 test=true;
										 break;
									 }
								 } 
							 }
							 if(!test){
								 currentRow--; 
							 }
							 else{
								 break;
							 }
						 }
						 else{
							 break;
						 }
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
	        			currentRow=previousRow;
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.ROW_SELECTION:
				{
					if(currentRow>0)
					{
						for(int i=0;i<cols;i++)
						{
							if(items[currentRow][i]!=null)
							{
								items[currentRow][i].focusLost();
							}
							if(focussed){
								if(items[currentRow-1][i]!=null)
								{
									items[currentRow-1][i].focusGained();
								}
		            		}
						}
						currentRow--;
	        			currentItem=items[currentRow][0];
	            		updateFocus();
					}
	        		else
	        		{
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.COL_SELECTION:
				{
					parent.keyPressedEventReturned(keyCode);
	        		break;
				}
				}
            	break;
            }
            case Canvas.RIGHT:
            {
            	switch (selectionMode) {
				case Grid.CELL_SELECTION:
				{
					int track=currentCol;
					while(track<items[0].length-1 && items[currentRow][track+1]!=null
	        				&& !items[currentRow][track+1].isFocusible())
	        		{
						track++;
	        		}
					if(track<items[0].length-1)track++;
	        		if(items[currentRow][track]!=null && items[currentRow][track].isFocusible())
	        		{
	        			currentCol=track;
						if(currentItem!=null)currentItem.focusLost();
	        			currentItem=items[currentRow][currentCol];
	            		if(focussed)currentItem.focusGained();
	            		updateFocus();
	        		}
	        		else
	        		{
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.ROW_SELECTION:
				{
					parent.keyPressedEventReturned(keyCode);
	        		break;
				}
				case Grid.COL_SELECTION:
				{
					if(currentCol<cols-1)
					{
						for(int i=0;i<rows;i++)
						{
							if(items[i][currentCol]!=null)
							{
								items[i][currentCol].focusLost();
							}
							if(focussed && items[i][currentCol+1]!=null)
							{
								items[i][currentCol+1].focusGained();
							}
						}
						currentCol++;
						currentItem=items[0][currentCol];
					}
	        		break;
				}
				}
            	break;
            }
            case Canvas.LEFT:
            {
            	switch (selectionMode) {
				case Grid.CELL_SELECTION:
				{
					int track=currentCol;
					while(track>0 && items[currentRow][track-1]!=null
	        				&& !items[currentRow][track-1].isFocusible())
	        		{
	        			track--;
	        		}
					if(track>0)track--;
					if(items[currentRow][track]!=null && items[currentRow][track].isFocusible())
					{
						currentCol=track;
						if(currentItem!=null)currentItem.focusLost();
	        			currentItem=items[currentRow][currentCol];
	            		if(focussed)currentItem.focusGained();
	            		updateFocus();
					}
	        		else
	        		{
	        			parent.keyPressedEventReturned(keyCode);
	        		}
	        		break;
				}
				case Grid.ROW_SELECTION:
				{
					parent.keyPressedEventReturned(keyCode);
	        		break;
				}
				case Grid.COL_SELECTION:
				{
					if(currentCol>0)
					{
						for(int i=0;i<rows;i++)
						{
							if(items[i][currentCol]!=null)
							{
								items[i][currentCol].focusLost();
							}
							if(focussed && items[i][currentCol-1]!=null)
							{
								items[i][currentCol-1].focusGained();
							}
						}
						currentCol--;
						currentItem=items[0][currentCol];
					}
	        		break;
				}
				}
            	break;
            }
            default:
            {
            	parent.keyPressedEventReturned(keyCode);
            }
        }
        repaint(displayRect);
	}
	
	//fewer code
	private void updateFocus()
	{
		switch (selectionMode) {
		case Grid.CELL_SELECTION:
			if(items[currentRow][currentCol]!=null)
			{
				items[currentRow][currentCol].focusGained();
				if(displayRect.width>1 && parent instanceof Scrollable)
			        ((Scrollable)parent).scrollRectToVisible(items[currentRow][currentCol].getDisplayRect(),
			        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
			}
			break;
		case Grid.COL_SELECTION:
		{
			for(int i=0;i<rows;i++)
			{
				if(items[i][currentCol]!=null)
				{
					items[i][currentCol].focusGained();
				}
			}
			int curX=displayRect.x;
			for(int i=0;i<currentCol;i++)
			{
				curX+=(distrib[i]*displayRect.height)/100;
			}
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(
		        		new Rectangle(curX,displayRect.y , (distrib[currentCol]*displayRect.height)/100, 
		        				displayRect.height),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
		}
		case Grid.ROW_SELECTION:
		{
			for(int i=0;i<cols;i++)
			{
				if(items[currentRow][i]!=null)
				{
					items[currentRow][i].focusGained();
				}
			}
			if(displayRect.width>1 && parent instanceof Scrollable)
		        ((Scrollable)parent).scrollRectToVisible(
		        		new Rectangle(displayRect.x,displayRect.y+(rowHei*currentRow) ,
		        				displayRect.width,rowHei),
		        		Scrollable.DIRECTION_X|Scrollable.DIRECTION_Y);
		}
		}
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
        	if(focussed)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().getProperty(
            			Style.COMPONENT_FOCUS_BACKGROUND)).intValue());
            	switch (selectionMode) {
        		case Grid.COL_SELECTION:
        		{
        			int curX=displayRect.x+1;
        			for(int i=0;i<currentCol;i++)
        			{
        				curX+=(distrib[i]*displayRect.width)/100;
        			}
        			g.fillRect(curX+currentCol, displayRect.y+1, 
        					(distrib[currentCol]*(displayRect.width-items[0].length-2))/100, 
        					displayRect.height-2-items.length);
        			break;
        		}
        		case Grid.ROW_SELECTION:
        			g.fillRect(displayRect.x+1, displayRect.y+(currentRow)*rowHei+currentRow, displayRect.width-2, 
        					rowHei+1);
        			break;	
        		}
        	}
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

	public void pointerPressedEvent(int x, int y) {
	}

	public void pointerPressedEventReturned(int x, int y) {}

	public void pointerReleasedEvent(int x, int y) {
		if(displayRect.contains(x, y, 0)){
			for(int i=0;i<rows;i++){
				for(int j=0;j<cols;j++){
					Item testItem=getItemAt(i, j);
					if(testItem.getDisplayRect().contains(x, y, 0)){
						if(testItem.isFocusible()){
							currentItem.focusLost();
							currentItem=testItem;
							currentRow=i;
							currentCol=j;
							currentItem.focusGained();
						}
						testItem.pointerReleasedEvent(x, y);
						return;
					}
				}
			}
		}
	}

	public void pointerReleasedEventReturned(int x, int y) {
		parent.pointerReleasedEventReturned(x, y);
	}

	public void repaint(Rectangle clip) {
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}

	public synchronized void setDisplayRect(Rectangle rect) {
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
	public boolean isFocussed() {
		return focussed;
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				if(items[i][j]!=null){
					items[i][j].moveRect(dx, dy);
				}
			}
		}
	}
	public int getRowCount() {
		return rows;
	}
	public int getColCount() {
		return cols;
	}
}