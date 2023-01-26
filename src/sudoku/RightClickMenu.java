/*
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 * 
 * This is the menu that is displayed when the user right
 * clicks on a cell. Originally, Bernhard Hobiger embedded this
 * in the SudokuPanel, but I felt that it needed to be decoupled
 * and made into it's own proper class.
 * 
 * I dislike the circular dependency that this created, but
 * at least it fees-up a couple hundred lines from the SudokuPanel.
 */

package sudoku;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class RightClickMenu extends JPopupMenu implements ActionListener {
	
	private static final long serialVersionUID = 2320401834850980638L;
	
	private MainFrame mainFrame;
	private SudokuPanel sudokuPanel;
	
	private JMenuItem[] toggleColorItems;
	private JMenuItem[] makeItems = null;
	private JMenuItem[] excludeItems = null;
	
	private javax.swing.JMenuItem color1aMenuItem;
	private javax.swing.JMenuItem color1bMenuItem;
	private javax.swing.JMenuItem color2aMenuItem;
	private javax.swing.JMenuItem color2bMenuItem;
	private javax.swing.JMenuItem color3aMenuItem;
	private javax.swing.JMenuItem color3bMenuItem;
	private javax.swing.JMenuItem color4aMenuItem;
	private javax.swing.JMenuItem color4bMenuItem;
	private javax.swing.JMenuItem color5aMenuItem;
	private javax.swing.JMenuItem color5bMenuItem;
	private javax.swing.JMenuItem deleteValueMenuItem;
	private javax.swing.JPopupMenu deleteValuePopupMenu;
	private javax.swing.JMenuItem exclude1MenuItem;
	private javax.swing.JMenuItem exclude2MenuItem;
	private javax.swing.JMenuItem exclude3MenuItem;
	private javax.swing.JMenuItem exclude4MenuItem;
	private javax.swing.JMenuItem exclude5MenuItem;
	private javax.swing.JMenuItem exclude6MenuItem;
	private javax.swing.JMenuItem exclude7MenuItem;
	private javax.swing.JMenuItem exclude8MenuItem;
	private javax.swing.JMenuItem exclude9MenuItem;
	private javax.swing.JMenuItem excludeSeveralMenuItem;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JMenuItem make1MenuItem;
	private javax.swing.JMenuItem make2MenuItem;
	private javax.swing.JMenuItem make3MenuItem;
	private javax.swing.JMenuItem make4MenuItem;
	private javax.swing.JMenuItem make5MenuItem;
	private javax.swing.JMenuItem make6MenuItem;
	private javax.swing.JMenuItem make7MenuItem;
	private javax.swing.JMenuItem make8MenuItem;
	private javax.swing.JMenuItem make9MenuItem;
	
	public RightClickMenu(MainFrame mainFrame, SudokuPanel sudokuPanel) {
		
		super();
		
		this.mainFrame = mainFrame;
		this.sudokuPanel = sudokuPanel;
		
		initialize();
		
		makeItems = new JMenuItem[] { 
			make1MenuItem, make2MenuItem, make3MenuItem, 
			make4MenuItem, make5MenuItem, make6MenuItem, 
			make7MenuItem, make8MenuItem, make9MenuItem
		};
		
		excludeItems = new JMenuItem[] {
			exclude1MenuItem, exclude2MenuItem, exclude3MenuItem,
			exclude4MenuItem, exclude5MenuItem, exclude6MenuItem,
			exclude7MenuItem, exclude8MenuItem, exclude9MenuItem
		};
		
		toggleColorItems = new JMenuItem[] {
			color1aMenuItem, color1bMenuItem, color2aMenuItem,
			color2bMenuItem, color3aMenuItem, color3bMenuItem,
			color4aMenuItem, color4bMenuItem, color5aMenuItem,
			color5bMenuItem
		};
		
		setColorIconsInPopupMenu();
	}
	
	private void initialize() {
		
		make1MenuItem = new javax.swing.JMenuItem();
		make2MenuItem = new javax.swing.JMenuItem();
		make3MenuItem = new javax.swing.JMenuItem();
		make4MenuItem = new javax.swing.JMenuItem();
		make5MenuItem = new javax.swing.JMenuItem();
		make6MenuItem = new javax.swing.JMenuItem();
		make7MenuItem = new javax.swing.JMenuItem();
		make8MenuItem = new javax.swing.JMenuItem();
		make9MenuItem = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		exclude1MenuItem = new javax.swing.JMenuItem();
		exclude2MenuItem = new javax.swing.JMenuItem();
		exclude3MenuItem = new javax.swing.JMenuItem();
		exclude4MenuItem = new javax.swing.JMenuItem();
		exclude5MenuItem = new javax.swing.JMenuItem();
		exclude6MenuItem = new javax.swing.JMenuItem();
		exclude7MenuItem = new javax.swing.JMenuItem();
		exclude8MenuItem = new javax.swing.JMenuItem();
		exclude9MenuItem = new javax.swing.JMenuItem();
		excludeSeveralMenuItem = new javax.swing.JMenuItem();
		jSeparator2 = new javax.swing.JSeparator();
		color1aMenuItem = new javax.swing.JMenuItem();
		color1bMenuItem = new javax.swing.JMenuItem();
		color2aMenuItem = new javax.swing.JMenuItem();
		color2bMenuItem = new javax.swing.JMenuItem();
		color3aMenuItem = new javax.swing.JMenuItem();
		color3bMenuItem = new javax.swing.JMenuItem();
		color4aMenuItem = new javax.swing.JMenuItem();
		color4bMenuItem = new javax.swing.JMenuItem();
		color5aMenuItem = new javax.swing.JMenuItem();
		color5bMenuItem = new javax.swing.JMenuItem();
		deleteValuePopupMenu = new javax.swing.JPopupMenu();
		deleteValueMenuItem = new javax.swing.JMenuItem();

		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/SudokuPanel");
		make1MenuItem.setText(bundle.getString("SudokuPanel.popup.make1"));
		make1MenuItem.addActionListener(this);
		this.add(make1MenuItem);

		make2MenuItem.setText(bundle.getString("SudokuPanel.popup.make2"));
		make2MenuItem.addActionListener(this);
		this.add(make2MenuItem);

		make3MenuItem.setText(bundle.getString("SudokuPanel.popup.make3"));
		make3MenuItem.addActionListener(this);
		this.add(make3MenuItem);

		make4MenuItem.setText(bundle.getString("SudokuPanel.popup.make4"));
		make4MenuItem.addActionListener(this);
		this.add(make4MenuItem);

		make5MenuItem.setText(bundle.getString("SudokuPanel.popup.make5"));
		make5MenuItem.addActionListener(this);
		this.add(make5MenuItem);

		make6MenuItem.setText(bundle.getString("SudokuPanel.popup.make6"));
		make6MenuItem.addActionListener(this);
		this.add(make6MenuItem);

		make7MenuItem.setText(bundle.getString("SudokuPanel.popup.make7"));
		make7MenuItem.addActionListener(this);
		this.add(make7MenuItem);

		make8MenuItem.setText(bundle.getString("SudokuPanel.popup.make8"));
		make8MenuItem.addActionListener(this);
		this.add(make8MenuItem);

		make9MenuItem.setText(bundle.getString("SudokuPanel.popup.make9"));
		make9MenuItem.addActionListener(this);
		this.add(make9MenuItem);
		this.add(jSeparator1);

		exclude1MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude1"));
		exclude1MenuItem.addActionListener(this);
		this.add(exclude1MenuItem);

		exclude2MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude2"));
		exclude2MenuItem.addActionListener(this);
		this.add(exclude2MenuItem);

		exclude3MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude3"));
		exclude3MenuItem.addActionListener(this);
		this.add(exclude3MenuItem);

		exclude4MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude4"));
		exclude4MenuItem.addActionListener(this);
		this.add(exclude4MenuItem);

		exclude5MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude5"));
		exclude5MenuItem.addActionListener(this);
		this.add(exclude5MenuItem);

		exclude6MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude6"));
		exclude6MenuItem.addActionListener(this);
		this.add(exclude6MenuItem);

		exclude7MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude7"));
		exclude7MenuItem.addActionListener(this);
		this.add(exclude7MenuItem);

		exclude8MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude8"));
		exclude8MenuItem.addActionListener(this);
		this.add(exclude8MenuItem);

		exclude9MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude9"));
		exclude9MenuItem.addActionListener(this);
		this.add(exclude9MenuItem);

		excludeSeveralMenuItem.setText(bundle.getString("SudokuPanel.popup.several"));
		excludeSeveralMenuItem.addActionListener(this);
		this.add(excludeSeveralMenuItem);
		this.add(jSeparator2);

		color1aMenuItem.setText(bundle.getString("SudokuPanel.popup.color1a"));
		color1aMenuItem.addActionListener(this);
		this.add(color1aMenuItem);

		color1bMenuItem.setText(bundle.getString("SudokuPanel.popup.color1b"));
		color1bMenuItem.addActionListener(this);
		this.add(color1bMenuItem);

		color2aMenuItem.setText(bundle.getString("SudokuPanel.popup.color2a"));
		color2aMenuItem.addActionListener(this);
		this.add(color2aMenuItem);

		color2bMenuItem.setText(bundle.getString("SudokuPanel.popup.color2b"));
		color2bMenuItem.addActionListener(this);
		this.add(color2bMenuItem);

		color3aMenuItem.setText(bundle.getString("SudokuPanel.popup.color3a"));
		color3aMenuItem.addActionListener(this);
		this.add(color3aMenuItem);

		color3bMenuItem.setText(bundle.getString("SudokuPanel.popup.color3b"));
		color3bMenuItem.addActionListener(this);
		this.add(color3bMenuItem);

		color4aMenuItem.setText(bundle.getString("SudokuPanel.popup.color4a"));
		color4aMenuItem.addActionListener(this);
		this.add(color4aMenuItem);

		color4bMenuItem.setText(bundle.getString("SudokuPanel.popup.color4b"));
		color4bMenuItem.addActionListener(this);
		this.add(color4bMenuItem);

		color5aMenuItem.setText(bundle.getString("SudokuPanel.popup.color5a"));
		color5aMenuItem.addActionListener(this);
		this.add(color5aMenuItem);

		color5bMenuItem.setText(bundle.getString("SudokuPanel.popup.color5b"));
		color5bMenuItem.addActionListener(this);
		this.add(color5bMenuItem);

		deleteValueMenuItem.setText(bundle.getString("SudokuPanel.deleteValueItem.text"));
		deleteValueMenuItem.addActionListener(this);
		deleteValuePopupMenu.add(deleteValueMenuItem);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == make1MenuItem || e.getSource() == make2MenuItem ||
			e.getSource() == make3MenuItem || e.getSource() == make4MenuItem ||
			e.getSource() == make5MenuItem || e.getSource() == make6MenuItem ||
			e.getSource() == make7MenuItem || e.getSource() == make8MenuItem ||
			e.getSource() == make9MenuItem) {
			popupSetCell((JMenuItem) e.getSource());
			this.setVisible(false);
			this.sudokuPanel.repaint();
			return;
		}
		
		if (e.getSource() == exclude1MenuItem || e.getSource() == exclude2MenuItem ||
			e.getSource() == exclude3MenuItem || e.getSource() == exclude4MenuItem ||
			e.getSource() == exclude5MenuItem || e.getSource() == exclude6MenuItem ||
			e.getSource() == exclude7MenuItem || e.getSource() == exclude8MenuItem ||
			e.getSource() == exclude9MenuItem) {
			popupExcludeCandidate((JMenuItem) e.getSource());
			this.setVisible(false);
			this.sudokuPanel.repaint();
			return;
		}
		
		if (e.getSource() == excludeSeveralMenuItem) {
			excludeSeveralMenuItemActionPerformed(e);
			this.setVisible(false);
			this.sudokuPanel.repaint();
			return;
		}
		
		if (e.getSource() == color1aMenuItem || e.getSource() == color1bMenuItem ||
			e.getSource() == color2aMenuItem || e.getSource() == color2bMenuItem ||
			e.getSource() == color3aMenuItem || e.getSource() == color3bMenuItem ||
			e.getSource() == color4aMenuItem || e.getSource() == color4bMenuItem ||
			e.getSource() == color5aMenuItem || e.getSource() == color5bMenuItem) {
			popupToggleColor((JMenuItem) e.getSource());
			this.sudokuPanel.repaint();
			return;
		}
		
		if (e.getSource() == deleteValueMenuItem) {
			popupDeleteValueFromCell();
			this.setVisible(false);
			this.sudokuPanel.repaint();
			return;
		}
	}
	
	/**
	 * Creates an icon (rectangle showing color) and sets it on the MenuItem.
	 *
	 * @param item
	 * @param color
	 */
	public void setColorIconInPopupMenu(JMenuItem item, Color color, boolean colorKu) {
		
		if (color == null) {
			// delete the icon
			item.setIcon(null);
			return;
		}
		
		try {
			
			BufferedImage img = null;
			
			if (colorKu) {
				img = new ColorKuImage(12, color);
			} else {
				img = ImageIO.read(getClass().getResource("/img/c_icon.png"));
				Graphics2D gImg = (Graphics2D) img.getGraphics();
				gImg.setColor(color);
				gImg.fillRect(1, 1, 12, 12);
			}
			
			item.setIcon(new ImageIcon(img));
			
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error setting color icons in popup menu", ex);
		}
	}
	
	/**
	 * Sets all the color icons in the popup menu.
	 */
	public final void setColorIconsInPopupMenu() {
		setColorIconInPopupMenu(color1aMenuItem, Options.getInstance().getColoringColors()[0], false);
		setColorIconInPopupMenu(color1bMenuItem, Options.getInstance().getColoringColors()[1], false);
		setColorIconInPopupMenu(color2aMenuItem, Options.getInstance().getColoringColors()[2], false);
		setColorIconInPopupMenu(color2bMenuItem, Options.getInstance().getColoringColors()[3], false);
		setColorIconInPopupMenu(color3aMenuItem, Options.getInstance().getColoringColors()[4], false);
		setColorIconInPopupMenu(color3bMenuItem, Options.getInstance().getColoringColors()[5], false);
		setColorIconInPopupMenu(color4aMenuItem, Options.getInstance().getColoringColors()[6], false);
		setColorIconInPopupMenu(color4bMenuItem, Options.getInstance().getColoringColors()[7], false);
		setColorIconInPopupMenu(color5aMenuItem, Options.getInstance().getColoringColors()[8], false);
		setColorIconInPopupMenu(color5bMenuItem, Options.getInstance().getColoringColors()[9], false);
	}

	public void setColorkuInPopupMenu(boolean on) {
		
		if (on) {
			
			setColorIconInPopupMenu(make1MenuItem, Options.getInstance().getColorKuColor(1), true);
			setColorIconInPopupMenu(make2MenuItem, Options.getInstance().getColorKuColor(2), true);
			setColorIconInPopupMenu(make3MenuItem, Options.getInstance().getColorKuColor(3), true);
			setColorIconInPopupMenu(make4MenuItem, Options.getInstance().getColorKuColor(4), true);
			setColorIconInPopupMenu(make5MenuItem, Options.getInstance().getColorKuColor(5), true);
			setColorIconInPopupMenu(make6MenuItem, Options.getInstance().getColorKuColor(6), true);
			setColorIconInPopupMenu(make7MenuItem, Options.getInstance().getColorKuColor(7), true);
			setColorIconInPopupMenu(make8MenuItem, Options.getInstance().getColorKuColor(8), true);
			setColorIconInPopupMenu(make9MenuItem, Options.getInstance().getColorKuColor(9), true);

			setColorIconInPopupMenu(exclude1MenuItem, Options.getInstance().getColorKuColor(1), true);
			setColorIconInPopupMenu(exclude2MenuItem, Options.getInstance().getColorKuColor(2), true);
			setColorIconInPopupMenu(exclude3MenuItem, Options.getInstance().getColorKuColor(3), true);
			setColorIconInPopupMenu(exclude4MenuItem, Options.getInstance().getColorKuColor(4), true);
			setColorIconInPopupMenu(exclude5MenuItem, Options.getInstance().getColorKuColor(5), true);
			setColorIconInPopupMenu(exclude6MenuItem, Options.getInstance().getColorKuColor(6), true);
			setColorIconInPopupMenu(exclude7MenuItem, Options.getInstance().getColorKuColor(7), true);
			setColorIconInPopupMenu(exclude8MenuItem, Options.getInstance().getColorKuColor(8), true);
			setColorIconInPopupMenu(exclude9MenuItem, Options.getInstance().getColorKuColor(9), true);

			excludeSeveralMenuItem.setEnabled(false);
			
		} else {
			
			setColorIconInPopupMenu(make1MenuItem, null, false);
			setColorIconInPopupMenu(make2MenuItem, null, false);
			setColorIconInPopupMenu(make3MenuItem, null, false);
			setColorIconInPopupMenu(make4MenuItem, null, false);
			setColorIconInPopupMenu(make5MenuItem, null, false);
			setColorIconInPopupMenu(make6MenuItem, null, false);
			setColorIconInPopupMenu(make7MenuItem, null, false);
			setColorIconInPopupMenu(make8MenuItem, null, false);
			setColorIconInPopupMenu(make9MenuItem, null, false);

			setColorIconInPopupMenu(exclude1MenuItem, null, false);
			setColorIconInPopupMenu(exclude2MenuItem, null, false);
			setColorIconInPopupMenu(exclude3MenuItem, null, false);
			setColorIconInPopupMenu(exclude4MenuItem, null, false);
			setColorIconInPopupMenu(exclude5MenuItem, null, false);
			setColorIconInPopupMenu(exclude6MenuItem, null, false);
			setColorIconInPopupMenu(exclude7MenuItem, null, false);
			setColorIconInPopupMenu(exclude8MenuItem, null, false);
			setColorIconInPopupMenu(exclude9MenuItem, null, false);

			excludeSeveralMenuItem.setEnabled(true);
		}
	}
	
	/**
	 * Handles activation of an "Exclude x" menu item. The selected number is
	 * deleted in all selected cells (if they present).
	 *
	 * @param menuItem
	 */
	public void popupExcludeCandidate(JMenuItem menuItem) {
		
		int candidate = -1;
		for (int i = 0; i < excludeItems.length; i++) {
			if (excludeItems[i] == menuItem) {
				candidate = i + 1;
				break;
			}
		}
		
		if (candidate != -1) {
			
			sudokuPanel.pushUndo();
			boolean changed = sudokuPanel.removeCandidateFromCellSelection(candidate);
			
			if (changed) {
				sudokuPanel.clearRedoStack();
				sudokuPanel.checkProgress();
			} else {
				sudokuPanel.popUndo();
			}
			
			sudokuPanel.updateCellZoomPanel();
			mainFrame.check();
			mainFrame.fixFocus();
			repaint();
		}
	}
	
	/**
	 * Handles activation of an "Toggle color x" menu item. Th color is set in the
	 * cell if not present or deleted if already present.
	 *
	 * @param menuItem
	 */
	public void popupToggleColor(JMenuItem menuItem) {
		
		Color color = null;
		for (int i = 0; i < toggleColorItems.length; i++) {
			if (toggleColorItems[i] == menuItem) {
				color = Options.getInstance().getColoringColors()[i];
				break;
			}
		}
		
		// coloring is active
		if (color != null) {
			int aktRow = sudokuPanel.getActiveRow();
			int aktCol = sudokuPanel.getActiveCol();
			sudokuPanel.handleColoring(aktRow, aktCol, -1, color);
			sudokuPanel.updateCellZoomPanel();
			mainFrame.check();
			repaint();
		}
	}
	
	/**
	 * Brings up the popup menu for the cell at row/col. If the cell is already
	 * set, a different menu is displayed, that allowsto delete the value from the
	 * cell. For every other cell the contents of the menu is restricted to sensible
	 * actions.<br>
	 * If a region of cells is selected, "Make x" is restricted to candidates, that
	 * appear in all cells, "Exclude x" is restricted to the combined set of
	 * candidates in all cells.
	 *
	 * @param row
	 * @param col
	 */
	public void showPopupMenu(int row, int col, int cellSize) {
		
		if (!Sudoku2.isValidIndex(row, col)) {
			return;
		}
		
		boolean isSingleCell = 
				sudokuPanel.getSelectedCells().isEmpty() ||
				sudokuPanel.getSelectedCells().size() == 1 &&
				sudokuPanel.getSelectedCells().get(0).intValue() == Sudoku2.getIndex(row, col);
		
		jSeparator2.setVisible(true);
		if (sudokuPanel.getSudoku().getValue(row, col) != 0 && isSingleCell) {
			
			// cell is already set -> delete value popup (not for givens!)			
			if (!sudokuPanel.getSudoku().isFixed(row, col)) {
				deleteValuePopupMenu.show(sudokuPanel, sudokuPanel.getX(row, col) + cellSize, sudokuPanel.getY(row, col));
			}
			
			return;
		}
		
		excludeSeveralMenuItem.setVisible(false);
		for (int i = 1; i <= 9; i++) {
			makeItems[i - 1].setVisible(false);
			excludeItems[i - 1].setVisible(false);
		}
		
		SudokuSet candSet = sudokuPanel.collectCandidates(true);
		for (int i = 0; i < candSet.size(); i++) {
			makeItems[candSet.get(i) - 1].setVisible(true);
		}
		
		candSet = sudokuPanel.collectCandidates(false);
		if (candSet.size() > 1) {
			
			if (candSet.size() > 2) {
				excludeSeveralMenuItem.setVisible(true);
			}
			
			for (int i = 0; i < candSet.size(); i++) {
				excludeItems[candSet.get(i) - 1].setVisible(true);
			}
			
		} else {
			jSeparator2.setVisible(false);
		}
		
		this.show(sudokuPanel, sudokuPanel.getX(row, col) + cellSize, sudokuPanel.getY(row, col));
	}
	
	/**
	 * Handles activation of a "Make x" menu item. The selected number is set in all
	 * selected cells (if they are not already set).
	 *
	 * @param menuItem
	 */
	public void popupSetCell(JMenuItem menuItem) {
		
		int candidate = -1;
		for (int i = 0; i < makeItems.length; i++) {
			if (makeItems[i] == menuItem) {
				candidate = i + 1;
				break;
			}
		}
		
		int aktRow = sudokuPanel.getActiveRow();
		int aktCol = sudokuPanel.getActiveCol();
		
		if (candidate != -1) {
			
			sudokuPanel.pushUndo();
			boolean changed = false;
			if (sudokuPanel.getSelectedCells().isEmpty()) {
				if (sudokuPanel.getSudoku().getValue(aktRow, aktCol) == 0) {
					sudokuPanel.setCell(aktRow, aktCol, candidate);
					changed = true;
				}
			} else {
				for (int index : sudokuPanel.getSelectedCells()) {
					if (sudokuPanel.getSudoku().getValue(index) == 0) {
						sudokuPanel.getSudoku().setCell(index, candidate);
						changed = true;
					}
				}
			}
			
			if (changed) {
				sudokuPanel.clearRedoStack();
				sudokuPanel.checkProgress();
			} else {
				sudokuPanel.popUndo();
			}
			
			sudokuPanel.updateCellZoomPanel();
			mainFrame.check();
			mainFrame.fixFocus();
			repaint();
		}
	}

	/**
	 * Deletes the value from the active cell.
	 */
	public void popupDeleteValueFromCell() {
		
		sudokuPanel.pushUndo();
		boolean changed = false;
		int aktRow = sudokuPanel.getActiveRow();
		int aktCol = sudokuPanel.getActiveCol();
		
		if (sudokuPanel.getSudoku().getValue(aktRow, aktCol) != 0 && !sudokuPanel.getSudoku().isFixed(aktRow, aktCol)) {
			sudokuPanel.getSudoku().setCell(aktRow, aktCol, 0);
			changed = true;
		}
		
		if (changed) {
			sudokuPanel.clearRedoStack();
			sudokuPanel.checkProgress();
		} else {
			sudokuPanel.popUndo();
		}
		
		sudokuPanel.updateCellZoomPanel();
		mainFrame.fixFocus();
		mainFrame.check();
		repaint();
	}
	
	public void excludeSeveralMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		String input = JOptionPane.showInputDialog(this,
			ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.cmessage"),
			ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.ctitle"),
			JOptionPane.QUESTION_MESSAGE
		);
		
		if (input != null) {
			sudokuPanel.pushUndo();
			boolean changed = false;
			for (int i = 0; i < input.length(); i++) {
				char digit = input.charAt(i);
				if (Character.isDigit(digit)) {
					if (sudokuPanel.removeCandidateFromCellSelection(Character.getNumericValue(digit))) {
						changed = true;
					}
				}
			}
			
			if (changed) {
				sudokuPanel.clearRedoStack();
				sudokuPanel.checkProgress();
			} else {
				sudokuPanel.popUndo();
			}
			
			sudokuPanel.updateCellZoomPanel();
			mainFrame.check();
			repaint();
		}
	}
	
	public void deleteValuePopup(int row, int col, int cellSize) {
		deleteValuePopupMenu.show(this, sudokuPanel.getX(row, col) + cellSize, sudokuPanel.getY(row, col));
	}
}
