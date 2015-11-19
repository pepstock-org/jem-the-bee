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
package org.pepstock.jem.springbatch;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.pepstock.jem.springbatch.SpringBatchKeys;
import org.pepstock.jem.springbatch.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * is a SAX parser. It's necessary to read SpringBatch source code to extract the job name which is mandatory. It searchs the <code>id</code>
 * attribute of <code>&lt;job&gt;</code> element. <code>
 * <pre>
 * &lt;job id="jobname" parent="jem.baseJob"&gt;<br>
 * 		....<br>
 * &lt;/job&gt;<br>
 * </pre>
 * </code>
 * 
 * @see org.pepstock.jem.springbatch.JemBean
 * @author Andrea "Stock" Stocchero
 * 
 */
public class XMLParser {

	private XMLReader xmlReader;

	private StringReader reader;

	private String jobName = null;

	/**
	 * Construct parser instance, adding new MessageHandler and saving reader
	 * instance
	 * 
	 * @param reader string reader with JCL source code content
	 * @throws SAXException if XML error occurs
	 */
	public XMLParser(StringReader reader) throws SAXException {
		// creates a default XMLReader
		xmlReader = XMLReaderFactory.createXMLReader();

		// add a Handler to check and read all XML elements
		xmlReader.setContentHandler(new MessageHandler());

		// save reader
		this.reader = reader;
	}

	/**
	 * Start parsing the JCL content, looking for job name
	 * 
	 * @return job name 
	 * @throws SAXException if XML error occurs
	 * @throws IOException if IO error occurs
	 */
	public String parse() throws SAXException, IOException {
		if (reader != null) {
			xmlReader.parse(new InputSource(reader));
		}
		return jobName;
	}

	/**
	 * Closes the reader
	 * 
	 * @throws IOException if IO error occurs
	 */
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
		}
	}

	/**
	 * Inner class to check and read all XML elements of SpringBatch source
	 * code.
	 * 
	 * @see DefaultHandler
	 * @author Andrea "Stock" Stocchero
	 * 
	 */
	private class MessageHandler extends DefaultHandler {

		/**
		 * Empty construct. It calls superclass constructor
		 */
		public MessageHandler() {
			super();
		}

		/**
		 * This method is called at the beginning of parsing for every element
		 * of XML document.<br>
		 * It search for <code>id</code> attribute of <code>&lt;job&gt;</code> element .
		 * 
		 * @param uri URI representation of element
		 * @param localName local name of element
		 * @param qualifiedName complete name of element (with name-space)
		 * @param attributes all attributes of elements
		 * @throws SAXException if XML syntax error occurs
		 */
		@Override
		public void startElement(String uri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
			try {

				if (localName.equalsIgnoreCase(SpringBatchKeys.JOB_TAG)) {
					// is here if meet a "job" element. extract if "abstract"
					// attribute is present
					String abstr = Utils.getOptional(attributes, SpringBatchKeys.ABSTRACT_ATTR);
					// if is not an abstract, extract the mandatory attribute
					// "id"
					if (abstr == null) {
						String findjobName = Utils.getMandatory(attributes, SpringBatchKeys.ID_ATTR);
						// use attribute "id" as job name
						jobName = findjobName;
					}
				}
			} catch (UnsupportedEncodingException uee) {
				throw new SAXException(uee);
			} catch (Exception ex) {
				throw new SAXException(ex);
			}
		}

		/**
		 * Do nothing
		 * 
		 * @param uri URI representation of element
		 * @param localName local name of element
		 * @param qualifiedName complete name of element (with name-space)
		 * @throws SAXException if XML syntax error occurs
		 */
		@Override
		public void endElement(String uri, String localName, String qualifiedName) throws SAXException {
			// do nothing
		}
	}
}