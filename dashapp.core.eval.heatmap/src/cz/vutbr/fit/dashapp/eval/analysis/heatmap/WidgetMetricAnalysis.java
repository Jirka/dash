package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

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
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRhythm;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSimplicity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.ColorfulnessRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.DummyRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sblog;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sblog_x;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sb;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sb_final;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sb_final05;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sb05;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSBRatioCalculator_sb05_max1;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.HSLRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.IntensityRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator.PosterizedIntensityRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterDensity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterFinalDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterFinalDensity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterSymmetry2;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my.MyRasterUnity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoNormRasterDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoNormRasterDensity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoNormRasterUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoNormRasterUnity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterDensity_X;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterRhythm;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterSymmetry2;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.NgoRasterUnity_X;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.VirtualDashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanSatistics;

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
	private static int FILTER_EXTREME_ITEMS = 4;
	
	IWidgetMetric[] metrics = new IWidgetMetric[] { 
			//new MyRasterBalance(new HSBRatioCalculator_sb05()),
			/*new NgoBalance(),
			new NgoRasterBalance(new IntensityRatioCalculator()),
			new NgoRasterBalance(new PosterizedIntensityRatioCalculator()),
			new NgoRasterBalance(new ColorfulnessRatioCalculator()),
			new NgoRasterBalance(new HSBRatioCalculator_sb()),
			new NgoRasterBalance(new HSBRatioCalculator_sb05()),
			new NgoRasterBalance(new HSBRatioCalculator_sblog()),
			new MyRasterBalance(new DummyRatioCalculator()),
			new MyRasterBalance(new IntensityRatioCalculator()),
			new MyRasterBalance(new PosterizedIntensityRatioCalculator()),
			new MyRasterBalance(new ColorfulnessRatioCalculator()),
			new MyRasterBalance(new HSBRatioCalculator_sb()),
			new MyRasterBalance(new HSBRatioCalculator_sb05()),
			new MyRasterBalance(new HSBRatioCalculator_sblog()),*/
			/*new NgoEquilibrium(),
			new NgoRasterEquilibrium(new DummyRatioCalculator()),
			new NgoRasterEquilibrium(new IntensityRatioCalculator()),
			new NgoRasterEquilibrium(new PosterizedIntensityRatioCalculator()),
			new NgoRasterEquilibrium(new ColorfulnessRatioCalculator()),
			new NgoRasterEquilibrium(new HSBRatioCalculator_sb()),
			new NgoRasterEquilibrium(new HSBRatioCalculator_sb05()),
			new NgoRasterEquilibrium(new HSBRatioCalculator_sblog()),
			new MyRasterEquilibrium(new DummyRatioCalculator()),
			new MyRasterEquilibrium(new IntensityRatioCalculator()),
			new MyRasterEquilibrium(new PosterizedIntensityRatioCalculator()),
			new MyRasterEquilibrium(new ColorfulnessRatioCalculator()),
			new MyRasterEquilibrium(new HSBRatioCalculator_sb()),
			new MyRasterEquilibrium(new HSBRatioCalculator_sb05()),
			new MyRasterEquilibrium(new HSBRatioCalculator_sb05_max1()),
			new MyRasterEquilibrium(new HSBRatioCalculator_sblog()),*/
			//new NgoSymmetry(NgoSymmetry.MAX),
			/*new NgoRasterSymmetry2(new DummyRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new IntensityRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new PosterizedIntensityRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new ColorfulnessRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sb(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sb05(), NgoSymmetry.MAX),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sblog(), NgoSymmetry.MAX),*/
			//new NgoSymmetry(NgoSymmetry.SUM),
			/*new NgoRasterSymmetry2(new DummyRatioCalculator(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new IntensityRatioCalculator(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new PosterizedIntensityRatioCalculator(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new ColorfulnessRatioCalculator(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sb(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sb05(), NgoSymmetry.SUM),
			new NgoRasterSymmetry2(new HSBRatioCalculator_sblog(), NgoSymmetry.SUM),*/
			/*new MyRasterSymmetry2(new DummyRatioCalculator(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new IntensityRatioCalculator(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new PosterizedIntensityRatioCalculator(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new ColorfulnessRatioCalculator(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new HSBRatioCalculator_sb(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new HSBRatioCalculator_sb05(), MyRasterSymmetry.MAX),
			new MyRasterSymmetry2(new HSBRatioCalculator_sblog(), MyRasterSymmetry.MAX),*/
			/*new MyRasterSymmetry2(new DummyRatioCalculator(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new IntensityRatioCalculator(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new PosterizedIntensityRatioCalculator(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new ColorfulnessRatioCalculator(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new HSBRatioCalculator_sb(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new HSBRatioCalculator_sb05(), MyRasterSymmetry.SUM),
			new MyRasterSymmetry2(new HSBRatioCalculator_sblog(), MyRasterSymmetry.SUM),*/
			/*new NgoRasterSequence(new DummyRatioCalculator(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new IntensityRatioCalculator(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new PosterizedIntensityRatioCalculator(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new ColorfulnessRatioCalculator(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sb(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sb05(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sblog(), NgoRasterSequence.QUADRANT_AREA),
			new NgoRasterSequence(new DummyRatioCalculator(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new IntensityRatioCalculator(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new PosterizedIntensityRatioCalculator(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new ColorfulnessRatioCalculator(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sb(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sb05(), NgoRasterSequence.ALL_AREA),
			new NgoRasterSequence(new HSBRatioCalculator_sblog(), NgoRasterSequence.ALL_AREA),*/
			//new NgoRasterUnity(new HSBRatioCalculator_sblog()),
			/*new NgoRasterUnity(new DummyRatioCalculator()),
			new NgoRasterUnity(new IntensityRatioCalculator()),
			new NgoRasterUnity(new PosterizedIntensityRatioCalculator()),
			new NgoRasterUnity(new ColorfulnessRatioCalculator()),
			new NgoRasterUnity(new HSBRatioCalculator_sb()),
			new NgoRasterUnity(new HSBRatioCalculator_sb05()),
			new NgoRasterUnity(new HSBRatioCalculator_sblog()),
			new MyRasterUnity(new DummyRatioCalculator()),
			new MyRasterUnity(new IntensityRatioCalculator()),
			new MyRasterUnity(new PosterizedIntensityRatioCalculator()),
			new MyRasterUnity(new ColorfulnessRatioCalculator()),
			new MyRasterUnity(new HSBRatioCalculator_sb()),
			new MyRasterUnity(new HSBRatioCalculator_sb05()),
			new MyRasterUnity(new HSBRatioCalculator_sblog()),
			new NgoNormRasterUnity(new DummyRatioCalculator()),
			new NgoNormRasterUnity(new IntensityRatioCalculator()),
			new NgoNormRasterUnity(new PosterizedIntensityRatioCalculator()),
			new NgoNormRasterUnity(new ColorfulnessRatioCalculator()),
			new NgoNormRasterUnity(new HSBRatioCalculator_sb()),
			new NgoNormRasterUnity(new HSBRatioCalculator_sb05()),
			new NgoNormRasterUnity(new HSBRatioCalculator_sblog()),*/
			/*new NgoRasterUnity_X(new DummyRatioCalculator()),
			new NgoRasterUnity_X(new IntensityRatioCalculator()),
			new NgoRasterUnity_X(new PosterizedIntensityRatioCalculator()),
			new NgoRasterUnity_X(new ColorfulnessRatioCalculator()),
			new NgoRasterUnity_X(new HSBRatioCalculator_sb()),
			new NgoRasterUnity_X(new HSBRatioCalculator_sb05()),
			new NgoRasterUnity_X(new HSBRatioCalculator_sblog()),*/
			/*new MyRasterUnity_X(new DummyRatioCalculator()),
			new MyRasterUnity_X(new IntensityRatioCalculator()),
			new MyRasterUnity_X(new PosterizedIntensityRatioCalculator()),
			new MyRasterUnity_X(new ColorfulnessRatioCalculator()),
			new MyRasterUnity_X(new HSBRatioCalculator_sb()),
			new MyRasterUnity_X(new HSBRatioCalculator_sb05()),
			new MyRasterUnity_X(new HSBRatioCalculator_sblog()),
			new NgoNormRasterUnity_X(new DummyRatioCalculator()),
			new NgoNormRasterUnity_X(new IntensityRatioCalculator()),
			new NgoNormRasterUnity_X(new PosterizedIntensityRatioCalculator()),
			new NgoNormRasterUnity_X(new ColorfulnessRatioCalculator()),
			new NgoNormRasterUnity_X(new HSBRatioCalculator_sb()),
			new NgoNormRasterUnity_X(new HSBRatioCalculator_sb05()),
			new NgoNormRasterUnity_X(new HSBRatioCalculator_sblog()),*/
			/*new NgoRasterDensity(new DummyRatioCalculator()),
			new NgoRasterDensity(new IntensityRatioCalculator()),
			new NgoRasterDensity(new PosterizedIntensityRatioCalculator()),
			new NgoRasterDensity(new ColorfulnessRatioCalculator()),
			new NgoRasterDensity(new HSBRatioCalculator_sb()),
			new NgoRasterDensity(new HSBRatioCalculator_sb05()),
			new NgoRasterDensity(new HSBRatioCalculator_sblog()),
			new MyRasterDensity(new DummyRatioCalculator()),
			new MyRasterDensity(new IntensityRatioCalculator()),
			new MyRasterDensity(new PosterizedIntensityRatioCalculator()),
			new MyRasterDensity(new ColorfulnessRatioCalculator()),
			new MyRasterDensity(new HSBRatioCalculator_sb()),
			new MyRasterDensity(new HSBRatioCalculator_sb05()),
			new MyRasterDensity(new HSBRatioCalculator_sblog()),
			new NgoNormRasterDensity(new DummyRatioCalculator()),
			new NgoNormRasterDensity(new IntensityRatioCalculator()),
			new NgoNormRasterDensity(new PosterizedIntensityRatioCalculator()),
			new NgoNormRasterDensity(new ColorfulnessRatioCalculator()),
			new NgoNormRasterDensity(new HSBRatioCalculator_sb()),
			new NgoNormRasterDensity(new HSBRatioCalculator_sb05()),
			new NgoNormRasterDensity(new HSBRatioCalculator_sblog()),*/
			/*new NgoRasterDensity_X(new DummyRatioCalculator()),
			new NgoRasterDensity_X(new IntensityRatioCalculator()),
			new NgoRasterDensity_X(new PosterizedIntensityRatioCalculator()),
			new NgoRasterDensity_X(new ColorfulnessRatioCalculator()),
			new NgoRasterDensity_X(new HSBRatioCalculator_sb()),
			new NgoRasterDensity_X(new HSBRatioCalculator_sb05()),
			new NgoRasterDensity_X(new HSBRatioCalculator_sblog()),
			new MyRasterDensity_X(new DummyRatioCalculator()),
			new MyRasterDensity_X(new IntensityRatioCalculator()),
			new MyRasterDensity_X(new PosterizedIntensityRatioCalculator()),
			new MyRasterDensity_X(new ColorfulnessRatioCalculator()),
			new MyRasterDensity_X(new HSBRatioCalculator_sb()),
			new MyRasterDensity_X(new HSBRatioCalculator_sb05()),
			new MyRasterDensity_X(new HSBRatioCalculator_sblog()),
			new NgoNormRasterDensity_X(new DummyRatioCalculator()),
			new NgoNormRasterDensity_X(new IntensityRatioCalculator()),
			new NgoNormRasterDensity_X(new PosterizedIntensityRatioCalculator()),
			new NgoNormRasterDensity_X(new ColorfulnessRatioCalculator()),
			new NgoNormRasterDensity_X(new HSBRatioCalculator_sb()),
			new NgoNormRasterDensity_X(new HSBRatioCalculator_sb05()),
			new NgoNormRasterDensity_X(new HSBRatioCalculator_sblog()),*/
			/*new NgoRasterRhythm(new DummyRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new IntensityRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new PosterizedIntensityRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new ColorfulnessRatioCalculator(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new HSBRatioCalculator_sb(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new HSBRatioCalculator_sb05(), NgoSymmetry.MAX),
			new NgoRasterRhythm(new HSBRatioCalculator_sblog(), NgoSymmetry.MAX),*/
			/*new WidgetCount(),
			new Area(),
			new NgoBalance(),
			new NgoCohesion(),
			new NgoDensity(),
			new NgoEconomy(),
			new NgoEquilibrium(),
			new NgoHomogenity(),
			new NgoProportion(),
			new NgoRegularity(),
			new NgoRhythm(),
			new NgoSimplicity(),
			new NgoSequence(),
			new NgoSymmetry(),
			new NgoUnity(),*/
			//new NgoDensity(),
			
			/*new MyRasterFinalDensity(new DummyRatioCalculator()),
			new MyRasterFinalDensity(new IntensityRatioCalculator()),
			new MyRasterFinalDensity(new PosterizedIntensityRatioCalculator()),
			new MyRasterFinalDensity(new ColorfulnessRatioCalculator()),
			new MyRasterFinalDensity(new HSBRatioCalculator_sb()),
			new MyRasterFinalDensity(new HSBRatioCalculator_sb05()),
			new MyRasterFinalDensity(new HSBRatioCalculator_sblog()),
			
			new MyRasterFinalDensity_X(new DummyRatioCalculator()),
			new MyRasterFinalDensity_X(new IntensityRatioCalculator()),
			new MyRasterFinalDensity_X(new PosterizedIntensityRatioCalculator()),
			new MyRasterFinalDensity_X(new ColorfulnessRatioCalculator()),
			new MyRasterFinalDensity_X(new HSBRatioCalculator_sb()),
			new MyRasterFinalDensity_X(new HSBRatioCalculator_sb05()),
			new MyRasterFinalDensity_X(new HSBRatioCalculator_sblog()),*/
			
			//new NgoRasterBalance(new HSLRatioCalculator()),
			//new MyRasterBalance(new HSLRatioCalculator()),
			//new MyRasterFinalDensity(new HSLRatioCalculator()),
			//new MyRasterFinalDensity_X(new HSLRatioCalculator()),
			//new MyRasterFinalDensity_X(new HSBRatioCalculator_sblog()),
			//new NgoRasterBalance(new HSBRatioCalculator_sblog_x()),
			//new MyRasterBalance(new HSBRatioCalculator_sblog_x()),
			//new MyRasterFinalDensity(new HSBRatioCalculator_sblog_x()),
			//new MyRasterFinalDensity_X(new HSBRatioCalculator_sblog_x()),
			
			new NgoRasterBalance(new HSBRatioCalculator_sb_final()),
			new MyRasterBalance(new HSBRatioCalculator_sb_final()),
			new MyRasterFinalDensity(new HSBRatioCalculator_sb_final()),
			new MyRasterFinalDensity_X(new HSBRatioCalculator_sb_final()),
			
			new NgoRasterBalance(new HSBRatioCalculator_sb_final05()),
			new MyRasterBalance(new HSBRatioCalculator_sb_final05()),
			new MyRasterFinalDensity(new HSBRatioCalculator_sb_final05()),
			new MyRasterFinalDensity_X(new HSBRatioCalculator_sb_final05()),
			
			/*new MyRasterFinalDensity(new ColorfulnessRatioCalculator()),
			new MyRasterFinalDensity_X(new ColorfulnessRatioCalculator()),*/
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
					image = ColorMatrix.printMatrixToImage(null, ColorMatrix.printImageToMatrix(image, dashboard));
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
		Map<IWidgetMetric, MeanSatistics[]> stats = calculator.statistics(resultsCollection, FILTER_EXTREME_ITEMS);
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
					actWorkspaceFolder.getPath() + 
					  (FILTER_EXTREME_ITEMS > 0 ? ("/_results_filtered_" + FILTER_EXTREME_ITEMS + "/") : "/results/")
					  + FILE + "_" + dashboard_source.name() + "/debug",
					metric.getName()
			);
			//System.out.println(sb.toString());
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
