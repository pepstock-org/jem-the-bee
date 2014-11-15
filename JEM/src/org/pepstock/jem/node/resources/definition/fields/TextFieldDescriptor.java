package org.pepstock.jem.node.resources.definition.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * This class represents a free-text field, with optional regular expression validator
 * @author Marco "Fuzzo" Cuccato
 */
public class TextFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor, SingleValueField {

	private static final long serialVersionUID = 8869915360528902211L;

	private String regExValidator = null;

	/**
	 * For serialization
	 */
	public TextFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public TextFieldDescriptor(String key, String label) {
		super(key, label);
	}

	/**
	 * @return a regular expression that will be used to validate the field value 
	 */
	public String getRegExValidator() {
		return regExValidator;
	}

	/**
	 * Set a regular expression that will be used to validate the field value
	 * @param regExValidator
	 */
	public void setRegExValidator(String regExValidator) {
		this.regExValidator = regExValidator;
	}

	@Override
	public String toString() {
		return "TextFieldDescriptor [toString()=" + super.toString() + ", regExValidator=" + regExValidator + "]";
	}

}
