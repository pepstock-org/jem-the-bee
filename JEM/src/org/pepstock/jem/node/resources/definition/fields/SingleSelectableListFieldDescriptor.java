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
 * This class represents a field with one selectable value from a set of many
 * @author Marco "Fuzzo" Cuccato
 */
public class SingleSelectableListFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor, MultiValuesField {

	private static final long serialVersionUID = 4234336709246533878L;

	private Set<String> values = new HashSet<String>();
	private boolean renderAsRadio = false;
	
	/**
	 * For serialization
	 */
	public SingleSelectableListFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the fieds
	 * @param key
	 * @param label
	 * @param values
	 */
	public SingleSelectableListFieldDescriptor(String key, String label, String... values) {
		super(key, label);
		loadValues(values);
	}

	/**
	 * @return <code>true</code> if you want this field to be rendered as a radio button group, <code>false</code> (default) if you want a combo box
	 */
	public boolean isRenderAsRadio() {
		return renderAsRadio;
	}

	/**
	 * Set if you want this field to be rendered as radio or combo
	 * @param renderAsRadio
	 */
	public void setRenderAsRadio(boolean renderAsRadio) {
		this.renderAsRadio = renderAsRadio;
	}

	/**
	 * @return all possible values
	 */
	public Set<String> getValues() {
		return values;
	}

	/**
	 * Sets all the possible values
	 * @param values
	 */
	public final void setValues(String... values) {
		loadValues(values);
	}
	
	/**
	 * load all the possible values
	 * @param values
	 */
	private void loadValues(String... values) {
		for (String v : values) {
			this.values.add(v);
		}
	}

	@Override
	public String toString() {
		return "SingleSelectableListFieldDescriptor [toString()=" + super.toString() + ", values=" + values + ", renderAsRadio=" + renderAsRadio + "]";
	}

}
