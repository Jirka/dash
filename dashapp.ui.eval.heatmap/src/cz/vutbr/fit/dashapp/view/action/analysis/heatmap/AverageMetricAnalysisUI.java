package cz.vutbr.fit.dashapp.view.action.analysis.heatmap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.eval.analysis.heatmap.AverageMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;

/**
 * UI part of analysis which provides additional settings dialog.
 * 
 * @author Jiri Hynek
 *
 */
public class AverageMetricAnalysisUI extends FolderAnalysisUI {
	
	private MetricType[] availableMetricTypes;
	private JTextField inputFileTextField;
	private JTextField outputPathTextField;
	private JCheckBox metricsCheckBox;
	private JCheckBox outBasicCheckBox;
	private JCheckBox outBasicBodyCheckBox;
	private JButton metricsButton;
	private List<MetricType> selectedMetricTypes;
	private JSlider filterSlider;
	private JCheckBox printMeanCheckBox;
	private JCheckBox printVarianceCheckBox;
	private JCheckBox printStdevCheckBox;
	private JCheckBox printMinCheckBox;
	private JCheckBox printMaxCheckBox;
	private JCheckBox cacheCheckBox;

	public AverageMetricAnalysisUI(MetricType[] availableMetricTypes) {
		super(new AverageMetricAnalysis());
		this.availableMetricTypes = availableMetricTypes;
	}
	
	@Override
	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		AverageMetricAnalysis averageMetricAnalysis = (AverageMetricAnalysis) analysis;
		
		// input files
		panel.add(new JLabel("Input files regex:"));
		inputFileTextField = new JTextField(averageMetricAnalysis.inputFilesRegex);
		panel.add(inputFileTextField);
		
		// output 'all' relative path
		panel.add(new JLabel("Ouput 'all' relative path:"));
		outputPathTextField = new JTextField(averageMetricAnalysis.outputFolderPath);
		panel.add(outputPathTextField);
		
		// output types
		outBasicCheckBox = new JCheckBox("basic", averageMetricAnalysis.enable_basic_output);
		panel.add(outBasicCheckBox);
		outBasicBodyCheckBox = new JCheckBox("body", averageMetricAnalysis.enable_basic_body_output);
		panel.add(outBasicBodyCheckBox);
		
		// metrics
		selectedMetricTypes = averageMetricAnalysis.metricTypes;
		metricsButton = new JButton(selectedMetricTypes.size() + " metrics selected");
		metricsButton.setEnabled(averageMetricAnalysis.enable_custom_metrics);
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
		metricsCheckBox = new JCheckBox("Custom metrics", averageMetricAnalysis.enable_custom_metrics);
		metricsCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == metricsCheckBox) {
					metricsButton.setEnabled(metricsCheckBox.isSelected());
				}
			}
		});
		panel.add(metricsCheckBox);
		panel.add(metricsButton);
		
		// filter extreme values
		panel.add(new JLabel("Filter extreme values:"));
		int defaultFilterAmount = averageMetricAnalysis.filter_extreme_results;
		filterSlider = new JSlider(0, 20, defaultFilterAmount);
		filterSlider.setMajorTickSpacing(5);
		filterSlider.setMinorTickSpacing(1);
		filterSlider.setPaintTicks(true);
		filterSlider.setPaintLabels(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( filterSlider.getMinimum() ), new JLabel(Integer.toString(filterSlider.getMinimum())) );
		JLabel actThresholdLabel = new JLabel((defaultFilterAmount < 10 ? "  " : "") + Integer.toString(defaultFilterAmount));
		labelTable.put( new Integer( filterSlider.getMaximum()/2 ), actThresholdLabel );
		labelTable.put( new Integer( filterSlider.getMaximum() ), new JLabel(Integer.toString(filterSlider.getMaximum())) );
		filterSlider.setLabelTable(labelTable);
		filterSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == filterSlider) {
					actThresholdLabel.setText(Integer.toString((((JSlider) e.getSource()).getValue())));
					actThresholdLabel.repaint();
				}
			}
		});
		panel.add(filterSlider);
		
		// stats output
		printMeanCheckBox = new JCheckBox("mean", averageMetricAnalysis.print_mean);
		panel.add(printMeanCheckBox);
		printVarianceCheckBox = new JCheckBox("variance", averageMetricAnalysis.print_variance);
		panel.add(printVarianceCheckBox);
		printStdevCheckBox = new JCheckBox("stdev", averageMetricAnalysis.print_stdev);
		panel.add(printStdevCheckBox);
		printMinCheckBox = new JCheckBox("min", averageMetricAnalysis.print_min);
		panel.add(printMinCheckBox);
		printMaxCheckBox = new JCheckBox("max", averageMetricAnalysis.print_max);
		panel.add(printMaxCheckBox);
		
		// cache
		cacheCheckBox = new JCheckBox("Enable image cache", averageMetricAnalysis.enable_image_cache);
		cacheCheckBox.setEnabled(false);
		panel.add(cacheCheckBox);
	}
	
	@Override
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		AverageMetricAnalysis averageMetricAnalysis = (AverageMetricAnalysis) analysis;
		
		// input files
		String chosenInputFileRegex = (String) inputFileTextField.getText();
		if(chosenInputFileRegex == null || chosenInputFileRegex.isEmpty()) {
			// TODO test validity
			chosenInputFileRegex = averageMetricAnalysis.inputFilesRegex;
		}
		averageMetricAnalysis.inputFilesRegex = chosenInputFileRegex;
		
		// output 'all' relative path
		String chosenOuputPath = (String) outputPathTextField.getText();
		if(chosenOuputPath == null || chosenOuputPath.isEmpty()) {
			// TODO test validity
			if(chosenOuputPath.endsWith("/")) {
				chosenOuputPath.substring(0, chosenOuputPath.length()-1);
			}
			chosenOuputPath = averageMetricAnalysis.outputFolderPath;
		}
		averageMetricAnalysis.outputFolderPath = chosenOuputPath;
		
		// output types
		averageMetricAnalysis.enable_basic_output = outBasicCheckBox.isSelected();
		averageMetricAnalysis.enable_basic_body_output = outBasicBodyCheckBox.isSelected();
		
		// metrics
		averageMetricAnalysis.enable_custom_metrics = metricsCheckBox.isSelected();
		averageMetricAnalysis.metricTypes = selectedMetricTypes;
		
		// filter extreme values
		averageMetricAnalysis.filter_extreme_results = filterSlider.getValue();
		
		// stats output
		averageMetricAnalysis.print_mean = printMeanCheckBox.isSelected();
		averageMetricAnalysis.print_variance = printVarianceCheckBox.isSelected();
		averageMetricAnalysis.print_stdev = printStdevCheckBox.isSelected();
		averageMetricAnalysis.print_min = printMinCheckBox.isSelected();
		averageMetricAnalysis.print_max = printMaxCheckBox.isSelected();
		
		// cache
		averageMetricAnalysis.enable_image_cache = cacheCheckBox.isSelected();
	}

}
