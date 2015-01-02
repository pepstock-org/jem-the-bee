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
package org.pepstock.jem.rest.services;

import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.entities.Stats;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;

import com.sun.jersey.api.client.GenericType;

/**
 * REST Client side of STATISTICS or ADMINISTRATION services.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StatisticsManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public StatisticsManager(RestClient restClient) {
		super(restClient);
	}

	/**
	 * Returns the collection of all active samples in JEM. 
	 * 
     * @return collection of samples
	 * @throws JemException if any exception occurs
	 */
	public Collection<LightSample> getSamples() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.GET_SAMPLES);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getSamples();
	}

	/**
	 * Calculates and returns last sample of JEM statistics
	 * 
     * @return last sample of statistics
	 * @throws JemException if any exception occurs
	 */
	public LightSample getCurrentSample() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.GET_CURRENT_SAMPLE);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getCurrentSample();
	}

	/**
	 * Returns a formatted string with all the contentions on resources.<br>
	 * If GRS is not activated, return that GRS is not activated!
	 * 
     * @param resourceKey resource pattern to check 
     * @return a formatted string with all contentions information
	 * @throws JemException if any exception occurs
	 */
	public String displayRequestors(String resourceKey) throws JemException {
		StatsPostService<StringReturnedObject, String> service = new StatsPostService<StringReturnedObject, String>(StatisticsManagerPaths.DISPLAY_REQUESTORS);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, resourceKey);
		return result.getValue();
	}

	/**
	 * Gets a collection of REDO statement if the cluster is waiting to store and persist objects.<br>
	 * It happens when the database to persist is not reachable.
	 * 
     * @return collection of REDO statements
	 * @throws JemException if any exception occurs
	 */
	public Collection<RedoStatement> getAllRedoStatements() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.GET_ALL_REDO_STATEMENTS);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getRedoStatements();
	}

	/**
     * Returns a about object with information about version, creation and licenses
     * @return about instance
	 * @throws JemException if any exception occurs
	 */
	public About getAbout() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.ABOUT);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getAbout();
	}
	
	/**
     * Returns an array of system information
     * @return an array of system information
	 * @throws JemException if any exception occurs
	 */
	public String[] getEnvironmentInformation() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.INFOS);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getInfos();
	}

	/**
	 * Inner service, which extends post the default get service.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class StatsGetService extends DefaultGetService<Stats, String> {

		/**
		 * Constructs the REST service, using HTTP client and service and subservice paths, passed as argument
		 * 
		 * @param subService subservice path
		 * 
		 */
		public StatsGetService(String subService) {
			super(StatisticsManager.this.getClient(), StatisticsManagerPaths.MAIN, subService);
		}

	}

	/**
	 * Inner service, which extends post the default post service.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class StatsPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * Constructs the REST service, using HTTP client and service and subservice paths, passed as argument
		 * 
		 * @param subService subservice path
		 * 
		 */
		public StatsPostService(String subService) {
			super(StatisticsManager.this.getClient(), StatisticsManagerPaths.MAIN, subService);
		}

	}

}