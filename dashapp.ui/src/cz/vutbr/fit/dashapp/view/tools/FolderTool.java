package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

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

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.view.IComponent;
import cz.vutbr.fit.dashapp.view.SideBar;

/**
 * View which contains list of dashboard files stored in particular folder.
 * 
 * @author Jiri Hynek
 *
 */
public class FolderTool extends AbstractGUITool implements IGUITool, IComponent, IPropertyChangeListener {
	
	private JScrollPane scrollPane;
	private JList<IWorkspaceFile> list;
	private ListModel listModel;
	private boolean propertyChangeDiasbled;
	private boolean valueChangedDiasbled;

	public FolderTool() {
		initModel();
		initGUI();
		initListeners();
		changeFolder(DashAppModel.getInstance().getWorkspaceFolder());
	}
	
	@Override
	public void provideSidebarItems(SideBar sideBar) {
		sideBar.addItem("folder", getComponent());
	}

	private void initModel() {
		listModel = new ListModel();
	}
	
	private void initGUI() {
		list = new JList<IWorkspaceFile>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new CellRenderrer());
		scrollPane = new JScrollPane(list);
	}
	
	private void initListeners() {
		DashAppController.getInstance().addPropertyChangeListener(this);
		// we need to use selection listener since there is problem with mouse click listener
		// list selection index might not be updated
		list.addListSelectionListener(new ListSelectionListener() {
			
			private int previousIndex = -1;
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting() && !valueChangedDiasbled) {
					int index = e.getFirstIndex();
					if(index == previousIndex) {
						index = e.getLastIndex();
					}
					if(index >= 0) {
						IWorkspaceFile selectedWorkspaceFile = null;
						if(!listModel.isEmpty() && index < listModel.size()) {
							selectedWorkspaceFile = listModel.get(index);
						}
						propertyChangeDiasbled = true;
						DashAppController.getEventManager().reloadSelectedWorkspaceFile(selectedWorkspaceFile);
						propertyChangeDiasbled = false;
						previousIndex = index;
					}
				}
			}
		});
		
		list.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// do nothing
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// do nothing
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode == KeyEvent.VK_BACK_SPACE) {
					DashAppController.getEventManager().updateWorkspaceFolder((WorkspaceFolder) listModel.get(0), false);
				} else if(keyCode == KeyEvent.VK_ENTER) {
					int selectedIndex = list.getSelectedIndex();
					if(selectedIndex >= 0) {
						IWorkspaceFile selectedSource = listModel.get(list.getSelectedIndex());
						if(selectedSource instanceof WorkspaceFolder) {
							DashAppController.getEventManager().updateWorkspaceFolder(((WorkspaceFolder) selectedSource), false);
						}
					}
				}
			}
		});
		
		list.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// do nothing
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// do nothing
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// do nothing
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// do nothing
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					IWorkspaceFile selectedSource = listModel.get(list.getSelectedIndex());
					if(selectedSource instanceof WorkspaceFolder) {
						DashAppController.getEventManager().updateWorkspaceFolder(((WorkspaceFolder) selectedSource), false);
					}
				}
			}
		});
	}
	
	private void changeFolder(WorkspaceFolder folder) {
		valueChangedDiasbled = true;
		listModel.clear();
		File folderFile = folder.getFile();
		if(folderFile.exists() && folderFile.isDirectory()) {
			// parent folder
			if(folderFile.getParentFile() != null) {
				listModel.addElement(new WorkspaceFolder(folder.getModel(), folderFile.getParentFile()));
			}
			// folder and dashboard files
			IWorkspaceFile[] children = folder.getChildren(true);
			for (IWorkspaceFile child : children) {
				listModel.addElement(child);
			}
		}
		valueChangedDiasbled = false;
	}
	
	/**
	 * Returns selected dashboard file.
	 * 
	 * @return
	 */
	public IWorkspaceFile getSelectedElement() {
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
				WorkspaceFolder newFolder = (WorkspaceFolder) e.modelChange.newValue;
				WorkspaceFolder oldFolder = (WorkspaceFolder) e.modelChange.oldValue;
				changeFolder(newFolder);
				int i = 0;
				if(newFolder.isParentOf(oldFolder)) {
					i = findWorkspaceFileIndex(oldFolder.getPath(), WorkspaceFolder.class);
					if(i < 0) {
						i = 0;
					}
				}
				list.setSelectedIndex(i);
			} else if(e.propertyKind == EventKind.FILE_SELECTION_CHANGED) {
				if(e.modelChange.newValue != null && e.modelChange.newValue instanceof DashboardFile) {
					int index = list.getNextMatch(((DashboardFile) e.modelChange.newValue).toString(), 0, Bias.Forward);
					if(index >= 0) {
						list.setSelectedIndex(index);
					}
				} else {
					list.setSelectedIndex(-1);
				}
			} else if(e.propertyKind == EventKind.DASHBOARD_STATE_CHANGED) {
				int index = list.getNextMatch(e.selectedFile.toString(), 0, Bias.Forward);
				/*if(index >= 0) {
					list.setSelectedIndex(index);
				}*/
				listModel.update(index);
			}
		}
	}
	
	private <T extends IWorkspaceFile> int findWorkspaceFileIndex(String name, Class<T> type) {
		int size = listModel.size();
		int i = 0;
		while(i < size) {
			IWorkspaceFile dashboardFile = listModel.get(i);
			if(type.isInstance(dashboardFile) && dashboardFile.toString().equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	/*@SuppressWarnings("unchecked")
	private <T extends IWorkspaceFile> T findWorkspaceFile(String name, Class<T> type) {
		int i = findWorkspaceFileIndex(name, type);
		if(i >= 0) {
			return (T) listModel.get(i);
		}
		return null;
	}*/
	
	private static class CellRenderrer extends DefaultListCellRenderer {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -2993769617735129991L;
		
		Icon iconDashboard = new ImageIcon(CellRenderrer.class.getResource("/icons/image.png"));
		Icon iconFolder = new ImageIcon(CellRenderrer.class.getResource("/icons/folder.png"));

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value instanceof DashboardFile) {
				setIcon(iconDashboard);
				if(((DashboardFile) value).getSerializedDashboard().isDirty()) {
					setText("* " + value.toString());
				} else {
					setText(value.toString());
				}
			} else if(value instanceof WorkspaceFolder) {
				setIcon(iconFolder);
				WorkspaceFolder folder = (WorkspaceFolder) value;
				if(folder.isParentOf(DashAppModel.getInstance().getWorkspaceFolder())) {
					setText("..");
				} else {
					setText(folder.getFile().getName());
				}
			}
			return this;
		}
	}
	
	private static class ListModel extends DefaultListModel<IWorkspaceFile> {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -2773975888860677050L;

		public void update(int index) {
			fireContentsChanged(this, index, index);
		}
	}

}
