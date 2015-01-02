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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface StatisticsManagerServiceAsync {

	/**
	 * @param callback
	 */
	void getCurrentSample(AsyncCallback<LightSample> callback);

	/**
	 * @param callback
	 */
	void getSamples(AsyncCallback<Collection<LightSample>> callback);

	/**
	 * 
	 * @param resorceKey
	 * @param callback
	 */
	void displayRequestors(String resorceKey, AsyncCallback<String> callback);

	/**
	 * 
	 * @param callback
	 */
	void getAllRedoStatements(AsyncCallback<Collection<RedoStatement>> callback);

}