package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricCalculator;
import cz.vutbr.fit.dashapp.eval.metric.MetricResultsCollection;
import cz.vutbr.fit.dashapp.eval.metric.widget.IWidgetMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.Area;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.WidgetCount;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoCohesion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEconomy;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoHomogenity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoProportion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRegularity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSimplicity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance.NgoIntensityBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance.NgoColorfulnessBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance.NgoHSBBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance.NgoHSBBalance2;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance.NgoHSBBalance4;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyIntensityBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyColorfulnessBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyHSBBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyHSBBalance2;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.MyRasterBalance.MyHSBBalance4;
import cz.vutbr.fit.dashapp.image.ColorMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.VirtualDashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

public class WidgetMetricAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Widget Metric Analysis";
	private static final String FILE = "_widget_stats";
	
	private enum DahsboardsInput {
		ALL, THRESHOLD, REF
	}
	
	private static DahsboardsInput dashboard_source = DahsboardsInput.ALL;
	private static boolean crop = true;
	private static boolean filtered = false;
	private static boolean stdev = true;
	private static boolean cacheImages = true;
	
	
	IWidgetMetric[] metrics = new IWidgetMetric[] { 
			//new WidgetCount(),
			//new Area(),
			new NgoBalance(),
			new NgoIntensityBalance(),
			new NgoColorfulnessBalance(),
			new NgoHSBBalance(),
			new NgoHSBBalance2(),
			new NgoHSBBalance4(),
			new MyBalance(),
			new MyIntensityBalance(),
			new MyColorfulnessBalance(),
			new MyHSBBalance(),
			new MyHSBBalance2(),
			new MyHSBBalance4(),
			/*new NgoCohesion(),
			new NgoDensity(),
			new NgoEconomy(),
			new NgoEquilibrium(),
			new NgoHomogenity(),
			new NgoProportion(),
			new NgoRegularity(),
			new NgoSimplicity(),
			new NgoSequence(),
			new NgoSymmetry(),
			new NgoUnity(),*/
	};
	
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValues;
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValuesFiltered;
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValuesCrop;
	
	public WidgetMetricAnalysis() {
		meanValues = new LinkedHashMap<>();
		meanValuesFiltered = new LinkedHashMap<>();
		meanValuesCrop = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		
		DashboardCollection analyzedDashboardCollection = getDashboardCollection(actWorkspaceFolder, actDashboards);
		if(cacheImages) {
			cacheImages(analyzedDashboardCollection);
		}
		
		meanValues.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, analyzedDashboardCollection));
		if(cacheImages) {
			// restore dashboard collection
			clearCache(analyzedDashboardCollection);
			analyzedDashboardCollection = getDashboardCollection(actWorkspaceFolder, actDashboards);
		}
		
		// crop by rectangle
		if(crop || filtered) {
			DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(
					actWorkspaceFolder.getChildren(
							DashboardFile.class, actWorkspaceFolder.getFileName().substring(1) + "-crop", false
					));
			if(cropDashboard.length == 1) {
				Dashboard dashboard = cropDashboard.dashboards[0];
				Rectangle cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
				
				// get image and store it in virtual dashboard file which will be used by used by dashboard samples
				IDashboardFile dashboardFile = dashboard.getDashboardFile();
				VirtualDashboardFile vdfCrop = new VirtualDashboardFile(dashboardFile.getModel());
				VirtualDashboardFile vdfFiltered = new VirtualDashboardFile(dashboardFile.getModel());
				BufferedImage image = dashboardFile.getImage();
				if(filtered) {
					vdfFiltered.setImage(image);
				}
				if(crop) {
					image = ColorMatrix.printMatrixToImage(null, MatrixUtils.printBufferedImage(image, dashboard));
					vdfCrop.setImage(image);
				}
				
				Dashboard[] dashboards = analyzedDashboardCollection.dashboards;
				List<Dashboard> cropDashboards = new LinkedList<>();
				List<Dashboard> filteredDashboards = new LinkedList<>();
				Dashboard dashboardCopy;
				for (int i = 0; i < dashboards.length; i++) {
					if(crop) {
						dashboardCopy = dashboards[i].copy(cropRectangle, 2);
						dashboardCopy.setDashboardFile(vdfCrop);
						cropDashboards.add(dashboardCopy);
					}
					if(filtered) {
						dashboardCopy = dashboards[i].filter(cropRectangle, 2);
						dashboardCopy.setDashboardFile(vdfFiltered);
						filteredDashboards.add(dashboardCopy);
					}
				}
				if(crop) {
					meanValuesCrop.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, new DashboardCollection(cropDashboards)));
					vdfCrop.clearCache();
					System.gc();
				}
				if(filtered) {
					meanValuesFiltered.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, new DashboardCollection(filteredDashboards)));
					vdfFiltered.clearCache();
				}
			}
		}
	}

	private void clearCache(DashboardCollection analyzedDashboardCollection) {
		Dashboard[] dashboards = analyzedDashboardCollection.dashboards;
		if(dashboards.length > 0) {
			IDashboardFile dashboardFile = dashboards[0].getDashboardFile();
			dashboardFile.clearCache();
			for (int i = 0; i < dashboards.length; i++) {
				analyzedDashboardCollection.dashboards[i] = null;
			}
		}
		System.gc();
	}

	private void cacheImages(DashboardCollection analyzedDashboardCollection) {
		Dashboard[] dashboards = analyzedDashboardCollection.dashboards;
		if(dashboards.length > 0) {
			IDashboardFile dashboardFile = dashboards[0].getDashboardFile();
			VirtualDashboardFile vdf = new VirtualDashboardFile(dashboardFile.getModel());
			vdf.setImage(dashboardFile.getImage());
			for (int j = 0; j < dashboards.length; j++) {
				Dashboard dashboardCopy = dashboards[j].copy();
				dashboardCopy.setDashboardFile(vdf);
				analyzedDashboardCollection.dashboards[j] = dashboardCopy;
			}
		}
	}

	private DashboardCollection getDashboardCollection(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		if (dashboard_source == DahsboardsInput.THRESHOLD) {
			DashboardCollection widgetDashboard = DashAppUtils.makeDashboardCollection(
					actWorkspaceFolder.getChildren(
							DashboardFile.class, "_widget_tb", false
					));
			if(widgetDashboard.dashboards != null && widgetDashboard.dashboards.length > 0) {
				List<DashboardFile> ref = actWorkspaceFolder.getChildren(
						DashboardFile.class, actWorkspaceFolder.getFileName().substring(1), false
				);
				if(!ref.isEmpty()) {
					DashboardFile refImgFile = ref.get(0);
					VirtualDashboardFile vdf = new VirtualDashboardFile(refImgFile.getModel());
					vdf.setImage(refImgFile.getImage());
					for (int i = 0; i < widgetDashboard.dashboards.length; i++) {
						(widgetDashboard.dashboards[i] = widgetDashboard.dashboards[i].copy()).setDashboardFile(vdf);;
					}
				}
			}
			return widgetDashboard;
		} else if (dashboard_source == DahsboardsInput.REF) {
			DashboardCollection refDashboard = DashAppUtils.makeDashboardCollection(
					actWorkspaceFolder.getChildren(
							DashboardFile.class, actWorkspaceFolder.getFileName().substring(1), false
					));
			return refDashboard;
		} else {
			return new DashboardCollection(actDashboards.dashboards);
		}
	}

	private Map<IWidgetMetric, MeanSatistics[]> analyzeDashboards(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		//printDashboards(actWorkspaceFolder, actDashboards, label);
		return calculateMetrics(actWorkspaceFolder, actDashboards);
	}

	private Map<IWidgetMetric, MeanSatistics[]> calculateMetrics(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		MetricCalculator calculator = new MetricCalculator();
		Map<IWidgetMetric, MetricResultsCollection> resultsCollection = calculator.measure(
				actDashboards,
				metrics,
				GEType.ALL_TYPES
		);
		Map<IWidgetMetric, MeanSatistics[]> stats = calculator.statistics(resultsCollection);
		return stats;
	}

	@SuppressWarnings("unused")
	private void printDashboards(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, String label) {
		int[][] printed = actDashboards.printDashboards(null);
		GrayMatrix.normalize(printed, actDashboards.length, false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, printed);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_crop/", actWorkspaceFolder.getFileName() + label);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		for (IMetric metric : metrics) {
			StringBuffer sb = new StringBuffer();
			for (WorkspaceFolder workspaceFolder : analyzedFolders) {
				sb.append(workspaceFolder.getFileName());
				MeanSatistics[] stats = meanValues.get(workspaceFolder).get(metric);
				sb.append(getFirstStat(stats));
				if(filtered) {
					MeanSatistics[] statsFiltered = meanValuesFiltered.get(workspaceFolder).get(metric);
					sb.append(getFirstStat(statsFiltered));
				}
				if(crop) {
					MeanSatistics[] statsCrop = meanValuesCrop.get(workspaceFolder).get(metric);
					sb.append(getFirstStat(statsCrop));
				}
				sb.append("\n");
			}
			FileUtils.saveTextFile(
					sb.toString(),
					actWorkspaceFolder.getPath() + "/_results/" + FILE + "_" + dashboard_source.name(),
					metric.getClass().getSimpleName()
			);
		}
	}
	
	private String getFirstStat(MeanSatistics[] stats) {
		String result = "\t" + stats[0].mean;
		if(stdev) {
			result += "\t" + stats[0].stdev;
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void formatStats(MeanSatistics[] stats, StringBuffer sb) {
		for (MeanSatistics stat : stats) {
			sb.append(stat.mean + "\t" + stat.stdev + " | \t");
		}
		sb.append("\n");
	}

}
