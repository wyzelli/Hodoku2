/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class UIColorPalette extends JPanel implements MouseListener, ActionListener {

	private static final long serialVersionUID = -7096778906107607575L;
	
	private static final int DEFAULT_BUTTON_SIZE = 42;
	private static final int PANEL_SIZE = DEFAULT_BUTTON_SIZE + DEFAULT_BUTTON_SIZE/2;	

	private UIBorderedImagePanel primaryColor;
	private UIBorderedImagePanel secondaryColor;
	private UIBorderedImagePanel switchColor;
	private UIBorderedImagePanel resetButton;
	private JDialog dialog;
	private JButton dialogButtonOK;
	private JButton dialogButtonCancel;
	private JButton dialogButtonReset;
	private Color initialColor;
	private Color selectedColor;
	private JColorChooser colorChooser;
	private SudokuPanel sudokuPanel;
	private CellZoomPanel cellZoomPanel;
	
	UIColorPalette(CellZoomPanel cellZoomPanel) {
		
		super(true);
		
		this.setSize(PANEL_SIZE, PANEL_SIZE);
		this.setLayout(null);
		
		this.cellZoomPanel = cellZoomPanel;
		
		ResourceBundle bundle = ResourceBundle.getBundle("intl/UIColorPalette");
		
		dialog = null;
		dialogButtonOK = new JButton(bundle.getString("dialogButtonOK.text"));
		dialogButtonCancel = new JButton(bundle.getString("dialogButtonCancel.text"));
		dialogButtonReset = new JButton(bundle.getString("dialogButtonReset.text"));
		initialColor = Color.white;
		selectedColor = Color.white;
		colorChooser = new JColorChooser(initialColor);
		
		dialogButtonOK.addActionListener(this);
		dialogButtonCancel.addActionListener(this);
		dialogButtonReset.addActionListener(this);
		
		primaryColor = new UIBorderedImagePanel();
		primaryColor.setBackground(Options.DEFAULT_PRIMARY_COLOR);
		primaryColor.setSize(DEFAULT_BUTTON_SIZE, DEFAULT_BUTTON_SIZE);
		primaryColor.setLocation(0, 0);
		primaryColor.setToolTipText(bundle.getString("primaryColor.tooltip"));
		primaryColor.addMouseListener(this);
		add(primaryColor);
		
		secondaryColor = new UIBorderedImagePanel();
		secondaryColor.setBackground(Options.DEFAULT_SECONDARY_COLOR);
		secondaryColor.setSize(DEFAULT_BUTTON_SIZE, DEFAULT_BUTTON_SIZE);
		secondaryColor.setLocation(DEFAULT_BUTTON_SIZE/2, DEFAULT_BUTTON_SIZE/2);
		secondaryColor.setToolTipText(bundle.getString("secondaryColor.tooltip"));
		secondaryColor.addMouseListener(this);
		add(secondaryColor);
		
		Image switchImage = new ImageIcon(getClass().getResource("/img/swap_color_arrow.png")).getImage();
		Image resetImage = new ImageIcon(getClass().getResource("/img/reset.png")).getImage();
		int offset = PANEL_SIZE - switchImage.getWidth(null);
		
		switchColor = new UIBorderedImagePanel(switchImage);
		switchColor.setLocation(offset, 0);
		switchColor.setToolTipText(bundle.getString("switchColor.tooltip"));
		switchColor.addMouseListener(this);
		add(switchColor);
		
		resetButton = new UIBorderedImagePanel(resetImage);
		resetButton.setLocation(0, offset);
		resetButton.setBorderVisible(true);
		resetButton.setToolTipText(bundle.getString("resetButton.tooltip"));
		resetButton.addMouseListener(this);
		add(resetButton);
	}
	
	public void setPrimaryColor(Color color) {
		primaryColor.setBackground(color);
	}
	
	public Color getPrimaryColor() {
		return primaryColor.getBackground();
	}
	
	public Color getSecondaryColor() {
		return secondaryColor.getBackground();
	}
	
	public void swap() {
		
		Color temp = secondaryColor.getBackground();
		secondaryColor.setBackground(primaryColor.getBackground());
		primaryColor.setBackground(temp);
		
		if (cellZoomPanel.isColoring()) {
			sudokuPanel.updateColorCursor();	
		}
		
		repaint();
	}
	
	private void clearColor(Color c) {
		if (cellZoomPanel.isColoringCells()) {
			sudokuPanel.clearCellColor(c);
		} else if (cellZoomPanel.isColoringCandidates()) {
			sudokuPanel.clearCandidateColor(c);
		}
	}
	
	private void clearAllColors() {
		
		//if (cellZoomPanel.isColoringCells()) {
		//	sudokuPanel.clearCellColors();
		//} else if (cellZoomPanel.isColoringCandidates()) {
		//	sudokuPanel.clearCandidateColors();
		//} else {
			sudokuPanel.clearColoring();
		//}
		
		sudokuPanel.repaint();
	}
	
	private Color showColorChooserDialog(Color currentColor) {
		
		initialColor = currentColor;
		selectedColor = currentColor;
		colorChooser.setColor(currentColor);
		
		JButton[] buttons = {
			dialogButtonOK, 
			dialogButtonCancel, 
			dialogButtonReset
		};
		
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(colorChooser);
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);
		optionPane.setOptionType(JOptionPane.NO_OPTION);
		optionPane.setInitialValue(buttons[0]);
		optionPane.setInitialSelectionValue(dialogButtonOK);
		
		ResourceBundle bundle = ResourceBundle.getBundle("intl/UIColorPalette");
		dialog = optionPane.createDialog(bundle.getString("optionPane.title"));
		dialog.setVisible(true);
		
		return selectedColor;
	}
	
	public void setSudokuPanel(SudokuPanel sudokuPanel) {
		this.sudokuPanel = sudokuPanel;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == dialogButtonOK) {
			selectedColor = colorChooser.getColor();
			dialog.setVisible(false);
		} else if (e.getSource() == dialogButtonCancel) {
			dialog.setVisible(false);
		} else if (e.getSource() == dialogButtonReset) {
			selectedColor = initialColor;
			colorChooser.setColor(initialColor);
		} else {
			dialog.setVisible(false);
		}
		
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		if (e.getSource() == primaryColor) {
			
			if (e.isControlDown()) {
				clearColor(primaryColor.getBackground());
			} else {
				primaryColor.setBackground(showColorChooserDialog(primaryColor.getBackground()));
				if (!cellZoomPanel.isDefaultMouse()) {
					sudokuPanel.updateColorCursor();
				}
			}
			
			repaint();
			
		} else if (e.getSource() == secondaryColor) {
			
			if (e.isControlDown()) {
				clearColor(secondaryColor.getBackground());
			} else {
				secondaryColor.setBackground(showColorChooserDialog(secondaryColor.getBackground()));
				if (!cellZoomPanel.isDefaultMouse()) {
					sudokuPanel.updateColorCursor();
				}
			}
			
			repaint();
			
		} else if (e.getSource() == switchColor) {
			swap();
		} else if (e.getSource() == resetButton) {
			clearAllColors();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
