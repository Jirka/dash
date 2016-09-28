package cz.vutbr.fit.dash.view.tools;

import java.awt.Component;
import java.io.File;
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

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.controller.EventManager.EventKind;
import cz.vutbr.fit.dash.controller.PropertyChangeEvent;
import cz.vutbr.fit.dash.controller.IPropertyChangeListener;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.DashboardFile;
import cz.vutbr.fit.dash.util.DashboardFileFilter;
import cz.vutbr.fit.dash.view.IComponent;
import cz.vutbr.fit.dash.view.SideBar;

/**
 * View which contains list of dashboard files stored in particular folder.
 * 
 * @author Jiri Hynek
 *
 */
public class FolderTool extends AbstractGUITool implements IGUITool, IComponent, IPropertyChangeListener {
	
	private JScrollPane scrollPane;
	private JList<DashboardFile> list;
	private ListModel listModel;
	private boolean propertyChangeDiasbled;

	public FolderTool() {
		initModel();
		initGUI();
		initListeners();
		changeFolder(DashAppModel.getInstance().getFolderPath());
	}
	
	@Override
	public void provideSidebarItems(SideBar sideBar) {
		sideBar.addItem("folder", getComponent());
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
		DashAppController.getInstance().addPropertyChangeListener(this);
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
						DashAppController.getEventManager().updateSelectedDashboard(selectedDashboardFile);
						propertyChangeDiasbled = false;
						previousIndex = index;
					}
				}
			}
		});
	}
	
	private void changeFolder(String folderPath) {
		listModel.clear();
		File folder = new File(folderPath);
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
	}
	
	/**
	 * Returns selected dashboard file.
	 * 
	 * @return
	 */
	public DashboardFile getSelectedElement() {
		return list.getSelectedValue();
	}
	
	/**
	 * Updates dashboard file selection.
	 * 
	 * @param dashboardFile
	 */
	public void setSelectedElement(DashboardFile dashboardFile) {
		list.setSelectedValue(dashboardFile, false);
	}

	@Override
	public JComponent getComponent() {
		return scrollPane;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(!propertyChangeDiasbled) {
			if(e.propertyKind == EventKind.FOLDER_PATH_CHANGED) {
				changeFolder((String) e.modelChange.newValue);
			} else if(e.propertyKind == EventKind.DASHBOARD_SELECTION_CHANGED) {
				int index = list.getNextMatch(((Dashboard) e.modelChange.newValue).getDashboardFile().toString(), 0, Bias.Forward);
				if(index >= 0) {
					list.setSelectedIndex(index);
				}
			} else if(e.propertyKind == EventKind.DASHBOARD_STATE_CHANGED) {
				int index = list.getNextMatch(e.selectedDashboard.getDashboard().getDashboardFile().toString(), 0, Bias.Forward);
				/*if(index >= 0) {
					list.setSelectedIndex(index);
				}*/
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
