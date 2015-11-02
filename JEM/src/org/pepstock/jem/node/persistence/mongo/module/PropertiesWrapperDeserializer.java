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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.util.CharSet;

/**
 * Deserializes from JSON to JAVA object. 
 * This is mandatory because in MONGODB field with "." or starting with "$" are not supported
 * and we want to have keys with ".".
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
class PropertiesWrapperDeserializer extends JsonDeserializer<PropertiesWrapper> {

	/* (non-Javadoc)
	 * @see org.codehaus.jackson.map.JsonDeserializer#deserialize(org.codehaus.jackson.JsonParser, org.codehaus.jackson.map.DeserializationContext)
	 */
	@Override
	public PropertiesWrapper deserialize(JsonParser parser, DeserializationContext arg1) throws IOException, JsonProcessingException {
		// creates the object to return
		PropertiesWrapper properties = new PropertiesWrapper();
		// reads the JSON node
		JsonNode node = parser.getCodec().readTree(parser);
		// scans all fields
		Iterator<String> iter = node.getFieldNames();
		while(iter.hasNext()){
			// gets field name
			String entry = iter.next();
			// gets STRING value
			String value = node.get(entry).getTextValue();
			// decodes the field name
			String newField = new String(Base64.decodeBase64(entry), CharSet.DEFAULT);
			// puts on map
			properties.put(newField, value);
		}
		// return map
		return properties;
	}
}
