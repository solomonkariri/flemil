package org.flemil.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.flemil.control.GlobalControl;
import org.flemil.control.Style;
import org.flemil.event.MenuCommandListener;
import org.flemil.event.TextItemListener;
import org.flemil.i18n.LocaleManager;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.ui.Window;
import org.flemil.util.Rectangle;




/**
 * Class that represents a RadioButton
 * @author Solomon Kariri
 *
 */
public class RadioButton implements TextItem 
{
	private Label nameDisplayer;
	private boolean focussed;
	private Rectangle displayRect;
	private Item parent;
	private boolean focusible=true;
	private boolean paintBorder=true;
	private boolean selected;
	private MenuItem markItem;
	private ChoiceGroup group;
	private boolean selectable=true;
	private boolean textChanged;
	private boolean addedItems;
	private Menu addMenu;

	public TextItemListener getTextListener() {
		return nameDisplayer.getTextListener();
	}
	public void setTextListener(TextItemListener textListener) {
		nameDisplayer.setTextListener(textListener);
	}
	/**
	 * Sets the text.name of this RadioButton
	 * @param text the name of this RadioButton
	 */
	public RadioButton(String text)
	{
		nameDisplayer=new Label(text);
		nameDisplayer.setFocusible(true);
		displayRect=new Rectangle();
	}
	/**
	 * Checks whether this RadioButton is selectable/Enabled or not
	 * @return true if selectable and false otherwise
	 */
	public boolean isSelectable() {
		return selectable;
	}
	/**
	 * Sets whether this RadioButton is selectable/enabled or not
	 * @param selectable true to make selectable/enabled and false otherwise
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	void addToGroup(ChoiceGroup grp)
	{
		group=grp;
	}
	private void addMenuItems()
	{
		synchronized (this) {
			if(!selectable)return;
			if(addedItems)return;
			Item test=parent;
			while(test!=null && !(test instanceof Window)){
				test=test.getParent();
			}
			if(test!=null){
				addMenu=((Window)test).getMenu();
			}
			markItem=new MenuItem(selected?
					LocaleManager.getTranslation("flemil.unmark"):
					LocaleManager.getTranslation("flemil.mark"));
			markItem.setListener(new MenuCommandListener(){

				public void commandAction(MenuItem item) {
					keyPressedEvent(
							GlobalControl.getControl().getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
					markItem.setName(selected?
							LocaleManager.getTranslation("flemil.unmark"):
						LocaleManager.getTranslation("flemil.mark"));
				}
			});
			addMenu.add(markItem);
			addedItems=true;
		}
		repaint(GlobalControl.getControl().getCurrent().getMenuBarRect());
	}
	private void removeMenuItems()
	{
		synchronized (this) {
			if(!selectable || !addedItems || addMenu==null)return;if(markItem==null)return;
			addMenu.remove(markItem);
			addedItems=false;
		}
		repaint(GlobalControl.getControl().getCurrent().getMenuBarRect());
	}
	public void focusGained() 
	{
		if(focussed)return;
		focussed=true;
        nameDisplayer.focusGained();
        repaint(displayRect);
        addMenuItems();
	}

	public void focusLost() {
		focussed=false;
		nameDisplayer.focusLost();
		repaint(displayRect);
		removeMenuItems();
	}

	public Rectangle getDisplayRect() {
		return displayRect;
	}

	public Rectangle getMinimumDisplayRect(int availWidth) {
		int circWid=nameDisplayer.getFont().getHeight()+2;
		Rectangle tmp=nameDisplayer.getMinimumDisplayRect(availWidth-circWid);
		tmp.width+=circWid;
		tmp.height+=2;
		return tmp;
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

	public void keyPressedEvent(int keyCode) {
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);     
        switch(key)
        {
            case Canvas.FIRE:
            {
            	selected=!selected;
            	repaint(displayRect);
            	setSelected(selected);
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
        	g.setClip(intersect.x, intersect.y, intersect.width, intersect.height);
        	g.setColor(focussed?((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOCUS_BACKGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_BACKGROUND)).intValue());
        	int radius=((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.CURVES_RADIUS)).intValue();
        	g.fillRoundRect(displayRect.x+1, displayRect.y+1, 
    				displayRect.width-1, displayRect.height-1,
    				radius, radius);
    		if(paintBorder)
    		{
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
    				getProperty(Style.COMPONENT_FOCUS_FOREGROUND)).intValue():
    					((Integer)GlobalControl.getControl().getStyle().
    				getProperty(Style.COMPONENT_FOREGROUND)).intValue());
        	int testHei=nameDisplayer.getFont().getHeight()-2;
        	if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
        		g.drawArc(displayRect.x+2, displayRect.y+
            			displayRect.height/2-testHei/2, 
            			testHei,
            			testHei,0,360);
            	if(selected)
            	{
            		g.fillArc(displayRect.x+4, displayRect.y+
                			displayRect.height/2-testHei/2+2, 
                			testHei-4,
                			testHei-4,0,360);
            	}
        	}
        	else{
        		g.drawArc(displayRect.x+displayRect.width-2-testHei, displayRect.y+
            			displayRect.height/2-testHei/2, 
            			testHei,
            			testHei,0,360);
            	if(selected)
            	{
            		g.fillArc(displayRect.x+displayRect.width-testHei, displayRect.y+
                			displayRect.height/2-testHei/2+2, 
                			testHei-4,
                			testHei-4,0,360);
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
			keyPressedEvent(GlobalControl.getControl().
					getMainDisplayCanvas().getKeyCode(Canvas.FIRE));
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

	public void setDisplayRect(Rectangle rect) {
		this.displayRect=rect;
		if(rect.width>nameDisplayer.getFont().getHeight())
		{
			if(LocaleManager.getTextDirection()==LocaleManager.LTOR){
				nameDisplayer.setDisplayRect(new Rectangle(
						displayRect.x+nameDisplayer.getFont().getHeight()+2,
						displayRect.y,
						displayRect.width-nameDisplayer.getFont().getHeight()+2,
						displayRect.height));
			}
			else{
				nameDisplayer.setDisplayRect(new Rectangle(
						displayRect.x,
						displayRect.y,
						displayRect.width-nameDisplayer.getFont().getHeight()-2,
						displayRect.height));
			}
		}
	}
	
	/**
	 * Checks whether this RadioButton is selected or not
	 * @return true if selected and false otherwise
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * Sets whether this RadioButton is selected or not
	 * @param selected true to set this RadioButton selected 
	 * and false to set it as not selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		if(markItem!=null)markItem.setName(selected?
				LocaleManager.getTranslation("flemil.unmark"):
					LocaleManager.getTranslation("flemil.mark"));
		if(group!=null && selected)
		{
			group.itemSelected(this);
		}
		if(parent!=null){
			repaint(displayRect);
			if(focussed)
			repaint(GlobalControl.getControl().getCurrent().getMenuBarRect());
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
		return nameDisplayer.getText();
	}
	public void setText(String text) {
		nameDisplayer.setText(text);
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
	public void setTextChanged(boolean textChanged) {
		this.textChanged = textChanged;
	}
	public boolean isTextChanged() {
		return textChanged;
	}
}
