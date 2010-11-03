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

package org.flemil.control;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;

import org.flemil.util.ImageFactory;
import org.flemil.util.ResourcesFactory;



/**
 * Class that represents the display styling for an item. Specifies this such 
 * as the background color, foreground color and theming color. The theming
 * color is the color used for the title and the Menu bar.
 * @author Solomon Kariri
 */
public class Style
{
    //Constants for the available styling attributes identification
    //background color for a component
    /**
     * The background color for a component, for example a Label.
     */
    public static final byte COMPONENT_BACKGROUND=0;
    //foreground color for a component
    /**
     * The text color for a component
     */
    public static final byte COMPONENT_FOREGROUND=1;
    /**
     * The radius of round rect used for generating round edged images and for
     * drawing round rects for components if the current 
     */
    public static final byte CURVES_RADIUS=2;
    //Menu focus background
    /**
     * The background color for the currently selected menu item
     */
    public static final byte MENU_HIGHLIGHT_BACKGROUND=3;
    //Menu focus foreground
    /**
     * The text color for the currently selected menu item
     */
    public static final byte MENU_HIGHLIGHT_FOREGROUND=4;
    //Components focus background
    /**
     * The background for a component when focussed
     */
    public static final byte COMPONENT_FOCUS_BACKGROUND=5;
    //Component focus foreground
    /**
     * The text color for a component when focussed
     */
    public static final byte COMPONENT_FOCUS_FOREGROUND=6;
    //Title bar background
    /**
     * The background color for titles
     */
    public static final byte TITLE_BACKGROUND=7;
    //Title bar foreground
    /**
     * The text color for titles
     */
    public static final byte TITLE_FOREGROUND=8;
    //Menu bar background
    /**
     * The background color for menu bar
     */
    public static final byte MENU_BAR_BACKGROUND=9;
    //Menu bar foreground
    /**
     * The text color for menu bar
     */
    public static final byte MENU_BAR_FOREGROUND=10;
    /**
     * The color of the outline for a component
     */
    public static final byte COMPONENT_OUTLINE_COLOR=11;
    /**
     * The color of the outline for a focussed component
     */
    public static final byte COMPONENT_FOCUS_OUTLINE_COLOR=12;
    /**
     * The foreground color for the menu
     */
    public static final byte MENU_ITEM_FOREGROUND=13;
    /**
     * The background color for the menu
     */
    public static final byte MENU_BACKGROUND=14;
    /**
     * The font for the menu items
     */
    public static final byte MENU_ITEM_FONT=15;
    /**
     * The font for window titles
     */
    public static final byte WINDOW_TITLE_FONT=16;
    /**
     * The font for text items and labels
     */
    public static final byte ITEM_FONT=17;
    /**
     * The font for menu bar
     */
    public static final byte MENU_BAR_FONT=18;
    /**
     * The opacity for the title top
     */
    public static final byte TITLE_TOP_OPACITY=19;
    /**
     * The opacity for the title bottom
     */
    public static final byte TITLE_BOTTOM_OPACITY=20;
    /**
     * The opacity for the menu bar top
     */
    public static final byte MENU_BAR_TOP_OPACITY=21;
    /**
     * The opacity for the menu bar bottom
     */
    public static final byte MENU_BAR_BOTTOM_OPACITY=22;
    /**
     * The opacity for the menu item top
     */
    public static final byte MENU_ITEM_TOP_OPACITY=23;
    /**
     * The opacity for the menu item bottom
     */
    public static final byte MENU_ITEM_BOTTOM_OPACITY=24;
    /**
     * The opacity of a faded out menu when a child of the menu is displayed or ScreenWindow
     * when a PopupWindow is displayed
     */
    public static final byte FADE_OPACITY=25;
    /**
     * The color of the fade for the Menu/ScreenWindow when a child of the Menu/PopupWindow
     *  is displayed respectively
     */
    public static final byte FADE_COLOR=26;
    /**
     * The fill color that is thematic and assumed as the background fill for all
     * Windows. This is not an image and is used to fill a rect before the there 
     * foreground image is drawn
     * @see #THEME_FOREGROUND
     */
    public static final byte THEME_BACKGROUND=27;
    /**
     * The color of the line on which the scroll bar scrolls
     */
    public static final byte SCROLLBAR_BACKGROUND=28;
    /**
     * The color of the image used for the bar on the ScrollBar
     * 
     */
    public static final byte SCROLLBAR_FOREGROUND=29;
    /**
     * The color used to create the Image used for the theme foreground 
     * fill in Windows
     */
    public static final byte THEME_FOREGROUND=30;
    /**
     * The opacity of the top of the Theme foreground Image
     */
    public static final byte THEME_TOP_OPACITY=31;
    /**
     * The opacity of the bottom of the Theme foreground Image
     */
    public static final byte THEME_BOTTOM_OPACITY=32;
    /**
     * Whether shading should ahppen for the theme foreground Image
     */
    public static final byte THEME_SHADING=33;
    /**
     * The lighting criteria to be used for creating the Theme foreground Image.
     * Should be one of the values
     * ImageFactory.LIGHT_TOP, ImageFactory.LIGHT_BOTTOM, ImageFactory.FRONT, ImageFactory.LIGHT_BEHIND
     */
    public static final byte THEME_LIGHTING=34;
    /**
     * The lighting criteria to be used for creating the windows title bar image.
     * Should be one of the values
     * ImageFactory.LIGHT_TOP, ImageFactory.LIGHT_BOTTOM, ImageFactory.FRONT, ImageFactory.LIGHT_BEHIND
     */
    public static final byte TITLE_BAR_LIGHTING=35;
    /**
     * Whether shading should happen for windows title bar images
     */
    public static final byte TITLE_BAR_SHADING=36;
    /**
     * The lighting criteria to be used for creating the menu bar image.
     * Should be one of the values
     * ImageFactory.LIGHT_TOP, ImageFactory.LIGHT_BOTTOM, ImageFactory.FRONT, ImageFactory.LIGHT_BEHIND
     */
    public static final byte MENU_BAR_LIGHTING=37;
    /**
     * Whether shading should happen for menu bar images
     */
    public static final byte MENU_BAR_SHADING=38;
    /**
     * The lighting criteria to be used for creating the menu item background image.
     * Should be one of the values
     * ImageFactory.LIGHT_TOP, ImageFactory.LIGHT_BOTTOM, ImageFactory.FRONT, ImageFactory.LIGHT_BEHIND
     */
    public static final byte MENU_ITEM_LIGHTING=39;
    /**
     * Whether shading should happen for menu item background images
     */
    public static final byte MENU_ITEM_SHADING=40;
    
    public static final byte TAB_CURVE_RADIUS=41;
    public static final byte TAB_FONT=42;
    public static final byte TAB_BACKGROUND=43;
    public static final byte TAB_FOREGROUND=44;
    
    public static final byte TAB_FOCUS_BACKGROUND=45;
    public static final byte TAB_FOCUS_FOREGROUND=46;
    public static final byte TAB_TOP_OPACITY=47;
    public static final byte TAB_BOTTOM_OPACITY=48;
    public static final byte TAB_LIGHTING=49;
    public static final byte TAB_SHADING=50;
    
    public static final byte BUTTON_CURVE_RADIUS=51;
    public static final byte BUTTON_FONT=52;
    public static final byte BUTTON_BACKGROUND=53;
    public static final byte BUTTON_FOREGROUND=54;
    public static final byte BUTTON_FOCUS_BACKGROUND=55;
    public static final byte BUTTON_FOCUS_FOREGROUND=56;
    
    public static final byte BUTTON_TOP_OPACITY=57;
    public static final byte BUTTON_BOTTOM_OPACITY=58;
    public static final byte BUTTON_LIGHTING=59;
    public static final byte BUTTON_SHADING=60;

    private Object []props=new Object[61];

    private Style()
    {
    }

    /**
     * Creates a new style object with the default properties set. The values 
     * for the default properties are as shown below.
     * <table align='center' border=0 cellpadding=3>
     * <tr><td><b>Property ID</b></td><td><b>Property Value</b></td></tr>
     * <tr><td>COMPONENT_BACKGROUND</td><td>Integer(0xffffff)</td></tr>
     * <tr><td>COMPONENT_FOREGROUND</td><td>Integer(0x000000)</td></tr>
     * <tr><td>COMPONENT_FOCUS_BACKGROUND</td><td>Integer(0xffeecc)</td></tr>
     * <tr><td>COMPONENT_FOCUS_FOREGROUND</td><td>Integer(0x000000)</td></tr>
     * <tr><td>CURVES_RADIUS</td><td>Integer(5))</td></tr>
     * <tr><td>MENU_HIGHLIGHT_BACKGROUND</td><td>Integer(0xff0000)</td></tr>
     * <tr><td>MENU_HIGHLIGHT_FOREGROUND</td><td>Integer(0xffffff)</td></tr>
     * <tr><td>TITLE_BACKGROUND</td><td>Integer(0x7777ff)</td></tr>
     * <tr><td>TITLE_FOREGROUND</td><td>Integer(0xffffff)</td></tr>
     * <tr><td>MENU_BAR_BACKGROUND</td><td>Integer(0x7777ff)</td></tr>
     * <tr><td>MENU_BAR_FOREGROUND</td><td>Integer(0xffffff)</td></tr>
     * <tr><td>COMPONENT_OUTLINE_COLOR</td><td>Integer(0x009900)</td></tr>
     * <tr><td>COMPONENT_FOCUS_OUTLINE_COLOR</td><td>Integer(0x772211)</td></tr>
     * <tr><td>MENU_ITEM_FOREGROUND</td><td>Integer(0x000000)</td></tr>
     * <tr><td>MENU_BACKGROUND</td><td>Integer(0xbbbbbb)</td></tr>
     * <tr><td>MENU_ITEM_FONT</td><td>Font(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL)</td></tr>
     * <tr><td>MENU_BAR_FONT</td><td>Font(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM)</td></tr>
     * <tr><td>WINDOW_TITLE_FONT</td><td>Font(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM)</td></tr>
     * <tr><td>ITEM_FONT</td><td>Font(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL)</td></tr>
     * <tr><td>MENU_BAR_TOP_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>MENU_BAR_BOTTOM_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>MENU_ITEM_TOP_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>MENU_ITEM_BOTTOM_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>TITLE_TOP_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>TITLE_BOTTOM_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>THEME_BACKGROUND</td><td>Integer(0xffffff)</td></tr>
     * <tr><td>FADE_COLOR</td><td>Integer(0x000000)</td></tr>
     * <tr><td>FADE_OPACITY</td><td>Integer(150)</td></tr>
     * <tr><td>SCROLLBAR_BACKGROUND</td><td>Integer(0x333333)</td></tr>
     * <tr><td>SCROLLBAR_FOREGROUND</td><td>Integer(0x7777ff)</td></tr>
     * <tr><td>THEME_BOTTOM_OPACITY</td><td>Integer(10)</td></tr>
     * <tr><td>THEME_TOP_OPACITY</td><td>Integer(255)</td></tr>
     * <tr><td>THEME_FOREGROUND</td><td>Integer(0xccccff)</td></tr>
     * <tr><td>THEME_LIGHTING</td><td>Byte(ImageFactory.LIGHT_FRONT)</td></tr>
     * <tr><td>THEME_SHADING</td><td>Boolean(false)</td></tr>
     * <tr><td>TITLE_BAR_LIGHTING</td><td>Byte(ImageFactory.LIGHT_TOP)</td></tr>
     * <tr><td>TITLE_BAR_SHADING</td><td>Boolean(true)</td></tr>
     * <tr><td>MENU_BAR_LIGHTING</td><td>Byte(ImageFactory.LIGHT_BOTTOM)</td></tr>
     * <tr><td>MENU_BAR_SHADING</td><td>Boolean(true)</td></tr>
     * <tr><td>MENU_ITEM_LIGHTING</td><td>Byte(ImageFactory.LIGHT_TOP)</td></tr>
     * <tr><td>MENU_ITEM_SHADING</td><td>Boolean(true)</td></tr>
     * </table>
     */
    public static Style getDefault()
    {
        Style style=new Style();
        style.setProperty(Style.COMPONENT_BACKGROUND, new Integer(0xffffff));
        style.setProperty(Style.COMPONENT_FOREGROUND, new Integer(0x000000));
        style.setProperty(Style.COMPONENT_FOCUS_BACKGROUND, new Integer(0xffeecc));
        style.setProperty(Style.COMPONENT_FOCUS_FOREGROUND, new Integer(0x000000));
        style.setProperty(Style.CURVES_RADIUS, new Integer(4));
        style.setProperty(Style.MENU_HIGHLIGHT_BACKGROUND, new Integer(0xff0000));
        style.setProperty(Style.MENU_HIGHLIGHT_FOREGROUND, new Integer(0xffffff));
        style.setProperty(Style.TITLE_BACKGROUND, new Integer(0x7777ff));
        style.setProperty(Style.TITLE_FOREGROUND, new Integer(0xffffff));
        style.setProperty(Style.MENU_BAR_BACKGROUND, new Integer(0x7777ff));
        style.setProperty(Style.MENU_BAR_FOREGROUND, new Integer(0xffffff));
        style.setProperty(Style.COMPONENT_OUTLINE_COLOR, new Integer(0x009900));
        style.setProperty(Style.COMPONENT_FOCUS_OUTLINE_COLOR, new Integer(0x772211));
        style.setProperty(Style.MENU_ITEM_FOREGROUND, new Integer(0x000000));
        style.setProperty(Style.MENU_BACKGROUND, new Integer(0xbbbbbb));
        style.setProperty(Style.MENU_ITEM_FONT, Font.getFont(
                Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
        style.setProperty(MENU_BAR_FONT, Font.getFont(
                Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        style.setProperty(WINDOW_TITLE_FONT, Font.getFont(
                Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        style.setProperty(ITEM_FONT, Font.getFont(
                Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        style.setProperty(Style.MENU_BAR_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.MENU_BAR_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.MENU_ITEM_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.MENU_ITEM_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.TITLE_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.TITLE_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.THEME_BACKGROUND, new Integer(0xffffff));
        style.setProperty(Style.FADE_COLOR, new Integer(0x000000));
        style.setProperty(Style.FADE_OPACITY, new Integer(150));
        style.setProperty(Style.SCROLLBAR_BACKGROUND, new Integer(0x333333));
        style.setProperty(Style.SCROLLBAR_FOREGROUND, new Integer(0x7777ff));
        style.setProperty(Style.THEME_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.THEME_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.THEME_FOREGROUND, new Integer(0xccccff));
        style.setProperty(Style.THEME_LIGHTING, new Byte(ImageFactory.LIGHT_BEHIND));
        style.setProperty(Style.THEME_SHADING, new Boolean(false));
        style.setProperty(Style.TITLE_BAR_LIGHTING, new Byte(ImageFactory.LIGHT_TOP));
        style.setProperty(Style.TITLE_BAR_SHADING, new Boolean(true));
        style.setProperty(Style.MENU_BAR_LIGHTING, new Byte(ImageFactory.LIGHT_BOTTOM));
        style.setProperty(Style.MENU_BAR_SHADING, new Boolean(true));
        style.setProperty(Style.MENU_ITEM_LIGHTING, new Byte(ImageFactory.LIGHT_TOP));
        style.setProperty(Style.MENU_ITEM_SHADING, new Boolean(true)); 
        
        
        style.setProperty(Style.TAB_CURVE_RADIUS, new Integer(10));
        style.setProperty(Style.TAB_FONT, Font.getFont(
                Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        style.setProperty(Style.TAB_BACKGROUND, new Integer(0xffffff));
        style.setProperty(Style.TAB_FOREGROUND, new Integer(0x000000));
        
        style.setProperty(Style.TAB_FOCUS_BACKGROUND, new Integer(0xff0000));
        style.setProperty(Style.TAB_FOCUS_FOREGROUND, new Integer(0xffffff));
        style.setProperty(Style.TAB_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.TAB_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.TAB_LIGHTING, new Byte(ImageFactory.LIGHT_BOTTOM));
        style.setProperty(Style.TAB_SHADING, new Boolean(true));
        
        style.setProperty(Style.BUTTON_CURVE_RADIUS, new Integer(10));
        style.setProperty(Style.BUTTON_FONT, Font.getFont(
                Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        style.setProperty(Style.BUTTON_BACKGROUND, new Integer(0x444488));
        style.setProperty(Style.BUTTON_FOREGROUND, new Integer(0xffffff));
        style.setProperty(Style.BUTTON_FOCUS_BACKGROUND, new Integer(0xff0000));
        style.setProperty(Style.BUTTON_FOCUS_FOREGROUND, new Integer(0xffffff));
        
        style.setProperty(Style.BUTTON_TOP_OPACITY, new Integer(255));
        style.setProperty(Style.BUTTON_BOTTOM_OPACITY, new Integer(255));
        style.setProperty(Style.BUTTON_LIGHTING, new Byte(ImageFactory.LIGHT_TOP));
        style.setProperty(Style.BUTTON_SHADING, new Boolean(true));
        return style;
    }
    /**
     * Sets a property of a Style. The properties that can be set are specified
     * by constants which are accessible as public static members of the Style
     * class. The property value is the value for the property which take values
     * governed by the following rules.
     * <p>For color properties, that is, backgrounds and foregrounds the
     * property values object is an Integer object initialized with a value
     * representing the color in RGB format. For example the colors red, grren
     * and blue will be represented by the following initializations
     * respectively: new Integer(0xff0000); new Integer(0x00ff00);
     * new Integer(0x0000ff);</p>
     * <p>For outline the property values object will be a Byte() object 
     * initialized using either of the two outline/border types static members
     * of the Style class. Therefore for block borders you use
     * new Byte(Style.BLOCK_OUTLINE), for rounded borders you use
     * new Byte(Style.ROUND_OUTLINE)</p>.
     * <p>See the types specified for the different types in the getDefault() 
     * method documentation/p>
     * @param propId the id of the property being set
     * @param val the value for the attribute being set
     * @throws java.lang.IllegalArgumentException if the property id is not a
     * member of the valid property ids or the value object is of the wrong type
     * e.g. Byte instead of Integer
     */
    public void setProperty(byte propId,Object val)throws IllegalArgumentException
    {
        if(propId<0 || propId>props.length-1)
        {
            throw new IllegalArgumentException(
                    "Illegal property id "+propId);
        }
        else
        {
            props[propId]=val;
        }
    }
    /**
     * Returns the property value for the specified property id for this Style. 
     * The property id must be one of the specified constants for properties in
     * the Style class.
     * @param propId the property ID for the property being requested
     * @return the value for the property id requested
     * @throws java.lang.IllegalArgumentException if the property id is not valid
     */
    public Object getProperty(byte propId)throws IllegalArgumentException
    {
        if(propId>props.length-1 || propId<0)
        {
            throw new IllegalArgumentException("Property ID not valid");
        }
        else
        {
            return props[propId];
        }
    }
    /**
     * Loads a theme from a Theme file and packages it into a Style object so 
     * that it can be used in the application by making a call to the
     * GlobalControl.setStyle() method. The theme file is a plain text file that has
     *  a simple and straight foward layout so that the application does not 
     *  use so much memory trying to parse normal css or xml files. the theme files 
     *  usually have a ftm (Flemil Theme) extension but any plain text document can be used.
     *  The Theme file is formatted in such a way that every attribute that can be set on a Style
     *  object is represented as a single line. See the demo application for a sample theme file
     *   and how they can be loaded into the application. Also its possible to create and preview
     *   themes on the <a href="http://flemil.com" target="_blank">Flemil website</a> which you can download to your 
     *   computer for use in application development or directly from the application on the mobile.
     * @param is InputStream that contains the theme definition.
     * @return a Style object representing the theme that was loaded from the InputStream
     * @throws IOException if an IOExceotion occurs reading from the InputStream
     */
    public synchronized static Style loadStyle(InputStream is)throws IOException
    {
    	Hashtable table=new Hashtable();
    	table.put("component-background", new Byte((byte)0));
    	table.put("component-foreground", new Byte((byte)1));
    	table.put("curves-radius", new Byte((byte)2));
    	table.put("menu-highlight-background", new Byte((byte)3));
    	table.put("menu-highlight-foreground", new Byte((byte)4));
    	table.put("component-focus-background", new Byte((byte)5));
    	table.put("component-focus-foreground", new Byte((byte)6));
    	table.put("title-background", new Byte((byte)7));
    	table.put("title-foreground", new Byte((byte)8));
    	table.put("menu-bar-background", new Byte((byte)9));
    	table.put("menu-bar-foreground", new Byte((byte)10));
    	table.put("component-outline-color", new Byte((byte)11));
    	table.put("component-focus-outline-color", new Byte((byte)12));
    	table.put("menu-item-foreground", new Byte((byte)13));
    	table.put("menu-background", new Byte((byte)14));
    	table.put("menu-item-font", new Byte((byte)15));
    	table.put("window-title-font", new Byte((byte)16));
    	table.put("item-font", new Byte((byte)17));
    	table.put("menu-bar-font", new Byte((byte)18));
    	table.put("title-top-opacity", new Byte((byte)19));
    	table.put("title-bottom-opacity", new Byte((byte)20));
    	table.put("menu-bar-top-opacity", new Byte((byte)21));
    	table.put("menu-bar-bottom-opacity", new Byte((byte)22));
    	table.put("menu-item-top-opacity", new Byte((byte)23));
    	table.put("menu-item-bottom-opacity", new Byte((byte)24));
    	table.put("fade-opacity", new Byte((byte)25));
    	table.put("fade-color", new Byte((byte)26));
    	table.put("theme-background", new Byte((byte)27));
    	table.put("scroll-bar-background", new Byte((byte)28));
    	table.put("scroll-bar-foreground", new Byte((byte)29));
    	table.put("theme-foreground", new Byte((byte)30));
    	table.put("theme-top-opacity", new Byte((byte)31));
    	table.put("theme-bottom-opacity", new Byte((byte)32));
    	table.put("theme-shading", new Byte((byte)33));
    	table.put("theme-lighting", new Byte((byte)34));
    	table.put("title-bar-lighting", new Byte((byte)35));
    	table.put("title-bar-shading", new Byte((byte)36));
    	table.put("menu-bar-lighting", new Byte((byte)37));
    	table.put("menu-bar-shading", new Byte((byte)38));
    	table.put("menu-item-lighting", new Byte((byte)39));
    	table.put("menu-item-shading", new Byte((byte)40));
    	
    	table.put("tab-curve-radius", new Byte((byte)41));
    	table.put("tab-font", new Byte((byte)42));
    	table.put("tab-background", new Byte((byte)43));
    	table.put("tab-foreground", new Byte((byte)44));
        
    	table.put("tab-focus-background", new Byte((byte)45));
    	table.put("tab-focus-foreground", new Byte((byte)46));
    	table.put("tab-top-opacity", new Byte((byte)47));
    	table.put("tab-bottom-opacity", new Byte((byte)48));
    	table.put("tab-lighting", new Byte((byte)49));
    	table.put("tab-shading", new Byte((byte)50));
        
    	table.put("button-curve-radius", new Byte((byte)51));
    	table.put("button-font", new Byte((byte)52));
    	table.put("button-background", new Byte((byte)53));
    	table.put("button-foreground", new Byte((byte)54));
    	table.put("button-focus-background", new Byte((byte)55));
    	table.put("button-focus-foreground", new Byte((byte)56));
        
    	table.put("button-top-opacity", new Byte((byte)57));
    	table.put("button-bottom-opacity", new Byte((byte)58));
    	table.put("button-lighting", new Byte((byte)59));
    	table.put("button-shading", new Byte((byte)60));
    	Style style=Style.getDefault();
    	Hashtable props=ResourcesFactory.loadProperites(is);
    	Enumeration keys=props.keys();
    	while(keys.hasMoreElements()){
    		String property=(String)keys.nextElement();
    		String value=(String)props.get(property);
    		property=property.toLowerCase();
    		value=value.toLowerCase();
    		if(table.containsKey(property))
			{
				int val=((Byte)table.get(property)).byteValue();
				switch(val)
				{
				case TITLE_BAR_SHADING:
				case MENU_BAR_SHADING:
				case MENU_ITEM_SHADING:
				case THEME_SHADING:
				case TAB_SHADING:
				case BUTTON_SHADING:
				{
					if(value.toLowerCase().equals("yes"))
					{
						style.setProperty((byte)val, new Boolean(true));
					}
					else if(value.toLowerCase().equals("no"))
					{
						style.setProperty((byte)val, new Boolean(false));
					}
					//process for boolean
					break;
				}
				case WINDOW_TITLE_FONT:
				case MENU_BAR_FONT:
				case MENU_ITEM_FONT:
				case ITEM_FONT:
				case TAB_FONT:
				case BUTTON_FONT:
				{
					//process font
					style.setProperty((byte)val, parseFont(value));
					break;
				}
				case THEME_LIGHTING:
				case TITLE_BAR_LIGHTING:
				case MENU_BAR_LIGHTING:
				case MENU_ITEM_LIGHTING:
				case TAB_LIGHTING:
				case BUTTON_LIGHTING:
				{
					if(value.toLowerCase().equals("front"))
					{
						style.setProperty((byte)val, new Byte(ImageFactory.LIGHT_FRONT));
					}
					else if(value.toLowerCase().equals("behind"))
					{
						style.setProperty((byte)val, new Byte(ImageFactory.LIGHT_BEHIND));
					}
					else if(value.toLowerCase().equals("top"))
					{
						style.setProperty((byte)val, new Byte(ImageFactory.LIGHT_TOP));
					}
					else if(value.toLowerCase().equals("bottom"))
					{
						style.setProperty((byte)val, new Byte(ImageFactory.LIGHT_BOTTOM));
					}
					break;
				}
				case TAB_TOP_OPACITY:
				case TAB_BOTTOM_OPACITY:
				case BUTTON_TOP_OPACITY:
				case BUTTON_BOTTOM_OPACITY:
				case TITLE_BOTTOM_OPACITY:
				case TITLE_TOP_OPACITY:
				case MENU_BAR_TOP_OPACITY:
				case MENU_BAR_BOTTOM_OPACITY:
				case MENU_ITEM_TOP_OPACITY:
				case MENU_ITEM_BOTTOM_OPACITY:
				case THEME_BOTTOM_OPACITY:
				case THEME_TOP_OPACITY:
				{
					StringBuffer bf=new StringBuffer(value);
					bf.deleteCharAt(0);
					try
					{
						int intValue=Integer.parseInt(bf.toString(),16);
						style.setProperty((byte)val, new Integer(intValue));
					}
					catch(NumberFormatException nfe)
					{
						nfe.printStackTrace();
					}  
					break;
				}
				default:
				{
					StringBuffer bf=new StringBuffer(value);
					bf.deleteCharAt(0);
					try
					{
						int intValue=Integer.parseInt(bf.toString(),16);
						style.setProperty((byte)val, new Integer(intValue));
					}
					catch(NumberFormatException nfe)
					{
						nfe.printStackTrace();
					}
				}
				}
			}
    	}
    	return style;
    }
    private static String serializeFont(Font font)
    {
    	//{face-proportional, style-[bold], size-large}
    	String face="";
    	if(font.getFace()==Font.FACE_MONOSPACE)
    	{
    		face="face-monospace";
    	}
    	else if(font.getFace()==Font.FACE_PROPORTIONAL)
    	{
    		face="face-proportional";
    	}
    	else if(font.getFace()==Font.FACE_SYSTEM)
    	{
    		face="face-system";
    	}
    	String size="";
    	if(font.getSize()==Font.SIZE_LARGE)
    	{
    		size="size-large";
    	}
    	else if(font.getSize()==Font.SIZE_MEDIUM)
    	{
    		size="size-medium";
    	}
    	else if(font.getSize()==Font.SIZE_SMALL)
    	{
    		size="size-small";
    	}
    	StringBuffer style=new StringBuffer();
    	style.append("style-[");
    	boolean set=false;
    	if(font.isPlain())
    	{
    		style.append("plain");
    		if(!set)set=true;
    	}
    	if(font.isBold())
    	{
    		if(set)style.append("|");
    		else set=true;
    		style.append("bold");
    	}
    	if(font.isItalic())
    	{
    		if(set)style.append("|");
    		else set=true;
    		style.append("italic");
    	}
    	if(font.isUnderlined())
    	{
    		if(set)style.append("|");
    		style.append("underlined");
    	}
    	style.append("]");
    	StringBuffer combine=new StringBuffer();
    	combine.append("{");
    	combine.append(face);
    	combine.append(",");
    	combine.append(style);
    	combine.append(",");
    	combine.append(size);
    	combine.append("}");
    	return combine.toString();
    }
    private static Font parseFont(String fontRep) 
    {
    	StringBuffer bf=new StringBuffer(fontRep);
		bf.deleteCharAt(0);
		bf.deleteCharAt(bf.length()-1);
		Vector strings=new Vector();
		int last=0;
		for(int i=0;i<bf.length();i++)
		{
			if(bf.charAt(i)==',')
			{
				strings.addElement(bf.toString().substring(last, i).trim());
				last=i+1;
			}
		}
		strings.addElement(bf.toString().substring(last, bf.length()).trim());
		bf=null;
		int face=Font.FACE_PROPORTIONAL;
		int styl=0;
		int size=Font.SIZE_MEDIUM;
		if(strings.size()==3)
		{
			for(int i=0;i<3;i++)
			{
				String current=strings.elementAt(i).toString();
				if(current.startsWith("face"))
				{
					if(current.equals("face-monospace"))
					{
						face=Font.FACE_MONOSPACE;
					}
					else if(current.equals("face-system"))
					{
						face=Font.FACE_SYSTEM;
					}
				}
				else if(current.startsWith("size"))
				{
					if(current.equals("size-small"))
					{
						size=Font.SIZE_SMALL;
					}
					else if(current.equals("size-large"))
					{
						size=Font.SIZE_LARGE;
					}
				}
				else if(current.startsWith("style-"))
				{
					String stylesStr=current.substring(current.indexOf('-')+2, current.length()-1);
					last=0;
					for(int j=0;j<stylesStr.length();j++)
					{
						if(stylesStr.charAt(j)=='|')
						{
							String tst=stylesStr.substring(last, j);
							last=j+1;
							styl=addStyle(styl, tst);
						}
					}
					String tst=stylesStr.substring(last, stylesStr.length());
					styl=addStyle(styl, tst);
				}
			}
		}
		return Font.getFont(face, styl, size);
    }
    private static int addStyle(int current,String style)
    {
    	if(style.equals("bold"))
    	{
    		current|=Font.STYLE_BOLD;
    	}
    	else if(style.equals("italic"))
    	{
    		current|=Font.STYLE_ITALIC;
    	}
    	else if(style.equals("undelined"))
    	{
    		current|=Font.STYLE_UNDERLINED;
    	}
    	else if(style.equals("plain"))
    	{
    		current|=Font.STYLE_PLAIN;
    	}
    	return current;
    }
    public byte[] toByteArray()
    {
    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
    	DataOutputStream dos=new DataOutputStream(baos);
    	for(int i=0;i<props.length;i++)
    	{
    		try
    		{
    			dos.writeByte(i);
    			if(props[i] instanceof Font)
    			{
    				dos.writeUTF(serializeFont((Font)props[i]));
    			}
    			else if(props[i] instanceof Boolean)
    			{
    				dos.writeUTF(((Boolean)props[i]).booleanValue()?"true":"false");
    			}
    			else
    			{
    				dos.writeUTF(props[i].toString());
    			}
    		}
    		catch(IOException ioe)
    		{
    			ioe.printStackTrace();
    			return null;
    		}
    	}
    	return baos.toByteArray();
    }
    public Style loadFromByteStream(InputStream is)throws IOException
    {
    	Style loaded=Style.getDefault();
    	DataInputStream dis=new DataInputStream(is);
    	for(int i=0;i<props.length;i++)
    	{
    		try
    		{
    			byte index=dis.readByte();
    			if(props[index] instanceof Integer)
    			{
    				loaded.setProperty(index, new Integer(Integer.parseInt(dis.readUTF())));
    			}
    			else if(props[index] instanceof Boolean)
    			{
    				loaded.setProperty(index, new Boolean(dis.readUTF().equals("true")?true:false));
    			}
    			else if(props[index] instanceof Byte)
    			{
    				loaded.setProperty(index, new Byte(Byte.parseByte(dis.readUTF())));
    			}
    			else if(props[index] instanceof Font)
    			{
    				loaded.setProperty(index, parseFont(dis.readUTF()));
    			}
    		}
    		catch(IOException ioe)
    		{
    			throw ioe;
    		}
    	}
    	return loaded;
    }
}