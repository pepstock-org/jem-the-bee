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
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.persistence.InputDBManager;
import org.pepstock.jem.node.persistence.JobDBManager;
import org.pepstock.jem.node.persistence.OutputDBManager;
import org.pepstock.jem.node.persistence.RoutingDBManager;
import org.pepstock.jem.node.persistence.RunningDBManager;
import org.pepstock.jem.springbatch.xml.JemBeanDefinitionParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStream;

/**
 * Is a database update implementation, which changes all jobs instance, to the new XML representation of version 2.2.
 * <br>
 * The changes are in version 2.2:<br>
 * <br>
 * <ul>
 * <li>there is ONLY 1 JCL class, is no longer depending on JCL factory</li>
 * <li>the <code>parameters</code> and <code>options</code> attributes, related to SpringBatch JCL, are elements of properties of JCL</li>
 * <li>the step representation used a attributre <code>description</code>, defined wrongly in the previous version, defined <code>descritpion</code></li>
 * </ul> 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class JobDBUpdate extends DBUpdate{

	private static final String INPUT_QUEUE = "select * from INPUT_QUEUE";
	
	private static final String RUNNING_QUEUE = "select * from RUNNING_QUEUE";
	
	private static final String OUTPUT_QUEUE = "select * from OUTPUT_QUEUE";
	
	private static final String ROUTING_QUEUE = "select * from ROUTING_QUEUE";
	
	private JobDBManager manager = null;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.migrate.DBUpdate#start()
	 */
	@Override
	void start() throws SQLException {
		LogAppl.getInstance().emit(NodeMessage.JEMC275I, Queues.INPUT_QUEUE);
		// defines the DB manager
		manager = InputDBManager.getInstance();
		// performs query to get all INPUT jobs
		query(INPUT_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC278I, Queues.INPUT_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC275I, Queues.RUNNING_QUEUE);
		// defines the DB manager		
		manager = RunningDBManager.getInstance();
		// performs query to get all RUNNING jobs
		query(RUNNING_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC278I, Queues.RUNNING_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC275I, Queues.OUTPUT_QUEUE);
		// defines the DB manager		
		manager = OutputDBManager.getInstance();
		// performs query to get all OUTPUT jobs
		query(OUTPUT_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC278I, Queues.OUTPUT_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC275I, Queues.ROUTING_QUEUE);
		// defines the DB manager		
		manager = RoutingDBManager.getInstance();
		// performs query to get all ROUTING jobs		
		query(ROUTING_QUEUE);
		LogAppl.getInstance().emit(NodeMessage.JEMC278I, Queues.ROUTING_QUEUE);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.migrate.DBUpdate#update(java.io.Reader)
	 */
	@Override
	void update(Document document) {
		Element jobIdElement = getElement(document.getDocumentElement(), "id");
		String jobId = jobIdElement.getTextContent();
	
		// creates a XStream
		XStream xs= new XStream();
		// writer
		StringWriter writer = new StringWriter();
		// maps to save parameters or option if there are
		Map<String, Object> jclMap = new HashMap<String, Object>();
		
		boolean isMigrated = false;
		try {
			// write the content into xml writer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			//+--------------------+
			//| UPDATE JCL factory |
			//+--------------------+
			// gets element
			Element jcl = getElement(document.getDocumentElement(), "jcl");
	
			// only if not null
			if (jcl != null){
				// OVERRIDE CLASS ATTRIBUTE
				// setting the JCL class name to all
				jcl.setAttribute("class", Jcl.class.getName());
				
				//+-----------------------------+
				//| UPDATE PARAMETERS attribute |
				//+-----------------------------+
				// gets all parameter nodes inside JCL
				Element parm = getElement(jcl, JemBeanDefinitionParser.PARAMETERS_ATTRIBUTE);
				// only if there is a parameter
				if (parm != null){
					// adds value to properties map
					jclMap.put(JemBeanDefinitionParser.PARAMETERS_ATTRIBUTE, parm.getTextContent());
					// removes the XML node, because OBSOLETE 
					jcl.removeChild(parm);
				}
				
				//+--------------------------+
				//| UPDATE OPTIONS attribute |
				//+--------------------------+
				// gets all options nodes inside JCL
				Element opt = getElement(jcl, JemBeanDefinitionParser.OPTIONS_ATTRIBUTE);
				if (opt != null){
					// adds value to properties map
					jclMap.put(JemBeanDefinitionParser.OPTIONS_ATTRIBUTE, opt.getTextContent());
					// removes the XML node, because OBSOLETE 
					jcl.removeChild(opt);
				}
			}
			
			//+---------------------------------------------------+
			//| UPDATE DESCRITPION element of step to DESCRIPTION |
			//+---------------------------------------------------+
			// gets all "currentStep" nodes
			// gets element
			Element step = getElement(document.getDocumentElement(), "currentStep");
			// must have ONLY 1 STEP node
			if (step != null){
				// gets element the wrong tag name: "descritpion"
				Element descr = getElement(step, "descritpion");
				if (descr != null){
					// creates a new element with the RIGHT tag name: description
					Element newDescElement = document.createElement("description");
					// sets the content
					newDescElement.setTextContent(descr.getTextContent());
					// add new element
					step.appendChild(newDescElement);
					// and remove the wrong one
					step.removeChild(descr);
				}
			}
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(writer);
			// Writes to the writer
			transformer.transform(source, result);
			// gets the job with new XML
			Job job = (Job) xs.fromXML(writer.toString());
			// adds JCL map even if is empty. Doesn't matter
			job.getJcl().setProperties(jclMap);

			// updates the database with new JOB
			String statement = manager.getSqlContainer().getUpdateStatement();
			manager.update(statement, job);
			
			LogAppl.getInstance().emit(NodeMessage.JEMC276I, job);
			isMigrated = true;
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC277E, e, jobId, e.getMessage());
		} catch (TransformerException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC277E, e, jobId, e.getMessage());
		} finally {
			if (!isMigrated){
				String xml = writer.toString();
				if (xml != null && xml.length() > 0){
					LogAppl.getInstance().emit(NodeMessage.JEMC283I, xml);
				}
			}
		}
	}
}
