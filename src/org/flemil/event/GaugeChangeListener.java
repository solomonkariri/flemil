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

import org.flemil.ui.component.Gauge;

/**
 * Interface that a callback class has to implement in order to be able to be notified when a
 * gauge changes its value. The implementations methods will be called whether the Gauge
 *  is set as being interactive or not.
 * @author Solomon Kariri
 *
 */
public interface GaugeChangeListener {
	/**
	 * Called when the value of a Gauge to which this Interfaces implementation is set as the listener for 
	 * value change events
	 * @param source the Gauge that was the cause of the even or to which a value change has occured
	 */
	public void valueChanged(Gauge source);
}
