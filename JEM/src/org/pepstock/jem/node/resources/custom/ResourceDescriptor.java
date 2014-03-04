package org.pepstock.jem.node.resources.custom;

import java.io.Serializable;
import java.util.LinkedList;

import com.google.gwt.user.client.ui.TabBar;

/**
 * A custom Resource
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class ResourceDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = -6651706310600815923L;

	private String type = null;
	private LinkedList<SectionDescriptor> sections = new LinkedList<SectionDescriptor>();
	
	/**
	 * For serialization
	 */
	public ResourceDescriptor() {
	}
	
	/**
	 * Builds a custom Resource with the given type name
	 * @param type the type name
	 */
	public ResourceDescriptor(String type) {
		this.type = type;
	}

	/**
	 * @return the Resource type name
	 */
	public String getType() {
		return type;
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
	
}
