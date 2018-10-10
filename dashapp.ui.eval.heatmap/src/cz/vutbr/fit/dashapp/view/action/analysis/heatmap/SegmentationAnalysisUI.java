package cz.vutbr.fit.dashapp.view.action.analysis.heatmap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.SegmentationAnalysis;
import cz.vutbr.fit.dashapp.segmenation.SegmentationType;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

public class SegmentationAnalysisUI extends FolderAnalysisUI {
	
	private SegmentationType[] availableSegmentationTypes;
	private JCheckBox actFolderOutputCheckBox;
	private JCheckBox allFolderOutputCheckBox;
	private JCheckBox debugCheckBox;
	private JTextField outputPathTextField;
	private JTextField outputFilePrefixTextField;
	private JCheckBox outBasicCheckBox;
	private JCheckBox outBasicBodyCheckBox;
	private List<SegmentationType> selectedSegmentationTypes;
	private JButton segmentationsButton;
	private JCheckBox segmentationCheckBox;

	public SegmentationAnalysisUI(SegmentationType[] availableSegmentationTypes) {
		super(new SegmentationAnalysis());
		this.availableSegmentationTypes = availableSegmentationTypes;
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		SegmentationAnalysis segmentationAnalysis = (SegmentationAnalysis) analysis;
		
		// output destinations
		actFolderOutputCheckBox = new JCheckBox("Act folder output", segmentationAnalysis.enable_act_folder_output);
		panel.add(actFolderOutputCheckBox);
		allFolderOutputCheckBox = new JCheckBox("All folder output", segmentationAnalysis.enable_all_folder_output);
		panel.add(allFolderOutputCheckBox);
		debugCheckBox = new JCheckBox("Debug output", segmentationAnalysis.enable_debug_output);
		panel.add(debugCheckBox);
		panel.add(new JLabel());
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(segmentationAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output files prefix
		panel.add(new JLabel("Ouput files prefix:"));
		outputFilePrefixTextField = new JTextField(segmentationAnalysis.outputFile);
		panel.add(outputFilePrefixTextField);
		
		// input types
		outBasicCheckBox = new JCheckBox("basic", segmentationAnalysis.enable_basic_input);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("body", segmentationAnalysis.enable_basic_body_input);
		panel.add(outBasicBodyCheckBox);
		
		// segmentation types
		selectedSegmentationTypes = segmentationAnalysis.segmentationTypes;
		segmentationsButton = new JButton(selectedSegmentationTypes.size() + " algorigthms selected");
		segmentationsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == segmentationsButton) {
					DefaultListModel<SegmentationType> model = new DefaultListModel<>();
					JList<SegmentationType> list = new JList<>();
					list.setModel(model);
					// remove selected which are not available
					boolean found;
					for (SegmentationType selectedSegmentationType : selectedSegmentationTypes.toArray(new SegmentationType[selectedSegmentationTypes.size()])) {
						found = false;
						for (SegmentationType availableSegmentationType : availableSegmentationTypes) {
							if(availableSegmentationType == selectedSegmentationType) {
								found = true;
								break;
							}
						}
						if(!found) {
							selectedSegmentationTypes.remove(selectedSegmentationType);
						}
					}
					// select selected
					int i = 0;
					for (SegmentationType availableSegmentationType : availableSegmentationTypes) {
						model.addElement(availableSegmentationType);
						if(selectedSegmentationTypes.contains(availableSegmentationType)) {
							list.addSelectionInterval(i, i);
						}
						i++;
					}
					JScrollPane scrollPane = new JScrollPane(list);
					int option = JOptionPane.showConfirmDialog(null, scrollPane, analysis + " Settings", JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION) {
						selectedSegmentationTypes = list.getSelectedValuesList();
						segmentationsButton.setText(selectedSegmentationTypes.size() + " algorigthms selected");
					}
				}
			}
		});
		segmentationCheckBox = new JCheckBox("Custom segmentations", segmentationAnalysis.enable_custom_segmentations);
		segmentationCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == segmentationCheckBox) {
					segmentationsButton.setEnabled(segmentationCheckBox.isSelected());
				}
			}
		});
		panel.add(segmentationCheckBox);
		panel.add(segmentationsButton);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		SegmentationAnalysis segmentationAnalysis = (SegmentationAnalysis) analysis;
		
		// output types
		segmentationAnalysis.enable_act_folder_output = actFolderOutputCheckBox.isSelected();
		segmentationAnalysis.enable_all_folder_output = allFolderOutputCheckBox.isSelected();
		segmentationAnalysis.enable_debug_output = debugCheckBox.isSelected();
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = segmentationAnalysis.outputFolderPath;
		}
		segmentationAnalysis.outputFolderPath = chosenOuputPath;
		
		// output file prefix
		String chosenOuputFilePrefix = (String) outputFilePrefixTextField.getText();
		if(chosenOuputFilePrefix == null || chosenOuputFilePrefix.isEmpty()) {
			// TODO test validity
			chosenOuputFilePrefix = segmentationAnalysis.outputFile;
		}
		segmentationAnalysis.outputFile = chosenOuputFilePrefix;
		
		// input types
		segmentationAnalysis.enable_basic_input = outBasicCheckBox.isSelected();
		segmentationAnalysis.enable_basic_body_input = outBasicBodyCheckBox.isSelected();
		
		// segmentation types
		segmentationAnalysis.enable_custom_segmentations = segmentationCheckBox.isSelected();
		segmentationAnalysis.segmentationTypes = selectedSegmentationTypes;
		
		
	}

}
