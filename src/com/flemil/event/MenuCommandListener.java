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

package com.flemil.event;

import com.flemil.ui.component.MenuItem;

/**
 * Interface that a callback class has to implement in order to be able to be notified when 
 * a selection action is performed on a MenuItem for which the implementation of this interface is set
 * as the listener for selection events
 * @author Solomon Kariri
 *
 */
public interface MenuCommandListener 
{
	/**
	 * Called then a selection action is performed on the MenuItem for which an implementation of 
	 * this interface is registered as the receiver of selection events
	 * @param item the MenuItem on which the selection has occurred
	 */
    public void commandAction(MenuItem item);
}
