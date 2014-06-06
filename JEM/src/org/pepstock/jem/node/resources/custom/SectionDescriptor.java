package org.pepstock.jem.node.resources.custom;

import java.io.Serializable;
import java.util.LinkedList;

import org.pepstock.jem.node.resources.custom.fields.AbstractFieldDescriptor;

/**
 * A fields container
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class SectionDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = -4440316497840447234L;

	private String name = null;
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
	 * @return the fields
	 */
	public LinkedList<AbstractFieldDescriptor> getFields() {
		return fields;
	}

	/**
	 * Add the fields to this Section
	 * @param fields
	 */
	public void addFields(AbstractFieldDescriptor... fields) {
		for (AbstractFieldDescriptor arf : fields) {
			this.fields.addLast(arf);
		}
	}

	@Override
	public String toString() {
		return "SectionDescriptor [name=" + name + ", fields=" + fields + "]";
	}

}
