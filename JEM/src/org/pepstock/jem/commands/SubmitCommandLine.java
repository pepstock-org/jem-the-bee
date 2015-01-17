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
package org.pepstock.jem.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.pepstock.jem.Job;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.Parser;

/**
 * Root class of SUBMIT command line engines. Contains all common arguments to submit
 * jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class SubmitCommandLine extends UserIDCommand {
	
	private final Map<String, SubmitArgument> arguments = new HashMap<String, SubmitArgument>();
	
	private String commandName = null;
	
	private String jcl = null;

	private String type = null;

	private boolean wait = false;
	
	private Job job = null;

	/**
	 * Constructs the object saving the command name (necessary on help) and adding arguments definitions.
	 * 
	 * @param commandName command name  (necessary on help)
	 */
	public SubmitCommandLine(String commandName) {
		this.commandName = commandName;
		arguments.put(SubmitParameters.JCL.getName(), SubmitParameters.createArgument(SubmitParameters.JCL, true));
		arguments.put(SubmitParameters.TYPE.getName(), SubmitParameters.createArgument(SubmitParameters.TYPE));
		arguments.put(SubmitParameters.WAIT.getName(), SubmitParameters.createArgument(SubmitParameters.WAIT));
	}
	
	/**
	 * @return the commandName
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @return the jcl
	 */
	public String getJcl() {
		return jcl;
	}

	/**
	 * @param jcl the jcl to set
	 */
	public void setJcl(String jcl) {
		this.jcl = jcl;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the wait
	 */
	public boolean isWait() {
		return wait;
	}

	/**
	 * @param wait the wait to set
	 */
	public void setWait(boolean wait) {
		this.wait = wait;
	}
	/**
	 * @return the arguments
	 */
	public Map<String, SubmitArgument> getArguments() {
		return arguments;
	}
	
	/**
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * It's called before submitting job. Here it checks the arguments passed by command line,
	 * saving the values
	 * 
	 * @throws SubmitException if any exception occurs
	 */
	public void beforeJobSubmit() throws SubmitException {
		SubmitArgument saJcl = arguments.get(SubmitParameters.JCL.getName());
		setJcl(saJcl.getValue());
		if (arguments.containsKey(SubmitParameters.TYPE.getName())){
			SubmitArgument saType = arguments.get(SubmitParameters.TYPE.getName());
			setType(saType.getValue());
		}
		
		if (arguments.containsKey(SubmitParameters.WAIT.getName())){
			SubmitArgument sakey = arguments.get(SubmitParameters.WAIT.getName());
			setWait(Parser.parseBoolean(sakey.getValue(), false));	
		} else {
			setWait(false);
		}
	}
	
	/**
	 * Submit the passed job. It must read JCL from URL and submit it.
	 * 
	 * @throws SubmitException if any exception occurs
	 */
	public abstract void jobSubmit() throws SubmitException;
	/**
	 * Checks the job execution, getting the return code
	 * @return return code of job execution
	 * @throws SubmitException if any exception occurs
	 */
	public abstract int afterJobSubmit() throws SubmitException;
	
	/**
	 * Parse arguments of command line
	 * @param args arguments passed by command line
	 * @throws ParseException if any exception occurs during arguments parsing 
	 */
	
	public final void parseArguments(String[] args) throws ParseException{
		// gets arguments
		Map<String, SubmitArgument> currArguments = getArguments();
		// parses args using command line
		ArgumentsParser parser = new ArgumentsParser(getCommandName());
		synchronized (OptionBuilderLock.getLock()) {
			// scans defined arguments and creates the option
			for (SubmitArgument argument : currArguments.values()){
				@SuppressWarnings("static-access")
				Option op = OptionBuilder.withArgName(argument.getParameter().getName()).hasArg().withDescription(argument.getParameter().getDescription()).create(argument.getParameter().getName());
				// sets if is required
				op.setRequired(argument.isRequired());
				// adds to parser
				parser.getOptions().add(op);
			}
			// gets the result in a properties
			Properties properties = parser.parseArg(args);
			
			// scans all properties to add value
			for(Object keyString : properties.keySet()){
				String key = keyString.toString();
				SubmitArgument sa = currArguments.get(key);
				sa.setValue(properties.getProperty(key));
			}
		}
	}
	
	/**
	 * Executes all necessary steps to submit the job.
	 * 
	 * @param args arguments passed by command line
	 * @return result of job execution
	 */
	public SubmitResult execute(String[] args){
		// creates logger
		LogAppl.getInstance();
		
		// result and return code instances
		SubmitResult result = new SubmitResult();
		int rc = 0;
		try {
			// parses arguments
			parseArguments(args);
			// before submit check
			beforeJobSubmit();
			// subit job
			jobSubmit();
			// takes the time 
			Times.submit();
			// gets return code
			rc = afterJobSubmit();
		} catch (ParseException e) {
			// with any exception, ends in CC 1
			LogAppl.getInstance().emit(NodeMessage.JEMC033E, e);
			// sets return code to error
			rc = 1;
		} catch (Exception e) {
			// with any exception, ends in CC 1
			LogAppl.getInstance().emit(NodeMessage.JEMC033E, e);
			// sets return code to error
			rc = 1;
		}
		// checks if job instance is present
		if (job != null) {
			// sets job id
			result.setJobId(job.getId());
		}
		// sets return code
		result.setRc(rc);
		return result;
	}
	
}
