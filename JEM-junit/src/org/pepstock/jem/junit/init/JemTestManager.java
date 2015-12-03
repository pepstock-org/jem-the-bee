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
package org.pepstock.jem.junit.init;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.commands.HttpSubmit;
import org.pepstock.jem.commands.LocalHostSubmit;
import org.pepstock.jem.commands.ProxySubmit;
import org.pepstock.jem.commands.Submit;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * Is the manager responsible of the submission of all the jobs inside the junit
 * test suite. Is a singleton and it uses a pool of threads for submit getting
 * the result of the job via future,
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class JemTestManager {

	private Submitter selectedSubmitter;

	private RestConf restConf;

	private Configuration configuration;

	private ExecutorService executor;

	private static JemTestManager jemSubmit;

	private static final int NTHREAD = 10;

	/**
	 * 
	 * @param configurationFile
	 * @throws Exception
	 */
	private JemTestManager(URL configurationUrl) throws Exception {
		File configurationFile = File.createTempFile("config", ".cfg");
		 FileUtils.copyURLToFile(configurationUrl, configurationFile);
		String xml = FileUtils.readFileToString(configurationFile);
		Configuration conf = Configuration.unmarshall(xml);
		this.configuration = conf;
		this.restConf = conf.getRestconf();
		for (Submitter submitter : conf.getSubmitters()) {
			if (submitter.getSelected() != null
					&& submitter.getSelected() == true) {
				if (submitter.getEmbedded() == null){
					submitter.setEmbedded(Boolean.TRUE);
				} else if (!submitter.getEmbedded() && System.getenv(ConfigKeys.JEM_HOME) == null){
					throw new JemException(ConfigKeys.JEM_HOME+" is missing!");
				}
				selectedSubmitter = submitter;
				if (!selectedSubmitter.getReferenceClass().equals(
						HttpSubmit.class)
						&& !selectedSubmitter.getReferenceClass().equals(
								ProxySubmit.class)
						&& !selectedSubmitter.getReferenceClass().equals(
								Submit.class)
						&& !selectedSubmitter.getReferenceClass().equals(
								LocalHostSubmit.class)) {
					throw new Exception("Class " + selectedSubmitter.getClass()
							+ " is not a valid submitter class");
				}
				break;
			}
		}
		executor = Executors.newFixedThreadPool(NTHREAD);
	}

	/**
	 * 
	 * @return the JemTestManager
	 * @throws Exception
	 */
	public static JemTestManager getSharedInstance() throws Exception {
		if (jemSubmit == null) {
			String config = System.getProperty(ConfigKeys.JEM_CONFIG);
			if (config != null){
				File file = new File(config);
				return new JemTestManager(file.toURI().toURL());	
			} else {
				return new JemTestManager(JemTestManager.class.getResource("Configuration.xml"));
			}
		}
		return jemSubmit;
	}

	/**
	 * use the selected submitter present in the configuration to submit the job
	 * in the configured environment with the spicified behavio
	 * 
	 * @param jcl
	 *            the jcl to submit
	 * @param type
	 *            the type (ant or sb)
	 * @param wait
	 *            true the submit will wait until the end of the execution false
	 *            it will not
	 * @param printout
	 *            true the submit will print the result of the job execution on
	 *            standard output false it will not
	 * @return
	 * @throws Exception
	 */
	public Future<SubmitResult> submit(String jcl, String type, boolean wait,
			boolean printout) {
		String newJcl = jcl;
		if (!selectedSubmitter.getEmbedded()){
			File temp;
            try {
	            temp = File.createTempFile(StringUtils.rightPad(type, 3, "0"), ".jcl");
	            URL url = new URL(jcl);
	            FileUtils.copyURLToFile(url, temp);
	            newJcl = temp.getAbsolutePath();
            } catch (MalformedURLException e) {
	            throw new RuntimeException(e);
            } catch (IOException e) {
	            throw new RuntimeException(e);
            }
		}
		Callable<SubmitResult> task = new Task(selectedSubmitter, newJcl, type,
				wait, printout);
		Future<SubmitResult> submitResult = executor.submit(task);
		return submitResult;
	}

	/**
	 * 
	 * @return the configuration object
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 
	 * @return the rest configuration object
	 */
	public RestConf getRestConf() {
		return restConf;
	}
}
