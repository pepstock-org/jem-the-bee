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
package org.pepstock.jem.jbpm;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.pepstock.jem.jbpm.xml.DataInput;
import org.pepstock.jem.jbpm.xml.DataInputAssociation;
import org.pepstock.jem.jbpm.xml.IoSpecification;
import org.pepstock.jem.jbpm.xml.TaskDescription;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class XmlParser {
	
	private static final String PROCESS_ELEMENT = "process";
	
	private static final String TASK_ELEMENT = "task";
	
	private static final String IO_SPECIFICATION_ELEMENT = "ioSpecification";

	private static final String DATA_INPUT_ELEMENT = "dataInput";
	
	private static final String DATA_INPUT_ASSOCIATION_ELEMENT = "dataInputAssociation";
	
	private static final String TARGET_REF_ELEMENT = "targetRef";
	
	private static final String ASSIGNMENT_ELEMENT = "assignment";
	
	private static final String FROM_ELEMENT = "from";
	
	private static final String TO_ELEMENT = "to";
	
	private static final String ID_ATTRIBUTE = "id";
	
	private static final String NAME_ATTRIBUTE = "name";
	
	private static final String TASK_NAME_ATTRIBUTE = "taskName";
	
	/**
	 * To avoid any instantiation
	 */
	private XmlParser() {
		
	}
	
	/**
	 * Loads all task defined as workitem based on JEM, to load all info about data description, data sources and locks.
	 * @param jclFile BPMN file to parse
	 * @return lits of all tasks of JCL
	 * @throws ParserConfigurationException if any exception occurs parsing XML
	 * @throws SAXException if any exception occurs parsing XML
	 * @throws IOException if any exception occurs reading file
	 */
	public final static List<TaskDescription> getTaskDescription(String jclFile) throws ParserConfigurationException, SAXException, IOException{
		InputSource source = new InputSource(new FileReader(jclFile));
		
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(source);
        
        // scan XML to read comment
        NodeList firstLevel = doc.getDocumentElement().getChildNodes();
        for (int i=0; i<firstLevel.getLength(); i++){
        	Node node = firstLevel.item(i);
        	String tagName = getElementName(node);
        	if (tagName != null && PROCESS_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		if (element.hasChildNodes()){
        			return getTasks(element.getChildNodes());
        		}
        	}
        }
        return null;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private static List<TaskDescription> getTasks(NodeList list){
		List<TaskDescription> result = new ArrayList<TaskDescription>();
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	String tagName = getElementName(node);
        	if (tagName != null && TASK_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		boolean isJemWorkItem = false;
        		
        		for (int k=0; k<element.getAttributes().getLength(); k++){
        			String attrName = element.getAttributes().item(k).getNodeName();
        			String value = element.getAttributes().item(k).getTextContent();
        			if (value != null){
        				if (attrName.endsWith(":"+TASK_NAME_ATTRIBUTE) || attrName.equalsIgnoreCase(TASK_NAME_ATTRIBUTE)){
        					isJemWorkItem = value.equalsIgnoreCase(JBpmKeys.JBPM_JEM_WORKITEM_NAME);
        				}
        			}
        		}
        		if (isJemWorkItem && element.hasChildNodes()){
        			TaskDescription task = new TaskDescription();
        			task.setId(element.getAttribute(ID_ATTRIBUTE));
        			task.setName(element.getAttribute(NAME_ATTRIBUTE));
        			task.setIoSpecification(getIoSpecification(element.getChildNodes()));
        			result.add(task);
        		}
        	}
        }
        return result;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private static IoSpecification getIoSpecification(NodeList list){
		IoSpecification ioSpecification = new IoSpecification();
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	String tagName = getElementName(node);
        	if (tagName != null){
        		if (IO_SPECIFICATION_ELEMENT.equalsIgnoreCase(tagName)){
        			Element element = (Element)node;
        			if (element.hasChildNodes()){
        				loadDataInput(element.getChildNodes(), ioSpecification);
        			}
        		} else if (DATA_INPUT_ASSOCIATION_ELEMENT.equalsIgnoreCase(tagName)){
        			Element element = (Element)node;
        			if (element.hasChildNodes()){
        				DataInputAssociation result = getDataInputAssociation(element.getChildNodes());
        				if (result.isValid()){
        					ioSpecification.getAssociations().put(result.getTargetRef(), result);
        				}
        			}
        		}
            }
        }
        return ioSpecification;
	}
	
	/**
	 * 
	 * @param list
	 */
	private static void loadDataInput(NodeList list, IoSpecification ioSpecification){
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	String tagName = getElementName(node);
        	if (tagName != null && DATA_INPUT_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		String id = element.getAttribute(ID_ATTRIBUTE);
        		String name = element.getAttribute(NAME_ATTRIBUTE);
        		if (name != null){
        			if (name.startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX) || 
        					name.startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX) || 
        					name.equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
        				DataInput input = new DataInput();
        				input.setId(id);
        				input.setName(name);
        				ioSpecification.getDataInputs().add(input);
        			}
        		}
        	}
        }
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private static DataInputAssociation getDataInputAssociation(NodeList list){
		DataInputAssociation result = new DataInputAssociation();
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	String tagName = getElementName(node);
        	if (tagName != null){
        		Element element = (Element)node;
        		if (TARGET_REF_ELEMENT.equalsIgnoreCase(tagName)){
        			result.setTargetRef(element.getTextContent());
        		}
        		if (ASSIGNMENT_ELEMENT.equalsIgnoreCase(tagName)){
        			NodeList fromTo = element.getChildNodes();
        			for (int k = 0; k < fromTo.getLength(); k++) {
        				Node fromToNode = fromTo.item(k);
        				String subTagName = getElementName(fromToNode);
        				if (subTagName != null){
        					Element elfromTo = (Element)fromToNode;
        					if (FROM_ELEMENT.equalsIgnoreCase(subTagName)){
        						result.setAssignmentFrom(elfromTo.getTextContent());
        					} else if (TO_ELEMENT.equalsIgnoreCase(subTagName)){
        						result.setAssignmentTo(elfromTo.getTextContent());
        					}
        				}
        			}
        		}        		
        	}
        }
        return result;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static String getElementName(Node node){
    	if (node.getNodeType() == Element.ELEMENT_NODE) {
        	Element el = (Element)node;
        	String tagName = null;
        	// checks if name space
        	if (el.getTagName().contains(":")){
        		tagName = StringUtils.substringAfter(el.getTagName(),":");
        	} else {
        		tagName = el.getTagName();
        	}
        	return tagName;
        }
		return null;
	}

}
