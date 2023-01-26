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

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.w3c.dom.Node;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import solver.SudokuStepFinder;

/**
 * A specialized JPanel for displaying and manipulating Sudokus.<br>
 *
 * Mouse click detection:<br>
 * <br>
 *
 * AWT seems to have problems with mouse click detection: if the mouse moves a
 * tiny little bit between PRESSED and RELEASED, no CLICKED event is produced.
 * This means, that when playing HoDoKu with the mouse fast, the program often
 * seems to ignore the mouse.<br>
 * <br>
 *
 * The solution is simple: Catch the PRESSED and RELEASED events and decide for
 * yourself, if a CLICKED has happened. For HoDoKu a CLICKED event is generated,
 * if PRESSED and RELEASED occured on the same candidate.
 *
 * @author hobiwan
 */
public class SudokuPanel extends javax.swing.JPanel implements Printable {

	private static final long serialVersionUID = 1L;

	private static BufferedImage[] colorKuImagesSmall = new BufferedImage[Sudoku2.UNITS + 2];
	private static BufferedImage[] colorKuImagesLarge = new BufferedImage[Sudoku2.UNITS];
	private static final int DEFAULT_DOUBLE_CLICK_SPEED = 500;
	private static final int DELTA = 5;
	private static final int DELTA_RAND = 5;
	private static final int[] KEY_CODES = new int[] {
		KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2,
		KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
		KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8,
		KeyEvent.VK_9
	};
	
	private boolean showCandidates = Options.getInstance().isShowCandidates();
	private boolean showWrongValues = Options.getInstance().isShowWrongValues();
	private boolean showDeviations = Options.getInstance().isShowDeviations();
	private boolean invalidCells = Options.getInstance().isInvalidCells();
	private boolean showInvalidOrPossibleCells = false;
	private boolean[] showHintCellValues = new boolean[11];
	private boolean showAllCandidatesAkt = false;
	private boolean showAllCandidates = false;
	private int delta = DELTA;
	private int deltaRand = DELTA_RAND;
	private Font valueFont;
	private Font candidateFont;
	private int candidateHeight;
	private Font bigFont;
	private Font smallFont;
	private Sudoku2 sudoku;
	private SudokuSolver solver;
	private SudokuGenerator generator;
	private MainFrame mainFrame;
	private CellZoomPanel cellZoomPanel;
	private SolutionStep step;
	private int chainIndex = -1;
	private List<Integer> alsToShow = new ArrayList<Integer>();
	private Rectangle gridRegion = new Rectangle();
	private float strokeWidth = 1;
	private float boxStrokeWidth = 1;
	private int strokeWidthInt = 1;
	private int oldWidth;
	private int cellSize;
	private Graphics2D g2;
	private CubicCurve2D.Double cubicCurve = new CubicCurve2D.Double();
	private Polygon arrow = new Polygon();
	private Stroke arrowStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Stroke strongLinkStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Stroke weakLinkStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] { 5.0f }, 0.0f);
	private List<Point2D.Double> points = new ArrayList<Point2D.Double>(200);
	private double arrowLengthFactor = 1.0 / 6.0;
	private double arrowHeightFactor = 1.0 / 3.0;
	private int shiftRow = -1;
	private int shiftCol = -1;
	private Stack<Sudoku2> undoStack = new Stack<Sudoku2>();
	private Stack<Sudoku2> redoStack = new Stack<Sudoku2>();
	private SortedMap<Integer, Color> coloringMap = new TreeMap<Integer, Color>();
	private SortedMap<Integer, Color> coloringCandidateMap = new TreeMap<Integer, Color>();
	private Cursor colorCursor = null;
	private Cursor colorCursorShift = null;
	private Cursor oldCursor = null;
	private ArrayList<Integer> cellSelection = new ArrayList<Integer>();
	private boolean[] dragCellSelection = new boolean[82];
	private ProgressChecker progressChecker = null;
	private Timer deleteCursorTimer = new Timer(Options.getInstance().getDeleteCursorDisplayLength(), null);
	private long lastCursorChanged = -1;
	private RightClickMenu rightClickMenu = null;
	private boolean[] remainingCandidates = new boolean[Sudoku2.UNITS];
	
	// event meta data
	public int lastPressedRow = -1;
	public int lastPressedCol = -1;
	public int lastPressedCandidate = -1;
	private int lastClickedRow = -1;
	private int lastClickedCol = -1;
	private int lastClickedCandidate = -1;
	private long lastClickedTime = 0;
	private long doubleClickSpeed = -1;
	private Candidate lastCandidateMouseOn = new Candidate();
	private boolean isCtrlDown;
	private Point lastMousePosition = new Point();
	private int lastHighlightedDigit = 0;
	private boolean isColoringVisible = true;

	/**
	 * Creates new form SudokuPanel
	 * @param mf
	 */
	public SudokuPanel(MainFrame mf) {

		mainFrame = mf;
		sudoku = new Sudoku2();
		sudoku.clearSudoku();
		setShowCandidates(Options.getInstance().isShowCandidates());
		generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
		solver = SudokuSolverFactory.getDefaultSolverInstance();
		solver.setSudoku(sudoku.clone());
		solver.solve();
		progressChecker = new ProgressChecker(mainFrame);
		isCtrlDown = false;
		setActiveCell(4, 4);
		//cellSelection.add(new Integer(Sudoku2.getIndex(4, 4)));
		
		clearDragSelection();
		initComponents();
		
		rightClickMenu = new RightClickMenu(mf, this);
		rightClickMenu.setColorIconsInPopupMenu();
		updateCellZoomPanel();
		calculateGridRegion(getBounds(), false, false);
		
		deleteCursorTimer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCursorTimer.stop();
				lastCursorChanged = System.currentTimeMillis() - Options.getInstance().getDeleteCursorDisplayLength() - 100;
				repaint();
			}
		});

		Object cs = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		if (cs instanceof Integer) {
			doubleClickSpeed = ((Integer) cs).intValue();
		}

		if (doubleClickSpeed == -1) {
			doubleClickSpeed = DEFAULT_DOUBLE_CLICK_SPEED;
		}
	}

	private void clearDragSelection() {
		Arrays.fill(dragCellSelection, false);
	}
	
	public int getFirstRow() {
		
		if (cellSelection.isEmpty()) {
			return -1;
		}
		
		int index = cellSelection.get(0);
		return Sudoku2.getRow(index);
	}
	
	public int getFirstCol() {
		
		if (cellSelection.isEmpty()) {
			return -1;
		}
		
		int index = cellSelection.get(0);
		return Sudoku2.getCol(index);
	}
	
	public int getCellSelectionSize() {
		return cellSelection.size();
	}
	
	public int getActiveRow() {
		
		if (cellSelection.isEmpty()) {
			return -1;
		}
		
		int index = cellSelection.get(cellSelection.size()-1);
		return Sudoku2.getRow(index);
	}
	
	public int getActiveCol() {
		
            if (cellSelection.isEmpty()) {
                    return -1;
            }

            int index = cellSelection.get(cellSelection.size()-1);
            return Sudoku2.getCol(index);
	}
	
	public Integer getActiveCell() {
		
            if (cellSelection.isEmpty()) {
                    return null;
            }

            return Integer.valueOf(Sudoku2.getIndex(getActiveRow(), getActiveCol()));
	}
	
	public void setActiveCell(int index) {
            
            Integer intObj = index;

            if (Options.getInstance().isDeleteCursorDisplay()) {
        	if (cellZoomPanel != null && !cellZoomPanel.isColoring()) {
                    deleteCursorTimer.stop();
                    lastCursorChanged = System.currentTimeMillis();
                    deleteCursorTimer.setDelay(Options.getInstance().getDeleteCursorDisplayLength());
                    deleteCursorTimer.setInitialDelay(Options.getInstance().getDeleteCursorDisplayLength());
                    deleteCursorTimer.start();	
                }
            }

            if (cellSelection.contains(intObj )) {
                cellSelection.remove(intObj );
            }
            
            cellSelection.add(intObj );
            mainFrame.updateCellSelectionStatus();
	}
	
	public void setActiveCell(int row, int col) {		
            setActiveCell(Sudoku2.getIndex(row, col));
	}
	
	public void clearSelection(int lastCell) {
		cellSelection.clear();
		//cellSelection.add(new Integer(lastCell));
		setActiveCell(lastCell);
	}
	
	public void clearSelection(int row, int col) {
		clearSelection(Sudoku2.getIndex(row, col));
	}
	
	public void clearSelection() {
		
		if (cellSelection.isEmpty()) {
			System.out.println("Error: SudokuPanel.clearSelection is empty...");
		}
		
		Integer lastCell = cellSelection.get(cellSelection.size()-1);
		cellSelection.clear();
		//cellSelection.add(lastCell);
		setActiveCell(lastCell);
	}
	
	public void resetKeyState() {
		isCtrlDown = false;
	}

	private void initComponents() {

		rightClickMenu = new RightClickMenu(this.mainFrame, this);

		setBackground(new java.awt.Color(255, 255, 255));
		setMinimumSize(new java.awt.Dimension(300, 300));
		setPreferredSize(new java.awt.Dimension(600, 600));
		addMouseListener(new java.awt.event.MouseListener() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {}

			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				onMouseDown(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				onMouseUp(evt);
			}

			@Override
			public void mouseEntered(MouseEvent evt) {
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				lastCandidateMouseOn = null;
				repaint();
			}
		});

		addMouseMotionListener(new java.awt.event.MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				lastMousePosition = e.getPoint();

				int row = getRow(e.getPoint());
				int col = getCol(e.getPoint());
				int index = Sudoku2.getIndex(row, col);

				updateCandidateMouseHighlight(e.getPoint());

				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}
				
				if (!Sudoku2.isValidIndex(row, col)) {
					return;
				}

				if (cellZoomPanel.isColoring()) {
					return;
				}

				if (!dragCellSelection[index]) {

					dragCellSelection[index] = true;
					if (cellSelection.contains(Integer.valueOf(index))) {						
						cellSelection.remove(Integer.valueOf(index));					
					} else {						
						cellSelection.add(Integer.valueOf(index));
					}
				}
				
				setActiveCell(row, col);
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				lastMousePosition = e.getPoint();
				updateCandidateMouseHighlight(e.getPoint());
			}
		});

		addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyPressed(java.awt.event.KeyEvent evt) {
				onKeyPressed(evt);
			}

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				onKeyReleased(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 600, Short.MAX_VALUE));

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 600, Short.MAX_VALUE));
	}
	
	private Rectangle calculateGridRegion(Rectangle bounds, boolean isPrint, boolean withBorder) {
		
		// determine the actual size of the quad
		int width = (bounds.height < bounds.width) ? bounds.height : bounds.width;
		int height = (bounds.width < bounds.height) ? bounds.width : bounds.height;
		
		// make the size of the lines larger, especially for high res printing
		this.strokeWidth = 2.0f / 1000.0f * width;
		if (width > 1000) {
			this.strokeWidth *= 1.5f;
		}

		this.boxStrokeWidth = (float) (this.strokeWidth * Options.getInstance().getBoxLineFactor());
		this.strokeWidthInt = Math.round(this.boxStrokeWidth / 2);
		
		this.delta = bounds.width / 100;
		this.deltaRand = bounds.width / 100;
		
		if (this.deltaRand < this.strokeWidthInt) {
			this.deltaRand = this.strokeWidthInt;
		}

		if (Options.getInstance().getDrawMode() == 1) {
			this.delta = 0;
		}
		
		// calculate the size of the cells and adjust for rounding errors
		this.cellSize = (width - 4 * this.delta - 2 * this.deltaRand) / Sudoku2.UNITS;

		width = height = this.cellSize * Sudoku2.UNITS + 4 * this.delta;
		
		int sx = (bounds.width - width) / 2;
		int sy = (bounds.height - height) / 2;
		
		if (isPrint && withBorder) {
			sy = 0;
		}
		
		return new Rectangle(sx, sy, width, height);
	}
	
	public boolean isOnGrid(Point point) {
		
		return 
			point.x >= this.gridRegion.x &&
			point.x < (this.gridRegion.x + this.gridRegion.width) &&
			point.y >= this.gridRegion.y &&
			point.y < (this.gridRegion.y + this.gridRegion.height);
			
	}

	private void updateCandidateMouseHighlight(Point mouse) {

		if (showCandidateHighlight()) {

			int row = getRow(mouse);
			int col = getCol(mouse);
			int candidate = getCandidate(mouse, row, col);
			int index = Sudoku2.getIndex(row, col);

			if (Sudoku2.isValidIndex(row, col)) {

				Candidate mouseOn = new Candidate(index, candidate);
				if (lastCandidateMouseOn != mouseOn) {
					lastCandidateMouseOn = mouseOn;
					repaint();
				}

			} else {
				if (lastCandidateMouseOn != null) {
					lastCandidateMouseOn = null;
					repaint();
				}
			}
		}
	}
	
	private void updateAutoHighlight(int row, int col) {
		
		if (Options.getInstance().isAutoHighlighting()) {
			int value = sudoku.getValue(row, col);
			
			if (value != 0 && value != lastHighlightedDigit) {
				setShowHintCellValue(value);
				setShowInvalidOrPossibleCells(true);
				lastHighlightedDigit = value;
			} else {/*
				resetShowHintCellValues();
				setShowInvalidOrPossibleCells(false);
				lastHighlightedDigit = 0;*/
			}
		}
	}

	private void onKeyReleased(java.awt.event.KeyEvent evt) {
		handleKeysReleased(evt);
		updateCellZoomPanel();
		mainFrame.fixFocus();
	}

	private void onKeyPressed(java.awt.event.KeyEvent evt) {

		int keyCode = evt.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ESCAPE:
			mainFrame.coloringPanelClicked(null);
			clearRegion();
			if (step != null) {
				mainFrame.abortStep();
			}
			break;
		default:
			handleKeys(evt);
		}

		updateCellZoomPanel();
		mainFrame.fixFocus();
	}

	private void onMouseDown(java.awt.event.MouseEvent evt) {

		lastMousePosition = evt.getPoint();
		lastPressedRow = getRow(evt.getPoint());
		lastPressedCol = getCol(evt.getPoint());
		lastPressedCandidate = getCandidate(evt.getPoint(), lastPressedRow, lastPressedCol);

		clearDragSelection();

		Integer index = Sudoku2.getIndex(lastPressedRow, lastPressedCol);
		boolean isLeftClick = evt.getButton() == MouseEvent.BUTTON1;
		boolean isRightClick = evt.getButton() == MouseEvent.BUTTON3;
		boolean rightClickOutsideSelection = !cellSelection.contains(Integer.valueOf(index)) && isRightClick;
		boolean onGrid = isOnGrid(evt.getPoint());
		
		if (!onGrid) {

			clearSelection();
			clearDragSelection();
			updateCellZoomPanel();
			
		} else if (rightClickOutsideSelection || cellZoomPanel.isColoring()) {
			
			if (rightClickOutsideSelection) {
				setActiveCell(lastPressedRow, lastPressedCol);
			}
			
			clearSelection();
			clearDragSelection();
			
		} else if (isLeftClick) {

			if (!isCtrlDown) {
				
				updateAutoHighlight(lastPressedRow, lastPressedCol);				
				setActiveCell(lastPressedRow, lastPressedCol);
				clearSelection();
				clearDragSelection();
				
			} else {

				if (cellSelection.contains(index) && cellSelection.size() > 1) {
					cellSelection.remove(index);
					int lastCellSelection = cellSelection.get(cellSelection.size()-1).intValue();
					setActiveCell(Sudoku2.getRow(lastCellSelection), Sudoku2.getCol(lastCellSelection));
				} else if (!cellSelection.contains(index)) {
					cellSelection.add(index);
					setActiveCell(index);
				}
			}
		}
		
		repaint();
	}

	private void onMouseUp(java.awt.event.MouseEvent evt) {

		lastMousePosition = evt.getPoint();

		int row = getRow(evt.getPoint());
		int col = getCol(evt.getPoint());
		int candidate = getCandidate(evt.getPoint(), row, col);
		boolean onGrid = isOnGrid(evt.getPoint());

		if (!onGrid) {

			lastPressedRow = -1;
			lastPressedCol = -1;
			lastPressedCandidate = -1;
			clearSelection();
			clearDragSelection();
			updateCellZoomPanel();
			
			lastPressedRow = -1;
			lastPressedCol = -1;
			lastPressedCandidate = -1;
			return;			
		}
			
		long ticks = System.currentTimeMillis();

		boolean isDoubleClick = 
			lastClickedTime != -1 && 
			(ticks - lastClickedTime) <= doubleClickSpeed && 
			row == lastClickedRow && 
			col == lastClickedCol && 
			candidate == lastClickedCandidate;

		if (isDoubleClick) {
			handleMouseClicked(evt, true);
			lastClickedTime = -1;
		} else {
			handleMouseClicked(evt, false);
			lastClickedTime = ticks;
		}

		lastClickedRow = row;
		lastClickedCol = col;
		lastClickedCandidate = candidate;
	}

	boolean onRightClick(MouseClickDTO dto) {
		
		boolean change = false;
		
		if (Options.getInstance().isSingleClickMode()) {
			
			// toggle candidate in cell(s) (three state mode)
			if (cellSelection.contains(Integer.valueOf(Sudoku2.getIndex(dto.row, dto.col)))) {
				
				// a region select exists and the cells lies within: toggle candidate
				if (dto.candidate != -1) {
					change = toggleCandidateInAktCells(dto.candidate);
				}
				
			} else {
				
				// no region or cell outside region -> change focus and toggle candidate
				setActiveCell(dto.row, dto.col);
				clearRegion();
				
				if (sudoku.getValue(dto.row, dto.col) != 0 && !sudoku.isFixed(dto.row, dto.col)) {
					
					rightClickMenu.deleteValuePopup(dto.row, dto.col, cellSize);
					
				} else {
					
					int showHintCellValue = getShowHintCellValue();
					if ((dto.candidate == -1 || 
						!sudoku.isCandidate(dto.row, dto.col, dto.candidate, !showCandidates)) && 
						showHintCellValue != 0) {
						
						// if the candidate is not present, but part of the solution and
						// show deviations is set, it is displayed, although technically
						// not present: it should be toggled, even if it is not the
						// hint value
						if (showDeviations && 
							sudoku.isSolutionSet() && 
							dto.candidate == sudoku.getSolution(getActiveRow(), getActiveCol())) {
							toggleCandidateInCell(getActiveRow(), getActiveCol(), dto.candidate);
						} else {
							toggleCandidateInCell(getActiveRow(), getActiveCol(), showHintCellValue);
						}
						
					} else if (dto.candidate != -1) {
						toggleCandidateInCell(getActiveRow(), getActiveCol(), dto.candidate);
					}
					
					change = true;
				}
			}
			
		} else {
			// bring up popup menu
			rightClickMenu.showPopupMenu(dto.row, dto.col, cellSize);
		}
		
		return change;
	}
	
	boolean onSingleLeftClick(MouseClickDTO dto, int index) {
		
		if (dto.ctrlPressed) {
			
			/*
			Integer cell = Integer.valueOf(index);

			// select additional cell
			if (cellSelection.size() == 0) {
				
				cellSelection.add(cell);
				setActiveCell(dto.row, dto.col);
							
				if (!dragCellSelection[index]) {
					if (cellSelection.contains(Integer.valueOf(index))) {
						cellSelection.remove(Integer.valueOf(index));
					} else {
						cellSelection.add(Integer.valueOf(index));
					}
				}
				
				setActiveCell(dto.row, dto.col);				
			}*/
			
			return false;			
		}
		
		if (dto.shiftPressed) {
			
			if (Options.getInstance().isUseShiftForRegionSelect()) {
				// select range of cells
				selectRegion(dto.row, dto.col);
			} else {
				if (dto.candidate != -1) {
					// toggle candidate
					if (sudoku.isCandidate(index, dto.candidate, !showCandidates)) {
						sudoku.setCandidate(index, dto.candidate, false, !showCandidates);
					} else {
						sudoku.setCandidate(index, dto.candidate, true, !showCandidates);
					}
					
					clearRegion();
					return true;
				}
			}
			
			return false;			
		}
		
		// select single cell, delete old markings if available
		// in the alternative mouse mode a single cell is only
		// selected, if the cell is outside a selected region
		if ((Options.getInstance().isSingleClickMode() == false && 
			cellSelection.contains(Integer.valueOf(Sudoku2.getIndex(dto.row, dto.col))) == false) ||
			Options.getInstance().isSingleClickMode() == true) {
			setActiveCell(dto.row, dto.col);
			clearRegion();
		}
		
		if (Options.getInstance().isSingleClickMode()) {
			
			// the selected cell(s) must be set to cand
			if (sudoku.getValue(index) == 0) {
				if (cellSelection.isEmpty()) {
					
					int showHintCellValue = getShowHintCellValue();
					if (sudoku.getAnzCandidates(index, !showCandidates) == 1) {
						// Naked single -> set it!
						int actCand = sudoku.getAllCandidates(index, !showCandidates)[0];
						setCell(dto.row, dto.col, actCand);
						return true;
					} else if (showHintCellValue != 0 && isHiddenSingle(showHintCellValue, dto.row, dto.col)) {
						// Hidden Single -> it
						setCell(dto.row, dto.col, showHintCellValue);
						return true;
					} else if (dto.candidate != -1) {
						// set candidate
						// (only if that candidate is still set in the cell)
						if (sudoku.isCandidate(index, dto.candidate, !showCandidates)) {
							setCell(dto.row, dto.col, dto.candidate);
						}
						
						return true;
					}
					
				} else {
					
					if (dto.candidate == -1 || !sudoku.isCandidate(index, dto.candidate, !showCandidates)) {
						// an empty space was clicked in the cell -> clear region
						setActiveCell(dto.row, dto.col);
						clearRegion();
					} else if (dto.candidate != -1) {
						// an actual candidate was clicked -> set value in all cells where it is
						// still possible (collect cells first to avoid side effects!)
						List<Integer> cells = new ArrayList<Integer>();
						for (int selIndex : cellSelection) {
							if (sudoku.getValue(selIndex) == 0 && 
								sudoku.isCandidate(selIndex, dto.candidate, !showCandidates)) {
								cells.add(selIndex);
							}
						}
						
						for (int cellIndex : cells) {
							setCell(Sudoku2.getRow(cellIndex), Sudoku2.getCol(cellIndex), dto.candidate);
						}
					}
				}
			} else {
				// clear selection
				setActiveCell(dto.row, dto.col);
				clearRegion();				
			}
			
			return true;
		}
		
		return false;
	}
	
	boolean onDoubleLeftClick(MouseClickDTO dto, int index) {
		
		if (dto.ctrlPressed) {
			if (dto.candidate != -1) {
				
				// toggle candidate
				if (sudoku.isCandidate(index, dto.candidate, !showCandidates)) {
					sudoku.setCandidate(index, dto.candidate, false, !showCandidates);
				} else {
					sudoku.setCandidate(index, dto.candidate, true, !showCandidates);
				}
				
				clearRegion();
				return true;
			}
			
		} else {
			
			if (sudoku.getValue(index) == 0) {
				
				int showHintCellValue = getShowHintCellValue();
				if (sudoku.getAnzCandidates(index, !showCandidates) == 1) {
					// Naked single -> set it!
					int actCand = sudoku.getAllCandidates(index, !showCandidates)[0];
					setCell(dto.row, dto.col, actCand);
					setCandidateFilterByGiven(dto.row, dto.col);
					return true;
				} else if (showHintCellValue != 0 && isHiddenSingle(showHintCellValue, dto.row, dto.col)) {
					// Hidden Single -> it
					setCell(dto.row, dto.col, showHintCellValue);
					setCandidateFilterByGiven(dto.row, dto.col);
					return true;
				} else if (dto.candidate != -1) {
					// candidate double clicked -> set it
					// (only if that candidate is still set in the cell)
					if (sudoku.isCandidate(index, dto.candidate, !showCandidates)) {
						setCell(dto.row, dto.col, dto.candidate);
						setCandidateFilterByGiven(dto.row, dto.col);
					}
					
					return true;
				}
				
			} else if (!sudoku.isFixed(index)) {
				// double clicking a user input value removes it
				setCell(dto.row, dto.col, 0);
				setCandidateFilterByGiven(dto.row, dto.col);
				return true;
			}
		}
		
		return false;
	}
	
	void setCandidateFilterByGiven(int row, int col) {
		if (Options.getInstance().isAutoHighlighting()) {
			int value = sudoku.getValue(row, col);
			if (value != 0) {
				setShowHintCellValue(value);
				setShowInvalidOrPossibleCells(true);
				showHintCellValues[value] = true;
				lastHighlightedDigit = value;
			} else {
				resetShowHintCellValues();
				setShowInvalidOrPossibleCells(false);
				lastHighlightedDigit = 0;
				mainFrame.repaint();
			}
		}
	}
	
	boolean onLeftClick(MouseClickDTO dto) {
		
		boolean change = false;
				
		// in normal mode we only react to the left mouse button
		int index = Sudoku2.getIndex(dto.row, dto.col);
		if (dto.isDoubleClick) {
			change = onDoubleLeftClick(dto, index);			
		} else {			
			change = onSingleLeftClick(dto, index);
		}
		
		return change;
	}
	
	void onColoring(MouseClickDTO dto) {
		
		if (cellZoomPanel.isColoringCells()) {
			// coloring for cells
			if (dto.isCellClicked) {
				handleColoring(dto.row, dto.col, -1, cellZoomPanel.getPrimaryColor());	
			}
		} else if (cellZoomPanel.isColoringCandidates()) {
			// coloring for candidates
			if (dto.candidate != -1) {
				if (dto.isCandidateClicked) {
					handleColoring(dto.row, dto.col, dto.candidate, cellZoomPanel.getPrimaryColor());
				}
			}
		}
	}
	
	class MouseClickDTO {
		
		public int row;
		public int col;
		public int candidate;
		public boolean ctrlPressed;
		public boolean shiftPressed;
		public boolean isValidCellIndex;
		public boolean isLeftClick;
		public boolean isMiddleClick;
		public boolean isRightClick;
		public boolean isCellClicked;
		public boolean isCandidateClicked;
		public boolean isDoubleClick;
		
		MouseClickDTO(MouseEvent e, SudokuPanel panel, boolean isDoubleClick) {
			this.row = panel.getRow(e.getPoint());
			this.col = panel.getCol(e.getPoint());
			this.candidate = panel.getCandidate(e.getPoint(), row, col);
			this.ctrlPressed = (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;
			this.shiftPressed = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
			this.isValidCellIndex = Sudoku2.isValidIndex(row, col);
			this.isLeftClick = e.getButton() == MouseEvent.BUTTON1;
			this.isMiddleClick = e.getButton() == MouseEvent.BUTTON2;
			this.isRightClick = e.getButton() == MouseEvent.BUTTON3;
			this.isCellClicked = 
				this.row == panel.lastPressedRow && 
				this.col == panel.lastPressedCol;
			this.isCandidateClicked = 
				this.isCellClicked &&
				this.candidate == panel.lastPressedCandidate;
			this.isDoubleClick = isDoubleClick;
		}
	}
	
	/**
	 * New mouse control for version 2.0:
	 * <ul>
	 * <li>clicking a cell sets the cursor to the cell (not in coloring mode)</li>
	 * <li>holding shift or ctrl down while clicking selects a region of cells</li>
	 * <li>double clicking a cell with only one candidate left sets that candidate
	 * in the cell</li>
	 * <li>double clicking a cell containing a Hidden Single sets that cell if
	 * filters are applied for the candidate</li>
	 * <li>double clicking a candidate with ctrl pressed toggles the candidate</li>
	 * <li>right click on a cell activates the context menu</li>
	 * </ul>
	 * If {@link #aktColorIndex} is set (ne -1), coloring mode is in effect and the
	 * mouse behaviour changes completely (whether a cell or a candidate should be
	 * colored is decided by {@link #isColoringCells}):
	 * <ul>
	 * <li>left click on a cell/candidate toggles the color on the
	 * cell/candidate</li>
	 * <li>left click on a cell/candidate with shift pressed toggles the alternate
	 * color on the cell/candidate</li>
	 * </ul>
	 * Context menu:<br>
	 * The context menu for a single cell shows entries to set the cell to all
	 * remaining candidates, entries to remove all remaining candidates (including
	 * one entry to remove multiple candidates in one move) and entries for
	 * coloring.<br>
	 * <b>Alternative Mouse Mode:</b><br>
	 * Since v2.1a new alternative mouse mode is available: Left click on a
	 * candidate sets the candidate in the cell(s), right click toggles the
	 * candidate in the cell(s). Selection works as before.
	 *
	 * @param evt
	 */
	private void handleMouseClicked(MouseEvent evt, boolean isDoubleClick) {
		
		MouseClickDTO dto = new MouseClickDTO(evt, this, isDoubleClick);
		boolean changed = false;
		
		undoStack.push(sudoku.clone());
		
		if (dto.isValidCellIndex) {
			if (dto.isRightClick) {				
				changed = onRightClick(dto);				
			} else {
				if (cellZoomPanel.isColoring()) {
					onColoring(dto);		
				} else if (dto.isLeftClick) {
					changed = onLeftClick(dto);	
				}				
			}
			
			if (changed) {
				redoStack.clear();
				checkProgress();
			} else {
				undoStack.pop();
			}
			
			updateCellZoomPanel();
			mainFrame.check();
			repaint();
		}
	}

	public void saveState() {
		undoStack.push(sudoku.clone());
		mainFrame.check();
		repaint();
	}
	
	/**
	 * Moves the cursor. If the cell actually changed, a timer is started, that
	 * triggers a repaint after {@link Options#getDeleteCursorDisplayLength() } ms.
	 *
	 * @param row
	 * @param col
	 */
	/*
	private void setAktRowCol(int row, int col) {
		
		if (activeRow != row) {
			activeRow = row;
		}
		
		if (activeCol != col) {
			activeCol = col;
		}
		
		if (Options.getInstance().isDeleteCursorDisplay()) {
			deleteCursorTimer.stop();
			lastCursorChanged = System.currentTimeMillis();
			deleteCursorTimer.setDelay(Options.getInstance().getDeleteCursorDisplayLength());
			deleteCursorTimer.setInitialDelay(Options.getInstance().getDeleteCursorDisplayLength());
			deleteCursorTimer.start();
		}
	}*/
	
	public void pushUndo() {
		this.undoStack.push(this.sudoku.clone());
	}
	
	public void popUndo() {
		this.undoStack.pop();
	}
	
	public void clearRedoStack() {
		this.redoStack.clear();
	}
	
	public ArrayList<Integer> getSelectedCells() {
		return this.cellSelection;
	}

	/**
	 * Loads all relevant objects into <code>state</code>. If <code>copy</code> is
	 * true, all objects are copied.<br>
	 * Some objects have to be copied regardless of parameter <code>copy</code>.
	 *
	 * @param state
	 * @param copy
	 */
	@SuppressWarnings("unchecked")
	public void getState(GuiState state, boolean copy) {
		// items that don't have to be copied
		state.setChainIndex(chainIndex);
		// items that must be copied anyway
		state.setUndoStack((Stack<Sudoku2>) undoStack.clone());
		state.setRedoStack((Stack<Sudoku2>) redoStack.clone());
		state.setColoringMap((SortedMap<Integer, Color>) ((TreeMap<Integer, Color>) coloringMap).clone());
		state.setColoringCandidateMap((SortedMap<Integer, Color>) ((TreeMap<Integer, Color>) coloringCandidateMap).clone());
		// items that might be null (and therefore wont be copied)
		state.setSudoku(sudoku);
		state.setStep(step);
		if (copy) {
			state.setSudoku(sudoku.clone());
			if (step != null) {
				state.setStep((SolutionStep) step.clone());
			}
		}
	}

	/**
	 * Loads back a saved state. Whether the objects had been copied before is
	 * irrelevant here.<br>
	 * The optional objects {@link GuiState#undoStack} and
	 * {@link GuiState#redoStack} can be null. If this is the case they are cleared.
	 *
	 * @param state
	 */
	public void setState(GuiState state) {
		
		chainIndex = state.getChainIndex();
		if (state.getUndoStack() != null) {
			undoStack = state.getUndoStack();
		} else {
			undoStack.clear();
		}
		
		if (state.getRedoStack() != null) {
			redoStack = state.getRedoStack();
		} else {
			redoStack.clear();
		}
		
		if (state.getColoringMap() != null) {
			coloringMap = state.getColoringMap();
		} else {
			coloringMap.clear();
		}
		
		if (state.getColoringCandidateMap() != null) {
			coloringCandidateMap = state.getColoringCandidateMap();
		} else {
			coloringCandidateMap.clear();
		}
		
		sudoku = state.getSudoku();
		sudoku.checkSudoku();
		step = state.getStep();
		
		updateCellZoomPanel();
		checkProgress();
		mainFrame.check();
		repaint();
	}

	/*
	public void loadFromFile(Sudoku sudoku, Sudoku solvedSudoku) {
		
		this.sudoku = sudoku;
		this.solvedSudoku = solvedSudoku;
		
		redoStack.clear();
		undoStack.clear();
		coloringMap.clear();
		coloringCandidateMap.clear();
		step = null;
		setChainInStep(-1);
		updateCellZoomPanel();
		mainFrame.check();
		repaint();
	}*/
	
	private void checkShowAllCandidates(int modifiers, int keyCode) {

		boolean oldShowAllCandidatesAkt = showAllCandidatesAkt;
		showAllCandidatesAkt = false;
		
		if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0 && (modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
			showAllCandidatesAkt = true;
		}

		boolean oldShowAllCandidates = showAllCandidates;
		showAllCandidates = false;
		
		if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0 && (modifiers & KeyEvent.ALT_DOWN_MASK) != 0) {
			showAllCandidates = true;
		}
		
		if (oldShowAllCandidatesAkt != showAllCandidatesAkt || oldShowAllCandidates != showAllCandidates) {
			repaint();
		}
	}

	public void handleKeysReleased(KeyEvent evt) {

		int modifiers = evt.getModifiersEx();
		int keyCode = 0;

		if (evt.getKeyCode() == KeyEvent.VK_CONTROL) {
			isCtrlDown = false;
			clearLastCandidateMouseOn();
			repaint();
		}

		checkShowAllCandidates(modifiers, keyCode);

		if (cellZoomPanel.isColoring()) {
			if (getCursor() == colorCursorShift) {
				setCursor(colorCursor);
			}
		}
	}

	public void handleKeys(KeyEvent evt) {

		boolean changed = false;
		undoStack.push(sudoku.clone());

		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiersEx();

		checkShowAllCandidates(modifiers, keyCode);

		if (!isCtrlDown && evt.getKeyCode() == KeyEvent.VK_CONTROL) {
			isCtrlDown = true;
			updateCandidateMouseHighlight(lastMousePosition);
		}

		if (cellZoomPanel.isColoring()) {
			if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
				if (getCursor() == colorCursor) {
					setCursor(colorCursorShift);
				}
			}
		}

		// 20120111: makes problems on certain laptops where key combinations are
		// used to produce numbers. New try: If getKeyChar() gives a number, the
		// corresponding key code is set
		char keyChar = evt.getKeyChar();
		if (Character.isDigit(keyChar)) {
			keyCode = KEY_CODES[keyChar - '0'];
		}
		
		int number = 0;
		boolean clearSelectedRegion = true;
		switch (keyCode) {
		case KeyEvent.VK_DOWN:
			
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 && 
				(modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0	&& 
				getShowHintCellValue() != 0) {
				// go to next filtered candidate
				int index = findNextHintCandidate(getFirstRow(), getFirstCol(), keyCode);
				setActiveCell(Sudoku2.getRow(index), Sudoku2.getCol(index));
			} else if (getActiveRow() < 8) {
				
				// go to the next row
				setActiveCell(getActiveRow() + 1, getActiveCol());
				
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					// go to the next unset cell
					while (getActiveRow() < 8 && sudoku.getValue(getActiveRow(), getActiveCol()) != 0) {
						setActiveCell(getActiveRow() + 1, getActiveCol());
					}
				} else if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					// expand the selected region
					
					setShift();
					//setActiveCell(getActiveRow() - 1, getActiveCol());
					//if (shiftRow < 8) {
					//	shiftRow++;
					//}
					
					selectRegion(shiftRow, shiftCol);
					clearSelectedRegion = false;
				}
				
			} else if (getActiveRow() == 8) {
				setActiveCell(0, getActiveCol());
			}

			if (clearSelectedRegion) {
				clearRegion();
			}

			break;
		case KeyEvent.VK_UP:
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 && 
				(modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0 && 
				getShowHintCellValue() != 0) {
				// go to next filtered candidate
				int index = findNextHintCandidate(getFirstRow(), getFirstCol(), keyCode);
				setActiveCell(Sudoku2.getRow(index), Sudoku2.getCol(index));
			} else if (getActiveRow() > 0) {
				// go to the next row
				setActiveCell(getActiveRow() - 1, getActiveCol());
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					// go to the next unset cell
					while (getActiveRow() > 0 && sudoku.getValue(getActiveRow(), getActiveCol()) != 0) {
						setActiveCell(getActiveRow() - 1, getActiveCol());
					}
				} else if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					// expand the selected region
					
					setShift();
					//setActiveCell(getActiveRow() + 1, getActiveCol());
					
					//if (shiftRow > 0) {
					//	shiftRow--;
					//}
					
					selectRegion(shiftRow, shiftCol);
					clearSelectedRegion = false;
				}
			} else if (getActiveRow() == 0) {
				setActiveCell(8, getActiveCol());
			}

			if (clearSelectedRegion) {
				clearRegion();
			}

			break;
		case KeyEvent.VK_RIGHT:
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 && 
				(modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0 &&
				getShowHintCellValue() != 0) {
				// go to next filtered candidate
				int index = findNextHintCandidate(getFirstRow(), getFirstCol(), keyCode);
				setActiveCell(Sudoku2.getRow(index), Sudoku2.getCol(index));
			} else if (getActiveCol() < 8) {
				// go to the next row
				setActiveCell(getActiveRow(), getActiveCol() + 1);
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					// go to the next unset cell
					while (getActiveCol() < 8 && sudoku.getValue(getActiveRow(), getActiveCol()) != 0) {
						setActiveCell(getActiveRow(), getActiveCol() + 1);
					}
				} else if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					// expand the selected region
					
					setShift();
					//setActiveCell(getActiveRow(), getActiveCol() - 1);
					
					//if (shiftCol < 8) {
					//	shiftCol++;
					//}
					
					selectRegion(shiftRow, shiftCol);
					clearSelectedRegion = false;
				}
			} else if (getActiveCol() == 8) {
				setActiveCell(getActiveRow(), 0);
			}

			if (clearSelectedRegion) {
				clearRegion();
			}

			break;
		case KeyEvent.VK_LEFT:
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 && 
				(modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0	&& 
				getShowHintCellValue() != 0) {
				// go to next filtered candidate
				int index = findNextHintCandidate(getFirstRow(), getFirstCol(), keyCode);
				setActiveCell(Sudoku2.getRow(index), Sudoku2.getCol(index));
			} else if (getActiveCol() > 0) {
				// go to the next col
				setActiveCell(getActiveRow(), getActiveCol() - 1);
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					// go to the next unset cell
					while (getActiveCol() > 0 && sudoku.getValue(getActiveRow(), getActiveCol()) != 0) {
						setActiveCell(getActiveRow(), getActiveCol() - 1);
					}
				} else if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					// expand the selected region
					
					setShift();
					//setActiveCell(getActiveRow(), getActiveCol() + 1);
					
					//if (shiftCol > 0) {
					//	shiftCol--;
					//}
					
					selectRegion(shiftRow, shiftCol);
					clearSelectedRegion = false;
				}
			} else if (getActiveCol() == 0) {
				setActiveCell(getActiveRow(), 8);
			}

			if (clearSelectedRegion) {
				clearRegion();
			}

			break;
		case KeyEvent.VK_HOME:
			if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
				
				setShift();
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					shiftRow = 0;
				} else {
					shiftCol = 0;
				}
				
				selectRegion(shiftRow, shiftCol);
				clearSelectedRegion = false;
			} else {
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					setActiveCell(0, getActiveCol());
				} else {
					setActiveCell(getActiveRow(), 0);
				}
			}
			
			if (clearSelectedRegion) {
				clearRegion();
			}
			
			break;
		case KeyEvent.VK_END:
			if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
				setShift();
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					shiftRow = 8;
				} else {
					shiftCol = 8;
				}
				
				selectRegion(shiftRow, shiftCol);
				clearSelectedRegion = false;
				
			} else {
				
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					setActiveCell(8, getActiveCol());
				} else {
					setActiveCell(getActiveRow(), 8);
				}
			}
			
			if (clearSelectedRegion) {
				clearRegion();
			}
			
			break;
		case KeyEvent.VK_ENTER: {
			int index = Sudoku2.getIndex(getActiveRow(), getActiveCol());
			if (sudoku.getValue(index) == 0) {
				int showHintCellValue = getShowHintCellValue();
				if (sudoku.getAnzCandidates(index, !showCandidates) == 1) {
					// Naked single -> set it!
					int actCand = sudoku.getAllCandidates(index, !showCandidates)[0];
					setCell(getActiveRow(), getActiveCol(), actCand);
					changed = true;
				} else if (showHintCellValue != 0 && isHiddenSingle(showHintCellValue, getActiveRow(), getActiveCol())) {
					// Hidden Single -> it
					setCell(getActiveRow(), getActiveCol(), showHintCellValue);
					changed = true;
				}
			}
		}
			break;
		case KeyEvent.VK_9:
		case KeyEvent.VK_NUMPAD9:
			number++;
		case KeyEvent.VK_8:
		case KeyEvent.VK_NUMPAD8:
			number++;
		case KeyEvent.VK_7:
		case KeyEvent.VK_NUMPAD7:
			number++;
		case KeyEvent.VK_6:
		case KeyEvent.VK_NUMPAD6:
			number++;
		case KeyEvent.VK_5:
		case KeyEvent.VK_NUMPAD5:
			number++;
		case KeyEvent.VK_4:
		case KeyEvent.VK_NUMPAD4:
			number++;
		case KeyEvent.VK_3:
		case KeyEvent.VK_NUMPAD3:
			number++;
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2:
			number++;
		case KeyEvent.VK_1:
		case KeyEvent.VK_NUMPAD1:
			number++;
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) == 0) {
				if (cellSelection.isEmpty()) {
					setCell(getActiveRow(), getActiveCol(), number);
					setCandidateFilterByGiven(getActiveRow(), getActiveCol());
					if (mainFrame.isInputMode() && Options.getInstance().isEditModeAutoAdvance()) {
						// automatically advance to the next cell
						if (getActiveCol() < 8) {
							setActiveCell(getActiveRow(), getActiveCol() + 1);
						} else if (getActiveRow() < 8) {
							setActiveCell(getActiveRow() + 1, 0);
						}
					}
				} else {
					
					// set value only in cells where the candidate is still present
					// problem: setting the first removes all other candidates in the
					// corresponding blocks so we have to collect the applicable cells first
					
					/*
					List<Integer> cells = new ArrayList<Integer>();
					for (int index : cellSelection) {
						if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, number, !showCandidates)) {
							cells.add(index);
						}
					}*/
					
					List<Integer> cells = new ArrayList<Integer>(cellSelection);
					Integer activeIndex = Integer.valueOf(Sudoku2.getIndex(getActiveRow(), getActiveCol()));
					if (!cells.contains(activeIndex)) {
						cells.add(activeIndex);
					}
					
					if (cells.size() == 1) {
						setCell(getActiveRow(), getActiveCol(), number);
						setCandidateFilterByGiven(getActiveRow(), getActiveCol());
					} else {
						for (int index : cells) {
							setCell(Sudoku2.getRow(index), Sudoku2.getCol(index), number);
						}	
					}
				}
				
				changed = true;
				
			} else {
				// only when shift is NOT pressed (if pressed its a menu accelerator)
				// 20120115: the accelerators have been removed!
				if (cellSelection.isEmpty()) {
					toggleCandidateInCell(getActiveRow(), getActiveCol(), number);
					changed = true;
				} else {
					changed = toggleCandidateInAktCells(number);
				}
			}
			
			break;
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_0:
		case KeyEvent.VK_NUMPAD0:
			
			if ((modifiers & KeyEvent.CTRL_DOWN_MASK) == 0) {
				
				if (sudoku.getValue(getActiveRow(), getActiveCol()) != 0 && !sudoku.isFixed(getActiveRow(), getActiveCol())) {
					sudoku.setCell(getActiveRow(), getActiveCol(), 0);
					setCandidateFilterByGiven(getActiveRow(), getActiveCol());
					changed = true;
				}
				
				if (mainFrame.isInputMode() && Options.getInstance().isEditModeAutoAdvance()) {
					// automatically advance to the next cell
					if (getActiveCol() < 8) {
						setActiveCell(getActiveRow(), getActiveCol() + 1);
					} else if (getActiveRow() < 8) {
						setActiveCell(getActiveRow() + 1, 0);
					}
				}
			}
			
			break;
		case KeyEvent.VK_F10:
			if ((modifiers & KeyEvent.ALT_DOWN_MASK) == 0) {				
				
				if (showHintCellValues[10]) {
					showHintCellValues[10] = false;
					resetShowHintCellValues();
				} else {
					showHintCellValues[10] = true;
					resetShowHintCellValues();
					setShowHintCellValue(10);
					checkIsShowInvalidOrPossibleCells();
				}
			}
			break;
		case KeyEvent.VK_F9:
			number++;
		case KeyEvent.VK_F8:
			number++;
		case KeyEvent.VK_F7:
			number++;
		case KeyEvent.VK_F6:
			number++;
		case KeyEvent.VK_F5:
			number++;
		case KeyEvent.VK_F4:
			number++;
		case KeyEvent.VK_F3:
			number++;
		case KeyEvent.VK_F2:
			number++;
		case KeyEvent.VK_F1:
			number++;
			if ((modifiers & KeyEvent.ALT_DOWN_MASK) == 0) {
				// pressing <Alt><F4> changes the selection ... not good
				// <fn> toggles the corresponding filter
				// <ctrl><fn> selects an additional candidate for filtering
				// <shift><fn> additionally toggles the filter mode
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
					// just toggle the candidate
					showHintCellValues[number] = !showHintCellValues[number];
					// no bivalue cells!
					showHintCellValues[10] = false;
				} else {
					
					if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
						invalidCells = !invalidCells;
					}
					setShowHintCellValue(number);
				}
				checkIsShowInvalidOrPossibleCells();
			}
			break;
		case KeyEvent.VK_SPACE:
			int candidate = getShowHintCellValue();
			if (isShowInvalidOrPossibleCells() && candidate != 0) {
				changed = toggleCandidateInAktCells(candidate);
			}
			break;
		case KeyEvent.VK_X:
			cellZoomPanel.swapColors();
			break;
		case KeyEvent.VK_E:
			number++;
		case KeyEvent.VK_D:
			number++;
		case KeyEvent.VK_C:
			number++;
		case KeyEvent.VK_B:
			number++;
		case KeyEvent.VK_A:
			
			// if ctrl or alt or meta is pressed, it's a shortcut
			if ((modifiers & KeyEvent.ALT_DOWN_MASK) != 0 || 
				(modifiers & KeyEvent.ALT_GRAPH_DOWN_MASK) != 0	|| 
				(modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 || 
				(modifiers & KeyEvent.META_DOWN_MASK) != 0) {
				// do nothing!
				break;
			}
			
			// calculate index in coloringColors[]
			number *= 2;
			if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
				number++;
			}
			
			handleColoring(-1, Options.getInstance().getColoringColors()[number]);
			
			break;
		case KeyEvent.VK_R:
			clearColoring();
			break;
		case KeyEvent.VK_GREATER:
		case KeyEvent.VK_COMMA:
		case KeyEvent.VK_LESS:
		case KeyEvent.VK_PERIOD:
		default:
			// doesn't work on all keyboards :-(
			// more precisely: doesn't work, if the keyboard layout in the OS
			// doesn't match the physical layout of the keyboard
			short rem = sudoku.getRemainingCandidates();
			char ch = evt.getKeyChar();
			if (ch == '<' || ch == '>' || ch == ',' || ch == '.') {
				boolean isUp = ch == '>' || ch == '.';
				if (isShowInvalidOrPossibleCells()) {
					int cand = 0;
					for (int i = 1; i < showHintCellValues.length - 1; i++) {
						if (showHintCellValues[i]) {
							cand = i;
							if (!isUp) {
								// get the first candidate
								break;
							}
						}
					}
					
					int count = 0;
					do {
						if (isUp) {
							cand++;
							if (cand > Sudoku2.UNITS) {
								cand = 1;
							}
						} else {
							cand--;
							if (cand < 1) {
								cand = Sudoku2.UNITS;
							}
						}
						count++;
					} while (count < 8 && (Sudoku2.MASKS[cand] & rem) == 0);
					
					if (count < 8) {
						// if only one candidate is left for filtering,
						// it would be toggled without this check
						setShowHintCellValue(cand);
						checkIsShowInvalidOrPossibleCells();
					}
				}
			}
			
			break;
		}
		
		if (changed) {
			redoStack.clear();
			checkProgress();
		} else {
			undoStack.pop();
		}
		
		updateCellZoomPanel();
		mainFrame.check();
		repaint();
	}

	/**
	 * Clears a selected region of cells
	 */
	private void clearRegion() {

		clearSelection();
		shiftRow = -1;
		shiftCol = -1;
		
		Integer index = Integer.valueOf(Sudoku2.getIndex(getActiveRow(), getActiveCol()));
		if (!cellSelection.contains(index)) {
			//cellSelection.add(index);
			setActiveCell(index);
		}
	}

	/**
	 * Select all cells in the rectangle defined by {@link #activeRow}/{@link #activeCol}
	 * and row/col
	 *
	 * @param row
	 * @param col
	 */
	private void selectRegion(int row, int col) {
		
		Integer startIndex = Integer.valueOf(Sudoku2.getIndex(getFirstRow(), getFirstCol()));
		Integer endIndex = Integer.valueOf(Sudoku2.getIndex(getActiveRow(), getActiveCol()));
		
		//clearSelection();
		if (row == getActiveRow() && col == getActiveCol()) {
			// same cell clicked twice -> no region selected -> do nothing
		} else {
			
			// every cell in the region gets selected, aktRow and aktCol are not changed
			int cStart = col < getActiveCol() ? col : getActiveCol();
			int rStart = row < getActiveRow() ? row : getActiveRow();
			int cEnd = cStart + Math.abs(col - getActiveCol());
			int rEnd = rStart + Math.abs(row - getActiveRow());
			
			// make sure selection starts with the same index.
			cellSelection.clear();
			cellSelection.add(startIndex);
			
			for (int c = cStart; c <= cEnd; c++) {
				for (int r = rStart; r <= rEnd; r++) {
					Integer cv = Integer.valueOf(Sudoku2.getIndex(r, c));
					if (!cellSelection.contains(cv)) {
						cellSelection.add(cv);
					}
				}
			}
			
			// fix the end index (just in case)
			setActiveCell(endIndex);
			/*
			if (!cellSelection.contains(endIndex)) {
				cellSelection.add(endIndex);
			} else {
				cellSelection.remove(endIndex);
				cellSelection.add(endIndex);
			}*/
		}
	}

	/**
	 * Initializes {@link #shiftRow}/{@link #shiftCol} for selecting regions of
	 * cells using the keyboard
	 */
	private void setShift() {
		if (shiftRow == -1) {
			shiftRow = getFirstRow();
			shiftCol = getFirstCol();
		}
	}

	/**
	 * Finds the next colored cell, if filters are applied. mode gives the direction
	 * in which to search (as KeyEvent). The search wraps at sudoku boundaries.
	 *
	 * @param row
	 * @param col
	 * @param mode
	 * @return
	 */
	private int findNextHintCandidate(int row, int col, int mode) {

		int index = Sudoku2.getIndex(row, col);
		int showHintCellValue = getShowHintCellValue();

		if (showHintCellValue == 0) {
			return index;
		}

		switch (mode) {
		case KeyEvent.VK_DOWN:
			
			// let's start with the next row
			row++;
			if (row == Sudoku2.UNITS) {
				row = 0;
				col++;
				if (col == Sudoku2.UNITS) {
					return index;
				}
			}
			
			for (int i = col; i < Sudoku2.UNITS; i++) {
				int j = i == col ? row : 0;
				for (; j < Sudoku2.UNITS; j++) {
					if (sudoku.getValue(j, i) == 0 && sudoku.isCandidate(j, i, showHintCellValue, !showCandidates)) {
						return Sudoku2.getIndex(j, i);
					}
				}
			}
			
			break;
		case KeyEvent.VK_UP:
			// let's start with the previous row
			row--;
			if (row < 0) {
				row = 8;
				col--;
				if (col < 0) {
					return index;
				}
			}
			
			for (int i = col; i >= 0; i--) {
				int j = i == col ? row : 8;
				for (; j >= 0; j--) {
					if (sudoku.getValue(j, i) == 0 && sudoku.isCandidate(j, i, showHintCellValue, !showCandidates)) {
						return Sudoku2.getIndex(j, i);
					}
				}
			}
			break;
		case KeyEvent.VK_LEFT:
			// lets start left
			index--;
			if (index < 0) {
				return index + 1;
			}
			
			while (index >= 0) {
				if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, showHintCellValue, !showCandidates)) {
					return index;
				}
				index--;
			}
			
			if (index < 0) {
				index = Sudoku2.getIndex(row, col);
			}
			
			break;
		case KeyEvent.VK_RIGHT:
			// lets start right
			index++;
			if (index >= sudoku.getCells().length) {
				return index - 1;
			}
			
			while (index < sudoku.getCells().length) {
				if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, showHintCellValue, !showCandidates)) {
					return index;
				}
				index++;
			}
			
			if (index >= sudoku.getCells().length) {
				index = Sudoku2.getIndex(row, col);
			}
			
			break;
		}
		return index;
	}
	
	public void clearCandidateColors() {
		coloringCandidateMap.clear();
		coloringCandidateMap.clear();
		updateCellZoomPanel();
		mainFrame.check();
	}
	
	public void clearCellColors() {
		coloringMap.clear();
		coloringMap.clear();
		updateCellZoomPanel();
		mainFrame.check();
	}

	/**
	 * Removes all coloring info
	 */
	public void clearColoring() {
		coloringMap.clear();
		coloringCandidateMap.clear();
		updateCellZoomPanel();
		mainFrame.check();
	}

	/**
	 * Handles coloring for all selected cells, delegates to
	 * {@link #handleColoring(int, int, int, int)} (see description there).
	 *
	 * @param candidate
	 * @param colorNumber
	 */
	public void handleColoring(int candidate, Color color) {
		if (cellSelection.isEmpty()) {
			handleColoring(getActiveRow(), getActiveCol(), candidate, color);
		} else {
			for (int index : cellSelection) {
				handleColoring(Sudoku2.getRow(index), Sudoku2.getCol(index), candidate, color);
			}
		}
	}

	public Color getActiveColor() {
		
		if (cellZoomPanel.isDefaultMouse()) {
			return null;
		}
		
		return cellZoomPanel.getPrimaryColor();
	}
	
	/**
	 * Toggles Color for candidate in active cell; only called from
	 * {@link CellZoomPanel}.
	 *
	 * @param candidate
	 */
	public void handleColoring(int candidate) {
		handleColoring(getActiveRow(), getActiveCol(), candidate, cellZoomPanel.getPrimaryColor());
		updateCellZoomPanel();
		mainFrame.fixFocus();
		repaint();
	}

	/**
	 * Handles the coloring of a cell or a candidate. If candidate equals -1, a cell
	 * is to be coloured, else a candidate. If the target is already colored and the
	 * new color matches the old one, coloring is removed, else it is set to the new
	 * color.<br>
	 * {@link Options#colorValues} decides, whether cells, that have already been
	 * set, may be colored.
	 *
	 * @param row
	 * @param col
	 * @param candidate
	 * @param colorNumber
	 */
	public void handleColoring(int row, int col, int candidate, Color color) {
		
		if (!Options.getInstance().isColorValues() && sudoku.getValue(row, col) != 0) {
			return;
		}
		
		SortedMap<Integer, Color> map = coloringMap;
		int key = Sudoku2.getIndex(row, col);
		if (candidate != -1) {
			key = key * 10 + candidate;
			map = coloringCandidateMap;
		}
		
		if (map.containsKey(key) && map.get(key) == color) {
			// pressing the same key on the same cell twice removes the coloring
			map.remove(key);
		} else {
			// either newly colored cell or change of cell color
			map.put(key, color);
		}
		
		updateCellZoomPanel();
	}

	/**
	 * Handles "set value" done in {@link CellZoomPanel}. Should not be used
	 * otherwise.
	 *
	 * @param number
	 */
	public void setCellFromCellZoomPanel(int number) {
		
		undoStack.push(sudoku.clone());
		if (cellSelection.isEmpty()) {
			setCell(getActiveRow(), getActiveCol(), number);
		} else {
			for (int index : cellSelection) {
				setCell(Sudoku2.getRow(index), Sudoku2.getCol(index), number);
			}
		}
		
		updateCellZoomPanel();
		mainFrame.check();
		repaint();
	}

	public void setCell(int row, int col, int number) {

		int index = Sudoku2.getIndex(row, col);
		if (!sudoku.isFixed(index) && sudoku.getValue(index) != number) {
			
			if (sudoku.getValue(index) != 0) {
				sudoku.setCell(row, col, 0);
			}
			
			sudoku.setCell(row, col, number);
			repaint();
			
			if (sudoku.isSolved() && Options.getInstance().isShowSudokuSolved()) {
				JOptionPane.showMessageDialog(
					this,
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudoku_solved"),
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.congratulations"),
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}

	/**
	 * Toggles candidate in all active cells (all cells in {@link #cellSelection} or
	 * cell denoted by {@link #activeRow}/{@link #activeCol} if {@link #cellSelection} is
	 * empty).<br>
	 *
	 * @param candidate
	 * @return <code>true</code>, if at least one cell was changed
	 */
	private boolean toggleCandidateInAktCells(int candidate) {
		
		boolean changed = false;
		if (cellSelection.isEmpty()) {
			toggleCandidateInCell(getActiveRow(), getActiveCol(), candidate);
			return true;
		} else {
			
			boolean candPresent = false;
			for (int index : cellSelection) {
				if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, candidate, !showCandidates)) {
					candPresent = true;
					break;
				}
			}
			
			for (int index : cellSelection) {
				if (candPresent) {
					if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, candidate, !showCandidates)) {
						sudoku.setCandidate(index, candidate, false, !showCandidates);
						changed = true;
					}
				} else {
					if (sudoku.getValue(index) == 0 && !sudoku.isCandidate(index, candidate, !showCandidates)) {
						sudoku.setCandidate(index, candidate, true, !showCandidates);
						changed = true;
					}
				}
			}
		}
		
		updateCellZoomPanel();
		return changed;
	}

	/**
	 * Toggles candidate in the cell denoted by row/col. Uses
	 * {@link #candidateMode}.
	 *
	 * @param row
	 * @param col
	 * @param candidate
	 */
	private void toggleCandidateInCell(int row, int col, int candidate) {
		
		int index = Sudoku2.getIndex(row, col);
		if (sudoku.getValue(index) == 0) {
			if (sudoku.isCandidate(index, candidate, !showCandidates)) {
				sudoku.setCandidate(index, candidate, false, !showCandidates);
			} else {
				sudoku.setCandidate(index, candidate, true, !showCandidates);
			}
		}
		
		updateCellZoomPanel();
	}

	/**
	 * Creates an image of the current sudoku in the given size.
	 *
	 * @param size
	 * @return
	 */
	public BufferedImage getSudokuImage(int size) {
		return getSudokuImage(size, false);
	}

	/**
	 * Creates an image of the current sudoku in the given size.
	 *
	 * @param size
	 * @param allBlack
	 * @return
	 */
	public BufferedImage getSudokuImage(int size, boolean allBlack) {
		BufferedImage fileImage = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = fileImage.createGraphics();
		this.g2 = g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, size, size);
		drawPage(size, size, true, false, allBlack, 1.0);
		return fileImage;
	}

	/**
	 * Prints the current sudoku into the graphics context <code>g</code> at the
	 * position <code>x</code>/ <code>y</code> with size <code>size</code>.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param size
	 * @param allBlack
	 * @param scale
	 */
	public void printSudoku(Graphics2D g, int x, int y, int size, boolean allBlack, double scale) {
		Graphics2D oldG2 = this.g2;
		this.g2 = g;
		AffineTransform trans = g.getTransform();
		g.translate(x, y);
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, size, size);
		drawPage(size, size, true, true, allBlack, scale);
		g.setTransform(trans);
		this.g2 = oldG2;
	}

	/**
	 * Writes an image of the current sudoku as png into a file. The image is
	 * <code>size</code> pixels wide and high, the resolution in the png file is set
	 * to <code>dpi</code>.
	 *
	 * @param file
	 * @param size
	 * @param dpi
	 */
	public void saveSudokuAsPNG(File file, int size, int dpi) {
		BufferedImage fileImage = getSudokuImage(size);
		writePNG(fileImage, dpi, file);
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		
		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}

		// CAUTION: The Graphics2D object is created with the native printer
		// resolution, but scaled down to 72dpi using an AffineTransform.
		// To print in high resolution this downscaling has to be reverted.
		Graphics2D printG2 = (Graphics2D) graphics;
		double scale = SudokuUtil.adjustGraphicsForPrinting(printG2);
		
		printG2.translate((int) (pageFormat.getImageableX() * scale), (int) (pageFormat.getImageableY() * scale));
		int printWidth = (int) (pageFormat.getImageableWidth() * scale);
		int printHeight = (int) (pageFormat.getImageableHeight() * scale);

		// scale fonts up too fit the printer resolution
		Font tmpFont = Options.getInstance().getBigFont();
		bigFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
		tmpFont = Options.getInstance().getSmallFont();
		smallFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
		printG2.setFont(bigFont);
		String title = MainFrame.VERSION;
		FontMetrics metrics = printG2.getFontMetrics();
		int textWidth = metrics.stringWidth(title);
		int textHeight = metrics.getHeight();
		int y = 2 * textHeight;
		printG2.drawString(title, (printWidth - textWidth) / 2, textHeight);

		// Level
		printG2.setFont(smallFont);
		if (sudoku != null && sudoku.getLevel() != null) {
			title = sudoku.getLevel().getName() + " (" + sudoku.getScore() + ")";
			metrics = printG2.getFontMetrics();
			textWidth = metrics.stringWidth(title);
			textHeight = metrics.getHeight();
			printG2.drawString(title, (printWidth - textWidth) / 2, y);
			y += textHeight;
		}

		printG2.translate(0, y);
		this.g2 = printG2;
		drawPage(printWidth, printHeight, true, true, false, scale);
		return Printable.PAGE_EXISTS;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		drawPage(getBounds().width, getBounds().height, false, true, false, 1.0);
	}

	/**
	 * Draws the sudoku in its current state on the graphics context denoted by
	 * {@link #g2} (code>g2</code> has to be set before calling this method). The
	 * graphics context can belong to a <code>Component</code> (basic redraw), a
	 * <code>BufferedImage</code> (save Sudoku as image) or to a print canvas.<br>
	 * <br>
	 *
	 * Sudokus are always drawn as quads, even if <code>totalWidth</code> and
	 * <code>totalHeight</code> are not the same. The quadrat is then center within
	 * the available space.
	 *
	 * @param totalWidth  The width of the sudoku in pixel
	 * @param totalHeight The height of the sudoku in pixel
	 * @param isPrint     The sudoku is drawn on a print canvas: always draw at the
	 *                    upper left corner and dont draw a cursor
	 * @param withBorder  A white border of at least {@link #DELTA_RAND} pixels is
	 *                    drawn around the sudoku.
	 * @param allBlack    Replace all colors with black. Should only be used, if
	 *                    filters, steps or coloring are not used.
	 * @param scale       Necessary for high resolution printing
	 */
	private void drawPage(
			int totalWidth, 
			int totalHeight, 
			boolean isPrint, 
			boolean withBorder, 
			boolean allBlack,
			double scale) {

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if (lastCursorChanged == -1) {
			lastCursorChanged = System.currentTimeMillis();
		}
		
		gridRegion = calculateGridRegion(new Rectangle(0, 0, totalWidth, totalHeight), isPrint, withBorder);

		int colorKuCellSize = (int) (cellSize * 0.9);

		// get the fonts every time the size of the grid changes or
		// the user selects a different font in the preferences dialog
		Font tmpFont = Options.getInstance().getDefaultValueFont();
		if (valueFont != null) {
			if (!valueFont.getName().equals(tmpFont.getName()) || 
				valueFont.getStyle() != tmpFont.getStyle() || 
				valueFont.getSize() != ((int) (cellSize * Options.getInstance().getValueFontFactor()))) {
				valueFont = new Font(
					tmpFont.getName(), 
					tmpFont.getStyle(),
					(int) (cellSize * Options.getInstance().getValueFontFactor())
				);
			}
		}

		tmpFont = Options.getInstance().getDefaultCandidateFont();
		if (candidateFont != null) {
			if (!candidateFont.getName().equals(tmpFont.getName()) || 
				candidateFont.getStyle() != tmpFont.getStyle() || 
				candidateFont.getSize() != ((int) (cellSize * Options.getInstance().getCandidateFontFactor()))) {
				
				int oldCandidateHeight = candidateHeight;
				candidateFont = new Font(
					tmpFont.getName(), 
					tmpFont.getStyle(),
					(int) (cellSize * Options.getInstance().getCandidateFontFactor())
				);
				
				FontMetrics cm = getFontMetrics(candidateFont);
				candidateHeight = (int) ((cm.getAscent() - cm.getDescent()) * 1.3);
				
				if (candidateHeight != oldCandidateHeight) {
					resetColorKuImages();
				}
			}
		}

		if (oldWidth != gridRegion.width) {
			
			int oldCandidateHeight = candidateHeight;
			oldWidth = gridRegion.width;
			valueFont = new Font(
				Options.getInstance().getDefaultValueFont().getName(),
				Options.getInstance().getDefaultValueFont().getStyle(),
				(int) (cellSize * Options.getInstance().getValueFontFactor())
			);
			candidateFont = new Font(
				Options.getInstance().getDefaultCandidateFont().getName(),
				Options.getInstance().getDefaultCandidateFont().getStyle(),
				(int) (cellSize * Options.getInstance().getCandidateFontFactor())
			);
			
			FontMetrics cm = getFontMetrics(candidateFont);
			candidateHeight = (int) ((cm.getAscent() - cm.getDescent()) * 1.3);
			
			if (candidateHeight != oldCandidateHeight) {
				resetColorKuImages();
			}
		}

		// draw the cells
		// dx, dy: Offset in a cell for drawing values
		// dcx, dcy: Offset in one nineth of a cell for drawing candidates
		// ddx, ddy: Height and width of the background circle for a candidate
		// more specifically: ddy is the diameter of the background circle
		double dx = 0, dy = 0, dcx = 0, dcy = 0, ddy = 0;
		for (int row = 0; row < Sudoku2.UNITS; row++) {
			for (int col = 0; col < Sudoku2.UNITS; col++) {

				// background first (ignore allBlack here!)
				g2.setColor(Options.getInstance().getDefaultCellColor());
				if (Sudoku2.getBlock(Sudoku2.getIndex(row, col)) % 2 != 0) {
					// every other block may have a different background color
					g2.setColor(Options.getInstance().getAlternateCellColor());
				}

				int cellIndex = Sudoku2.getIndex(row, col);
				boolean isSelected = 
					(row == getActiveRow() && col == getActiveCol()) || 
					cellSelection.contains(Integer.valueOf(cellIndex));
				
				// the cell doesn't count as selected, if the last change of the cursor has been a while
				if (isSelected && cellSelection.size() == 1 && Options.getInstance().isDeleteCursorDisplay()) {
					if ((System.currentTimeMillis() - lastCursorChanged) > Options.getInstance().getDeleteCursorDisplayLength()) {
						isSelected = false;
					}
				}
				
				// don't paint the whole cell yellow, just a small frame, if onlySmallCursors is set
				if (isSelected && !isPrint && !Options.getInstance().isOnlySmallCursors()) {
					setColor(g2, allBlack, Options.getInstance().getAktCellColor());
				}
				
				// check if the candidate denoted by showHintCellValue is a valid candidate; if
				// showCandidates == true,
				// this can be done by SudokuCell.isCandidateValid(); if it is false, candidates
				// entered by the user
				// are highlighted, regardless of validity
				// CHANGE: no filters if showCandiates == false
				boolean candidateValid = false;
				if (showInvalidOrPossibleCells) {
					if (showCandidates) {
						candidateValid = sudoku.areCandidatesValid(cellIndex, showHintCellValues, false);
					}
				}
				
				boolean isHighlightingBivalue = showHintCellValues[10];
				boolean isCellBivalue = sudoku.getAllCandidates(cellIndex).length == 2;
				
				// highlight (filter)
				if (isShowInvalidOrPossibleCells()) {
					
					if (!isInvalidCells()) {
						
						// highlighting
						if (sudoku.getValue(cellIndex) == 0 && 
							candidateValid && 
							!Options.getInstance().isOnlySmallFilters()) {
							setColor(g2, allBlack, Options.getInstance().getPossibleCellColor());
						} else if (Options.getInstance().isHighlightingGivens() && 
								   sudoku.getValue(cellIndex) != 0 &&
								   showHintCellValues[sudoku.getValue(cellIndex)]) {
							setColor(g2, allBlack, Options.getInstance().getPossibleFixedCellColor());
						}
						
					} else {
						
						// inverse highlight
						if (isInvalidCells() && 
							(sudoku.getValue(cellIndex) != 0 || 
							(showInvalidOrPossibleCells && !candidateValid))) {
							setColor(g2, allBlack, Options.getInstance().getInvalidCellColor());
						}
					}
				}

				// coloring
				if (coloringMap.containsKey(cellIndex) && 
					(sudoku.getValue(cellIndex) == 0 || 
					Options.getInstance().isColorValues()) &&
					isColoringVisible) {
					setColor(g2, allBlack, coloringMap.get(cellIndex));
				}
				
				// draw the cell background
				int cellX = getX(row, col);
				int cellY = getY(row, col);
				g2.fillRect(cellX, cellY, cellSize, cellSize);
				
				// draw cell selection
				if (isSelected && !isPrint && g2.getColor() != Options.getInstance().getAktCellColor()) {
					
					setColor(g2, allBlack, Options.getInstance().getAktCellColor());
					
					int frameSize = (int) (cellSize * Options.getInstance().getCursorFrameSize());
					
					// make cell selection border smaller then the main selector
					if (row != getActiveRow() || col != getActiveCol()) {
						frameSize = frameSize / 2 + 1;
					}
					
					/*
					if (row == activeRow && col == getActiveCol()) {
						g2.setColor(new Color(0.1f, 0.3f, 0.7f, 0.1f));
						g2.fillRect(cellX, cellY, cellSize, cellSize);
						g2.setColor(new Color(0.1f, 0.3f, 0.7f, 0.5f));
						frameSize = 1;
					}*/
				
					((Graphics)g2).fillRect(cellX, cellY, cellSize, frameSize+1);
					((Graphics)g2).fillRect(cellX, cellY, frameSize+1, cellSize);
					((Graphics)g2).fillRect(cellX + cellSize - frameSize, cellY, frameSize, cellSize);
					((Graphics)g2).fillRect(cellX, cellY + cellSize - frameSize, cellSize, frameSize);	
				}

				if (showCandidateHighlight()) {

					// draw candidate mouse highlight
					if (lastCandidateMouseOn != null && 
						lastCandidateMouseOn.getIndex() >= 0 && 
						lastCandidateMouseOn.getIndex() < Sudoku2.LENGTH) {

						int cellColumn = lastCandidateMouseOn.getIndex() % Sudoku2.UNITS;
						int cellRow = lastCandidateMouseOn.getIndex() / Sudoku2.UNITS;

						if (row == cellRow && 
							col == cellColumn && 
							sudoku.getValue(lastCandidateMouseOn.getIndex()) == 0) {

							int startX = getX(cellRow, cellColumn);
							int startY = getY(cellRow, cellColumn);
							double third = cellSize / 3.0;
							double shiftX = ((lastCandidateMouseOn.getValue() - 1) % 3) * third;
							double shiftY = ((lastCandidateMouseOn.getValue() - 1) / 3) * third;
							FontMetrics cm = getFontMetrics(candidateFont);
							g2.setFont(candidateFont);
							int candidate = lastCandidateMouseOn.getValue();
							int cw = (int) (third - g2.getFontMetrics().stringWidth(Integer.toString(candidate)));
							int ch = (int) ((cm.getAscent() - cm.getDescent()) * 1.3);
							int ccx = (int) Math.round(startX + shiftX + third / 2.0 - cw / 2.0);
							int ccy = (int) Math.round(startY + shiftY + third / 2.0 - ch / 2.0);
							dcx = (third - g2.getFontMetrics().stringWidth("8")) / 2.0;
							dcy = (third + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2.0;

							if (!sudoku.isCandidate(lastCandidateMouseOn.getIndex(), lastCandidateMouseOn.getValue())) {
								g2.setColor(new Color(0.2f, 0.2f, 0.2f, 0.3f));
								g2.drawString(
									Integer.toString(candidate), 
									(int) Math.round(startX + dcx + shiftX),
									(int) Math.round(startY + dcy + shiftY)
								);
							}

							g2.setColor(new Color(0.1f, 0.3f, 0.8f, 0.1f));
							g2.fillRect(ccx, ccy, cw, ch);
							g2.setColor(new Color(0.1f, 0.3f, 0.8f, 0.2f));
							g2.drawRect(ccx, ccy, cw, ch);
						}
					}
				}

				// background is done, draw the value
				int startX = getX(row, col);
				int startY = getY(row, col);
				Color offColor = null;
				int offCand = 0;
				if (sudoku.getValue(cellIndex) != 0) {
					
					// value set in cell: draw it
					setColor(g2, allBlack, Options.getInstance().getCellValueColor());
					if (sudoku.isFixed(cellIndex)) {
						setColor(g2, allBlack, Options.getInstance().getCellFixedValueColor());
					} else if (isShowWrongValues() && !sudoku.isValidValue(row, col, sudoku.getValue(cellIndex))) {
						offColor = Options.getInstance().getColorKuColor(10);
						offCand = 10;
						setColor(g2, allBlack, Options.getInstance().getWrongValueColor());
					} else if (isShowDeviations() && sudoku.isSolutionSet()	&& sudoku.getValue(cellIndex) != sudoku.getSolution(cellIndex)) {
						offColor = Options.getInstance().getColorKuColor(11);
						offCand = 11;
						setColor(g2, allBlack, Options.getInstance().getDeviationColor());
					}
					
					g2.setFont(valueFont);
					dx = (cellSize - g2.getFontMetrics().stringWidth("8")) / 2.0;
					dy = (cellSize + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2.0;
					int value = sudoku.getValue(cellIndex);
					if (Options.getInstance().isShowColorKuAct()) {
						
						// draw the corresponding icon
						drawColorBox(
							value, g2, 
							getX(row, col) + (cellSize - colorKuCellSize) / 2,
							getY(row, col) + (cellSize - colorKuCellSize) / 2, 
							colorKuCellSize, true
						);

						if (offColor != null) {
							// invalid values or deviations are shown with an "X" in different colors
							setColor(g2, allBlack, offColor);
							g2.drawString("X", (int) (startX + dx), (int) (startY + dy));
						}
						
					} else {
						// draw the value
						g2.drawString(Integer.toString(value), (int) (startX + dx), (int) (startY + dy));
					}

				} else {
					
					// draw the candidates equally distributed within the cell
					// if showCandidates is false, the candidates are drawn anyway, if
					// the user presses <shift><ctrl> (current cell - showAllCandidatesAkt)
					// or <shift><alt> (all cells - showAllCandidates)
					g2.setFont(candidateFont);
					boolean userCandidates = !showCandidates;
					if (showAllCandidates || showAllCandidatesAkt && row == getActiveRow() && col == getActiveCol()) {
						userCandidates = false;
					}
					
					// calculate the width of the space for one candidate
					double third = cellSize / 3.0;
					dcx = (third - g2.getFontMetrics().stringWidth("8")) / 2.0;
					dcy = (third + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2.0;
					ddy = (g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) * Options.getInstance().getHintBackFactor();

					for (int i = 1; i <= Sudoku2.UNITS; i++) {
						
						offColor = null;
						// one candidate at a time
						if (sudoku.isCandidate(cellIndex, i, userCandidates) || 
							(showCandidates && showDeviations && sudoku.isSolutionSet() && i == sudoku.getSolution(cellIndex))) {
							
							Color hintColor = null;
							Color candColor = null;
							candColor = Options.getInstance().getCandidateColor();
							double shiftX = ((i - 1) % 3) * third;
							double shiftY = ((i - 1) / 3) * third;
							
							if (Options.getInstance().isShowColorKuAct()) {
								// Colorku has to be drawm here, or filters, coloring, hints wont be visible
								int ccx = (int) Math.round(startX + shiftX + third / 2.0 - candidateHeight / 2.0);
								int ccy = (int) Math.round(startY + shiftY + third / 2.0 - candidateHeight / 2.0);
								drawColorBox(i, g2, ccx, ccy, candidateHeight, false);
							}

							if (step != null) {
								
								int index = Sudoku2.getIndex(row, col);
								if (step.getIndices().indexOf(index) >= 0 && step.getValues().indexOf(i) >= 0) {
									hintColor = Options.getInstance().getHintCandidateBackColor();
									candColor = Options.getInstance().getHintCandidateColor();
								}
								
								int alsIndex = step.getAlsIndex(index, chainIndex);
								if (alsIndex != -1 && ((chainIndex == -1 && !step.getType().isKrakenFish())
										|| alsToShow.contains(alsIndex))) {
									hintColor = Options.getInstance().getHintCandidateAlsBackColors()[alsIndex
											% Options.getInstance().getHintCandidateAlsBackColors().length];
									candColor = Options.getInstance().getHintCandidateAlsColors()[alsIndex
											% Options.getInstance().getHintCandidateAlsColors().length];
								}
								
								for (int k = 0; k < step.getChains().size(); k++) {
									
									if (step.getType().isKrakenFish() && chainIndex == -1) {
										// Index 0 means show no chain at all
										continue;
									}
									
									if (chainIndex != -1 && k != chainIndex) {
										// show only one chain in Forcing Chains/Nets
										continue;
									}
									
									Chain chain = step.getChains().get(k);
									for (int j = chain.getStart(); j <= chain.getEnd(); j++) {
										if (chain.getChain()[j] == Integer.MIN_VALUE) {
											// Trennmarker fï¿½r mins -> ignorieren
											continue;
										}
										
										int chainEntry = Math.abs(chain.getChain()[j]);
										int index1 = -1, index2 = -1, index3 = -1;
										if (Chain.getSNodeType(chainEntry) == Chain.NORMAL_NODE) {
											index1 = Chain.getSCellIndex(chainEntry);
										}
										
										if (Chain.getSNodeType(chainEntry) == Chain.GROUP_NODE) {
											index1 = Chain.getSCellIndex(chainEntry);
											index2 = Chain.getSCellIndex2(chainEntry);
											index3 = Chain.getSCellIndex3(chainEntry);
										}
										
										if ((index == index1 || index == index2 || index == index3)
												&& Chain.getSCandidate(chainEntry) == i) {
											if (Chain.isSStrong(chainEntry)) {
												// strong link
												hintColor = Options.getInstance().getHintCandidateBackColor();
												candColor = Options.getInstance().getHintCandidateColor();
											} else {
												hintColor = Options.getInstance().getHintCandidateFinBackColor();
												candColor = Options.getInstance().getHintCandidateFinColor();
											}
										}
									}
								}
								
								for (Candidate cand : step.getFins()) {
									if (cand.getIndex() == index && cand.getValue() == i) {
										hintColor = Options.getInstance().getHintCandidateFinBackColor();
										candColor = Options.getInstance().getHintCandidateFinColor();
									}
								}
								
								for (Candidate cand : step.getEndoFins()) {
									if (cand.getIndex() == index && cand.getValue() == i) {
										hintColor = Options.getInstance().getHintCandidateEndoFinBackColor();
										candColor = Options.getInstance().getHintCandidateEndoFinColor();
									}
								}
								
								if (step.getValues().contains(i) && step.getColorCandidates().containsKey(index)) {
									hintColor = Options.getInstance().getColoringColors()[step.getColorCandidates()
											.get(index)];
									candColor = Options.getInstance().getCandidateColor();
								}
								
								for (Candidate cand : step.getCandidatesToDelete()) {
									if (cand.getIndex() == index && cand.getValue() == i) {
										hintColor = Options.getInstance().getHintCandidateDeleteBackColor();
										candColor = Options.getInstance().getHintCandidateDeleteColor();
									}
								}
								
								for (Candidate cand : step.getCannibalistic()) {
									if (cand.getIndex() == index && cand.getValue() == i) {
										hintColor = Options.getInstance().getHintCandidateCannibalisticBackColor();
										candColor = Options.getInstance().getHintCandidateCannibalisticColor();
									}
								}
							}
							
							if (isShowWrongValues() == true && !sudoku.isCandidateValid(cellIndex, i, userCandidates)) {
								offColor = Options.getInstance().getColorKuColor(10);
								offCand = 10;
								candColor = Options.getInstance().getWrongValueColor();
							}
							
							if (!sudoku.isCandidate(cellIndex, i, userCandidates) && isShowDeviations()
									&& sudoku.isSolutionSet() && i == sudoku.getSolution(cellIndex)) {
								offColor = Options.getInstance().getColorKuColor(11);
								offCand = 11;
								candColor = Options.getInstance().getDeviationColor();
							}
							
							boolean isFilteringCandidates = 
									isShowInvalidOrPossibleCells() && 
									!isInvalidCells() && 
									showHintCellValues[i] && 
									Options.getInstance().isOnlySmallFilters();
							
							boolean isFilteringBivalueCandidates = 
									sudoku.getValue(cellIndex) == 0 && 
								    Options.getInstance().isOnlySmallFilters() &&
								    isHighlightingBivalue &&
								    isCellBivalue;

							// highlight/filters candidates instead of cells
							if (candidateValid && (isFilteringCandidates || isFilteringBivalueCandidates) && !isInvalidCells()) {
							
								if (isFilteringCandidates) {
									setColor(g2, allBlack, Options.getInstance().getPossibleCellColor());
								} else if (isFilteringBivalueCandidates) {
									setColor(g2, allBlack, Options.getInstance().getPossibleCellColor());
								}
								
								g2.fillRect(
									(int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
									(int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
									(int) Math.round(ddy), (int) Math.round(ddy)
								);
							}

							// Coloring
							Color coloringColor = null;
							if (isColoringVisible && coloringCandidateMap.containsKey(cellIndex * 10 + i)) {
								coloringColor = coloringCandidateMap.get(cellIndex * 10 + i);
							}

							if (coloringColor != null) {
								setColor(g2, allBlack, coloringColor);
								g2.fillRect(
									(int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
									(int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
									(int) Math.round(ddy), (int) Math.round(ddy)
								);
							}
							
							if (hintColor != null) {
								setColor(g2, allBlack, hintColor);
								g2.fillOval(
									(int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
									(int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
									(int) Math.round(ddy), (int) Math.round(ddy)
								);
							}
							
							setColor(g2, allBlack, candColor);
							if (!Options.getInstance().isShowColorKuAct()) {
								g2.drawString(
									Integer.toString(i), 
									(int) Math.round(startX + dcx + shiftX),
									(int) Math.round(startY + dcy + shiftY)
								);
							} else {
								if (offColor != null) {
									int ccx = (int) Math.round(startX + shiftX + third / 2.0 - candidateHeight / 2.0);
									int ccy = (int) Math.round(startY + shiftY + third / 2.0 - candidateHeight / 2.0);
									drawColorBox(offCand, g2, ccx, ccy, candidateHeight, false);
								}
							}

						}
					}
				}
			}
		}

		switch (Options.getInstance().getDrawMode()) {
		case 0:
			
			if (allBlack) {
				g2.setStroke(new BasicStroke(strokeWidth / 2));
			} else {
				g2.setStroke(new BasicStroke(strokeWidth));
			}
			
			setColor(g2, allBlack, Options.getInstance().getInnerGridColor());
			drawBlockLine(delta + gridRegion.x, 1 * delta + gridRegion.y, true);
			drawBlockLine(delta + gridRegion.x, 2 * delta + gridRegion.y + 3 * cellSize, true);
			drawBlockLine(delta + gridRegion.x, 3 * delta + gridRegion.y + 6 * cellSize, true);
			setColor(g2, allBlack, Options.getInstance().getGridColor());
			g2.setStroke(new BasicStroke(boxStrokeWidth));
			g2.drawRect(gridRegion.x, gridRegion.y, gridRegion.width, gridRegion.height);
			
			for (int i = 0; i < 3; i++) {
				
				g2.drawRect(
					(i + 1) * delta + gridRegion.x + i * 3 * cellSize,
					1 * delta + gridRegion.y,
					3 * cellSize,
					3 * cellSize
				);
				
				g2.drawRect(
					(i + 1) * delta + gridRegion.x + i * 3 * cellSize,
					2 * delta + gridRegion.y + 3 * cellSize,
					3 * cellSize,
					3 * cellSize
				);
				
				g2.drawRect(
					(i + 1) * delta + gridRegion.x + i * 3 * cellSize,
					3 * delta + gridRegion.y + 6 * cellSize,
					3 * cellSize,
					3 * cellSize
				);
			}
			
			break;
			
		case 1:
			
			if (allBlack) {
				g2.setStroke(new BasicStroke(strokeWidth / 2));
			} else {
				g2.setStroke(new BasicStroke(strokeWidth));
			}
			
			setColor(g2, allBlack, Options.getInstance().getInnerGridColor());
			drawBlockLine(delta + gridRegion.x, 1 * delta + gridRegion.y, false);
			drawBlockLine(delta + gridRegion.x, 2 * delta + gridRegion.y + 3 * cellSize, false);
			drawBlockLine(delta + gridRegion.x, 3 * delta + gridRegion.y + 6 * cellSize, false);
			setColor(g2, allBlack, Options.getInstance().getGridColor());
			g2.setStroke(new BasicStroke(boxStrokeWidth));
			g2.drawRect(gridRegion.x, gridRegion.y, gridRegion.width, gridRegion.height);
			
			for (int i = 0; i < 3; i++) {
				g2.drawLine(gridRegion.x, gridRegion.y + i * 3 * cellSize, gridRegion.x + Sudoku2.UNITS * cellSize, gridRegion.y + i * 3 * cellSize);
				g2.drawLine(gridRegion.x + i * 3 * cellSize, gridRegion.y, gridRegion.x + i * 3 * cellSize, gridRegion.y + Sudoku2.UNITS * cellSize);
			}
			
			break;
		}

		if (step != null && !step.getChains().isEmpty()) {
			
			points.clear();

			for (int ci = 0; ci < step.getChainAnz(); ci++) {
				
				if (step.getType().isKrakenFish() && chainIndex == -1) {
					continue;
				}
				
				if (chainIndex != -1 && chainIndex != ci) {
					continue;
				}
				
				Chain chain = step.getChains().get(ci);
				for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
					int che = Math.abs(chain.getChain()[i]);
					points.add(getCandKoord(Chain.getSCellIndex(che), Chain.getSCandidate(che), cellSize));
					if (Chain.getSNodeType(che) == Chain.GROUP_NODE) {
						int indexC = Chain.getSCellIndex2(che);
						if (indexC != -1) {
							points.add(getCandKoord(indexC, Chain.getSCandidate(che), cellSize));
						}
						indexC = Chain.getSCellIndex3(che);
						if (indexC != -1) {
							points.add(getCandKoord(indexC, Chain.getSCandidate(che), cellSize));
						}
					}
				}
			}
			
			for (Candidate cand : step.getCandidatesToDelete()) {
				points.add(getCandKoord(cand.getIndex(), cand.getValue(), cellSize));
			}

			for (int ai = 0; ai < step.getAlses().size(); ai++) {
				
				if (step.getType().isKrakenFish() && chainIndex == -1) {
					continue;
				}
				
				if (chainIndex != -1 && !alsToShow.contains(ai)) {
					continue;
				}
				
				AlsInSolutionStep als = step.getAlses().get(ai);
				for (int i = 0; i < als.getIndices().size(); i++) {
					int index = als.getIndices().get(i);
					int[] cands = sudoku.getAllCandidates(index);
					for (int j = 0; j < cands.length; j++) {
						points.add(getCandKoord(index, cands[j], cellSize));
					}
				}
			}

			for (int ci = 0; ci < step.getChainAnz(); ci++) {
				
				if (step.getType().isKrakenFish() && chainIndex == -1) {
					continue;
				}
				
				if (chainIndex != -1 && ci != chainIndex) {
					continue;
				}
				
				Chain chain = step.getChains().get(ci);
				drawChain(g2, chain, cellSize, ddy, allBlack);
			}
		}
	}

	/**
	 * Convenience method to make printing in all black easier.
	 *
	 * @param g2
	 * @param color
	 * @param allBlack
	 */
	private void setColor(Graphics2D g2, boolean allBlack, Color color) {
		if (allBlack) {
			g2.setColor(Color.BLACK);
		} else {
			g2.setColor(color);
		}
	}

	/**
	 * Draws a chain.
	 * <ul>
	 * <li>Calculate the end points of each link</li>
	 * <li>Check, if another node is on the direct line between the end points</li>
	 * <li>If so, draw a Bezier curve instead of a line (tangents are 45 degrees of
	 * the direct line)</li>
	 * <li>If the length is very small, the link is ommitted</li>
	 * </ul>
	 *
	 * @param g2
	 * @param chain
	 * @param cellSize
	 * @param ddy
	 * @param allBlack
	 */
	private void drawChain(Graphics2D g2, Chain chain, int cellSize, double ddy, boolean allBlack) {
		// Calculate the coordinates of the startpoint for every link
		int[] ch = chain.getChain();
		List<Point2D.Double> points1 = new ArrayList<Point2D.Double>(chain.getEnd() + 1);
		for (int i = 0; i <= chain.getEnd(); i++) {
			if (i < chain.getStart()) {
				// belongs to some other chain-> ignore!
				points1.add(null);
				continue;
			}
			int che = Math.abs(ch[i]);
			points1.add(getCandKoord(Chain.getSCellIndex(che), Chain.getSCandidate(che), cellSize));
		}
		
		Stroke oldStroke = g2.getStroke();
		int oldChe = 0;
		int oldIndex = 0;
		int index = 0;
		for (int i = chain.getStart(); i < chain.getEnd(); i++) {
			// link is only drawn between different cells
			if (ch[i + 1] == Integer.MIN_VALUE) {
				// end point of a net branch -> ignore
				continue;
			}
			
			index = i;
			int che = Math.abs(ch[i]);
			int che1 = Math.abs(ch[i + 1]);
			if (ch[i] > 0 && ch[i + 1] < 0) {
				oldChe = che;
				oldIndex = i;
			}
			
			if (ch[i] == Integer.MIN_VALUE && ch[i + 1] < 0) {
				che = oldChe;
				index = oldIndex;
			}
			
			if (ch[i] < 0 && ch[i + 1] > 0) {
				che = oldChe;
				index = oldIndex;
			}
			
			if (Chain.getSCellIndex(che) == Chain.getSCellIndex(che1)) {
				// same cell -> ignore
				continue;
			}
			
			setColor(g2, allBlack, Options.getInstance().getArrowColor());
			if (Chain.isSStrong(che1)) {
				g2.setStroke(strongLinkStroke);
			} else {
				g2.setStroke(weakLinkStroke);
			}
			
			drawArrow(g2, index, i + 1, cellSize, ddy, points1);
		}
		
		g2.setStroke(oldStroke);

	}

	private void drawArrow(Graphics2D g2, int index1, int index2, int cellSize, double ddy,	List<Point2D.Double> points1) {
		
		// calculate the start and end points for the arrow
		Point2D.Double p1 = (java.awt.geom.Point2D.Double) (points1.get(index1).clone());
		Point2D.Double p2 = (java.awt.geom.Point2D.Double) (points1.get(index2).clone());
		double length = p1.distance(p2);
		double deltaX = p2.x - p1.x;
		double deltaY = p2.y - p1.y;
		double alpha = Math.atan2(deltaY, deltaX);
		adjustEndPoints(p1, p2, alpha, ddy);

		// check, if another candidate lies on the direct line
		double epsilon = 0.1;
		double dx1 = deltaX;
		double dy1 = deltaY;
		boolean doesIntersect = false;
		for (int i = 0; i < points.size(); i++) {
			
			if (points.get(i).equals(points1.get(index1)) || points.get(i).equals(points1.get(index2))) {
				continue;
			}
			
			Point2D.Double point = points.get(i);
			double dx2 = point.x - p1.x;
			double dy2 = point.y - p1.y;

			if (Math.signum(dx1) == Math.signum(dx2) && 
				Math.signum(dy1) == Math.signum(dy2) && 
				Math.abs(dx2) <= Math.abs(dx1) && 
				Math.abs(dy2) <= Math.abs(dy1)) {
				if (dx1 == 0.0 || dy1 == 0.0 || Math.abs(dx1 / dy1 - dx2 / dy2) < epsilon) {
					doesIntersect = true;
					break;
				}
			}
		}
		if (length < 2.0 * ddy) {
			// line is very short, would not be seen if drawn directly
			doesIntersect = true;
		}

		// values for arrow head
		double aAlpha = alpha;

		// draw the line of the arrow
		if (doesIntersect) {
			
			double bezierLength = 20.0;
			
			// adjust for very short lines
			if (length < 2.0 * ddy) {
				bezierLength = length / 4.0;
			}
			
			// the end points are rotated 45 degrees (counter clockwise for the
			// start point, clockwise for the end point)
			rotatePoint(points1.get(index1), p1, -Math.PI / 4.0);
			rotatePoint(points1.get(index2), p2, Math.PI / 4.0);

			aAlpha = alpha - Math.PI / 4.0;
			double bX1 = p1.x + bezierLength * Math.cos(aAlpha);
			double bY1 = p1.y + bezierLength * Math.sin(aAlpha);
			aAlpha = alpha + Math.PI / 4.0;
			double bX2 = p2.x - bezierLength * Math.cos(aAlpha);
			double bY2 = p2.y - bezierLength * Math.sin(aAlpha);
			cubicCurve.setCurve(p1.x, p1.y, bX1, bY1, bX2, bY2, p2.x, p2.y);
			g2.draw(cubicCurve);

		} else {
			g2.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x), (int) Math.round(p2.y));
		}

		g2.setStroke(arrowStroke);
		double arrowLength = cellSize * arrowLengthFactor;
		double arrowHeight = arrowLength * arrowHeightFactor;
		if (length > (arrowLength * 2 + ddy)) {
			// calculate values for arrow head
			double sin = Math.sin(aAlpha);
			double cos = Math.cos(aAlpha);
			double aX = p2.x - cos * arrowLength;
			double aY = p2.y - sin * arrowLength;
			
			if (doesIntersect) {
				// try to calculate the real intersection point of
				// the bezier curve with the arrows middle line
				// the distance between p2 and aX/aY must be arrowLength
				// aX/aY should lie on the cubic curve
				double aXTemp = 0;
				double aYTemp = 0;
				double eps = Double.MAX_VALUE;
				double[] tmpPoints = new double[6];
				PathIterator pIt = cubicCurve.getPathIterator(null, 0.01);
				while (!pIt.isDone()) {
					
					@SuppressWarnings("unused")
					int type = pIt.currentSegment(tmpPoints);
					double dist = p2.distance(tmpPoints[0], tmpPoints[1]);
					
					if (Math.abs(dist - arrowLength) < eps) {
						eps = Math.abs(dist - arrowLength);
						aXTemp = tmpPoints[0];
						aYTemp = tmpPoints[1];
					}
					
					pIt.next();
				}
				
				// ok, closest point is now in aXTemp/aYTemp
				aX = aXTemp;
				aY = aYTemp;
				aAlpha = Math.atan2(p2.y - aY, p2.x - aX);
				sin = Math.sin(aAlpha);
				cos = Math.cos(aAlpha);
			}
			
			double daX = sin * arrowHeight;
			double daY = cos * arrowHeight;
			arrow.reset();
			arrow.addPoint((int) Math.round(aX - daX), (int) Math.round(aY + daY));
			arrow.addPoint((int) Math.round(p2.x), (int) Math.round(p2.y));
			arrow.addPoint((int) Math.round(aX + daX), (int) Math.round(aY - daY));
			g2.fill(arrow);
			g2.draw(arrow);
		}
	}

	/**
	 * Rotate <code>p2</code> <code>angle</code> degrees counterclockwise around
	 * <code>p1</code>.
	 *
	 * @param p1
	 * @param p2
	 * @param angle
	 */
	private void rotatePoint(Point2D.Double p1, Point2D.Double p2, double angle) {
		
		// translate p2 to 0/0
		p2.x -= p1.x;
		p2.y -= p1.y;

		// rotate angle degrees
		double sinAngle = Math.sin(angle);
		double cosAngle = Math.cos(angle);
		double xact = p2.x;
		double yact = p2.y;
		
		p2.x = xact * cosAngle - yact * sinAngle;
		p2.y = xact * sinAngle + yact * cosAngle;
		p2.x += p1.x;
		p2.y += p1.y;
	}

	/**
	 * Adjust the end points of an arrow: the arrow should start and end outside the
	 * circular background of the candidate.
	 *
	 * @param p1
	 * @param p2
	 * @param alpha
	 * @param ddy
	 */
	private void adjustEndPoints(Point2D.Double p1, Point2D.Double p2, double alpha, double ddy) {
		
		double tmpDelta = ddy / 2.0 + 4.0;
		int pX = (int) (tmpDelta * Math.cos(alpha));
		int pY = (int) (tmpDelta * Math.sin(alpha));
		
		p1.x += pX;
		p1.y += pY;
		p2.x -= pX;
		p2.y -= pY;
	}

	/**
	 * Returns the center of the position of a candidate in the grid.
	 *
	 * @param index
	 * @param cand
	 * @param cellSize
	 * @return
	 */
	private Point2D.Double getCandKoord(int index, int cand, int cellSize) {
		double third = cellSize / 3;
		double startX = getX(Sudoku2.getRow(index), Sudoku2.getCol(index));
		double startY = getY(Sudoku2.getRow(index), Sudoku2.getCol(index));
		double shiftX = ((cand - 1) % 3) * third;
		double shiftY = ((cand - 1) / 3) * third;
		double x = startX + shiftX + third / 2.0;
		double y = startY + shiftY + third / 2.0;
		return new Point2D.Double(x, y);
	}

	public int getX(int row, int col) {
		
		int x = col * cellSize + delta + gridRegion.x;
		if (col > 2) {
			x += delta;
		}
		
		if (col > 5) {
			x += delta;
		}
		
		return x;
	}

	public int getY(int row, int col) {
		
		int y = row * cellSize + delta + gridRegion.y;
		if (row > 2) {
			y += delta;
		}
		
		if (row > 5) {
			y += delta;
		}
		
		return y;
	}

	private int getRow(Point p) {
		
		double tmp = p.y - gridRegion.y - delta;
		if ((tmp >= 3 * cellSize && tmp <= 3 * cellSize + delta)
				|| (tmp >= 6 * cellSize + delta && tmp <= 6 * cellSize + 2 * delta)) {
			return -1;
		}
		
		if (tmp > 3 * cellSize) {
			tmp -= delta;
		}
		
		if (tmp > 6 * cellSize) {
			tmp -= delta;
		}
		
		return (int) Math.ceil((tmp / cellSize) - 1);
	}

	private int getCol(Point p) {
		
		double tmp = p.x - gridRegion.x - delta;
		
		if ((tmp >= 3 * cellSize && tmp <= 3 * cellSize + delta)
				|| (tmp >= 6 * cellSize + delta && tmp <= 6 * cellSize + 2 * delta)) {
			return -1;
		}
		
		if (tmp > 3 * cellSize) {
			tmp -= delta;
		}
		
		if (tmp > 6 * cellSize) {
			tmp -= delta;
		}
		
		return (int) Math.ceil((tmp / cellSize) - 1);
	}

	/**
	 * Checks whether a candidate has been clicked. The correct values for font
	 * metrics and candidate factors are ignored: the valid candidate region is
	 * simple the corresponding ninth of the cell.<br>
	 * <br>
	 *
	 * @param p    The point of a mouse click
	 * @param row The row, in which p lies (may be -1 for "invalid")
	 * @param col  The column, in which p lies (may be -1 for "invalid")
	 * @return The number of a candidate, if a click could be confirmed, or else -1
	 */
	public int getCandidate(Point p, int row, int col) {
		
		// check if a cell was clicked
		if (row < 0 || col < 0) {
			// clicked between cells -> cant mean a candidate
			return -1;
		}
		
		// calculate the coordinates of the left upper corner of the cell
		double startX = gridRegion.x + col * cellSize;
		if (col > 2) {
			startX += delta;
		}
		
		if (col > 5) {
			startX += delta;
		}
		
		double startY = gridRegion.y + row * cellSize;
		if (row > 2) {
			startY += delta;
		}
		
		if (row > 5) {
			startY += delta;
		}
		
		// now check if a candidate was clicked
		int candidate = -1;
		double cs3 = cellSize / 3.0;		
		double dx = cs3;
		double leftDx = 0;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				
				double sx = startX + i * cs3 + leftDx;
				double sy = startY + j * cs3 + leftDx;

				// candidate was clicked
				if (p.x >= sx && p.x <= sx + dx && p.y >= sy && p.y <= sy + dx) {
					candidate = j * 3 + i + 1;
					return candidate;
				}
			}
		}
		
		return -1;
	}

	public void setActiveColor(Color color) {
		
		if (color == null) {
			
			// reset everything to normal
			if (oldCursor != null) {
				setCursor(oldCursor);
				colorCursor = null;
				colorCursorShift = null;
			}
			
		} else {
			
			// create new Cursors and set them
			if (oldCursor == null) {
				oldCursor = getCursor();
			}
			
			createColorCursors();
			setCursor(colorCursor);
		}
		
		// no region selects are allowed in coloring
		clearRegion();
		updateCellZoomPanel();
	}

	public void resetActiveColor() {
		Color temp = getActiveColor();
		setActiveColor(null);
		setActiveColor(temp);
	}

	private void drawBlockLine(int x, int y, boolean withRect) {
		drawBlock(x, y, withRect);
		drawBlock(x + 3 * cellSize + delta, y, withRect);
		drawBlock(x + 6 * cellSize + 2 * delta, y, withRect);
	}

	private void drawBlock(int x, int y, boolean withRect) {
		
		if (withRect) {
			g2.drawRect(x, y, 3 * cellSize, 3 * cellSize);
		}
		
		g2.drawLine(x, y + 1 * cellSize, x + 3 * cellSize, y + 1 * cellSize);
		g2.drawLine(x, y + 2 * cellSize, x + 3 * cellSize, y + 2 * cellSize);
		g2.drawLine(x + 1 * cellSize, y, x + 1 * cellSize, y + 3 * cellSize);
		g2.drawLine(x + 2 * cellSize, y, x + 2 * cellSize, y + 3 * cellSize);
	}

	public Sudoku2 getSudoku() {
		return sudoku;
	}

	public boolean isShowCandidates() {
		return showCandidates;
	}

	public final void setShowCandidates(boolean showCandidates) {
		this.showCandidates = showCandidates;
		repaint();
	}

	public boolean isShowWrongValues() {
		return showWrongValues;
	}

	public void setShowWrongValues(boolean showWrongValues) {
		this.showWrongValues = showWrongValues;
		repaint();
	}

	public boolean undoPossible() {
		return undoStack.size() > 0;
	}

	public boolean redoPossible() {
		return redoStack.size() > 0;
	}

	public void undo() {
		if (undoPossible()) {
			redoStack.push(sudoku);
			sudoku = undoStack.pop();
			updateCellZoomPanel();
			checkProgress();
			mainFrame.setCurrentLevel(sudoku.getLevel());
			mainFrame.setCurrentScore(sudoku.getScore());
			mainFrame.check();
			repaint();
		}
	}

	public void redo() {
		if (redoPossible()) {
			undoStack.push(sudoku);
			sudoku = redoStack.pop();
			updateCellZoomPanel();
			checkProgress();
			mainFrame.setCurrentLevel(sudoku.getLevel());
			mainFrame.setCurrentScore(sudoku.getScore());
			mainFrame.check();
			repaint();
		}
	}

	/**
	 * Clears undo/redo. Is called from {@link SolutionPanel} when a step is double
	 * clicked.
	 */
	public void clearUndoRedo() {
		undoStack.clear();
		redoStack.clear();
	}

	public void setSudoku(Sudoku2 newSudoku) {
		setSudoku(newSudoku.getSudoku(ClipboardMode.PM_GRID, null), false);
	}

	public void setSudoku(Sudoku2 newSudoku, boolean alreadySolved) {
		setSudoku(newSudoku.getSudoku(ClipboardMode.PM_GRID, null), alreadySolved);
	}

	public void setSudoku(String init) {
		setSudoku(init, false);
	}

	public void setSudoku(String init, boolean alreadySolved) {

		step = null;
		setChainInStep(-1);
		undoStack.clear();
		redoStack.clear();
		coloringMap.clear();
		resetShowHintCellValues();
		
		clearSelection();
		clearDragSelection();
		cellSelection.clear();
		cellSelection.add(Integer.valueOf(Sudoku2.getIndex(4, 4)));
		isCtrlDown = false;
		lastPressedRow = -1;
		lastPressedCol = -1;
		lastPressedCandidate = -1;
		lastClickedRow = -1;
		lastClickedCol = -1;
		lastClickedCandidate = -1;
		lastClickedTime = -1;
		shiftRow = -1;
		shiftCol = -1;
		lastHighlightedDigit = 0;
		isColoringVisible = true;

		if (init == null || init.length() == 0) {
			sudoku = new Sudoku2();
		} else {
			sudoku.setSudoku(init);
			// the sudoku must be set in the solver to reset the step list
			// (otherwise the result panels are not updated correctly)
			sudoku.setLevel(Options.getInstance().getDifficultyLevels()[DifficultyType.EASY.ordinal()]);
			sudoku.setScore(0);
			Sudoku2 tmpSudoku = sudoku.clone();
			if (!alreadySolved) {
				getSolver().setSudoku(tmpSudoku);
			}
			// boolean unique = generator.validSolution(sudoku);
			int anzSolutions = generator.getNumberOfSolutions(sudoku, 1);
			if (anzSolutions == 0) {
				JOptionPane.showMessageDialog(this,
						java.util.ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.no_solution"),
						java.util.ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.invalid_puzzle"),
						JOptionPane.ERROR_MESSAGE);
				sudoku.setStatus(SudokuStatus.INVALID);
			} else if (anzSolutions > 1) {
				JOptionPane.showMessageDialog(this,
						java.util.ResourceBundle.getBundle("intl/SudokuPanel")
								.getString("SudokuPanel.multiple_solutions"),
						java.util.ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.invalid_puzzle"),
						JOptionPane.ERROR_MESSAGE);
				sudoku.setStatus(SudokuStatus.MULTIPLE_SOLUTIONS);
			} else {
				if (!sudoku.checkSudoku()) {
					JOptionPane.showMessageDialog(this,
							java.util.ResourceBundle.getBundle("intl/SudokuPanel")
									.getString("SudokuPanel.wrong_values"),
							java.util.ResourceBundle.getBundle("intl/SudokuPanel")
									.getString("SudokuPanel.invalid_puzzle"),
							JOptionPane.ERROR_MESSAGE);
					sudoku.setStatus(SudokuStatus.INVALID);
				} else {
					sudoku.setStatus(SudokuStatus.VALID);
					if (sudoku.getFixedCellsAnz() > 17) {
						Sudoku2 fixedOnly = new Sudoku2();
						fixedOnly.setSudoku(sudoku.getSudoku(ClipboardMode.CLUES_ONLY));
						int anzFixedSol = generator.getNumberOfSolutions(fixedOnly, 1);
						sudoku.setStatusGivens(anzFixedSol);
					}

					if (!alreadySolved) {
						tmpSudoku.setStatus(SudokuStatus.VALID);
						tmpSudoku.setStatusGivens(sudoku.getStatusGivens());
						tmpSudoku.setSolution(sudoku.getSolution());
						getSolver().solve(true);
					}

					sudoku.setLevel(getSolver().getSudoku().getLevel());
					sudoku.setScore(getSolver().getSudoku().getScore());
				}
			}
		}

		updateCellZoomPanel();
		
		if (mainFrame != null) {
			mainFrame.setCurrentLevel(sudoku.getLevel());
			mainFrame.setCurrentScore(sudoku.getScore());
			mainFrame.check();
		}

		repaint();
	}

	public String getSudokuString(ClipboardMode mode) {
		return sudoku.getSudoku(mode, step);
	}

	public SudokuSolver getSolver() {
		return solver;
	}

	/**
	 * Solves the sudoku to a certain point: if game mode is playing, the sudoku is
	 * solved until the first non progress step is reached; in all other modes the
	 * solving stops, when the first training step has been reached.
	 */
	public void solveUpTo() {
		
		SolutionStep actStep = null;
		boolean changed = false;
		undoStack.push(sudoku.clone());
		GameMode gm = Options.getInstance().getGameMode();
		
		while ((actStep = solver.getHint(sudoku, false)) != null) {
			
			if (actStep.isGiveUp()) {
				// should display a message saying it failed, but I don;t know where the log UI is located.
				break;
			} else if (gm == GameMode.PLAYING) {
				if (!actStep.getType().getStepConfig().isEnabledProgress()) {
					// solving stops
					break;
				}
			} else {
				if (actStep.getType().getStepConfig().isEnabledTraining()) {
					// solving stops
					break;
				}
			}
			
			// still here? do the step
			getSolver().doStep(sudoku, actStep);
			changed = true;
		}
			
		/*
		if (actStep.isGiveUp()) {
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dont_know"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
				JOptionPane.ERROR_MESSAGE
			);
			break;
		}*/
		
		if (changed) {
			redoStack.clear();
		} else {
			undoStack.pop();
		}
		
		step = null;
		setChainInStep(-1);
		updateCellZoomPanel();
		checkProgress();
		mainFrame.check();
		repaint();
	}

	/**
	 * When solving manually, the sudoku can be in an invalid state. This should be
	 * handled before calling this method.
	 *
	 * @param singlesOnly
	 * @return
	 */
	public SolutionStep getNextStep(boolean singlesOnly) {
		step = solver.getHint(sudoku, singlesOnly);
		setChainInStep(-1);
		repaint();
		return step;
	}

	public void setStep(SolutionStep step) {
		this.step = step;
		setChainInStep(-1);
		repaint();
	}

	public SolutionStep getStep() {
		return step;
	}

	public void setChainInStep(int chainIndex) {
		
		if (step == null) {
			chainIndex = -1;
		} else if (step.getType().isKrakenFish() && chainIndex > -1) {
			chainIndex--;
		}
		
		if (chainIndex >= 0 && chainIndex > step.getChainAnz() - 1) {
			chainIndex = -1;
		}
		
		this.chainIndex = chainIndex;
		alsToShow.clear();
		if (chainIndex != -1) {
			Chain chain = step.getChains().get(chainIndex);
			for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
				if (chain.getNodeType(i) == Chain.ALS_NODE) {
					alsToShow.add(Chain.getSAlsIndex(chain.getChain()[i]));
				}
			}
		}

		repaint();
	}

	public void doStep() {
		
		if (step != null) {
			
			undoStack.push(sudoku.clone());
			redoStack.clear();
			getSolver().doStep(sudoku, step);
			step = null;
			setChainInStep(-1);
			updateCellZoomPanel();
			checkProgress();
			mainFrame.check();
			repaint();
			
			if (sudoku.isSolved() && Options.getInstance().isShowSudokuSolved()) {
				JOptionPane.showMessageDialog(
					this,
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudoku_solved"),
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.congratulations"),
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}

	public void abortStep() {
		step = null;
		setChainInStep(-1);
		repaint();
	}

	public int getSolvedCellsAnz() {
		return sudoku.getSolvedCellsAnz();
	}

	public void setNoClues() {
		sudoku.setNoClues();
		repaint();
	}

	public boolean isInvalidCells() {
		return invalidCells;
	}

	public void setInvalidCells(boolean invalidCells) {
		this.invalidCells = invalidCells;
	}

	public boolean isShowInvalidOrPossibleCells() {
		return showInvalidOrPossibleCells;
	}

	public void setShowInvalidOrPossibleCells(boolean showInvalidOrPossibleCells) {
		this.showInvalidOrPossibleCells = showInvalidOrPossibleCells;
	}

	public boolean[] getShowHintCellValues() {
		return showHintCellValues;
	}

	public void setShowHintCellValues(boolean[] showHintCellValues) {
		this.showHintCellValues = showHintCellValues;
	}

	public void setShowHintCellValue(int candidate) {
		
		if (candidate == 10) {
			// filter bivalue cells
			for (int i = 0; i < showHintCellValues.length - 1; i++) {
				showHintCellValues[i] = false;
			}
			showHintCellValues[10] = !showHintCellValues[10];
		} else {
			showHintCellValues[10] = false;
			for (int i = 0; i < showHintCellValues.length - 1; i++) {
				if (i == candidate) {
					showHintCellValues[i] = !showHintCellValues[i];
				} else {
					showHintCellValues[i] = false;
				}
			}
		}
	}

	public void resetShowHintCellValues() {
		for (int i = 1; i < showHintCellValues.length; i++) {
			showHintCellValues[i] = false;
		}
		showInvalidOrPossibleCells = false;
	}

	public boolean isShowDeviations() {
		return showDeviations;
	}

	public void setShowDeviations(boolean showDeviations) {
		this.showDeviations = showDeviations;
		mainFrame.check();
		repaint();
	}

	/**
	 * Schreibt ein BufferedImage in eine PNG-Datei. Dabei wird die Auflï¿½sung
	 * in die Metadaten der Datei geschrieben, was alles etwas kompliziert macht.
	 *
	 * @param bi       Zu zeichnendes Bild
	 * @param dpi      Auflï¿½sung in dots per inch
	 * @param fileName Pfad und Name der neuen Bilddatei
	 */
	private void writePNG(BufferedImage bi, int dpi, File file) {
		
		Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName("png");
		// are there any jpeg encoders available?

		// there's at least one ImageWriter, just use the first one
		if (i.hasNext()) {
			
			ImageWriter imageWriter = i.next();
			ImageWriteParam param = imageWriter.getDefaultWriteParam();
			ImageTypeSpecifier its = new ImageTypeSpecifier(bi.getColorModel(), bi.getSampleModel());
			IIOMetadata iomd = imageWriter.getDefaultImageMetadata(its, param);
			String formatName = "javax_imageio_png_1.0";
			Node node = iomd.getAsTree(formatName);

			int dpiRes = (int) (dpi / 2.54 * 100);
			IIOMetadataNode res = new IIOMetadataNode("pHYs");
			res.setAttribute("pixelsPerUnitXAxis", String.valueOf(dpiRes));
			res.setAttribute("pixelsPerUnitYAxis", String.valueOf(dpiRes));
			res.setAttribute("unitSpecifier", "meter");
			node.appendChild(res);

			try {
				iomd.setFromTree(formatName, node);
			} catch (IIOInvalidTreeException e) {
				JOptionPane.showMessageDialog(
					this, 
					e.getLocalizedMessage(),
					java.util.ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.error"),
					JOptionPane.ERROR_MESSAGE
				);
			}
			
			// attach the metadata to an image
			IIOImage iioimage = new IIOImage(bi, null, iomd);
			try {
				
				FileImageOutputStream out = new FileImageOutputStream(file);
				imageWriter.setOutput(out);
				imageWriter.write(iioimage);
				out.close();

				String companionFileName = file.getPath();
				if (companionFileName.toLowerCase().endsWith(".png")) {
					companionFileName = companionFileName.substring(0, companionFileName.length() - 4);
				}
				
				companionFileName += ".txt";
				PrintWriter cOut = new PrintWriter(new BufferedWriter(new FileWriter(companionFileName)));
				cOut.println(getSudokuString(ClipboardMode.CLUES_ONLY));
				cOut.println(getSudokuString(ClipboardMode.LIBRARY));
				cOut.println(getSudokuString(ClipboardMode.PM_GRID));
				if (step != null) {
					cOut.println(getSudokuString(ClipboardMode.PM_GRID_WITH_STEP));
				}
				
				cOut.close();
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(
					this, 
					e.getLocalizedMessage(),
					java.util.ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.error"),
					JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}

	/**
	 * @param colorCells the colorCells to set
	 */
	/*
	public void setColorCells(boolean colorCells) {
		this.isColoringCells = colorCells;
		Options.getInstance().setColorCells(colorCells);
		updateCellZoomPanel();
	}*/
	
	public void updateColorCursor() {
		createColorCursors();
		setCursor(colorCursor);
	}

	/**
	 * Creates cursors for coloring: The color is specified by
	 * {@link #aktColorIndex}, cursors for both colors of the pair are created and
	 * stored in {@link #colorCursor} and {@link #colorCursorShift}.
	 */
	private void createColorCursors() {
		
		try {
			
			Point cursorHotSpot = new Point(2, 4);
			BufferedImage img1 = ImageIO.read(getClass().getResource("/img/c_color.png"));
			Graphics2D gImg1 = (Graphics2D) img1.getGraphics();
			gImg1.setColor(cellZoomPanel.getPrimaryColor());
			gImg1.fillRect(19, 18, 12, 12);

			colorCursor = Toolkit.getDefaultToolkit().createCustomCursor(img1, cursorHotSpot, "c_strong");

			BufferedImage img2 = ImageIO.read(getClass().getResource("/img/c_color.png"));
			Graphics2D gImg2 = (Graphics2D) img2.getGraphics();
			gImg2.setColor(cellZoomPanel.getPrimaryColor());

			gImg2.fillRect(19, 18, 12, 12);
			colorCursorShift = Toolkit.getDefaultToolkit().createCustomCursor(img2, cursorHotSpot, "c_weak");
			
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error creating color cursors", ex);
		}
	}

	/**
	 * Checks whether the candidate in the given cell is a Hidden Single.
	 *
	 * @param candidate
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean isHiddenSingle(int candidate, int row, int col) {
		
		// sometimes the internal singles queues get corrupted
		sudoku.rebuildInternalData();
		SudokuStepFinder finder = SudokuSolverFactory.getDefaultSolverInstance().getStepFinder();
		List<SolutionStep> steps = finder.findAllHiddenSingles(sudoku);
		for (SolutionStep act : steps) {
			if (act.getType() == SolutionType.HIDDEN_SINGLE && 
				act.getValues().get(0) == candidate	&& 
				act.getIndices().get(0) == Sudoku2.getIndex(row, col)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean showCandidateHighlight() {

		/*
		 * isCtrlDown was originally used to do two things: 1. toggle candidate
		 * visibility on double click 2. toggle cell selection on single click
		 * 
		 * Those functions don't leave any room for showCandidateHighlight. They
		 * conflict together, and such, I should come up with a new mechanism to toggle
		 * their state; however, for the time being, I will simply provide an option to
		 * keep it permanently on, or off. I find it too distracting.
		 */

		return Options.getInstance().isShowCandidateHighlight() /* || isCtrlDown */;
	}

	/**
	 * Collects the intersection or union of all valid candidates in all selected
	 * cells. Used to adjust the popup menu.
	 *
	 * @param intersection
	 * @return
	 */
	public SudokuSet collectCandidates(boolean intersection) {
		
		SudokuSet resultSet = new SudokuSet();
		SudokuSet tmpSet = new SudokuSet();
		
		if (intersection) {
			resultSet.setAll();
		}
		
		if (cellSelection.isEmpty()) {
			if (sudoku.getValue(getActiveRow(), getActiveCol()) == 0) {
				// get candidates only when cell is not set!
				sudoku.getCandidateSet(getActiveRow(), getActiveCol(), tmpSet);
				if (intersection) {
					resultSet.and(tmpSet);
				} else {
					resultSet.or(tmpSet);
				}
			}
		} else {
			// BUG: if all cells in the selection are set,
			// all candidates become valid for intersection == true
			boolean emptyCellsOnly = true;
			for (int index : cellSelection) {
				if (sudoku.getValue(index) == 0) {
					emptyCellsOnly = false;
					// get candidates only when cell is not set!
					sudoku.getCandidateSet(index, tmpSet);
					if (intersection) {
						resultSet.and(tmpSet);
					} else {
						resultSet.or(tmpSet);
					}
				}
			}
			
			if (intersection && emptyCellsOnly) {
				resultSet.clear();
			}
		}
		
		return resultSet;
	}

	/**
	 * Removes the candidate from all selected cells.
	 *
	 * @param candidate
	 * @return true if sudoku is changed, false otherwise
	 */
	public boolean removeCandidateFromCellSelection(int candidate) {
		
		boolean changed = false;
		if (cellSelection.isEmpty()) {
			System.err.println("Unexpected error in removeCandidateFromCellSelection, cellSelection should not be empty.");
			return false;
			/*
			int index = Sudoku2.getIndex(getActiveRow(), getActiveCol());
			if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, candidate, !showCandidates)) {
				sudoku.setCandidate(index, candidate, false, !showCandidates);
				changed = true;
			}*/
		} else {
			for (int index : cellSelection) {
				if (sudoku.getValue(index) == 0 && sudoku.isCandidate(index, candidate, !showCandidates)) {
					sudoku.setCandidate(index, candidate, false, !showCandidates);
					changed = true;
				}
			}
		}
		
		return changed;
	}

	public void toggleCandidateFromCellSelection(int candidate) {
		
		if (cellSelection.isEmpty()) {
			System.err.println("Unexpected error in toggleCandidateFromCellSelection, cellSelection should not be empty.");
			return;
		} else {
			for (int index : cellSelection) {
				if (sudoku.getValue(index) == 0) {
					//boolean isCandidateOn = sudoku.isCandidate(index, candidate, !showCandidates);
					//sudoku.setCandidate(index, candidate, !isCandidateOn, !showCandidates);
                                        sudoku.setCandidate(index, candidate, false, !showCandidates);
				}
			}
		}
	}

	/**
	 * Handles candidate changed done in {@link CellZoomPanel}. Should not be used
	 * otherwise.
	 *
	 * @param candidate
	 */
	public void toggleCandidateFromCellZoomPanel(int candidate) {
		
		if (candidate != -1) {
			
			undoStack.push(sudoku.clone());
			boolean changed = false;
			
			if (cellSelection.isEmpty()) {
				System.err.println("Unexpected error in toggleCandidateFromCellZoomPanel, cellSelection should not be empty.");				
				return;
			} else {
				toggleCandidateFromCellSelection(candidate);
				changed = true;
			}
			
			if (changed) {
				redoStack.clear();
				checkProgress();
			} else {
				undoStack.pop();
			}
			
			updateCellZoomPanel();
			mainFrame.check();
			mainFrame.repaint();
		}
	}

	/**
	 * @return the cellZoomPanel
	 */
	public CellZoomPanel getCellZoomPanel() {
		return cellZoomPanel;
	}

	/**
	 * @param cellZoomPanel the cellZoomPanel to set
	 */
	public void setCellZoomPanel(CellZoomPanel cellZoomPanel) {
		this.cellZoomPanel = cellZoomPanel;
	}
	
	public void clearCellColor(Color colorNumber) {
		
		for (int i = 0; i < Sudoku2.LENGTH; i++) {			
			SortedMap<Integer, Color> map = coloringMap;			
			if (map.containsKey(i) && map.get(i) == colorNumber) {
				map.remove(i);
			}
		}

		updateCellZoomPanel();
		repaint();
	}
	
	
	public void clearCandidateColor(Color colorNumber) {

		SortedMap<Integer, Color> map = coloringCandidateMap;
		for (int index = 0; index < Sudoku2.LENGTH; index++) {
			for (int candidate = 1; candidate <= Sudoku2.UNITS; candidate++) {
				int key = index * 10 + candidate;				
				if (map.containsKey(key) && map.get(key) == colorNumber) {
					map.remove(key);
				}		
			}
		}
		
		updateCellZoomPanel();
		repaint();
	}

	public void updateCellZoomPanel() {
		
		if (cellZoomPanel == null) {
			return;
		}
		
		if (cellZoomPanel.isColoring()) {
			
			cellZoomPanel.update(
				SudokuSetBase.EMPTY_SET,
				SudokuSetBase.EMPTY_SET,
				0,
				true,
				null,
				null
			);
			
			return;
		}
		
		int index = Sudoku2.getIndex(getActiveRow(), getActiveCol());
		boolean singleCell = cellSelection.isEmpty() && sudoku.getValue(index) == 0;
		
		if (cellZoomPanel.isDefaultMouse()) {
			// normal operation -> collect candidates for selected cell(s)
			if (sudoku.getValue(index) != 0 && cellSelection.isEmpty()) {
				// cell is already set -> nothing can be selected
				cellZoomPanel.update(
					SudokuSetBase.EMPTY_SET,
					SudokuSetBase.EMPTY_SET,
					index,
					singleCell,
					null,
					null
				);
				
			} else {
				
				SudokuSet valueSet = collectCandidates(true);
				SudokuSet candSet = collectCandidates(false);
				
				cellZoomPanel.update(
					valueSet, 
					candSet, 
					index, 
					singleCell, 
					null, 
					null
				);
			}
			
		} else {
			
			if (!cellSelection.isEmpty() || (cellSelection.isEmpty() && sudoku.getValue(index) != 0)) {
				// no coloring, when set of cells is selected
				cellZoomPanel.update(
					SudokuSetBase.EMPTY_SET,
					SudokuSetBase.EMPTY_SET,
					index,
					singleCell,
					null,
					null
				);
				
			} else {
				
				SudokuSet valueSet = collectCandidates(true);
				SudokuSet candSet = collectCandidates(false);
				
				cellZoomPanel.update(
					valueSet, 
					candSet, 
					index,
					singleCell,
					coloringMap,
					coloringCandidateMap
				);
			}
		}
	}

	/**
	 * Gets a 81 character string. For every digit in that string, the corresponding
	 * cell is set as a given.
	 *
	 * @param givens
	 */
	public void setGivens(String givens) {
		undoStack.push(sudoku.clone());
		sudoku.setGivens(givens);
		updateCellZoomPanel();
		repaint();
		mainFrame.check();
	}

	/**
	 * Checks the progress of the current {@link #sudoku}. If the Sudoku is not yet
	 * valid, only the status of the sudoku is updated. If it is valid, a background
	 * progress check is scheduled.<br>
	 */
	public void checkProgress() {
		
		int anz = sudoku.getSolvedCellsAnz();

		if (anz == 0) {
			sudoku.setStatus(SudokuStatus.EMPTY);
			sudoku.setStatusGivens(SudokuStatus.EMPTY);
		} else if (anz <= 17) {
			sudoku.setStatus(SudokuStatus.INVALID);
			sudoku.setStatusGivens(SudokuStatus.INVALID);
		} else {
			
			if (sudoku.checkSudoku()) {				
				// we have to check!
				int anzSol = generator.getNumberOfSolutions(sudoku, 1000);
				sudoku.setStatus(anzSol);
				// the status of the givens is not changed here; it only changes
				// when the givens themselved are changed
				// sudoku.setStatusGivens(anzSol);
				if (anzSol == 1) {
					// the sudoku is valid -> check the progress
					progressChecker.startCheck(sudoku);
				}	
			}
		}
	}

	/**
	 * Checks the array {@link #showHintCellValues}. If only one value is selected,
	 * that value is returned. If no or more than one values are selected, 0 is
	 * returned.
	 *
	 * @return
	 */
	private int getShowHintCellValue() {
		
		int value = 0;
		for (int i = 1; i < showHintCellValues.length - 1; i++) {
			if (showHintCellValues[i]) {
				if (value == 0) {
					value = i;
				} else {
					// more than one value
					return 0;
				}
			}
		}
		
		return value;
	}

	public void checkIsShowInvalidOrPossibleCells() {
		showInvalidOrPossibleCells = false;
		for (int i = 1; i < showHintCellValues.length; i++) {
			if (showHintCellValues[i]) {
				showInvalidOrPossibleCells = true;
			}
		}
	}
	
	public void setColorIconsInPopupMenu() {
		rightClickMenu.setColorIconsInPopupMenu();
	}

	public void setShowColorKu() {
		rightClickMenu.setColorkuInPopupMenu(Options.getInstance().isShowColorKuAct());
		cellZoomPanel.calculateLayout();
		updateCellZoomPanel();
		repaint();
	}
	
	public void setColorsVisible(boolean isVisible) {
		isColoringVisible = isVisible;
	}

	public void resetColorKuImages() {
		for (int i = 0; i < colorKuImagesLarge.length; i++) {
			colorKuImagesLarge[i] = null;
			colorKuImagesSmall[i] = null;
		}
	}

	private void drawColorBox(int n, Graphics gc, int cx, int cy, int boxSize, boolean large) {
		
		BufferedImage[] images = null;
		
		if (large) {
			images = colorKuImagesLarge;
		} else {
			images = colorKuImagesSmall;
		}
		
		if (images[0] == null || images[0].getWidth() != boxSize) {
			for (int i = 0; i < images.length; i++) {
				images[i] = new ColorKuImage(boxSize, Options.getInstance().getColorKuColor(i + 1));
			}
		}

		gc.drawImage(images[n - 1], cx, cy, null);
	}

	/**
	 * Returns an array, that controls the display of the filter buttons in the
	 * toolbar. For every candidate, that is still present as candidate and thus can
	 * be filtered, the appropriate array element is <code>true</code>.<br>
	 * <br>
	 *
	 * Care has to be taken with prerequisites:
	 * <ul>
	 * <li>If "Show all candidates" is disabled, filtering is not possible</li>
	 * <li>...</li>
	 * </ul>
	 *
	 * @return
	 */
	public boolean[] getRemainingCandidates() {
		
		for (int i = 0; i < remainingCandidates.length; i++) {
			remainingCandidates[i] = false;
		}
		
		if (isShowCandidates()) {
			final int[] cands = Sudoku2.POSSIBLE_VALUES[sudoku.getRemainingCandidates()];
			for (int i = 0; i < cands.length; i++) {
				remainingCandidates[cands[i] - 1] = true;
			}
		}
		
		return remainingCandidates;
	}

	public void clearLastCandidateMouseOn() {
		lastCandidateMouseOn = null;
	}
}
