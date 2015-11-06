/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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

package org.pepstock.jem.node;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM node. <br>
 * It is a list of <code>NodeMessage</code>. <br>
 * Each <code>NodeMessage</code> in the list corresponds to a
 * <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0
 */
public enum NodeMessage implements MessageInterface {
	
	/**
	 * "Node is starting", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that JEM node is starting")
	JEMC001I("0001", "Node is starting", MessageLevel.INFO),

	/**
	 * "Hazelcast node is starting", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that Hazelcast cluster is starting.")
	JEMC002I("0002", "Hazelcast node is starting", MessageLevel.INFO),

	/**
	 * Hazelcast node [{0}] started", MessageLevel.INFO
	 */
	@Description(explanation = "Display the UUID of Hazelcast member")
	JEMC003I("0003", "Hazelcast node [{0}] started", MessageLevel.INFO),

	/**
	 * "Unable to start HTTPS submitter", MessageLevel.ERROR
	 */
	@Description(explanation = "HTTPS submitter is not able to start. Check the ports allocation.")
	JEMC004E("0004", "Unable to start HTTPS submitter", MessageLevel.ERROR),

	/**
	 * "System property {0} not found", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the system property not set but mandatory. Check command line.")
	JEMC005E("0005", "System property {0} not found", MessageLevel.ERROR),

	/**
	 * "Configuration error", MessageLevel.ERROR
	 */
	@Description(explanation = "Some mistakes are present on configuration files. Check configuration files.")
	JEMC006E("0006", "Configuration error", MessageLevel.ERROR),

	/**
	 * "Unable to start RMI registry", MessageLevel.ERROR
	 */
	@Description(explanation = "RMI registry is not able to start. Check the ports allocation.")
	JEMC007E("0007", "Unable to start RMI registry", MessageLevel.ERROR),

	/**
	 * "Configuration file {0} loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the config file which has been loaded")
	JEMC008I("0008", "Configuration file {0} loaded", MessageLevel.INFO),

	/**
	 * "Configuration xml node {0} not found", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the element of xml that is missing nonetheless is mandatory. Check configuration file.")
	JEMC009E("0009", "Configuration xml node {0} not found", MessageLevel.ERROR),

	/**
	 * "Environment mismatch configuration: \"{0}\" against in Hazelcast \"{1}\""
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "Display the 2 different values found inside of JEM configuration node and Hazelcast configuration file. " + "The environment value and group one must be the same. "
			+ "Check the configuration files changing the group name or environment value.")
	JEMC010E("0010", "Environment mismatch configuration: \"{0}\" against in Hazelcast \"{1}\"", MessageLevel.ERROR),

	/**
	 * "{0}", MessageLevel.INFO - used for Hazelcast log records
	 */
	@Deprecated
	@Description(explanation = "Display all messages produced by Hazelcast, errors as well.")
	JEMC011I("0011", "{0}", MessageLevel.INFO),

	/**
	 * "Initiator [{0}] started", MessageLevel.INFO
	 */
	@Description(explanation = "Display the process ID and hostname of JEM node started.")
	JEMC012I("0012", "Initiator [{0}] started", MessageLevel.INFO),

	/**
	 * "RMI registry is starting on port {0}", MessageLevel.INFO
	 */
	@Description(explanation = "Display the listening RMI port.")
	JEMC013I("0013", "RMI registry is starting on port {0}", MessageLevel.INFO),

	/**
	 * "RMI registry started", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that RMI registry listener is started.")
	JEMC014I("0014", "RMI registry started", MessageLevel.INFO),

	/**
	 * "Remote-object \"{0}\" is added", MessageLevel.INFO
	 */
	@Description(explanation = "Display the name of RMI object, binded on registry")
	JEMC015I("0015", "Remote-object \"{0}\" is added", MessageLevel.INFO),

	/**
	 * "Unable to add remote-object \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the name of RMI object which wasn't able to be binded on registry. See the exception")
	JEMC016E("0016", "Unable to add remote-object \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Interrupt waiting", MessageLevel.ERROR
	 */
	@Description(explanation = "Some interrupt unblock the wait state. Internal error.")
	JEMC017E("0017", "Interrupt waiting", MessageLevel.ERROR),

	/**
	 * "Job {0} is taken for processing", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name and id of job taken from input queue to be executed.")
	JEMC018I("0018", "Job {0} is taken for processing", MessageLevel.INFO),

	/**
	 * "Job {0} is routed to \"{1}\" for processing", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name and id of job which was routed to another cluster.")
	JEMC019I("0019", "Job {0} is routed to \"{1}\" for processing", MessageLevel.INFO),

	/**
	 * "Job {0} is submitted for processing", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name and id of job which is now in submission phase, not executed yet.")
	JEMC020I("0020", "Job {0} is submitted for processing", MessageLevel.INFO),

	/**
	 * "Job {0} is ended in return-code {1}", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name and id of job which is ended in the displayed return code.")
	JEMC021I("0021", "Job {0} is ended in return-code {1}", MessageLevel.INFO),

	/**
	 * "Job {0} is running on process {1}", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name and id of job which is now running in new process with displayed process id.")
	JEMC022I("0022", "Job {0} is running on process {1}", MessageLevel.INFO),

	/**
	 * "HTTPS connection error: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "HTTPS worker has got a connection error. <br>Please che the root cause exception.")
	JEMC023E("0023", "HTTPS connection error: {0}", MessageLevel.ERROR),

	/**
	 * "Member removed \"{0}\" is not in {1} queue", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the member which is not present on queue as expected. Internal error.")
	JEMC024E("0024", "Member removed \"{0}\" is not in {1} queue", MessageLevel.ERROR),

	/**
	 * "Process ID is null and \"cancel\" command is ignored", MessageLevel.WARNING
	 */
	@Description(explanation = "Cancel command aborted because the job is ended.")
	JEMC025W("0025", "Process ID is null and \"cancel\" command is ignored", MessageLevel.WARNING),

	/**
	 * "Path \"{0}\" has been created", MessageLevel.INFO
	 */
	@Description(explanation = "Display the path that is necessary but it doesn't exist so JEM created.")
	JEMC026I("0026", "Path \"{0}\" has been created", MessageLevel.INFO),

	/**
	 * "Unable to write job log record", MessageLevel.ERROR
	 */
	@Description(explanation = "Internal error.")
	JEMC027E("0027", "Unable to write job log record", MessageLevel.ERROR),

	/**
	 * "Ask canceling job {0} which is not equal to job {1}, currently running",
	 * MessageLevel.WARNING
	 */
	@Description(explanation = "Cancel command aborted because the job is ended.")
	JEMC028W("0028", "Ask canceling job {0} which is not equal to job {1}, currently running", MessageLevel.WARNING),

	/**
	 * "Cancel command for job {0} by {1} (force mode:{2})", MessageLevel.INFO
	 */
	@Description(explanation = "Display the job name, id of job and user who canceled it.")
	JEMC029I("0029", "Cancel command for job {0} by {1} (force mode:{2})", MessageLevel.INFO),

	/**
	 * "Initiator is {0}", MessageLevel.INFO
	 */
	@Description(explanation = "Display the status of JEM node.")
	JEMC030I("0030", "Initiator is {0}", MessageLevel.INFO),

	/**
	 * "Unable to load factory \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>Check the class name and classpath of JEM node")
	JEMC031E("0031", "Unable to load factory \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Factory \"{0}\" [type=\"{1}\"] loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the JEM factory loaded and its type.")
	JEMC032I("0032", "Factory \"{0}\" [type=\"{1}\"] loaded", MessageLevel.INFO),

	/**
	 * "Unable to submit job", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the submitter is not able to submit a job.<br>Please have a look to the root cause exception")
	JEMC033E("0033", "Unable to submit job", MessageLevel.ERROR),

	/**
	 * "Job lifecycle listeners are ready", MessageLevel.INFO
	 */
	@Description(explanation = "Informs that the job lifecycle listeners are loaded and ready to work.")
	JEMC034I("0034", "Job lifecycle listeners are ready", MessageLevel.INFO),

	/**
	 * "Unable to load job listener \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>Check the class name and classpath of JEM node.")
	JEMC035E("0035", "Unable to load job listener \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Configured job listener \"{0}\" is not an instance of JobLifecycleListener"
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>" + "Check the class name and classpath of JEM node because is not a JobLifecycleListener.")
	JEMC036E("0036", "Configured job listener \"{0}\" is not an instance of JobLifecycleListener", MessageLevel.ERROR),

	/**
	 * "JobLifecycle listener \"{0}\" loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the JobLifecycle listener loaded.")
	JEMC037I("0037", "JobLifecycle listener \"{0}\" loaded", MessageLevel.INFO),

	/**
	 * "{0} \"{1}\" is not well-defined", MessageLevel.WARNING
	 */
	@Description(explanation = "Display the element of xml that is not well defined.<br>Check configuration file.")
	JEMC038W("0038", "{0} \"{1}\" is not well-defined", MessageLevel.WARNING),

	/**
	 * "Value of \"{0}\" is not well-defined", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the path name not defined even if is mandatory.<br>Check configuration file.")
	JEMC039E("0039", "Value of path \"{0}\" is not defined", MessageLevel.ERROR),

	/**
	 * "Configured factory \"{0}\" is not an instance of JemFactory",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Display the JEM factory that is not a JEM Factory.<br>Check configuration file and source code of factory.")
	JEMC040E("0040", "Configured factory \"{0}\" is not an instance of JemFactory", MessageLevel.ERROR),

	/**
	 * "Job lifecycle listener system is not able to process the queue
	 */
	@Description(explanation = "Internal error of event manager.")
	JEMC041E("0041", "Job lifecycle listener system is not able to process the queue", MessageLevel.ERROR),

	/**
	 * "Job lifecycle listener system is not able to add a event in queue,
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Internal error of event manager.")
	JEMC042E("0042", "Job lifecycle listener system is not able to add a event in queue", MessageLevel.ERROR),

	/**
	 * "Unable to persist objects due to an exception on database",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Occurs when the persistent MapManager is not able to store data on database.")
	JEMC043E("0043", "Unable to persist objects due to an exception on database", MessageLevel.ERROR),

	/**
	 * "Database manager instance is null.", MessageLevel.ERROR
	 */
	@Description(explanation = "Occurs when the persistent MapManager is not able to store data on database.")
	JEMC044E("0044", "Database manager instance is null.", MessageLevel.ERROR),

	/**
	 * "{0} key loaded for \"{1}\" queue", MessageLevel.INFO
	 */
	@Description(explanation = "Display the map name and keys loaded (and then the objects) for Hazelcast.")
	JEMC045I("0045", "{0} keys loaded for \"{1}\" queue", MessageLevel.INFO),

	/**
	 * "HTTPS submitter is starting on port {0}", MessageLevel.INFO
	 */
	@Description(explanation = "Display the listening HTTPS submitter port.")
	JEMC046I("0046", "HTTPS submitter is starting on port {0}", MessageLevel.INFO),

	/**
	 * "Job {0} is ended in exception:\n {1}", MessageLevel.ERROR
	 */
	@Deprecated
	JEMC047E("0047", "Job {0} is ended in exception:\n {1}", MessageLevel.ERROR),

	/**
	 * "{0} jobs loaded for \"{1}\" queue", MessageLevel.INFO
	 */
	@Description(explanation = "Display how many jobs are loaded from database.")
	JEMC048I("0048", "{0} jobs loaded for \"{1}\" queue", MessageLevel.INFO),

	/**
	 * "Affinity \"{0}\" loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the affinity factory loaded.")
	JEMC049I("0049", "AffinityFactory \"{0}\" loaded", MessageLevel.INFO),

	/**
	 * "{0}" ", MessageLevel.INFO
	 */
	@Description(explanation = "Display all information of configured and calculated execution environment.")
	JEMC050I("0050", "{0}", MessageLevel.INFO),

	/**
	 * "JS policy file \"{0}\" executed", MessageLevel.INFO
	 */
	@Description(explanation = "Display script file used for policy by affinity factory.")
	JEMC051I("0051", "Policy file \"{0}\" executed", MessageLevel.INFO),

	/**
	 * "{0} is added", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "Display the common resource added.")
	JEMC052I("0052", "Resource {0} is added", MessageLevel.INFO),

	/**
	 * "{0} is replaced", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "Display the common resource removed.")
	JEMC053I("0053", "Resource {0} is replaced", MessageLevel.INFO),

	/**
	 * "Unable to add or replace a resource", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "Internal error.")
	JEMC054E("0054", "Unable to add or replace a resource", MessageLevel.ERROR),

	/**
	 * "{0} resources loaded for \"{1}\" queue", MessageLevel.INFO
	 */
	@Description(explanation = "Display how many resources are loaded from database.")
	JEMC055I("0055", "{0} resources loaded for \"{1}\" queue", MessageLevel.INFO),

	/**
	 * "Missing required options:\n{0}, MessageLevel.INFO
	 */
	@Description(explanation = "Display all necessary options and how command usage, because the command has been launched without all mandatory options.<br>" + "Check command line")
	JEMC056I("0056", "Missing required options:\n{0}", MessageLevel.INFO),

	/**
	 * ""Environment variable \"{0}\" is loaded. Value: \"{1}\"", MessageLevel.INFO
	 */
	JEMC057I("0057", "Environment variable \"{0}\" is loaded. Value: \"{1}\"", MessageLevel.INFO),

	/**
	 * ""Environment variable \"{0}\" is missing but mandatory", MessageLevel.ERROR
	 */
	JEMC058E("0058", "Environment variable \"{0}\" is missing but mandatory", MessageLevel.ERROR),

	/**
	 * "\"{0}\" successfully created see path: \"{1}\", MessageLevel.INFO
	 */
	JEMC059I("0059", "\"{0}\" successfully created see path: \"{1}\"", MessageLevel.INFO),

	/**
	 * "The path \"{0}\" already exist cannot create node, MessageLevel.INFO
	 */
	JEMC060E("0060", "The path \"{0}\" already exist cannot create node", MessageLevel.ERROR),

	/**
	 * "{0} is removed", MessageLevel.INFO
	 */
	@Deprecated
	JEMC061I("0061", "Resource {0} is removed", MessageLevel.INFO),

	/**
	 * "{0} doesn't exist", MessageLevel.WARNING
	 */
	@Deprecated
	JEMC062W("0062", "Resource {0} does not exist", MessageLevel.WARNING),

	/**
	 * Resource {0} properties:\n{1}, MessageLevel.INFO
	 */
	@Deprecated
	JEMC063I("0063", "Resource {0} properties:\n{1}", MessageLevel.INFO),

	/**
	 * Resources list:\n{0}, MessageLevel.INFO
	 */
	@Deprecated
	JEMC064I("0064", "Resources list:\n{0}", MessageLevel.INFO),

	/**
	 * "Error while substituting variable on string {0}", MessageLevel.ERROR
	 */
	@Deprecated
	JEMC065E("0065", "Error while substituting variable on string {0}", MessageLevel.ERROR),

	/**
	 * "Missing required property {0}", MessageLevel.ERROR
	 */
	JEMC066E("0066", "Missing required property {0}", MessageLevel.ERROR),

	/**
	 * "Folder {0} does not exist, you need to create it", MessageLevel.ERROR
	 */
	JEMC067E("0067", "Folder {0} does not exist, you need to create it", MessageLevel.ERROR),

	/**
	 * "Component {0} is shutting down", MessageLevel.WARNING
	 */
	JEMC068W("0068", "Component {0} is shutting down", MessageLevel.WARNING),

	/**
	 * "{0} shutdown is completed", MessageLevel.INFO
	 */
	JEMC069I("0069", "{0} shutdown is completed", MessageLevel.INFO),

	/**
	 * "{0} shutdown is completed with exception", MessageLevel.ERROR
	 */
	JEMC070E("0070", "{0} shutdown is completed with exception", MessageLevel.ERROR),

	/**
	 * "{0} starts shutdown procedure", MessageLevel.INFO
	 */
	JEMC071I("0071", "{0} starts shutdown procedure", MessageLevel.INFO),

	/**
	 * "Job {0} is still running. Wait for 20 seconds to check again",
	 * MessageLevel.INFO
	 */
	JEMC072I("0072", "Job {0} is still running. Wait for 20 seconds to check again", MessageLevel.INFO),

	/**
	 * "Error during creation of database for environment {0}." , MessageLevel.ERROR
	 */
	@Deprecated
	JEMC073E("0073", "Error during creation of database for environment {0}.", MessageLevel.ERROR),

	/**
	 * "Job lifecycle listener error.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is a general problem inside the Job lifecycle listener execution.<br>Main Job lifecycle listener methods cannot throw exceptions, so errors are cached and this log is written.<br>No exception is thrown, but this error is logged.<br>Check the log because the real problem cause is surely written before this log, so it is possible to correct the error.")
	JEMC074E("0074", "Job lifecycle listener error.", MessageLevel.ERROR),

	/**
	 * "Created the folder \"{0}\" to store statistics logs files", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the statistics folder is missing but necessary to store the statistics log record so it creates it.")
	JEMC075I("0075", "Created the folder \"{0}\" to store statistics logs files", MessageLevel.INFO),

	/**
	 * "The folder \"{0}\" is used to store statistics logs files", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the statistics manager is starting and informs the folder used to store the statistics log record so it creates it.")
	JEMC076I("0076", "The folder \"{0}\" is used to store statistics logs files", MessageLevel.INFO),

	/**
	 * "The statistics manager is stopped", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when shutdown hook is started to close the JEM node.")
	JEMC077I("0077", "The statistics manager is stopped", MessageLevel.INFO),

	/**
	 * "The current statistics manager is managing statistics for whole JEM environment"
	 * , MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node is the coordinator and works to extract from all nodes and store all statistics.")
	JEMC078I("0078", "The current statistics manager is managing statistics for whole JEM environment", MessageLevel.INFO),

	/**
	 * "Unable to execute statistics remote command on all nodes", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when receives an exception during the execution of future task on all node to extract statistics.<br>Check the showed exception.")
	JEMC079E("0079", "Unable to execute statistics remote command on all nodes", MessageLevel.ERROR),

	/**
	 * "Unable to write statistics log record in \"{0}\" file", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when receives an exception during the writing of statistics log records in the file.<br>Check the showed exception and if there's any file system error.")
	JEMC080E("0080", "Unable to write statistics log record in \"{0}\" file", MessageLevel.ERROR),

	/**
	 * "Statistics management lasted \"{0}\" ms", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the statistics manager ends its activities to extract and store the stat log records and it lasted more than 2 seconds.<br>It shows the elapsed time in milliseconds spent to complete the activities.")
	JEMC081W("0081", "Statistics management lasted {0} ms", MessageLevel.WARNING),

	/**
	 * "Unable to write statistics log record in \"{0}\" file", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs to inform what file is used to store statistics log records.")
	JEMC082I("0082", "The statistics log file used to store records is \"{0}\" file", MessageLevel.INFO),

	/**
	 * "Database for environment {0} already present.", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "It occurs during the creation of a new environment in case the database is alreadty present, for example because you have installed the same environment in different machines.")
	JEMC083I("0083", "Database for environment {0} already present.", MessageLevel.INFO),

	/**
	 * "File  {0} is been modified. {1}", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "It occurs when a configuration file is been modified.")
	JEMC084I("0084", "File  {0} is been modified. {1} ", MessageLevel.INFO),

	/**
	 * "Map {0} has size {1} KB", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the cluster check if there are enough member to start up")
	JEMC085I("0085", "Map {0} has size {1} KB", MessageLevel.INFO),

	/**
	 * "Cluster free memory= {0} KB is less than the needed memory= {1} KB. Wait for other nodes to join the claster."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs at cluster start up when the are not enough nodes to bring the persisted queue in meory")
	JEMC086W("0086", "Cluster free memory= {0} KB is less than the needed memory= {1} KB. Wait for other nodes to join the claster.", MessageLevel.WARNING),

	/**
	 * "The path {0} set for property {1} must not contain spaces.",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when configuring a node if the path for the some folders of the gfs contains white spaces.")
	JEMC087E("0087", "The path {0} set for property {1} must not contain spaces.", MessageLevel.ERROR),

	/**
	 * "The path {0} set for property {1} does not exist.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when configuring a node if the path for the some folders of the gfs does not exist.")
	JEMC088E("0088", "The path {0} set for property {1} does not exist.", MessageLevel.ERROR),

	/**
	 * "Configured factory \"{0}\" is not an instance of AffinityLoader",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Display the affintity loader that is not a loader.<br>Check configuration file and source code of affinity loader.")
	JEMC089E("0089", "Configured factory \"{0}\" is not an instance of AffinityLoader", MessageLevel.ERROR),

	/**
	 * "Affinity \"{0}\" loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the node implementation which is loaded.")
	JEMC090I("0090", "Node \"{0}\" loaded", MessageLevel.INFO),

	/**
	 * "Configured node \"{0}\" is not an instance of NodeInfo", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the node class that is not a NodeInfo.<br>Check configuration file and source code of Node.")
	JEMC091E("0091", "Configured node \"{0}\" is not an instance of NodeInfo", MessageLevel.ERROR),

	/**
	 * Unable to read {0} object after javascript execution", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "Errors during retreving the object result after javascript execution.<br>Check the javascript policy file.")
	JEMC092E("0092", "Unable to read {0} object after javascript execution", MessageLevel.ERROR),

	/**
	 * "Job \"{0}\" is in input queue", MessageLevel.INFO
	 */
	@Description(explanation = "Simple information from default job listener that job is inut queue.")
	JEMC093I("0093", "Job \"{0}\" is in input queue", MessageLevel.INFO),

	/**
	 * "Job \"{0}\" is running", MessageLevel.INFO
	 */
	@Description(explanation = "Simple information from default job listener that job is running.")
	JEMC094I("0094", "Job \"{0}\" is running", MessageLevel.INFO),

	/**
	 * "Job \"{0}\" is ended. Return-code={1}", MessageLevel.INFO
	 */
	@Description(explanation = "Simple information from default job listener that job is ended.")
	JEMC095I("0095", "Job \"{0}\" is ended. Return-code={1}", MessageLevel.INFO),

	/**
	 * Hazelcast configuration file for the environment is null., MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when it's not possible to locate Hazelcast configuration file.<br>Check the location and path for JEM node installation.")
	JEMC096E("0096", "Hazelcast configuration file \"{0}\" for the environment is null.", MessageLevel.ERROR),

	/**
	 * "JCL url is null.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when url, which represents JCL, is null.<br> Check where JCL is and its URL.")
	JEMC97E("0097", "JCL url is null.", MessageLevel.ERROR),

	/**
	 * "{0} isn't a directory.", MessageLevel.ERROR
	 */
	JEMC098E("0098", "{0} is not a directory.", MessageLevel.ERROR),

	/**
	 * "{0} doesn't exist.", MessageLevel.ERROR
	 */
	JEMC099E("0099", "{0} does not exist.", MessageLevel.ERROR),

	/**
	 * "{0} not found.", MessageLevel.ERROR
	 */
	JEMC100E("0100", "{0} not found.", MessageLevel.ERROR),

	/**
	 * "{0} does not name a context.", MessageLevel.ERROR
	 */
	JEMC101E("0101", "{0} does not name a context.", MessageLevel.ERROR),

	/**
	 * "Use rebind to override.", MessageLevel.ERROR
	 */
	JEMC102E("0102", "Use rebind to override.", MessageLevel.ERROR),

	/**
	 * "Error while retrieving roles for user {0}", MessageLevel.ERROR
	 */
	JEMC103E("0103", "Error while retrieving roles for user {0}", MessageLevel.ERROR),

	/**
	 * "{0} not allowed!", MessageLevel.ERROR
	 */
	JEMC104E("0104", "{0} not allowed!", MessageLevel.ERROR),

	/**
	 * "User not allowed for permission {0}", MessageLevel.ERROR
	 */
	@Deprecated
	JEMC105E("0105", "User not allowed for permission {0}", MessageLevel.ERROR),

	/**
	 * "Login denied.", MessageLevel.WARNING
	 */
	JEMC106W("0106", "Login denied.", MessageLevel.WARNING),

	/**
	 * "Login denied for {0}", MessageLevel.WARNING
	 */
	JEMC107W("0107", "Login denied for {0}", MessageLevel.WARNING),

	/**
	 * "Password in Hazelcast configuration must not be null or empty"" ,
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Password in Hazelcast configuration must not be null or empty")
	JEMC108E("0108", "Password in Hazelcast configuration must not be null or empty", MessageLevel.ERROR),

	/**
	 * "Invalid request/response {0}", MessageLevel.WARNING
	 */
	JEMC109W("0109", "Invalid request/response {0}", MessageLevel.WARNING),

	/**
	 * "Changed database encryption for environment {0}.", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "It occurs during during the execution of the command ChangeDbEncryption if the database is already encrypted.")
	JEMC110I("0110", "Changed database encryption for environment {0}.", MessageLevel.INFO),

	/**
	 * "Encrypted database for environment {0}.", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "It occurs during during the execution of the command ChangeDbEncryption if the database is not encrypted.")
	JEMC111I("0111", "Encrypted database for environment {0}.", MessageLevel.INFO),

	/**
	 * "Executor call back exception.", MessageLevel.ERROR
	 */
	@Description(explanation = "Internal error during a command. It may be called from user interface.")
	JEMC112E("0112", "Executor call back exception.", MessageLevel.ERROR),

	/**
	 * "Executor call back result {0}.", MessageLevel.INFO
	 */
	@Description(explanation = "Internal error during a command. It may be called from user interface.")
	JEMC113I("0113", "Executor call back result {0}.", MessageLevel.INFO),

	/**
	 * "Property \"{0}\" is not defined.", MessageLevel.ERROR
	 */
	@Description(explanation = "The JS affinity file is null.")
	JEMC114E("0114", "Property \"{0}\" is not defined.", MessageLevel.ERROR),

	/**
	 * "{0} is shutting down.", MessageLevel.ERROR
	 */
	JEMC115E("0115", "{0} is shutting down.", MessageLevel.ERROR),

	/**
	 * "Key is null.", MessageLevel.ERROR
	 */
	JEMC116E("0116", "Key is null.", MessageLevel.ERROR),

	/**
	 * "Connection object instance is null.", MessageLevel.ERROR
	 */
	@Deprecated
	JEMC117E("0117", "Connection object instance is null.", MessageLevel.ERROR),

	/**
	 * "Hashed valued is not equals to orginal cryted value.", MessageLevel.ERROR
	 */
	JEMC118E("0118", "Hashed valued is not equals to orginal cryted value.", MessageLevel.ERROR),

	/**
	 * "Unable to lock \"{0}\" map in 10 seconds.", MessageLevel.ERROR
	 */
	JEMC119E("0119", "Unable to lock \"{0}\" map in 10 seconds.", MessageLevel.ERROR),

	/**
	 * "User \"{0}\" have not got the {1}.", MessageLevel.ERROR
	 */
	@Deprecated
	JEMC120E("0120", "User \"{0}\" have not got the {1}.", MessageLevel.ERROR),

	/**
	 * "User \"{0}\" is not authorized for \"{1}\".", MessageLevel.ERROR
	 */
	JEMC121E("0121", "User \"{0}\" is not authorized for \"{1}\".", MessageLevel.ERROR),

	/**
	 * "\"{0}\" is not defined!", MessageLevel.ERROR
	 */
	JEMC122E("0122", "{0} is not defined!", MessageLevel.ERROR),

	/**
	 * "Role {0} couldn't be changed.", MessageLevel.ERROR
	 */
	JEMC123E("0123", "Role \"{0}\" could not be changed.", MessageLevel.ERROR),

	/**
	 * "Role \"{0}\" doesn't exist.", MessageLevel.ERROR
	 */
	JEMC124E("0124", "Role \"{0}\" does not exist.", MessageLevel.ERROR),

	/**
	 * "Role \"{0}\" is not removable.", MessageLevel.ERROR
	 */
	JEMC125E("0125", "Role \"{0}\" is not removable.", MessageLevel.ERROR),

	/**
	 * "Role \"{0}\" already exists.", MessageLevel.ERROR
	 */
	JEMC126E("0126", "Role \"{0}\" already exists.", MessageLevel.ERROR),

	/**
	 * "Any override to a 'jem' property is not allowed!", MessageLevel.ERROR
	 */
	JEMC127E("0127", "Any override to a \"jem\" property is not allowed!", MessageLevel.ERROR),

	/**
	 * "Access to RMI listener is not allowed.", MessageLevel.ERROR
	 */
	JEMC128E("0128", "Access to RMI listener is not allowed.", MessageLevel.ERROR),

	/**
	 * "Wildcard string cannot be null or empty. Make sure permission strings are properly formatted."
	 * , MessageLevel.ERROR
	 */
	JEMC129E("0129", "Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.", MessageLevel.ERROR),

	/**
	 * "Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted."
	 * , MessageLevel.ERROR
	 */
	JEMC130E("0130", "Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.", MessageLevel.ERROR),

	/**
	 * "Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted."
	 * , MessageLevel.ERROR
	 */
	JEMC131E("0131", "Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.", MessageLevel.ERROR),

	/**
	 * "Resource \"{0}\" is not a InputStream.", MessageLevel.ERROR
	 */
	JEMC132E("0132", "Resource \"{0}\" is not a InputStream.", MessageLevel.ERROR),

	/**
	 * "Object is not a reference {0}.", MessageLevel.ERROR
	 */
	JEMC133E("0133", "Object is not a reference {0}.", MessageLevel.ERROR),

	/**
	 * "Resource \"{0}\" is not a OutputStream.", MessageLevel.ERROR
	 */
	JEMC134E("0134", "Resource \"{0}\" is not a OutputStream.", MessageLevel.ERROR),

	/**
	 * "DataSet INLINE is not allowed to open in output mode.", MessageLevel.ERROR
	 */
	JEMC135E("0135", "DataSet INLINE is not allowed to open in output mode.", MessageLevel.ERROR),

	/**
	 * "Mandatory property \"{0}\" is missing.", MessageLevel.ERROR
	 */
	JEMC136E("0136", "Mandatory property \"{0}\" is missing.", MessageLevel.ERROR),

	/**
	 * "The protocol must be 'ftp' or 'ftps' instead of \"{0}\".", MessageLevel.ERROR
	 */
	JEMC137E("0137", "The protocol must be \"ftp\" or \"ftps\" instead of \"{0}\".", MessageLevel.ERROR),

	/**
	 * "Reply code \"{0}\", during connection, is not positive.", MessageLevel.ERROR
	 */
	JEMC138E("0138", "Reply code \"{0}\", during connection, is not positive.", MessageLevel.ERROR),

	/**
	 * "Cannot bind empty name.", MessageLevel.ERROR
	 */
	JEMC139E("0139", "Cannot bind empty name.", MessageLevel.ERROR),

	/**
	 * "getObjectInstance failed.", MessageLevel.ERROR
	 */
	JEMC140E("0140", "getObjectInstance failed.", MessageLevel.ERROR),

	/**
	 * "System property \"{0}\" is not set.", MessageLevel.ERROR
	 */
	JEMC141E("0141", "System property \"{0}\" is not set.", MessageLevel.ERROR),

	/**
	 * "Object \"{0}\" is missing.", MessageLevel.ERROR
	 */
	JEMC142E("0142", "Object \"{0}\" is missing.", MessageLevel.ERROR),

	/**
	 * "Factory for {0} is missing.", MessageLevel.ERROR
	 */
	JEMC143E("0143", "Factory for {0} is missing.", MessageLevel.ERROR),

	/**
	 * "User \"{0}\" does not have the \"{1}\" permission.", MessageLevel.ERROR
	 */
	JEMC144E("0144", "User \"{0}\" does not have the \"{1}\" permission.", MessageLevel.ERROR),

	/**
	 * "Resource name is null.", MessageLevel.ERROR
	 */
	JEMC145E("0145", "Resource name is null.", MessageLevel.ERROR),

	/**
	 * "Mode must be READ_MODE or WRITE_MODE value.", MessageLevel.ERROR
	 */
	JEMC146E("0146", "Mode must be READ_MODE or WRITE_MODE value.", MessageLevel.ERROR),

	/**
	 * "Error while loading node.", MessageLevel.ERROR
	 */
	JEMC147E("0147", "Error while loading node.", MessageLevel.ERROR),

	/**
	 * "File \"{0}\" is opened for reading", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a statistic file is opened for processing.")
	JEMC148I("0148", "File \"{0}\" is opened for reading", MessageLevel.INFO),

	/**
	 * "File \"{0}\" is removed", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a statistic file is removed after processing.")
	JEMC149I("0149", "File \"{0}\" is removed", MessageLevel.INFO),

	/**
	 * "File \"{0}\" is not removed", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a statistic file is not removed after processing because it is not able to delete the file from file system.")
	JEMC150W("0150", "File \"{0}\" is not removed", MessageLevel.WARNING),

	/**
	 * "File \"{0}\" is not removed", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a statistic file is not removed after processing because it is not able to read all samples.")
	JEMC151W("0151", "File \"{0}\" is not removed due to previuos error on samples", MessageLevel.WARNING),

	/**
	 * "The sample at line \"{0}\" is not correct. Exception: \"{1}\"",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a sample in the statistic file is not correct, usually not XML well-formed<br> Please check content of file and correct it.")
	JEMC152E("0152", "The sample at line \"{0}\" is not correct. Exception: \"{1}\"", MessageLevel.ERROR),

	/**
	 * "Unable to create {0} directory.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the path (indicated in JEM node configuration) doesn't exists.<br> Please check 'statistics-manager' element in JEM node configuration file.")
	JEMC153E("0153", "Unable to create {0} directory. Disable statistics persistence", MessageLevel.ERROR),

	/**
	 * "Unable to rename the node folder", MessageLevel.ERROR);
	 */
	JEMC154E("0154", "Unable to rename the node folder", MessageLevel.ERROR),

	/**
	 * "Unable to write JCL and message log for {0} job", MessageLevel.WARNING);
	 */
	JEMC155W("0155", "Unable to write JCL and message log for {0} job, after job validation", MessageLevel.WARNING),

	/**
	 * "Error creating the HttpClient for HttpResource. Exception: \"{0}\"",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an error occurs creating the HttpClient for HttpResource.<br> Please check the configuration file of the HttpResource.")
	JEMC156E("0156", "Error creating the HttpClient for HttpResource. Exception: \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Error executing Http Request Method. Exception: \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an error occurs executing Http Request Method.<br> Please check the log file for details.")
	JEMC157E("0157", "Error executing Http Request Method. Exception: \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Property {0} not found in the Context Environment executing Http Request Method."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs executing Http Request Method when a property for is not found in the context Environment.")
	JEMC158W("0158", "Property \"{0}\" not found in the Context Environment executing Http Request Method.", MessageLevel.WARNING),

	/**
	 * "Error executing login for HttpResource: {0} or {1} is mandatory.",
	 * MessageLevel.ERROR
	 */
	JEMC159E("0159", "Error executing login for HttpResource: {0} or {1} is mandatory", MessageLevel.ERROR),

	/**
	 * "Error executing login for HttpResource: {0} is mandatory.",
	 * MessageLevel.ERROR
	 */
	JEMC160E("0160", "Error executing login for HttpResource: {0} or is mandatory", MessageLevel.ERROR),

	/**
	 * "Statistic executor is still running from previous scheduling",
	 * MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs executing statistic manager try to execute the collector task but it's still running.<br> Please check the log file for details and contact your JEM administrators.")
	JEMC161W("0161", "Statistic executor is still running from previous scheduling", MessageLevel.WARNING),

	/**
	 * "Error during shutdown of {0} component", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs during shutdown phase and it wasn't able to shutdown a component.<br> Please check the log file for details and contact your JEM administrators.")
	JEMC162E("0162", "Error during shutdown of {0} component", MessageLevel.ERROR),

	/**
	 * "Time occurs during polling on JCL queue", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs during JCL check. It means that node wasn't able to get the object from queue due to a lock on queue itself.<br> Please check the log file for details and contact your JEM administrators.")
	JEMC163E("0163", "Time occurs during polling on JCL queue", MessageLevel.ERROR),

	/**
	 * "Unable to remove output job {0} on directory {1}", MessageLevel.WARNING
	 * MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when after purge command, output folder for job is not deleted.<br>Please check file system where otuput folder is stored.")
	JEMC164W("0164", "Unable to remove output job {0} on directory {1}", MessageLevel.WARNING),

	/**
	 * "Unable to create the database connection for {0}", MessageLevel.ERROR);
	 */
	@Description(explanation = "It occurs during start up of node when opens the connection with database to persist Hazelcast structures.<br>Please check configuration file on database element and contact your JEM administrators.")
	JEMC165E("0165", "Unable to create the database connection to {0}", MessageLevel.ERROR),

	/**
	 * "Unable to parse URL string parameter for the database connection: {0}",
	 * MessageLevel.ERROR);
	 */
	@Description(explanation = "It occurs during start up of node when it try to understand what is type of database.<br>Please check configuration file on database element and contact your JEM administrators.")
	JEMC166E("0166", "Unable to parse URL string parameter for the database connection: {0}", MessageLevel.ERROR),

	/**
	 * "Unable to create the tables for the persistency", MessageLevel.ERROR);
	 */
	@Description(explanation = "It occurs during start up of node when tries to create the necessary tables to persist jobs.<br>Please check configuration file on database element and contact your JEM administrators.")
	JEMC167E("0167", "Unable to create the tables for the persistency", MessageLevel.ERROR),

	/**
	 * "Unable to write job log record", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node tries to write the job in XML on output path.<br>Please check global file system availability and contact your JEM administrators.")
	JEMC168E("0168", "Unable to write job object in xml format", MessageLevel.ERROR),

	/**
	 * "Unable to create statistic record", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the node is trying to create statistics but it gets an exception.<br>Please contact your JEM administrators.")
	JEMC169W("0169", "Unable to create statistic record", MessageLevel.WARNING),

	/**
	 * "Unable to move job from checking map to input one", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to put a job in input map.<br>Please contact your JEM administrators.")
	JEMC170E("0170", "Unable to move job {0} from checking map to input one", MessageLevel.ERROR),

	/**
	 * "Unable to move job {0} from checking map to output one", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to put a job in output map, after a validation of JCL.<br>Please contact your JEM administrators.")
	JEMC171E("0171", "Unable to move job {0} from checking map to output one", MessageLevel.ERROR),

	/**
	 * ""Node is operational", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node, after applying all REDO statements, is again ready to work.")
	JEMC172I("0172", "Node is operational", MessageLevel.INFO),

	/**
	 * ""Node is not operational", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to persist the job in queues on data base.<br>Please check database availability and contact your JEM administrators.")
	JEMC173E("0173", "Node is not operational", MessageLevel.ERROR),

	/**
	 * ""Unable to store node information in memory data map", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to store its data in Hazelcast.<br>Please contact your JEM administrators.")
	JEMC174E("0174", "Unable to store node information in memory data map", MessageLevel.ERROR),

	/**
	 * "Unable to move job {0} from running map to output one", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to put a job in output map, job execution.<br>Please contact your JEM administrators.")
	JEMC175E("0175", "Unable to move job {0} from running map to output one", MessageLevel.ERROR),

	/**
	 * "Redo {0} applied", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node is able to redo all cached actions, after that database is available again.")
	JEMC176I("0176", "Redo {0} applied", MessageLevel.INFO),

	/**
	 * "All Redo statemente ({0}) has been applied", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node applied all redo statements.")
	JEMC177I("0177", "All Redo statemente ({0}) has been applied", MessageLevel.INFO),

	/**
	 * "Unable to apply redo statements", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to apply redo statement on data base.<br>Please check database availability and contact your JEM administrators.")
	JEMC178E("0178", "Unable to apply redo statements", MessageLevel.ERROR),

	/**
	 * "Store a redo statement: {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node is not able to persist data and save a redo statement in Hazelcast map.")
	JEMC179I("0179", "Store a redo statement: {0}", MessageLevel.INFO),

	/**
	 * "Unable to store a redo statement", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to store redo statement in a Hazelcast map.<br>Please contact your JEM administrators.")
	JEMC180E("0180", "Unable to store a redo statement", MessageLevel.ERROR),

	/**
	 * "Unable to check the free space of global file system", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to read the freespace of OUTPUT folder.<br>Please check file system availability and contact your JEM administrators.")
	JEMC181E("0181", "Unable to check the free space of global file system", MessageLevel.ERROR),

	/**
	 * "Not enough free space in global file system: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is aware that there is less than 10 MB free on global file system.<br>Please check file system usage and contact your JEM administrators.")
	JEMC182E("0182", "Not enough free space in global file system: {0}", MessageLevel.ERROR),

	/**
	 * "Statistics persistence is disabled by configuration", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when in JEM node configuration file the statistics manager doesn't have 'path' attribute.<br/> No statistics record is saved on filesystem.")
	JEMC183W("0183", "Statistics persistence is disabled", MessageLevel.WARNING),

	/**
	 * "Unable to extract jem version from jem.jar", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when JEM node is unable to extract jem version from jem.jar")
	JEMC184W("0184", "Unable to extract jem version from jem.jar", MessageLevel.WARNING),

	/**
	 * "Installed nodes has different release version, the behavior of the cluster may become unpredictable."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when JEM nodes inside the cluster have different release version")
	JEMC185W("0185", "Installed nodes has different release version, the behavior of the cluster may become unpredictable", MessageLevel.WARNING),

	/**
	 * "Path \"{0}\" doesn't exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you're going thru global file system but you asked a path that doesn't exist! Please check inserted path.")
	JEMC186E("0186", "Path \"{0}\" does not exist", MessageLevel.ERROR),

	/**
	 * "Path \"{0}\" is not a folder", MessageLevel.ERROR);
	 */
	@Description(explanation = "It occurs when you're going thru global file system but you asked a path that is not a path but a file! Please check inserted path.")
	JEMC187E("0187", "Path \"{0}\" is not a folder", MessageLevel.ERROR),

	/**
	 * "Path \"{0}\" is not a file", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you're going thru global file system but you asked a file that is not a file but a path! Please check inserted file name.")
	JEMC188E("0188", "Path \"{0}\" is not a file", MessageLevel.ERROR),

	/**
	 * "JEM is started in maintainance mode", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the node is started in access maint, by the JEM property set to true.")
	JEMC189I("0189", "JEM is started in maintainance mode", MessageLevel.INFO),

	/**
	 * "Job {0} was executing. ID: {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "Display the job name and id of job which was executing and it was in ruuning queue due to a cluster failover. It moved on output queue. Please check the job if any action is needed to recover any inconsistent situation.")
	JEMC190W("0190", "Job {0} was executing. ID: {1}", MessageLevel.WARNING),

	/**
	 * "Installed nodes has different release version", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when JEM nodes inside the cluster have different release version")
	JEMC191E("0191", "Installed nodes has different release version", MessageLevel.ERROR),

	/**
	 * "Installed nodes has different release version", MessageLevel.WARNING
	 */
	@Description(explanation = "Print jvm information")
	JEMC192I("0192", "JVM version: {0}, home: {1}, vendor: {2}", MessageLevel.INFO),

	/**
	 * "Installed nodes has different release version", MessageLevel.WARNING
	 */
	@Description(explanation = "Print databe url on node startup")
	JEMC193I("0193", "Database url: {0}", MessageLevel.INFO),

	/**
	 * "Platform not supported!", MessageLevel.ERROR
	 */
	@Description(explanation = "JEM is running in a platform not supported yet, to execute any job. Please contact your JEM administrator.")
	JEMC194E("0194", "Platform not supported!", MessageLevel.ERROR),

	/**
	 * "Remote-object \"{0}\" is added", MessageLevel.INFO
	 */
	@Deprecated
	@Description(explanation = "Display the name of RMI EXTERNAL object, binded on registry. It's used for ANT utilities")
	JEMC195I("0195", "External Remote-object \"{0}\" is added", MessageLevel.INFO),

	/**
	 * "Unable to add remote-object \"{0}\"", MessageLevel.WARNING
	 */
	@Deprecated
	@Description(explanation = "Display the name of RMI object which wasn't able to be binded on registry. See the exception. It's used for ANT utilities")
	JEMC196W("0196", "Unable to add external remote-object by \"{0}\"", MessageLevel.WARNING),

	/**
	 * "{0} is been routed to environment {1}, cannot provide output.",
	 * MessageLevel.INFO
	 */
	@Description(explanation = "Inform user that the job was routed so it is not possible to get the log back.")
	JEMC197I("0197", "{0} is been routed to environment {1}, cannot provide output.", MessageLevel.WARNING),

	/**
	 * "Arguments \"{0}\" and \"{1}\" are not compatible", MessageLevel.WARNING
	 */
	@Description(explanation = "Using the command line, you're using 2 arguments that are non comaptible. That means JEM is not able to apply both. Please check better command line. ")
	JEMC198W("0198", "Arguments \"{0}\" and \"{1}\" are not compatible", MessageLevel.WARNING),

	/**
	 * "User private key is not protected by password", MessageLevel.WARNING
	 */
	@Description(explanation = "Inform user that the RSA private key supplied is not protected by password")
	JEMC199W("0199", "User private key is not protected by password", MessageLevel.WARNING),

	/**
	 * "The certificate with alias {0} is not a X.509 certificate or is not present in the keystore"
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "Inform user that the provided certificate is not a valid X.509 certificate or is not present in the keystore.")
	JEMC200E("0200", "The certificate with alias {0} is not a X.509 certificate or is not present in the keystore", MessageLevel.ERROR),

	/**
	 * "Certificate for user {0} has expired, you need to substitute it.",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Inform user that the user certificate has expired.")
	JEMC201E("0201", "Certificate for user {0} has expired on {1}, you need to substitute it.", MessageLevel.ERROR),

	/**
	 * "Using socket interceptor for autentication", MessageLevel.INFO
	 */
	@Description(explanation = "Inform that nodes are connecting via socket interceptor.")
	JEMC202I("0202", "Using socket interceptor for autentication", MessageLevel.INFO),

	/**
	 * "request/response on login phase {0}", MessageLevel.INFO
	 */
	@Description(explanation = "print the protocol during login phase.")
	JEMC203I("0203", "request/response on login phase\n {0}", MessageLevel.INFO),

	/**
	 * "Error while decrypting password", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "Error while decrypting password")
	JEMC204E("0204", "Error while decrypting password", MessageLevel.ERROR),

	/**
	 * "User private key need password that has not been provided",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "Error while decrypting password")
	JEMC205E("0205", "User private key need password that has not been provided", MessageLevel.ERROR),

	/**
	 * ""Unable to get FTp stream for file \"{0}\" with reply code \"{1}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it's not able to have FTP stream. Please check if file exists.")
	JEMC206E("0206", "Unable to get FTP stream for file \"{0}\" with reply code \"{1}\"", MessageLevel.ERROR),
	
	/**
	 * "Unable to load licenses information", MessageLevel.ERROR
	 */
	@Deprecated
	@Description(explanation = "It occurs when is not able to read the licenses, required by user interface. Please check licenses files.")
	JEMC207E("0207", "Unable to load licenses information", MessageLevel.ERROR),

	/**
	 * "Inconsistent entity: current: {0}, modified {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are trying to modify an entity that someone else has already chanegd and saved.<br>reapply own updates to new instance.")
	JEMC208E("0208", "Inconsistent entity: current: {0}, modified {1}", MessageLevel.ERROR),
	
	/**
	 * "Used default value for parallel jobs due to inconsistent value: {0} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a prallel jobs value has been set in configuration file but it's not a number. Default value is used.")
	JEMC209W("0209", "Used default value for parallel jobs due to inconsistent value: {0} ", MessageLevel.WARNING),
	
	/**
	 * "Used default value for parallel jobs due to the value {0} is too high. Maximum is {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a prallel jobs value has been set in configuration file but it's too high. Default value is used.")
	JEMC210W("0210", "Used default value for parallel jobs due to the value {0} is too high. Maximum is {1}", MessageLevel.WARNING),
	
	/**
	 * "Used default value for parallel jobs due to the value {0} is too low. Minimum is {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a prallel jobs value has been set in configuration file but it's too high. Default value is used.")
	JEMC211W("0211", "Used default value for parallel jobs due to the value {0} is too low. Minimum is {1}", MessageLevel.WARNING),
	
	/**
	 * "Parallel jobs is set to {0} ", MessageLevel.INFO
	 */
	@Description(explanation = "It shows maximum number of jobs executed in parallel inside the node.")
	JEMC212I("0212", "Parallel jobs is set to {0} ", MessageLevel.INFO),

	/**
	 * "Used default value for memory due to inconsistent value: {0} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a prallel jobs value has been set in configuration file but it's not a number. Default value is used.")
	JEMC213W("0213", "Used default value for memory due to inconsistent value: {0} ", MessageLevel.WARNING),
	
	/**
	 * "Used default value for memory due to the value {0} is too high. Maximum is {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a prallel jobs value has been set in configuration file but it's too high. Default value is used.")
	JEMC214W("0214", "Used default value for memory due to the value {0} is too high. Maximum is {1}", MessageLevel.WARNING),
	
	/**
	 * "Used default value for memory due to the value {0} is too low. Minimum is {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a parallel jobs value has been set in configuration file but it's too high. Default value is used.")
	JEMC215W("0215", "Used default value for memory due to the value {0} is too low. Minimum is {1}", MessageLevel.WARNING),
	
	/**
	 * "Memory is set to {0} ", MessageLevel.INFO
	 */
	@Description(explanation = "It shows maximum number of jobs executed in parallel inside the node.")
	JEMC216I("0216", "Memory is set to {0} ", MessageLevel.INFO),
	
	/**
	 * "Invalid submitter class:  {0} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a invalid submitter is running on pool. Please contact your JEM administrator.")
	JEMC217E("0217", "Invalid submitter class:  {0} ", MessageLevel.ERROR),
	
	/**
	 * "Invalid submitter class:  {0} ", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when node is shutting down, informing about how many jobs are still running on node.")
	JEMC218I("0218", "{0} jobs are still running", MessageLevel.INFO),
	
	/**
	 * "Recovery from database is staring", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when Hazelcast has got some data loss and JEM recovers the data from database.")
	JEMC219I("0219", "Recovery from database is started", MessageLevel.INFO),
	
	/**
	 * "Recovery of map {0} is started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a recovery of a map is started.")
	JEMC220I("0220", "Recovery of map {0} is started", MessageLevel.INFO),
	
	/**
	 * "Recovery from database is ended in {0} ms", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a recovery from database is ended an shows how long it took.")
	JEMC221I("0221", "Recovery from database is ended in {0} ms", MessageLevel.INFO),
	
	/**
	 * "Unable to recovery map {0} from database", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a recovery from database thows an exception. Please contact your JEM administrator.")
	JEMC222E("0222", "Unable to recovery map {0} from database", MessageLevel.ERROR),
	
	/**
	 * "{0} has been removed but is not in queue", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a member has been removed but it's not in queue where events are stored. Please contact your JEM administrator.")
	JEMC223W("0223", "{0} has been removed but is not in queue", MessageLevel.WARNING),
	
	/**
	 * "Error in multicast service ", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is an exception in the multicast service.")
	JEMC224E("0224", "Error in multicast service", MessageLevel.ERROR),

	/**
	 * "Received multicast request from web client with address:{0}",
	 * MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a correct multicast request is received form a web client.")
	JEMC225I("0225", "Received multicast request from web client with address:{0}", MessageLevel.INFO),

	/**
	 * "Received from host {0} unknown message on JEM multicast group {0}:{1}. Unknown message: {2}"
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when we received an unknown message on JEM multicast group and port.")
	JEMC226W("0226", "Received from host {0} unknown message on JEM multicast group {0}:{1}. Unknown message: {2}", MessageLevel.WARNING),

	/**
	 * "Multicast service {0}.", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the multicast service change status.")
	JEMC227I("0227", "Multicast service {0}", MessageLevel.INFO),

	/**
	 * "Unable to stop Multicast service gracefully.", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the Multicast service can't be stopped gracefully.")
	JEMG228W("0228", "Unable to stop Multicast service gracefully.", MessageLevel.WARNING),
	
	/**
	 * " "Unable to clean up information in modes memory data map", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the node is not able to clean its nodes data in Hazelcast.<br>Please contact your JEM administrators.")
	JEMC229E("0229", "Unable to clean up information in modes memory data map", MessageLevel.ERROR),
	
	/**
	 * "Error executing Http Login Method. Exception: \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an error occurs executing Http login Method.<br> Please check the log file for details.")
	JEMC230E("0230", "Error executing Http Login Method. Exception: \"{0}\"", MessageLevel.ERROR),

	/**
	 * "Error executing Http Login Method. Exception: \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an error occurs executing Http login Method.<br> Please check the log file for details.")
	JEMC231E("0231", "Error executing Http Logoff Method. Exception: \"{0}\"", MessageLevel.ERROR),
	
	/**
	 * "Object is to a file what does not exist: {0}.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI dataset factory is looking for a file what doesn't exist.<br> Please check JCL definition.")
	JEMC232E("0232", "Object is a file reference what does not exist: {0}.", MessageLevel.ERROR),
	
	/**
	 * "The protocol must be 'ftp' or 'ftps' instead of \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI FTP factory is trying to create a URL but the property set is not well-formed.<br> Please check FTP resource definition.")
	JEMC233E("0233", "FTP URL \"{0}\" is malformed.", MessageLevel.ERROR),
	
	/**
	 * "Unable to create FTP client or to get file stream.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI FTP factory is not able to create a FTP client.<br> Please check FTP exception.")
	JEMC234E("0234", "Unable to create FTP client or to get file stream.", MessageLevel.ERROR),
	
	/**
	 * "Unable to add references by name.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI JMS factory is not able to add environemnt property to context.<br> Please check JMS exception.")
	JEMC235E("0235", "Unable to add references by name.", MessageLevel.ERROR),
	
	/**
	 * "Invalid internal key value.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when absolute map, cross classloaders, received a wrong key to decrypt values.<br> Please contact your JEM administrator.")
	JEMC236E("0236", "Invalid internal key value.", MessageLevel.ERROR),

	/**
	 * "Unable to invoke the proxy method.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when absolute map, cross classloaders, is not able to call a mthod by a proxy.<br> Please contact your JEM administrator.")
	JEMC237E("0237", "Unable to invoke the proxy method.", MessageLevel.ERROR),
	
	/**
	 * "Unable to read or save file {0}.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are trying to read or save a file by a executor.<br>Please check the generated exception.")
	JEMC238E("0238", "Unable to read or save file {0}.", MessageLevel.ERROR),
	
	/**
	 * "Unable to remove a certificate.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are trying to add, remove or get certificates.<br>Please contact your JEM administrator.")
	JEMC239E("0239", "Unable to manage certificates.", MessageLevel.ERROR),
	
	/**
	 * "Unable to encrypt a secret.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are trying to encrypt a secret.<br>Please contact your JEM administrator.")
	JEMC240E("0240", "Unable to encrypt a secret.", MessageLevel.ERROR),
	
	/**
	 * "Unable to check XML configuration content: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are trying to check XML file but is not well formed.<br>Please check XML syntax.")
	JEMC241E("0241", "Unable to check XML configuration content: {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to read file {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you're trying to read a file but an IO error occurs.<br>Please contact your JEM administrator.")
	JEMC242E("0242", "Unable to read file {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to get system activity for job {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you're trying to get job system activity but error occurs.<br>Please contact your JEM administrator.")
	JEMC243E("0243", "Unable to get system activity for job {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to get system activity.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you're trying to get system activity but error occurs.<br>Please contact your JEM administrator.")
	JEMC244E("0244", "Unable to get system activity.", MessageLevel.ERROR),
	
	/**
	 * "{0} is not supported.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when node is starting on operating system not supported.<br>Please check JEM requirements.")
	JEMC245E("0245", "{0} is not supported.", MessageLevel.ERROR),
	
	/**
	 * "Content of job {0}\n{1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a submit command has been executed and -printOutput parm has been set.")
	JEMC246I("0246", "Content of job {0}\n{1}", MessageLevel.ERROR),
	
	/**
	 * "Unable to delete WAR folder", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you are creating a node but the process is not able to remove WAR lib folder.")
	JEMC247E("0247", "Unable to delete war/lib folder", MessageLevel.ERROR),

	/**
	 * "Selected interface {0} for multicast service", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when in hazelcast configuration interfaces is enabled and JEM uses those to set the right onr for multicast service.")
	JEMC248I("0248", "Selected interface {0} for multicast service", MessageLevel.INFO),

	/**
	 * "No interfaces of the nodes matches the one configured in hazelcast configuration", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the inetrfaces set in hazelcast configuration does not match the node interfaces")
	JEMC249W("0249", "No interfaces of the nodes matches those configured in hazelcast configuration or network inetrfaces are not properly defined", MessageLevel.WARNING),
	
	/**
	 * "Unable to extract the interfaces by network service", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM tries to extract the right netwoirk interface to use but network service return an excpetion.")
	JEMC250E("0250", "Unable to extract the interfaces by network service", MessageLevel.ERROR),
	
	/**
	 * "File {0} does not match with any dataset rule", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the filename, put in JCL, does not match with any dataset rule. Please have a look and correct file name.")
	JEMC251E("0251", "File {0} does not match with any dataset rule", MessageLevel.ERROR),
	
	/**
	 * "{0} datasets rules loaded", MessageLevel.INFO
	 */
	@Description(explanation = "It shows the amount of datasets rules loaded.")
	JEMC252I("0252", "{0} datasets rules loaded", MessageLevel.INFO),
	
	/**
	 * "Rules file is not defined but the default file {0} exists", MessageLevel.WARNIG
	 */
	@Description(explanation = "It occurs when a data path is defined and rules file name not. JEM tries to create a rule file on the file but the "+
	 "default file already exists. Please have a look to JEM environment configuration.")
	JEMC253W("0253", "Rules file is not defined but the default file {0} exists", MessageLevel.WARNING),
	
	/**
	 * "No rule has been loaded!", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when rules definition file contains wrong defintions and no rule has been loaded. Please have a look at rules file configuration.")
	JEMC254E("0254", "No rule has been loaded", MessageLevel.ERROR),

	/**
	 * "Path \"{0}\" is undefined!", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a rule definition contains a link to undefined path. Please have a look at rules file configuration.")
	JEMC255W("0255", "Path \"{0}\" is undefined", MessageLevel.WARNING),

	/**
	 * "'pathName' is a mandatory attribute!", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when a rule definition doesn't contain any path name. Please have a look at rules file configuration.")
	JEMC256W("0256", "\"pathName\" is a mandatory attribute", MessageLevel.WARNING),
	
	/**
	 * "Unable to load rules file : {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node is not able to load rule definition. Please have a look at JEM environment configuration folder.")
	JEMC257E("0257", "Unable to load rules : {0}", MessageLevel.ERROR),
	
	/**
	 * "Invalid data paths defintion: cluster has got {0} definition, node {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node at startup checks is own data paths are coherent with data paths of cluster. The data paths must be logically "+
	 " the same. Please have a look at JEM node configuration fils.")
	JEMC258E("0258", "Invalid data paths defintions: cluster has got {0} definitions, node {1}", MessageLevel.ERROR),
	
	/**
	 * "Invalid data paths defintion: path {0} does not exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node at startup checks is own data paths are coherent with data paths of cluster. The data paths must be logically "+
	 " the same. Please have a look at JEM node configuration file.")
	JEMC259E("0259", "Invalid data paths defintion: path {0} does not exist", MessageLevel.ERROR),
	
	/**
	 * "Unable to acquire lock for {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node is not able to use lock by semaphore a specific file. Please check what Hazelcast structure is not locked and contatc you JEM adminitrator.")
	JEMC260E("0260", "Unable to acquire lock for {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to release lock for {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node is not able to use lock by semaphore a specific file. Please check what Hazelcast structure is not locked and contatc you JEM adminitrator.")
	JEMC261E("0261", "Unable to release lock for {0}", MessageLevel.ERROR),
	
	/**
	 *  "No rule has been defined!", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node is not able to load rule definition. Please have a look at JEM environment configuration folder.")
	JEMC262E("0262", "No rule has been defined", MessageLevel.ERROR),
	
	/**
	 *  "No datasets pattern has been defined for rule with pathName {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JEM node is not able to load rule definition. Please have a look at JEM environment configuration folder.")
	JEMC263E("0263", "No datasets pattern has been defined for rule with pathName {0}", MessageLevel.ERROR),
	
	/**
	 *  "Upload are allowed only on the following GFS folder: LIBRARY, SOURCE, CLASS, BINARY", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when user try to upload a file in the GFS in a folder different from LIBRARY, SOURCE, CLASS or BINARY.")
	JEMC264E("0264", "Upload are allowed only on the following GFS folder: LIBRARY, SOURCE, CLASS, BINARY", MessageLevel.ERROR),
	
	/**
	 *  "Error while trying to upload file {0}.Exception {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs there is an exception during the upload of a file.")
	JEMC265E("0265", "Error while trying to upload file {0}. Exception {1}", MessageLevel.ERROR),
	
	/**
	 *  "Error while trying to create complete path of uploade file {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs there is an exception during the creation of upload of a file.")
	JEMC266E("0266", "Error while trying to create complete path of uploaded file {0}", MessageLevel.ERROR),
	
	/**
	 *  "Error while trying to rename  temp file {0} to uploaded file {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs there is an exception renaming temporary file to final one during uploading.")
	JEMC267E("0267", "Error while trying to rename  temp file {0} to uploaded file {1}", MessageLevel.ERROR),
	
	/**
	 *  "Error while trying to delete uploaded file {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs there is an exception deleting to final file after upload.")
	JEMC268E("0268", "Error while trying to delete uploaded file {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to login to JEM by REST call.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when JNDI JEM factory is not able to login to JEM.<br> Please check JEM login exception.")
	JEMC269E("0269", "Unable to login to JEM by REST call.", MessageLevel.ERROR),
	
	/**
	 * "System.exit call is not allowed", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a java class call a System.exit inside the job execution.<br> Please check java code.")
	JEMC270E("0270", "System.exit call is not allowed", MessageLevel.ERROR),
	
	/**
	 * "Property \"{0}\" is unknown.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when inside of a resource you used a property not known.<br> Please check definition.")
	JEMC271E("0271", "Property \"{0}\" is unknown.", MessageLevel.ERROR),
	
	/**
	 * "Property \"{0}\" contains a wrong value.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the value assigns to a property has got  awrong value.<br> Please check definition.")
	JEMC272E("0272", "Property \"{0}\" contains a wrong value.", MessageLevel.ERROR),
	
	/**
	 * "Network interface {0} is used", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when Jem choose which network interface to use.")
	JEMC273I("0273", "Network interface {0} is used", MessageLevel.INFO),
	
	/**
	 * "To change the security manager is not allowed!", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to set a new Security manager but it's not allowed.<br>Please change your code to be compliant with JEM security rules.")
	JEMC274E("0274", "To change the security manager is not allowed!", MessageLevel.ERROR),
	
	/**
	 * "Job migration of map \"{0}\" is started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the migration of jobs starts. Shows which map is affected.")
	JEMC275I("0275", "Job migration of map \"{0}\" is started", MessageLevel.INFO),
	
	/**
	 * "Job \"{0}\" has been migrated", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a job has been migrated. Shows the job id.")
	JEMC276I("0276", "Job \"{0}\" has been migrated", MessageLevel.INFO),
	
	/**
	 * "Job \"{0}\" hasn't been migrated due to an exception: {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a job has been migrated. Shows the job id.")
	JEMC277E("0277", "Job \"{0}\" hasn't been migrated due to an exception: {1}", MessageLevel.ERROR),
	
	/**
	 * "Job migration of map \"{0}\" is started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the migration of jobs ends. Shows which map was affected.")
	JEMC278I("0278", "Job migration of map \"{0}\" is ended", MessageLevel.INFO),
	
	/**
	 * "Resource migration is started", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the migration of resource starts.")
	JEMC279I("0279", "Resource migration is started", MessageLevel.INFO),
	
	/**
	 * "Resource \"{0}\" has been migrated", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a resource has been migrated.")
	JEMC280I("0280", "Resource \"{0}\" has been migrated", MessageLevel.INFO),
	
	/**
	 * "Resource \"{0}\" hasn't been migrated due to an exception: {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a resouce has been migrated. Shows the resource name.")
	JEMC281E("0281", "Resource \"{0}\" hasn't been migrated due to an exception: {1}", MessageLevel.ERROR),
	
	/**
	 * "Resource migration is ended", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the migration of jobs ends. Shows which map was affected.")
	JEMC282I("0282", "Resource migration is ended", MessageLevel.INFO),
	
	/**
	 * "XML: {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the migration ends with exception and shows the XML.")
	JEMC283I("0283", "XML: {0}", MessageLevel.INFO),
	
	/**
	 * "{0} method is not supported from{1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the client access to HTTPS submitter without using POST.<br> Please checks the client implementation.")
	JEMC284W("0284", "{0} method is not supported from {1}", MessageLevel.WARNING),
	
	/**
	 * "URI {0} action is not supported from {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the client access to HTTPS submitter using a wrong URI.<br> Please checks the client implementation.")
	JEMC285W("0285", "URI {0} action is not supported from {1}", MessageLevel.WARNING),
	
	/**
	 * "Wrong signature for user {0} from {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the client access to HTTPS submitter using a wrong signature.<br> Please checks the client execution.")
	JEMC286W("0286", "Wrong signature for user {0} from {1}", MessageLevel.WARNING),
	
	/**
	 * "Signature for user {0} is missing from {1}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the client access to HTTPS submitter without passing any signature.<br> Please checks the client execution.")
	JEMC287W("0287", "Signature for user {0} is missing from {1}", MessageLevel.WARNING),
	
	/**
	 * "Environment or password of JEM environment is not valid from {0}", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the client access to HTTPS submitter with worng environment or password.<br> Please checks the client execution.")
	JEMC288W("0288", "Environment or password of JEM environment is not valid from {0}", MessageLevel.WARNING),
	
	/**
	 * "Unable to callback {0} after the end of the job {1}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the job lifecycle listener is not able to connect the clinet in HTTP listening mode.<br>Please have a look to the root exception.")
	JEMC289E("0289", "Unable to callback {0} after the end of the job {1}", MessageLevel.ERROR),
	
	/**
	 * "JAVA {0} on {1} has been loaded", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when during the startup if node, it loads all JAVA defined and usable from JEM to execute jobs.")
	JEMC290I("0290", "JAVA {0} on {1} has been loaded", MessageLevel.INFO),
	
	/**
	 * "JAVA {0} on {1} has been loaded and used as default", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when during the startup if node, it loads all JAVA defined and usable from JEM to execute jobs. it uses as default.")
	JEMC291I("0291", "JAVA {0} on {1} has been loaded and used as default", MessageLevel.INFO),
	
	/**
	 * "JAVA {0} on {1} does not exist", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when during the startup if node, it loads all JAVA defined and the folder of JAVA home doesn't exists.<br>Please have a look to the node configuration.")
	JEMC292E("0292", "JAVA {0} on {1} does not exist", MessageLevel.ERROR),
	
	/**
	 * "A default JAVA has been already set. JAVA {0} is NOT the JAVA default", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when during the startup if node, it loads all JAVA defined and the JAVA definition says it's the default but there is already another default JAVA set.<br>Please have a look to the node configuration.")
	JEMC293W("0293", "A default JAVA has been already set. JAVA {0} is NOT the JAVA default", MessageLevel.WARNING),
	
	/**
	 * "Hazelcast config is incomplete. Map {0} is NOT defined", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when during the startup the Hazelcast configuration is not completed and a map is missing.<br>Please have a look to the Hazelcast configuration.")
	JEMC294E("0294", "Hazelcast config is incomplete. Map {0} is NOT defined", MessageLevel.ERROR),
	
	/**
	 * "Unable to get the objects from database", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when it tries to get the data directly from database when teh Hazelcast eviction is active.")
	JEMC295E("0295", "Unable to get the objects from database", MessageLevel.ERROR);
	/**
	 * The {@link Message} created in the constructor corresponding to an
	 * instance of <code>NodeMessage</code>.
	 * 
	 * @see Message
	 */
	private Message message;

	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code>
	 * constructor and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at
	 *            runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private NodeMessage(String code, String messageContent, MessageLevel level) {
		this.message = new Message(code, MessageCode.NODE.getCode(), messageContent, level);
	}

	/**
	 * It returns the {@link Message} corresponding to an
	 * <code>NodeMessage</code> instance.
	 * 
	 * @return the {@link Message} corresponding to an <code>NodeMessage</code>
	 *         instance.
	 */
	@Override
	public Message toMessage() {
		return this.message;
	}
}
