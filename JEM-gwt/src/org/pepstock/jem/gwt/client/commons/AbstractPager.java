/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Base abstract class for table pager
 * 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public abstract class AbstractPager extends HorizontalPanel {

	private SimplePager wrappedPager = null;
	
	/**
	 * Constructor that wrap a {@link SimplePanel}
	 * @param wrappedPager simple pager
	 */
	public AbstractPager(SimplePager wrappedPager) {
		this.wrappedPager = wrappedPager;
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		add(this.wrappedPager);
		setWidth(Sizes.HUNDRED_PERCENT);
	}
	
	/**
	 * Returns the simple pager used to construct the object
	 * @return the wrapped {@link SimplePanel}
	 */
	public SimplePager getWrappedPager() {
		return wrappedPager;
	}
	
}