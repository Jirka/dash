package cz.vutbr.fit.dashapp.view.action.analysis.heatmap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.EntropyAnalysis;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class EntropyAnalysisUI extends FolderAnalysisUI {
	
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
	

	public EntropyAnalysisUI() {
		super(new EntropyAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		EntropyAnalysis entropyAnalysis = (EntropyAnalysis) analysis;
		
		// file regex
		panel.add(new JLabel("Input files regex:"));
		fileRegexTextField = new JTextField(entropyAnalysis.inputFilesRegex);
		panel.add(fileRegexTextField);
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", entropyAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", entropyAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		statsCheckBox = new JCheckBox("Stats output", entropyAnalysis.enable_stats_output);
		panel.add(statsCheckBox);
		panel.add(new JLabel());
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(entropyAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(entropyAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// output types
		outBasicCheckBox = new JCheckBox("Full with borders", entropyAnalysis.enable_basic_output);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("Body with borders", entropyAnalysis.enable_basic_body_output);
		panel.add(outBasicBodyCheckBox);
		outBordersCheckBox = new JCheckBox("Full without borders", entropyAnalysis.enable_borders_output);
		panel.add(outBordersCheckBox);
		outBordersBodyCheckBox = new JCheckBox("Body without borders", entropyAnalysis.enable_borders_body_output);
		panel.add(outBordersBodyCheckBox);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		EntropyAnalysis entropyAnalysis = (EntropyAnalysis) analysis;
		
		// file regex
		String chosenFileRegex = (String) fileRegexTextField.getText();
		if(chosenFileRegex == null || chosenFileRegex.isEmpty()) {
			// TODO test validity
			chosenFileRegex = entropyAnalysis.inputFilesRegex;
		}
		entropyAnalysis.inputFilesRegex = chosenFileRegex;
		
		// output types
		entropyAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		entropyAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		entropyAnalysis.enable_stats_output = statsCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = entropyAnalysis.outputFolderPath;
		}
		entropyAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = entropyAnalysis.outputFile;
		}
		entropyAnalysis.outputFile = chosenOuputFilePrefix;
		
		// output types
		entropyAnalysis.enable_basic_output = outBasicCheckBox.isSelected();
		entropyAnalysis.enable_basic_body_output = outBasicBodyCheckBox.isSelected();
		entropyAnalysis.enable_borders_output = outBordersCheckBox.isSelected();
		entropyAnalysis.enable_borders_body_output = outBordersBodyCheckBox.isSelected();
	}

}
