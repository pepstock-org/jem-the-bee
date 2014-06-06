package org.pepstock.jem.node.resources.custom.fields;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.pepstock.jem.node.resources.custom.ResourcePartDescriptor;

/**
 * This class represents a field with one selectable value from a set of many
 * @author Marco "Fuzzo" Cuccato
 */
public class SingleSelectableListFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 4234336709246533878L;

	private Set<String> values = new HashSet<String>();
	private boolean renderAsRadio = false;
	
	/**
	 * For serialization
	 */
	public SingleSelectableListFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the fieds
	 * @param key
	 * @param label
	 * @param values
	 */
	public SingleSelectableListFieldDescriptor(String key, String label, String... values) {
		super(key, label);
		loadValues(values);
	}

	/**
	 * @return <code>true</code> if you want this field to be rendered as a radio button group, <code>false</code> (default) if you want a combo box
	 */
	public boolean isRenderAsRadio() {
		return renderAsRadio;
	}

	/**
	 * Set if you want this field to be rendered as radio or combo
	 * @param renderAsRadio
	 */
	public void setRenderAsRadio(boolean renderAsRadio) {
		this.renderAsRadio = renderAsRadio;
	}

	/**
	 * @return all possible values
	 */
	public Set<String> getValues() {
		return values;
	}

	/**
	 * Sets all the possible values
	 * @param values
	 */
	public final void setValues(String... values) {
		loadValues(values);
	}
	
	/**
	 * load all the possible values
	 * @param values
	 */
	private void loadValues(String... values) {
		for (String v : values) {
			this.values.add(v);
		}
	}

	@Override
	public String toString() {
		return "SingleSelectableListFieldDescriptor [toString()=" + super.toString() + ", values=" + values + ", renderAsRadio=" + renderAsRadio + "]";
	}

}
