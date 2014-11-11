package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * Base abstract class for custom resource property panel. Has support for {@link ResourceDescriptor}.
 * <code>D</code> is the type of configuration descriptor
 * @author Marco "Fuzzo" Cuccato
 * @param <D> 
 *
 */
public abstract class CommonResourcePropertiesPanel<D extends ResourcePartDescriptor> extends ResourcesPropertiesPanel {
	
	protected D descriptor = null;
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the descriptor, contains rendering information
	 * @param hasComplexWidget 
	 */
	public CommonResourcePropertiesPanel(Resource resource, D descriptor, boolean hasComplexWidget) {
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
