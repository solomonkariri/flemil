package com.flemil.ui.component;
/**
 * A
 * ChoiceGroup represents a logical grouping of RadioButton Items such that
 * no two RadioItem elements members of this group can be selected at the same time.
 * The radio button don't have to be together on the GUI and can be quite separate 
 * from each other
 * @author Solomon Kariri
 *
 */

public class ChoiceGroup {
	private RadioButton currentItem;
	
	/**
	 * Default constructor
	 */
	public ChoiceGroup()
	{
		
	}
	/**
	 * Adds a RadioButton to this group. If the Radio button is selected
	 * any previously selected RadioButton already a member of the group is 
	 * unselected.  
	 * @param item the RadioButton to be added to this ChoiceGroup
	 */
	public void add(RadioButton item)
	{
		item.addToGroup(this);
		if(item.isSelected())
		{
			if(currentItem!=null)currentItem.setSelected(false);
			currentItem=item;
		}
	}
	void itemSelected(RadioButton item)
	{
		if(currentItem!=null && currentItem!=item)currentItem.setSelected(false);
		currentItem=item;
	}
}
