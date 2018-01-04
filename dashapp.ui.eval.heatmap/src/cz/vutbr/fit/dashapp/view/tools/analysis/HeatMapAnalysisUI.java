package cz.vutbr.fit.dashapp.view.tools.analysis;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.HeatMapAnalysis;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class HeatMapAnalysisUI extends FolderAnalysisUI {
	
	private JTextField fileRegexTextField;
	private JCheckBox actFolderOutputCheckBox;
	private JCheckBox allFolderOutputCheckBox;
	private JCheckBox statsCheckBox;
	private JTextField outputPathTextField;
	private JTextField outputFilePrefixTextField;
	private JCheckBox outBasicCheckBox;
	private JCheckBox outBasicBodyCheckBox;
	private JCheckBox outBordersCheckBox;
	private JCheckBox outBordersBodyCheckBox;
	

	public HeatMapAnalysisUI() {
		super(new HeatMapAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		HeatMapAnalysis heatMapAnalysis = (HeatMapAnalysis) analysis;
		
		// file regex
		panel.add(new JLabel("Input files regex:"));
		fileRegexTextField = new JTextField(heatMapAnalysis.inputFilesRegex);
		panel.add(fileRegexTextField);
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", heatMapAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", heatMapAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		statsCheckBox = new JCheckBox("Stats output", heatMapAnalysis.enable_stats_output);
		panel.add(statsCheckBox);
		panel.add(new JLabel());
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(heatMapAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(heatMapAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// output types
		outBasicCheckBox = new JCheckBox("Full with borders", heatMapAnalysis.enable_basic_output);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("Body with borders", heatMapAnalysis.enable_basic_body_output);
		panel.add(outBasicBodyCheckBox);
		outBordersCheckBox = new JCheckBox("Full without borders", heatMapAnalysis.enable_borders_output);
		panel.add(outBordersCheckBox);
		outBordersBodyCheckBox = new JCheckBox("Body without borders", heatMapAnalysis.enable_borders_body_output);
		panel.add(outBordersBodyCheckBox);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		HeatMapAnalysis heatMapAnalysis = (HeatMapAnalysis) analysis;
		
		// file regex
		String chosenFileRegex = (String) fileRegexTextField.getText();
		if(chosenFileRegex == null || chosenFileRegex.isEmpty()) {
			// TODO test validity
			chosenFileRegex = heatMapAnalysis.inputFilesRegex;
		}
		heatMapAnalysis.inputFilesRegex = chosenFileRegex;
		
		// output types
		heatMapAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		heatMapAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		heatMapAnalysis.enable_stats_output = statsCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = heatMapAnalysis.outputFolderPath;
		}
		heatMapAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = heatMapAnalysis.outputFile;
		}
		heatMapAnalysis.outputFile = chosenOuputFilePrefix;
		
		// output types
		heatMapAnalysis.enable_basic_output = outBasicCheckBox.isSelected();
		heatMapAnalysis.enable_basic_body_output = outBasicBodyCheckBox.isSelected();
		heatMapAnalysis.enable_borders_output = outBordersCheckBox.isSelected();
		heatMapAnalysis.enable_borders_body_output = outBordersBodyCheckBox.isSelected();
	}

}
