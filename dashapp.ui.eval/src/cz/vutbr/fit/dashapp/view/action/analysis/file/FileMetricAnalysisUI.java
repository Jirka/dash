package cz.vutbr.fit.dashapp.view.action.analysis.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.vutbr.fit.dashapp.eval.analysis.file.FileMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.view.action.analysis.FileAnalysisUI;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FileMetricAnalysisUI extends FileAnalysisUI {
	
	private MetricType[] availableMetricTypes;
	private JCheckBox metricsCheckBox;
	private JButton metricsButton;
	private List<MetricType> selectedMetricTypes;
	
	public FileMetricAnalysisUI(MetricType[] availableMetricTypes) {
		super(new FileMetricAnalysis());
		this.availableMetricTypes = availableMetricTypes;
	}

	protected void getCustomSettings(JPanel panel) {
		super.getCustomSettings(panel);
		
		FileMetricAnalysis simpleMetricAnalysis = (FileMetricAnalysis) analysis;
		
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
	
	protected void processCustomSettings() {
		super.processCustomSettings();
		
		FileMetricAnalysis simpleMetricAnalysis = (FileMetricAnalysis) analysis;
		
		// metrics
		simpleMetricAnalysis.enable_custom_metrics = metricsCheckBox.isSelected();
		simpleMetricAnalysis.metricTypes = selectedMetricTypes;
	}

}
