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
package org.pepstock.jem.util.migrate;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.persistence.database.CommonResourcesDBManager;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.XmlUtil;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;

/**
 * Is a database update implementation, which changes all resources instance, to the new XML representation of version 2.2.
 * <br>
 * The changes are in version 2.2:<br>
 * <br>
 * <ul>
 * <li>the attribute <code>username</code> has been changed in <code>userid</code></li>
 * </ul> 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class ResourceDBUpdate extends DBUpdate{
	
	private static final String RESOURCES_QUEUE = "select * from COMMON_RESOURCES_MAP";
	
	private static final List<String> TYPES = Collections.unmodifiableList(Arrays.asList("ftp", "jdbc", "jms", "http", "jppf"));
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.migrate.DBUpdate#start()
	 */
	@Override
	void start() throws SQLException {
		LogAppl.getInstance().emit(NodeMessage.JEMC279I);
		// starts querying 
		query(RESOURCES_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC282I);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.migrate.DBUpdate#update(org.w3c.dom.Document)
	 */
	@Override
	void update(Document document) {
		String resourceName =document.getDocumentElement().getAttribute("name");
		
		// gets XStream for resources
		XStream xs= XmlUtil.getXStream();
		// flag to understand if the resources has been changed
		boolean isTransformed = false;
		// creates a writer
		StringWriter writer = new StringWriter();
		try {
			// gets the resource type
			String type = document.getDocumentElement().getAttribute("type");
			// if is the OOTB of JEM 2.1, then changes
			if (TYPES.contains(type)){
				// gets all property nodes
				NodeList propertiesNodes = document.getElementsByTagName("property");
				// scans all to search the property "name"
				for (int i=0; i<propertiesNodes.getLength(); i++){
					// gets nodes
					Node propertyNode = propertiesNodes.item(i);
					// checks is is an element
					if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
						// casts 
						Element property = (Element) propertyNode;
						// gets the attributes name to check the value
						String name = property.getAttribute("name");
						// checks if name is equals to username
						if ("username".equalsIgnoreCase(name)){
							// changes the attribute
							property.setAttribute("name", CommonKeys.USERID);
							isTransformed = true;
						}
					}
				}
				if (isTransformed){
					// write the content into xml writer
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(document);
					StreamResult result = new StreamResult(writer);
					// writes the XML 
					transformer.transform(source, result);
					// gets resource object
					Resource res = (Resource) xs.fromXML(writer.toString());
					// saves the object by DB manager
					String statement = CommonResourcesDBManager.getInstance().getSqlContainer().getUpdateStatement();
					CommonResourcesDBManager.getInstance().update(statement, res);
					
					LogAppl.getInstance().emit(NodeMessage.JEMC280I, res);
				}
			}
		} catch (Exception e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC281E, e, resourceName, e.getMessage());
			String xml = writer.toString();
			if (xml != null && xml.length() > 0){
				LogAppl.getInstance().emit(NodeMessage.JEMC283I, xml);
			}
		}
	}
}
