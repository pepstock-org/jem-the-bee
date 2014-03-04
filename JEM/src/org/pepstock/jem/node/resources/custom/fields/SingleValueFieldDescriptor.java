package org.pepstock.jem.node.resources.custom.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.custom.ResourcePartDescriptor;

/**
 * This class represent a single-selectedValue resource field.
 * Value si intended to be free-typed. Regular Expression validation of it is supported.
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class SingleValueFieldDescriptor extends AbstractFieldDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 2636121542239371494L;

	private String selectedValue = null;
	private String defaultValue = null;

	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public SingleValueFieldDescriptor(String key, String label) {
		super(key, label);
	}
	
	/**
	 * @return the field selectedValue
	 */
	public String getSelectedValue() {
		return selectedValue;
	}

	/**
	 * Sets the field selectedValue
	 * @param value the field selectedValue
	 */
	public void setSelectedValue(String value) {
		this.selectedValue = value;
	}

	/**
	 * @return a String that will be used as default (pre-compiled) field selectedValue 
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default field selectedValue
	 * @param defaultValue a String that will be used as default (pre-compiled) field falue
	 */
	public void setDefaultValue(String defaultValue) {
		if (defaultValue != null && defaultValue.trim().isEmpty()) {
			this.defaultValue = null;
		} else {
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * @return <code>true</code> if this field has a default value
	 */
	public boolean hasDefaultValue() {
		return defaultValue != null;
	}
	
}
