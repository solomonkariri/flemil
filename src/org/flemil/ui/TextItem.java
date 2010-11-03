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

import javax.microedition.lcdui.Font;

/**
 * Interface that represents an Item that contains text or that can be used to display text to the user
 * @author Solomon Kariri
 *
 */
public interface TextItem extends Item 
{
	/**
	 * Align text to the left of the TextItem 
	 */
	public static final byte ALIGN_LEFT=1;
	/**
	 * Align text to the right of the TextItem	
	 */
	public static final byte ALIGN_RIGHT=2;
	/**
	 * Align text at the center of the TextItem
	 */
	public static final byte ALIGN_CENTER=3;
	
	/**
	 * Sets the Font that is going to be used by this TextItem to render its text. If this
	 * method is not called for a component, thhis TextItem will use the font defined by the 
	 * current Style for the whole application. Calling this method for a TextItem isolates 
	 * it from the effects of theming on Font such that the TextItems font is not affected
	 * by theme changes.
	 * @param font the Font to be used for rendering font for this TextItem
	 */
	public void setFont(Font font);
	/**
	 * Returns the Font that this TextItem uses to render its text
	 * @return Font that this TextItem uses to render its text
	 */
	public Font getFont();
	/**
	 * Sets whether this TextItems should wrap it's text and hence occupy more space on the screen 
	 * such that all the text in this TextItem is fully visible to the user at all times provided 
	 * this Items displayRect falls fully within the visible area of the application. Passing a 
	 * value of false to this method makes the text item scroll its contents automatically when 
	 * focused on. 
	 * @param textWraps true for this TextItem to wrap its content and false otherwise
	 */
	public void setTextWraps(boolean textWraps);
	/**
	 * Returns true if text wrapping is enabled for this TextItem and false otherwise.
	 * @return true if text wrapping is enabled for this TextItem and false otherwise.
	 */
	public boolean isTextWraps();
	/**
	 * Sets the alignment to be used for text by this TextItem. The value of this parameter
	 * should be one of the values TextItem.ALIGN_LEFT, TextItem.ALIGN_RIGHT, or TextItem.ALIGN_CENTER
	 * @param alignment the alignment to be used for text by this TextItem.
	 */
	public void setAlignment(byte alignment);
	/**
	 * Returns the alignment being used for text by this TextItem. The return value 
	 * is one of the values TextItem.ALIGN_LEFT, TextItem.ALIGN_RIGHT, or TextItem.ALIGN_CENTER
	 * @return the alignment being used for text by this TextItem.
	 */
	public byte getAlignment();
	/**
	 * Retuns the text being currently displayed by this TextItem
	 * @return the text being currently displayed by this TextItem
	 */
	public String getText();
	/**
	 * Sets the text to be displayed by this TextItem
	 * @param text the text to be displayed by this TextItem
	 */
	public void setText(String text);
	/**
	 * Resets the font of a TextItem such that its font is again affected by the theming font
	 * if it had been set during the use of the previous theme
	 */
	public void resetFont();
	/**
	 * Returns whether this TextItems text is currently scrolling or scrolling is enabled and currently
	 * active in this TextItem
	 * @return true if scrolling and false otherwise
	 */
	public boolean isScrolling();
	/**
	 * Sets whether this TextItem is currently scrolling its text or not
	 * @param scrolling true to indicate that a TextScroller is active in this TextItem and
	 * false otherwise
	 */
	public void setScrolling(boolean scrolling);
	/**
	 * Checks whether this TextItem is currently focused
	 * @return true if focused and false otherwise
	 */
	public boolean isFocussed();
	/**
	 * Returns the width of the text that is currently being displayed by this TextItem for
	 * the current Font being used by this TextItem
	 * @return the width of the text that is currently being displayed by this TextItem
	 */
	public int getTextWidth();
	/**
	 * Returns the current indentation for text beginning if this TextItems text is scrolling.
	 * The value of indentation will be zero for a TextItem in which scrolling is disabled or 
	 * one in which text wrapping is allowed
	 * @return the current amount of indentation for Text of this TextItem
	 */
	public int getTextIndent();
	/**
	 * Sets the current indentation for text beginning if this TextItems text is scrolling.
	 * The value of indentation will be zero for a TextItem in which scrolling is disabled or 
	 * one in which text wrapping is allowed
	 * @param indent the indentation to be used by this TextItem when rendering its text
	 */
	public void setTextIndent(int indent);
	
	public boolean isTextChanged();
	public void setTextChanged(boolean changed);
}
