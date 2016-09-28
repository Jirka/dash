package cz.vutbr.fit.dash.view.tools;

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

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.controller.EventManager.EventKind;
import cz.vutbr.fit.dash.controller.PropertyChangeEvent;
import cz.vutbr.fit.dash.controller.IPropertyChangeListener;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.SerializedDashboard;
import cz.vutbr.fit.dash.view.MenuBar;
import cz.vutbr.fit.dash.view.ToolBar;

/**
 * Undo and redo action support.
 * 
 * @author Jiri Hynek
 *
 */
public class HistoryTool extends AbstractGUITool implements IGUITool, IPropertyChangeListener {
	
	protected Map<Dashboard, History> historyCache;
	protected int HISTORY_LIMIT = 20;
	
	protected boolean userBrowsingHistory;
	
	private List<AbstractButton> btnsUndo;
	private List<AbstractButton> btnsRedo;
	
	public HistoryTool() {
		historyCache = new HashMap<Dashboard, History>();
		userBrowsingHistory = false;
		btnsUndo = new ArrayList<>();
		btnsRedo = new ArrayList<>();
		DashAppController.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Edit");
		
		// undo
		AbstractButton btn = menuBar.addItem(subMenu, "Undo", new HistoryAction(HistoryAction.UNDO));
		btn.setEnabled(false);
		btnsUndo.add(btn);
		
		// redo
		btn = menuBar.addItem(subMenu, "Redo", new HistoryAction(HistoryAction.REDO));
		btn.setEnabled(false);
		btnsRedo.add(btn);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		
		// undo
        AbstractButton btn = toolbar.addButton("Undo", "/icons/Undo.png", new HistoryAction(HistoryAction.UNDO), 0);
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setEnabled(false);
        btnsUndo.add(btn);
        
        // redo
		btn = toolbar.addButton("Redo", "/icons/Redo.png", new HistoryAction(HistoryAction.REDO), 0);
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
		enableButtons(btnsUndo, history.canUndo());
		enableButtons(btnsRedo, history.canRedo());
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
	
	private History getHistory(Dashboard selectedDashboard) {
		History history = historyCache.get(selectedDashboard);
		if(history == null) {
			history = new History(HISTORY_LIMIT);
			historyCache.put(selectedDashboard, history);
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
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			History history = getHistory(selectedDashboard);
			if(history != null) {
				SerializedDashboard sd = selectedDashboard.getSerializedDashboard();
				userBrowsingHistory = true;
				try {
					DashAppController.getEventManager().updateDashboardXml(selectedDashboard, history.undo(sd.getXml()));
				} finally {
					userBrowsingHistory = false;
				}
				updateButtons(history);
			}
		}

		/**
		 * Redo action.
		 */
		public void redo() {
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			History history = getHistory(selectedDashboard);
			if(history != null) {
				SerializedDashboard sd = selectedDashboard.getSerializedDashboard();
				userBrowsingHistory = true;
				try {
					DashAppController.getEventManager().updateDashboardXml(selectedDashboard, history.redo(sd.getXml()));
				} finally {
					userBrowsingHistory = false;
				}
				updateButtons(history);
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
			Dashboard updatedDashboard = e.selectedDashboard;
			History history = getHistory(updatedDashboard);
			history.save((String) e.xmlChange.oldValue);
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			if(selectedDashboard != null && selectedDashboard == updatedDashboard) {
				updateButtons(history);
			}
		} else if(e.propertyKind == EventKind.DASHBOARD_SELECTION_CHANGED) {
			updateButtons(getHistory(e.selectedDashboard));
		}
	}

}
