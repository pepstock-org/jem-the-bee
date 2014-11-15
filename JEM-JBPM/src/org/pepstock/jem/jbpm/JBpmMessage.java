/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea 'Stock" Stocchero
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

import org.apache.commons.lang.StringUtils;
import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages related to the JBPM utilities. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea 'Stock" Stocchero
 * @version 2.2	
 */
public enum JBpmMessage implements MessageInterface{

	/**
	 * "Mandatory variable \"{0}\" is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the JEM work item doesn't have all necessary parameter.<br>Check JBPM JCL.")
	JEMM001E(1, "Mandatory variable \"{0}\" is missing", MessageLevel.ERROR), 
	
	/**
	 * "Current Task \"{0}\" not found", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the JEM work item is not able to get the task related to itself.<br> Please check JBPM JCL.")
	JEMM002E(2, "Current Task with ID \"{0}\" not found", MessageLevel.ERROR), 

	/**
	 * "Unable to create a data description implementation", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description implementation method throw an exception.<br> Please check JCL data description definition.")
	JEMM003E(3, "Unable to create a data description implementation", MessageLevel.ERROR),
	
	/**
	 * "WorkiItem class \"{0}\" is not an instance of JemWorkItem", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>" + "Check the class name and classpath of job because is not a JemWorkItem.")
	JEMM004E(4, "WorkiItem class \"{0}\" is not an instance of JemWorkItem", MessageLevel.ERROR),
	
	/**
	 * "Data description doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description in JCL does't have <code>name</code> attribute.<br>Check data description definition.")
	JEMM005E(5, "Data description doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Unable to load work item class \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>Check the class name and classpath of job")
	JEMM006E(6, "Unable to load work item class \"{0}\"", MessageLevel.ERROR),
	
	/**
	 * "Output path is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.output</code> is null.<br>Check node log because some exception should be occurred.")
	JEMM007E(7, "Output path is null", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description with many datasets without using SHR disposition."+
	 "<br>Check data description definition.")
	JEMM008E(8, "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description reference without using SHR disposition which is mandatory."+
	 "<br>Check data description definition.")
	JEMM009E(9, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Disposition wrong with a reference: only SHR is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", \"{1}\" : Dataset doesn't exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to a dataset which doesn't exist."+
	 "<br>Check data description definition or file system.")
	JEMM010E(10, "Data description \"{0}\", \"{1}\" : Dataset doesn't exist", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\", \"{2}\" : File not found
	 */
	@Description(explanation = "It occurs when you try to access to file which is not found."+
	 "<br>Check data description definition or file system.")
	JEMM011E(11, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : File not found", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset is not a reference
	 */
	@Description(explanation = "It occurs when you try to access to a reference but dataset is not a reference."+
	 "<br>Check data description definition.")
	JEMM012E(12, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset is not a reference", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to temporary dataset without using NEW disposition which is mandatory."+
	 "<br>Check data description definition.")
	JEMM013E(13, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Disposition wrong with a temporary file: only NEW is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set without <code>name</code> attribute."+
	 "<br>Check data description definition.")
	JEMM014E(14, "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to dataset using NEW disposition but the data set already exists."+
	 "<br>Check data description definition and file system.")
	JEMM015E(15, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset already exists", MessageLevel.ERROR),	
	
	/**
	 *"Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a output stream, as expect<br>Check JCL definition.")
	JEMM016E(16, "Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR),

	/**
	 *"Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a input stream, as expect<br>Check JCL definition.")
	JEMM017E(17, "Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR),
	
	/**
	 *  "Task name:\"{0}\", id:\"{1}\"  is started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a task of JBPM flow has started.")
	JEMM018I(18, "Task name:\"{0}\", id:\"{1}\"  started", MessageLevel.INFO),
	
	/**
	 *  "Task name:\"{0}\", id:\"{1}\"  is ended", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a task of JBPM flow has ended.")
	JEMM019I(19, "Task name:\"{0}\", id:\"{1}\"  ended", MessageLevel.INFO),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set using data source attribute which is empty even if defined."+
	 "<br>Check data description definition.")
	JEMM020E(20, "Data source value is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the name attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMM021E(21, "Data source name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the resource attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMM022E(22, "Data source resource attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Lock name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a lock but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMM023E(23, "Lock name attribute is empty", MessageLevel.ERROR),
	
	/**
	 *  "Job \"{0}\"  started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a process of JBPM flow has started.")
	JEMM024I(24, "Process \"{0}\" started", MessageLevel.INFO),
	
	/**
	 *  "Job \"{0}\" ended", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a process of JBPM flow has ended.")
	JEMM025I(25, "Process \"{0}\" ended", MessageLevel.INFO),
	
	/**
	 * "List of current tasks is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the engine is not able to extract all tasks necessary to be tracked.<br> Please contact your JEM administrator .")
	JEMM026E(26, "List of current tasks is null", MessageLevel.ERROR),
	
	/**
	 * "Data source is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data source element doesn't contain <code>resource</code>."+
	 "<br>Check JCL data source definition.")
	JEMM027E(27, "Data source is not well defined: 'resource' is null", MessageLevel.ERROR),
	
	/**
	 * "Datasource is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to a resource but you're not authorized."+
	 "<br>Check permissions on resources that job user has got.")
	JEMM028E(28, "Access to \"{0}\" is not allowed", MessageLevel.ERROR),
	
	/**
	 * "An exception [\"{0}\"] happened in process instance [\"{1}\"] of process [\"{2}\"] in node [id: \"{3}\", name: \"{4}\"]", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JBPM work flow went in error and the launcher manages the exception.<br>Check the exception occurred.")
	JEMM029E(29, "An exception [\"{0}\"] happened in process instance [\"{1}\"] of process [\"{2}\"] in node [id: \"{3}\", name: \"{4}\"]", MessageLevel.ERROR),
	
	/**
	 * "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to use a resource which has go a type not supported."+
	 "<br>Check with JEM administrator which types of resources are available.")
	JEMM030E(30, "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job name is missing.<br>Check JCL and fill name attribute project element or use <code>jem.job.name</code> property.")
	JEMM031E(31, "Job name is null", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the <code>jem.job.lockingScope</code> property is wrong.<br>Check JCL using the right locking scope.")
	JEMM032E(32, "Property \"{0}\" must be "+JBpmKeys.JBPM_JOB_SCOPE+", "+JBpmKeys.JBPM_STEP_SCOPE+" or "+JBpmKeys.JBPM_TASK_SCOPE+", instead of \"{1}\"", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It informs what kind of locking scope the job is using.")
	JEMM033I(33, "Job is using \"{0}\" locking scope.", MessageLevel.INFO),
	
	/**
	 * "Data description reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data description is currently used.")
	JEMM034I(34, "Data description instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Data source reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data source is currently used.")
	JEMM035I(35, "Data source instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Unable to store GDG", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the commit phase on GDG.<br>Check with JEM adnimistrator.")
	JEMM036E(36, "Unable to complete storing GDG : {0}", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the unbind phase of JNDI resources.<br>Check with JEM adnimistrator.")
	JEMM037E(37, "Unable to unbind resources from JNDI: {0}", MessageLevel.ERROR),
	
	/**
	 * "RMI object {0} is not binded", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it tries to get object by RMI but is not binded.<br>Check with JEM adnimistrator.")
	JEMM038E(38, "RMI object {0} is not binded", MessageLevel.ERROR),
	
	/**
	 * "Security Manager already instantiated.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it tries to get a new batch security manager but is already instantiated.<br>Check with JEM adnimistrator.")
	JEMM039E(39, "Security Manager already instantiated.", MessageLevel.ERROR),
	
	/**
	 * "Error while starting job {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when for any exception, JBPM is stopped during the initialization phase.<br>Check with JEM adnimistrator.")
	JEMM040E(40, "Error while starting job {0}", MessageLevel.ERROR),
	
	/**
	 * "Dataset name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a dataset but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMM041E(41, "Dataset name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Sort is using the following comparator class: {0}",  MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility. It show which comparator class is used.")
	JEMM042I(42, "Sort is using the following comparator class: {0}", MessageLevel.INFO),
	
	/**
	 * "Sort is using default comparator class",  MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility without any comparator. It uses the default comparator.")
	JEMM043I(43, "Sort is using default comparator class", MessageLevel.INFO),
	
	/**
	 * "Unable to load sort statements from COMMAND data description : {0}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility but you don't put any command. It uses the default comparator.")
	JEMM044W(44, "Unable to load sort statements from COMMAND data description: {0}", MessageLevel.WARNING),
	
	/**
	 * "Sort statements from COMMAND data description: {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility. It shows the commands to use to sort.")
	JEMM045I(45, "Sort statements from COMMAND data description: {0}", MessageLevel.INFO),
	
	/**
	 * "Syntax error parsing sort statement \"{0}\": {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility and commands contain some mistakes.<br> Please check the syntax of your command.")
	JEMM046W(46, "Syntax error parsing sort statement \"{0}\": {1}", MessageLevel.WARNING),
	
	/**
	 * "JCL Validation error, look:\"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JCL validation fails. <br>Check JCL definition.")
	JEMM047E(47, "JCL Validation error, look:\"{0}\"", MessageLevel.ERROR),

	/**
	 * "Not possible to know if the work-item \"{0}\" is completed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a task ended abnormally wothout any excpetion.<br>The launcher will complete the internal actions.")
	JEMM048E(48, "Not possible to know if the work-item \"{0}\" is completed", MessageLevel.ERROR),

	/**
	 * "Invalid arguments are passed to launcher", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JBPM laucnher has been scheduled.<br>Please contact your JEM administrator.")
	JEMM049E(49, "Invalid arguments are passed to launcher", MessageLevel.ERROR),

	/**
	 * "Disposition is missing for data description \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the dispostion of data description is missing.<br>Please have a look to JCL.")
	JEMM050E(50, "Disposition is missing for data description \"{0}\"", MessageLevel.ERROR),

	/**
	 * "SYSOUT has been specified with a dataset for data description \"{0}\". Not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you put both SYSOUT and a DSN parameters for a data description.<br>Please have a look to JCL.")
	JEMM051E(51, "SYSOUT has been specified with a dataset for data description \"{0}\". Not allowed", MessageLevel.ERROR),
	
	/**
	 * "More than 1 dataset has been specified with DATASOURCE key word for data description \"{0}\". Not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you put both DATASOURE and more than 1 dataset for a data description.<br>Please have a look to JCL.")
	JEMM052E(52, "More than 1 dataset has been specified with DATASOURCE key word for data description \"{0}\". Not allowed", MessageLevel.ERROR),
	
	/**
	 * "Dataset name and content are null for data description \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you put nothing for a data description.<br>Please have a look to JCL.")
	JEMM053E(53, "Dataset name and content are null for data description \"{0}\"", MessageLevel.ERROR),

	/**
	 * "PROPERTIES syntax error for data source \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you put a wrong syntax for properteis for a data source.<br>Please have a look to JCL.")
	JEMM054E(54, "PROPERTIES syntax error for data source \"{0}\"", MessageLevel.ERROR),
	
	/**
	 * "ATTENTION", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM is not able to finalize the operations after work item execution.<br> Please see the exception.")
	JEMM055E(55, StringUtils.center("ATTENTION", 40, "-").concat("\n").concat("{0}"), MessageLevel.ERROR),
	
	/**
	 * "Invalid class name: {0} ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the class name passed to work item is not valid (contains blanks).<br> Please have a look to JCL.")
	JEMM056E(56, "Invalid class name: {0} ", MessageLevel.ERROR),
	
	/**
	 * "Task \"{0}\" is ended with return code {1}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a task ended and shows the return code.")
	JEMM057I(57, "Task \"{0}\" is ended with return code {1}", MessageLevel.INFO),
	
	/**
	 * "Unable to parse sort statements from COMMAND data description.", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility but you had got an error parsng or reading statement. It uses the default comparator.")
	JEMM058W(58, "Unable to parse sort statements from COMMAND data description.", MessageLevel.WARNING),
	
	/**
	 * "Unable to create internal lock manager.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to create a locker which should lock resourcess.<br>Please contact your JEM administrators.")
	JEMM059E(59, "Unable to create internal lock manager.", MessageLevel.ERROR),
	
	/**
	 * "Unable to get lock for resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to get lock for requested resources.<br>Please contact your JEM administrators.")
	JEMM060E(60, "Unable to get lock for resources.", MessageLevel.ERROR),
	
	/**
	 * "Unable to unlock resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to unlock resources.<br>Please contact your JEM administrators.")
	JEMM061E(61, "Unable to unlock resources.", MessageLevel.ERROR),
	
	/**
	 *"\"{0}\" bytes have been written", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the copy task is ended, showing how many bytes have been written.")
	JEMM062I(62, "\"{0}\" bytes have been written", MessageLevel.INFO),
	
	/**
	 * "Unable to get process", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you can't get the process.<br>Please contact your JEM administrators.")
	JEMM063E(63, "Unable to get process", MessageLevel.ERROR);
	
	/**
	 * The {@link Message} created in the constructor corresponding to instances of ANT utilities. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private JBpmMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, "M", messageContent, level);
	}
	
	/**
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}