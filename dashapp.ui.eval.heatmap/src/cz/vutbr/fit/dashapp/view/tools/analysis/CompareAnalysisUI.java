package cz.vutbr.fit.dashapp.view.tools.analysis;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.CompareAnalysis;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class CompareAnalysisUI extends FolderAnalysisUI {
	
	private JTextField file1TextField;
	private JTextField file2TextField;
	private JCheckBox actFolderOutputCheckBox;
	private JCheckBox allFolderOutputCheckBox;
	private JCheckBox statsCheckBox;
	private JTextField outputPathTextField;
	private JTextField outputFilePrefixTextField;
	

	public CompareAnalysisUI() {
		super(new CompareAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		CompareAnalysis compareAnalysis = (CompareAnalysis) analysis;
		
		// input files
		panel.add(new JLabel("Input file 1:"));
		file1TextField = new JTextField(compareAnalysis.inputFile1);
		panel.add(file1TextField);
		
		panel.add(new JLabel("Input file 2:"));
		file2TextField = new JTextField(compareAnalysis.inputFile2);
		panel.add(file2TextField);
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", compareAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", compareAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		statsCheckBox = new JCheckBox("Stats output", compareAnalysis.enable_stats_output);
		panel.add(statsCheckBox);
		panel.add(new JLabel());
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(compareAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(compareAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		CompareAnalysis compareAnalysis = (CompareAnalysis) analysis;
		
		// input files
		String chosenFile1 = (String) file1TextField.getText();
		if(chosenFile1 == null || chosenFile1.isEmpty()) {
			// TODO test validity
			chosenFile1 = compareAnalysis.inputFile1;
		}
		compareAnalysis.inputFile1 = chosenFile1;
		
		String chosenFile2 = (String) file2TextField.getText();
		if(chosenFile2 == null || chosenFile2.isEmpty()) {
			// TODO test validity
			chosenFile2 = compareAnalysis.inputFile2;
		}
		compareAnalysis.inputFile2 = chosenFile2;
		
		// output types
		compareAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		compareAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		compareAnalysis.enable_stats_output = statsCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = compareAnalysis.outputFolderPath;
		}
		compareAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = compareAnalysis.outputFile;
		}
		compareAnalysis.outputFile = chosenOuputFilePrefix;
	}

}
