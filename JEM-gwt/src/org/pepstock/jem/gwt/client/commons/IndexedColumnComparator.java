/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.io.Serializable;
import java.util.Comparator;

import org.pepstock.jem.gwt.client.security.CurrentUser;

/**
 * Is the template of column comparator to sort cell table. Save the column index of table and 
 * if you want ascending or descending order. 
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> object type
 * 
 */
public abstract class IndexedColumnComparator<T> implements Comparator<T>, Serializable {
	
    private static final long serialVersionUID = 1L;
    
    private static final String SEPARATOR = "-"; 

	private static final int DEFAULT_INDEX = 1;

	private int index = DEFAULT_INDEX;

	private boolean ascending = true;
	
	private String preferenceKey = null;
	
	/**
	 * Constructs the object using the default index of column
	 */
	public IndexedColumnComparator(){
		this(DEFAULT_INDEX);
	}

	/**
	 * Constructs the object saving the index of column
	 * 
	 * @param index column index to sort
	 * 
	 */
	public IndexedColumnComparator(int index) {
		this(index, null);
	}

	/**
	 * Constructs the object saving the index of column
	 * 
	 * @param index column index to sort
	 * @param preferenceKey preference key to extract previous choice
	 * 
	 */
	public IndexedColumnComparator(int index, String preferenceKey) {
		this.index = index;
		this.preferenceKey = preferenceKey;
		getPreferenceValues();
	}
	
	/**
	 * Returns preference key
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}

	/**
	 * Sets preference key
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	/**
	 * Returns column index to sort
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets column index to sort
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
		setPreferenceValues();
	}

	/**
	 * Returns if is ascending sort
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Sets if is ascending sort
	 * @param ascending
	 *            the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
		setPreferenceValues();
	}

	/**
	 * Ges preferences
	 */
	private void getPreferenceValues(){
		if (preferenceKey != null){
			String pref = CurrentUser.getInstance().getStringPreference(preferenceKey);
			if (pref != null){
				// format is: [int]-[boolean]
				//            [index]-[ascending]
				String[] values = pref.split(SEPARATOR);
				this.index = Integer.parseInt(values[0]);
				this.ascending = Boolean.valueOf(values[1]);
			}
		}
	}

	/**
	 * Saves preferences values
	 */
	private void setPreferenceValues(){
		if (preferenceKey != null){
			CurrentUser.getInstance().setStringPreference(preferenceKey, index+SEPARATOR+ascending);
		}
	}

}