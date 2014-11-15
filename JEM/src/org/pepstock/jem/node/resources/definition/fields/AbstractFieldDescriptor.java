package org.pepstock.jem.node.resources.definition.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.definition.AbstractField;
import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * This class represent a generic resource field. 
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class AbstractFieldDescriptor extends AbstractField implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 1L;

	private boolean override = true;
	private boolean visible = true;

	/**
	 * Builds the field
	 * @param key the field key, identifier of the field
	 * @param label the field label, what the user see next to this field 
	 */
	public AbstractFieldDescriptor(String key, String label) {
		super.setKey(key);
		super.setLabel(label);
	}
	
	/**
	 * @return the override
	 */
	public boolean isOverride() {
		return override;
	}

	/**
	 * @param override the override to set
	 */
	public void setOverride(boolean override) {
		this.override = override;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return "AbstractFieldDescriptor [toString()=" + super.toString() + ", override=" + override + ", visible=" + visible + "]";
	}

}
