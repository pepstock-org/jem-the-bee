/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.persistence.mongo.module;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.node.ResourceLockMap;
import org.pepstock.jem.node.resources.ResourceProperties;
import org.pepstock.jem.node.security.UserPreferencesMap;

/**
 * Jackson module where it's registering all custom serializer and deserializer.
 * This is mandatory because in MONGODB field with "." or starting with "$" are not supported
 * and we want to have keys with ".".
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class MongoModule extends SimpleModule {

	/**
	 * Loads all de/serializer necessary 
	 */
	public MongoModule() {
		super(MongoModule.class.getName(), new Version(0, 1, 1, "Jem-Mongo"));
		
		// adds custom module for MAP<String, String>
		// in the RESOURCE and JCL
		addSerializer(PropertiesWrapper.class, new PropertiesWrapperSerializer()); 
		addDeserializer(PropertiesWrapper.class, new PropertiesWrapperDeserializer());
		// adds custom module for MAP<String, ResourceProperty>
		// in the RESOURCE
		addSerializer(ResourceProperties.class, new ResourcePropertiesSerializer()); 
		addDeserializer(ResourceProperties.class, new ResourcePropertiesDeserializer());
		// adds custom module for MAP<String, ResourceLock>
		// in the DefaultRequestLock of NODES
		addSerializer(ResourceLockMap.class, new ResourceLockMapSerializer()); 
		addDeserializer(ResourceLockMap.class, new ResourceLockMapDeserializer());
		// adds custom module for MAP<String, UserPreference>
		// in the USERPREFERENCES
		addSerializer(UserPreferencesMap.class, new UserPreferencesMapSerializer());
		addDeserializer(UserPreferencesMap.class, new UserPreferencesMapDeserializer());
	}
}
