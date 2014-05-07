package org.pepstock.jem.gwt.client.charts.gflot;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.HasSizes;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.PieDataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Background;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Formatter;
import com.googlecode.gflot.client.options.PlotOptions;

public class GPieChart implements IsWidget, HasSizes {

	public static int DEFAULT_WIDTH = 300;
	public static int DEFAULT_HEIGHT = 300;
	
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	
	private final PlotModel model = new PlotModel();
	private final PlotOptions options = PlotOptions.create();
	private SimplePlot plot = null;
	
	private double pieRadious = 1;
	private double pieInnerRadious = 0.2;
	
	private boolean showLegend = true;
	
	private boolean showLabel = true;
	private double labelRadious = 0.5;
	private double labelThreshold = 0.05;
	private Formatter labelFormatter = new DefaultLabelFormatter();
	private double labelBackgroundOpacity = 0.8;
	
	private List<DataPoint<String, Double>> data = new LinkedList<DataPoint<String, Double>>();
	
	public GPieChart() {
	}

	public GPieChart(List<DataPoint<String, Double>> data) {
		setData(data);
	}

	public GPieChart(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public GPieChart(List<DataPoint<String, Double>> data, int width, int height) {
		setData(data);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Widget asWidget() {
		applyOptions();
		plot = new SimplePlot(model, options);
		applySizes();
		return plot;
	}

	protected void applyOptions() {
		// activate the pie
		options.setGlobalSeriesOptions(GlobalSeriesOptions.create().setPieSeriesOptions(
			PieSeriesOptions.create().setShow(true).setRadius(pieRadious).setInnerRadius(pieInnerRadious).setLabel(
				Label.create().setShow(showLabel).setRadius(labelRadious).setBackground(
						Background.create().setOpacity(labelBackgroundOpacity)).setThreshold(labelThreshold).setFormatter(labelFormatter)
					)));
		options.setLegendOptions(LegendOptions.create().setShow(showLegend));
		//options.setGridOptions(GridOptions.create().setHoverable(true));
	}
	
	protected void applySizes() {
		if (plot != null) {
			plot.setWidth(getWidth());
			plot.setHeight(getHeight());
		}
	}
	
	public List<DataPoint<String, Double>> getData() {
		return data;
	}

	public void setData(List<DataPoint<String, Double>> data) {
		// save the data
		this.data = data;
		// reset model
		model.removeAllSeries();
		// create model
		for (DataPoint<String, Double> dataPoint : data) {
			SeriesHandler seriesHandler;// 
			if (dataPoint.hasColor()) {
				seriesHandler = model.addSeries(Series.of(dataPoint.getX(), dataPoint.getColor()));
			} else {
				seriesHandler = model.addSeries(Series.of(dataPoint.getX()));
			}
			seriesHandler.add(PieDataPoint.of(dataPoint.getY()));
		}
	}
	
	public double getPieRadious() {
		return pieRadious;
	}

	public void setPieRadious(double pieRadious) {
		this.pieRadious = pieRadious;
	}

	public double getPieInnerRadious() {
		return pieInnerRadious;
	}

	public void setPieInnerRadious(double pieInnerRadious) {
		this.pieInnerRadious = pieInnerRadious;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public double getLabelRadious() {
		return labelRadious;
	}

	public void setLabelRadious(double labelRadious) {
		this.labelRadious = labelRadious;
	}

	public double getLabelThreshold() {
		return labelThreshold;
	}

	public void setLabelThreshold(double labelThreshold) {
		this.labelThreshold = labelThreshold;
	}

	public Formatter getLabelFormatter() {
		return labelFormatter;
	}

	public void setLabelFormatter(Formatter labelFormatter) {
		this.labelFormatter = labelFormatter;
	}

	public double getLabelBackgroundOpacity() {
		return labelBackgroundOpacity;
	}

	public void setLabelBackgroundOpacity(double labelBackgroundOpacity) {
		this.labelBackgroundOpacity = labelBackgroundOpacity;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		applySizes();
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
		applySizes();
	}

	/**
	 * Default pie label formatter, that is 'value (percent%)'  
	 * @author Marco "Fuzzo" Cuccato
	 */
	private static class DefaultLabelFormatter implements Formatter {

		private static NumberFormat FORMAT = NumberFormat.getFormat("#0.#");
		
		@Override
		public String format(String label, Series series) {
			return FORMAT.format(series.getData().getY(0)) + " (" + FORMAT.format(series.getPercent()) + "%)";
		}
		
	}

}
