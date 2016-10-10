package cz.vutbr.fit.dashapp.view.tools.canvas;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.util.PaintUtil;

public abstract class AbstractCanvasTool extends AbstractGUITool implements MouseListener, MouseMotionListener, KeyListener {
	
	protected boolean requiresSeparator;
	protected boolean isDefault;
	protected ButtonGroup buttonGroup;
	protected PaintUtil paintUtil;
	
	public AbstractCanvasTool() {
		this(false, false, null);
	}
	
	public AbstractCanvasTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		this.isDefault = isDefault;
		this.requiresSeparator = requiresSeparator;
		this.buttonGroup = buttonGroup;
	}
	
	public void init(Canvas canvas) {
		super.init(canvas);
		if(isDefault) {
			canvas.setActiveCanvasTool(this);
		}
		this.paintUtil = new PaintUtil(canvas);
	}
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(requiresSeparator) {
			toolbar.addSeparator();
		}
        JToggleButton btn = toolbar.addToggleButton(getLabel(), getImage(), new SurfaceToolSwitchAction(), 0);
        if(buttonGroup != null) {
        	btn.getModel().setGroup(buttonGroup);
            if(isDefault) {
            	buttonGroup.setSelected(btn.getModel(), true);
            }
        }
	}
	
	public void firePropertyChange(PropertyChangeEvent e) {
		// do nothing
	}

	protected abstract String getLabel();
	
	protected abstract String getTooltip();
	
	protected abstract String getImage();

	private class SurfaceToolSwitchAction extends AbstractAction {

		/**
		 * UID 
		 */
		private static final long serialVersionUID = 5963443114469200675L;

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.setActiveCanvasTool(AbstractCanvasTool.this);
			toolSelected(e);
		}
	}
	
	protected void toolSelected(ActionEvent e) {
		// do nothing
	}
	
	public void resetSelections() {
		// do nothing
	};

	@Override
	public void keyPressed(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing
	}
	
	public void paintComponent(Graphics2D g) {
		// do nothing
	}
}