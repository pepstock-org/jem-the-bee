/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea 'Stock" Stocchero
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
public enum SpringBatchMessage implements MessageInterface{

	/**
	 * "Data path is null", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when the system property <code>jem.data</code> is null.<br>Check node log because some exception should be occurred.")
	JEMS001E(1, "Data path is null", MessageLevel.ERROR),
	
	/**
	 * "RMI port is null", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when the system property <code>jem.rmi.port</code> is null.<br>Check node log because some exception should be occurred.")
	JEMS002E(2, "RMI port is null", MessageLevel.ERROR),
	
	/**
	 * "Data description doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a data description in JCL does't have <code>name</code> attribute.<br>Check data description definition.")
	JEMS003E(3, "Data description doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : MultiDataset is not allowed in STEP-EXEC", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data description with many datasets to write and this is not allowed."+
	 "<br>Check data description definition.")
	JEMS004E(4, "Data description \"{0}\", disposition \"{1}\" : MultiDataset is not allowed with ItemWriter", MessageLevel.ERROR),
	
	/**
	 * "Output path is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the system property <code>jem.output</code> is null.<br>Check node log because some exception should be occurred.")
	JEMS005E(5, "Output path is null", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemReader: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description with ItemReader without using SHR disposition."+
	 "<br>Check data description definition.")
	JEMS006E(6, "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemReader: only SHR is allowed", MessageLevel.ERROR),	
	

	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to temporary dataset without using NEW disposition which is mandatory."+
	 "<br>Check data description definition.")
	JEMS007E(7, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Disposition wrong with a temporary file: only NEW is allowed", MessageLevel.ERROR),	
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set without <code>name</code> attribute."+
	 "<br>Check data description definition.")
	JEMS008E(8, "Data description \"{0}\", disposition \"{1}\" : Dataset doesn't have 'name' attribute", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a multi dataset: only SHR is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to dataset using NEW disposition but the data set already exists."+
	 "<br>Check data description definition and file system.")
	JEMS009E(9, "Data description \"{0}\", disposition \"{1}\", \"{2}\" : Dataset already exists", MessageLevel.ERROR),	
	
	/**
	 *"Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when data description is not a output stream, as expect<br>Check JCL definition.")
	JEMS010E(10, "Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR),

	/**
	 *"Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when data description is not a input stream, as expect<br>Check JCL definition.")
	JEMS011E(11, "Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data set using data source attribute which is empty even if defined."+
	 "<br>Check data description definition.")
	JEMS012E(12, "Data source value is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the name attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMS013E(13, "Data source name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source value is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a data source but the resource attribute is empty even if defined."+
	 "<br>Check data source definition.")
	JEMS014E(14, "Data source resource attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Lock name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a lock but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMS015E(15, "Lock name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Data source is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data source element doesn't contain <code>resource</code>."+
	 "<br>Check JCL data source definition.")
	JEMS016E(16, "Data source is not well defined: 'resource' is null", MessageLevel.ERROR),
	
	/**
	 * "Datasource is not well defined: 'name' or 'resource' is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to a resource but you're not authorized."+
	 "<br>Check permissions on resources that job user has got.")
	JEMS017E(17, "Access to \"{0}\" is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Key  \"{0}\" for resource  \"{1}\" can not be overrided", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to override a property which is defined as not overridable."+
	 "<br>Check resource definition.")
	JEMS018E(18, "Key  \"{0}\" for resource  \"{1}\" can not be overrided", MessageLevel.ERROR),
	
	/**
	 * "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to use a resource which has go a type not supported."+
	 "<br>Check with JEM administrator which types of resources are available.")
	JEMS019E(19, "Resorce \"{0}\" with type \"{1}\" is not supported", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job name is missing.<br>Check JCL and fill name attribute project element or use <code>jem.job.name</code> property.")
	JEMS020E(20, "Job name is null", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the <code>lockingScope</code> property is wrong.<br>Check JCL using the right locking scope.")
	JEMS021E(21, "Property 'lockingScope' must be "+SpringBatchKeys.JOB_SCOPE+"' or '"+SpringBatchKeys.STEP_SCOPE+", instead of \"{0}\"", MessageLevel.ERROR),
	
	/**
	 * "Job name is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It informs what kind of locking scope the job is using.")
	JEMS022I(22, "Job \"{0}\" is using \"{1}\" locking scope.", MessageLevel.INFO),
	
	/**
	 * "Data description reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data description is currently used.")
	JEMS023I(23, "Data description instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Data source reference: \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs what data source is currently used.")
	JEMS024I(24, "Data source instance: \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "Unable to store GDG", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the commit phase on GDG.<br>Check with JEM adnimistrator.")
	JEMS025E(25, "Unable to complete storing GDG : {0}", MessageLevel.ERROR),
	
	/**
	 * "RMI object {0} is not binded", MessageLevel.ERROR
	 */
	JEMS026E(26, "RMI object {0} is not binded", MessageLevel.ERROR),
	
	/**
	 * "Security Manager already instantiated.", MessageLevel.ERROR
	 */
	JEMS027E(27, "Security Manager already instantiated.", MessageLevel.ERROR),
	/**
	 * "Error while starting job {0}", MessageLevel.ERROR
	 */
	JEMS028E(28, "Error while starting job {0}", MessageLevel.ERROR),
	
	/**
	 * "Dataset name attribute is empty", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to define a dataset but the name attribute is empty even if defined."+
	 "<br>Check lock definition.")
	JEMS029E(29, "Dataset name attribute is empty", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemWriter: SHR is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to data description with ItemWriter using SHR disposition."+
	 "<br>Check data description definition.")
	JEMS030E(30, "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemWriter: SHR is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemWriter: SHR is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a item sets resources which are read by JCL but the container has more than 1 resource or null"+
	 "<br>Check data description definition.")
	JEMS031E(31, "Resources instance is invalid. Must 1 but is {0}", MessageLevel.ERROR),
	
	/**
	 * "Data description \"{0}\", disposition \"{1}\" : Disposition wrong with a ItemWriter: SHR is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a item doesn't have any delegate."+
	 "<br>Check data description definition.")
	JEMS032E(32, "Delegate is mandatory property to set. Instance is null", MessageLevel.ERROR),
	
	/**
	 * "Input resource must not be null, must exists, must be readabl and must be a Dataset.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a item is not able to read the resource for one of described cases."+
	 "<br>Check data description definition.")
	JEMS033E(33, "Input resource must not be null, must exists, must be readable and must be a Dataset.", MessageLevel.ERROR),
	
	/**
	 * ""Reader must be open before it can be read", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a reader engine is null during the read phase."+
	 "<br>Check how the item reader is called.")
	JEMS034E(34, "Reader must be open before it can be read", MessageLevel.ERROR),
	
	/**
	 * "JEM bean is not defined", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM bean is not present in JCL."+
	 "<br>Check JCL and add JEM bean defining all necessary attributes.")
	JEMS035E(35, "\"{0}\" bean is not defined", MessageLevel.ERROR),
	
	/**
	 * "JEM bean is not a JemBean instance but \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when for JEM bean it's defined a different class in JCL."+
	 "<br>Check JCL and correct the JEM bean defintion using JemBean class.")
	JEMS036E(36, "\"{0}\" bean is not a \"{1}\" bean instance but \"{2}\"", MessageLevel.ERROR),
	
	/**
	 * "Unable to create dataset implementation: {0}", MessageLevel.WARNING);
	 */
	@Description(explanation = "It occurs when the reader is not able to create a dataset implementation."+
	 "<br>Check JCL and correct the JCL or contact your JM administrator.")
	JEMS037W(37, "Unable to create dataset implementation: {0}", MessageLevel.WARNING),
	
	/**
	 * "Unable to load data description \"{0}\", cause: \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to create a data description implementation.<br>Check data description definition and generated exception.")
	JEMS038E(38, "Unable to load data description \"{0}\", cause: \"{1}\"", MessageLevel.ERROR),
	
	/**
	 * "Unable to get lock for resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to get lock for requested resources.<br>Please contact your JEM administrators.")
	JEMS039E(39, "Unable to get lock for resources.", MessageLevel.ERROR),
	
	/**
	 * "Unable to unlock resources.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to unlock resources.<br>Please contact your JEM administrators.")
	JEMS040E(40, "Unable to unlock resources.", MessageLevel.ERROR),
	
	/**
	 * "Unable to create internal lock manager.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to create a locker which should lock resourcess.<br>Please contact your JEM administrators.")
	JEMS041E(41, "Unable to create internal lock manager.", MessageLevel.ERROR),

	/**
	 * "Unable to notify change step status to JEM node.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are not able to send by RMI the change step status.<br>Please contact your JEM administrators.")
	JEMS042E(42, "Unable to notify change step status to JEM node.", MessageLevel.ERROR),
	
	/**
	 * "Unable to consume necessary information by JNDI context", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to access to JNDI.<br> Please check generated exception.")
	JEMS043E(43, "Unable to consume necessary information by JNDI context", MessageLevel.ERROR),
	
	/**
	 * "Unable to load GDG : {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during GDG information loading.<br>Check with JEM adnimistrator.")
	JEMS044E(44, "Unable to load GDG : {0}", MessageLevel.ERROR),
	
	/**
	 * "{0} ended with exception: {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during tasklet execution.")
	JEMS045E(45, "{0} ended with exception: {1}", MessageLevel.ERROR),
	
	/**
	 * "Echo: {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when Echotasklet is running.")
	JEMS046I(46, "Echo: {0}", MessageLevel.INFO),
	
	/**
	 * "Unable to unbind resources from JNDI: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the unbind phase of JNDI resources.<br>Check with JEM adnimistrator.")
	JEMS047E(47, "Unable to unbind resources from JNDI: {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to create JNDI context: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an exception is thrown during the JNDI context creation.<br>Check with JEM adnimistrator.")
	JEMS048E(48, "Unable to create JNDI context: {0}", MessageLevel.ERROR),

	/**
	 * "Data description \"{0}\", disposition \"{1}\" dataset does not exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to access to dataset using a disposition different from NEWbut the data does not exists."+
	 "<br>Check data description definition and file system.")
	JEMS049E(49, "Data description \"{0}\", disposition \"{1}\" dataset does not exist", MessageLevel.ERROR),
	
	/**
	 * "Class \"{0}\" is not an instance of Runnable or without ToBeExecuted annotation", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>" + "Check the class name and classpath of job because is not a Runnable or without ToBeExecuted annotation.")
	JEMS050E(50, "Class \"{0}\" is not an instance of Runnable or without ToBeExecuted annotation", MessageLevel.ERROR),
	
	/**
	 * "Mandatory variable \"className\" is missing", MessageLevel.ERRO
	 */
	@Description(explanation = "It occurs when the JEM work item doesn't have all necessary parameter.<br>Check JCL.")
	JEMS051E(51, "Mandatory variable \"className\" is missing", MessageLevel.ERROR),
	
	/**
	 * "Unable to load class \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>Check the class name and classpath of job")
	JEMS052E(52, "Unable to load class \"{0}\"", MessageLevel.ERROR),
	
	/**
	 *"\"{0}\" bytes have been written", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the copy task is ended, showing how many bytes have been written.")
	JEMS053I(53, "\"{0}\" bytes have been written", MessageLevel.INFO),
	
	/**
	 * "Class \"{0}\" does not have any public static void main method", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the launcher can try to execute a java main class but the class does not have any main method.<br>" + "Check the class  and the signature of main method.")
	JEMS054E(54, "Class \"{0}\" does not have any public static void main method", MessageLevel.ERROR),

	/**
	 * "Data source for job restart-ability is null", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the JEM transaction manager or job explorer don't have any data source instance to manage the restartability of jobs.<br>" + "Check the JEM SpringBatch JCL factory in JEM environment configuration file.")
	JEMS055E(55, "Data source for job restartability is null", MessageLevel.ERROR),

	/**
	 * "Unable to get data source definition from JEM", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the JEM transaction manager or job explorer don't get datasource defintion by RMI from JEM.<br>" + "Check the root cause exception.")
	JEMS056E(56, "Unable to get data source definition from JEM", MessageLevel.ERROR),
	
	/**
	 * "Data source type is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the data source type property is not set.<br>" + "Check the JEM SpringBatch JCL factory in JEM environment configuration file.")
	JEMS057E(57, "Data source type is missing", MessageLevel.ERROR),

	/**
	 * "Unable to find the schema for {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the data source type is incorrect because the schema file is not present for this type.<br>" + "Check the JEM SpringBatch JCL factory in JEM environment configuration file.")
	JEMS058E(58, "Unable to find the schema for {0}", MessageLevel.ERROR),
	
	/**
	 * "Database schema for {0} has been loaded", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the data source schema file has been loaded.")
	JEMS059I(59, "Database schema for {0} has been loaded", MessageLevel.INFO),
	
	/**
	 * "Database schema must be created because missing", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the data source schema must be created.")
	JEMS060I(60, "Database schema must be created because missing", MessageLevel.INFO),
	
	/**
	 * "Database schema has been created", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the data source schemahas been created.")
	JEMS061I(61, "Database schema has been created", MessageLevel.INFO),
	
	/**
	 * "Unable to create database schema", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it was not able to create the database schema for job repository.<br>" + "Check the root cause exception.")
	JEMS062E(62, "Unable to create database schema", MessageLevel.ERROR),
	
	/**
	 * Repair job {0} because cancelled or crashed within a node", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when it a job has been cancelled and JEM node repairs the job repository database.")
	JEMS063I(63, "Repair job {0} because cancelled or crashed within a node", MessageLevel.INFO),
	
	/**
	 * "Unable to repair the cancelled job (executionId: {0})", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it was not able to repair (update) the database of job repository.<br>" + "Check the root cause exception.")
	JEMS064E(64, "Unable to repair the cancelled job executionId: {0})", MessageLevel.ERROR),
	
	/**
	 * "Unable to clean job repository after job (instanceId: {0}) correctly completed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it was not able to clean (delete) the database of job repository when the job ended correctly.<br>" + "Check the root cause exception.")
	JEMS065E(65, "Unable to clean job repository after job (instanceId: {0}) correctly completed", MessageLevel.ERROR),
	
	/**
	 * "Unable to get data base type from datasource", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it was not able to get the database type from data source.<br>" + "Check the SB JCL definition.")
	JEMS066E(66, "Unable to get data base type from datasource", MessageLevel.ERROR),
	
	/**
	 * "JOB ID: {0}
	 */
	@Description(explanation = "It occurs when before a job starts to show the jobID that you can use to restart it in case of failure.")
	JEMS067I(67, "JOB ID: {0}", MessageLevel.INFO);
	
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
	private SpringBatchMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.SPRING_BATCH.getCode(), messageContent, level);
	}
	
	/**
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}
