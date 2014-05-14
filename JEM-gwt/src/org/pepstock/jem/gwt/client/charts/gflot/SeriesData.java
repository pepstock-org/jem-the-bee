package org.pepstock.jem.gwt.client.charts.gflot;

import java.util.LinkedList;
import java.util.List;

public class SeriesData<X, Y> {

	private String label;
	private String color;
	private List<DataPoint<X, Y>> dataPoints = new LinkedList<DataPoint<X, Y>>();
	
	public SeriesData() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean hasColor() {
		return color != null;
	}
	
	public List<DataPoint<X, Y>> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<DataPoint<X, Y>> dataPoints) {
		this.dataPoints = dataPoints;
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
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeriesData<?, ?> other = (SeriesData<?, ?>) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (dataPoints == null) {
			if (other.dataPoints != null)
				return false;
		} else if (!dataPoints.equals(other.dataPoints))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
}
