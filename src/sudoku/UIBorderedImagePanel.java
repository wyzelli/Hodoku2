/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class UIBorderedImagePanel extends JPanel {

	private static final long serialVersionUID = 7893104594584912420L;
	
	private static final Color DEFAULT_BORDER_COLOR_DARK = new Color(64, 64, 64);
	private static final Color DEFAULT_BORDER_COLOR_LIGHT = new Color(192, 192, 192);
	
	private Color borderColorDark;
	private Color borderColorLight;
	private Image backgroundImage;
	private boolean isBorderVisible;

	public UIBorderedImagePanel() {
		
		super(true);
		
		this.borderColorDark = DEFAULT_BORDER_COLOR_DARK;
		this.borderColorLight = DEFAULT_BORDER_COLOR_LIGHT;
		this.backgroundImage = null;
		this.isBorderVisible = true;
	}
	
	public UIBorderedImagePanel(Image backgroundImage) {
		
		super(true);
		
		this.borderColorDark = DEFAULT_BORDER_COLOR_DARK;
		this.borderColorLight = DEFAULT_BORDER_COLOR_LIGHT;
		this.backgroundImage = backgroundImage;
		this.isBorderVisible = false;
		
		if (backgroundImage != null) {
			this.setSize(backgroundImage.getWidth(null), backgroundImage.getHeight(null));	
		} else {
			System.out.println("Error: UIBorderedImagePanel image is null.");
		}
	}
	
	public void setBorderVisible(boolean isVisible) {
		this.isBorderVisible = isVisible;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (this.backgroundImage != null) {
			g.drawImage(this.backgroundImage, 0, 0, null);
		}
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		
		super.paintBorder(g);
		
		if (this.isBorderVisible) {
			g.setColor(borderColorDark);
			g.drawLine(0, 0, getWidth()-1, 0);
			g.drawLine(0, 0, 0, getHeight()-1);
			g.setColor(borderColorLight);
			g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight()-1);
			g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);	
		}
	}
}
