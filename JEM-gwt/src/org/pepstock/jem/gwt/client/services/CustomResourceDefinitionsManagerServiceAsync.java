package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.resources.custom.ResourceDescriptor;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Marco "Fuzzo" Cuccato
 */
public interface CustomResourceDefinitionsManagerServiceAsync {

	/**
	 * Return a collection containing all custom {@link ResourceDescriptor}
	 * 
	 * @param callback
	 */
	void getAllResourceDescriptors(AsyncCallback<Collection<ResourceDescriptor>> callback);

	/**
	 * Returns all custom resource names
	 * 
	 * @param callback
	 */
	void getAllResourceNames(AsyncCallback<Collection<String>> callback);

	/**
	 * Return the descriptor of resource identified by <code>resourceName</code>
	 * 
	 * @param resourceName
	 *            the resource name
	 * @param callback
	 */
	void getDescriptorOf(String resourceName, AsyncCallback<ResourceDescriptor> callback);

}
