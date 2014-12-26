package org.pepstock.jem.node.resources.definition.fields;


/**
 * This class represents a password field, with optional regular expression validator.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.3
 */
public class PasswordFieldDescriptor extends TextFieldDescriptor {

	private static final long serialVersionUID = 0L;

	/**
	 * Empty constructor to be serialized
	 */
	public PasswordFieldDescriptor() {
		super();
	}

	/**
	 * Constructs the filed using key and label 
	 * @param key property key
	 * @param label label to be showed on UI
	 */
	public PasswordFieldDescriptor(String key, String label) {
		super(key, label);
		super.setVisible(false);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.definition.fields.TextFieldDescriptor#toString()
	 */
	@Override
	public String toString() {
		return "PasswordFieldDescriptor [toString()=" + super.toString() + "]";
	}
}
