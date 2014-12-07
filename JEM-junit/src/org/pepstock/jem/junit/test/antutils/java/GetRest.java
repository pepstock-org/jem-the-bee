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
package org.pepstock.jem.junit.test.antutils.java;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.annotations.AssignDataSource;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class will show an example of how to use a JEM datasource
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class GetRest {

	@AssignDataSource("JUNIT-REST-RESOURCE")
	private static RestClient rest = null;
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.err.println();
		System.err.println("*** REST XML");
		get(rest, MediaType.APPLICATION_XML);
		get(rest, MediaType.APPLICATION_JSON);
		get(rest, MediaType.APPLICATION_XML);
		get(rest, MediaType.APPLICATION_JSON);
	}
	
	private static void get(RestClient rest, String media) throws IOException{
		WebResource resource = rest.getBaseWebResource();
		resource = resource.path(StatisticsManagerPaths.MAIN).path(StatisticsManagerPaths.ABOUT);
		ClientResponse response = resource.accept(media).get(ClientResponse.class);
		System.err.println(response.getStatus()+" "+response.getLength());
		System.err.println(IOUtils.toString(response.getEntityInputStream()));
	}
}
