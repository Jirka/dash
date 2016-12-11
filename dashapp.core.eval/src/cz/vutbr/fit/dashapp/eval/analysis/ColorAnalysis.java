package cz.vutbr.fit.dashapp.eval.analysis;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

public class ColorAnalysis extends AbstractAnalysis implements IAnalysis {
	
	private final DecimalFormat df = new DecimalFormat("#.#####");

	public ColorAnalysis(DashboardFile dashboardFile) {
		super(dashboardFile);
	}

	@Override
	public String getName() {
		return "Color analysis";
	}
	
	public static Map.Entry<Integer, Integer> getMax(Map<Integer, Integer> histogram, Map.Entry<Integer, Integer> limit) {
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
	
	private void analyseImage(StringBuffer buffer, BufferedImage image, int bit) {
		int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
		MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, bit)), false);
		Map<Integer, Integer> histogram = MatrixUtils.getColorHistogram(matrix);
		
		buffer.append("===== " + bit + " bit (" + (int)(Math.pow(2, bit)) + "*3 colors) =====\n");
		
		long size = histogram.values().size();
		
		buffer.append("Color count = " + size + "\n");
		size *= 100;
		buffer.append("  -> 3*"+ bit + " bit: " + Math.pow(2, bit*3) + " (" + df.format(((double) size)/Math.pow(2, bit*3)) + "%)\n");
		buffer.append("  -> 3*8 bit (C): " + 16777216 + " (" + df.format(((double) size)/16777216) + "%)\n");
		
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
		
		buffer.append("\n");
	}

	@Override
	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				buffer.append("====== COLOR ANALYSIS ======\n");
				buffer.append("  -> posterization\n");
				buffer.append("\n");
				
				analyseImage(buffer, image, 8);
				analyseImage(buffer, image, 7);
				analyseImage(buffer, image, 6);
				analyseImage(buffer, image, 5);
				analyseImage(buffer, image, 4);
				analyseImage(buffer, image, 3);
			}
		}
		
		return buffer.toString();
	}

}
