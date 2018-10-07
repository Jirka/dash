package cz.vutbr.fit.dashapp.view.tools.segmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.segmenation.methods.DashboardSegmentation;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashboardSegmentationUI extends SegmentationAlgorithmUI {

	private JCheckBox gradientThresholdCheckBox;
	private JSlider gradientThresholdSlider;
	private JCheckBox posterizationValueCheckBox;
	private JSlider posterizationValueSlider;

	public DashboardSegmentationUI() {
		super(new DashboardSegmentation());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		DashboardSegmentation dashboardSegmentationAlgorithm = (DashboardSegmentation) segmentationAlgorithm;
		
		// gradient threshold values
		boolean isGradinetThresholdEnabled = dashboardSegmentationAlgorithm.enableCustomGradientThreshold;
		int defaultGradientThreshold = dashboardSegmentationAlgorithm.gradientThreshold;
		gradientThresholdSlider = new JSlider(0, 4, defaultGradientThreshold);
		//gradientThresholdSlider.setMajorTickSpacing(2);
		gradientThresholdSlider.setMinorTickSpacing(1);
		gradientThresholdSlider.setPaintTicks(true);
		gradientThresholdSlider.setPaintLabels(true);
		Hashtable<Integer, JLabel> gradientLabelTable = new Hashtable<Integer, JLabel>();
		gradientLabelTable.put( new Integer( gradientThresholdSlider.getMinimum() ), new JLabel(Integer.toString(gradientThresholdSlider.getMinimum())) );
		JLabel actGradientLabel = new JLabel((defaultGradientThreshold < 10 ? "  " : "") + Integer.toString(defaultGradientThreshold));
		gradientLabelTable.put( new Integer( gradientThresholdSlider.getMaximum()/2 ), actGradientLabel );
		gradientLabelTable.put( new Integer( gradientThresholdSlider.getMaximum() ), new JLabel(Integer.toString(gradientThresholdSlider.getMaximum())) );
		gradientThresholdSlider.setLabelTable(gradientLabelTable);
		gradientThresholdSlider.setEnabled(isGradinetThresholdEnabled);
		gradientThresholdSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == gradientThresholdSlider) {
					actGradientLabel.setText(Integer.toString((((JSlider) e.getSource()).getValue())));
					actGradientLabel.repaint();
				}
			}
		});
		gradientThresholdCheckBox = new JCheckBox("Custom gradient threshold", isGradinetThresholdEnabled);
		gradientThresholdCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == gradientThresholdCheckBox) {
					gradientThresholdSlider.setEnabled(gradientThresholdCheckBox.isSelected());
				}
			}
		});
		panel.add(gradientThresholdCheckBox);
		panel.add(gradientThresholdSlider);
		
		// gradient threshold values
		boolean isPosterizationValueEnabled = dashboardSegmentationAlgorithm.enableCustomPosterizationValue;
		int defaultPosterizationValueThreshold = dashboardSegmentationAlgorithm.posterizationValue;
		posterizationValueSlider = new JSlider(1, 8, defaultPosterizationValueThreshold);
		//posterizationValueSlider.setMajorTickSpacing(5);
		posterizationValueSlider.setMinorTickSpacing(1);
		posterizationValueSlider.setPaintTicks(true);
		posterizationValueSlider.setPaintLabels(true);
		Hashtable<Integer, JLabel> posterizationlabelTable = new Hashtable<Integer, JLabel>();
		posterizationlabelTable.put( new Integer( posterizationValueSlider.getMinimum() ), new JLabel(Integer.toString(posterizationValueSlider.getMinimum())) );
		JLabel actPosterizationValueLabel = new JLabel((defaultPosterizationValueThreshold < 10 ? "  " : "") + Integer.toString(defaultPosterizationValueThreshold));
		posterizationlabelTable.put( new Integer( posterizationValueSlider.getMaximum()/2 ), actPosterizationValueLabel );
		posterizationlabelTable.put( new Integer( posterizationValueSlider.getMaximum() ), new JLabel(Integer.toString(posterizationValueSlider.getMaximum())) );
		posterizationValueSlider.setLabelTable(posterizationlabelTable);
		posterizationValueSlider.setEnabled(isPosterizationValueEnabled);
		posterizationValueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == posterizationValueSlider) {
					actPosterizationValueLabel.setText(Integer.toString((((JSlider) e.getSource()).getValue())));
					actPosterizationValueLabel.repaint();
				}
			}
		});
		posterizationValueCheckBox = new JCheckBox("Custom posterization value", isPosterizationValueEnabled);
		posterizationValueCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == posterizationValueCheckBox) {
					posterizationValueSlider.setEnabled(posterizationValueCheckBox.isSelected());
				}
			}
		});
		panel.add(posterizationValueCheckBox);
		panel.add(posterizationValueSlider);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		DashboardSegmentation dashboardSegmentationAlgorithm = (DashboardSegmentation) segmentationAlgorithm;
		
		// gradient threshold
		dashboardSegmentationAlgorithm.enableCustomGradientThreshold = gradientThresholdCheckBox.isSelected();
		dashboardSegmentationAlgorithm.gradientThreshold = gradientThresholdSlider.getValue();
		
		// gradient threshold
		dashboardSegmentationAlgorithm.enableCustomPosterizationValue = posterizationValueCheckBox.isSelected();
		dashboardSegmentationAlgorithm.posterizationValue = posterizationValueSlider.getValue();
	}

}
