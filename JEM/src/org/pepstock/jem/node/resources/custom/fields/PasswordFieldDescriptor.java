package org.pepstock.jem.node.resources.custom.fields;


/**
 * This class represents a password field, with optional regular expression validator
 * @author Marco "Fuzzo" Cuccato
 */
public class PasswordFieldDescriptor extends TextFieldDescriptor {

	private static final long serialVersionUID = 0L;

	/**
	 * 
	 */
	public PasswordFieldDescriptor() {
		super();
	}

	/**
	 * @param key
	 * @param label
	 */
	public PasswordFieldDescriptor(String key, String label) {
		super(key, label);
		super.setVisible(false);
	}

	
}
