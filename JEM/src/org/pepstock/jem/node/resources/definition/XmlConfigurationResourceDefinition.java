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

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.engine.ResourceTemplateReader;
import org.pepstock.jem.node.resources.definition.fields.AbstractFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.BooleanValueField;
import org.pepstock.jem.node.resources.definition.fields.MultiValuesField;
import org.pepstock.jem.node.resources.definition.fields.SingleValueField;

/**
 * This is an abstract class that implements {@link ResourceDefinition}. <br>
 * In particular this class is responsible for loading the
 * {@link ResourceDescriptor} from a <code>xml</code> resource template file
 * (from file system or classpath) or url for resource user interface. When a
 * new resource type is needed, <code>ResourceDefinition</code> must be
 * implemented creating a new specific class, for example
 * <code>TestResourceDefinition</code>. <br>
 * If no file (from file system or classpath) or url configuration is needed by
 * <code>TestResourceDefinition</code>, it may implement
 * <code>ResourceDefinition</code>. <br>
 * Otherwise if file (from file system or classpath) or url configuration is
 * needed <code>TestResourceDefinition</code> must extends
 * <code>XmlConfigurationResourceDefinition</code>.
 * 
 * @see ResourceDefinition
 * @see ResourceDescriptor
 * @see ResourceTemplateReader
 * 
 * @author Alessandro Zambrini
 */
public abstract class XmlConfigurationResourceDefinition implements ResourceDefinition {

	private static final long serialVersionUID = -6651706310600815923L;

	/**
	 * It is the {@link ResourceDescriptor} loaded from <code>xml</code> file
	 * (from file system or classpath) or url.
	 */
	private ResourceDescriptor resourceDescriptor = null;

	/**
	 * Builds teh java script interpret
	 */
	public XmlConfigurationResourceDefinition() {

	}

	/**
	 * Sets the {@link ResourceDescriptor}.
	 * 
	 * @param resourceDescriptor the {@link ResourceDescriptor} to set.
	 */
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		this.resourceDescriptor = resourceDescriptor;
	}

	/**
	 * This method returns the {@link ResourceDescriptor} obtained reading the
	 * <code>xml</code> file (from file system or classpath) or url.
	 * 
	 * @return the {@link ResourceDescriptor} associated to the new type of
	 *         Resource.
	 */
	public ResourceDescriptor getDescriptor() {
		return this.resourceDescriptor;
	}

	/**
	 * It loads the {@link ResourceDescriptor} from a <code>xml</code> resource
	 * template file (from file system or classpath) or url for resource user
	 * interface.
	 * 
	 * @param resourceTemplateFile the resource template (from file system or
	 *            classpath) or url path with the definition of the resource
	 *            user interface.
	 * @param type set type of resource
	 * @param description set description of resource
	 * @throws ResourceDefinitionException if the resource template file (from
	 *             file system or classpath) or url does not exist,
	 * 
	 * @see ResourceTemplateReader
	 */
	public void loadResourceTemplateFile(URL resourceTemplateFile, String type, String description) throws ResourceDefinitionException {
		ResourceTemplateReader resourceTemplateReader = new ResourceTemplateReader(resourceTemplateFile, this);
		resourceTemplateReader.initialize(type, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.resources.definition.ResourceDefinition#
	 * completeResource(org.pepstock.jem.node.resources.Resource)
	 */
	@Override
	public void completeResource(Resource resource) {
		// nop
	}

	/**
	 * Validates all resource properties if the mandatory ones are present or if there are any unknown properties 
	 * @param resource resource to be checked
	 * @param mandatoryKeys list of mandatory keys
	 * @param allKeys list of all keys
	 * @throws ResourceDefinitionException if any mandatory keys are missing or if there are any unknown properties
	 */
	public final void validateResource(Resource resource, List<String> mandatoryKeys, List<String> allKeys) throws ResourceDefinitionException {
		Map<String, ResourceProperty> properties = resource.getProperties();
		// checks mandatory
		for (String key : mandatoryKeys){
			if (!properties.containsKey(key)){
				throw new ResourceDefinitionException(NodeMessage.JEMC136E, key);
			} else {
				ResourceProperty property = properties.get(key);
				if (!isContentCorrect(property)){
					throw new ResourceDefinitionException(NodeMessage.JEMC272E, key);
				}
			}
		}
		// checks if properties added is not defined
		for (String key : properties.keySet()){
			if (!allKeys.contains(key)){
				throw new ResourceDefinitionException(NodeMessage.JEMC271E, key);
			}
		}
	}
	
	private boolean isContentCorrect(ResourceProperty property){
		ResourceDescriptor descriptor = getDescriptor();
		for (SectionDescriptor section : descriptor.getSections()){
			for (AbstractFieldDescriptor field : section.getFields()){
				if (field.getKey().equalsIgnoreCase(property.getName())){
					if (field instanceof BooleanValueField){
						return JSRegExEvaluator.getInstance().test("true|false", property.getValue());
					} else if (field instanceof SingleValueField){
						SingleValueField svf = (SingleValueField) field;
						if (svf.getRegExValidator() != null){
							return JSRegExEvaluator.getInstance().test(svf.getRegExValidator(), property.getValue());
						} else {
							return true;
						}
					} else if (field instanceof MultiValuesField){
						MultiValuesField mvf = (MultiValuesField) field;
						if (mvf.getValues().isEmpty()){
							return true;
						} else {
							return mvf.getValues().contains(property.getValue());
						}
					}
				}
			}
		}
		return false;
	}
	
}
