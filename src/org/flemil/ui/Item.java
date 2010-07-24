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

import javax.microedition.lcdui.Graphics;

import org.flemil.util.Rectangle;


/**
 *The interface that every component must implement for effective interaction 
 * with and manipulation by other components and display management utilities
 * @author Solomon Kariri
 */
public interface Item
{
    /**
     * Sets the parent for this Item which also have to be an Item. This
     * reference to the parent will be used to provide features most of which
     * is the scrolling callback to notify the parent container of a scrolling
     * action.
     * @param parent the Item in which this Item is placed. null if this Item
     * is the topmost Item
     */
    public void setParent(Item parent);
    /**
     * Returns the parent Item to this Item
     * @return the parent Item to this Item
     */
    public Item getParent();
    /**
     * Called when this Item gains focus.
     */
    public void focusGained();
    /**
     * Called when this Item looses focus.
     */
    public void focusLost();
    /**
     * Sets the rectangle that is available on the display for the rendering of
     * this component
     * @param rect the Rectangle area available on the display for rendering of
     * this Item
     */
    public void setDisplayRect(Rectangle rect);
    /**
     * Returns the currently set displayRect for this Item
     * @see #getMinimumDisplayRect(int)
     * @see #setDisplayRect(Rectangle)
     * @return the displayRect for this Item
     */
    public Rectangle getDisplayRect();
    /**
     * Returns the minimum dimensions that this Item requires for visible display
     * of its contents. A rectangle with a zero dimension implies no minimum limit
     * in that dimension.
     * @see #setDisplayRect(Rectangle)
     * @see #getDisplayRect()
     * @param availWidth the maximum available width that this Items rect can be allowed
     *  to occupy
     * @return the minimum possible rectangle for usable operation of this Item.
     */
    public Rectangle getMinimumDisplayRect(int availWidth);
    /**
     * Repaints the given region of this Item. Mostly all calls to this method 
     * will result in the propagation of the call to the parent of this Item until the 
     * bottom most Item is reached so that all Items whose displayRectanges are intersected
     *  by the repainted rectangles repaints the intersects as well
     * @param clip the clip region that is to be repainted
     */
    public void repaint(Rectangle clip);
    /**
     * Paints the region of this Item specified by the clip. The Item makes
     * calculations to reduce as much as possible the area that is repainted for
     * the Item. If the clip does not intersect in any way with this Items displayRectangle,
     * then the Item is not painted at all.
     * @param g the graphics on which the painting is to be done
     * @param clip the region of the graphics that requires update or that is to
     * be painted on by this Item
     */
    public void paint(Graphics g,Rectangle clip);
    /**
     * Invoked when a key is pressed when this Item is in focus. This method
     * performs the necessary action for the key pressed.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyPressedEvent(int keyCode);
    /**
     * Invoked when a key is released when this Item is in focus. This method
     * performs the necessary action for the key pressed.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyReleasedEvent(int keyCode);
    /**
     * Invoked by a child of this Item if it cannot handle a keyPressed event 
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyPressedEventReturned(int keyCode);
    /**
     * Invoked by a child of this Item if it cannot handle a keyReleased event 
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyReleasedEventReturned(int keyCode);
    /**
     * Invoked when a key is repeated i.e. pressed and held down, when this Item
     * is in focus. This method performs the necessary action for the key
     * repeated.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyRepeatedEvent(int keyCode);
    /**
     * Invoked by a child of this Item if it cannot handle a keyRepeated event
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it.
     * @param keyCode the key code as provided by the underlying native platform.
     */
    public void keyRepeatedEventReturned(int keyCode);
    /**
     * Invoked by a child of this Item if it cannot handle a pointerPressed event
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it. The coordinates
     * are relative to the whole display area of the application
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerPressedEventReturned(int x,int y);
    /**
     * Invoked by a child of this Item if it cannot handle a pointerReleased event
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it. The coordinates
     * are relative to the whole display area of the application
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerReleasedEventReturned(int x,int y);
    /**
     * Invoked by a child of this Item if it cannot handle a pointerDragged event
     * passed to it. The parent then deals with the event if it can or propagates
     * it to its parent if any if it cannot handle it. The coordinates
     * are relative to the whole display area of the application and not this Item.
     * To know whether the coordinate falls within its displayRect it can call the
     * Rectangle.contains() method with the coordinates as the parameters.
     * @see #getDisplayRect()
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerDraggedEventReturned(int x,int y);
    /**
     * Called in a device that supports a pointer. This method provides the 
     * location information for the pointer when it is pressed. The coordinates
     * are relative to the whole display area of the application
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerPressedEvent(int x,int y);
    /**
     * Called in a device that supports a pointer. This method provides the 
     * location information for the pointer when it is released. The 
     * coordinates are relative to the whole display area of the application
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerReleasedEvent(int x,int y);
    /**
     * Called in a device that supports a pointer. This method provides the 
     * location information for the pointer when it is dragged. The 
     * coordinates are relative to the whole display area of the application
     * @param x the x coordinate of the pointer
     * @param y the y coordinate of the pointer
     */
    public void pointerDraggedEvent(int x,int y);
    /**
     * Used to specify whether this Item should paint its border or not. If set true the Item will paint 
     * its border and not otherwise. All borders are painted one pixel wide and might be rounded at the edges
     * depending on the Style settings of the application
     * 
     * @param paint sets whether this Item should paint its border. 
     */
    public void setPaintBorder(boolean paint);
    /**
     * Returns whether this Item paints its border
     * @see #paint(Graphics, Rectangle)
     * @return true if paints border and false otherwise
     */
    public boolean isPaintBorder();
    /**
     * Checks whether this Item can be set as the currently focused Item. This means that this Item
     * should be able to adjust its behavior or look and feel in response to calls to its focusGained() or
     * focusLost() methods
     * @see #focusGained()
     * @see #focusLost()
     * @return true if can be focused and false otherwise
     */
    public boolean isFocusible();
    /**
     * Sets whether this Item should be focusable. This if passed the value true, then this Item 
     * will change its effects when being drawn to respond to focus gain and focus loss events
     * @see #focusGained()
     * @see #focusLost()
     * 
     * @param focusible
     */
    public void setFocusible(boolean focusible);
    
    public boolean isFocussed();
}
