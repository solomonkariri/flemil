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

package org.flemil.ui;


import org.flemil.ui.component.Menu;
import org.flemil.ui.component.Panel;
import org.flemil.util.Rectangle;



/**
 * This Interface represents an Item that represents a Window that can be displayed
 * in the application. A window can have a title, must have a body where the items 
 * within the window are shown and must have a menu associated with it that is 
 * displayed to the user when this Window is the current Window on top of the 
 * application display. The menu bar used to represent the menu does not necessarily
 *  have to be associated with this window as is the case for PopupWindow which uses
 *  the menu bar provided by its parent.
 * @author Solomon Kariri
 */
public interface Window extends Item
{
	/**
	 * Returns the Menu that is associated with this Window. 
	 * Every Window Must have a Menu associated with it.
	 * @return the Menu that is associated with this Window.
	 */
    public Menu getMenu();
    /**
     * Sets the title for this Window. If the title is long enough not to fit within
     * the available width of the window, the window will scroll the title automatically
     * when it is displayed so that the whole title is readable by the user.
     * @return the title for this Window.
     */
    public String getTitle();
    /**
     * Sets the title for this Window.If the title is long enough not to fit within
     * the available width of the window, the window will scroll the title automatically
     * when it is displayed so that the whole title is readable by the user.
     * @param title the title to be used this Window.
     */
    public void setTitle(String title);
    /**
     * Returns the bar Rectangle that is used for displaying this Window's Menu. The
     * bar does not have to belong to this window and it can be the bar for a parent 
     * to this window as is the case for PopupWindow 
     * @return the bar Rectangle that is used for displaying this Window's Menu.
     */
    public Rectangle getMenuBarRect();
    /**
     * Method that is called when the right item of the menu bar for this window is selected.
     * This might be the right soft key or not depending on the current orientation of the 
     * application. The window does not need to care about which key it is but should only 
     * respond to the fact that the right key for the MenuBar has been selected. 
     */
    public void menuRightSelected();
    /**
     * Method that is called when the left item of the menu bar for this window is selected.
     * This might be the left soft key or not depending on the current orientation of the 
     * application. The window does not need to care about which key it is but should only 
     * respond to the fact that the left key for the MenuBar has been selected. 
     */
    public void menuLeftSelected();
    /**
     * Returns the container that is used by this window to add items to its 
     * central display regions
     * @return the container that is used by this window to add items to its 
     * central display regions
     */
    public Panel getContentPane();
}