package cz.vutbr.fit.dashapp.view.tools.analysis;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.CompareAllAnalysis;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class CompareAllAnalysisUI extends FolderAnalysisUI {
	
	private JTextField fileRefTextField;
	private JTextField fileRegexTextField;
	private JTextField outputPathTextField;
	private JTextField outputFilePrefixTextField;
	private JCheckBox printAvgCheckBox;
	private JCheckBox printAllCheckBox;

	public CompareAllAnalysisUI() {
		super(new CompareAllAnalysis());
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		CompareAllAnalysis compareAllAnalysis = (CompareAllAnalysis) analysis;
		
		// input files
		panel.add(new JLabel("Reference input file:"));
		fileRefTextField = new JTextField(compareAllAnalysis.inputFileRef);
		panel.add(fileRefTextField);
		
		// file regex
		panel.add(new JLabel("Input files regex:"));
		fileRegexTextField = new JTextField(compareAllAnalysis.inputFilesRegex);
		panel.add(fileRegexTextField);
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(compareAllAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(compareAllAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// print average value or all diffs
		printAvgCheckBox = new JCheckBox("Print average values", compareAllAnalysis.printAverageOutput);
		panel.add(printAvgCheckBox);
		
		// print average value or all diffs
		printAllCheckBox = new JCheckBox("Print all values", compareAllAnalysis.printAllOutput);
		panel.add(printAllCheckBox);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		CompareAllAnalysis compareAllAnalysis = (CompareAllAnalysis) analysis;
		
		// input files
		String chosenFile1 = (String) fileRefTextField.getText();
		if(chosenFile1 == null || chosenFile1.isEmpty()) {
			// TODO test validity
			chosenFile1 = compareAllAnalysis.inputFileRef;
		}
		compareAllAnalysis.inputFileRef = chosenFile1;
		
		// file regex
		String chosenFileRegex = (String) fileRegexTextField.getText();
		if(chosenFileRegex == null || chosenFileRegex.isEmpty()) {
			// TODO test validity
			chosenFileRegex = compareAllAnalysis.inputFilesRegex;
		}
		compareAllAnalysis.inputFilesRegex = chosenFileRegex;
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = compareAllAnalysis.outputFolderPath;
		}
		compareAllAnalysis.outputFolderPath = chosenOuputPath;
		
		// output 'all' relative path
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = compareAllAnalysis.outputFile;
		}
		compareAllAnalysis.outputFile = chosenOuputFilePrefix;
		
		// print average value or all diffs
		compareAllAnalysis.printAverageOutput = printAvgCheckBox.isSelected();
		compareAllAnalysis.printAllOutput = printAllCheckBox.isSelected();
	}

}
