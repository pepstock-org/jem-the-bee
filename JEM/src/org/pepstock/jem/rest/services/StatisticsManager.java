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
 * Client side of NODES service.
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @return
	 * @throws JemException
	 */
	public LightSample getCurrentSample() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.GET_CURRENT_SAMPLE);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getCurrentSample();
	}

	/**
	 * 
	 * @param resourceKey
	 * @return
	 * @throws JemException
	 */
	public String displayRequestors(String resourceKey) throws JemException {
		StatsPostService<StringReturnedObject, String> service = new StatsPostService<StringReturnedObject, String>(StatisticsManagerPaths.DISPLAY_REQUESTORS);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, resourceKey);
		return result.getValue();
	}

	/**
	 * 
	 * @return
	 * @throws JemException
	 */
	public Collection<RedoStatement> getAllRedoStatements() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.GET_ALL_REDO_STATEMENTS);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getRedoStatements();
	}

	/**
	 * 
	 * @return
	 * @throws JemException
	 */
	public About getAbout() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.ABOUT);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getAbout();
	}
	
	/**
	 * 
	 * @return
	 * @throws JemException
	 */
	public String[] getEnvironmentInformation() throws JemException {
		StatsGetService service = new StatsGetService(StatisticsManagerPaths.INFOS);
		GenericType<JAXBElement<Stats>> generic = new GenericType<JAXBElement<Stats>>() {

		};
		Stats result = service.execute(generic, null);
		return result.getInfos();
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class StatsGetService extends DefaultGetService<Stats, String> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public StatsGetService(String subService) {
			super(StatisticsManager.this.getClient(), StatisticsManagerPaths.MAIN, subService);
		}

	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class StatsPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public StatsPostService(String subService) {
			super(StatisticsManager.this.getClient(), StatisticsManagerPaths.MAIN, subService);
		}

	}

}