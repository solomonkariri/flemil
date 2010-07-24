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

import org.flemil.util.Rectangle;

/**
 * This interface is extended by nay item that can show ScrollBars such 
 * that it can contain Items that more than its displayRect both horizontally
 * and vertically. An example of such a Control is a Panel
 * @author Solomon Kariri
 *
 */
public interface Scrollable extends Item 
{
	/**
	 * Used to denote scrolling in the x direction. Scrolling can be 
	 * either positive or negative.
	 * This value is passed to an method to tell it that scrolling in the 
	 * X direction should be accounted for
	 */
	public static final byte DIRECTION_X=0x01;
	/**
	 * Used to denote scrolling in the Y direction. Scrolling can be 
	 * either positive or negative.
	 * This value is passed to an method to tell it that scrolling in the 
	 * Y direction should be accounted for
	 */
	public static final byte DIRECTION_Y=0x02;
	/**
	 * Scrolls the given Rectangle such that its visible or falls within this items displayRect.
	 * This method makes sure that the top of the passed in rectangle is visible even though 
	 * the bottom might not be visible if the passed in rect has a longer height than this Scrollble's
	 * displayRect. The remainder can/should be made possible via scrolling.
	 * @param rect the Rectangle to be made visible
	 * @param scrollDirection the direction in which to scroll. This value can be either 
	 * Scrollable.DIRECTION_Y for Y scrolling only,
	 * Scrollable.DIRECTION_X for X scrolling only
	 * or Scrollable.DIRECTION_Y|Scrollable.DIRECTION_X for both Y and X scrolling
	 */
	public void scrollRectToVisible(Rectangle rect, int scrollDirection);
	/**
	 * Tells whether this Scrollable can be or is allowed to do horizontal scrolling
	 * @return true if allowed and false otherwise
	 */
	public boolean isHorScrolling();
	/**
	 * Sets whether this Scrollable is allowed to do horizontal scrolling.
	 * @param horScrolling to allow horizontal scrolling and false therwise
	 */
	public void setHorScrolling(boolean horScrolling);
}
