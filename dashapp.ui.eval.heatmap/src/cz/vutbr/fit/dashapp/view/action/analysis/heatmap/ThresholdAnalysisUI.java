package cz.vutbr.fit.dashapp.view.action.analysis.heatmap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.ThresholdAnalysis;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class ThresholdAnalysisUI extends FolderAnalysisUI {
	
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
	

	public ThresholdAnalysisUI() {
		super(new ThresholdAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		ThresholdAnalysis thresholdAnalysis = (ThresholdAnalysis) analysis;
		
		// file regex
		panel.add(new JLabel("Input files regex:"));
		fileRegexTextField = new JTextField(thresholdAnalysis.inputFilesRegex);
		panel.add(fileRegexTextField);
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", thresholdAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", thresholdAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(thresholdAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(thresholdAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// output types
		outBasicCheckBox = new JCheckBox("Full with borders", thresholdAnalysis.enable_basic_output);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("Body with borders", thresholdAnalysis.enable_basic_body_output);
		panel.add(outBasicBodyCheckBox);
		outBordersCheckBox = new JCheckBox("Full without borders", thresholdAnalysis.enable_borders_output);
		panel.add(outBordersCheckBox);
		outBordersBodyCheckBox = new JCheckBox("Body without borders", thresholdAnalysis.enable_borders_body_output);
		panel.add(outBordersBodyCheckBox);
		
		// threshold
		boolean isThresholdEnabled = thresholdAnalysis.enable_custom_threshold;
		double defaultThreshold = thresholdAnalysis.threshold;
		thresholdSlider = new JSlider(0, 100, (int) (defaultThreshold*100));
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
		thresholdCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
		
		ThresholdAnalysis thresholdAnalysis = (ThresholdAnalysis) analysis;
		
		// file regex
		String chosenFileRegex = (String) fileRegexTextField.getText();
		if(chosenFileRegex == null || chosenFileRegex.isEmpty()) {
			// TODO test validity
			chosenFileRegex = thresholdAnalysis.inputFilesRegex;
		}
		thresholdAnalysis.inputFilesRegex = chosenFileRegex;
		
		// output types
		thresholdAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		thresholdAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		//heatMapAnalysis.enable_stats_output = statsCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = thresholdAnalysis.outputFolderPath;
		}
		thresholdAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = thresholdAnalysis.outputFile;
		}
		thresholdAnalysis.outputFile = chosenOuputFilePrefix;
		
		// output types
		thresholdAnalysis.enable_basic_output = outBasicCheckBox.isSelected();
		thresholdAnalysis.enable_basic_body_output = outBasicBodyCheckBox.isSelected();
		thresholdAnalysis.enable_borders_output = outBordersCheckBox.isSelected();
		thresholdAnalysis.enable_borders_body_output = outBordersBodyCheckBox.isSelected();
		
		// threshold
		thresholdAnalysis.enable_custom_threshold = thresholdCheckBox.isSelected();
		thresholdAnalysis.threshold = thresholdSlider.getValue()/100.0;
	}

}
