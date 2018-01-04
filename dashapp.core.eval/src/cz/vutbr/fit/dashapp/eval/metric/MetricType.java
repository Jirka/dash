package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.eval.metric.raster.ColorShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.color.Colorfulness;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.BlackDensity;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramBackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramIntensitiesCount;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.WidgetTotalArea;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.WidgetCount;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.WidgetUsedArea;
import cz.vutbr.fit.dashapp.eval.metric.widget.kim.KimBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.kim.KimSymmetry;
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
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.IntensityRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.PosterizedIntensityRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.ColorfulnessRatioCalculator;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.HSBRatioCalculator_sb_final;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.HSBRatioCalculator_sb_final05;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterDensity.DivideByType;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterRhythm;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterSymmetry.NormalizationType;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterSymmetry.QuadrantResolverType;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterUnity;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo.NgoRasterUnity.MultiplyByType;
import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public enum MetricType {
	// raster color
	Colorfulness_HSB_s,
	Colorfulness_CIE_s,
	ColorShare,
	
	// raster gray
	BlackDensity,
	GrayBalance,
	GraySymmetry,
	
	// raster gray histogram
	BackgroundShare,
	IntensitiesCount,
	
	// widget basic
	WidgetCount,
	WidgetActualArea,
	WidgetUsedArea,
	
	// widget kim
	KimBalance,
	KimSymmetry,
	
	// widget ngo
	NgoBalance,
	NgoCohesion,
	NgoDensity,
	NgoEconomy,
	NgoEquilibrium,
	NgoHomogenity,
	NgoProportion,
	NgoRegularity,
	NgoRhythm_MAX,
	NgoSimplicity,
	NgoSequence,
	NgoSymmetry_MAX,
	NgoUnity,
	
	// widget raster ngo - intensity calculator
	Intensity_NgoBalance,
	Intensity_NgoBalance_side_area,
	Intensity_NgoDensity,
	Intensity_NgoDensity_dl_MAX_RATIO,
	Intensity_NgoDensity_total_dl_MAX_RATIO,
	Intensity_NgoEquilibrium,
	Intensity_NgoEquilibrium_side_area,
	Intensity_NgoRhythm_MAX,
	Intensity_NgoSequence_QUADRANT,
	Intensity_NgoSymmetry_MAX_14,
	Intensity_NgoSymmetry_MAX_14_2,
	Intensity_NgoUnity,
	
	// widget raster ngo - posterized intensity calculator
	PostIntensity_NgoBalance,
	PostIntensity_NgoBalance_side_area,
	PostIntensity_NgoDensity,
	PostIntensity_NgoDensity_dl_MAX_RATIO,
	PostIntensity_NgoDensity_total_dl_MAX_RATIO,
	PostIntensity_NgoEquilibrium,
	PostIntensity_NgoEquilibrium_side_area,
	PostIntensity_NgoRhythm_MAX,
	PostIntensity_NgoSequence_QUADRANT,
	PostIntensity_NgoSymmetry_MAX_14,
	PostIntensity_NgoSymmetry_MAX_14_2,
	PostIntensity_NgoUnity,
	
	// widget raster ngo - colorfulness calculator
	Colorfulness_NgoBalance,
	Colorfulness_NgoBalance_side_area,
	Colorfulness_NgoDensity,
	Colorfulness_NgoDensity_dl_MAX_RATIO,
	Colorfulness_NgoDensity_total_dl_MAX_RATIO,
	Colorfulness_NgoEquilibrium,
	Colorfulness_NgoEquilibrium_side_area,
	Colorfulness_NgoRhythm_MAX,
	Colorfulness_NgoSequence_QUADRANT,
	Colorfulness_NgoSymmetry_MAX_14,
	Colorfulness_NgoSymmetry_MAX_14_2,
	Colorfulness_NgoUnity,
	
	// widget raster ngo - HSB sb
	FinalSB_NgoBalance,
	FinalSB_NgoBalance_side_area,
	FinalSB_NgoDensity,
	FinalSB_NgoDensity_dl_MAX_RATIO,
	FinalSB_NgoDensity_total_dl_MAX_RATIO,
	FinalSB_NgoEquilibrium,
	FinalSB_NgoEquilibrium_side_area,
	FinalSB_NgoRhythm_MAX,
	FinalSB_NgoSequence_QUADRANT,
	FinalSB_NgoSymmetry_MAX_14,
	FinalSB_NgoSymmetry_MAX_14_2,
	FinalSB_NgoUnity,
	
	// widget raster ngo - HSB sb 0.5
	FinalSB05_NgoBalance,
	FinalSB05_NgoBalance_side_area,
	FinalSB05_NgoDensity,
	FinalSB05_NgoDensity_dl_MAX_RATIO,
	FinalSB05_NgoDensity_total_dl_MAX_RATIO,
	FinalSB05_NgoEquilibrium,
	FinalSB05_NgoEquilibrium_side_area,
	FinalSB05_NgoRhythm_MAX,
	FinalSB05_NgoSequence_QUADRANT,
	FinalSB05_NgoSymmetry_MAX_14,
	FinalSB05_NgoSymmetry_MAX_14_2,
	FinalSB05_NgoUnity,
	;
	
	String label = null;
	
	private MetricType() {
		this.label = "";
	}
	
	private MetricType(String label) {
		this.label = label;
	}
	
	public IMetric createMetric() {
		switch (this) {
		
			// raster color
			case Colorfulness_HSB_s: return new Colorfulness(HSB.class, HSB.CHANNEL_SATURATION);
			case Colorfulness_CIE_s: return new Colorfulness(CIE.class, CIE.CHANNEL_SATURATION);
			case ColorShare: return new ColorShare();
			
			// raster gray
			case BlackDensity: return new BlackDensity();
			case GrayBalance: return new GrayBalance();
			case GraySymmetry: return new GraySymmetry();
			
			// raster gray histogram
			case BackgroundShare: return new HistogramBackgroundShare();
			case IntensitiesCount: return new HistogramIntensitiesCount();
			
			// widget basic
			case WidgetCount: return new WidgetCount();
			case WidgetActualArea: return new WidgetTotalArea();
			case WidgetUsedArea: return new WidgetUsedArea();
			
			// widget kim
			case KimBalance: return new KimBalance();
			case KimSymmetry: return new KimSymmetry();
			
			// widget ngo
			case NgoBalance: return new NgoBalance();
			case NgoCohesion: return new NgoCohesion();
			case NgoDensity: return new NgoDensity();
			case NgoEconomy: return new NgoEconomy();
			case NgoEquilibrium: return new NgoEquilibrium();
			case NgoHomogenity: return new NgoHomogenity();
			case NgoProportion: return new NgoProportion();
			case NgoRegularity: return new NgoRegularity();
			case NgoRhythm_MAX: return new NgoRhythm();
			case NgoSimplicity: return new NgoSimplicity();
			case NgoSequence: return new NgoSequence();
			case NgoSymmetry_MAX: return new NgoSymmetry();
			case NgoUnity: return new NgoUnity();
			
			// widget raster ngo - intensity calculator
			case Intensity_NgoBalance: return new NgoRasterBalance(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterBalance.BASIC);
			case Intensity_NgoBalance_side_area: return new NgoRasterBalance(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterBalance.AREA_OF_SIDE);
			case Intensity_NgoDensity: return new NgoRasterDensity(GEType.ALL_TYPES, new IntensityRatioCalculator(), false, false, DivideByType.DIVIDE_BY_1);
			case Intensity_NgoDensity_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new IntensityRatioCalculator(), false, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case Intensity_NgoDensity_total_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new IntensityRatioCalculator(), true, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case Intensity_NgoEquilibrium: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case Intensity_NgoEquilibrium_side_area: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case Intensity_NgoRhythm_MAX: return new NgoRasterRhythm(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterRhythm.MAX);
			case Intensity_NgoSequence_QUADRANT: return new NgoRasterSequence(GEType.ALL_TYPES, new IntensityRatioCalculator(), NgoRasterSequence.QUADRANT_AREA);
			case Intensity_NgoSymmetry_MAX_14: return new NgoRasterSymmetry(GEType.ALL_TYPES, new IntensityRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.BASIC_14);
			case Intensity_NgoSymmetry_MAX_14_2: return new NgoRasterSymmetry(GEType.ALL_TYPES, new IntensityRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.ADVANCED_14);
			case Intensity_NgoUnity: return new NgoRasterUnity(GEType.ALL_TYPES, new IntensityRatioCalculator(), false, MultiplyByType.MULTIPLY_BY_1);
			
			// widget raster ngo - posterized intensity calculator
			case PostIntensity_NgoBalance: return new NgoRasterBalance(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterBalance.BASIC);
			case PostIntensity_NgoBalance_side_area: return new NgoRasterBalance(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterBalance.AREA_OF_SIDE);
			case PostIntensity_NgoDensity: return new NgoRasterDensity(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), false, false, DivideByType.DIVIDE_BY_1);
			case PostIntensity_NgoDensity_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), false, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case PostIntensity_NgoDensity_total_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), true, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case PostIntensity_NgoEquilibrium: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case PostIntensity_NgoEquilibrium_side_area: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case PostIntensity_NgoRhythm_MAX: return new NgoRasterRhythm(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterRhythm.MAX);
			case PostIntensity_NgoSequence_QUADRANT: return new NgoRasterSequence(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NgoRasterSequence.QUADRANT_AREA);
			case PostIntensity_NgoSymmetry_MAX_14: return new NgoRasterSymmetry(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.BASIC_14);
			case PostIntensity_NgoSymmetry_MAX_14_2: return new NgoRasterSymmetry(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.ADVANCED_14);
			case PostIntensity_NgoUnity: return new NgoRasterUnity(GEType.ALL_TYPES, new PosterizedIntensityRatioCalculator(), false, MultiplyByType.MULTIPLY_BY_1);
			
			// widget raster ngo - colorfulness calculator
			case Colorfulness_NgoBalance: return new NgoRasterBalance(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterBalance.BASIC);
			case Colorfulness_NgoBalance_side_area: return new NgoRasterBalance(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterBalance.AREA_OF_SIDE);
			case Colorfulness_NgoDensity: return new NgoRasterDensity(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), false, false, DivideByType.DIVIDE_BY_1);
			case Colorfulness_NgoDensity_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), false, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case Colorfulness_NgoDensity_total_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), true, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case Colorfulness_NgoEquilibrium: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case Colorfulness_NgoEquilibrium_side_area: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterEquilibrium.BASIC);
			case Colorfulness_NgoRhythm_MAX: return new NgoRasterRhythm(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterRhythm.MAX);
			case Colorfulness_NgoSequence_QUADRANT: return new NgoRasterSequence(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NgoRasterSequence.QUADRANT_AREA);
			case Colorfulness_NgoSymmetry_MAX_14: return new NgoRasterSymmetry(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.BASIC_14);
			case Colorfulness_NgoSymmetry_MAX_14_2: return new NgoRasterSymmetry(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), NormalizationType.MAX, QuadrantResolverType.ADVANCED_14);
			case Colorfulness_NgoUnity: return new NgoRasterUnity(GEType.ALL_TYPES, new ColorfulnessRatioCalculator(), false, MultiplyByType.MULTIPLY_BY_1);
			
			// widget raster ngo - HSB sb
			case FinalSB_NgoBalance: return new NgoRasterBalance(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterBalance.BASIC);
			case FinalSB_NgoBalance_side_area: return new NgoRasterBalance(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterBalance.AREA_OF_SIDE);
			case FinalSB_NgoDensity: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), false, false, DivideByType.DIVIDE_BY_1);
			case FinalSB_NgoDensity_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), false, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case FinalSB_NgoDensity_total_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), true, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case FinalSB_NgoEquilibrium: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterEquilibrium.BASIC);
			case FinalSB_NgoEquilibrium_side_area: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterEquilibrium.BASIC);
			case FinalSB_NgoRhythm_MAX: return new NgoRasterRhythm(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterRhythm.MAX);
			case FinalSB_NgoSequence_QUADRANT: return new NgoRasterSequence(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NgoRasterSequence.QUADRANT_AREA);
			case FinalSB_NgoSymmetry_MAX_14: return new NgoRasterSymmetry(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NormalizationType.MAX, QuadrantResolverType.BASIC_14);
			case FinalSB_NgoSymmetry_MAX_14_2: return new NgoRasterSymmetry(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), NormalizationType.MAX, QuadrantResolverType.ADVANCED_14);
			case FinalSB_NgoUnity: return new NgoRasterUnity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final(), false, MultiplyByType.MULTIPLY_BY_1);
			
			// widget raster ngo - HSB sb 0.5
			case FinalSB05_NgoBalance: return new NgoRasterBalance(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterBalance.BASIC);
			case FinalSB05_NgoBalance_side_area: return new NgoRasterBalance(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterBalance.AREA_OF_SIDE);
			case FinalSB05_NgoDensity: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), false, false, DivideByType.DIVIDE_BY_1);
			case FinalSB05_NgoDensity_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), false, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case FinalSB05_NgoDensity_total_dl_MAX_RATIO: return new NgoRasterDensity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), true, true, DivideByType.DIVIDE_BY_MAX_RATIO);
			case FinalSB05_NgoEquilibrium: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterEquilibrium.BASIC);
			case FinalSB05_NgoEquilibrium_side_area: return new NgoRasterEquilibrium(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterEquilibrium.BASIC);
			case FinalSB05_NgoRhythm_MAX: return new NgoRasterRhythm(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterRhythm.MAX);
			case FinalSB05_NgoSequence_QUADRANT: return new NgoRasterSequence(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NgoRasterSequence.QUADRANT_AREA);
			case FinalSB05_NgoSymmetry_MAX_14: return new NgoRasterSymmetry(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NormalizationType.MAX, QuadrantResolverType.BASIC_14);
			case FinalSB05_NgoSymmetry_MAX_14_2: return new NgoRasterSymmetry(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), NormalizationType.MAX, QuadrantResolverType.ADVANCED_14);
			case FinalSB05_NgoUnity: return new NgoRasterUnity(GEType.ALL_TYPES, new HSBRatioCalculator_sb_final05(), false, MultiplyByType.MULTIPLY_BY_1);
			
			default: return null;
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		// raster color
		case Colorfulness_HSB_s:
		case Colorfulness_CIE_s:	
		case ColorShare:
		
		// raster gray
		case BlackDensity:
		case GrayBalance:
		case GraySymmetry:
		
		// raster gray histogram
		case BackgroundShare:
		case IntensitiesCount:
		
		// widget basic
		case WidgetCount:
		case WidgetActualArea:
		case WidgetUsedArea:
		
		// widget kim
		case KimBalance:
		case KimSymmetry:
		
		// widget ngo
		case NgoBalance:
		case NgoCohesion:
		case NgoDensity:
		case NgoEconomy:
		case NgoEquilibrium:
		case NgoHomogenity:
		case NgoProportion:
		case NgoRegularity:
		case NgoRhythm_MAX:
		case NgoSimplicity:
		case NgoSequence:
		case NgoSymmetry_MAX:
		case NgoUnity:
			return this.name()/* + "()"*/;

		default:
			return super.toString();
		}
	}
}