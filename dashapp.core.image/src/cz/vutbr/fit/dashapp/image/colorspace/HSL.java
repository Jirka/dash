package cz.vutbr.fit.dashapp.image.colorspace;

import java.awt.Color;

import org.colormine.colorspace.ColorSpaceConverter;
import org.colormine.colorspace.Hsl;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HSL implements ColorSpace {
	
	public static final int CHANNEL_HUE = 0;
	public static final int CHANNEL_SATURATION = 1;
	public static final int CHANNEL_LIGHTNESS = 2;

	public float h;
	public float s;
	public float l;

	public HSL(int rgb) {
		Hsl hsbvals = ColorSpaceConverter.colorToHsl(new Color(ColorMatrix.getRed(rgb), ColorMatrix.getGreen(rgb), ColorMatrix.getBlue(rgb)));
		this.h = (float) hsbvals.H;
		this.s = (float) hsbvals.S;
		this.l = (float) hsbvals.L;
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
		case CHANNEL_HUE:
			return new Double(h);
		case CHANNEL_SATURATION:
			return new Double(s);
		case CHANNEL_LIGHTNESS:
			return new Double(l);
		default:
			return -1;
		}
	}
	
	public int toRGB()
	{
		return ColorSpaceConverter.hslToColor(h, s, l).getRGB();
	}
	
	public static HSL[][] fromRGB(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		HSL[][] matrixHSL = new HSL[mW][mH];
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrixHSL[i][j] = new HSL(matrix[i][j]);
			}
		}
		return matrixHSL;
	}
}