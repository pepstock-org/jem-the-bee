/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Reads a XML result after a FILE UPLOAD by a submit form.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class XmlResultViewer {
	
	/**
	 * XML element for result (root)
	 */
	public static final String RESULT_TAG = "result"; 
	
	/**
	 * XML element for message 
	 */
	public static final String MESSAGE_TAG = "message";
	
	/**
	 * XML element for return code
	 */
	public static final String RETURN_CODE_TAG = "return-code";
	
	/**
	 * To avoid any instantiation
	 */
    private XmlResultViewer() {
    }

	/**
	 * Shows a alert message based on XML parsing of result
	 * 
	 * @param title title of toast
	 * @param content content to parse, result of RPC call for file upload
	 */
	public static void showResult(String title, String content){
		
		String xml = (new HTML(content)).getText();
		Document doc = XMLParser.parse(xml);
		Node rc = doc.getElementsByTagName(RETURN_CODE_TAG).item(0);
		Node msg = doc.getElementsByTagName(MESSAGE_TAG).item(0);
		if (rc != null && msg != null){
			String retCode = rc.getFirstChild().getNodeValue();
			String message = msg.getFirstChild().getNodeValue();
			if (retCode != null){
				new Toast(MessageLevel.fromIntLevel(Integer.parseInt(retCode)), message, title).show();
				return;
			}
		}
		new Toast(MessageLevel.ERROR, "Result is not well-formed. Message received: " + xml, "Result unparsable!").show();
	}

}
