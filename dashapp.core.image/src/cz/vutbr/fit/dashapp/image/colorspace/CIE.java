package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import extern.CIELab2;

public class CIE implements ColorSpace {
	
	public static final int CHANNEL_LIGHTNESS = 0;
	public static final int CHANNEL_A = 1;
	public static final int CHANNEL_B = 2;
	public static final int CHANNEL_CHROMA = 3;
	public static final int CHANNEL_HUE = 4;
	public static final int CHANNEL_SATURATION = 5;
	
	public double l;
	public double a;
	public double b;
	public double c;
	public double h;

	public CIE(int rgb) {
		double[] lchvals = CIELab2.fromRGB(ColorMatrix.getRed(rgb), ColorMatrix.getGreen(rgb), ColorMatrix.getBlue(rgb));
		this.l = lchvals[0];
		this.a = lchvals[1];
		this.b = lchvals[2];
		this.c = lchvals[3];
		this.h = lchvals[4];
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
		case CHANNEL_LIGHTNESS:
			return l;
		case CHANNEL_A:
			return a;
		case CHANNEL_B:
			return b;
		case CHANNEL_CHROMA:
			return c;
		case CHANNEL_HUE:
			return h;
		case CHANNEL_SATURATION:
			if (l == 0)
				return new Double(0);
			return c / l;
		default:
			return -1;
		}
	}

	@Override
	public int toRGB() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static CIE[][] fromRGB(int[][] matrix) {
		if(matrix.length > 0) {
			CIE[][] matrixLCH = new CIE[matrix.length][matrix[0].length];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrixLCH[i][j] = new CIE(matrix[i][j]);
				}
			}
			return matrixLCH;
		}
		return null;
	}
}