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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.gwt.server.services.StatisticsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.entities.Stats;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;

/**
 * REST services published in the web part, to manage statistics and administration stuff.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Path(StatisticsManagerPaths.MAIN)
public class StatisticsManagerImpl extends DefaultServerResource {

	private StatisticsManager statisticsManager = null;

	/**
	 * REST service which returns list of collected sample in the JEM cluster
	 * 
	 * @return a list of statistics from all nodes
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@GET
	@Path(StatisticsManagerPaths.GET_SAMPLES)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getSamples() throws JemException {
		Stats result = new Stats();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setSamples(statisticsManager.getSamples());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * REST service which last sample with all statistics on JEM nodes
	 * 
	 * @return last statistics sample
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@GET
	@Path(StatisticsManagerPaths.GET_CURRENT_SAMPLE)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getCurrentSample() throws JemException {
		Stats result = new Stats();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setCurrentSample(statisticsManager.getCurrentSample());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * REST service which list of requestors in GRS node when configured
	 * 
	 * @param resourceKey
	 *            wild card of resources name
	 * 
	 * @return list of requestors
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@POST
	@Path(StatisticsManagerPaths.DISPLAY_REQUESTORS)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public StringReturnedObject displayRequestors(String resourceKey) throws JemException {
		StringReturnedObject result = new StringReturnedObject();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setValue(statisticsManager.displayRequestors(resourceKey));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * REST service which list of REDo statements which are waiting of DB will be restarted
	 * 
	 * @return list of REDO statementes
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@GET
	@Path(StatisticsManagerPaths.GET_ALL_REDO_STATEMENTS)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getAllRedoStatements() throws JemException {
		Stats result = new Stats();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setRedoStatements(statisticsManager.getAllRedoStatements());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * REST service which returns the JEM "about", with licenses, versions, etc.
	 * 
	 * @return JEM "about", with licenses, versions, etc
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@GET
	@Path(StatisticsManagerPaths.ABOUT)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getAbout() throws JemException {
		Stats result = new Stats();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setAbout(statisticsManager.getAbout());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * REST service which returns a list of information about JEM environment
	 * 
	 * @return a list of information about JEM environment
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@GET
	@Path(StatisticsManagerPaths.INFOS)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getEnvironmentInformation() throws JemException {
		Stats result = new Stats();
		if (isEnable()) {
			if (statisticsManager == null) {
				initManager();
			}
			try {
				result.setInfos(statisticsManager.getEnvironmentInformation());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * Initialize the manager
	 */
	private synchronized void initManager() {
		if (statisticsManager == null) {
			statisticsManager = new StatisticsManager();
		}
	}
}
