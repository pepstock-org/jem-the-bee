package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService;
import org.pepstock.jem.gwt.server.services.CommonResourceDefinitionsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;

/**
 * GWT service that provide methods to manage custom common resource definitions.
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class CustomResourceDefinitionsManagerServiceImpl extends DefaultManager implements CustomResourceDefinitionsManagerService {

	private static final long serialVersionUID = 1L;

	private transient CommonResourceDefinitionsManager customResourceDefinitionsManager = null;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getDescriptorOf(java.lang.String)
	 */
    @Override
    public ResourceDescriptor getDescriptorOf(String resourceName) throws JemException {
		checkIsEnable();
		initManager();
		try {
			return customResourceDefinitionsManager.getDescriptorOf(resourceName);
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}

    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getAllResourceDescriptors()
	 */
    @Override
    public Collection<ResourceDescriptor> getAllResourceDescriptors() throws JemException {
		checkIsEnable();
		initManager();
		try {
			return customResourceDefinitionsManager.getAllDescriptors();
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getAllResourceNames()
	 */
    @Override
    public Collection<String> getAllResourceNames() throws JemException {
		checkIsEnable();
		initManager();
		try {
			return customResourceDefinitionsManager.getAllResourceNames();
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}

	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
     */
	private synchronized void initManager() throws JemException {
		if (customResourceDefinitionsManager == null) {
			try {
				customResourceDefinitionsManager = new CommonResourceDefinitionsManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, ex);
				throw new JemException(ex.getMessage());
			}
		}
	}

}
