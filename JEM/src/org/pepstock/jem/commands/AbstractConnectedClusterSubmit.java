/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.Result;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.jobs.GetMessagesLog;
import org.pepstock.jem.util.CmdConsole;
import org.pepstock.jem.util.Parser;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Extends the command line engine to submit job connecting directly to cluster (usually by Hazelcast protocol).
 * It contains the necessary parameters for that, as JEM PASSWORD, PRIVATE KEY and PRIVATE KEY PASSWORD, and PRINTOUTPUT. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class AbstractConnectedClusterSubmit extends SubmitCommandLine implements MessageListener<Job> {

	private String password = null;

	private boolean printOutput = false;
	
	private String privateKey = null;

	private String privateKeyPassword = null;

	private HazelcastInstance client = null;
	
	private final CountDownLatch lock = new CountDownLatch(1);

	/**
	 * Creates the submit engine passing the command name
	 * 
	 * @param commandName command name  (necessary on help)
	 */
	public AbstractConnectedClusterSubmit(String commandName) {
		super(commandName);
		Map<String, SubmitArgument> arguments = getArguments();
		arguments.put(SubmitParameters.PASSWORD.getName(), SubmitParameters.createArgument(SubmitParameters.PASSWORD));
		arguments.put(SubmitParameters.PRINT_OUTPUT.getName(), SubmitParameters.createArgument(SubmitParameters.PRINT_OUTPUT));
		arguments.put(SubmitParameters.PRIVATE_KEY.getName(), SubmitParameters.createArgument(SubmitParameters.PRIVATE_KEY));
		arguments.put(SubmitParameters.PRIVATE_KEY_PWD.getName(), SubmitParameters.createArgument(SubmitParameters.PRIVATE_KEY_PWD));
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the printOutput
	 */
	public boolean isPrintOutput() {
		return printOutput;
	}

	/**
	 * @param printOutput the printOutput to set
	 */
	public void setPrintOutput(boolean printOutput) {
		this.printOutput = printOutput;
	}

	/**
	 * @return the privateKey
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param privateKey the privateKey to set
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * @return the privateKeyPassword
	 */
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	/**
	 * @param privateKeyPassword the privateKeyPassword to set
	 */
	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	/**
	 * Is called from Hazelcast engine when a job is put in OUTPUT queue, so is
	 * ended. Checks if same job that has sumbitted. If yes, notifies the thread
	 * to end
	 * 
	 * @param endedJob job ended
	 */
	@Override
	public final void onMessage(Message<Job> endedJob) {
		Job job = getJob();
		// check if it was a routed job
		if (endedJob.getMessageObject().getRoutingInfo().getId() != null) {
			// if is not the job submitted, does nothing
			if (!job.getId().equals(endedJob.getMessageObject().getRoutingInfo().getId())){
				return;
			}
			// save job ended, overriding the previous instance beacause routing
			// info has been added
			job = endedJob.getMessageObject();
			setJob(job);
			// if nowait is false remove job from ROUTED QUEUE
			if (!job.isNowait()) {
				IMap<String, Job> routedQueue = client.getMap(Queues.ROUTED_QUEUE);
				routedQueue.remove(job.getRoutingInfo().getId());
			}
		} else {
			// if is not the job submitted, does nothing
			if (!job.equals(endedJob.getMessageObject())){
				return;
			}
			// save job ended, overriding the previous instance (no necessary
			// now
			// anymore)
			job = endedJob.getMessageObject();
			setJob(job);
		}
		// notify that job is ended
		lock.countDown();
	}
	
	/**
	 * Returns a Hazelcast instance to use to connect to JEM cluster and then submit the job
	 * @return Hazelcast instance 
	 * @throws SubmitException if nay excetpion occurs
	 */
	public abstract HazelcastInstance createClient() throws SubmitException;
	
	/**
	 * Submits the job, connecting to Hazelcast, reading JCL content and
	 * creating new PRE job object, adds itself as listener to a hazelcast topic
	 * 
	 * @throws Exception if errors occur
	 */
	@Override
	public void jobSubmit() throws SubmitException {
		// sets HC with Log4j
		System.setProperty("hazelcast.logging.type", "log4j");
		// creates a new Client instance of Hazelcast
		client = createClient();

		// gets URL of JCL content, reads and loads it into Prejob object,
		// setting the JCL type
		URL urlJcl = null;
		try {
			urlJcl = new URL(getJcl());
		} catch (MalformedURLException ex) {
			// ignore
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			// if it's not a URL, try as a file
			File jclFile = new File(getJcl());
			try {
				urlJcl = jclFile.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new SubmitException(SubmitMessage.JEMW006E, e, getJcl());
			}
		}

		PreJob preJob;
		try {
			preJob = Factory.createPreJob(urlJcl);
		} catch (InstantiationException e) {
			throw new SubmitException(SubmitMessage.JEMW007E, e, getJcl());
		}
		preJob.setJclType(getType());

		// creates a job asking to Hazelcast for a new long value
		// Pads the value with "0"
		Job job = new Job();
		// sets user
		job.setUser(getUserID());
		job.setOrgUnit(getGroupID());

		IdGenerator generator = client.getIdGenerator(Queues.JOB_ID_GENERATOR);
		long id = generator.newId();

		String jobId = Factory.createJobId(job, id);
		job.setId(jobId);
		// loads all line arguments (the -D properties).
		// could be useful to factories, listeners and during job execution to
		// job itself
		job.setInputArguments(ManagementFactory.getRuntimeMXBean().getInputArguments());
		// set nowait
		job.setNowait(!isWait());
		// loads prejob with job
		preJob.setJob(job);
		
		super.setJob(job);

		// gets topic object and adds itself as listener
		ITopic<Job> topic = client.getTopic(Queues.ENDED_JOB_TOPIC);
		topic.addMessageListener(this);

		// puts the pre job in a queue for validating and miving to right QUEUE
		// (input if is correct, output if is wrong)
		IQueue<PreJob> jclCheckingQueue = client.getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			jclCheckingQueue.put(preJob);
		} catch (InterruptedException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#execute(java.lang.String[])
	 */
	@Override
	public SubmitResult execute(String[] args) {
		SubmitResult result =  super.execute(args);
		// shutdown hazelcast client
		if (client != null) {
			client.getLifecycleService().shutdown();
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#beforeJobSubmit()
	 */
	@Override
	public void beforeJobSubmit() throws SubmitException {
		super.beforeJobSubmit();
		Map<String, SubmitArgument> arguments = getArguments();
		if (arguments.containsKey(SubmitParameters.PASSWORD.getName())){
			SubmitArgument sa = arguments.get(SubmitParameters.PASSWORD.getName());
			setPassword(sa.getValue());
		}
		if (getPassword() == null){
			CmdConsole console = new CmdConsole();
			try {
				setPassword(console.readPassword("JEM"));
			} catch (IOException e) {
				throw new SubmitException(SubmitMessage.JEMW008E, e);
			}
		}
		
		// for remote submit, it overrides the default for WAIT! it uses TRUE
		// instead of FALSE
		if (arguments.containsKey(SubmitParameters.WAIT.getName())){
			SubmitArgument sakey = arguments.get(SubmitParameters.WAIT.getName());
			setWait(Parser.parseBoolean(sakey.getValue(), true));	
		} else {
			setWait(true);
		}
		
		if (arguments.containsKey(SubmitParameters.PRINT_OUTPUT.getName())){
			SubmitArgument sakey = arguments.get(SubmitParameters.PRINT_OUTPUT.getName());
			setPrintOutput(Parser.parseBoolean(sakey.getValue(), false));	
		} else {
			setPrintOutput(false);
		}

		// mismatch between WAIT and PRINTOUTPUT
		if (!isWait() && isPrintOutput()){
			LogAppl.getInstance().emit(NodeMessage.JEMC198W, SubmitParameters.WAIT.getName(), SubmitParameters.PRINT_OUTPUT.getName());
		}
		
		// Privates
		if (arguments.containsKey(SubmitParameters.PRIVATE_KEY.getName())){
			SubmitArgument sakey= arguments.get(SubmitParameters.PRIVATE_KEY.getName());
			setPrivateKey(sakey.getValue());
		}
		
		if (arguments.containsKey(SubmitParameters.PRIVATE_KEY_PWD.getName())){
			SubmitArgument sakey= arguments.get(SubmitParameters.PRIVATE_KEY_PWD.getName());
			setPrivateKeyPassword(sakey.getValue());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#afterJobSubmit()
	 */
	@Override
	public int afterJobSubmit() throws SubmitException {
		int rc = 0;
		if (isWait()){
			try {
				// waits for ending ot job execution
				lock.await();
				Job job = getJob();
				// checks if job is not null
				if (job != null) {
					// logs return code and exception if exists
					LogAppl.getInstance().emit(NodeMessage.JEMC021I, job.toString(), String.valueOf(job.getResult().getReturnCode()));
					if (job.getResult().getReturnCode() != Result.SUCCESS) {
						LogAppl.getInstance().emit(NodeMessage.JEMC021I, job.toString(), job.getResult().getExceptionMessage());
					}
				}
				// sets return code to exit
				if (job != null) {
					// sets return code to exit
					Result res = job.getResult();
					if (res != null) {
						rc = job.getResult().getReturnCode();
					} else {
						rc = 1;
					}
				} else {
					rc = 1;
				}
				// printOutput
				if (job != null && isPrintOutput()) {
					try {
						printOutput();
					} catch (Exception e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC017E, e);
						rc = 1;
					}
				}
			} catch (InterruptedException e1) {
				// sets return code to error
				LogAppl.getInstance().emit(NodeMessage.JEMC017E, e1);
				rc = 1;
			}
		}
		return rc;
	}

	/**
	 * Calls a executor on JEM cluster, by Hazelcast, to getjob output.
	 * @throws Exception
	 */
	private void printOutput() throws SubmitException {
		Cluster cluster = client.getCluster();
		Set<Member> set = cluster.getMembers();
		Member member = set.iterator().next();
		DistributedTask<OutputFileContent> task = new DistributedTask<OutputFileContent>(new GetMessagesLog(getJob()), member);
		ExecutorService executorService = client.getExecutorService();
		executorService.execute(task);
		OutputFileContent content;
		try {
			content = task.get();
			LogAppl.getInstance().emit(NodeMessage.JEMC246I, getJob().getName(), content.getContent());
		} catch (InterruptedException e) {
			throw new SubmitException(SubmitMessage.JEMW009E, e, getJob());
		} catch (ExecutionException e) {
			throw new SubmitException(SubmitMessage.JEMW009E, e, getJob());
		}
	}
}
