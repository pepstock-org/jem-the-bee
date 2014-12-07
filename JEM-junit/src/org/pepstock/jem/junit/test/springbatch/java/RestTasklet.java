/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.junit.test.springbatch.java;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RestTasklet implements Tasklet {


	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.pepstock.jem.node.tasks.jndi.JemContextFactory");

		InitialContext context = new InitialContext(env);
		RestClient rest = (RestClient)context.lookup("JUNIT-REST-RESOURCE");

		System.err.println();
		System.err.println("*** REST XML");
		get(rest, MediaType.APPLICATION_XML);
		get(rest, MediaType.APPLICATION_JSON);
		get(rest, MediaType.APPLICATION_XML);
		get(rest, MediaType.APPLICATION_JSON);
		return RepeatStatus.FINISHED;
	}

	private void get(RestClient rest, String media) throws IOException{
		WebResource resource = rest.getBaseWebResource();
		resource = resource.path(StatisticsManagerPaths.MAIN).path(StatisticsManagerPaths.ABOUT);
		ClientResponse response = resource.accept(media).get(ClientResponse.class);
		System.err.println(response.getStatus()+" "+response.getLength());
		System.err.println(IOUtils.toString(response.getEntityInputStream()));
	}
}
