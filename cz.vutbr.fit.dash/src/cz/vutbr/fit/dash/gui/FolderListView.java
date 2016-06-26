package cz.vutbr.fit.dash.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position.Bias;

import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.DashboardFile;
import cz.vutbr.fit.dash.model.PropertyChangeEvent;
import cz.vutbr.fit.dash.model.PropertyChangeListener;
import cz.vutbr.fit.dash.model.SerializedDashboard;
import cz.vutbr.fit.dash.util.DashboardFileFilter;

public class FolderListView implements IComponent, PropertyChangeListener {
	
	private JScrollPane scrollPane;
	private JList<DashboardFile> list;
	private ListModel listModel;
	private boolean propertyChangeDiasbled;

	public FolderListView() {
		initModel();
		initGUI();
		initListeners();
	}

	private void initModel() {
		listModel = new ListModel();
	}
	
	private void initGUI() {
		list = new JList<DashboardFile>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new CellRenderrer());
		scrollPane = new JScrollPane(list);
	}
	
	private void initListeners() {
		DashAppModel.getInstance().addPropertyChangeListener(this);
		list.addListSelectionListener(new ListSelectionListener() {
			
			private int previousIndex = -1;
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					int index = e.getFirstIndex();
					if(index == previousIndex) {
						index = e.getLastIndex();
					}
					if(index >= 0) {
						DashboardFile selectedDashboardFile = null;
						if(!listModel.isEmpty() && index < listModel.size()) {
							selectedDashboardFile = listModel.get(index);
						}
						propertyChangeDiasbled = true;
						DashAppModel.getInstance().setSelectedDashboard(selectedDashboardFile);
						propertyChangeDiasbled = false;
						previousIndex = index;
					}
				}
			}
		});
	}

	@Override
	public JComponent getComponent() {
		return scrollPane;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(!propertyChangeDiasbled) {
			if(e.propertyKind == PropertyKind.FOLDER_PATH) {
				listModel.clear();
				File folder = new File((String) e.newValue);
				if(folder.exists() && folder.isDirectory()) {
					File[] files = folder.listFiles(new DashboardFileFilter());
					Arrays.sort(files);
					if(files != null) {
						for (File file : files) {
							String name = file.getName();
							int dotPosition = name.lastIndexOf('.');
							name = name.substring(0, dotPosition);
							DashboardFile dashboardFile = findDashboardFile(name);
							if(dashboardFile != null) {
								dashboardFile.setFile(file);
							} else {
								listModel.addElement(new DashboardFile(file));
							}
						}
					}
				}
			} else if(e.propertyKind == PropertyKind.DASHBOARD_SELECTION) {
				int index = list.getNextMatch(((Dashboard) e.newValue).getDashboardFile().toString(), 0, Bias.Forward);
				if(index >= 0) {
					list.setSelectedIndex(index);
				}
			} else if(e.propertyKind == PropertyKind.IS_DIRTY) {
				int index = list.getNextMatch(((SerializedDashboard) e.newValue).getDashboard().getDashboardFile().toString(), 0, Bias.Forward);
				if(index >= 0) {
					list.setSelectedIndex(index);
				}
				listModel.update(index);
			}
		}
	}
	
	private DashboardFile findDashboardFile(String name) {
		int size = listModel.size();
		int i = 0;
		while(i < size) {
			DashboardFile dashboardFile = listModel.get(i);
			if(dashboardFile.toString().equals(name)) {
				return dashboardFile;
			}
			i++;
		}
		return null;
	}
	
	private static class CellRenderrer extends DefaultListCellRenderer {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -2993769617735129991L;
		
		Icon icon = new ImageIcon(CellRenderrer.class.getResource("/icons/image.png"));

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value instanceof DashboardFile) {
				setIcon(icon);
				Dashboard dashboard = ((DashboardFile) value).getDashboard();
				if(dashboard != null) {
					if(dashboard.getSerializedDashboard().isDirty()) {
						setText("* " + value.toString());
					} else {
						setText(value.toString());
					}
				}
			}
			return this;
		}
	}
	
	private static class ListModel extends DefaultListModel<DashboardFile> {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -2773975888860677050L;

		public void update(int index) {
			fireContentsChanged(this, index, index);
		}
	}

}
