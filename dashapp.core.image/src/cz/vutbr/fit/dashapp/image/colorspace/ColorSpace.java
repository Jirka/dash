package cz.vutbr.fit.dashapp.image.colorspace;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface ColorSpace {

	public Object getColorChannel(int colorChannel);
	
	public int toRGB();
}