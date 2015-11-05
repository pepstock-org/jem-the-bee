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

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.pepstock.jem.node.security.UserPreference;
import org.pepstock.jem.node.security.UserPreferencesMap;
import org.pepstock.jem.util.CharSet;

/**
 * Deserializes from JSON to JAVA object. 
 * This is mandatory because in MONGODB field with "." or starting with "$" are not supported
 * and we want to have keys with ".".
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
class UserPreferencesMapDeserializer extends JsonDeserializer<UserPreferencesMap> {

	/* (non-Javadoc)
	 * @see org.codehaus.jackson.map.JsonDeserializer#deserialize(org.codehaus.jackson.JsonParser, org.codehaus.jackson.map.DeserializationContext)
	 */
	@Override
	public UserPreferencesMap deserialize(JsonParser parser, DeserializationContext deser) throws IOException, JsonProcessingException {
		// gets codec
		ObjectCodec codec = parser.getCodec();
		// creates json factory 
		// to deserialize the object, value of the map
		JsonFactory factory = new JsonFactory(codec);
		// creates the object
		UserPreferencesMap properties = new UserPreferencesMap();
		// reads element
		JsonNode node = parser.getCodec().readTree(parser);
		// scans all fields
		Iterator<String> iter = node.getFieldNames();
		while(iter.hasNext()){
			// gets entry
			String entry = iter.next();
			// gets JSON node
			JsonNode valueNode = node.get(entry);
			// passes thru parser
			JsonParser jp = factory.createJsonParser(valueNode.toString());
			// reads the preferences
			UserPreference property = codec.readValue(jp, UserPreference.class);
			// decodes the key, previously saved in BASE64
			String newField = new String(Base64.decodeBase64(entry), CharSet.DEFAULT);
			// adds to map
			properties.put(newField, property);
		}
		// returns map 
		return properties;
	}
}
