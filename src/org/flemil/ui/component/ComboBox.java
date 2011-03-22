package org.flemil.ui.component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.ComboBoxListener;
import org.flemil.event.ListSelectionListener;
import org.flemil.event.MenuCommandListener;
import org.flemil.event.TextItemListener;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.util.Rectangle;




public class ComboBox implements TextItem {

	private Label nameDisplayer;
	private boolean focussed;
	private Rectangle displayRect;
	private Item parent;
	private boolean focusible=true;
	private boolean paintBorder=true;
	private Image downImage;
	private int selectedIndex=-1;
	private Object currentObject=null;
	private Vector elements=new Vector();
	private MenuItem selectItem;
	private MenuItem cancelItem;
	private boolean textChanged;
	private ComboBoxListener listener;
	
	public TextItemListener getTextListener() {
		return nameDisplayer.getTextListener();
	}

	public void setTextListener(TextItemListener textListener) {
		nameDisplayer.setTextListener(textListener);
	}

	/**
	 * Creates a new ComboBox with no entries
	 */
	public ComboBox()
	{
		nameDisplayer=new Label("");
		nameDisplayer.setFocusible(true);
		nameDisplayer.setTextWraps(false);
		displayRect=new Rectangle();
	}
	
	public ComboBox(Object []items){
		this();
		setElements(items);
	}
	
	public void setElements(Object [] elements){
		synchronized (this) {
			selectedIndex=-1;
			this.elements.removeAllElements();
			for(int i=0;i<elements.length;i++){
				this.elements.addElement(elements[i]);
			}
			if(!this.elements.isEmpty()){
				selectedIndex=0;
			}
		}
	}
	
	/**
	 * Adds an entry to the this ComboBox. The string displayed for this entry
	 * will be the value returned by the entry's toString() method call
	 * @param entry entry to be added to this ComboBox
	 */
	public void add(Object entry)
	{
		if(elements.isEmpty())
		{
			currentObject=entry;
			selectedIndex=0;
		}
		elements.addElement(entry);
		nameDisplayer.setText(currentObject.toString());
	}
	/**
	 * Removes the item at the specified index from this ComboBox
	 * @param index the index at which to delete an entry
	 */
	public void remove(int index)
	{
		remove(elements.elementAt(index));
	}
	/**
	 * Removes the specified entry from this ComboBox
	 * @param entry the entry to be removed from this ComboBox
	 */
	public void remove(Object entry)
	{
		if(elements.contains(entry))
		{
			int index=elements.indexOf(entry);
			elements.removeElement(entry);
			if(index<=selectedIndex && !elements.isEmpty())
			{
				if(index!=0)
				{
					selectedIndex-=1;
				}
				currentObject=elements.elementAt(selectedIndex);
				nameDisplayer.setText(currentObject.toString());
			}
			if(elements.isEmpty())
			{
				nameDisplayer.setText("");
				currentObject=null;
				selectedIndex=-1;
			}
		}
	}
	/**
	 * Sets the Entry that is currently select for this ComboBox
	 * @param entry the Entry to be the currently selected for this ComboBox
	 */
	public void setSelectedEntry(Object entry)
	{
		if(elements.contains(entry))
		{
			int index=elements.indexOf(entry);
			currentObject=elements.elementAt(index);
			selectedIndex=index;
			nameDisplayer.setText(currentObject.toString());
			if(listener!=null){
				listener.selectionChanged(this);
			}
		}
	}
	/**
	 * Sets the entry at the specified index as the currently selected
	 * 
	 * @param index the index in this ComboBox of the entry to be selected
	 */
	public void setSelectedIndex(int index)
	{
		setSelectedEntry(elements.elementAt(index));
	}
	public void focusGained() 
	{
		focussed=true;
        nameDisplayer.focusGained();
        repaint(displayRect);
	}
	public void focusLost() {
		focussed=false;
		nameDisplayer.focusLost();
		repaint(displayRect);
	}
	public Rectangle getDisplayRect() {
		return displayRect;
	}
	public Rectangle getMinimumDisplayRect(int availWidth) {
		synchronized (this) {
			int boxWid=nameDisplayer.getFont().getHeight()+2;
			Rectangle tmp=nameDisplayer.getMinimumDisplayRect(availWidth-boxWid);
			tmp.width=availWidth;
			tmp.height=nameDisplayer.getFont().getHeight()+6;
			return tmp;
		}
	}

	public Item getParent() {
		return this.parent;
	}
	public boolean isFocusible() {
		return this.focusible;
	}

	public boolean isPaintBorder() {
		return this.paintBorder;
	}
	
	private void showSelectionPopup()
	{
		if(elements.isEmpty())return;
		ChoiceGroup grp=new ChoiceGroup();
		final List list=new List();
		list.setWraps(true);
		list.setPaintBorder(false);
		Enumeration en=elements.elements();
		final PopUpWindow pop=new PopUpWindow("",false);
		pop.getContentPane().add(list);
		int track=0;
		while(en.hasMoreElements())
		{
			Object item=en.nextElement();
			RadioButton rad=new RadioButton(item.toString());
			rad.setSelectable(false);
			rad.setTextWraps(nameDisplayer.isTextWraps());
			grp.add(rad);
			list.add(rad);
			if(selectedIndex==track)rad.setSelected(true);
			track++;
		}
		selectItem=new MenuItem("Select");
		cancelItem=new MenuItem("Cancel");
		selectItem.setListener(new MenuCommandListener(){
			public void commandAction(MenuItem item) {
				setSelectedIndex(selectedIndex=list.getSelectedIndex());
				nameDisplayer.setText(elements.elementAt(selectedIndex).toString());
				pop.getMenu().remove(selectItem);
				pop.getMenu().remove(cancelItem);
				GlobalControl.getControl().getCurrent().hidePopup(
						GlobalControl.getControl().getCurrent().getCurrentPopup());
				repaint(displayRect);
			}
		});
		cancelItem.setListener(new MenuCommandListener(){
			public void commandAction(MenuItem item) {
				pop.getMenu().remove(selectItem);
				pop.getMenu().remove(cancelItem);
				GlobalControl.getControl().getCurrent().hidePopup(
						GlobalControl.getControl().getCurrent().getCurrentPopup());
			}
		});
		pop.getMenu().add(cancelItem);
		pop.getMenu().add(selectItem);
		GlobalControl.getControl().getCurrent().showPopUp(pop);
		list.setSelectedIndex(selectedIndex);
		list.setListener(new ListSelectionListener(){

			public void itemSelected(List source) {
				setSelectedIndex(selectedIndex=source.getSelectedIndex());
				GlobalControl.getControl().getCurrent().hidePopup(
						GlobalControl.getControl().getCurrent().getCurrentPopup());
				repaint(displayRect);
			}
		});
	}
	
	public void keyPressedEvent(int keyCode) {
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.FIRE:
            {
            	repaint(displayRect);
				showSelectionPopup();
				break;
            }
            default:
            {
            	if(parent!=null)
        		{
        			parent.keyPressedEventReturned(keyCode);
        		}
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
        	if(focussed)
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue());
        	}
        	else
        	{
        		g.setColor(((Integer)GlobalControl.getControl().getStyle().
        				getProperty(Style.COMPONENT_BACKGROUND)).intValue());
        	}
        	g.fillRoundRect(displayRect.x, displayRect.y, 
        			displayRect.width-1, displayRect.height-1,
        			radius, radius);
        	if(paintBorder){
        		g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
            			getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue():
            				((Integer)GlobalControl.getControl().getStyle().
            						getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
            	g.drawRoundRect(displayRect.x, displayRect.y, 
            			displayRect.width-1, displayRect.height-1,
            			radius, radius);
        	}
        	nameDisplayer.paint(g, intersect);
        	//dar the circle
        	g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_OUTLINE_COLOR)).intValue());
        	if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
        		if(paintBorder){
        			g.drawLine(displayRect.x+displayRect.width-nameDisplayer.getFont().getHeight()-2,
                			displayRect.y, 
                			displayRect.x+displayRect.width-nameDisplayer.getFont().getHeight()-2, 
                			displayRect.y+displayRect.height);
        		}
            	g.drawImage(downImage, 
            			displayRect.x+displayRect.width-nameDisplayer.getFont().getHeight(), 
            			displayRect.y+displayRect.height/2-downImage.getHeight()/2,
            			Graphics.TOP|Graphics.LEFT);
        	}
        	else{
        		if(paintBorder){
        			g.drawLine(displayRect.x+nameDisplayer.getFont().getHeight()+2,
                			displayRect.y, 
                			displayRect.x+nameDisplayer.getFont().getHeight()+2, 
                			displayRect.y+displayRect.height);
        		}
            	g.drawImage(downImage, 
            			displayRect.x+1, 
            			displayRect.y+displayRect.height/2-downImage.getHeight()/2,
            			Graphics.TOP|Graphics.LEFT);
        	}
            g.setClip(clip.x, clip.y, clip.width, clip.height);
        }
	}

	public void pointerDraggedEvent(int x, int y) {}

	public void pointerDraggedEventReturned(int x, int y) {}

	public void pointerPressedEvent(int x, int y) {
		
	}

	public void pointerPressedEventReturned(int x, int y) {}

	public void pointerReleasedEvent(int x, int y){
		if(displayRect.contains(x, y, 0)){
			keyPressedEvent(GlobalControl.getControl().
					getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
		}
	}
	public void pointerReleasedEventReturned(int x, int y){
		parent.pointerReleasedEventReturned(x, y);
	}

	public void repaint(Rectangle clip) {
		if(parent!=null)
		{
			parent.repaint(clip);
		}
	}
	public void setDisplayRect(Rectangle rect) {
		if(rect.height!=displayRect.height)
		{
			int imgWidth=nameDisplayer.getFont().getHeight()-2;
    		try
    		{
    			downImage=GlobalControl.getImageFactory().scaleImage(Image.createImage("/arrow.png"), imgWidth,
    					imgWidth, Sprite.TRANS_ROT270);
    		}catch(IOException ioe){
//    			ioe.printStackTrace();
    			}
		}
		this.displayRect=rect;
		if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
			nameDisplayer.setDisplayRect(new Rectangle(
					displayRect.x,
					displayRect.y,
					displayRect.width-nameDisplayer.getFont().getHeight()-2,
					displayRect.height));
		}
		else{
			nameDisplayer.setDisplayRect(new Rectangle(
					displayRect.x+nameDisplayer.getFont().getHeight()+2,
					displayRect.y,
					displayRect.width-(nameDisplayer.getFont().getHeight()+2),
					displayRect.height));
		}
	}
	public void setFocusible(boolean focusible) {
		this.focusible=focusible;
	}

	public void setPaintBorder(boolean paint) {
		this.paintBorder=paint;
	}

	public void setParent(Item parent) {
		this.parent=parent;
		nameDisplayer.setParent(this);
	}
	public byte getAlignment() {
		return this.nameDisplayer.getAlignment();
	}
	public int getSelectedIndex(){
		return selectedIndex;
	}
	public Font getFont() {
		return nameDisplayer.getFont();
	}
	public boolean isTextWraps() {
		return nameDisplayer.isTextWraps();
	}
	public void setAlignment(byte alignment) {
		nameDisplayer.setAlignment(alignment);
	}
	public void setFont(Font font) {
		nameDisplayer.setFont(font);
	}
	public void setTextWraps(boolean textWraps) {
		nameDisplayer.setTextWraps(textWraps);
	}

	public String getText() {
		return currentObject.toString();
	}

	public void setText(String text) {
		for(int i=0;i<elements.size();i++){
			if(elements.elementAt(i).toString().equals(text)){
				setSelectedIndex(i);
				return;
			}
		}
	}
	public void resetFont() {
		this.nameDisplayer.resetFont();
	}
	public int getTextIndent() {
		return nameDisplayer.getTextIndent();
	}

	public int getTextWidth() {
		return nameDisplayer.getTextWidth();
	}

	public boolean isFocussed() {
		return nameDisplayer.isFocussed();
	}

	public boolean isScrolling() {
		synchronized (this) {
			return nameDisplayer.isScrolling();
		}
	}

	public void setScrolling(boolean scrolling) {
		synchronized (this) {
			nameDisplayer.setScrolling(scrolling);
		}
	}

	public void setTextIndent(int indent) {
		nameDisplayer.setTextIndent(indent);
	}
	public void moveRect(int dx, int dy) {
		displayRect.x+=dx;
		displayRect.y+=dy;
		nameDisplayer.moveRect(dx, dy);
	}

	public void removeAll() {
		currentObject=null;
		elements.removeAllElements();
		repaint(displayRect);
	}

	public void setTextChanged(boolean textChanged) {
		this.textChanged = textChanged;
	}

	public boolean isTextChanged() {
		return textChanged;
	}

	public void setListener(ComboBoxListener listener) {
		this.listener = listener;
	}

	public ComboBoxListener getListener() {
		return listener;
	}
} 