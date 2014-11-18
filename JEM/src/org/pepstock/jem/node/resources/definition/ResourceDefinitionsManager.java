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
package org.pepstock.jem.node.resources.definition;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.pepstock.jem.annotations.Mode;
import org.pepstock.jem.annotations.ResourceMetaData;
import org.pepstock.jem.annotations.ResourceTemplate;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.configuration.CommonResourceDefinition;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.engine.ResourceTemplateException;
import org.pepstock.jem.util.ClassLoaderUtil;
import org.pepstock.jem.util.ObjectAndClassPathContainer;
import org.pepstock.jem.util.VariableSubstituter;

import com.hazelcast.core.IMap;

/**
 * Contains all resource definitions for resources configuration.<br>
 * 
 * @author Alessandro Zambrini
 * 
 */
public class ResourceDefinitionsManager {

	/**
	 * Field containing the list of all resource definitions. <br>
	 * The keys are there source types.
	 */
	private Map<String, ResourceDefinition> resourceDefinitions = null;

	/**
	 * Constructor that initializes the field {@link #resourceDefinitions}
	 * .
	 */
	public ResourceDefinitionsManager() {
		this.resourceDefinitions = new HashMap<String, ResourceDefinition>();
	}

	/**
	 * Adds a {@link ResourceDefinition}.
	 * 
	 * @param resourceType the type of there source definition to
	 *            be added.
	 * @param resourceDefinition the <code>ResourceDefinition</code> to be
	 *            added.
	 * @throws ResourceDefinitionException if the parameter
	 *             <code>resourceDefinition</code> or
	 *             <code>resourceType</code> is null.
	 */
	public void addResourceDefinition(String resourceType, ResourceDefinition resourceDefinition) throws ResourceDefinitionException {
		if (null == resourceType || "".equalsIgnoreCase(resourceType.trim())) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E, "resource type");
		}
		if (null == resourceDefinition) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "resource definition class");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E, "resource definition class");
		}
		this.resourceDefinitions.put(resourceType, resourceDefinition);
	}

	/**
	 * Adds a {@link ResourceDefinition}.
	 * 
	 * @param resourceDefinition the <code>ResourceDefinition</code> to be
	 *            added.
	 * @throws ResourceDefinitionException if the parameter
	 *             <code>resourceDefinition</code> is null or no type for
	 *             this definition has been found.
	 */
	public void addResourceDefinition(ResourceDefinition resourceDefinition) throws ResourceDefinitionException {
		if (null == resourceDefinition) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "resource definition class");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E, "resource definition class");
		}
		if (null == resourceDefinition.getDescriptor()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "resource descriptor");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E, "resource descriptor");
		}
		String type = resourceDefinition.getDescriptor().getType();
		this.addResourceDefinition(type, resourceDefinition);
	}

	/**
	 * Returns the {@link ResourceDefinition} associated to the parameter
	 * <code>resourceType</code>. <br>
	 * If the parameter is null or is a type not configured an exception is
	 * thrown.
	 * 
	 * @param resourceType there source type for which to get the
	 *            <code>ResourceDefinition</code>.
	 * @return the {@link ResourceDefinition} associated to the parameter
	 *         <code>resourceType</code>.
	 * @throws ResourceDefinitionException if parameter is null or is a type not
	 *             configured.
	 */
	public ResourceDefinition getResourceDefinition(String resourceType) throws ResourceDefinitionException {
		if (null == resourceType) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR013E, "resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR013E, "resource type");
		}
		ResourceDefinition def = this.resourceDefinitions.get(resourceType);
		if (null == def) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR014E, resourceType);
			throw new ResourceDefinitionException(ResourceMessage.JEMR014E, resourceType);
		}
		return def;
	}

	/**
	 * This method returns <code>true</code> if the parameter
	 * <code>resourceType</code> is contained in the list of the custom
	 * resources defined by the user, otherwise it returns <code>false</code>.
	 * 
	 * @param resourceType the resource type searched.
	 * @return <code>true</code> if the <code>resourceType</code> is
	 *         contained in the list of there sources defined by the
	 *         user, otherwise it returns <code>false</code>.
	 * @throws ResourceDefinitionException if the resource type searched is
	 *             null.
	 */
	public boolean hasResourceDefinition(String resourceType) throws ResourceDefinitionException {
		if (null == resourceType) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR018E, "resource type");
			throw new ResourceDefinitionException(ResourceMessage.JEMR018E, "resource type");
		}
		return this.resourceDefinitions.containsKey(resourceType);
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
	 * @param props properties with all variable to substitute
	 * @throws ResourceDefinitionException if some error occurs.
	 */
	public void loadResourceDefinition(CommonResourceDefinition resourceDefinitionConfiguration, Properties props) throws ResourceDefinitionException {
		if (null == resourceDefinitionConfiguration) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR012E, "resource definition configuration");
			throw new ResourceDefinitionException(ResourceMessage.JEMR012E, "resource definition configuration");
		}
		String className = resourceDefinitionConfiguration.getClassName();
		try {
			// load by Class.forName of ResourceDefinition
			ObjectAndClassPathContainer oacp = ClassLoaderUtil.loadAbstractPlugin(resourceDefinitionConfiguration, props);
			Object object = oacp.getObject();
			
			// check if it's a CustomResourceDefinition. if not,
			// exception occurs. if yes, it's loaded.
			if (object instanceof ResourceDefinition) {
				ResourceDefinition resourceDefinition = (ResourceDefinition) object;
				String type = null;
				String description = null;
				Annotation ann = object.getClass().getAnnotation(ResourceMetaData.class);
				if (ann != null){
					ResourceMetaData metaData = (ResourceMetaData)ann;
					type = metaData.type();
					description = metaData.description();
				} 
				if (resourceDefinition instanceof XmlConfigurationResourceDefinition) {
					// gets URL
					URL resTemplate = getResourceTemplateURL(object, props);
					((XmlConfigurationResourceDefinition) resourceDefinition).loadResourceTemplateFile(resTemplate, type, description);
					LogAppl.getInstance().emit(ResourceMessage.JEMR020I, resTemplate);
				} else {
					if (type == null){
						throw new ResourceTemplateException(ResourceMessage.JEMR004E);
					}
					resourceDefinition.getDescriptor().setType(type);
					if (description != null){
						resourceDefinition.getDescriptor().setDescription(description);
					}
				}
				
				if (!hasResourceDefinition(resourceDefinition.getDescriptor().getType())) {
					this.addResourceDefinition(resourceDefinition);
					LogAppl.getInstance().emit(ResourceMessage.JEMR016I, className, resourceDefinition.getDescriptor().getType());
				} else {
					// already loaded a resource with same type of current one,
					// exiting!
					LogAppl.getInstance().emit(ResourceMessage.JEMR023E, resourceDefinition.getDescriptor().getType());
					throw new ResourceDefinitionException(ResourceMessage.JEMR023E, resourceDefinition.getDescriptor().getType());
				}
			} else {
				LogAppl.getInstance().emit(ResourceMessage.JEMR011E, className);
				throw new ResourceDefinitionException(ResourceMessage.JEMR011E, className);
			}
		} catch (MalformedURLException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E, className);
		} catch (InstantiationException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E, className);
		} catch (IllegalAccessException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E, className);
		} catch (ClassNotFoundException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E, className);
		} catch (IOException e) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR015E, e, className);
			throw new ResourceDefinitionException(ResourceMessage.JEMR015E, className);
		}
	}
	
	/** 
	 * Get the URL of resource template
	 * @param object
	 * @param props
	 * @return
	 * @throws ResourceTemplateException
	 * @throws MalformedURLException 
	 */
	private URL getResourceTemplateURL(Object object, Properties props) throws ResourceTemplateException, MalformedURLException{
		URL resTemplate = null;
		Annotation ann = object.getClass().getAnnotation(ResourceTemplate.class);
		if (ann != null){
			ResourceTemplate p = (ResourceTemplate)ann;
			String value = VariableSubstituter.substitute(p.value(), props);
			if (value == null){
				LogAppl.getInstance().emit(ResourceMessage.JEMR001E);
				throw new ResourceTemplateException(ResourceMessage.JEMR001E);
			}
			if (Mode.FROM_CLASSPATH.equalsIgnoreCase(p.mode())){
				resTemplate = object.getClass().getClassLoader().getResource(value);
			} else if (Mode.FROM_FILESYSTEM.equalsIgnoreCase(p.mode())){
				File file = new File(value);
				if (!file.exists() || !file.isFile()){
					LogAppl.getInstance().emit(ResourceMessage.JEMR002E, value);
					throw new ResourceTemplateException(ResourceMessage.JEMR002E, value);
				}
				resTemplate = file.toURI().toURL();
			} else if (Mode.FROM_URL.equalsIgnoreCase(p.mode())){
				resTemplate = new URL(value);
			} else {
				LogAppl.getInstance().emit(ResourceMessage.JEMR027W, p.mode());
				// by default it looks inside of classpath
				resTemplate = this.getClass().getClassLoader().getResource(value);
			}
			if (resTemplate == null){
				LogAppl.getInstance().emit(ResourceMessage.JEMR002E, value);
				throw new ResourceTemplateException(ResourceMessage.JEMR002E, value);
			}
		} else {
			LogAppl.getInstance().emit(ResourceMessage.JEMR001E);
			throw new ResourceTemplateException(ResourceMessage.JEMR001E);
		}
		return resTemplate;
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
		if (!hasResourceDefinition(newResourceDefinition.getDescriptor().getType())) {
			this.resourceDefinitions.put(newResourceDefinition.getDescriptor().getType(), newResourceDefinition);
		} else {
			LogAppl.getInstance().emit(ResourceMessage.JEMR023E, newResourceDefinition.getDescriptor().getType());
			throw new ResourceDefinitionException(ResourceMessage.JEMR023E, newResourceDefinition.getDescriptor().getType());
		}
		this.resourceDefinitions.remove(oldResourceType);
		LogAppl.getInstance().emit(ResourceMessage.JEMR025I, oldResourceType, newResourceDefinition.getDescriptor().getType());
		try {
			deleteAllResources(oldResourceType);
			LogAppl.getInstance().emit(ResourceMessage.JEMR026I, oldResourceType);
		} catch (ResourceDefinitionException rdEx) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR024W, rdEx, oldResourceType, newResourceDefinition.getDescriptor().getType());
		}
	}

	/**
	 * It returns the list of all the{@link ResourceDefinition}
	 * configured in JEM.
	 * 
	 * @return the list of all the{@link ResourceDefinition} configured
	 *         in JEM.
	 */
	public Collection<ResourceDefinition> getAllResourceDefinitions() {
		return resourceDefinitions.values();
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
		return Main.RESOURCE_DEFINITION_MANAGER.hasResourceDefinition(resourceType);
	}

	/**
	 * @return all resource names
	 */
	public Collection<String> getAllResourceNames() {
		return new ArrayList<String>(resourceDefinitions.keySet());
	}

	/**
	 * Returns the {@link ResourceDescriptor} identified by it's type
	 * 
	 * @param resourceType the type of the {@link ResourceDescriptor} you want
	 * @return a {@link ResourceDescriptor}
	 */
	public ResourceDescriptor getResourceDescriptorOf(String resourceType) {
		return resourceDefinitions.get(resourceType).getDescriptor();
	}

}
