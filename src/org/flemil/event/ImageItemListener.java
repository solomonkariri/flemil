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
package org.flemil.event;

import org.flemil.ui.component.ImageItem;
/**
 * Interface that a callback class has to implement in order to be able to be notified when an
 *  action is performed when an ImageItem it focused. 
 * @author Solomon Kariri
 *
 */
public interface ImageItemListener {
	/**
	 * Indicates that the fire key has been pressed when this item is in focus.This 
	 * value is the one passed to 
	 * the eventFired method when the ImageItem for which the Implementation of this 
	 * Interface is set as the listener
	 */
	public static final byte FIRE_KEY_PRESSED=1;
	/**
	 * Indicates that this item just gained focus. This value is the one passed to 
	 * the eventFired method when the ImageItem for which the Implementation of this 
	 * Interface is set as the listener
	 */
	public static final byte FOCUSS_GAINED=2;
	/**
	 * Indicates that this item just lost focus. This value is the one passed to 
	 * the eventFired method when the ImageItem for which the Implementation of this 
	 * Interface is set as the listener
	 */
	public static final byte FOCUS_LOST=3;
	/**
	 * Calls when an event of either type FIRE_KEY_PRESSED or FOCUSS_GAINED or FOCUS_LOST
	 *  occurs on this item.
	 *  @see #FIRE_KEY_PRESSED
	 *  @see #FOCUS_LOST
	 *  @see #FOCUSS_GAINED
	 * @param source the ImageItem from which the even originated
	 * @param eventType the type of event which can be either FIRE_KEY_PRESSED, FOCUSS_GAINED or 
	 * FOCUS_LOST
	 */
	public void eventFired(ImageItem source, byte eventType);
}
