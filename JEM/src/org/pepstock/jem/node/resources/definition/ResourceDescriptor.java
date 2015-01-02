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
package org.pepstock.jem.node.resources.definition;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.TabBar;

/**
 * This descriptor is the root element to define a XML resource template.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 2.0
 *
 */
public class ResourceDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 1L;

	private String type = null;
	
	private String description = null;
	
	private List<SectionDescriptor> sections = new LinkedList<SectionDescriptor>();
	
	/**
	 * For serialization
	 */
	public ResourceDescriptor() {
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the Resource type name
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the sections
	 */
	public List<SectionDescriptor> getSections() {
		return sections;
	}

	/**
	 * Add the sections to this Resource
	 * @param sections
	 */
	public void addSections(SectionDescriptor... sections) {
		for (SectionDescriptor s : sections) {
			this.sections.add(s);
		}
	}

	/**
	 * @return <code>true</code> if the resource has more than one section.
	 * Should be used to know if to be rendered it needs a {@link TabBar} or a simple panel.
	 */
	public boolean hasSections() {
		return sections.size() > 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResourceDescriptor [type=" + type + ", description=" + description + ", sections=" + sections + "]";
	}
}
