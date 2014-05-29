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

import com.googlecode.gflot.client.options.AxisOptions;
import com.googlecode.gflot.client.options.TickFormatter;

/**
 * Base class for grid-based charts.
 * @author Marco "Fuzzo" Cuccato
 *
 */
public abstract class AbstractGridBasedChart extends AbstractChart<Double, Double> {

	private Long minX, maxX, minXTickSize;
	private Long minY, maxY, minYTickSize;

	private Double tickSizeX, tickSizeY;
	private Double tickDecimalsX, tickDecimalsY;
	
	private String labelX, labelY;
	
	private TickFormatter tickFormatterX, tickFormatterY; 
	
	/**
	 * Build an empty GridBasedChart widget
	 */
	public AbstractGridBasedChart() {
		super();
	}

	/**
	 * Build a GidBasedChart widget
	 * @param width the widget width
	 * @param height the widget height
	 */
	public AbstractGridBasedChart(int width, int height) {
		super(width, height);
	}

	/**
	 * Apply common chart axis options, this method is intended to be called in a applyOptions() subclass implementation
	 */
	protected void applyAxisOptions() {
		// axis X options
		AxisOptions optionsX = AxisOptions.create();
		if (hasMinXTickSize()) {
			optionsX.setMinTickSize(getMinXTickSize());
		}
		if (hasMinX()) {
			optionsX.setMinimum(getMinX());
		}
		if (hasMaxX()) {
			optionsX.setMaximum(getMaxX());
		}
		if (hasLabelX()) {
			optionsX.setLabel(getLabelX());
		}
		if (hasTickFormatterX()) {
			optionsX.setTickFormatter(getTickFormatterX());
		}
		if (hasTickDecimalsX()) {
			optionsX.setTickDecimals(getTickDecimalsX());
		}
		if (hasTickSizeX()) {
			optionsX.setTickSize(getTickSizeX());
		}
		getOptions().addXAxisOptions(optionsX);
		
		// axis Y options
		AxisOptions optionsY = AxisOptions.create();
		if (hasMinYTickSize()) {
			optionsY.setMinTickSize(getMinYTickSize());
		}
		if (hasMinY()) {
			optionsY.setMinimum(getMinY());
		}
		if (hasMaxY()) {
			optionsY.setMaximum(getMaxY());
		}
		if (hasLabelY()) {
			optionsY.setLabel(getLabelY());
		}
		if (hasTickFormatterY()) {
			optionsY.setTickFormatter(getTickFormatterY());
		}
		if (hasTickDecimalsY()) {
			optionsY.setTickDecimals(getTickDecimalsY());
		}
		if (hasTickSizeY()) {
			optionsY.setTickSize(getTickSizeY());
		}
		getOptions().addYAxisOptions(optionsY);
	}
	
	/**
	 * @return the minimum X value
	 */
	public Long getMinX() {
		return minX;
	}

	/**
	 * @param minX the minimum X value
	 */
	public void setMinX(Long minX) {
		this.minX = minX;
	}

	/**
	 * @return <code>true</code> if the value <i>minX</i> is specified, <code>false</code> if not 
	 */
	public boolean hasMinX() {
		return minX != null;
	}
	
	/**
	 * @return the maximum X value
	 */
	public Long getMaxX() {
		return maxX;
	}

	/**
	 * Set the max value of X axis. <b>Note:</b> setting this value prevents automatic axis scale redrawing
	 * @param maxX the maximum X value
	 */
	public void setMaxX(Long maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return <code>true</code> if the value <i>maxX</i> is specified, <code>false</code> if not
	 */
	public boolean hasMaxX() {
		return maxX != null;
	}
	
	/**
	 * @return the minimum X axis scale step
	 */
	public Long getMinXTickSize() {
		return minXTickSize;
	}

	/**
	 * @param minXTickSize the minimum X axis scale step
	 */
	public void setMinXTickSize(Long minXTickSize) {
		this.minXTickSize = minXTickSize;
	}

	/**
	 * @return <code>true</code> if the value <i>minXTickSize</i> is specified, <code>false</code> if not
	 */
	public boolean hasMinXTickSize() {
		return minXTickSize != null;
	}
	
	/**
	 * @return the minimum Y value
	 */
	public Long getMinY() {
		return minY;
	}

	/**
	 * @param minY the minimum Y value
	 */
	public void setMinY(Long minY) {
		this.minY = minY;
	}

	/**
	 * @return <code>true</code> if the value <i>minY</i> is specified, <code>false</code> if not
	 */
	public boolean hasMinY() {
		return minY != null;
	}
	
	/**
	 * @return the maximum Y value
	 */
	public Long getMaxY() {
		return maxY;
	}

	/**
	 * Set the max value of Y axis. <b>Note:</b> setting this value prevents automatic axis scale redrawing
	 * @param maxY  the maximum Y value
	 */
	public void setMaxY(Long maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return <code>true</code> if the value <i>maxY</i> is specified, <code>false</code> if not
	 */
	public boolean hasMaxY() {
		return maxY != null;
	}
	
	/**
	 * @return the minimum Y axis scale step
	 */
	public Long getMinYTickSize() {
		return minYTickSize;
	}

	/**
	 * @param minYTickSize the minimum Y axis scale step
	 */
	public void setMinYTickSize(Long minYTickSize) {
		this.minYTickSize = minYTickSize;
	}

	/**
	 * @return <code>true</code> if the value <i>minYTickSize</i> is specified, <code>false</code> if not
	 */
	public boolean hasMinYTickSize() {
		return minYTickSize != null;
	}
	
	/**
	 * @return the X axis label
	 */
	public String getLabelX() {
		return labelX;
	}

	/**
	 * @param labelX the X axis label
	 */
	public void setLabelX(String labelX) {
		this.labelX = labelX;
	}

	/**
	 * @return <code>true</code> if the value <i>labelX</i> is specified, <code>false</code> if not
	 */
	public boolean hasLabelX() {
		return labelX != null;
	}
	
	/**
	 * @return the Y axis label
	 */
	public String getLabelY() {
		return labelY;
	}

	/**
	 * @param labelY the Y axis value
	 */
	public void setLabelY(String labelY) {
		this.labelY = labelY;
	}

	/**
	 * @return <code>true</code> if the value <i>labelY</i> is specified, <code>false</code> if not
	 */
	public boolean hasLabelY() {
		return labelY != null;
	}
	
	/**
	 * @return the X axis {@link TickFormatter}
	 */
	public TickFormatter getTickFormatterX() {
		return tickFormatterX;
	}

	/**
	 * Set the X axis {@link TickFormatter}. This class will be used as renderer of X axis ticks
	 * @param tickFormatterX
	 */
	public void setTickFormatterX(TickFormatter tickFormatterX) {
		this.tickFormatterX = tickFormatterX;
	}

	/**
	 * @return <code>true</code> if a {@link TickFormatter} is provided, <code>false</code> otherwhise
	 */
	public boolean hasTickFormatterX() {
		return tickFormatterX != null;
	}
	
	/**
	 * @return the Y axis {@link TickFormatter}
	 */
	public TickFormatter getTickFormatterY() {
		return tickFormatterY;
	}

	/**
	 * Set the Y axis {@link TickFormatter}. This class will be used as renderer of Y axis ticks
	 * @param tickFormatterY
	 */
	public void setTickFormatterY(TickFormatter tickFormatterY) {
		this.tickFormatterY = tickFormatterY;
	}

	/**
	 * @return <code>true</code> if a {@link TickFormatter} is provided, <code>false</code> otherwhise
	 */
	public boolean hasTickFormatterY() {
		return tickFormatterY != null;
	}
	
	/**
	 * @return the number of decimals rendered in X axis ticks
	 */
	public double getTickDecimalsX() {
		return tickDecimalsX;
	}

	/**
	 * @param tickDecimalsX the number of decimals rendered in X axis ticks. If a {@link TickFormatter} is provided, this is ignored.
	 */
	public void setTickDecimalsX(double tickDecimalsX) {
		this.tickDecimalsX = tickDecimalsX;
	}

	/**
	 * @return <code>true</code> if the value <i>tickDecimalsX</i> is specified, <code>false</code> if not
	 */
	public boolean hasTickDecimalsX() {
		return tickDecimalsX != null;
	}
	
	/**
	 * @return the number of decimals rendered in Y axis ticks
	 */
	public double getTickDecimalsY() {
		return tickDecimalsY;
	}

	/**
	 * @param tickDecimalsY the number of decimals rendered in Y axis ticks. If a {@link TickFormatter} is provided, this is ignored.
	 */
	public void setTickDecimalsY(double tickDecimalsY) {
		this.tickDecimalsY = tickDecimalsY;
	}

	/**
	 * @return <code>true</code> if the value <i>tickDecimalsY</i> is specified, <code>false</code> if not
	 */
	public boolean hasTickDecimalsY() {
		return tickDecimalsY != null;
	}
	
	/**
	 * @return the tick interval size for X axis. If you set it to 2, you'll get ticks at 2, 4, 6, etc.
	 */
	public Double getTickSizeX() {
		return tickSizeX;
	}

	/**
	 * @param tickSizeX set the tick interval size for X axis. If you set it to 2, you'll get ticks at 2, 4, 6, etc.
	 */
	public void setTickSizeX(Double tickSizeX) {
		this.tickSizeX = tickSizeX;
	}

	/**
	 * @return <code>true</code> if the value <i>tickSizeX</i> is specified, <code>false</code> if not
	 */
	public boolean hasTickSizeX() {
		return tickSizeX != null;
	}

	/**
	 * @return the tick interval size for Y axis. If you set it to 2, you'll get ticks at 2, 4, 6, etc.
	 */
	public Double getTickSizeY() {
		return tickSizeY;
	}

	/**
	 * @param tickSizeY set the tick interval size for Y axis. If you set it to 2, you'll get ticks at 2, 4, 6, etc.
	 */
	public void setTickSizeY(Double tickSizeY) {
		this.tickSizeY = tickSizeY;
	}

	/**
	 * @return <code>true</code> if the value <i>tickSizeY</i> is specified, <code>false</code> if not
	 */
	public boolean hasTickSizeY() {
		return tickSizeY != null;
	}
	
	
}
