/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.jppf;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM JPPF integration. <br>
 * It is a list of <code>JPPFMessage</code>. <br> 
 * Each <code>JPPFMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea Stocchero
 * @version 1.4	
 */
public enum JPPFMessage implements MessageInterface{
		
	/**
	 * "Runnable attribute is null and is not allowed.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when runnable class name attribute is missing.<br>Please check JCL definition.")
	JEMJ001E(1, "Runnable class name attribute is null and is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Runnable class name must be {0} or {1} instance. It is {2}.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when runnable class is neither a Runnable nor a JPPFTask.<br>Please check JCL definition and runnable class implementation.")
	JEMJ002E(2, "Runnable class {0} must be {1} or {2} instance", MessageLevel.ERROR),
	
	/**
	 * "Job {0} is submitted on JPPF grid.", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that the job has been submitted on JPPF grid.")
	JEMJ003I(3, "Job {0} is submitted on JPPF grid", MessageLevel.INFO),
	
	/**
	 * "{0} is executed on node {1} ", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that the task has been excuted on JPPF grid.")
	JEMJ004I(4, "{0} is executed on node {1} ", MessageLevel.INFO),

	/**
	 * "{0} ended with exception: {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when runnable is ended with some errors.<br>Please check output to find the error.")
	JEMJ005E(5, "{0} ended with exception: {1}", MessageLevel.ERROR),
	
	/**
	 * {0} ended providing the following result: {1}", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that task is ended and shows the result provided from task.")
	JEMJ006I(6, "{0} ended providing the following result: {1}", MessageLevel.INFO),
	
	/**
	 * {0} ended without any result", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that task is ended, without any result.")
	JEMJ007I(7, "{0} ended without any result", MessageLevel.INFO),
	
	/**
	 * "Host or port definition is missing: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined port vwithout host or viceversa.<br>Please check JCL and add the missing attribute.")
	JEMJ008E(8, "Host or port definition is missing: {0}", MessageLevel.ERROR),
	
	/**
	 * "String refeernce addr {0} is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM is trying to create a JPPF resource but there is any property.<br>Please check JPPF resource definition.")
	JEMJ009E(9, "String reference addr {0} is missing", MessageLevel.ERROR),
	
	/**
	 * "Resource \"{0}\" is a \"{1}\" instance instead of \"{2}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI factory for JPPF create an object of a wrong type.<br>Please check JPPF resource definition.")
	JEMJ010E(10, "Resource \"{0}\" is a \"{1}\" instance instead of \"{2}\"", MessageLevel.ERROR),
	
	/**
	 * "Bean with JPPF configuration is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when SpringBatch bean to configure JPPF is missing.<br>Please check SpringBtach JCL.")
	JEMJ011E(11, "Bean with JPPF configuration is missing", MessageLevel.ERROR),
	
	/**
	 * {0} ended without any result", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs before merging when result is null.<br> Please check with JEM administrator.")
	JEMJ012E(12, "{0} ended without any result", MessageLevel.ERROR),
	
	/**
	 * "Delimiter is defined but chunkableDataDescription is missing", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the delimiter to read chunks but no input datadescription is passed.<br> Please check your JCL JPPF task.")
	JEMJ013E(13, "Delimiter is defined but chunkableDataDescription is missing", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\" is in disposition SHR and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the mergedDataDescription parameter but is defined in SHR. It couldn't be due to you must write the file.<br> Please check your JCL JPPF task.")
	JEMJ014E(14, "DataDescription \"{0}\" is in disposition SHR and is not allowed", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\"  is not in disposition SHR and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the chunkableDataDescription parameter but is not defined in SHR. It couldn't be due to you must read the file.<br> Please check your JCL JPPF task.")
	JEMJ015E(15, "DataDescription \"{0}\"  is not in disposition SHR and is not allowed", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\"  is multidataset but only singledataset is allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the chunkableDataDescription parameter but datadescritpiton is more than a dataset. It couldn't be due to you must read a single file.<br> Please check your JCL JPPF task.")
	JEMJ016E(16, "DataDescription \"{0}\"  is multidataset but only singledataset is allowed", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\"  is a datasource link and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the chunkableDataDescription parameter but datadescritpiton is linked to a datasource. It couldn't be due to you must read a single file.<br> Please check your JCL JPPF task.")
	JEMJ017E(17, "DataDescription \"{0}\"  is a datasource link and is not allowed", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\"  is inline and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the chunkableDataDescription parameter but datadescritpiton is a inline dataset. It couldn't be due to you must read a single file.<br> Please check your JCL JPPF task.")
	JEMJ018E(18, "DataDescription \"{0}\"  is inline and is not allowed", MessageLevel.ERROR),
	
	/**
	 * "DataDescription \"{0}\"  is inline and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you defined the chunkableDataDescription parameter but datadescritpiton is not defined.<br> Please check your JCL JPPF task.")
	JEMJ019E(19, "DataDescription \"{0}\"  is missing", MessageLevel.ERROR),

	/**
	 * "Delimtier \"{0}\" used for chunking", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about the delimiter used to creates the chunks.")
	JEMJ020I(20, "Delimiter \"{0}\" used for chunking", MessageLevel.INFO),
	
	/**
	 * Chunks \"{0}\" ", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about the list of chunks.")
	JEMJ021I(21, "Chunks {0} ", MessageLevel.INFO),
	
	/**
	 * "Temporary file \"{0}\" used for task {1} for merging", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about the temporary file created and passed to task, necessary for emrging after computing.")
	JEMJ022I(22, "Temporary file \"{0}\" used for task {1} for merging ", MessageLevel.INFO),
	
	/**
	 * "{0} tasks has been created to be executed in grid", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about how many tasks has been created.")
	JEMJ023I(23, "{0} tasks has been created to be executed in grid", MessageLevel.INFO),
	
	/**
	 * "Merge phase is started", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that merging is starting.")
	JEMJ024I(24, "Merge phase is starting", MessageLevel.INFO),
	
	/**
	 * "{0} has been merged, {1} bytes written", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that merging is starting.")
	JEMJ025I(25, "{0} has been merged, {1} bytes written", MessageLevel.INFO),
	
	/**
	 * "Merge phase is ended", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that merging is ended.")
	JEMJ026I(26, "Merge phase is ended", MessageLevel.INFO),
	
	/**
	 *"Both delimiter and delimiterString attribute are defined and is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when both 'delimiter' and 'delimiterString' are defined. Please check JCL.")
	JEMJ027E(27, "Both delimiter and delimiterString attribute are defined and is not allowed", MessageLevel.ERROR),
	
	/**
	 *"Unable to submit JPFF job", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JPPF client is not able to submit the job.<br> Please check generated exception.")
	JEMJ028E(28, "Unable to submit JPFF job", MessageLevel.ERROR),
	
	/**
	 * "Merge phase is ended with exception: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to merge the results of all tasks.<br> Please check generated exception.")
	JEMJ029E(29, "Merge phase is ended with exception: {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to create the JPPF job, MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to create JPPF job, accessing to JNDI or JPPF internal exception.<br> Please check generated exception.")
	JEMJ030E(30, "Unable to create the JPPF job", MessageLevel.ERROR),
	
	/**
	 * "Unable to create the JPPF job, accessing to chunks utility", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to chunk the files.<br> Please check generated exception.")
	JEMJ031E(31, "Unable to create JPPF job, accessing to chunks utility", MessageLevel.ERROR),
	
	/**
	 * "Unable to consume necessary information by JNDI context", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to create JPPF job, accessing to JNDI or JPPF internal exception.<br> Please check generated exception.")
	JEMJ032E(32, "Unable to consume necessary information by JNDI context", MessageLevel.ERROR),
	
	/**
	 * "Unable to get files stream", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it is not able to create input or output stream for files.<br> Please check generated exception.")
	JEMJ033E(33, "Unable to get files stream", MessageLevel.ERROR);

	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>JPPFMessage</code>. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor
	 * and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private JPPFMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.JPPF.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>JPPFMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>JPPFMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}