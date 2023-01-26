/*
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20  PseudoFish
 * Copyright (C) 2008-12  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */
package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.SortedMap;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author hobiwan
 */
@SuppressWarnings("serial")
public class CellZoomPanel extends JPanel implements ActionListener {

	private static final int X_OFFSET = 10;
	private static final int Y_OFFSET = 33;
	private static final int SMALL_GAP = 6;
	private static final int LARGE_GAP = 14;
	private static final int COLOR_PANEL_MAX_HEIGHT = 50;
	private static final int DIFF_SIZE = 1;
	private static final String[] NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private static final int COLOR_BUTTON_COUNT = 12;
	
	private MainFrame mainFrame;
	private Font buttonFont = null;
	private Font iconFont = null;
	private int buttonFontSize = -1;
	private int defaultButtonFontSize = -1;
	private int defaultButtonHeight = -1;
	private JButton[] setValueButtons = null;
	private JButton[] toggleCandidatesButtons = null;
	private JPanel[] cellPanels = null;
	private Color normButtonForeground = null;
	private Color normButtonBackground = null;
	private SudokuPanel sudokuPanel;
	private int colorImageHeight = -1;
	private Icon[] colorKuIcons = new Icon[9];
	private boolean isInitialized = false;
	
	private javax.swing.JPanel chooseColorPanel;
	private javax.swing.JButton jFontButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JLabel setValueLabel;
	private javax.swing.JPanel setValuePanel;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel toggleCandidatesLabel;
	private javax.swing.JPanel toggleCandidatesPanel;
	private JPanel radioButtonPanel;
	private ButtonGroup radioButtonGroup;
	private JRadioButton radioButtonDefault;
	private JRadioButton radioButtonColorCells;
	private JRadioButton radioButtonColorCandidates;
	private UIColorPalette colorPalette;
	private UIColorTools colorTools;

	/**
	 * Creates new form CellZoomPanel
	 * 
	 * @param mainFrame
	 */
	public CellZoomPanel(MainFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		
		cellPanels = new JPanel[COLOR_BUTTON_COUNT];
		setValueButtons = new JButton[Sudoku2.UNITS];		
		toggleCandidatesButtons = new JButton[Sudoku2.UNITS];
		
		initComponents();
		
		JButton defaultButton = new JButton();
		normButtonForeground = defaultButton.getForeground();
		normButtonBackground = defaultButton.getBackground();

		jFontButton.setVisible(false);
		buttonFont = jFontButton.getFont();
		buttonFontSize = 11;
		defaultButtonFontSize = buttonFontSize;
		defaultButtonHeight = 23;
		iconFont = new Font(buttonFont.getName(), buttonFont.getStyle(), defaultButtonFontSize - DIFF_SIZE);

		int fontSize = 12;
		if (getFont().getSize() > 12) {
			fontSize = getFont().getSize();
		}
		
		Font font = titleLabel.getFont();
		titleLabel.setFont(new Font(font.getName(), Font.BOLD, fontSize));
		
		isInitialized = true;
		calculateLayout();
	}
	
	private JPanel createColorButtonPanel(int id) {
		
		JPanel panel = new StatusColorPanel(id);
		
		panel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				chooseCellColorPanelMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout group = new javax.swing.GroupLayout(panel);
		panel.setLayout(group);
		group.setHorizontalGroup(group.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 18, Short.MAX_VALUE));
		group.setVerticalGroup(group.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1, Short.MAX_VALUE));

		return panel;
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		titleLabel = new javax.swing.JLabel();
		setValueLabel = new javax.swing.JLabel();
		setValuePanel = new javax.swing.JPanel();
		toggleCandidatesLabel = new javax.swing.JLabel();
		toggleCandidatesPanel = new javax.swing.JPanel();
		chooseColorPanel = new javax.swing.JPanel();
		jFontButton = new javax.swing.JButton();
		
		colorPalette = new UIColorPalette(this);
		add(colorPalette);
		
		colorTools = new UIColorTools();
		add(colorTools);
		
		ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/CellZoomPanel");
		String defaultText = bundle.getString("CellZoomPanel.radioButtonDefault.text");
		String colorCandidatesText = bundle.getString("CellZoomPanel.radioButtonColorCandidates.text");
		String colorCellsText = bundle.getString("CellZoomPanel.radioButtonColorCells.text");
		
		radioButtonPanel = new JPanel(new GridLayout(3, 1));
		radioButtonPanel.setSize(130, colorPalette.getHeight());
		radioButtonGroup = new ButtonGroup();
		radioButtonDefault = new JRadioButton(defaultText);
		radioButtonDefault.setSelected(true);
		radioButtonColorCandidates = new JRadioButton(colorCandidatesText);
		radioButtonColorCells = new JRadioButton(colorCellsText);
		radioButtonDefault.addActionListener(this);
		radioButtonColorCells.addActionListener(this);
		radioButtonColorCandidates.addActionListener(this);
		radioButtonGroup.add(radioButtonDefault);
		radioButtonGroup.add(radioButtonColorCandidates);
		radioButtonGroup.add(radioButtonColorCells);
		radioButtonPanel.add(radioButtonDefault);
		radioButtonPanel.add(radioButtonColorCandidates);
		radioButtonPanel.add(radioButtonColorCells);
		add(radioButtonPanel);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));

		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized(evt);
			}
		});
		setLayout(null);

		titleLabel.setBackground(new java.awt.Color(0, 51, 255));
		titleLabel.setFont(new java.awt.Font("Tahoma", 1, 12));
		titleLabel.setForeground(new java.awt.Color(255, 255, 255));
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText(bundle.getString("CellZoomPanel.titleLabel.text"));
		titleLabel.setOpaque(true);
		add(titleLabel);
		titleLabel.setBounds(0, 0, 63, 15);
		setValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		setValueLabel.setText(bundle.getString("CellZoomPanel.setValueLabel.text"));
		add(setValueLabel);
		setValueLabel.setBounds(0, 0, 49, 14);
		setValuePanel.setLayout(new java.awt.GridLayout(3, 3));

		// initialize all set value buttons
		for (int index = 0; index < Sudoku2.UNITS; index++) {
			
			setValueButtons[index] = new JButton();
			setValueButtons[index].setText(NUMBERS[index]);
			setValueButtons[index].addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					setValueButtonActionPerformed(evt);
				}
			});
			
			setValuePanel.add(setValueButtons[index]);
		}

		add(setValuePanel);
		setValuePanel.setBounds(0, 0, 117, 69);
		toggleCandidatesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		toggleCandidatesLabel.setText(bundle.getString("CellZoomPanel.toggleCandidatesLabel.text"));
		add(toggleCandidatesLabel);
		toggleCandidatesLabel.setBounds(0, 0, 93, 14);
		toggleCandidatesPanel.setLayout(new java.awt.GridLayout(3, 3));

		// initialize all toggle candidate buttons
		for (int index = 0; index < Sudoku2.UNITS; index++) {
			
			toggleCandidatesButtons[index] = new JButton();
			toggleCandidatesButtons[index].setText(NUMBERS[index]);
			toggleCandidatesButtons[index].addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					toggleCandidatesButtonActionPerformed(evt);
				}
			});
			
			toggleCandidatesPanel.add(toggleCandidatesButtons[index]);
		}

		add(toggleCandidatesPanel);
		toggleCandidatesPanel.setBounds(0, 0, 117, 69);
		chooseColorPanel.setLayout(new java.awt.GridLayout(2, 6, 1, 1));
		
		for (int i = 0; i < COLOR_BUTTON_COUNT; i++) {
			cellPanels[i] = createColorButtonPanel(i);
		}
		
		for (int i = 0; i < COLOR_BUTTON_COUNT; i += 2) {
			chooseColorPanel.add(cellPanels[i]);
		}
		
		for (int i = 1; i < COLOR_BUTTON_COUNT; i += 2) {
			chooseColorPanel.add(cellPanels[i]);
		}

		add(chooseColorPanel);
		chooseColorPanel.setBounds(0, 0, 113, 3);

		jFontButton.setText("FontButton");
		jFontButton.setEnabled(false);
		add(jFontButton);
		jFontButton.setBounds(29, 130, 110, 23);
	}

	private void formComponentResized(java.awt.event.ComponentEvent evt) {
		calculateLayout();
		printSize();
	}

	private void setValueButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setValue((JButton) evt.getSource());
	}

	private void toggleCandidatesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		onToggleCandidate((JButton) evt.getSource());
	}

	private void chooseCellColorPanelMouseClicked(java.awt.event.MouseEvent evt) {
		handleColorChange((JPanel) evt.getSource(), evt.isControlDown());
	}

	private void onToggleCandidate(JButton button) {
		
		int candidate = -1;
		
		for (int i = 0; i < toggleCandidatesButtons.length; i++) {
			if (button == toggleCandidatesButtons[i]) {
				candidate = i + 1;
				break;
			}
		}
		
		if (sudokuPanel != null && candidate != -1) {
			if (isDefaultMouse()) {
				sudokuPanel.toggleCandidateFromCellSelection(candidate);
				mainFrame.repaint();
			} else {
				sudokuPanel.handleColoring(candidate);
			}
            toggleCandidatesButtons[candidate - 1].setEnabled(false);
            toggleCandidatesButtons[candidate - 1].setText("");
			toggleCandidatesButtons[candidate - 1].setEnabled(false);
			toggleCandidatesButtons[candidate - 1].setForeground(normButtonForeground);
			toggleCandidatesButtons[candidate - 1].setBackground(normButtonBackground);
			toggleCandidatesButtons[candidate - 1].setIcon(null);
		}
	}

	private void setValue(JButton button) {
		
		int number = -1;
		
		for (int i = 0; i < setValueButtons.length; i++) {
			if (button == setValueButtons[i]) {
				number = i + 1;
				break;
			}
		}
		
		if (sudokuPanel != null && number != -1) {
			sudokuPanel.setCellFromCellZoomPanel(number);
		}
	}

	private void handleColorChange(JPanel panel, boolean isCtrlDown) {
		
		boolean found = false;
		int colorNumber = -1;
		
		for (int i = 0; i < cellPanels.length; i++) {
			if (panel == cellPanels[i]) {
				colorNumber = i;
				found = true;
				break;
			}
		}
		
		if (colorNumber >= 0 && !isCtrlDown) {
			Color color = Options.getInstance().getColoringColors()[colorNumber];
			colorPalette.setPrimaryColor(color);
		}
		
		if (found && mainFrame != null) {
			if (isCtrlDown && colorNumber > 0) {
				if (isColoringCells()) {
					sudokuPanel.clearCellColor(Options.getInstance().getColoringColors()[colorNumber]);
				} else if (isColoringCandidates()) {
					sudokuPanel.clearCandidateColor(Options.getInstance().getColoringColors()[colorNumber]);
				}
			} else {
				if (isColoring()) {
					if (isColoringCells()) {
						mainFrame.setColoring(colorPalette.getPrimaryColor(), true);
					} else if (isColoringCandidates()) {
						mainFrame.setColoring(colorPalette.getPrimaryColor(), false);
					}
				}
			}
		}
		
		sudokuPanel.repaint();
	}

	public final void calculateLayout() {
		
		if (!isInitialized) {
			// not yet initialized!
			return;
		}
		
		int width = getWidth();
		int height = getHeight();
		int y = Y_OFFSET;

		// adjust height and width for the labels
		FontMetrics metrics = getFontMetrics(getFont());
		int textHeight = metrics.getHeight();

		// calculate widths and height of components
		// how much vertical space is actually available?
		int labelHeight = 4 * textHeight;
		int availableVert = height - Y_OFFSET - 4 * (SMALL_GAP + LARGE_GAP) - labelHeight;

		// try default sizes
		int buttonPanelHeight = availableVert * 2 / 6;
		int colorPanelHeight = availableVert / 6;
		if (colorPanelHeight > COLOR_PANEL_MAX_HEIGHT) {
			colorPanelHeight = COLOR_PANEL_MAX_HEIGHT;
		}
		
		if (buttonPanelHeight > (width - 2 * X_OFFSET)) {
			buttonPanelHeight = width - 2 * X_OFFSET;
		}
		
		// adjust color panels
		if (buttonPanelHeight < 120) {
			colorPanelHeight -= (120 - buttonPanelHeight);
			buttonPanelHeight = 120;
		}
		
		int colorPanelGesWidth = colorPanelHeight * 4;
		if (colorPanelGesWidth > width - 2 * X_OFFSET) {
			colorPanelHeight = (int) ((width - 2 * X_OFFSET) / 4.5);
		}
		
		colorPanelGesWidth = colorPanelHeight * 4;
		int newColorImageHeight = colorPanelHeight * 2 / 3;

		titleLabel.setSize(width, textHeight);
		setValueLabel.setSize(width - 2 * X_OFFSET, textHeight);
		setValueLabel.setLocation(X_OFFSET, y);
		y += textHeight;
		y += SMALL_GAP;
		setValuePanel.setSize(buttonPanelHeight, buttonPanelHeight);
		setValuePanel.setLocation((width - buttonPanelHeight) / 2, y);
		setValuePanel.doLayout();
		y += buttonPanelHeight;
		y += LARGE_GAP;
		toggleCandidatesLabel.setSize(width - 2 * X_OFFSET, textHeight);
		toggleCandidatesLabel.setLocation(X_OFFSET, y);
		y += textHeight;
		y += SMALL_GAP;
		toggleCandidatesPanel.setSize(buttonPanelHeight, buttonPanelHeight);
		toggleCandidatesPanel.setLocation((width - buttonPanelHeight) / 2, y);
		toggleCandidatesPanel.doLayout();

		y += toggleCandidatesPanel.getHeight();
		y += LARGE_GAP + SMALL_GAP;

		colorPalette.setLocation(setValuePanel.getX(), y);
		radioButtonPanel.setLocation(colorPalette.getX() + colorPalette.getWidth() + 10, colorPalette.getY());
		y += colorPalette.getHeight();
		y += LARGE_GAP;
		
		chooseColorPanel.setSize(colorPanelHeight/2*COLOR_BUTTON_COUNT/2, colorPanelHeight);
		chooseColorPanel.setLocation(setValuePanel.getX(), y);
		chooseColorPanel.doLayout();
		chooseColorPanel.setSize(colorPanelHeight/2*COLOR_BUTTON_COUNT/2, colorPanelHeight);
		colorTools.setSize(colorPanelHeight/2, colorPanelHeight/2);
		colorTools.setLocation(chooseColorPanel.getX() + chooseColorPanel.getWidth(), y);
		colorTools.doLayout();
		y += colorPanelHeight;
		y += LARGE_GAP;

		// set correct font size for buttons
		int newFontSize = defaultButtonFontSize * buttonPanelHeight / (defaultButtonHeight * 4);
		if (newFontSize > 0 && newFontSize != buttonFontSize) {

			buttonFontSize = newFontSize;
			buttonFont = new Font(buttonFont.getName(), buttonFont.getStyle(), buttonFontSize);
			iconFont = new Font(buttonFont.getName(), buttonFont.getStyle(), buttonFontSize - DIFF_SIZE);

			for (int i = 0; i < setValueButtons.length; i++) {
				setValueButtons[i].setFont(buttonFont);
				toggleCandidatesButtons[i].setFont(buttonFont);
			}
		}
		
		// ColorKu icons should be the same size as the candidate numbers
		// icons are only created, if colorKu mode is active
		if (newColorImageHeight > 0 && 
			Options.getInstance().isShowColorKuAct() && 
			newColorImageHeight != colorImageHeight) {
			
			colorImageHeight = newColorImageHeight;
			for (int i = 0; i < colorKuIcons.length; i++) {
				colorKuIcons[i] = new ImageIcon(new ColorKuImage(colorImageHeight, Options.getInstance().getColorKuColor(i + 1)));
			}
		}
		
		repaint();
	}

	/**
	 * Two modi: normal operation or coloring.<br>
	 * Normal operation:
	 * <ul>
	 * <li>activeColor has to be -1</li>
	 * <li>values and candidates contain valid choices (set of cells or single
	 * cell)</li>
	 * </ul>
	 * Coloring:
	 * <ul>
	 * <li>activeColor holds the color, colorCellOrCandidate is true for coloring
	 * cells</li>
	 * <li>buttons are only available, if a single cell is selected
	 * <li>
	 * <li>if a set of cells is selected, coloredCells and coloredCandidates are
	 * null</li>
	 * </ul>
	 * 
	 * @param values
	 * @param candidates
	 * @param aktColor
	 * @param index
	 * @param colorCellOrCandidate
	 * @param singleCell
	 * @param coloredCells
	 * @param coloredCandidates
	 */
	public void update(
			SudokuSet values, 
			SudokuSet candidates, 
			int index, 
			boolean singleCell, 
			SortedMap<Integer, Color> coloredCells,
			SortedMap<Integer, Color> coloredCandidates) {
		
		// reset all buttons
		for (int i = 0; i < setValueButtons.length; i++) {
			setValueButtons[i].setText("");
			setValueButtons[i].setEnabled(false);
			setValueButtons[i].setForeground(normButtonForeground);
			setValueButtons[i].setBackground(normButtonBackground);
			setValueButtons[i].setIcon(null);
			toggleCandidatesButtons[i].setText("");
			toggleCandidatesButtons[i].setEnabled(false);
			toggleCandidatesButtons[i].setForeground(normButtonForeground);
			toggleCandidatesButtons[i].setBackground(normButtonBackground);
			toggleCandidatesButtons[i].setIcon(null);
		}

		// now set accordingly
		if (isDefaultMouse()) {
			
			// no coloring -> buttons are available
			for (int i = 0; i < values.size(); i++) {
				int cand = values.get(i) - 1;
				if (cand >= 0 && cand <= 8) {
					if (Options.getInstance().isShowColorKuAct()) {
						setValueButtons[cand].setText(null);
						setValueButtons[cand].setIcon(colorKuIcons[cand]);
					} else {
						setValueButtons[cand].setText(NUMBERS[cand]);
						setValueButtons[cand].setIcon(null);
					}
					setValueButtons[cand].setEnabled(true);
				}
			}
			
			for (int i = 0; i < candidates.size(); i++) {
				int cand = candidates.get(i) - 1;
				if (cand >= 0 && cand <= 8) {
					if (Options.getInstance().isShowColorKuAct()) {
						toggleCandidatesButtons[cand].setText(null);
						toggleCandidatesButtons[cand].setIcon(colorKuIcons[cand]);
					} else {
						toggleCandidatesButtons[cand].setText(NUMBERS[cand]);
						toggleCandidatesButtons[cand].setIcon(null);
					}
					toggleCandidatesButtons[cand].setEnabled(true);
				}
			}
			
			ResourceBundle bundle = ResourceBundle.getBundle("intl/CellZoomPanel");
			if (singleCell) {
				toggleCandidatesLabel.setText(bundle.getString("CellZoomPanel.toggleCandidatesLabel.text"));
				for (int i = 0; i < toggleCandidatesButtons.length; i++) {
					toggleCandidatesButtons[i].setEnabled(true);
				}
			} else {
				toggleCandidatesLabel.setText(bundle.getString("CellZoomPanel.toggleCandidatesLabel.text2"));
			}
			
		} else {
			
			// coloring			
			if (coloredCells != null) {

				if (isColoringCells()) {
					for (int i = 0; i < candidates.size(); i++) {
						int cand = candidates.get(i);
						if (coloredCandidates.containsKey(index * 10 + cand)) {
							toggleCandidatesButtons[cand - 1].setForeground(getPrimaryColor());
							toggleCandidatesButtons[cand - 1].setBackground(getPrimaryColor());
							toggleCandidatesButtons[cand - 1].setIcon(createImage(colorImageHeight, getPrimaryColor(), cand));
							toggleCandidatesButtons[cand - 1].setEnabled(true);
						} else {
							toggleCandidatesButtons[cand - 1].setText(NUMBERS[cand - 1]);
							toggleCandidatesButtons[cand - 1].setEnabled(true);
						}
					}
				}
			}
		}
	}

	private ImageIcon createImage(int size, Color color, int cand) {
		
		if (size > 0) {
			
			Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			
			if (color == null) {
				color = Options.getInstance().getDefaultCellColor();
			}
			
			g.setColor(color);
			g.fillRect(0, 0, size, size);
			if (cand > 0) {
				if (Options.getInstance().isShowColorKuAct()) {
					BufferedImage cImg = new ColorKuImage(size, Options.getInstance().getColorKuColor(cand));
					g.drawImage(cImg, 0, 0, null);
				} else {
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setFont(iconFont);
					FontMetrics fm = g.getFontMetrics();
					String str = String.valueOf(cand);
					int strWidth = fm.stringWidth(str);
					int strHeight = fm.getAscent();
					g.setColor(normButtonForeground);
					g.drawString(String.valueOf(cand), (size - strWidth) / 2, (size + strHeight - 2) / 2);
				}
			}
			
			return new ImageIcon(img);
		} else {
			return null;
		}
	}

	private void printSize() {}

	public void setTitleLabelColors(Color fore, Color back) {
		titleLabel.setBackground(back);
		titleLabel.setForeground(fore);
	}

	/**
	 * @param sudokuPanel the sudokuPanel to set
	 */
	public void setSudokuPanel(SudokuPanel sudokuPanel) {
		this.sudokuPanel = sudokuPanel;
		this.colorPalette.setSudokuPanel(sudokuPanel);
		this.colorTools.setSudokuPanel(sudokuPanel);
	}
	
	public void swapColors() {
		colorPalette.swap();
		repaint();
	}
	
	public boolean isDefaultMouse() {
		return radioButtonDefault.isSelected();
	}
	
	public boolean isColoringCells() {
		return radioButtonColorCells.isSelected();
	}
	
	public boolean isColoringCandidates() {
		return radioButtonColorCandidates.isSelected();
	}
	
	public boolean isColoring() {
		return isColoringCells() || isColoringCandidates();
	}
	
	public void setDefaultMouse(boolean enable) {
		if (!radioButtonDefault.isSelected() && enable) {
			radioButtonDefault.setSelected(true);
		}
	}
	
	public void setColorCells(boolean enable) {
		if (radioButtonColorCells.isSelected() && !enable) {
			radioButtonDefault.setSelected(true);
		} else if (!radioButtonColorCells.isSelected()) {
			radioButtonDefault.setSelected(true);
		}
	}
	
	public void setColorCandidates(boolean enable) {
		if (radioButtonColorCandidates.isSelected() && !enable) {
			radioButtonDefault.setSelected(true);
		} else if (!radioButtonColorCandidates.isSelected()) {
			radioButtonDefault.setSelected(true);
		}
	}
	
	public Color getPrimaryColor() {
		return colorPalette.getPrimaryColor();
	}
	
	public Color getSecondaryColor() {
		return colorPalette.getSecondaryColor();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == radioButtonDefault) {
			mainFrame.setColoring(null, false);
		} else if (e.getSource() == radioButtonColorCells) {
			mainFrame.setColoring(getPrimaryColor(), true);
		} else if (e.getSource() == radioButtonColorCandidates) {
			mainFrame.setColoring(getPrimaryColor(), false);
		}
		
		mainFrame.check();		
		mainFrame.repaint();
	}
}
