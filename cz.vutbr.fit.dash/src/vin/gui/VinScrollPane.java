package vin.gui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * Class with scroll pane.
 * 
 * @author jurij
 *
 */
public class VinScrollPane implements VinIComponent {
	
	protected JScrollPane panel;
	protected VinSurface surface;
	
	/**
	 * Creates new scroll pane.
	 */
	public VinScrollPane() {
	    
		surface = new VinSurface(640,480);
		
		panel = new JScrollPane(surface);
	    
	    
    }
	
	/**
	 * Returns surface where picture is set.
	 * 
	 * @return surface
	 */
	public VinSurface getVinSurface() {
		return surface;
	}

	@Override
    public JComponent getWidget() {
	    return panel;
    }

}
