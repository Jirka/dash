package cz.vutbr.fit.dash.analyses;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;

import cz.vutbr.fit.dash.metric.BackgroundShare;
import cz.vutbr.fit.dash.metric.Cohesion;
import cz.vutbr.fit.dash.metric.ColorShare;
import cz.vutbr.fit.dash.metric.Colorfulness;
import cz.vutbr.fit.dash.metric.IntensitiesCount;
import cz.vutbr.fit.dash.metric.Proportion;
import cz.vutbr.fit.dash.metric.RasterBalance;
import cz.vutbr.fit.dash.metric.RasterSymmetry;
import cz.vutbr.fit.dash.metric.Regularity;
import cz.vutbr.fit.dash.metric.ThresholdDensity;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.util.MatrixUtils;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dash.util.MatrixUtils.HSB;
import cz.vutbr.fit.dash.util.MatrixUtils.LCH;

public class ComplexWidgetAnalysis extends AbstractAnalysis implements IAnalysis {

	public ComplexWidgetAnalysis(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getName() {
		return "Complex raster analysis";
	}

	@Override
	public String analyse() {
		// HSB		HSB h mean	HSB h stdev	HSB h	HSB s mean	HSB s stdev	HSB s	HSB b mean	HSB b stdev	HSB b		
		// CIE Lab	CIE L mean	CIE L stdev	CIE L	CIE a mean	CIE a stdev	CIE a	CIE b mean	CIE b stdev	CIE b	CIE c mean	CIE c stdev	CIE c	CIE h mean	CIE h stdev	CIE h	LCH colorf.	LCH mean	LCH std var		
		// RGB 3*8	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// RGB 3*4	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.	1. st color		
		// gray 8	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.	balance	H balance	V balance	symmetry	H balance	V balance		
		// gray 4	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// BW 1		% black	balance	H balance	V balance	symmetry	H balance	V balance
		StringBuffer buffer = new StringBuffer();
		DecimalFormat df = new DecimalFormat("#.#####");
		
		if(dashboard != null) {
			appendValue(buffer, df, new Cohesion(dashboard, null).measure(), 0, false);
			appendValue(buffer, df, new Proportion(dashboard, null).measure(), 1, false);
			appendValue(buffer, df, new Regularity(dashboard, null).measure(), 1, false);
		}
		
		return buffer.toString();
	}
	
	private void appendValue(StringBuffer buffer, DecimalFormat df, Object value, int tabNum, boolean rolFirst) {
		for (int i = 0; i < tabNum; i++) {
			buffer.append('\t');
		}
		if(value instanceof Double) {
			buffer.append(df.format(value));
		} else if(value instanceof Object[]) {
			Object[] mm = (Object[]) value;
			int i = 0;
			Object first = 0;
			for (Object d : mm) {
				if(rolFirst && i == 0) {
					first = d;
				} else {
					if((i > 0 && !rolFirst) || i > 1) {
						buffer.append('\t');
					}
					if(d instanceof Double) {
						buffer.append(df.format((double) d));
					} else {
						buffer.append(d.toString());
					}
				}
				i++;
			}
			if (rolFirst) {
				buffer.append('\t');
				buffer.append(df.format(first));
			}
		}
	}

}
