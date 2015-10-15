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
package org.pepstock.jem.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;

import com.sun.jersey.api.client.ClientResponse;

/**
 * JSON utility which contains a Jackson OBJECT mapper to serialize and deserialize objects on REST calls and response.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class JsonUtil {

	private static final JsonUtil INSTANCE = new JsonUtil();
	
	// normal mapper to creates object
	private ObjectMapper mapper = null;
	
	// object mapper to pretty print objects 
	private ObjectMapper mapperPrettyPrint = null;
	
	/**
	 * To avoid any instantiation
	 */
	private JsonUtil() {
		// creates a normal mapper 
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);

		// creates a mapper to pretty print objects
		mapperPrettyPrint = new ObjectMapper();
		mapperPrettyPrint.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapperPrettyPrint.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapperPrettyPrint.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		// this is the difference with the other mapper
		// it indents the JSON
		mapperPrettyPrint.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}
	
	/**
	 * Returns the instance
	 * @return the instance
	 */
	public static JsonUtil getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Returns the Object Mapper JSON
	 * @return the Object Mapper JSON
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * Transforms a REST response entity to a LIST of objects.
	 * @param <T>
	 * @param response REST response
	 * @param cls Java type of list to return
	 * @return list of objects of class instance
	 * @throws JsonParseException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	public <T> List<T> deserializeList(ClientResponse response, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
		return deserializeList(responseToString(response), cls);
	}
	
	/**
	 * Transforms a JSON string to a LIST of objects.
	 * @param json JSON string
	 * @param cls Java type of list to return
	 * @return list of objects of class instance
	 * @throws JsonParseException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> deserializeList(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
		JavaType typeInfo = mapper.getTypeFactory().constructCollectionType(List.class, cls);
		return (List<T>) mapper.readValue(json, typeInfo);
	}

	/**
	 * Transforms a REST response entity to an object.
	 * @param response REST response
	 * @param cls Java type of the object
	 * @return object of class instance
	 * @throws JsonParseException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	public Object deserialize(ClientResponse response, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		return deserialize(responseToString(response), cls);
	}

	/**
	 * Transforms a JSON string to an object.
	 * @param json JSON string
	 * @param cls Java type of the object
	 * @return object of class instance
	 * @throws JsonParseException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	public Object deserialize(String json, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, cls);
	}
	
	/**
	 * Transforms an object to a JSON string.
	 * @param obj object to serialize into a string
	 * @return a JSON string which represents the object
	 * @throws JsonGenerationException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	public String serialize(Object obj) throws JsonGenerationException, JsonMappingException, IOException  {
		if (obj != null){
			return mapper.writeValueAsString(obj);
		} else {
			return null;
		}
	}
	
	/**
	 * Reads the REST response body into a string
	 * @param response REST response
	 * @return a string which represents the body of REST response
	 * @throws IOException if any error occurs
	 */
	public String responseToString(ClientResponse response) throws IOException{
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntityInputStream(), writer);
		return writer.toString();
	}
	
	/**
	 * Prints on standard output the JSON string, in pretty format
	 * @param json JSON string to print
	 * @throws JsonGenerationException if any JSON error occurs
	 * @throws JsonMappingException if any JSON error occurs
	 * @throws IOException if any JSON error occurs
	 */
	public void prettyPrint(Object json) throws JsonGenerationException, JsonMappingException, IOException{
		System.out.println(mapperPrettyPrint.writeValueAsString(json));
	}
}

