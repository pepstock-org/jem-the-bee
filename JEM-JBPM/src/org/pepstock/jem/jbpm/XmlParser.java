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
 * Is a XML parser, to extract all needed JEM nodes for creating all JEM structures (data description, data sets, data sources, locks).<br>
 * It scans all tasks tagged as TASK, with taskName="Jem"
 * 
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
	 * @return list of all tasks of JCL
	 * @throws ParserConfigurationException if any exception occurs parsing XML
	 * @throws SAXException if any exception occurs parsing XML
	 * @throws IOException if any exception occurs reading file
	 */
	public final static List<TaskDescription> getTaskDescription(String jclFile) throws ParserConfigurationException, SAXException, IOException{
		// creates an input soure
		InputSource source = new InputSource(new FileReader(jclFile));
		
		// DOM document and parsing
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(source);
        
        // scan XML to read comment
        NodeList firstLevel = doc.getDocumentElement().getChildNodes();
        for (int i=0; i<firstLevel.getLength(); i++){
        	Node node = firstLevel.item(i);
        	// gets tag name 
        	String tagName = getElementName(node);
        	// if is a process, starts scanning nodes
        	if (tagName != null && PROCESS_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		if (element.hasChildNodes()){
        			// scans children only if the element has got children
        			return getTasks(element.getChildNodes());
        		}
        	}
        }
        return null;
	}
	
	/**
	 * Scans all children of PROCESS element, all BPMN tasks
	 * @param list list of task, children of process
	 * @return collection of tasks
	 */
	private static List<TaskDescription> getTasks(NodeList list){
		// creates list of tasks to return
		List<TaskDescription> result = new ArrayList<TaskDescription>();
		// scans all nodes
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	// gets tagname
        	String tagName = getElementName(node);
        	// checks if is TASK element
        	if (tagName != null && TASK_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		boolean isJemWorkItem = false;
        		// scans attributes to check if is a JEM node
        		for (int k=0; k<element.getAttributes().getLength(); k++){
        			// gets attribute and value
        			String attrName = element.getAttributes().item(k).getNodeName();
        			String value = element.getAttributes().item(k).getTextContent();
        			// checks if the task is a JEM task.
        			// chekcs if there is a namespace also
        			if (value != null && (attrName.endsWith(":"+TASK_NAME_ATTRIBUTE) || attrName.equalsIgnoreCase(TASK_NAME_ATTRIBUTE))){
        				isJemWorkItem = value.equalsIgnoreCase(JBpmKeys.JBPM_JEM_WORKITEM_NAME);
        			}
        		}
        		// if it has found JEM task, loads information to task
        		if (isJemWorkItem && element.hasChildNodes()){
        			// loads data
        			TaskDescription task = new TaskDescription();
        			task.setId(element.getAttribute(ID_ATTRIBUTE));
        			task.setName(element.getAttribute(NAME_ATTRIBUTE));
        			// gets IOSPECIFICATION, where all IO information are written
        			task.setIoSpecification(getIoSpecification(element.getChildNodes()));
        			result.add(task);
        		}
        	}
        }
        return result;
	}
	
	/**
	 * Scans all IOSPECIFICATION information. In this area you can find all parameters for the task.
	 * JEM uses this parameters to set data description, data source and locks
	 * 
	 * @param list list of nodes to scan
	 * @return IoSpecification instance
	 */
	private static IoSpecification getIoSpecification(NodeList list){
		// creates instance to return 
		IoSpecification ioSpecification = new IoSpecification();
		// scans all nodes
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	// gets tagname
        	String tagName = getElementName(node);
        	if (tagName != null){
        		// if is IO SPECIFICATION, then load all DATA INPUT
        		if (IO_SPECIFICATION_ELEMENT.equalsIgnoreCase(tagName)){
        			Element element = (Element)node;
        			if (element.hasChildNodes()){
        				// loads data input
        				loadDataInput(element.getChildNodes(), ioSpecification);
        			}
        		} else if (DATA_INPUT_ASSOCIATION_ELEMENT.equalsIgnoreCase(tagName)){
        			// if is DATA_INOUT _ASSOCIATION, scan for all assignments
        			Element element = (Element)node;
        			if (element.hasChildNodes()){
        				// scans children loading assignments
        				DataInputAssociation result = getDataInputAssociation(element.getChildNodes());
        				// checks if target_ref and value of assignment is the same
        				if (result.isValid()){
        					// adds assignment
        					ioSpecification.getAssociations().put(result.getTargetRef(), result);
        				}
        			}
        		}
            }
        }
        return ioSpecification;
	}
	
	/**
	 * Loads all data input elements
	 * @param list list of node's children 
	 * @param ioSpecification container of data inputs
	 */
	private static void loadDataInput(NodeList list, IoSpecification ioSpecification){
		// scans all nodes
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	// gets tag name
        	String tagName = getElementName(node);
        	// checks if data input element
        	if (tagName != null && DATA_INPUT_ELEMENT.equalsIgnoreCase(tagName)){
        		Element element = (Element)node;
        		// gets ID and name
        		String id = element.getAttribute(ID_ATTRIBUTE);
        		String name = element.getAttribute(NAME_ATTRIBUTE);
        		if (name != null){
        			// checks if is a data description, data source and locks
        			if (name.startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX) || 
        					name.startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX) || 
        					name.equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
        				// loads the bean 
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
	 * Loads all data assignment 
	 * @param list list of node's children
	 * @return data input association entity
	 */
	private static DataInputAssociation getDataInputAssociation(NodeList list){
		// creates the result 
		DataInputAssociation result = new DataInputAssociation();
		// scans nodes
        for (int i = 0; i < list.getLength(); i++) {
        	Node node = list.item(i);
        	// gets tagname
        	String tagName = getElementName(node);
        	if (tagName != null){
        		Element element = (Element)node;
        		// extracts teh TARGET REF data
        		if (TARGET_REF_ELEMENT.equalsIgnoreCase(tagName)){
        			result.setTargetRef(element.getTextContent());
        		}
        		// gets assignment information
        		if (ASSIGNMENT_ELEMENT.equalsIgnoreCase(tagName)){
        			// scans all children of ASSIGNMENT
        			NodeList fromTo = element.getChildNodes();
        			for (int k = 0; k < fromTo.getLength(); k++) {
        				Node fromToNode = fromTo.item(k);
        				// gets tag name
        				String subTagName = getElementName(fromToNode);
        				if (subTagName != null){
        					Element elfromTo = (Element)fromToNode;
        					// loads FROM and TO
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
	 * Returns the tag name , removing the namespace
	 * @param node node to use to extract the tag name
	 * @return tag name of node
	 */
	private static String getElementName(Node node){
		// ONLY if is a element returns teh name
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
