package org.pepstock.jem.gwt.client.panels.resources.inspector.custom;

import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;
import org.pepstock.jem.node.resources.custom.ResourcePartDescriptor;

/**
 * Base abstract class for custom resource property panel. Has support for {@link ResourceDescriptor}.
 * <code>D</code> is the type of configuration descriptor
 * @author Marco "Fuzzo" Cuccato
 * @param <D> 
 *
 */
public abstract class CustomResourcePropertiesPanel<D extends ResourcePartDescriptor> extends ResourcesPropertiesPanel {
	
	protected D descriptor = null;
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the descriptor, contains rendering information
	 * @param hasComplexWidget 
	 */
	public CustomResourcePropertiesPanel(Resource resource, D descriptor, boolean hasComplexWidget) {
		super(resource, hasComplexWidget);
		this.descriptor = descriptor;
	}

	@Override
	public abstract void initializeResource();

	/**
	 * @return the resource descriptor
	 */
	public D getDescriptor() {
		return descriptor;
	}
	
}
