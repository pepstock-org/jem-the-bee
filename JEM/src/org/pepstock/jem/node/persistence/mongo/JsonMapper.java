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
package org.pepstock.jem.node.persistence.mongo;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.pepstock.jem.node.persistence.mongo.module.MongoModule;

/**
 * JSON utility which contains a Jackson OBJECT mapper to serialize and deserialize objects inside MONGODB.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class JsonMapper {

	private static final JsonMapper INSTANCE = new JsonMapper();
	
	// normal mapper to creates object
	private ObjectMapper mapper = null;
	
	/**
	 * To avoid any instantiation
	 */
	private JsonMapper() {
		// creates a normal mapper 
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
		// add this module to customize
		// the serialization of json object
		// from map to object
		mapper.registerModule(new MongoModule());
	}
	
	/**
	 * Returns the instance
	 * @return the instance
	 */
	public static JsonMapper getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Returns the Object Mapper JSON
	 * @return the Object Mapper JSON
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

}

