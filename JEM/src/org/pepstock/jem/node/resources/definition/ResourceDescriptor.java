package org.pepstock.jem.node.resources.definition;

import java.io.Serializable;
import java.util.LinkedList;

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
	
	private LinkedList<SectionDescriptor> sections = new LinkedList<SectionDescriptor>();
	
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
	public LinkedList<SectionDescriptor> getSections() {
		return sections;
	}

	/**
	 * Add the sections to this Resource
	 * @param sections
	 */
	public void addSections(SectionDescriptor... sections) {
		for (SectionDescriptor s : sections) {
			this.sections.addLast(s);
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
