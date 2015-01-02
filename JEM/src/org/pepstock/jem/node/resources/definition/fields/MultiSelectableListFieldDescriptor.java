/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.node.resources.definition.fields;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * This descriptor represents an abstract field that hold values in a list-based form and offer multiple selections.  
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class MultiSelectableListFieldDescriptor extends AbstractFieldDescriptor implements Serializable, ResourcePartDescriptor, MultiValuesField {

	private static final long serialVersionUID = 9218545401365213862L;

	private Set<String> values = new HashSet<String>();
	private Set<String> defaultValues = new HashSet<String>();
	private Set<String> selectedValues = new HashSet<String>();

	/**
	 * For serialization
	 */
	public MultiSelectableListFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public MultiSelectableListFieldDescriptor(String key, String label) {
		super(key, label);
	}
	
	/**
	 * @return a String set, containing the all the possible field selectedValues. 
	 */
	public Set<String> getSelectedValues() {
		return selectedValues;
	}

	/**
	 * Sets the selectedValues that this field can have. 
	 * @param values a bounce of String, duplicates will be discarded
	 */
	public void setSelectedValues(String... values) {
		for (String v : values) {
			this.selectedValues.add(v);
		}
	}

	/**
	 * Gets the values that will be auto-selected. It will be <code>1</code>-size if <code>isMultipleSelection()</code> is <code>false</code>.
	 * It's size should be <code>&lt;=</code> rather than <code>values</code> size.  
	 * @return a String[] containing the values that should be automatically selected. 
	 */
	public Set<String> getDefaultValues() {
		return defaultValues;
	}

	/**
	 * Sets the values that should be automatically selected.
	 * @param defaultValues a bounce of String, duplicates will be discarded
	 */
	public void setDefaultValues(String... defaultValues) {
		for (String dv : defaultValues) {
			this.defaultValues.add(dv);
		}
	}

	/**
	 * @return <code>true</code> if the descriptor has one or more than one default values
	 */
	public boolean hasDefaultValues() {
		return !defaultValues.isEmpty();
	}
	
	/**
	 * @return all the possible values
	 */
	public Set<String> getValues() {
		return values;
	}

	/**
	 * Sets all the possible values
	 * @param values all the possible values
	 */
	public void setValues(String... values) {
		for (String v : values) {
			this.values.add(v);
		}
	}

	@Override
	public String toString() {
		return "MultiSelectableListFieldDescriptor [toString()=" + super.toString() + ", values=" + values + ", defaultValues=" + defaultValues + ", selectedValues=" + selectedValues + "]";
	}

}
