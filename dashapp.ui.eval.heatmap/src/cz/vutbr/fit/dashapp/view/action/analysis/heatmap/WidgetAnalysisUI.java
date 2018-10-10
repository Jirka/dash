package cz.vutbr.fit.dashapp.view.action.analysis.heatmap;

import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.HeatMapWidgetAnalysis;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class WidgetAnalysisUI extends FolderAnalysisUI {
	
	private JTextField fileRegexTextField;
	private JCheckBox actFolderOutputCheckBox;
	private JCheckBox allFolderOutputCheckBox;
	private JCheckBox thresholdCheckBox;
	private JSlider thresholdSlider;
	private JTextField outputPathTextField;
	private JTextField outputFilePrefixTextField;
	private JCheckBox outBasicCheckBox;
	private JCheckBox outBasicBodyCheckBox;
	private JCheckBox outBordersCheckBox;
	private JCheckBox outBordersBodyCheckBox;
	

	public WidgetAnalysisUI() {
		super(new HeatMapWidgetAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		HeatMapWidgetAnalysis widgetAnalysis = (HeatMapWidgetAnalysis) analysis;
		
		// file regex
		panel.add(new JLabel("Input files regex:"));
		fileRegexTextField = new JTextField(widgetAnalysis.inputFilesRegex);
		panel.add(fileRegexTextField);
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", widgetAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", widgetAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(widgetAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(widgetAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// output types
		outBasicCheckBox = new JCheckBox("Full with borders", widgetAnalysis.enable_basic_output);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("Body with borders", widgetAnalysis.enable_basic_body_output);
		panel.add(outBasicBodyCheckBox);
		outBordersCheckBox = new JCheckBox("Full without borders", widgetAnalysis.enable_borders_output);
		panel.add(outBordersCheckBox);
		outBordersBodyCheckBox = new JCheckBox("Body without borders", widgetAnalysis.enable_borders_body_output);
		panel.add(outBordersBodyCheckBox);
		
		// threshold
		boolean isThresholdEnabled = widgetAnalysis.enable_custom_threshold;
		double defaultThreshold = widgetAnalysis.threshold;
		thresholdSlider = new JSlider(0, 100, 80);
		thresholdSlider.setEnabled(isThresholdEnabled);
		thresholdSlider.setMajorTickSpacing(50);
		thresholdSlider.setMinorTickSpacing(10);
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.setPaintLabels(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("WHITE") );
		JLabel actThresholdLabel = new JLabel(String.format("%.2f", defaultThreshold));
		labelTable.put( new Integer( 50 ), actThresholdLabel );
		labelTable.put( new Integer( 100 ), new JLabel("BLACK") );
		thresholdSlider.setLabelTable(labelTable);
		thresholdSlider.setEnabled(isThresholdEnabled);
		thresholdSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == thresholdSlider) {
					actThresholdLabel.setText(String.format("%.2f", (((JSlider) e.getSource()).getValue()/100.0)));
				}
			}
		});
		thresholdCheckBox = new JCheckBox("Custom threshold", isThresholdEnabled);
		thresholdCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == thresholdCheckBox) {
					thresholdSlider.setEnabled(thresholdCheckBox.isSelected());
				}
			}
		});
		panel.add(thresholdCheckBox);
		panel.add(thresholdSlider);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		HeatMapWidgetAnalysis widgetAnalysis = (HeatMapWidgetAnalysis) analysis;
		
		// file regex
		String chosenFileRegex = (String) fileRegexTextField.getText();
		if(chosenFileRegex == null || chosenFileRegex.isEmpty()) {
			// TODO test validity
			chosenFileRegex = widgetAnalysis.inputFilesRegex;
		}
		widgetAnalysis.inputFilesRegex = chosenFileRegex;
		
		// output types
		widgetAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		widgetAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		//heatMapAnalysis.enable_stats_output = statsCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = widgetAnalysis.outputFolderPath;
		}
		widgetAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = widgetAnalysis.outputFile;
		}
		widgetAnalysis.outputFile = chosenOuputFilePrefix;
		
		// output types
		widgetAnalysis.enable_basic_output = outBasicCheckBox.isSelected();
		widgetAnalysis.enable_basic_body_output = outBasicBodyCheckBox.isSelected();
		widgetAnalysis.enable_borders_output = outBordersCheckBox.isSelected();
		widgetAnalysis.enable_borders_body_output = outBordersBodyCheckBox.isSelected();
		
		// threshold
		widgetAnalysis.enable_custom_threshold = thresholdCheckBox.isSelected();
		widgetAnalysis.threshold = thresholdSlider.getValue()/100.0;
	}

}
