package org.flemil.event;

import org.flemil.ui.component.Menu;
import org.flemil.ui.component.MenuItem;

public interface MenuListener {
	public void highlightChanged(Menu source, MenuItem highlightedItem);
}
