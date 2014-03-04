package org.pepstock.jem.gwt.client.panels.resources.inspector;

/**
 * Provide methods for Resource Inspector 
 * @author Marco "Fuzzo" Cuccato
 */
public interface ResourceUIComponent {

	/**
	 * Checks for mandatory elements
	 * @return <code>true</code> if all mandatory attributes are set
	 */
	boolean checkMandatory();
	
	/**
	 * Validate the values
	 * @return <code>true</code> if the value match the validation regular expression (optionally provided by Descriptor
	 */
	boolean validate();
	
	/**
	 * Load resource properties to UI
	 */
	void loadProperties();

}
