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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.FolderMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class FolderMetricAnalysisUI extends FolderAnalysisUI {
	
	private MetricType[] availableMetricTypes;
	private JTextField inputFileTextField;
	private JTextField outputPathTextField;
	private JCheckBox metricsCheckBox;
	private JButton metricsButton;
	private List<MetricType> selectedMetricTypes;

	public FolderMetricAnalysisUI(MetricType[] availableMetricTypes) {
		super(new FolderMetricAnalysis());
		this.availableMetricTypes = availableMetricTypes;
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		FolderMetricAnalysis simpleMetricAnalysis = (FolderMetricAnalysis) analysis;
		
		// input files
		panel.add(new JLabel("Input file:"));
		inputFileTextField = new JTextField(simpleMetricAnalysis.inputFile);
		panel.add(inputFileTextField);
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(simpleMetricAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// metrics
		selectedMetricTypes = simpleMetricAnalysis.metricTypes;
		metricsButton = new JButton(selectedMetricTypes.size() + " metrics selected");
		metricsButton.setEnabled(simpleMetricAnalysis.enable_custom_metrics);
		metricsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == metricsButton) {
					DefaultListModel<MetricType> model = new DefaultListModel<>();
					JList<MetricType> list = new JList<>();
					list.setModel(model);
					// remove selected which are not available
					boolean found;
					for (MetricType selectedMetricType : selectedMetricTypes.toArray(new MetricType[selectedMetricTypes.size()])) {
						found = false;
						for (MetricType availableMetricType : availableMetricTypes) {
							if(availableMetricType == selectedMetricType) {
								found = true;
								break;
							}
						}
						if(!found) {
							selectedMetricTypes.remove(selectedMetricType);
						}
					}
					// select selected
					int i = 0;
					for (MetricType availableMetricType : availableMetricTypes) {
						model.addElement(availableMetricType);
						if(selectedMetricTypes.contains(availableMetricType)) {
							list.addSelectionInterval(i, i);
						}
						i++;
					}
					JScrollPane scrollPane = new JScrollPane(list);
					int option = JOptionPane.showConfirmDialog(null, scrollPane, analysis + " Settings", JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION) {
						selectedMetricTypes = list.getSelectedValuesList();
						metricsButton.setText(selectedMetricTypes.size() + " metrics selected");
					}
				}
			}
		});
		metricsCheckBox = new JCheckBox("Custom metrics", simpleMetricAnalysis.enable_custom_metrics);
		metricsCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == metricsCheckBox) {
					metricsButton.setEnabled(metricsCheckBox.isSelected());
				}
			}
		});
		panel.add(metricsCheckBox);
		panel.add(metricsButton);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		FolderMetricAnalysis simpleMetricAnalysis = (FolderMetricAnalysis) analysis;
		
		// input files
		String chosenInputFile = (String) inputFileTextField.getText();
		if(chosenInputFile == null || chosenInputFile.isEmpty()) {
			// TODO test validity
			chosenInputFile = simpleMetricAnalysis.inputFile;
		}
		simpleMetricAnalysis.inputFile = chosenInputFile;
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = simpleMetricAnalysis.outputFolderPath;
		}
		simpleMetricAnalysis.outputFolderPath = chosenOuputPath;
		
		// metrics
		simpleMetricAnalysis.enable_custom_metrics = metricsCheckBox.isSelected();
		simpleMetricAnalysis.metricTypes = selectedMetricTypes;
	}

}
