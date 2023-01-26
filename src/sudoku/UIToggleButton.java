/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class UIToggleButton extends JPanel implements MouseListener {

	private static final long serialVersionUID = -5840641639104950117L;
	
	private static final Color DEFAULT_BORDER_COLOR_DARK = new Color(64, 64, 64);
	private static final Color DEFAULT_BORDER_COLOR_LIGHT = new Color(192, 192, 192);
	
	private static RenderingHints RENDER_HINT_BILINIAR = new RenderingHints(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR
    );
	
	private Color borderColorDark;
	private Color borderColorLight;
	private Image backgroundImageOn;
	private Image backgroundImageOff;
	private boolean isBorderVisible;
	private boolean isOn;
	
	UIToggleButton() {
		
		super(true);
		
		this.borderColorDark = DEFAULT_BORDER_COLOR_DARK;
		this.borderColorLight = DEFAULT_BORDER_COLOR_LIGHT;
		this.backgroundImageOn = null;
		this.backgroundImageOff = null;
		this.isBorderVisible = true;
		this.isOn = true;
		this.addMouseListener(this);
	}
	
	UIToggleButton(Image on, Image off) {
		
		super(true);
		
		this.borderColorDark = DEFAULT_BORDER_COLOR_DARK;
		this.borderColorLight = DEFAULT_BORDER_COLOR_LIGHT;
		this.backgroundImageOn = on;
		this.backgroundImageOff = off;
		this.isBorderVisible = true;
		this.isOn = true;
		this.addMouseListener(this);
	}
	
	public void setBorderVisible(boolean isVisible) {
		this.isBorderVisible = isVisible;
	}
	
	public void setOn(Image on, Image off) {
		this.backgroundImageOn = on;
		this.backgroundImageOff = off;
	}
	
	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
	
	public boolean isOn() {
		return this.isOn;
	}
	
	public void toggle() {
		this.isOn = !this.isOn;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (this.isOn) {
			if (this.backgroundImageOn != null) {
				((Graphics2D)g).setRenderingHints(RENDER_HINT_BILINIAR);
				g.drawImage(this.backgroundImageOn, 0, 0, getWidth(), getHeight(), null);
			}
		} else {
			if (this.backgroundImageOff != null) {
				((Graphics2D)g).setRenderingHints(RENDER_HINT_BILINIAR);
				g.drawImage(this.backgroundImageOff, 0, 0, getWidth(), getHeight(), null);
			}	
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

	@Override
	public void mouseClicked(MouseEvent e) {
		toggle();
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
