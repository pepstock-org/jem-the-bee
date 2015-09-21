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
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class JsonUtil {

	private static final JsonUtil INSTANCE = new JsonUtil();
	
	private ObjectMapper mapper = null;
	
	private ObjectMapper mapperPrettyPrint = null;
	
	/**
	 * To avoid any instantiation
	 */
	private JsonUtil() {
		mapper = new ObjectMapper();

		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
//			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			mapper.setDateFormat(sf);

		
		mapperPrettyPrint = new ObjectMapper();

		mapperPrettyPrint.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapperPrettyPrint.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapperPrettyPrint.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		mapperPrettyPrint.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapperPrettyPrint.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
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
	 * 
	 * @param response
	 * @param cls
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<?> deserializeList(ClientResponse response, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		return deserializeList(responseToString(response), cls);
	}
	
	/**
	 * 
	 * @param json
	 * @param cls
	 * @param asList
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws RestException
	 */
	public List<?> deserializeList(String json, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		JavaType typeInfo = mapper.getTypeFactory().constructCollectionType(List.class, cls);
		List<?> response = (List<?>) mapper.readValue(json, typeInfo);
		return response;
	}

	/**
	 * 
	 * @param json
	 * @param cls
	 * @param asList
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws RestException
	 */
	public Object deserialize(ClientResponse response, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		return deserialize(responseToString(response), cls);
	}

	/**
	 * 
	 * @param json
	 * @param cls
	 * @param asList
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws RestException
	 */
	public Object deserialize(String json, Class<?> cls) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, cls);
	}
	/**
	 * 
	 * @param obj
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @throws RestException 
	 * @throws ApiException
	 */
	public String serialize(Object obj) throws JsonGenerationException, JsonMappingException, IOException  {
		if (obj != null){
			return mapper.writeValueAsString(obj);
		} else {
			return null;
		}
	}
	
	public String responseToString(ClientResponse response) throws IOException{
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntityInputStream(), writer);
		return writer.toString();
	}
	
	public void prettyPrint(String json) throws JsonGenerationException, JsonMappingException, IOException{
		System.out.println(mapperPrettyPrint.writeValueAsString(json));
	}
}

