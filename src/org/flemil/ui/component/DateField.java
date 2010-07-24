package org.flemil.ui.component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.flemil.control.GlobalControl;
import org.flemil.event.MenuCommandListener;
import org.flemil.ui.Item;
import org.flemil.ui.TextItem;
import org.flemil.util.Rectangle;

public class DateField implements Item{
	public static final int DATE=1;
	public static final int TIME=2;
	public static final int DATETIME=3;
	
	private int mode;
	
	private static Calendar calendar;
	private Date date;
	
	private TextField dateField;
	private TextField timeField;
	private Grid fieldsGrid;
	
	private static Grid datesGrid;
	
	private static DateSelectionWindow dateWindow;
	
	private static Grid yearGrid;
	private static Grid monthGrid;
	
	private static Image rightArrow;
	private static Image leftArrow;
	private static Image upArrow;
	private static Image downArrow;
	
	private static Grid timeGrid;
	
	private static MenuItem saveDateItem;
	private static MenuItem backDateItem;
	
	private static MenuItem saveTimeItem;
	private static MenuItem backTimeItem;
	
	private static DateField currentDateField;
	
	private static String[] months={
			"January",
			"February",
			"March",
			"April",
			"May",
			"June",
			"July",
			"August",
			"September",
			"October",
			"November",
			"December"
	};
	
	private static int daysCount[]={31,28,31,30,31,30,31,31,30,31,30,31};
	
	private static String[] daysInitials={"S","M","T","W","T","F","S"};
	
	private static ScreenWindow previousWindow; 
	
	private static TimeSelectionWindow timeWindow;
	
	static boolean inited;
	
	public DateField(int mode){
		this(mode, null);
	}
	
	public DateField(int mode,Date date){
		if(!inited){
			try {
				rightArrow=Image.createImage("/arrow.png");
				rightArrow=GlobalControl.getImageFactory().scaleImage(rightArrow, 
						rightArrow.getWidth(), 
						rightArrow.getHeight(), 
						Sprite.TRANS_ROT180);
				leftArrow=Image.createImage("/arrow.png");
				upArrow=GlobalControl.getImageFactory().scaleImage(leftArrow, 
						leftArrow.getHeight(), 
						leftArrow.getWidth(), 
						Sprite.TRANS_ROT90);
				downArrow=GlobalControl.getImageFactory().scaleImage(leftArrow, 
						leftArrow.getHeight(), 
						leftArrow.getWidth(), 
						Sprite.TRANS_ROT270);
			} catch (IOException e) {
				e.printStackTrace();
			}
			yearGrid=new Grid(1, 5);
			monthGrid=new Grid(1, 5);
			datesGrid=new Grid(7, 7);
			timeGrid=new Grid(3, 4);
			dateWindow=new DateSelectionWindow("Select Date");
			dateWindow.getContentPane().setAlignment(
					Panel.SPAN_FULL_WIDTH|Panel.CENTER_VERTICAL_ALIGN);
			//add the year label
			yearGrid.setColumnsDistribution(new int[]{20,10,40,10,20});
			monthGrid.setColumnsDistribution(new int[]{10,10,60,10,10});
			yearGrid.add(new Label(""));
			ImageItem imgIt=new ImageItem(leftArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			yearGrid.add(imgIt);
			TextField txtField=new TextField("",5,javax.microedition.lcdui.TextField.ANY);
			txtField.setEditable(false);
			txtField.setAlignment(TextItem.ALIGN_CENTER);
			txtField.setPaintBorder(false);
			txtField.setTextWraps(false);
			yearGrid.add(txtField);
			imgIt=new ImageItem(rightArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			yearGrid.add(imgIt);
			yearGrid.add(new Label(""));
			
			monthGrid.add(new Label(""));
			imgIt=new ImageItem(leftArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			monthGrid.add(imgIt);
			txtField=new TextField("",40,javax.microedition.lcdui.TextField.ANY);
			txtField.setEditable(false);
			txtField.setAlignment(TextItem.ALIGN_CENTER);
			txtField.setPaintBorder(false);
			txtField.setTextWraps(false);
			monthGrid.add(txtField);
			imgIt=new ImageItem(rightArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			monthGrid.add(imgIt);
			monthGrid.add(new Label(""));
			
			for(int j=0;j<7;j++){
				Label label=new Label("");
				label.setAlignment(TextItem.ALIGN_CENTER);
				label.setTextWraps(false);
				datesGrid.add(label);
			}
			
			for(int i=1;i<7;i++){
				for(int j=0;j<7;j++){
					txtField=new TextField("",4,javax.microedition.lcdui.TextField.ANY);
					txtField.setEditable(false);
					txtField.setAlignment(TextItem.ALIGN_CENTER);
					txtField.setPaintBorder(false);
					txtField.setTextWraps(false);
					txtField.setFocusible(false);
					datesGrid.add(txtField);
				}
			}
			for(int i=0;i<7;i++){
				((Label)datesGrid.getItemAt(0, i)).setText(daysInitials[i]);
			}
			
			saveDateItem=new MenuItem("Save");
			backDateItem=new MenuItem("Back");
			dateWindow.getMenu().add(saveDateItem);
			dateWindow.getMenu().add(backDateItem);
			DateChangeMenuListener listener=new DateChangeMenuListener();
			saveDateItem.setListener(listener);
			backDateItem.setListener(listener);
			
			timeGrid.setColumnsDistribution(new int[]{30,20,20,30});
			
			timeGrid.add(new Label(""));
			
			imgIt=new ImageItem(upArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			
			timeGrid.add(imgIt);
			
			imgIt=new ImageItem(upArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			
			
			timeGrid.add(imgIt);
			timeGrid.add(new Label(""));
			timeGrid.add(new Label(""));
			
			txtField=new TextField("",4,javax.microedition.lcdui.TextField.ANY);
			txtField.setEditable(false);
			txtField.setAlignment(TextItem.ALIGN_CENTER);
			txtField.setPaintBorder(false);
			txtField.setTextWraps(false);
			
			timeGrid.add(txtField);
			
			txtField=new TextField("",4,javax.microedition.lcdui.TextField.ANY);
			txtField.setEditable(false);
			txtField.setAlignment(TextItem.ALIGN_CENTER);
			txtField.setPaintBorder(false);
			txtField.setTextWraps(false);
			
			timeGrid.add(txtField);
			timeGrid.add(new Label("H"));
			timeGrid.add(new Label(""));
			
			imgIt=new ImageItem(downArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			
			timeGrid.add(imgIt);
			
			imgIt=new ImageItem(downArrow);
			imgIt.setResizeToFit(true);
			imgIt.setFocusible(false);
			imgIt.setPaintBorder(false);
			
			timeGrid.add(imgIt);
			timeGrid.add(new Label(""));
			
			timeWindow=new TimeSelectionWindow("Time", true);
			timeWindow.getContentPane().setAlignment(Panel.CENTER_HORIZONTAL_ALIGN);
			timeWindow.getContentPane().add(timeGrid);
			
			saveTimeItem=new MenuItem("Save");
			backTimeItem=new MenuItem("Back");
			timeWindow.getMenu().add(saveTimeItem);
			timeWindow.getMenu().add(backTimeItem);
			TimeChangeMenuListener timeListener=new TimeChangeMenuListener();
			saveTimeItem.setListener(timeListener);
			backTimeItem.setListener(timeListener);
			
			inited=true;
		}
		this.mode=mode;
		this.date=date;
		int cols=mode==DATETIME?2:1;
		this.fieldsGrid=new Grid(1, cols);
		if((this.mode & DateField.DATE)>0){
			this.dateField=new TextField("", 100, 
					javax.microedition.lcdui.TextField.ANY);
			this.dateField.setEditable(false);
			this.dateField.setTextWraps(false);
			fieldsGrid.add(dateField);
			dateField.setText(getDateString(this.date));
		}
		if((this.mode & DateField.TIME)>0){
			this.timeField=new TextField("", 100, 
					javax.microedition.lcdui.TextField.ANY);
			this.timeField.setEditable(false);
			this.timeField.setTextWraps(false);
			fieldsGrid.add(timeField);
			timeField.setText(getTimeString(this.date));
		}
		if(this.mode==DateField.DATETIME){
			fieldsGrid.setColumnsDistribution(new int[]{60,40});
		}
	}
	
	private String getTimeString(Date date) {
		if(date==null){
			return "<Time>";
		}
		else{
			Calendar cl=Calendar.getInstance();
			cl.setTime(date);
			StringBuffer result=new StringBuffer();
			result.append(
					cl.get(Calendar.HOUR_OF_DAY)<10?
							"0"+cl.get(Calendar.HOUR_OF_DAY):
								""+cl.get(Calendar.HOUR_OF_DAY));
			result.append(":");
			result.append(cl.get(Calendar.MINUTE)<10?"0"+cl.get(Calendar.MINUTE):
				""+cl.get(Calendar.MINUTE));
			result.append("h");
			return result.toString();
		}
	}

	private String getDateString(Date date){
		if(date==null){
			return "<Date>";
		}
		else{
			Calendar cl=Calendar.getInstance();
			cl.setTime(date);
			StringBuffer result=new StringBuffer();
			result.append(cl.get(Calendar.DATE)<10?"0"+cl.get(Calendar.DATE):""+cl.get(Calendar.DATE));
			result.append(" ");
			result.append(months[cl.get(Calendar.MONTH)]);
			result.append(" ");
			result.append(cl.get(Calendar.YEAR));
			return result.toString();
		}
	}
	private static void populateFieldsForDate(Calendar cal){
		((TextItem)yearGrid.getItemAt(0, 2)).setText(""+cal.get(Calendar.YEAR));
		((TextItem)monthGrid.getItemAt(0, 2)).setText(months[cal.get(Calendar.MONTH)]);
		Calendar testCalendar=Calendar.getInstance();
		testCalendar.setTime(cal.getTime());
		testCalendar.set(Calendar.DATE, 1);
		int start=testCalendar.get(Calendar.DAY_OF_WEEK)-1;
		int dayCount=daysCount[cal.get(Calendar.MONTH)];
		if(dayCount==28)dayCount+=(cal.get(Calendar.YEAR)%4==0)?1:0;
		int row;
		int col;
		for(int i=start;i<start+dayCount;i++){
			row=i/7;
			col=i%7;
			((TextField)datesGrid.getItemAt(row+1, col)).setFocusible(true);
			((TextField)datesGrid.getItemAt(row+1, col)).setText(""+(i-start+1));
			if(cal.get(Calendar.DATE)==(i-start+1)){
				datesGrid.setSelectedCell(row+1, col);
			}
		}
		for(int i=0;i<start;i++){
			row=i/7;
			col=i%7;
			((TextField)datesGrid.getItemAt(row+1, col)).setFocusible(false);
			((TextField)datesGrid.getItemAt(row+1, col)).setText("");
		}
		for(int i=start+dayCount;i<42;i++){
			row=i/7;
			col=i%7;
			((TextField)datesGrid.getItemAt(row+1, col)).setFocusible(false);
			((TextField)datesGrid.getItemAt(row+1, col)).setText("");
		}
		dateWindow.repaint(dateWindow.getDisplayRect());
	}
	
	private static void populateFieldsForTime(Calendar cal){
		((TextItem)timeGrid.getItemAt(1, 1)).setText(
				cal.get(Calendar.HOUR_OF_DAY)<10?
						"0"+cal.get(Calendar.HOUR_OF_DAY):
							""+cal.get(Calendar.HOUR_OF_DAY));
		((TextItem)timeGrid.getItemAt(1, 2)).setText(
				cal.get(Calendar.MINUTE)<10?"0"+cal.get(Calendar.MINUTE):
					""+cal.get(Calendar.MINUTE));
	}
	
	public void focusGained() {
		fieldsGrid.focusGained();
	}

	public void focusLost() {
		fieldsGrid.focusLost();
	}

	public Rectangle getDisplayRect() {
		return fieldsGrid.getDisplayRect();
	}

	public Rectangle getMinimumDisplayRect(int availWidth) {
		Rectangle rect=fieldsGrid.getMinimumDisplayRect(availWidth);
		rect.height+=3;
		return rect;
	}

	public Item getParent() {
		return fieldsGrid.getParent();
	}

	public boolean isFocusible() {
		return fieldsGrid.isFocusible();
	}

	public boolean isPaintBorder() {
		return fieldsGrid.isPaintBorder();
	}

	public void keyPressedEvent(int keyCode) {
		int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);   
        switch(key)
        {
            case Canvas.DOWN:
            case Canvas.UP:
            case Canvas.LEFT:
            case Canvas.RIGHT:
            {
            	fieldsGrid.keyPressedEvent(keyCode);
            	break;
            }
            default:
            {
            	if(dateField!=null && dateField.isFocussed()){
            		showDateChangeWindow();
            	}
            	else if(timeField!=null && timeField.isFocussed()){
            		showTimeChangeWindow();
            	}
            }
        }
	}

	private void showTimeChangeWindow() {
		calendar=Calendar.getInstance();
		if(date!=null){
			calendar.setTime(date);
		}
		populateFieldsForTime(calendar);
		timeWindow.getContentPane().removeAll();
		timeWindow.getContentPane().add(timeGrid);
		currentDateField=this;
		GlobalControl.getControl().getCurrent().showPopUp(timeWindow);
	}

	private void showDateChangeWindow() {
		previousWindow=GlobalControl.getControl().getCurrent();
		calendar=Calendar.getInstance();
		if(date!=null){
			calendar.setTime(date);
		}
		populateFieldsForDate(calendar);
		DateField.dateWindow.getContentPane().removeAll();
		DateField.dateWindow.getContentPane().add(yearGrid);
		DateField.dateWindow.getContentPane().add(monthGrid);
		DateField.dateWindow.getContentPane().add(datesGrid);
		currentDateField=this;
		GlobalControl.getControl().setCurrent(dateWindow);
	}

	public void keyPressedEventReturned(int keyCode) {}

	public void keyReleasedEvent(int keyCode) {}

	public void keyReleasedEventReturned(int keyCode) {}

	public void keyRepeatedEvent(int keyCode) {
		keyPressedEvent(keyCode);
	}

	public void keyRepeatedEventReturned(int keyCode) {}

	public void paint(Graphics g, Rectangle clip) {
		fieldsGrid.paint(g, clip);
	}

	public void pointerDraggedEvent(int x, int y) {}

	public void pointerDraggedEventReturned(int x, int y) {}

	public void pointerPressedEvent(int x, int y) {}

	public void pointerPressedEventReturned(int x, int y) {}

	public void pointerReleasedEvent(int x, int y) {}

	public void pointerReleasedEventReturned(int x, int y) {}

	public void repaint(Rectangle clip) {
		fieldsGrid.repaint(clip);
	}

	public void setDisplayRect(Rectangle rect) {
		fieldsGrid.setDisplayRect(rect);
	}

	public void setFocusible(boolean focusible) {
		fieldsGrid.setFocusible(focusible);
	}

	public void setPaintBorder(boolean paint) {
		fieldsGrid.setPaintBorder(paint);
	}

	public void setParent(Item parent) {
		fieldsGrid.setParent(parent);
	}

	public void setDate(Date date) {
		this.date=date;
	}

	public Date getDate() {
		return this.date;
	}

	public byte getAlignment() {
		if(dateField!=null){
			return dateField.getAlignment();
		}
		else{
			return timeField.getAlignment();
		}
	}

	public Font getFont() {
		if(dateField!=null){
			return dateField.getFont();
		}
		else{
			return timeField.getFont();
		}
	}

	public boolean isFocussed() {
		return fieldsGrid.isFocussed();
	}

	public boolean isTextWraps() {
		if(dateField!=null){
			return dateField.isTextWraps();
		}
		else{
			return timeField.isTextWraps();
		}
	}

	public void resetFont() {
		if(dateField!=null){
			dateField.resetFont();
		}
		if(timeField!=null){
			timeField.resetFont();
		}
	}

	public void setAlignment(byte alignment) {
		if(dateField!=null){
			dateField.setAlignment(alignment);
		}
		if(timeField!=null){
			timeField.setAlignment(alignment);
		}
	}

	public void setFont(Font font) {
		if(dateField!=null){
			dateField.setFont(font);
		}
		if(timeField!=null){
			timeField.setFont(font);
		}
	}

	public void setTextWraps(boolean textWraps) {
		if(dateField!=null){
			dateField.setTextWraps(textWraps);
		}
		if(timeField!=null){
			timeField.setTextWraps(textWraps);
		}
	}
	
	private class TimeSelectionWindow extends PopUpWindow{

		public TimeSelectionWindow(String title, boolean showTitleBar) {
			super(title, showTitleBar);
		}
		public void keyPressedEventReturned(int keyCode){
			int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);
			switch(key){
			case Canvas.UP:{
				if(timeGrid.getSelectedColumn()==1){
					if(calendar.get(Calendar.HOUR_OF_DAY)==23){
						calendar.set(Calendar.HOUR_OF_DAY, 0);
					}
					else{
						calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);
					}
					populateFieldsForTime(calendar);
				}
				else if(timeGrid.getSelectedColumn()==2){
					if(calendar.get(Calendar.MINUTE)==59){
						calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);
						calendar.set(Calendar.MINUTE, 0);
					}
					else{
						calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
					}
					populateFieldsForTime(calendar);
				}
				break;
			}
			case Canvas.DOWN:{
				if(timeGrid.getSelectedColumn()==1){
					if(calendar.get(Calendar.HOUR_OF_DAY)==0){
						calendar.set(Calendar.HOUR_OF_DAY, 23);
					}
					else{
						calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-1);
					}
					populateFieldsForTime(calendar);
				}
				else if(timeGrid.getSelectedColumn()==2){
					if(calendar.get(Calendar.MINUTE)==0){
						calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-1);
						calendar.set(Calendar.MINUTE, 59);
					}
					else{
						calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)-1);
					}
					populateFieldsForTime(calendar);
				}
				break;
			}
			case Canvas.FIRE:{
				String hours=((TextItem)timeGrid.getItemAt(1, 1)).getText();
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
				String mins=((TextItem)timeGrid.getItemAt(1, 2)).getText();
				calendar.set(Calendar.MINUTE, Integer.parseInt(mins));
				currentDateField.date=calendar.getTime();
				if(currentDateField.timeField!=null){
					currentDateField.timeField.setText(getTimeString(currentDateField.date));
				}
				GlobalControl.getControl().getCurrent().hidePopup(
						GlobalControl.getControl().getCurrent().getCurrentPopup());
				break;
			}
			default:{
				super.keyPressedEventReturned(keyCode);
			}
			}
		}
	}
	
	private class DateSelectionWindow extends ScreenWindow{

		public DateSelectionWindow(String title) {
			super(title);
		}
		public void keyPressedEventReturned(int keyCode){
			int key=GlobalControl.getControl().getMainDisplayCanvas().getGameAction(keyCode);
			switch(key){
			case Canvas.LEFT:{
				if(yearGrid.isFocussed()){
					calendar.set(Calendar.DATE, 1);
					calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
					populateFieldsForDate(calendar);
				}
				else if(monthGrid.isFocussed()){
					calendar.set(Calendar.DATE, 1);
					if(calendar.get(Calendar.MONTH)==0){
						calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
						calendar.set(Calendar.MONTH, 11);
					}
					else{
						calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
					}
					populateFieldsForDate(calendar);
				}
				break;
			}
			case Canvas.RIGHT:{
				if(yearGrid.isFocussed()){
					calendar.set(Calendar.DATE, 1);
					calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
					populateFieldsForDate(calendar);
				}
				else if(monthGrid.isFocussed()){
					calendar.set(Calendar.DATE, 1);
					if(calendar.get(Calendar.MONTH)==11){
						calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
						calendar.set(Calendar.MONTH, 0);
					}
					else{
						calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
					}
					populateFieldsForDate(calendar);
				}
				break;
			}
			case Canvas.FIRE:{
				if(datesGrid.isFocussed()){
					String dat=((TextItem)datesGrid.getCurrentItem()).getText();
					calendar.set(Calendar.DATE, Integer.parseInt(dat));
					currentDateField.date=calendar.getTime();
					if(currentDateField.dateField!=null){
						currentDateField.dateField.setText(getDateString(currentDateField.date));
					}
					GlobalControl.getControl().setCurrent(previousWindow);
					previousWindow.repaint(previousWindow.getDisplayRect());
				}
				else{
					PopUpWindow noDPop=new PopUpWindow("No Date", true);
					noDPop.getContentPane().setAlignment(Panel.SPAN_FULL_WIDTH);
					noDPop.getContentPane().add(new Label("Please select a date"));
					MenuItem okItem=new MenuItem("Ok");
					okItem.setListener(new MenuCommandListener() {
						
						public void commandAction(MenuItem item) {
							GlobalControl.getControl().getCurrent().hidePopup(
									GlobalControl.getControl().getCurrent().getCurrentPopup());
						}
					});
					noDPop.getMenu().add(okItem);
					GlobalControl.getControl().getCurrent().showPopUp(noDPop);
				}
				break;
			}
			default:{
				super.keyPressedEventReturned(keyCode);
			}
			}
		}
	}
	
	private class DateChangeMenuListener implements MenuCommandListener{
		public void commandAction(MenuItem item) {
			if(previousWindow!=null){
				if(item==saveDateItem){
					if(datesGrid.isFocussed()){
						String dat=((TextItem)datesGrid.getCurrentItem()).getText();
						calendar.set(Calendar.DATE, Integer.parseInt(dat));
						currentDateField.date=calendar.getTime();
						if(currentDateField.dateField!=null){
							currentDateField.dateField.setText(getDateString(currentDateField.date));
						}
						GlobalControl.getControl().setCurrent(previousWindow);
					}
					else{
						PopUpWindow noDPop=new PopUpWindow("No Date", true);
						noDPop.getContentPane().setAlignment(Panel.SPAN_FULL_WIDTH);
						noDPop.getContentPane().add(new Label("Please select a date"));
						MenuItem okItem=new MenuItem("Ok");
						okItem.setListener(new MenuCommandListener() {
							
							public void commandAction(MenuItem item) {
								GlobalControl.getControl().getCurrent().hidePopup(
										GlobalControl.getControl().getCurrent().getCurrentPopup());
							}
						});
						noDPop.getMenu().add(okItem);
						GlobalControl.getControl().getCurrent().showPopUp(noDPop);
					}
				}
				else{
					GlobalControl.getControl().setCurrent(previousWindow);
				}
			}
		}
	}
	
	private class TimeChangeMenuListener implements MenuCommandListener{
		public void commandAction(MenuItem item) {
			if(item==saveTimeItem){
				String hours=((TextItem)timeGrid.getItemAt(1, 1)).getText();
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
				String mins=((TextItem)timeGrid.getItemAt(1, 2)).getText();
				calendar.set(Calendar.MINUTE, Integer.parseInt(mins));
				currentDateField.date=calendar.getTime();
				if(currentDateField.timeField!=null){
					currentDateField.timeField.setText(getTimeString(currentDateField.date));
				}
			}
			GlobalControl.getControl().getCurrent().hidePopup(
					GlobalControl.getControl().getCurrent().getCurrentPopup());
		}
	}
}
