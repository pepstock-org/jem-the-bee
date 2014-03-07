/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pepstock.jem.node.resources.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.configuration.CustomResourceDefinition;
import org.pepstock.jem.node.resources.FtpResource;
import org.pepstock.jem.node.resources.HttpResource;
import org.pepstock.jem.node.resources.JdbcResource;
import org.pepstock.jem.node.resources.JmsResource;
import org.pepstock.jem.node.resources.JppfResource;
import org.pepstock.jem.node.resources.Resource;

import com.hazelcast.core.IMap;

/**
 * Contains all resource definitions for custom resources configuration.<br>
 * 
 * @author Alessandro Zambrini
 * 
 */
public class ResourceDefinitionsManager {

	/**
	 * It is the <code>name</code> of the optional
	 * <code>xml resource template file</code> property in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>xml resource template file</code>
	 * property value, in case someone wants to define the user interface of the
	 * custom resource via xml.
	 */
	public static final String XML_RESOURCE_TEMPLATE_FILE_PROPERTY = "jem.xml.resource.template.file";

	/**
	 * Field containing the list of all the custom resource definitions. <br>
	 * The keys are the custom resource types.
	 */
	private Map<String, ResourceDefinition> customResourceDefinitions = null;

	/**
	 * Constructor that initializes the field {@link #customResourceDefinitions}
	 * .
	 */
	public ResourceDefinitionsManager() {
		this.customResourceDefinitions = new HashMap<String, ResourceDefinition>();
	}

	/**
	 * Adds a {@link ResourceDefinition}.
	 * 
	 * @param customResourceType the type of the custom resource definition to
	 *            be added.
	 * @param customResourceDefinition the <code>ResourceDefinition</code> to be
	 *            added.
	 * @throws ResourceDefinitionException if the parameter
	 *             <code>customResourceDefinition</code> or
	 *             <code>customResourceType</code> is null.
	 */
	public void addCustomResourceDefinition(String customResourceType, ResourceDefinition customResourceDefinition) throws ResourceDefinitionException {
		if (null == customResourceType || "".equalsIgnoreCase(customResourceType.trim())) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "custom resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E.toMessage().getFormattedMessage("custom resource type"));
		}
		if (null == customResourceDefinition) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "custom resource definition class");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E.toMessage().getFormattedMessage("custom resource definition class"));
		}
		this.customResourceDefinitions.put(customResourceType, customResourceDefinition);
	}

	/**
	 * Adds a {@link ResourceDefinition}.
	 * 
	 * @param customResourceDefinition the <code>ResourceDefinition</code> to be
	 *            added.
	 * @throws ResourceDefinitionException if the parameter
	 *             <code>customResourceDefinition</code> is null or no type for
	 *             this definition has been found.
	 */
	public void addCustomResourceDefinition(ResourceDefinition customResourceDefinition) throws ResourceDefinitionException {
		if (null == customResourceDefinition) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "custom resource definition class");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E.toMessage().getFormattedMessage("custom resource definition class"));
		}
		if (null == customResourceDefinition.getResourceDescriptor()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "custom resource descriptor");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E.toMessage().getFormattedMessage("custom resource descriptor"));
		}
		String type = customResourceDefinition.getResourceDescriptor().getType();
		this.addCustomResourceDefinition(type, customResourceDefinition);
	}

	/**
	 * Returns the {@link ResourceDefinition} associated to the parameter
	 * <code>customResourceType</code>. <br>
	 * If the parameter is null or is a type not configured an exception is
	 * thrown.
	 * 
	 * @param customResourceType the custom resource type for which to get the
	 *            <code>ResourceDefinition</code>.
	 * @return the {@link ResourceDefinition} associated to the parameter
	 *         <code>customResourceType</code>.
	 * @throws ResourceDefinitionException if parameter is null or is a type not
	 *             configured.
	 */
	public ResourceDefinition getCustomResourceDefinition(String customResourceType) throws ResourceDefinitionException {
		if (null == customResourceType) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR013E, "custom resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR013E.toMessage().getFormattedMessage("custom resource type"));
		}
		ResourceDefinition def = this.customResourceDefinitions.get(customResourceType);
		if (null == def) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR014E, customResourceType);
			throw new ResourceDefinitionException(ResourceMessage.JEMR014E.toMessage().getFormattedMessage(customResourceType));
		}
		return def;
	}

	/**
	 * This method returns <code>true</code> if the parameter
	 * <code>customResourceType</code> is contained in the list of the custom
	 * resources defined by the user, otherwise it returns <code>false</code>.
	 * 
	 * @param customResourceType the resource type searched.
	 * @return <code>true</code> if the <code>customResourceType</code> is
	 *         contained in the list of the custom resources defined by the
	 *         user, otherwise it returns <code>false</code>.
	 * @throws ResourceDefinitionException if the resource type searched is
	 *             null.
	 */
	public boolean hasCustomResourceDefinition(String customResourceType) throws ResourceDefinitionException {
		if (null == customResourceType) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR018E, "custom resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR018E.toMessage().getFormattedMessage("custom resource type"));
		}
		return this.customResourceDefinitions.containsKey(customResourceType);
	}

	/**
	 * Method that load a {@link ResourceDefinition} inside Jem. <br>
	 * The parameter <code>resourceDefinitionConfiguration</code> the
	 * information of the <code>Class</code> to be used. If the
	 * <code>ResourceDefinition</code> to be loaded is an
	 * {@link XmlConfigurationResourceDefinition} and there is a
	 * <code>xml</code> resource template file configured, this file is loaded
	 * and {@link ResourceDescriptor} is builded from that file.
	 * 
	 * @param resourceDefinitionConfiguration the configuration of the
	 *            {@link ResourceDefinition} to be loaded.
	 * @param xmlResourceTemplateFile the optional <code>xml</code> resource
	 *            template for user interface.
	 * @throws ResourceDefinitionException if some error occurs.
	 */
	public void loadCustomResourceDefinition(CustomResourceDefinition resourceDefinitionConfiguration, String xmlResourceTemplateFile) throws ResourceDefinitionException {
		if (null == resourceDefinitionConfiguration) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "custom resource definition configuration");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E.toMessage().getFormattedMessage("custom resource definition configuration"));
		}
		String className = resourceDefinitionConfiguration.getClassName();
		try {
			// load by Class.forName of ResourceDefinition
			Object object = Class.forName(className).newInstance();

			// check if it's a CustomResourceDefinition. if not,
			// exception occurs. if yes, it's loaded.
			if (object instanceof ResourceDefinition) {
				ResourceDefinition resourceDefinition = (ResourceDefinition) object;
				if (null != xmlResourceTemplateFile && resourceDefinition instanceof XmlConfigurationResourceDefinition) {
					((XmlConfigurationResourceDefinition) resourceDefinition).loadResourceTemplateFile(xmlResourceTemplateFile);
					LogAppl.getInstance().emit(ResourceMessage.JEMR020I, xmlResourceTemplateFile);
				}
				if (!hasCustomResourceDefinition(resourceDefinition.getResourceDescriptor().getType())) {
					this.addCustomResourceDefinition(resourceDefinition);
					LogAppl.getInstance().emit(ResourceMessage.JEMR016I, className, resourceDefinition.getResourceDescriptor().getType());
				} else {
					// already loaded a resource with same type of current one,
					// exiting!
					LogAppl.getInstance().emit(ResourceMessage.JEMR023E, resourceDefinition.getResourceDescriptor().getType());
					throw new ResourceDefinitionException(ResourceMessage.JEMR023E.toMessage().getFormattedMessage(resourceDefinition.getResourceDescriptor().getType()));
				}
			} else {
				LogAppl.getInstance().emit(ResourceMessage.JEMR011E, className);
				throw new ResourceDefinitionException(ResourceMessage.JEMR011E.toMessage().getFormattedMessage(className));
			}
		} catch (ClassNotFoundException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E.toMessage().getFormattedMessage(className));
		} catch (InstantiationException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E.toMessage().getFormattedMessage(className));
		} catch (IllegalAccessException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E.toMessage().getFormattedMessage(className));
		}
	}

	/**
	 * This method changes a resource type. It removes the old
	 * {@link ResourceDefinition} and add the new one. Removes all the resource
	 * of the previous type
	 * 
	 * @param newResourceDefinition the new {@link ResourceDefinition}
	 * @param oldResourceType the old type to be removed
	 * 
	 * @throws ResourceDefinitionException if some error occurs.
	 */
	public void changeResourceType(ResourceDefinition newResourceDefinition, String oldResourceType) throws ResourceDefinitionException {
		if (!hasCustomResourceDefinition(newResourceDefinition.getResourceDescriptor().getType())) {
			this.customResourceDefinitions.put(newResourceDefinition.getResourceDescriptor().getType(), newResourceDefinition);
		} else {
			LogAppl.getInstance().emit(ResourceMessage.JEMR023E, newResourceDefinition.getResourceDescriptor().getType());
			throw new ResourceDefinitionException(ResourceMessage.JEMR023E.toMessage().getFormattedMessage(newResourceDefinition.getResourceDescriptor().getType()));
		}
		this.customResourceDefinitions.remove(oldResourceType);
		LogAppl.getInstance().emit(ResourceMessage.JEMR025I, oldResourceType, newResourceDefinition.getResourceDescriptor().getType());
		try {
			deleteAllResources(oldResourceType);
			LogAppl.getInstance().emit(ResourceMessage.JEMR026I, oldResourceType);
		} catch (ResourceDefinitionException rdEx) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR024W, rdEx, oldResourceType, newResourceDefinition.getResourceDescriptor().getType());
		}
	}

	/**
	 * It returns the list of all the custom {@link ResourceDefinition}
	 * configured in JEM.
	 * 
	 * @return the list of all the custom {@link ResourceDefinition} configured
	 *         in JEM.
	 */
	public Collection<ResourceDefinition> getAllCustomResourceDefinitions() {
		return customResourceDefinitions.values();
	}

	/**
	 * This method removes all common resources whose type (
	 * {@link Resource#getType()}) is equals to the parameter
	 * <code>resourceType</code>.
	 * 
	 * @param resourceType the type of the resources to be removed
	 * 
	 * @throws ResourceDefinitionException if some error occurs.
	 * 
	 * @see Resource
	 */
	public void deleteAllResources(String resourceType) throws ResourceDefinitionException {
		IMap<String, Resource> resourceMap = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		for (Resource resource : resourceMap.values()) {
			if (resource.getType().equalsIgnoreCase(resourceType)) {
				String name = resource.getName();
				try {
					// locks the key
					resourceMap.lock(name);
					// remove the resource from map
					resource = resourceMap.remove(name);
					LogAppl.getInstance().emit(ResourceMessage.JEMR019W, name, resource.getType());
				} finally {
					// unlocks always the key
					resourceMap.unlock(name);
				}
			}
		}
	}

	/**
	 * This method clean all common resources removing all resources whose type
	 * ({@link Resource#getType()}) is not configured or is not one of the
	 * predefined resources.
	 * 
	 * @throws ResourceDefinitionException if some error occurs.
	 * 
	 * @see #isValidResourceType(String)
	 * @see Resource
	 */
	public void deleteNotExistingTypesResources() throws ResourceDefinitionException {
		IMap<String, Resource> resourceMap = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		for (Resource resource : resourceMap.values()) {
			if (!isValidResourceType(resource.getType())) {
				String name = resource.getName();
				try {
					// locks the key
					resourceMap.lock(name);
					// remove the resource from map
					resource = resourceMap.remove(name);
					LogAppl.getInstance().emit(ResourceMessage.JEMR019W, name, resource.getType());
				} finally {
					// unlocks always the key
					resourceMap.unlock(name);
				}
			}
		}
	}

	/**
	 * It checks if the resource type is configured, or is one of the the
	 * predefined resources: <li>{@link FtpResource} <li>{@link HttpResource}
	 * <li>{@link JdbcResource} <li>{@link JmsResource} <li>{@link JppfResource}
	 * 
	 * @param resourceType the resource type to be checked.
	 * @return <code>true</code> if the resource type is configured, or is one
	 *         of the the predefined resources, <code>false</code> otherwise.
	 * @throws ResourceDefinitionException if some error occurs.
	 */
	private boolean isValidResourceType(String resourceType) throws ResourceDefinitionException {
		// splitted in 2 boolean to avoid issue on code analyser
		boolean protocolBasedResources = resourceType.equalsIgnoreCase(FtpResource.TYPE) || 
				resourceType.equalsIgnoreCase(HttpResource.TYPE) || 
				resourceType.equalsIgnoreCase(JdbcResource.TYPE);
		boolean complexResources = resourceType.equalsIgnoreCase(JmsResource.TYPE)	|| 
				resourceType.equalsIgnoreCase(JppfResource.TYPE);
		if (protocolBasedResources|| 
				complexResources || 
				Main.CUSTOM_RESOURCE_DEFINITION_MANAGER.hasCustomResourceDefinition(resourceType)) {
			return true;
		}
		return false;
	}

	/**
	 * @return all custom resource names
	 */
	public Collection<String> getAllCustomResourceNames() {
		return new ArrayList<String>(customResourceDefinitions.keySet());
	}

	/**
	 * Returns the {@link ResourceDescriptor} identified by it's type
	 * 
	 * @param resourceType the type of the {@link ResourceDescriptor} you want
	 * @return a {@link ResourceDescriptor}
	 */
	public ResourceDescriptor getResourceDescriptorOf(String resourceType) {
		return customResourceDefinitions.get(resourceType).getResourceDescriptor();
	}

}
