/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.node.resources.definition;

import java.io.Serializable;
import java.util.LinkedList;

import org.pepstock.jem.node.resources.definition.fields.AbstractFieldDescriptor;

/**
 * A section is a container, from UI perspective, of all necessary forms to define a resource.
 * <br>
 * Graphically, it will be a tab panel inside a tabs container.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 2.0
 *
 */
public class SectionDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private boolean propertiesEditor = false;
	
	private LinkedList<AbstractFieldDescriptor> fields = new LinkedList<AbstractFieldDescriptor>();
	
	/**
	 * Creates a Section without name
	 */
	public SectionDescriptor() {
		this(null);
	}

	/**
	 * Creates a section with the given name
	 * @param name the Section name
	 */
	public SectionDescriptor(String name) {
		this.name = name;
	}

	/**
	 * @return the Section name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the Section name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the propertiesEditor
	 */
	public boolean isPropertiesEditor() {
		return propertiesEditor;
	}

	/**
	 * @param propertiesEditor the propertiesEditor to set
	 */
	public void setPropertiesEditor(boolean propertiesEditor) {
		this.propertiesEditor = propertiesEditor;
	}

	/**
	 * @return the fields
	 */
	public LinkedList<AbstractFieldDescriptor> getFields() {
		return fields;
	}

	/**
	 * Add the fields to this Section
	 * @param fields additional fields to add to section
	 */
	public void addFields(AbstractFieldDescriptor... fields) {
		// scans fields and add to section
		for (AbstractFieldDescriptor arf : fields) {
			this.fields.addLast(arf);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SectionDescriptor [name=" + name + ", propertiesEditor=" + propertiesEditor + ", fields=" + fields + "]";
	}
}