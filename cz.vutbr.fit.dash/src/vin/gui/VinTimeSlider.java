package vin.gui;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class which contains time slider.
 * 
 * @author jurij
 *
 */
public class VinTimeSlider implements VinIComponent, ChangeListener {
	
	protected JToolBar panel;
	protected JSlider slider;
	
	/**
	 * Creates time slider.
	 */
	public VinTimeSlider() {
	    panel = new JToolBar();
	    slider = new JSlider(0, 100);
	    slider.setValue(ViewGui.getVinScrollPane().getVinSurface().getTime());
	    slider.setToolTipText("Set clock speed.");
	    slider.setMajorTickSpacing(20);
	    slider.setMinorTickSpacing(10);
	    slider.setPaintLabels(true);
	    slider.setPaintTicks(true);
	    slider.addChangeListener(this);
	    panel.add(slider);
    }

	@Override
    public JComponent getWidget() {
	    return panel;
    }

	@Override
    public void stateChanged(ChangeEvent e) {
	    if(e.getSource() == slider) {
	    	ViewGui.getVinScrollPane().getVinSurface().setTime(slider.getValue());
	    }
    }

}
