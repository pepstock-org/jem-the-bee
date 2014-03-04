package org.pepstock.jem.gwt.client.panels.resources.inspector;

import org.pepstock.jem.node.resources.Resource;

/**
 * Defines methods for Resource handling
 * @author Marco "Fuzzo" Cuccato
 *
 */
public interface Initializable {

	/**
	 * Initialize the {@link #resource} in case
	 * of creation (not editing) of a new {@link Resource}. 
	 */
	void initializeResource();

}
