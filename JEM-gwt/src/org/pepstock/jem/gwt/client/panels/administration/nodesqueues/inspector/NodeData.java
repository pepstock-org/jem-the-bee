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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues.inspector;


/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodeData {
	
	private String time;
	
	private long input = 0;
	
	private long running = 0;
	
	private long output = 0;

	private long routing = 0;

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the input
	 */
	public long getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(long input) {
		this.input = input;
	}

	/**
	 * @return the running
	 */
	public long getRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(long running) {
		this.running = running;
	}

	/**
	 * @return the output
	 */
	public long getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(long output) {
		this.output = output;
	}

	/**
	 * @return the routing
	 */
	public long getRouting() {
		return routing;
	}

	/**
	 * @param routing the routing to set
	 */
	public void setRouting(long routing) {
		this.routing = routing;
	}

}