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
package org.pepstock.jem.gwt.server.rest;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.gwt.server.services.StatisticsManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.paths.CommonPaths;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * REST services published in the web part, to manage statistics and
 * administration stuff.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Singleton
@Path(StatisticsManagerPaths.MAIN)
public class StatisticsManagerImpl extends DefaultServerResource {

	private StatisticsManager statisticsManager = null;

	/**
	 * REST service which returns list of collected sample in the JEM cluster
	 * 
	 * @return a list of statistics from all nodes
	 */
	@GET
	@Path(StatisticsManagerPaths.SAMPLES)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSamples() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns samples
				return ResponseBuilder.JSON.ok(statisticsManager.getSamples());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which last sample with all statistics on JEM nodes
	 * 
	 * @return last statistics sample
	 */
	@GET
	@Path(StatisticsManagerPaths.CURRENT_SAMPLE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentSample() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns sample
				return ResponseBuilder.JSON.ok(statisticsManager.getCurrentSample());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which list of requestors in GRS node when configured
	 * 
	 * @param resourceKey
	 *            wild card of resources name
	 * 
	 * @return list of requestors
	 */
	@GET
	@Path(StatisticsManagerPaths.DISPLAY_REQUESTORS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response displayRequestors(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String resourceKey) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns requestors
				return ResponseBuilder.PLAIN.ok(statisticsManager.displayRequestors(resourceKey));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which list of REDo statements which are waiting of DB will
	 * be restarted
	 * 
	 * @return list of REDO statementes
	 */
	@GET
	@Path(StatisticsManagerPaths.REDO_STATEMENTS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRedoStatements() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns redo statements
				return ResponseBuilder.JSON.ok(statisticsManager.getAllRedoStatements());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns the JEM "about", with licenses, versions, etc.
	 * 
	 * @return JEM "about", with licenses, versions, etc
	 */
	@GET
	@Path(StatisticsManagerPaths.ABOUT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAbout() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns about
				return ResponseBuilder.JSON.ok(statisticsManager.getAbout());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns a list of information about JEM environment
	 * 
	 * @return a list of information about JEM environment
	 */
	@GET
	@Path(StatisticsManagerPaths.INFOS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironmentInformation() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns env info
				return ResponseBuilder.JSON.ok(Arrays.asList(statisticsManager.getEnvironmentInformation()));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#init()
	 */
	@Override
	boolean init() throws Exception {
		if (statisticsManager == null) {
			statisticsManager = new StatisticsManager();
		}
		return true;
	}
}
