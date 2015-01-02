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
package org.pepstock.jem.junit.test.http.java;


import java.io.InputStream;
import java.io.StringWriter;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.node.resources.impl.http.HttpResourceKeys;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * This class is a spring batch tasklet that will consume a HTTP connection.
 * 
 * @author Alessandro
 *
 */
public class HttpConsumeSbTasklet extends JemTasklet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework
	 * .batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution,
			ChunkContext chuckContext) throws TaskletException {
		
		try {
			Hashtable<String, String> env = this.createEnvironment();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.pepstock.jem.node.tasks.jndi.JemContextFactory");
			InitialContext context = new InitialContext(env);
			// get http resource, note that jem-http is the name of the resource
			// present in the JCL
			InputStream httpStream = (InputStream) context.lookup("jem-http");
	        StringWriter writer = new StringWriter();
	        IOUtils.copy(httpStream, writer);
	        String theString = writer.toString();
	        System.out.println("Read Http: " + theString);
	        httpStream.close();
		} catch (Exception e) {
			throw new TaskletException(e);
		}
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Create the environment with the properties
	 * useful to use Http Resource.
	 * 
	 * @return the environment with the useful properties
	 */
	private  Hashtable<String, String> createEnvironment(){
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(HttpResourceKeys.REQUEST_PATH, "/search");
		environment.put(HttpResourceKeys.REQUEST_QUERY_STRING, "q=pepstock");
		return environment;
	}

}
