package org.pepstock.jem.node.resources.custom.fields;

import com.google.gwt.user.client.ui.CheckBox;

/**
 * The most simple implementation of {@link MultiSelectableListFieldDescriptor}, rendered as a {@link CheckBox}es  
 * @author Marco "Fuzzo" Cuccato
 */
public class CheckBoxesListFieldDescriptor extends MultiSelectableListFieldDescriptor {

	private static final long serialVersionUID = -3414197323940075115L;

	/**
	 * Builds the field
	 */
	public CheckBoxesListFieldDescriptor() {
	}

	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public CheckBoxesListFieldDescriptor(String key, String label) {
		super(key, label);
	}

	@Override
	public String toString() {
		return "CheckBoxesListFieldDescriptor [toString()=" + super.toString() + "]";
	}

}
