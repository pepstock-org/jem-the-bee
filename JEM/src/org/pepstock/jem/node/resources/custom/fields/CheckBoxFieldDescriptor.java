package org.pepstock.jem.node.resources.custom.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.custom.ResourcePartDescriptor;

/**
 * This class represents a checkbox field
 * @author Marco "Fuzzo" Cuccato
 */
public class CheckBoxFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 0L;

	/**
	 * For serialization
	 */
	public CheckBoxFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public CheckBoxFieldDescriptor(String key, String label) {
		super(key, label);
	}

	/**
	 * @return the defaultValue
	 */
	public boolean isDefaultValue() {
		return Boolean.parseBoolean(super.getDefaultValue());
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(boolean defaultValue) {
		super.setDefaultValue(String.valueOf(defaultValue));
	}

	@Override
	public String toString() {
		return "CheckBoxFieldDescriptor [toString()=" + super.toString() + "]";
	}
	
}
