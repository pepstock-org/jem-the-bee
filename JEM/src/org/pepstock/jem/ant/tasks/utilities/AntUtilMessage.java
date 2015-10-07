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
package org.pepstock.jem.ant.tasks.utilities;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;

/**
 * It is an enumeration containing all the messages related to the ANT utilities. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea 'Stock" Stocchero
 * @version 2.3	
 */
public enum AntUtilMessage implements MessageInterface{

	/**
	 * "{0} on \"{1}\" is ended. Complete file: {2}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the indicated phase on gdg is completed.")
	JEMZ001I(1, "{0} on \"{1}\" is ended. Complete file: {2}", MessageLevel.INFO),
	
	/**
	 * "File \"{0}\" is not deleted", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when during the cleanup phase it is not able to delete the indicated file.")
	JEMZ002W(2, "File \"{0}\" is not deleted", MessageLevel.WARNING),

	/**
	 *"File \"{0}\" is deleted", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when during the cleanup phase the indicated file is deleted.")
	JEMZ003I(3, "File \"{0}\" is deleted", MessageLevel.INFO),

	/**
	 *"\"{0}\" command syntax error in command \"{1}\" ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when syntax of command is not correct.<br>Check command in JCL and correct it.")
	JEMZ004E(4, "\"{0}\" command syntax error in command \"{1}\" ", MessageLevel.ERROR),
	
	/**
	 *"Generation \"{0}\" is created", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when after the GDG defintion, an empty generation is created.")
	JEMZ005I(5, "Generation \"{0}\" is created", MessageLevel.INFO),
	
	/**
	 *"Generation key \"{0}\" is removed", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when during the rebuild phase, the key is not longer valid and it must be removed from root.")
	JEMZ006I(6, "Generation key \"{0}\" is removed", MessageLevel.INFO),
	
	/**
	 *"File \"{0}\" with key \"{1}\" is added", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when during the rebuild phase a new file with a specific key is added to root.")
	JEMZ007I(7, "File \"{0}\" with key \"{1}\" is added", MessageLevel.INFO),
	
	/**
	 *"GDG \"{0}\" has got generation zero to \"{1}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when after rebuild phase a new generation 0 is set and saved to root.")
	JEMZ008I(8, "GDG \"{0}\" has got generation zero to \"{1}\"", MessageLevel.INFO),
	
	
	/**
	 *"Resource \"{0}\" XML definition is created", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a new resource is created and added to JEM.")
	JEMZ009I(9, "Resource \"{0}\" XML definition is created", MessageLevel.INFO),
	
	/**
	 *"Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a output stream, as expect.<br>Check JCL definition.")
	JEMZ010E(10, "Data description name \"{0}\" is not a OutputStream instance but \"{1}\"", MessageLevel.ERROR),

	/**
	 *"Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when data description is not a input stream, as expect.<br>Check JCL definition.")
	JEMZ011E(11, "Data description name \"{0}\" is not a InputStream instance but \"{1}\"", MessageLevel.ERROR),

	/**
	 *"\"{0}\" resources XML definition are created", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about how many resources defintions are created.")
	JEMZ012I(12, "\"{0}\" resources XML definitions are created", MessageLevel.INFO),
	
	
	/**
	 *"Resource \"{0}\" is not removed", MessageLevel.ERROR
	 */
	@Description(explanation = "It informs that resource is not removed.")
	JEMZ013E(13, "Resource \"{0}\" is not removed", MessageLevel.ERROR),
	
	/**
	 *"Resource \"{0}\" is removed", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that resource is removed.")
	JEMZ014I(14, "Resource \"{0}\" is removed", MessageLevel.INFO),
	
	/**
	 *"Resource \"{0}\" is added", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that resource is added.")
	JEMZ015I(15, "Resource \"{0}\" is added", MessageLevel.INFO),
	
	/**
	 *"Resource \"{0}\" is replaced, MessageLevel.INFO
	 */
	@Description(explanation = "It informs that resource is replaced.")
	JEMZ016I(16, "Resource \"{0}\" is replaced", MessageLevel.INFO),
	
	/**
	 *"Source file doesn't contain any resource or resources object but \"{0}\" ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when inside of file with all resources dfinitions doesn't contain all resources defintion.<br>Check the content of file.")
	JEMZ017E(17, "Source file doesn't contain any resource or resources object but \"{0}\" ", MessageLevel.ERROR),
	
	/**
	 *"User \"{0}\" added to role \"{1}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that user is added to a role.")
	JEMZ018I(18, "User \"{0}\" added to role \"{1}\"", MessageLevel.INFO),
	
	/**
	 *"Role \"{0}\" is created", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that new role is created.")
	JEMZ019I(19, "Role \"{0}\" is created", MessageLevel.INFO),
	
	/**
	 *"User \"{0}\" deleted from role \"{1}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that user is deleted from a role.")
	JEMZ020I(20, "User \"{0}\" deleted from role \"{1}\"", MessageLevel.INFO),
	
	/**
	 *"Grant "+Permissions.STAR+" permission is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to add or remove '*' permission to anyone. This is not possible because ONLY administrators can have this permission.")
	JEMZ021E(21, "Manage "+Permissions.STAR+" permission is not allowed", MessageLevel.ERROR),
	
	/**
	 *"Permission \"{0}\" granted to role \"{1}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that a new permission is assinged to a role.")
	JEMZ022I(22, "Permission \"{0}\" granted to role \"{1}\"", MessageLevel.INFO),

	/**
	 *"Role \"{0}\" is removed", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that role is removed.")
	JEMZ023I(23, "Role \"{0}\" is removed", MessageLevel.INFO),

	/**
	 *"Permission \"{0}\" revoked from role \"{1}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that a permission is revoked from a role.")
	JEMZ024I(24, "Permission \"{0}\" revoked from role \"{1}\"", MessageLevel.INFO),
	
	/**
	 *"Parsing command \"{0}\" ", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that is parsing the indicated command line.")
	JEMZ025I(25, "Parsing command \"{0}\" ", MessageLevel.INFO),
	
	/**
	 *"\"{0}\" command is unknown: \"{1}\" ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when syntax of command is unknown.<br>Check command in JCL and correct it.")
	JEMZ026E(26, "\"{0}\" command is unknown: \"{1}\" ", MessageLevel.ERROR),
	
	/**
	 *""DATE and DAYS arguments are missing either", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when both DATE and DAYS arguments are missing in stats collect task.<br>Check arguments in JCL and correct them.")
	JEMZ027E(27, "DATE and DAYS arguments are missing either", MessageLevel.ERROR),
	
	/**
	 *"Class \"{0}\" is not an instance of \"{1}\", but \"{2}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the class of transformer and loader is not of the right class.<br>Check transformer and loader implemented class.")
	JEMZ028E(28, "Class \"{0}\" is not an instance of \"{1}\", but \"{2}\"", MessageLevel.ERROR),
	
	/**
	 *"Date is set to \"{0}\" and TransformAndLoader is set to \"{1}\" ", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about the date what will use to load stats files and the transformer and loader class to use to compute the data.")
	JEMZ029I(29, "Date is set to \"{0}\" and TransformAndLoader is set to \"{1}\" ", MessageLevel.INFO),	
	
	/**
	 *"TransformAndLoader \"{0}\" throws an excpetion parsing record {1} : {2} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the transformer and loader throws a exception during computing the sample at line indicated in MessageLevel.<br>Check if the sample is correct.")
	JEMZ030W(30, "TransformAndLoader \"{0}\" throws an excpetion parsing record {1} : {2} ", MessageLevel.WARNING),	
	
	/**
	 *"The sample at record \"{0}\" is XML not well-formed : {1} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the task is not able to read the sample at indicated record.<br>Check if the sample is XML well-formed.")
	JEMZ031W(31, "The sample at record \"{0}\" is not XML well-formed : {1} ", MessageLevel.WARNING),	
	
	/**
	 *"TransformAndLoader \"{0}\" skips file \"{1}\" ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the transformer and loader skips to compute the file.<br>Check if the transformer and loader has a correct business logic.")
	JEMZ032W(32, "TransformAndLoader \"{0}\" skips file \"{1}\" ", MessageLevel.WARNING),	
	
	/**
	 *"Statistics folder \"{0}\" doesn't exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the folder used by JEM to save stats files doesn't exists.<br>Check in global file system the folder 'stats' or the log of JEM node if any exceptions occurred.")
	JEMZ033E(33, "Statistics folder \"{0}\" doesn't exist", MessageLevel.ERROR),
	
	/**
	 *"Collected and managed \"{0}\" files", MessageLevel.INFO
	 */
	@Description(explanation = "It shows how many files have been managed by task.")
	JEMZ034I(34, "Collected and managed \"{0}\" files", MessageLevel.INFO),
	

	/**
	 * "Property \"{0}\" hasn't got any 'hash' attribute", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a property is set as crypted but the hash, used to check it, is missing.<br>Check the resource property definition.")
	JEMZ035E(35, "Property \"{0}\" hasn't got any 'hash' attribute", MessageLevel.ERROR),

	/**
	 * "Property \"{0}\" hasn't got any value", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a property is set but its value is null.<br>Check the resource property definition.")
	JEMZ036E(36, "Property \"{0}\" hasn't got any value", MessageLevel.ERROR),
	
	/**
	 * "Permission \"{0}\" is not defined", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to add/revoke a permission that doesn't exist.<br>Check command.")
	JEMZ037E(37, "Permission \"{0}\" is not defined", MessageLevel.ERROR),
	
	/**
	 * "Attribute  \"license\" is missing", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when you did not put any license file. Please check the file name of license and put it in persistence path, adding licence attribute.")
	JEMZ038E(38, "Attribute  \"license\" is missing", MessageLevel.ERROR),
	
	/**
	 * "Attribute  \"license\" is missing", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when you did not put a valid license file which does not exist. Please check the file name of license and put it in persistence path.")
	JEMZ039E(39, "License file  \"{0}\" does not exist", MessageLevel.ERROR),
	
	/**
	 * "Unable to create \"{0}\" generation druing rebuild by root phase", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it's unable to create the generation file (empty) during the rebuild of GDG. Please check the GDG path on GFS and its consistency.")
	JEMZ040E(40, "Unable to create \"{0}\" generation druing rebuild by root phase", MessageLevel.ERROR),
	
	/**
	 * "Current GDG root content: {0}", MessageLevel.INFO);
	 */
	@Description(explanation = "It occurs after GDG clean up.")
	JEMZ041I(41, "Current GDG root content: {0}", MessageLevel.INFO),
	
	/**
	 *"Class \"{0}\" is not an instance of \"{1}\", but \"{2}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the class of job output archive is not of the right class.<br>Check job output archive implemented class.")
	JEMZ042E(42, "Class \"{0}\" is not an instance of \"{1}\", but \"{2}\"", MessageLevel.ERROR),
	
	/**
	 *"Job Output Archive is set to \"{0}\" ", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about job output archive class to use to compute the job data.")
	JEMZ043I(43, "Job Output Archive is set to \"{0}\" ", MessageLevel.INFO),
	
	/**
	 *"Unable to remove job \"{0}\" from output queue", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job output archive task wasn't able to remove the job from queue.<br>Check node log and contact JEM administrators.")
	JEMZ044E(44, "Unable to remove job \"{0}\" from output queue", MessageLevel.ERROR),
	
	/**
	 *"Job \"{0}\" is not removed by JobOutputArchive decision", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the job output archive plugin return 'false' because it decided to maintain the job.")
	JEMZ045W(45, "Job \"{0}\" is not removed by JobOutputArchive decision", MessageLevel.WARNING),
	
	/**
	 *""JobOutputArchive invocation error", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job output archive plugin return an exception.<br>Please check the job output archive plugin.")
	JEMZ046E(46, "JobOutputArchive invocation error with job \"{0}\" ", MessageLevel.ERROR),
	
	/**
	 *"Job \"{0}\" has been archived", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about job has been correctly archived.")
	JEMZ047I(47, "Job \"{0}\" has been archived", MessageLevel.INFO),
	
	/**
	 *"{0} jobs has been archived on {1}", MessageLevel.INFO
	 */
	@Description(explanation = "It informs about how many jobs have been correctly archived on all candidated ones.")
	JEMZ048I(48, "{0} jobs has been archived on {1}", MessageLevel.INFO),
	
	/**
	 *"Invalid parameter {0}: is not a valid generation", MessageLevel.ERROR
	 */
	@Description(explanation = "During a delete or rename of GDG command, an invalid generation is past. Please check jcl GDG command.")
	JEMZ049E(49, "Invalid parameter {0}: is not a valid generation", MessageLevel.ERROR),
	
	/**
	 *"Invalid parameter {0}: does not exist", MessageLevel.ERROR
	 */
	@Description(explanation = "During a delete or rename of GDG command, a generation which does not exist is past. Please check jcl GDG command.")
	JEMZ050E(50, "Invalid parameter {0}: does not exist", MessageLevel.ERROR),
	
	/**
	 *"File \"{0}\" is renamed in \"{0}\"", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a rename of GDG generation ends correctly.")
	JEMZ051I(51, "File \"{0}\" is renamed in \"{0}\"", MessageLevel.INFO),
	
	/**
	 * "File \"{0}\" is not renamed", MessageLevel.ERROR
	 */
	@Description(explanation = "GDG command hasn't been able to rename a GDG generation. Please check jcl GDG command and file system.")
	JEMZ052E(52, "File \"{0}\" is not renamed", MessageLevel.ERROR),
	
	/**
	 * "{0} node(s) drained", MessageLevel.INFO
	 */
	@Description(explanation = "It shows a number of nodes which has been drained after a command.")
	JEMZ053I(53, "{0} node(s) drained", MessageLevel.INFO),
	
	/**
	 * "{0} node(s) started", MessageLevel.INFO
	 */
	@Description(explanation = "It shows a number of nodes which has been started after a command.")
	JEMZ054I(54, "{0} node(s) started", MessageLevel.INFO),

	/**
	 *"Certificate with alias \"{0}\" is added", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "It informs that certificate with given alias is added.")
	JEMZ055I(55, "Certificate with alias \"{0}\" is added", MessageLevel.INFO),

	/**
	 *"Certificate with alias \"{0}\" is removed", MessageLevel.INFO
	 */
	@Description(explanation = "It informs that certificate with given alias is removed.")
	JEMZ056I(56, "Certificate with alias \"{0}\" is removed", MessageLevel.INFO),
	
	/**
	 *"Statistics manager is disabled", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when JEM has not activated the statistics storing on file system.<br>Check JEM environment configuration file.")
	JEMZ057W(57, "Statistics manager is disabled", MessageLevel.WARNING);

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
	private AntUtilMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.ANT_UTIL.getCode(), messageContent, level);
	}
	
	/**
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}