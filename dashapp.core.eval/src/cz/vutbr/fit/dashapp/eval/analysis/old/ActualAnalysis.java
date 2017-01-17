package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;

public class ActualAnalysis extends AbstractAnalysis implements IAnalysis {

	@Override
	public String getName() {
		return "Actual image analysis";
	}
	
	private Map.Entry<Integer, Integer> getMax(Map<Integer, Integer> histogram, Map.Entry<Integer, Integer> limit) {
		Map.Entry<Integer, Integer> max = null;
		for (Map.Entry<Integer, Integer> item : histogram.entrySet()) {
			if(max != null) {
				if(item.getValue() > max.getValue()) {
					if(limit == null || (item.getValue() <= limit.getValue() && item != limit)) {
						max = item;
					}
				}
			} else if(limit == null || (item != limit && item.getValue() <= limit.getValue())) {
				max = item;
			}
		}
		return max;
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		Dashboard dashboard = dashboardFile.getDashboard(true);
		if(dashboard != null) {
			BufferedImage image = DashAppView.getInstance().getDashboardView().getCanvas().getImage();
			if(image != null) {
				int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
				Map<Integer, Integer> histogram = MatrixUtils.getColorHistogram(matrix);
				
				DecimalFormat df = new DecimalFormat("#.#####");
				
				long size = histogram.values().size();
				
				buffer.append("Color count = " + size + "\n");
				size *= 100;
				buffer.append("  -> 1*1 bit (BW): " + 2 + " (" + df.format(((double) size)/2) + "%)\n");
				buffer.append("  -> 1*8 bit (GS): " + 256 + " (" + df.format(((double) size)/256) + "%)\n");
				buffer.append("  -> 3*8 bit (C): " + 16777216 + " (" + df.format(((double) size)/16777216) + "%)\n");
				buffer.append("  -> 3*7 bit: " + 2097152 + " (" + df.format(((double) size)/2097152) + "%)\n");
				buffer.append("  -> 3*6 bit: " + 262144 + " (" + df.format(((double) size)/262144) + "%)\n");
				buffer.append("  -> 3*5 bit: " + 32768 + " (" + df.format(((double) size)/32768) + "%)\n");
				buffer.append("  -> 3*4 bit: " + 4096 + " (" + df.format(((double) size)/4096) + "%)\n");
				buffer.append("  -> 3*3 bit: " + 512 + " (" + df.format(((double) size)/512) + "%)\n");
				buffer.append("  -> 3*2 bit: " + 64 + " (" + df.format(((double) size)/64) + "%)\n");
				buffer.append("  -> 3*2 bit: " + 8 + " (" + df.format(((double) size)/8) + "%)\n\n");
				
				Map.Entry<Integer, Integer> max = getMax(histogram, null);
				Integer key = max.getKey();
				Integer value = max.getValue();
				int area = dashboard.area();
				buffer.append("Most frequent color: R=" + MatrixUtils.getRed(key) + " G=" + MatrixUtils.getGreen(key) + " B=" + MatrixUtils.getBlue(key) + "\n");
				buffer.append("  -> " + value + "/" + area + "=" + df.format((((double) value)/(area))*100) + "%\n");
				
				max = getMax(histogram, max);
				key = max.getKey();
				value = max.getValue();
				buffer.append("Second frequent color: R=" + MatrixUtils.getRed(key) + " G=" + MatrixUtils.getGreen(key) + " B=" + MatrixUtils.getBlue(key) + "\n");
				buffer.append("  -> " + value + "/" + area + "=" + df.format((((double) value)/(area))*100) + "%\n");
			}
		}
		
		return buffer.toString();
	}

}
