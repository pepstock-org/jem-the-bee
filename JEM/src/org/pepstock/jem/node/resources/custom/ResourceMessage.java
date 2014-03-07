/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.custom;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM Custom Resources. <br>
 * It is a list of <code>ResourceMessage</code>. <br> 
 * Each <code>ResourceMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public enum ResourceMessage implements MessageInterface{

	/**
	 * "Wrong resource template file directory: \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if the resource template file for the the custom resource User interface is configured not properly.<br>In particular it means that the directory of the template file (specified in the log: {0}) does not exist or is the root directory, for example '/customResourceConfiguration.xml' or 'customResourceConfiguration.xml' or '/dir/customResourceConfiguration.xml' if 'dir' doesn't exist.<br>An exception is thrown.<br>Check the Node configuration. Correct it.")
	JEMR001E(1, "Wrong resource template file directory: \"{0}\".", MessageLevel.ERROR),
	
	/**
	 * "Configuration resource template file \"{0}\" does not exist. Create new resource template file \"{0}\" inside directory: \"{1}\"."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs if the resource template file for the custom resource User interface (specified in the log: {0}) inside Node configuration does not exist.<br>No exception is thrown, but this warning is logged.<br>Check the node configuration. Write an existing resource template file, and automatically it will be loaded, so no node restart is needed.")
	JEMR002W(2, "Configuration resource template file \"{0}\" does not exist. Create new resource template file \"{0}\" inside directory: \"{1}\".", MessageLevel.WARNING),
	
	/**
	 * "Error loading ResourceTemplate.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is a problem loading the resource template file for the custom resources User interface.<br>No exception is thrown, but this error is logged.<br>Check if the resource template file is an XML well formed and correct it. Automatically it will be reloaded, so no node restart is needed.")
	JEMR003E(3, "Error loading ResourceTemplate.", MessageLevel.ERROR),

	/**
	 * "Error starting automatic control for resource template file changes. The template file is: \"{0}\"."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is an error trying to start the automatic control for resource template file changes.<br>It may occurs if the automatic control is already started, or if there is a problem initializing the directory Observer that checks directory changes. It is a system error.")
	JEMR004E(4, "Error starting automatic control for resource template file changes. The template file is: \"{0}\".", MessageLevel.ERROR),
	
	/**
	 * "Error reading resource template file \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is a problem reading the resource template file for the custom resources User interface (specified in the log: {0}).<br>An exception is thrown.<br>Check if the resource template file is an XML well formed and correct it. Automatically it will be reloaded, so no node restart is needed.")
	JEMR005E(5, "Error reading resource template file \"{0}\".", MessageLevel.ERROR),

	/**
	 * "The resource template xml file \"{0}\" has been deleted, using previous ResourceTemplate. Create new resource template file \"{0}\" inside directory: {1}."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "<br>It occurs if someone delete the resource template file for the resource template User interface (specified in the log: {0}).<br>No exception is thrown, but this warning is logged.<br>Create a new resource template xml file in the correct directory, and automatically it will be reloaded, so no node restart is needed.")
	JEMR006W(6, "The resource template xml file \"{0}\" has been deleted, using previous ResourceTemplate. Create new resource template file \"{0}\" inside directory: {1}.", MessageLevel.WARNING),

	/**
	 * "Error building {0}. {1} is null. It is mandatory.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when building the descriptor {0} the following thing (specified in the log: {1}) is null.<br>An exception is thrown.<br>Check the log and the resource template configuration file. In the log the cause is written, so it is possible to correct the error.")
	JEMR007E(7, "Error building: {0}. {1} is null. It is mandatory.", MessageLevel.ERROR),
	
	/**
	 * "Type of field not found: {0}.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when building a field descriptor the type of the field template (specified in the log: {0}) is unknown.<br>An exception is thrown.")
	JEMR008E(8, "Type of field not found: {0}.", MessageLevel.ERROR),

	/**
	 * "List field must have at least one value!", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when building a list field descriptor the list field template has no values.<br>An exception is thrown.<br>Check the resource template xml file and add at least one value.")
	JEMR009E(9, "List field must have at least one value!", MessageLevel.ERROR),
	
	/**
	 * "In a SingleSelectableListFieldTemplate the default value \"{0}\" must be contained inside the list of values.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when building a SingleSelectableListFieldTemplateDescriptor the default value (specified in the log: {0}) is not contained inside the list of values.<br>An exception is thrown.<br>Check the resource template xml file and change the default value or add it in the list of values.")
	JEMR010E(10, "In a SingleSelectableListFieldTemplate the default value \"{0}\" must be contained inside the list of values.", MessageLevel.ERROR),
	
	/**
	 * "Configured custom resource definition \"{0}\" is not an instance of ResourceDefinition"
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>" + "Check the class name and classpath of JEM node because is not a ResourceDefinition.")
	JEMR011E(11, "Configured custom resource definition \"{0}\" is not an instance of ResourceDefinition", MessageLevel.ERROR),
	
	/**
	 * "Impossible to add Configured custom resource definition: \"{0}\" is null."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs adding a Configured custom resource definition because the value (specified in the log: {0}) is null." + "Check the JEM node configuration and the custom classes added.")
	JEMR012E(12, "Impossible to add Configured custom resource definition: \"{0}\" is null.", MessageLevel.ERROR),
	
	/**
	 * "Impossible to get Configured custom resource definition: \"{0}\" is null."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs getting a Configured custom resource definition because the value (specified in the log: {0}) is null.")
	JEMR013E(13, "Impossible to get Configured custom resource definition: \"{0}\" is null.", MessageLevel.ERROR),

	/**
	 * "Resource type \"{0}\" not found inside the Configured custom resource definitions."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs getting a Configured custom resource definition because the resource type (specified in the log: {0}) is not configured." + "Check the JEM node configuration and the custom classes added.")
	JEMR014E(14, "Resource type \"{0}\" not found inside the Configured custom resource definitions.", MessageLevel.ERROR),
	
	/**
	 * "Unable to load class \"{0}\"", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the class name which wasn't able to be loaded.<br>Check the class name and classpath of JEM node.")
	JEMR015E(15, "Unable to load class \"{0}\"", MessageLevel.ERROR),

	/**
	 * "ResourceDefinition \"{0}\" of type \"{1}\" loaded", MessageLevel.INFO
	 */
	@Description(explanation = "Display the ResourceDefinition loaded.")
	JEMR016I(16, "ResourceDefinition \"{0}\" of type \"{1}\" loaded", MessageLevel.INFO),

	/**
	 * "No The resource template xml file configured for Resource\"{0}\". Add template xml file in configuration.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs getting the ResourceDescriptor associated to a resource from file when no resource template xml file is configured. Add template xml file in configuration")
	JEMR017E(17, "No The resource template xml file configured for Resource\"{0}\". Add template xml file in configuration.", MessageLevel.ERROR),
	
	/**
	 * "Impossible to find Configured custom resource definition: \"{0}\" is null."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs finding a Configured custom resource definition because the value (specified in the log: {0}) is null.")
	JEMR018E(18, "Impossible to find Configured custom resource definition: \"{0}\" is null.", MessageLevel.ERROR),

	/**
	 * "Removed resource \"{0}\" of type \"{1}\": type no longer exists!"
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the sturtup phase of jem is found a resource of type (specified in the log: {1}) no longer existing: it is removed.")
	JEMR019W(19, "Removed resource \"{0}\" of type \"{1}\": type no longer exists!", MessageLevel.WARNING),

	/**
	 * "Loading ResourceDescriptor from file \"{1}\"."
	 * , MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a custom resource loads the ResourceDescriptor from file (specified in the log: {0}).")
	JEMR020I(20, "Loaded ResourceDescriptor from file \"{0}\".", MessageLevel.INFO),
	
	/**
	 * "Loaded ResourceDescriptor for resource type \"{0}\" from file \"{1}\"."
	 * , MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the ResourceDescriptor is loaded or reloaded from file (specified in the log: {0}).")
	JEMR021I(21, "Loading ResourceDescriptor for resource type \"{0}\" from file \"{1}\".", MessageLevel.INFO),
	
	/**
	 * "List Field must be the only one field in a section!"
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when someone defines in the template xml file a section containing a List Field and other fields. List Field must be the only one field in a section.")
	JEMR022E(22, "List Field must be the only one field in a section!", MessageLevel.ERROR),
	
	/**
	 * ""
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when you try to load more than one custom resource with the same type")
	JEMR023E(23, "Found duplicate custom resource with type \"{0}\". Check the configuration.", MessageLevel.ERROR),
	
	/**
	 * "Changing a resource definition type from \"{0}\" to \"{1}\", it was not possible to clean the resources of the old type!"
	 * , MessageLevel.WARN
	 */
	@Description(explanation = "It occurs when changing the type of a resource definition it was not possible to remove the resource of the old type")
	JEMR024W(24, "Changing a resource definition type from \"{0}\" to \"{1}\", it was not possible to clean the resources of the old type!", MessageLevel.WARNING),
	
	/**
	 * "Changed the resource definition type from \"{0}\" to \"{1}\"."
	 * , MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when changing the type of a resource definition.")
	JEMR025I(25, "Changed the resource definition type from \"{0}\" to \"{1}\".", MessageLevel.INFO),
	
	/**
	 * "Deleted all resources of type: \"{0}\"."
	 * , MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when a custom resource type is deleted: all corresponding resources must be removed.")
	JEMR026I(26, "Deleted all resources of type: \"{0}\".", MessageLevel.INFO);
	
	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>ResourceMessage</code>. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor
	 * and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string do display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private ResourceMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.RESOURCE.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>ResourceMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>ResourceMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}
