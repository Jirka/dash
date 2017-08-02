package cz.vutbr.fit.dashapp.image.colorspace;

public interface ColorSpace {

	public Object getColorChannel(int colorChannel);
	
	public int toRGB();
}