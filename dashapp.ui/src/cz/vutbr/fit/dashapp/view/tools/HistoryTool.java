package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.model.SerializedDashboard;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

/**
 * Undo and redo action support.
 * 
 * @author Jiri Hynek
 *
 */
public class HistoryTool extends AbstractGUITool implements IGUITool, IPropertyChangeListener {
	
	protected Map<IWorkspaceFile, History> historyCache;
	protected int HISTORY_LIMIT = 20;
	
	protected boolean userBrowsingHistory;
	
	private List<AbstractButton> btnsUndo;
	private List<AbstractButton> btnsRedo;
	
	private static final String LABEL_UNDO = "Undo";
	private static final String LABEL_REDO = "Redo";
	
	public HistoryTool() {
		this(false);
	}
	
	public HistoryTool(boolean addSeparator) {
		super(addSeparator);
		historyCache = new HashMap<IWorkspaceFile, History>();
		userBrowsingHistory = false;
		btnsUndo = new ArrayList<>();
		btnsRedo = new ArrayList<>();
		DashAppController.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Edit");
		
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		
		// undo
		AbstractButton btn = menuBar.addItem(subMenu, LABEL_UNDO, new HistoryAction(HistoryAction.UNDO));
		btn.setEnabled(false);
		btnsUndo.add(btn);
		
		// redo
		btn = menuBar.addItem(subMenu, LABEL_REDO, new HistoryAction(HistoryAction.REDO));
		btn.setEnabled(false);
		btnsRedo.add(btn);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		
		// undo
        AbstractButton btn = toolbar.addButton(LABEL_UNDO, "/icons/Undo.png", new HistoryAction(HistoryAction.UNDO), 0);
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setEnabled(false);
        btnsUndo.add(btn);
        
        // redo
		btn = toolbar.addButton(LABEL_REDO, "/icons/Redo.png", new HistoryAction(HistoryAction.REDO), 0);
		btn.setPreferredSize(new Dimension(24, 24));
		btn.setEnabled(false);
		btnsRedo.add(btn);
	}
	
	/**
	 * Updates button according to history state.
	 * 
	 * @param history
	 */
	private void updateButtons(History history) {
		if(history != null) {
			enableButtons(btnsUndo, history.canUndo());
			enableButtons(btnsRedo, history.canRedo());
		}
	}
	
	/**
	 * Enables/disables selected button.
	 * 
	 * @param buttons
	 * @param enable
	 */
	public void enableButtons(List<AbstractButton> buttons, boolean enable) {
		for (AbstractButton button : buttons) {
			button.setEnabled(enable);
		}
	}
	
	private History getHistory(IWorkspaceFile workspaceFile) {
		History history = historyCache.get(workspaceFile);
		if(history == null && workspaceFile instanceof DashboardFile) {
			history = new History(HISTORY_LIMIT);
			historyCache.put(workspaceFile, history);
		}
		return history;
	}
	
	public class HistoryAction extends AbstractAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -6014412330042783023L;
		
		public static final int UNDO = 0;
		public static final int REDO = 1;
		
		private int kind;
		
		public HistoryAction(int kind) {
			this.kind = kind;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(kind == UNDO) {
				undo();
			} else if(kind == REDO) {
				redo();
			}
		}
		
		/**
		 * Undo action.
		 */
		public void undo() {
			DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
			if(selectedDashboardFile != null) {
				History history = getHistory(selectedDashboardFile);
				if(history != null) {
					SerializedDashboard sd = (selectedDashboardFile).getSerializedDashboard();
					userBrowsingHistory = true;
					try {
						DashAppController.getEventManager().updateDashboardXml(selectedDashboardFile, history.undo(sd.getXml()));
					} finally {
						userBrowsingHistory = false;
					}
					updateButtons(history);
				}
			}
		}

		/**
		 * Redo action.
		 */
		public void redo() {
			DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
			if(selectedDashboardFile != null) {
				History history = getHistory(selectedDashboardFile);
				if(history != null) {
					SerializedDashboard sd = selectedDashboardFile.getSerializedDashboard();
					userBrowsingHistory = true;
					try {
						DashAppController.getEventManager().updateDashboardXml(selectedDashboardFile, history.redo(sd.getXml()));
					} finally {
						userBrowsingHistory = false;
					}
					updateButtons(history);
				}
			}
		}
	}
	
	/**
	 * Representation of dashboard history
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class History {
		private Stack<String> undo;
		private Stack<String> redo;
		private int limit;
		
		public History(int limit) {
			this.limit = limit;
			undo = new Stack<String>();
			redo = new Stack<String>();
		}
		
		public boolean canUndo() {
			return !undo.isEmpty();
		}
		
		public boolean canRedo() {
			return !redo.isEmpty();
		}
		
		public void save(String text) {
			undo.push(text);
			if(undo.size() > limit) {
				undo.remove(0);
			}
			redo.clear();
		}
		
		public String undo(String actualString) {
			if(canUndo()) {
				redo.push(actualString);
				return undo.pop();
			} else {
				return actualString;
			}
			
		}
		
		public String redo(String actualString) {
			if(canRedo()) {
				undo.push(actualString);
				return redo.pop();
			} else {
				return actualString;
			}
		}
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(!userBrowsingHistory && EventKind.isModelChanged(e) && e.xmlChange != null) {
			IWorkspaceFile updatedFile = e.selectedFile;
			History history = getHistory(updatedFile);
			if(history != null) {
				history.save((String) e.xmlChange.oldValue);
				DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
				if(selectedDashboardFile != null && selectedDashboardFile == updatedFile) {
					updateButtons(history);
				}
			}
		} else if(e.propertyKind == EventKind.FILE_SELECTION_CHANGED) {
			updateButtons(getHistory(e.selectedFile));
		}
	}

}
