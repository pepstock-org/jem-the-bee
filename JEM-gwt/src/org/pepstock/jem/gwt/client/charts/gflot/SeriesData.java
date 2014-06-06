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
package org.pepstock.jem.gwt.client.charts.gflot;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.ColorsHex;

/**
 * Hold a chart series information
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <X> the type of X {@link DataPoint}s
 * @param <Y> the type of Y {@link DataPoint}s
 */
public class SeriesData<X, Y> {

	private String label;
	private String color;
	private List<DataPoint<X, Y>> dataPoints = new LinkedList<DataPoint<X, Y>>();
	private Boolean fill;
	
	/**
	 * 
	 */
	public SeriesData() {
	}

	/**
	 * @return the series label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the series label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the series color (see {@link ColorsHex})
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the series color (see {@link ColorsHex})
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return <code>true</code> if the series has a custom color specified, <code>false</code> otherwhise (a default color will be used)
	 */
	public boolean hasColor() {
		return color != null;
	}
	
	/**
	 * @return the {@link DataPoint} list
	 */
	public List<DataPoint<X, Y>> getDataPoints() {
		return dataPoints;
	}

	/**
	 * @param dataPoints the {@link DataPoint} list
	 */
	public void setDataPoints(List<DataPoint<X, Y>> dataPoints) {
		this.dataPoints = dataPoints;
	}

	/**
	 * @return <code>true</code> if the area have to be filled, <code>false</code> otherwhise (only applicable for {@link LineChart})
	 */
	public Boolean getFill() {
		return fill;
	}

	/**
	 * @param fill set it to <code>true</code> if you want the area filled, <code>false</code> otherwhise (only applicable for {@link LineChart})
	 */
	public void setFill(Boolean fill) {
		this.fill = fill;
	}

	@Override
	public String toString() {
		return "SeriesData [label=" + label + ", color=" + color
				+ ", dataPoints=" + dataPoints + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result
				+ ((dataPoints == null) ? 0 : dataPoints.hashCode());
		result = prime * result + ((fill == null) ? 0 : fill.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SeriesData<?, ?> other = (SeriesData<?, ?>) obj;
		if (color == null) {
			if (other.color != null) {
				return false;
			}
		} else if (!color.equals(other.color)) {
			return false;
		}
		if (dataPoints == null) {
			if (other.dataPoints != null) {
				return false;
			}
		} else if (!dataPoints.equals(other.dataPoints)) {
			return false;
		}
		if (fill == null) {
			if (other.fill != null) {
				return false;
			}
		} else if (!fill.equals(other.fill)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}
	
}
