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
package org.pepstock.jem.ant;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages related to the ANT utilities. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea 'Stock" Stocchero
 * @version 1.0	
 */
public enum AntMessage implements MessageInterface{

	/**
	 * "Data path is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.data</code> is null.<br>Check node log because some exception should be occurred.")
	JEMA001E(1, "Data path is null", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\" not found", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description is not defined in JCL but the program needs for it.<br> Please check JCL data description definition.")
	JEMA002E(2, "Data description \"{0}\" not found", MessageLevel.ERROR),

	/**
	 * "Unable to create a data description implementation", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description implementation method throw an exception.<br> Please check JCL data description definition.")
	JEMA003E(3, "Unable to create a data description implementation", MessageLevel.ERROR),
	
	/**
	 * "RMI port is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.rmi.port</code> is null.<br>Check node log because some exception should be occurred.")
	JEMA004E(4, "RMI port is null", MessageLevel.ERROR),
	
	/**
	 * "Data description doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description in JCL does't have <code>name</code> attribute.<br>Check data description definition.")
	JEMA005E(5, "Data description doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : MultiDataset is not allowed in STEP-EXEC", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data description with many datasets and this is possibile because the datasets are cahnged in environment variables and not in a list."+
	 "<br>Check data description definition.")
	JEMA006E(6, "Data description \"{0}\", disposition \"{1}\" : MultiDataset is not allowed in STEP-EXEC", MessageLevel.ERROR),
	
	/**
	 * "Output path is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.output</code> is null.<br>Check node log because some exception should be occurred.")
	JEMA007E(7, "Output path is null", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description with many datasets without using SHR disposition."+
	 "<br>Check data description definition.")
	JEMA008E(8, "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description reference without using SHR disposition which is mandatory."+
	 "<br>Check data description definition.")
	JEMA009E(9, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Disposition wrong with a reference: only SHR is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", \"{1}\" : Dataset doesn't exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to a dataset which doesn't exist."+
	 "<br>Check data description definition or file system.")
	JEMA010E(10, "Data description \"{0}\", \"{1}\" : Dataset doesn't exist", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\", \"{2}\" : File not found
	 */
	@Description(explanation = "It occurs when you try to access to file which is not found."+
	 "<br>Check data description definition or file system.")
	JEMA011E(11, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : File not found", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset is not a reference
	 */
	@Description(explanation = "It occurs when you try to access to a reference but dataset is not a reference."+
	 "<br>Check data description definition.")
	JEMA012E(12, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset is not a reference", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to temporary dataset without using NEW disposition which is mandatory."+
	 "<br>Check data description definition.")
	JEMA013E(13, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Disposition wrong with a temporary file: only NEW is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set without <code>name</code> attribute."+
	 "<br>Check data description definition.")
	JEMA014E(14, "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to dataset using NEW disposition but the data set already exists."+
	 "<br>Check data description definition and file system.")
	JEMA015E(15, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset already exists", MessageLevel.ERROR),	
	
	/**
	 *"Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a output stream, as expect<br>Check JCL definition.")
	JEMA016E(16, "Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR),

	/**
	 *"Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a input stream, as expect<br>Check JCL definition.")
	JEMA017E(17, "Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR),
	
	/**
	 *  "Data description \"{0}\" is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a mandatory data descritpion is not defined in JCL."+
	 "<br>Check data description definition.")
	JEMA018E(18, "Data description \"{0}\" is missing", MessageLevel.ERROR),
	
	/**
	 *   "Shell is not defined", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a shell script task is defined but the shell to use is missing."+
	 "<br>Check JCL task definition.")
	JEMA019E(19, "Shell is not defined", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set using data source attribute which is empty even if defined."+
	 "<br>Check data description definition.")
	JEMA020E(20, "Data source value is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the name attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMA021E(21, "Data source name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the resource attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMA022E(22, "Data source resource attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Lock name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a lock but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMA023E(23, "Lock name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * Task \"{0}\" is not present in the procedure \"{0}\": overriding is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to override a task inside a procedure but the task is not defined in procedure definition."+
	 "<br>Check procedure definition.")
	JEMA024E(24, "Task \"{0}\" is not present in the procedure \"{0}\": overriding is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Only one nested element allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define more than one task inside of procedure definition."+
	 "<br>use <code>SEQUENTIAL</code> tasks of ANT if more than one task is needed.")
	JEMA025E(25, "Only one nested element allowed", MessageLevel.ERROR),
	
	/**
	 * "'addTask' method is called with a task that is not an unknown element but is a \"{0}\" ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a task inside a procedure but the task is not a UnknownElement."+
	 "<br>Check ANT log because this is ANT internal error.")
	JEMA026E(26, "'addTask' method is called with a task that is not an unknown element but is a \"{0}\" ", MessageLevel.ERROR),
	
	/**
	 * "Data source is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data source element doesn't contain <code>name</code> or <code>resource</code>."+
	 "<br>Check JCL data source definition.")
	JEMA027E(27, "Data source is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR),
	
	/**
	 * "Datasource is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to a resource but you're not authorized."+
	 "<br>Check permissions on resources that job user has got.")
	JEMA028E(28, "Access to \"{0}\" is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Key  \"{0}\" for resource  \"{1}\" can not be overrided", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to override a property which is defined as not overridable."+
	 "<br>Check resource definition.")
	JEMA029E(29, "Key  \"{0}\" for resource  \"{1}\" can not be overrided", MessageLevel.ERROR),
	
	/**
	 * "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to use a resource which has go a type not supported."+
	 "<br>Check with JEM administrator which types of resources are available.")
	JEMA030E(30, "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job name is missing.<br>Check JCL and fill name attribute project element or use <code>jem.job.name</code> property.")
	JEMA031E(31, "Job name is null", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the <code>jem.job.lockingScope</code> property is wrong.<br>Check JCL using the right locking scope.")
	JEMA032E(32, "Property \"{0}\" must be "+AntKeys.ANT_JOB_SCOPE+", "+AntKeys.ANT_STEP_SCOPE+" or "+AntKeys.ANT_TASK_SCOPE+", instead of \"{1}\"", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It informs what kind of locking scope the job is using.")
	JEMA033I(33, "Job \"{0}\" is using \"{1}\" locking scope.", MessageLevel.INFO),
	
	/**
	 * "Data description reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data description is currently used.")
	JEMA034I(34, "Data description instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Data source reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data source is currently used.")
	JEMA035I(35, "Data source instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Unable to store GDG", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the commit phase on GDG.<br>Check with JEM adnimistrator.")
	JEMA036E(36, "Unable to complete storing GDG : {0}", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the unbind phase of JNDI resources.<br>Check with JEM adnimistrator.")
	JEMA037E(37, "Unable to unbind resources from JNDI: {0}", MessageLevel.ERROR),
	
	/**
	 * "RMI object {0} is not binded", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it tries to get object by RMI but is not binded.<br>Check with JEM adnimistrator.")
	JEMA038E(38, "RMI object {0} is not binded", MessageLevel.ERROR),
	
	/**
	 * "Security Manager already instantiated.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it tries to get a new batch security manager but is already instantiated.<br>Check with JEM adnimistrator.")
	JEMA039E(39, "Security Manager already instantiated.", MessageLevel.ERROR),
	
	/**
	 * "Error while starting job {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when for any exception, ANT is stopped during the initialization phase.<br>Check with JEM adnimistrator.")
	JEMA040E(40, "Error while starting job {0}", MessageLevel.ERROR),
	
	/**
	 * "Dataset name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a dataset but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMA041E(41, "Dataset name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Sort is using the following comparator class: {0}",  MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility. It show which comparator class is used.")
	JEMA042I(42, "Sort is using the following comparator class: {0}", MessageLevel.INFO),
	
	/**
	 * "Sort is using default comparator class",  MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility without any comparator. It uses the default comparator.")
	JEMA043I(43, "Sort is using default comparator class", MessageLevel.INFO),
	
	/**
	 * "Unable to load sort statements from COMMAND data description : {0}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility but you don't put any command. It uses the default comparator.")
	JEMA044W(44, "Unable to load sort statements from COMMAND data description: {0}", MessageLevel.WARNING),
	
	/**
	 * "Sort statements from COMMAND data description: {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when you are using SORT utility. It shows the commands to use to sort.")
	JEMA045I(45, "Sort statements from COMMAND data description: {0}", MessageLevel.INFO),
	
	/**
	 * "Syntax error parsing sort statement \"{0}\": {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility and commands contain some mistakes.<br> Please check teh syntax of your command.")
	JEMA046W(46, "Syntax error parsing sort statement \"{0}\": {1}", MessageLevel.WARNING),
	
	/**
	 * "JCL Validation error, look:\"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JCL validation fails. <br>Check JCL definition.")
	JEMA047E(47, "JCL Validation error, look:\"{0}\"", MessageLevel.ERROR),

	/**
	 * "File not found, look:\"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the file is not found<br>Check definition.")
	JEMA048E(48, "JCL validator file not found, look:\"{0}\"", MessageLevel.ERROR),

	/**
	 * "Invalid file name, look:\"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the file name has an invalid value<br>Check definition.")
	JEMA049E(49, "JCL validator file has an invalid value, look:\"{0}\"", MessageLevel.ERROR),

	/**
	 * "Invalid SAX-compatible TransformerFactory", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there isn't a SAX-compatible TransformerFactory<br>Check xalan library.")
	JEMA050E(50, "JCL validator did not find a SAX-compatible TransformerFactory", MessageLevel.ERROR),

	/**
	 * "Annunce to have successfully loaded the xslt validator with \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "Annunce to have successfully loaded the xslt validator.")
	JEMA051I(51, "JCL validator successfully loaded with \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "WrapperTask accepts ONLY a ANT task", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you added more than a ANT task to a wrapper task.<br>Please check JCL defintion.")
	JEMA052E(52, "WrapperTask accepts ONLY a ANT task", MessageLevel.ERROR),
	
	/**
	 * "Binary path is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.bianry</code> is null.<br>Check node log because some exception should be occurred.")
	JEMA053E(53, "Binary path is null", MessageLevel.ERROR),

	/**
	 * "COMMAND attribute is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the ExecBinaryTask is used but COMMAND is not allowed.<br> Please use EXECUTABLE.")
	JEMA054E(54, "COMMAND attribute is not allowed", MessageLevel.ERROR),
	
	/**
	 * "EXECUTABLE attribute is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the ExecBinaryTask is used but EXECUTABKE is missing.<br> Please use EXECUTABLE.")
	JEMA055E(55, "EXECUTABLE attribute is missing", MessageLevel.ERROR),
	
	/**
	 * "{0} is not supported", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the ExecBinaryTask is used but operating system is not supported.")
	JEMA056E(56, "{0} is not supported", MessageLevel.ERROR),
	
	/**
	 * "JCL validator is not successfully loaded from \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the validator engine is not able to load validator file.<br>Please check validator file defintion.")
	JEMA057E(57, "Unable to load JCL validator from \"{0}\"", MessageLevel.ERROR),
	
	/**
	 * "Unable to parse sort statements from COMMAND data description.", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when you are using SORT utility but you had got an error parsng or reading statement. It uses the default comparator.")
	JEMA058W(58, "Unable to parse sort statements from COMMAND data description.", MessageLevel.WARNING),
	
	/**
	 * "Unable to create internal lock manager.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to create a locker which should lock resourcess.<br>Please contact your JEM administrators.")
	JEMA059E(59, "Unable to create internal lock manager.", MessageLevel.ERROR),
	
	/**
	 * "Unable to get lock for resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to get lock for requested resources.<br>Please contact your JEM administrators.")
	JEMA060E(60, "Unable to get lock for resources.", MessageLevel.ERROR),
	
	/**
	 * "Unable to unlock resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to unlock resources.<br>Please contact your JEM administrators.")
	JEMA061E(61, "Unable to unlock resources.", MessageLevel.ERROR);


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
	private AntMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.ANT.getCode(), messageContent, level);
	}
	
	/**
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}