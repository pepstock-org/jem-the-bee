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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.persistence.database.DBPoolManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Abstract class to use to migrate data in the database.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
abstract class DBUpdate {

	/**
	 * To avoid any instantiation, extra package
	 */
	DBUpdate() {
	}
	
	/**
	 * Method called to start the migration.
	 * 
	 * @throws SQLException if any error occurs 
	 */
	abstract void start() throws SQLException;
	
	/**
	 * Method called after the query on database.
	 * <br>
	 * The argument of this method is the XML representation of
	 * object on database.
	 * 
	 * @param currentContent XML document representation of the object on database
	 */
	abstract void update(Document document);
	
	/**
	 * Performs the query, passed as argument, and calls the update with XML object.
	 * <br>
	 * The method is configured to receive a result table with 2 fields:<br>
	 * <br>
	 * 1. key of table<br>
	 * 2. XML object<br>
	 *  
	 * @param query SQL statement to execute on database to get objects in XMl format
	 * @throws SQLException  if any error occurs 
	 */
	@SuppressWarnings("resource")
	void query(String query) throws SQLException{
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// creates statement
			stmt = connection.prepareStatement(query);
			// sets resource names where condition
			// checks if is a string or long (long used only for queue of HC)
			// executes query
			rs = stmt.executeQuery();

			// checks if I have the result. ONLY 1 row if expected. If more, is
			// an error because the resource name
			// is a primary key of table
			while (rs.next()) {
				try {
					// get CLOB field which contains RESOURCE XML serialization
					// creates XML document factories
					DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = documentFactory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(rs.getCharacterStream(2)));
					update(document);
				} catch (ParserConfigurationException e) {
					throw new SQLException(e.getMessage(), e);
				} catch (SAXException e) {
					throw new SQLException(e.getMessage(), e);
				} catch (IOException e) {
					throw new SQLException(e.getMessage(), e);
				}
			}
		} finally{
			// closes statement and result set
			try {
				if (stmt != null){
					stmt.close();
				}			
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				connection.close();
			}
		}
	}
	
	/**
	 * Returns the first element of a node. This is helpful when you need a element
	 * which is unique.
	 * @param parent parent element to start searching
	 * @param name name of element to get
	 * @return the XML element
	 */
	Element getElement(Element parent, String name){
		NodeList nodes = parent.getElementsByTagName(name);
		// only if there is a parameter
		if (nodes.getLength() == 1){
			// gets node
			Node node = nodes.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// gets element
				return (Element) node;
			}
		}
		return null;
	}
	
}
