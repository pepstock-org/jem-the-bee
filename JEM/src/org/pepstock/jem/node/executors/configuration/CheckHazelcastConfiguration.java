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
package org.pepstock.jem.node.executors.configuration;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.ReaderInputStream;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.xml.sax.SAXException;

/**
 * Tests if Hazelcast configuration file, updated by user interface, is consistent and valid (from XML point view).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class CheckHazelcastConfiguration extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private String content = null;
	
	/**
	 * Constructs the object with Hazelcast configuration file content to test
	 * @param content configuration file content to check
	 */
	public CheckHazelcastConfiguration(String content) {
		this.content = content;
	}

	/**
	 * Checks if content could be a Hazelcast configuration file
	 * @return always TRUE
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws Exception occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		try {
			// XML syntax check
			// because Hazelcast continues if XMl error occurs
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new ReaderInputStream(new StringReader(content)));
			return Boolean.TRUE;
		} catch (ParserConfigurationException e) {
			throw new ExecutorException(NodeMessage.JEMC241E, e, e.getMessage());
		} catch (SAXException e) {
			throw new ExecutorException(NodeMessage.JEMC241E, e, e.getMessage());
		} catch (IOException e) {
			throw new ExecutorException(NodeMessage.JEMC241E, e, e.getMessage());
		}
	}
}