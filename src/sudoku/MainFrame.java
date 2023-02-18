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

import generator.BackgroundGeneratorThread;
import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
// import sudoku.FileDrop;

/**
 * @author hobiwan
 */
public class MainFrame extends javax.swing.JFrame implements FlavorListener {

	private static final long serialVersionUID = 1L;
	public static final String VERSION = "Hodoku - v2.3.2";

	// public static final String BUILD = "Build 16";
	public static final String BUILD;
	public static final String REV = "$LastChangedRevision: 116 $";

	/** The size of the toggle button icons */
	private static final int TOGGLE_BUTTON_ICON_SIZE = 32;
	private SudokuPanel sudokuPanel;
	// private DifficultyLevel level =
	// Options.getInstance().getDifficultyLevels()[DifficultyType.EASY.ordinal()];
	private JToggleButton[] toggleButtons = new JToggleButton[10];
	/** Icons for the filter toggle buttons in the toolbar (original version) */
	private Icon[] toggleButtonIconsOrg = new Icon[10];
	/** Icons for the filter toggle buttons in the toolbar (original version) */
	private Icon[] emptyToggleButtonIconsOrg = new Icon[10];
	/** Images for the filter toggle button icons (ColorKu version) */
	private ColorKuImage[] toggleButtonImagesColorKu = new ColorKuImage[10];
	/** Icons for the filter toggle buttons in the toolbar (ColorKu version) */
	private Icon[] toggleButtonIconsColorKu = new Icon[10];
	/** Icons for the filter toggle buttons in the toolbar (currently displayed) */
	private Icon[] toggleButtonIcons = new Icon[10];
	/** Icons for the filter toggle buttons in the toolbar (no candidates left) */
	private Icon[] emptyToggleButtonIcons = new Icon[10];
	/** One empty icon for disabled filter buttons - digits */
	@SuppressWarnings("unused")
	private Icon emptyToggleButtonIconOrg = new ImageIcon(getClass().getResource("/img/f_0c.png"));
	/** One empty icon for disabled filter buttons */
	private Icon emptyToggleButtonIconOrgColorKu = new ImageIcon(
			new ColorKuImage(TOGGLE_BUTTON_ICON_SIZE, Color.WHITE));
	/** One empty icon for disabled filter buttons */
	private JRadioButtonMenuItem[] levelMenuItems = new JRadioButtonMenuItem[5];
	private JRadioButtonMenuItem[] modeMenuItems;
	private boolean oldShowDeviations = true;
	/** only set, when the givens are changed */
	private boolean oldShowDeviationsValid = false;
	/** true if new givens are being entered */
	private boolean gameMode = false;
	private SplitPanel splitPanel = new SplitPanel();
	private SummaryPanel summaryPanel = new SummaryPanel(this);
	private SolutionPanel solutionPanel = new SolutionPanel(this);
	private AllStepsPanel allStepsPanel = new AllStepsPanel(this, null);
	private CellZoomPanel cellZoomPanel = new CellZoomPanel(this);
	private JTabbedPane tabPane = new JTabbedPane();
	private PageFormat pageFormat = null;
	private PrinterJob job = null;
	private double saveImageDefaultSize = 800;
	private int bildAufloesung = 96;
	private int bildEinheit = 2;
	
	private MyFileFilter[] puzzleFileSaveFilters = new MyFileFilter[] { 
		new MyFileFilter(1), new MyFileFilter(2), new MyFileFilter(3), 
		new MyFileFilter(4), new MyFileFilter(5), new MyFileFilter(6), 
		new MyFileFilter(7), new MyFileFilter(9)
	};
	
	private MyFileFilter[] puzzleFileLoadFilters = new MyFileFilter[] { 
		new MyFileFilter(1), new MyFileFilter(8), new MyFileFilter(9)
	};
	
	private MyFileFilter[] configFileFilters = new MyFileFilter[] { new MyFileFilter(0) };
	private MyCaretListener caretListener = new MyCaretListener();
	private boolean outerSplitPaneInitialized = false; // used to adjust divider bar at startup!
	private int resetHDivLocLoc = -1; // when resetting windows, the divider location gets changed by some layout function
	private boolean resetHDivLoc = false; // adjust DividerLocation after change
	private long resetHDivLocTicks = 0; // only adjust within a second or so
	private String configFileExt = java.util.ResourceBundle.getBundle("intl/MainFrame")
			.getString("MainFrame.config_file_ext");
	private String solutionFileExt = java.util.ResourceBundle.getBundle("intl/MainFrame")
			.getString("MainFrame.solution_file_ext");
	private String textFileExt = java.util.ResourceBundle.getBundle("intl/MainFrame")
			.getString("MainFrame.text_file_ext");
	private String ssFileExt = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.ss_file_ext");
	private MessageFormat formatter = new MessageFormat("");
	private List<GuiState> savePoints = new ArrayList<GuiState>(); // container for savepoints
	// private GameMode mode = GameMode.PLAYING;
	private boolean changingFullScreenMode = false;
	/** Array with images for progressLabel */
	private ImageIcon[] progressImages = new ImageIcon[Options.DEFAULT_DIFFICULTY_LEVELS.length];
	/**
	 * For progress label: current difficulty level; access must be synchronized!
	 */
	private DifficultyLevel currentLevel = null;
	/** For progress label: current score; access must be synchronized! */
	private int currentScore = 0;
	/** Vage hint button in toolbar */
	private JButton vageHintToggleButton = null;
	/** Concrete hint button in toolbar */
	private JButton concreteHintToggleButton = null;
	/** Show next step button in toolbar */
	private JButton showNextStepToggleButton = null;
	/** Execute next step button in toolbar */
	private JButton executeStepToggleButton = null;
	/** Abort step button in toolbar */
	private JButton abortStepToggleButton = null;
	/** Seperator for hint buttons in toolbar */
	private JSeparator hintSeperator = null;
	/**
	 * A Timer for accessing the clipboard. If the Clipboard is not available when
	 * querying for DataFlavors, it is started an we try again when it expires.
	 */
	private Timer clipTimer = new Timer(100, null);
	/** The file name of the last loaded sudoku file */
	private String sudokuFileName = null;
	/**
	 * The file type of the last loaded sudoku file: 1 .. hsol, 8 .. txt or 9 .. ss
	 */
	private int sudokuFileType = -1;

	private UIExportLine exportWindow;
	private UIImportLine importWindow;
	private UIQuickBrowse quickBrowseWindow;

	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JRadioButtonMenuItem allStepsMenuItem;
	private javax.swing.JMenuItem setAllSinglesMenuItem;
	private javax.swing.JMenu viewMenu;
	private javax.swing.JMenuItem askQuestionMenuItem;
	private javax.swing.JMenuItem backdoorSearchMenuItem;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JRadioButtonMenuItem cellZoomMenuItem;
	private javax.swing.ButtonGroup colorButtonGroup;
	private javax.swing.JRadioButtonMenuItem colorCandidatesMenuItem;
	private javax.swing.JRadioButtonMenuItem colorCellsMenuItem;
	private javax.swing.JMenuItem configMenuItem;
	private javax.swing.JMenuItem copyCluesMenuItem;
	private javax.swing.JMenuItem copyFilledMenuItem;
	private javax.swing.JMenuItem copyLibraryMenuItem;
	private javax.swing.JMenuItem copyPmGridMenuItem;
	private javax.swing.JMenuItem copyPmGridWithStepMenuItem;
	private javax.swing.JMenuItem copySSMenuItem;
	private javax.swing.JMenuItem createSavePointMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenuItem printMenuItem;
	private javax.swing.JMenuItem extendedPrintMenuItem;
	private javax.swing.JMenuItem quickBrowseMenuItem;
	private javax.swing.JToggleButton f1ToggleButton;
	private javax.swing.JToggleButton f2ToggleButton;
	private javax.swing.JToggleButton f3ToggleButton;
	private javax.swing.JToggleButton f4ToggleButton;
	private javax.swing.JToggleButton f5ToggleButton;
	private javax.swing.JToggleButton f6ToggleButton;
	private javax.swing.JToggleButton f7ToggleButton;
	private javax.swing.JToggleButton f8ToggleButton;
	private javax.swing.JToggleButton f9ToggleButton;
	private javax.swing.JCheckBoxMenuItem fullScreenMenuItem;
	private javax.swing.JToggleButton fxyToggleButton;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JPanel hintPanel;
	private javax.swing.JButton hinweisAbbrechenButton;
	private javax.swing.JButton hinweisAusfuehrenButton;
	private javax.swing.JTextArea hinweisTextArea;
	private javax.swing.JMenuItem historyMenuItem;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator11;
	private javax.swing.JSeparator jSeparator12;
	private javax.swing.JSeparator jSeparator13;
	private javax.swing.JSeparator jSeparator24;
	private javax.swing.JSeparator jSeparator25;
	private javax.swing.JSeparator jSeparator8;
	private javax.swing.JSeparator jSeparator9;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JMenuItem keyMenuItem;
	private javax.swing.JRadioButtonMenuItem learningMenuItem;
	private javax.swing.ButtonGroup levelButtonGroup;
	private javax.swing.JComboBox<String> levelComboBox;
	private javax.swing.JMenu levelMenu;
	private javax.swing.JRadioButtonMenuItem levelEasyMenuItem;
	private javax.swing.JRadioButtonMenuItem levelMediumMenuItem;
	private javax.swing.JRadioButtonMenuItem levelHardMenuItem;
	private javax.swing.JRadioButtonMenuItem levelDiabolicalMenuItem;
	private javax.swing.JRadioButtonMenuItem levelExtremeMenuItem;
	private javax.swing.JMenuItem loadConfigMenuItem;
	private javax.swing.JMenuItem loadPuzzleMenuItem;
	private javax.swing.JMenuItem solutionStepMenuItem;
	private javax.swing.JMenuItem mediumHintMenuItem;
	private javax.swing.JMenuItem solvePuzzleMenuItem;
	private javax.swing.JMenuItem solutionCountMenuItem;
	private javax.swing.ButtonGroup modeButtonGroup;
	private javax.swing.JMenu modeMenu;
	private javax.swing.JMenuItem newMenuItem;
	private javax.swing.JMenuItem newEmptyMenuItem;
	private javax.swing.JButton newNoteButton;
	private javax.swing.JButton newGameToolButton;
	private javax.swing.JMenu optionMenu;
	private javax.swing.JSplitPane outerSplitPane;
	private javax.swing.JMenuItem pasteMenuItem;
	private javax.swing.JRadioButtonMenuItem playingMenuItem;
	private javax.swing.JRadioButtonMenuItem practisingMenuItem;
	private javax.swing.JLabel progressLabel;
	private javax.swing.JMenuItem projectHomePageMenuItem;
	private javax.swing.JMenu puzzleMenu;
	private javax.swing.JToggleButton redGreenToggleButton;
	private javax.swing.JMenuItem redoMenuItem;
	private javax.swing.JButton redoToolButton;
	private javax.swing.JMenuItem reportErrorMenuItem;
	private javax.swing.JMenuItem resetSpielMenuItem;
	private javax.swing.JMenuItem resetViewMenuItem;
	private javax.swing.JMenuItem restartSpielMenuItem;
	private javax.swing.JMenuItem restoreSavePointMenuItem;
	private javax.swing.JMenuItem saveConfigAsMenuItem;
	private javax.swing.JMenuItem savePuzzleAsMenuItem;
	private javax.swing.JMenuItem importPuzzleMenuItem;
	private javax.swing.JMenuItem exportPuzzleMenuItem;
	private javax.swing.JMenuItem savePuzzleMenuItem;
	private javax.swing.JMenuItem printSetupMenuItem;
	private javax.swing.JMenuItem resetCandidatesMenuItem;
	private javax.swing.JMenuItem setGivensMenuItem;
	private javax.swing.JCheckBoxMenuItem showCandidatesMenuItem;
	private javax.swing.JCheckBoxMenuItem showCandidateHighlightMenuItem;
	private javax.swing.JCheckBoxMenuItem showColorKuMenuItem;
	private javax.swing.JCheckBoxMenuItem showDeviationsMenuItem;
	private javax.swing.JCheckBoxMenuItem showHintButtonsCheckBoxMenuItem;
	private javax.swing.JCheckBoxMenuItem showHintPanelMenuItem;
	private javax.swing.JCheckBoxMenuItem showToolBarMenuItem;
	private javax.swing.JCheckBoxMenuItem showWrongValuesMenuItem;
	private javax.swing.JRadioButtonMenuItem solutionMenuItem;
	private javax.swing.JButton solveUpToButton;
	private javax.swing.JMenuItem solvingGuideMenuItem;
	private javax.swing.JMenuItem saveAsPictureMenuItem;
	private javax.swing.JMenuItem editGivensMenuItem;
	private javax.swing.JMenuItem playGameMenuItem;
	private javax.swing.JLabel statusLabelCellCandidate;
	private javax.swing.JLabel statusLabelLevel;
	private javax.swing.JLabel statusLabelModus;
	private javax.swing.JLabel statusLabelCellSelection;
	private javax.swing.JPanel statusLinePanel;
	private javax.swing.JPanel statusPanelColor1;
	private javax.swing.JPanel statusPanelColor2;
	private javax.swing.JPanel statusPanelColor3;
	private javax.swing.JPanel statusPanelColor4;
	private javax.swing.JPanel statusPanelColor5;
	private javax.swing.JPanel statusPanelColorClear;
	private javax.swing.JPanel statusPanelColorReset;
	private javax.swing.JPanel statusPanelColorResult;
	private javax.swing.JRadioButtonMenuItem sudokuOnlyMenuItem;
	private javax.swing.JRadioButtonMenuItem summaryMenuItem;
	private javax.swing.JMenuItem undoMenuItem;
	private javax.swing.JButton undoToolButton;
	private javax.swing.JMenuItem userManualMenuItem;
	private javax.swing.JMenuItem vagueHintMenuItem;
	private javax.swing.ButtonGroup viewButtonGroup;

	/**
	 * Incorporates the last subversion revision of this file into the version
	 * string.<br>
	 * <br>
	 * 
	 * CAUTION: MainFrame.java must be changed and committed to change the build
	 * number.
	 */
	static {
		String[] dummy = REV.split(" ");
		BUILD = "Build " + dummy[1];
	}

	/**
	 * Creates new form MainFrame
	 * 
	 * @param launchFile
	 */
	public MainFrame(String launchFile) {
		// if a configuration file is given at the command line, load it before anything
		// else is done (helps restoring the screen layout)
		if (launchFile != null && launchFile.endsWith("." + configFileExt)) {
			Options.readOptions(launchFile);
			BackgroundGeneratorThread.getInstance().resetAll();
		}
		Options.getInstance().checkAllFonts();

		initComponents();
		setTitleWithFile();
		outerSplitPane.getActionMap().getParent().remove("startResize");
		outerSplitPane.getActionMap().getParent().remove("toggleFocus");

		// change hintTextArea font to a proportional font
		String fontName = "Arial";
		if (!Options.getInstance().checkFont(fontName)) {
			fontName = Font.SANS_SERIF;
		}
		
		Font font = hinweisTextArea.getFont();

		font = new Font(fontName, font.getStyle(), editMenu.getFont().getSize());
		hinweisTextArea.setFont(font);

		// status line fonts are a bit larger than default in Windows LAF
		// allow adjustments
		font = statusLinePanel.getFont();
		fontName = "Tahoma";
		if (!Options.getInstance().checkFont(fontName)) {
			fontName = font.getName();
		}
		
		int fontSize = 12;
		if (font.getSize() > fontSize) {
			fontSize = font.getSize();
		}
		
		font = new Font(fontName, getFont().getStyle(), fontSize);
		statusLabelCellCandidate.setFont(font);
		statusLabelLevel.setFont(font);
		statusLabelModus.setFont(font);
		statusLabelCellSelection.setFont(font);
		progressLabel.setFont(font);

		// get the current difficulty level (is overriden when levels are added
		// to the combo box)
		int actLevel = Options.getInstance().getActLevel();

		Color lafMenuBackColor = UIManager.getColor("textHighlight");
		Color lafMenuColor = UIManager.getColor("textHighlightText");

		if (lafMenuBackColor == null) {
			lafMenuBackColor = Color.BLUE;
		}
		
		if (lafMenuColor == null) {
			lafMenuColor = Color.BLACK;
		}

		statusLinePanel.setBackground(lafMenuBackColor);
		statusLabelLevel.setForeground(lafMenuColor);
		summaryPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
		solutionPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
		cellZoomPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
		statusLabelModus.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.playingMenuItem.text"));

		clipTimer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clipTimer.stop();
				adjustPasteMenuItem();
			}
		});
		
		try {
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			clip.addFlavorListener(this);
			adjustPasteMenuItem();
		} catch (IllegalStateException ex) {
			clipTimer.start();
		}

		sudokuPanel = new SudokuPanel(this);
		sudokuPanel.setCellZoomPanel(cellZoomPanel);
		cellZoomPanel.setSudokuPanel(sudokuPanel);
		outerSplitPane.setLeftComponent(splitPanel);
		splitPanel.setSplitPane(sudokuPanel, null);

		tabPane.addTab(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.summary"), summaryPanel);
		tabPane.addTab(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_path"), solutionPanel);
		tabPane.addTab(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.all_steps"), allStepsPanel);
		tabPane.addTab(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.cell_zoom"), cellZoomPanel);
		tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tabPaneMouseClicked(evt);
			}
		});

		if (Options.getInstance().isSaveWindowLayout()) {
			setWindowLayout(false);
		} else {
			setWindowLayout(true);
		}
		
		outerSplitPaneInitialized = false;
		if (Options.getInstance().getInitialXPos() != -1 && Options.getInstance().getInitialYPos() != -1) {
			
			Toolkit t = Toolkit.getDefaultToolkit();
			Dimension screenSize = t.getScreenSize();
			
			int x = Options.getInstance().getInitialXPos();
			int y = Options.getInstance().getInitialYPos();
			
			if (x + getWidth() > screenSize.width) {
				x = screenSize.width - getWidth() - 10;
			}
			
			if (y + getHeight() > screenSize.height) {
				y = screenSize.height - getHeight() - 10;
			}
			
			if (x < 0) {
				x = 0;
			}
			
			if (y < 0) {
				y = 0;
			}
			
			setLocation(x, y);
		}
		
		showHintPanelMenuItem.setSelected(Options.getInstance().isShowHintPanel());
		showToolBarMenuItem.setSelected(Options.getInstance().isShowToolBar());

		levelMenuItems[0] = levelEasyMenuItem;
		levelMenuItems[1] = levelMediumMenuItem;
		levelMenuItems[2] = levelHardMenuItem;
		levelMenuItems[3] = levelDiabolicalMenuItem;
		levelMenuItems[4] = levelExtremeMenuItem;
		
		FontMetrics metrics = levelComboBox.getFontMetrics(levelComboBox.getFont());
		int miWidth = 0;
		int miHeight = metrics.getHeight();
		Set<Character> mnemonics = new HashSet<Character>();
		
		for (int i = 1; i < DifficultyType.values().length; i++) {
			
			levelMenuItems[i - 1].setText(Options.getInstance().getDifficultyLevels()[i].getName());
			char mnemonic = 0;
			boolean mnemonicFound = false;
			
			for (int j = 0; j < Options.getInstance().getDifficultyLevels()[i].getName().length(); j++) {
				mnemonic = Options.getInstance().getDifficultyLevels()[i].getName().charAt(j);
				if (!mnemonics.contains(mnemonic)) {
					mnemonicFound = true;
					break;
				}
			}
			
			if (mnemonicFound) {
				mnemonics.add(mnemonic);
				levelMenuItems[i - 1].setMnemonic(mnemonic);
			}
			
			levelComboBox.addItem(Options.getInstance().getDifficultyLevels()[i].getName());
			int aktWidth = metrics.stringWidth(Options.getInstance().getDifficultyLevels()[i].getName());

			if (aktWidth > miWidth) {
				miWidth = aktWidth;
			}
		}
		
		mnemonics = null;

		// mode menu items
		modeMenuItems = new JRadioButtonMenuItem[] { playingMenuItem, learningMenuItem, practisingMenuItem };

		// in Windows miWidth = 35, miHeight = 14; size = 60/20
		if (miWidth > 35) {
			Dimension newLevelSize = new Dimension(60 + (miWidth - 35) + 8, 20 + (miHeight - 14) + 3);
			levelComboBox.setMaximumSize(newLevelSize);
			levelComboBox.setMinimumSize(newLevelSize);
			levelComboBox.setPreferredSize(newLevelSize);
			levelComboBox.setSize(newLevelSize);
		}

		// set back the saved difficulty level
		Options.getInstance().setActLevel(actLevel);

		check();

		toggleButtons[0] = f1ToggleButton;
		toggleButtons[1] = f2ToggleButton;
		toggleButtons[2] = f3ToggleButton;
		toggleButtons[3] = f4ToggleButton;
		toggleButtons[4] = f5ToggleButton;
		toggleButtons[5] = f6ToggleButton;
		toggleButtons[6] = f7ToggleButton;
		toggleButtons[7] = f8ToggleButton;
		toggleButtons[8] = f9ToggleButton;
		toggleButtons[9] = fxyToggleButton;
		
		for (int i = 0, lim = toggleButtons.length; i < lim; i++) {
			
			toggleButtonIconsOrg[i] = toggleButtons[i].getIcon();
			toggleButtonIcons[i] = toggleButtons[i].getIcon();
			
			if (i < Sudoku2.UNITS) {
				emptyToggleButtonIconsOrg[i] = new ImageIcon(getClass().getResource("/img/f_" + (i + 1) + "c_inactive.png"));
			} else {
				emptyToggleButtonIcons[i] = toggleButtons[i].getIcon();
			}
		}
		
		setToggleButton(null, false);
		prepareToggleButtonIcons(Options.getInstance().isShowColorKu());

		// initialize colorKuMeniItem
		showColorKuMenuItem.setSelected(Options.getInstance().isShowColorKu());
		Options.getInstance().setShowColorKuAct(Options.getInstance().isShowColorKu());

		// Caret-Listener for display of Forcing Chains
		hinweisTextArea.addCaretListener(caretListener);

		// Images for progressLabel
		createProgressLabelImages();
		progressLabel.setIcon(progressImages[0]);

		// set the mode
		setMode(Options.getInstance().getGameMode(), false);

		// show hint buttons in toolbar
		setShowHintButtonsInToolbar();

		// if a puzzle file is given at the command line, load it
		if (launchFile != null && launchFile.endsWith("." + solutionFileExt)) {
			loadFromFile(launchFile, 1);
		}
		
		if (launchFile != null && launchFile.endsWith("." + textFileExt)) {
			loadFromFile(launchFile, 8);
		}

		fixFocus();

		// Start background creation
		BackgroundGeneratorThread.getInstance().startCreation();

		new FileDrop(this, new FileDrop.Listener() {
			public void filesDropped(File[] files) {
				onDragDropFile(files);
			}
		});

		updateCellSelectionStatus();
		exportWindow = new UIExportLine(sudokuPanel);
		importWindow = new UIImportLine(this);
		quickBrowseWindow = new UIQuickBrowse(this);
	}

	void onDragDropFile(File[] files) {

		if (files.length == 1 && files[0].exists() && files[0].canRead() && files[0].isFile()) {

			try {
				String importLine = new String(Files.readAllBytes(Paths.get(files[0].toURI())), StandardCharsets.UTF_8);
				if (ValidateImportLine(importLine)) {
					loadFromFile(files[0].getPath(), 8);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCellSelectionStatus() {
		
		if (sudokuPanel == null) {
			return;
		}
		
		int size = sudokuPanel.getCellSelectionSize();
		int r = sudokuPanel.getActiveRow() + 1;
		int c = sudokuPanel.getActiveCol() + 1;
		
		if (size == 1) {
			statusLabelCellSelection.setText("R"+r+"C"+c);
		} else {
			statusLabelCellSelection.setText("");
		}
		
		statusLabelCellSelection.repaint();
	}
	
	private void initComponents() {

		levelButtonGroup = new javax.swing.ButtonGroup();
		viewButtonGroup = new javax.swing.ButtonGroup();
		colorButtonGroup = new javax.swing.ButtonGroup();
		modeButtonGroup = new javax.swing.ButtonGroup();
		statusLinePanel = new javax.swing.JPanel();
		statusPanelColorResult = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		statusPanelColor1 = new StatusColorPanel(0);
		statusPanelColor2 = new StatusColorPanel(2);
		statusPanelColor3 = new StatusColorPanel(4);
		statusPanelColor4 = new StatusColorPanel(6);
		statusPanelColor5 = new StatusColorPanel(8);
		statusPanelColorClear = new StatusColorPanel(-1);
		statusPanelColorReset = new StatusColorPanel(-2);
		statusLabelCellCandidate = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		statusLabelLevel = new javax.swing.JLabel();
		jSeparator8 = new javax.swing.JSeparator();
		progressLabel = new javax.swing.JLabel();
		jSeparator24 = new javax.swing.JSeparator();
		jSeparator25 = new javax.swing.JSeparator();
		statusLabelModus = new javax.swing.JLabel();
		statusLabelCellSelection = new javax.swing.JLabel();
		jToolBar1 = new javax.swing.JToolBar();
		undoToolButton = new javax.swing.JButton();
		redoToolButton = new javax.swing.JButton();
		jSeparator9 = new javax.swing.JSeparator();
		newGameToolButton = new javax.swing.JButton();
		jSeparator12 = new javax.swing.JSeparator();
		levelComboBox = new javax.swing.JComboBox<String>();
		jSeparator13 = new javax.swing.JSeparator();
		jSeparator11 = new javax.swing.JSeparator();
		redGreenToggleButton = new javax.swing.JToggleButton();
		f1ToggleButton = new javax.swing.JToggleButton();
		f2ToggleButton = new javax.swing.JToggleButton();
		f3ToggleButton = new javax.swing.JToggleButton();
		f4ToggleButton = new javax.swing.JToggleButton();
		f5ToggleButton = new javax.swing.JToggleButton();
		f6ToggleButton = new javax.swing.JToggleButton();
		f7ToggleButton = new javax.swing.JToggleButton();
		f8ToggleButton = new javax.swing.JToggleButton();
		f9ToggleButton = new javax.swing.JToggleButton();
		fxyToggleButton = new javax.swing.JToggleButton();
		outerSplitPane = new javax.swing.JSplitPane();
		hintPanel = new javax.swing.JPanel();
		newNoteButton = new javax.swing.JButton();
		hinweisAusfuehrenButton = new javax.swing.JButton();
		solveUpToButton = new javax.swing.JButton();
		hinweisAbbrechenButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		hinweisTextArea = new javax.swing.JTextArea();
		jMenuBar1 = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		newMenuItem = new javax.swing.JMenuItem();
		newEmptyMenuItem = new javax.swing.JMenuItem();
		loadPuzzleMenuItem = new javax.swing.JMenuItem();
		savePuzzleMenuItem = new javax.swing.JMenuItem();
		savePuzzleAsMenuItem = new javax.swing.JMenuItem();
		importPuzzleMenuItem = new javax.swing.JMenuItem();
		exportPuzzleMenuItem = new javax.swing.JMenuItem();
		loadConfigMenuItem = new javax.swing.JMenuItem();
		saveConfigAsMenuItem = new javax.swing.JMenuItem();
		printSetupMenuItem = new javax.swing.JMenuItem();
		printMenuItem = new javax.swing.JMenuItem();
		extendedPrintMenuItem = new javax.swing.JMenuItem();
		quickBrowseMenuItem = new javax.swing.JMenuItem();
		saveAsPictureMenuItem = new javax.swing.JMenuItem();
		editGivensMenuItem = new javax.swing.JMenuItem();
		playGameMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		undoMenuItem = new javax.swing.JMenuItem();
		redoMenuItem = new javax.swing.JMenuItem();
		copyCluesMenuItem = new javax.swing.JMenuItem();
		copyFilledMenuItem = new javax.swing.JMenuItem();
		copyPmGridMenuItem = new javax.swing.JMenuItem();
		copyPmGridWithStepMenuItem = new javax.swing.JMenuItem();
		copyLibraryMenuItem = new javax.swing.JMenuItem();
		copySSMenuItem = new javax.swing.JMenuItem();
		pasteMenuItem = new javax.swing.JMenuItem();
		restartSpielMenuItem = new javax.swing.JMenuItem();
		resetSpielMenuItem = new javax.swing.JMenuItem();
		configMenuItem = new javax.swing.JMenuItem();
		modeMenu = new javax.swing.JMenu();
		playingMenuItem = new javax.swing.JRadioButtonMenuItem();
		learningMenuItem = new javax.swing.JRadioButtonMenuItem();
		practisingMenuItem = new javax.swing.JRadioButtonMenuItem();
		optionMenu = new javax.swing.JMenu();
		showCandidatesMenuItem = new javax.swing.JCheckBoxMenuItem();
		showCandidateHighlightMenuItem = new javax.swing.JCheckBoxMenuItem();
		showWrongValuesMenuItem = new javax.swing.JCheckBoxMenuItem();
		showDeviationsMenuItem = new javax.swing.JCheckBoxMenuItem();
		showColorKuMenuItem = new javax.swing.JCheckBoxMenuItem();
		colorCellsMenuItem = new javax.swing.JRadioButtonMenuItem();
		colorCandidatesMenuItem = new javax.swing.JRadioButtonMenuItem();
		levelMenu = new javax.swing.JMenu();
		levelEasyMenuItem = new javax.swing.JRadioButtonMenuItem();
		levelMediumMenuItem = new javax.swing.JRadioButtonMenuItem();
		levelHardMenuItem = new javax.swing.JRadioButtonMenuItem();
		levelDiabolicalMenuItem = new javax.swing.JRadioButtonMenuItem();
		levelExtremeMenuItem = new javax.swing.JRadioButtonMenuItem();
		puzzleMenu = new javax.swing.JMenu();
		vagueHintMenuItem = new javax.swing.JMenuItem();
		mediumHintMenuItem = new javax.swing.JMenuItem();
		solvePuzzleMenuItem = new javax.swing.JMenuItem();
		solutionCountMenuItem = new javax.swing.JMenuItem();
		solutionStepMenuItem = new javax.swing.JMenuItem();
		backdoorSearchMenuItem = new javax.swing.JMenuItem();
		historyMenuItem = new javax.swing.JMenuItem();
		createSavePointMenuItem = new javax.swing.JMenuItem();
		restoreSavePointMenuItem = new javax.swing.JMenuItem();
		resetCandidatesMenuItem = new javax.swing.JMenuItem();
		setGivensMenuItem = new javax.swing.JMenuItem();
		setAllSinglesMenuItem = new javax.swing.JMenuItem();
		viewMenu = new javax.swing.JMenu();
		sudokuOnlyMenuItem = new javax.swing.JRadioButtonMenuItem();
		summaryMenuItem = new javax.swing.JRadioButtonMenuItem();
		solutionMenuItem = new javax.swing.JRadioButtonMenuItem();
		allStepsMenuItem = new javax.swing.JRadioButtonMenuItem();
		cellZoomMenuItem = new javax.swing.JRadioButtonMenuItem();
		showHintPanelMenuItem = new javax.swing.JCheckBoxMenuItem();
		showToolBarMenuItem = new javax.swing.JCheckBoxMenuItem();
		showHintButtonsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
		fullScreenMenuItem = new javax.swing.JCheckBoxMenuItem();
		resetViewMenuItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		keyMenuItem = new javax.swing.JMenuItem();
		userManualMenuItem = new javax.swing.JMenuItem();
		solvingGuideMenuItem = new javax.swing.JMenuItem();
		projectHomePageMenuItem = new javax.swing.JMenuItem();
		reportErrorMenuItem = new javax.swing.JMenuItem();
		askQuestionMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();
		
		// Swing uses F10 as a default menu selector to navigate with keyboard.
		// When setting filters, I accidently hit F10 all the time which stalls the key events.
		// For this reason, I am disabling this feature and making F10 highlight bivalue cells instead.
		String key = "F10";
		KeyStroke f10 = KeyStroke.getKeyStroke(key);
		this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(f10, key);
		this.getRootPane().getActionMap().put(key, new AbstractAction() {
			private static final long serialVersionUID = 628736937855343558L;
			public void actionPerformed(ActionEvent e) {/*do nothing*/}
		});
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/MainFrame");
		setTitle(bundle.getString("MainFrame.title"));
		setIconImage(getIcon());
		addWindowListener(new java.awt.event.WindowAdapter() {
			
			@Override
			public void windowClosed(java.awt.event.WindowEvent evt) {
				formWindowClosed(evt);
			}

			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		statusLinePanel.setBackground(new java.awt.Color(0, 153, 255));
		statusLinePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

		statusPanelColorResult.setToolTipText(bundle.getString("MainFrame.statusPanelColorResult.toolTipText"));

		javax.swing.GroupLayout statusPanelColorResultLayout = new javax.swing.GroupLayout(statusPanelColorResult);
		statusPanelColorResult.setLayout(statusPanelColorResultLayout);
		statusPanelColorResultLayout.setHorizontalGroup(statusPanelColorResultLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 30, Short.MAX_VALUE));
		statusPanelColorResultLayout.setVerticalGroup(statusPanelColorResultLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		statusLinePanel.add(statusPanelColorResult);

		jPanel1.setOpaque(false);
		jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));

		statusPanelColor1.setToolTipText(bundle.getString("MainFrame.statusPanelColor1.toolTipText"));
		statusPanelColor1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColor1MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColor1Layout = new javax.swing.GroupLayout(statusPanelColor1);
		statusPanelColor1.setLayout(statusPanelColor1Layout);
		statusPanelColor1Layout.setHorizontalGroup(statusPanelColor1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColor1Layout.setVerticalGroup(statusPanelColor1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColor1);

		statusPanelColor2.setToolTipText(bundle.getString("MainFrame.statusPanelColor2.toolTipText"));
		statusPanelColor2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColor2MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColor2Layout = new javax.swing.GroupLayout(statusPanelColor2);
		statusPanelColor2.setLayout(statusPanelColor2Layout);
		statusPanelColor2Layout.setHorizontalGroup(statusPanelColor2Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColor2Layout.setVerticalGroup(statusPanelColor2Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColor2);

		statusPanelColor3.setToolTipText(bundle.getString("MainFrame.statusPanelColor3.toolTipText"));
		statusPanelColor3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColor3MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColor3Layout = new javax.swing.GroupLayout(statusPanelColor3);
		statusPanelColor3.setLayout(statusPanelColor3Layout);
		statusPanelColor3Layout.setHorizontalGroup(statusPanelColor3Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColor3Layout.setVerticalGroup(statusPanelColor3Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColor3);

		statusPanelColor4.setToolTipText(bundle.getString("MainFrame.statusPanelColor4.toolTipText"));
		statusPanelColor4.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColor4MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColor4Layout = new javax.swing.GroupLayout(statusPanelColor4);
		statusPanelColor4.setLayout(statusPanelColor4Layout);
		statusPanelColor4Layout.setHorizontalGroup(statusPanelColor4Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColor4Layout.setVerticalGroup(statusPanelColor4Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColor4);

		statusPanelColor5.setToolTipText(bundle.getString("MainFrame.statusPanelColor5.toolTipText"));
		statusPanelColor5.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColor5MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColor5Layout = new javax.swing.GroupLayout(statusPanelColor5);
		statusPanelColor5.setLayout(statusPanelColor5Layout);
		statusPanelColor5Layout.setHorizontalGroup(statusPanelColor5Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColor5Layout.setVerticalGroup(statusPanelColor5Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColor5);

		statusPanelColorClear.setToolTipText(bundle.getString("MainFrame.statusPanelColorClear.toolTipText"));
		statusPanelColorClear.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColorClearMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColorClearLayout = new javax.swing.GroupLayout(statusPanelColorClear);
		statusPanelColorClear.setLayout(statusPanelColorClearLayout);
		statusPanelColorClearLayout.setHorizontalGroup(statusPanelColorClearLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColorClearLayout.setVerticalGroup(statusPanelColorClearLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColorClear);

		statusPanelColorReset.setToolTipText(bundle.getString("MainFrame.statusPanelColorReset.toolTipText"));
		statusPanelColorReset.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusPanelColorResetMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout statusPanelColorResetLayout = new javax.swing.GroupLayout(statusPanelColorReset);
		statusPanelColorReset.setLayout(statusPanelColorResetLayout);
		statusPanelColorResetLayout.setHorizontalGroup(statusPanelColorResetLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		statusPanelColorResetLayout.setVerticalGroup(statusPanelColorResetLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		jPanel1.add(statusPanelColorReset);

		statusLinePanel.add(jPanel1);

		statusLabelCellCandidate.setText(bundle.getString("MainFrame.statusLabelCellCandidate.text.cell"));
		statusLabelCellCandidate.setToolTipText(bundle.getString("MainFrame.statusLabelCellCandidate.toolTipText"));
		statusLabelCellCandidate.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				statusLabelCellCandidateMouseClicked(evt);
			}
		});
		statusLinePanel.add(statusLabelCellCandidate);

		jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator1.setPreferredSize(new java.awt.Dimension(2, 17));
		statusLinePanel.add(jSeparator1);

		statusLabelLevel.setText(bundle.getString("MainFrame.statusLabelLevel.text"));
		statusLabelLevel.setToolTipText(bundle.getString("MainFrame.statusLabelLevel.toolTipText"));
		statusLinePanel.add(statusLabelLevel);

		jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator8.setPreferredSize(new java.awt.Dimension(2, 17));
		statusLinePanel.add(jSeparator8);

		progressLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/invalid20.png")));
		progressLabel.setText("null");
		progressLabel.setToolTipText(bundle.getString("MainFrame.progressLabel.toolTipText"));
		statusLinePanel.add(progressLabel);

		jSeparator24.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator24.setPreferredSize(new java.awt.Dimension(2, 17));
		statusLinePanel.add(jSeparator24);

		statusLabelModus.setText(bundle.getString("MainFrame.statusLabelModus.textPlay"));
		statusLabelModus.setToolTipText(bundle.getString("MainFrame.statusLabelModus.toolTipText"));
		statusLinePanel.add(statusLabelModus);

		jSeparator25.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator25.setPreferredSize(new java.awt.Dimension(2, 17));
		statusLinePanel.add(jSeparator25);
		
		statusLabelCellSelection.setText("");
		statusLinePanel.add(statusLabelCellSelection);

		getContentPane().add(statusLinePanel, java.awt.BorderLayout.SOUTH);

		undoToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/undo.png")));
		undoToolButton.setToolTipText(bundle.getString("MainFrame.undoToolButton.toolTipText"));
		undoToolButton.setEnabled(false);
		undoToolButton.setRequestFocusEnabled(false);
		undoToolButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				undoToolButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(undoToolButton);

		redoToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/redo.png")));
		redoToolButton.setToolTipText(bundle.getString("MainFrame.redoToolButton.toolTipText"));
		redoToolButton.setEnabled(false);
		redoToolButton.setRequestFocusEnabled(false);
		redoToolButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				redoToolButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(redoToolButton);

		jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator9.setMaximumSize(new java.awt.Dimension(5, 32767));
		jToolBar1.add(jSeparator9);

		newGameToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hodoku02-32.png")));
		newGameToolButton.setToolTipText(bundle.getString("MainFrame.neuesSpielToolButton.toolTipText"));
		newGameToolButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newGameToolButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(newGameToolButton);

		jSeparator12.setEnabled(false);
		jSeparator12.setMaximumSize(new java.awt.Dimension(3, 0));
		jToolBar1.add(jSeparator12);

		levelComboBox.setToolTipText(bundle.getString("MainFrame.levelComboBox.toolTipText"));
		levelComboBox.setMaximumSize(new java.awt.Dimension(80, 20));
		levelComboBox.setMinimumSize(new java.awt.Dimension(15, 8));
		levelComboBox.setPreferredSize(new java.awt.Dimension(20, 10));
		levelComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelComboBoxActionPerformed(evt);
			}
		});
		jToolBar1.add(levelComboBox);

		jSeparator13.setMaximumSize(new java.awt.Dimension(3, 0));
		jToolBar1.add(jSeparator13);

		jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
		jSeparator11.setMaximumSize(new java.awt.Dimension(5, 32767));
		jToolBar1.add(jSeparator11);

		redGreenToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/rgDeselected1.png")));
		redGreenToggleButton.setSelected(true);
		redGreenToggleButton.setToolTipText(bundle.getString("MainFrame.redGreenToggleButton.toolTipText"));
		redGreenToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/rgSelected1.png")));
		redGreenToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				redGreenToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(redGreenToggleButton);

		f1ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_1c.png")));
		f1ToggleButton.setToolTipText(bundle.getString("MainFrame.f1ToggleButton.toolTipText"));
		f1ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed1(evt);
			}
		});
		jToolBar1.add(f1ToggleButton);

		f2ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_2c.png")));
		f2ToggleButton.setToolTipText(bundle.getString("MainFrame.f2ToggleButton.toolTipText"));
		f2ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f2ToggleButton);

		f3ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_3c.png")));
		f3ToggleButton.setToolTipText(bundle.getString("MainFrame.f3ToggleButton.toolTipText"));
		f3ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f3ToggleButton);

		f4ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_4c.png")));
		f4ToggleButton.setToolTipText(bundle.getString("MainFrame.f4ToggleButton.toolTipText"));
		f4ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f4ToggleButton);

		f5ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_5c.png")));
		f5ToggleButton.setToolTipText(bundle.getString("MainFrame.f5ToggleButton.toolTipText"));
		f5ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f5ToggleButton);

		f6ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_6c.png")));
		f6ToggleButton.setToolTipText(bundle.getString("MainFrame.f6ToggleButton.toolTipText"));
		f6ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f6ToggleButton);

		f7ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_7c.png")));
		f7ToggleButton.setToolTipText(bundle.getString("MainFrame.f7ToggleButton.toolTipText"));
		f7ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f7ToggleButton);

		f8ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_8c.png")));
		f8ToggleButton.setToolTipText(bundle.getString("MainFrame.f8ToggleButton.toolTipText"));
		f8ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f8ToggleButton);

		f9ToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_9c.png")));
		f9ToggleButton.setToolTipText(bundle.getString("MainFrame.f9ToggleButton.toolTipText"));
		f9ToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				f1ToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(f9ToggleButton);

		fxyToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/f_xyc.png")));
		fxyToggleButton.setToolTipText(bundle.getString("MainFrame.fxyToggleButton.toolTipText"));
		fxyToggleButton.setFocusable(false);
		fxyToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		fxyToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		fxyToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fxyToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(fxyToggleButton);

		getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

		outerSplitPane.setDividerLocation(525);
		outerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		outerSplitPane.setResizeWeight(1.0);
		outerSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				outerSplitPanePropertyChange(evt);
			}
		});

		hintPanel.setBorder(
				javax.swing.BorderFactory.createTitledBorder(bundle.getString("MainFrame.hintPanel.border.title")));
		hintPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				hintPanelPropertyChange(evt);
			}
		});

		newNoteButton.setMnemonic('n');
		newNoteButton.setText(bundle.getString("MainFrame.neuerHinweisButton.text"));
		newNoteButton.setToolTipText(bundle.getString("MainFrame.neuerHinweisButton.toolTipText"));
		newNoteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				neuerHinweisButtonActionPerformed(evt);
			}
		});

		hinweisAusfuehrenButton.setMnemonic('f');
		hinweisAusfuehrenButton.setText(bundle.getString("MainFrame.hinweisAusfuehrenButton.text"));
		hinweisAusfuehrenButton.setToolTipText(bundle.getString("MainFrame.hinweisAusfuehrenButton.toolTipText"));
		hinweisAusfuehrenButton.setEnabled(false);
		hinweisAusfuehrenButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hinweisAusfuehrenButtonActionPerformed(evt);
			}
		});

		solveUpToButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.solveUpToButton.mnemonic").charAt(0));
		solveUpToButton.setText(bundle.getString("MainFrame.solveUpToButton.text"));
		solveUpToButton.setToolTipText(bundle.getString("MainFrame.solveUpToButton.toolTipText"));
		solveUpToButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				solveUpToButtonActionPerformed(evt);
			}
		});

		hinweisAbbrechenButton.setMnemonic('a');
		hinweisAbbrechenButton.setText(bundle.getString("MainFrame.hinweisAbbrechenButton.text"));
		hinweisAbbrechenButton.setToolTipText(bundle.getString("MainFrame.hinweisAbbrechenButton.toolTipText"));
		hinweisAbbrechenButton.setEnabled(false);
		hinweisAbbrechenButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hinweisAbbrechenButtonActionPerformed(evt);
			}
		});

		hinweisTextArea.setColumns(20);
		hinweisTextArea.setEditable(false);
		hinweisTextArea.setLineWrap(true);
		hinweisTextArea.setRows(5);
		hinweisTextArea.setWrapStyleWord(true);
		jScrollPane1.setViewportView(hinweisTextArea);

		javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
		hintPanel.setLayout(hintPanelLayout);
		hintPanelLayout.setHorizontalGroup(hintPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hintPanelLayout.createSequentialGroup()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(newNoteButton).addComponent(solveUpToButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(hinweisAusfuehrenButton).addComponent(hinweisAbbrechenButton))));

		hintPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
				hinweisAbbrechenButton, hinweisAusfuehrenButton, newNoteButton, solveUpToButton });

		hintPanelLayout.setVerticalGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(hintPanelLayout.createSequentialGroup()
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(hinweisAusfuehrenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(newNoteButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(hinweisAbbrechenButton).addComponent(solveUpToButton)))
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE));

		hintPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] { hinweisAbbrechenButton,
				hinweisAusfuehrenButton, newNoteButton, solveUpToButton });

		outerSplitPane.setRightComponent(hintPanel);

		getContentPane().add(outerSplitPane, java.awt.BorderLayout.CENTER);

		fileMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.dateiMenuMnemonic").charAt(0));
		fileMenu.setText(bundle.getString("MainFrame.dateiMenu.text"));

		newMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK ));
		newMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.neuMenuItemMnemonic").charAt(0));
		newMenuItem.setText(bundle.getString("MainFrame.neuMenuItem.text"));
		newMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				neuMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(newMenuItem);
		
		newEmptyMenuItem.setText(bundle.getString("MainFrame.newEmptyMenuItem.text"));
		newEmptyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newEmptyGameMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(newEmptyMenuItem);
		fileMenu.add(new javax.swing.JPopupMenu.Separator());

		loadPuzzleMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK ));
		loadPuzzleMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.loadMenuItemMnemonic").charAt(0));
		loadPuzzleMenuItem.setText(bundle.getString("MainFrame.loadPuzzleMenuItem.text"));
		loadPuzzleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadPuzzleMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(loadPuzzleMenuItem);

		savePuzzleMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK ));
		savePuzzleMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.savePuzzleMenuItem.mnemonic").charAt(0));
		savePuzzleMenuItem.setText(bundle.getString("MainFrame.savePuzzleMenuItem.text"));
		savePuzzleMenuItem.setEnabled(false);
		savePuzzleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savePuzzleMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(savePuzzleMenuItem);

		savePuzzleAsMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK ));
		savePuzzleAsMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.saveAsMenuItemMnemonic").charAt(0));
		savePuzzleAsMenuItem.setText(bundle.getString("MainFrame.savePuzzleAsMenuItem.text"));
		savePuzzleAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savePuzzleAsMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(savePuzzleAsMenuItem);

		importPuzzleMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		importPuzzleMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.saveAsMenuItemMnemonic").charAt(0));
		importPuzzleMenuItem.setText(bundle.getString("MainFrame.importPuzzleMenuItem.text"));
		importPuzzleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				importPuzzleMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(importPuzzleMenuItem);

		exportPuzzleMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		exportPuzzleMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.saveAsMenuItemMnemonic").charAt(0));
		exportPuzzleMenuItem.setText(bundle.getString("MainFrame.exportPuzzleMenuItem.text"));
		exportPuzzleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportPuzzleMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exportPuzzleMenuItem);

		loadConfigMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.loadConfigMenuItem.mnemonic").charAt(0));
		loadConfigMenuItem.setText(bundle.getString("MainFrame.loadConfigMenuItem.text"));
		loadConfigMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadConfigMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(loadConfigMenuItem);

		saveConfigAsMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.saveConfigAsMenuItem.mnemonic").charAt(0));
		saveConfigAsMenuItem.setText(bundle.getString("MainFrame.saveConfigAsMenuItem.text"));
		saveConfigAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveConfigAsMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(saveConfigAsMenuItem);
		fileMenu.add(new javax.swing.JPopupMenu.Separator());

		printSetupMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.seiteEinrichtenMenuItemMnemonic").charAt(0));
		printSetupMenuItem.setText(bundle.getString("MainFrame.seiteEinrichtenMenuItem.text"));
		printSetupMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				printSetupMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(printSetupMenuItem);

		printMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		printMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.druckenMenuItemMnemonic").charAt(0));
		printMenuItem.setText(bundle.getString("MainFrame.druckenMenuItem.text"));
		printMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				druckenMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(printMenuItem);

		extendedPrintMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.extendedPrintMenuItem.mnemonic").charAt(0));
		extendedPrintMenuItem.setText(bundle.getString("MainFrame.extendedPrintMenuItem.text"));
		extendedPrintMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				extendedPrintMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(extendedPrintMenuItem);			

		saveAsPictureMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.speichernAlsBildMenuItemMnemonic").charAt(0));
		saveAsPictureMenuItem.setText(bundle.getString("MainFrame.speichernAlsBildMenuItem.text"));
		saveAsPictureMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				speichernAlsBildMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(saveAsPictureMenuItem);
		fileMenu.add(new javax.swing.JPopupMenu.Separator());
		
		quickBrowseMenuItem.setText("Quick Browse");
		quickBrowseMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				quickBrowseWindow.setVisible(true);
			}
		});
		fileMenu.add(quickBrowseMenuItem);
		fileMenu.add(new javax.swing.JPopupMenu.Separator());

		exitMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
		exitMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.beendenMenuItemMnemonic").charAt(0));
		exitMenuItem.setText(bundle.getString("MainFrame.beendenMenuItem.text"));
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		jMenuBar1.add(fileMenu);

		editMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.bearbeitenMenuMnemonic").charAt(0));
		editMenu.setText(bundle.getString("MainFrame.bearbeitenMenu.text"));

		undoMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		undoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.undoMenuItemMnemonic").charAt(0));
		undoMenuItem.setText(bundle.getString("MainFrame.undoMenuItem.text"));
		undoMenuItem.setEnabled(false);
		undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				undoMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(undoMenuItem);

		redoMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		redoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.redoMenuItemMnemonic").charAt(0));
		redoMenuItem.setText(bundle.getString("MainFrame.redoMenuItem.text"));
		redoMenuItem.setEnabled(false);
		redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				redoMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(redoMenuItem);
		editMenu.add(new javax.swing.JPopupMenu.Separator());

		copyCluesMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		copyCluesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copyCluesMenuItemMnemonic").charAt(0));
		copyCluesMenuItem.setText(bundle.getString("MainFrame.copyCluesMenuItem.text"));
		copyCluesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyCluesMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copyCluesMenuItem);

		copyFilledMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copyFilledMenuItemMnemonic").charAt(0));
		copyFilledMenuItem.setText(bundle.getString("MainFrame.copyFilledMenuItem.text"));
		copyFilledMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyFilledMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copyFilledMenuItem);

		copyPmGridMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		copyPmGridMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copyPmGridMenuItemMnemonic").charAt(0));
		copyPmGridMenuItem.setText(bundle.getString("MainFrame.copyPmGridMenuItem.text"));
		copyPmGridMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyPmGridMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copyPmGridMenuItem);

		copyPmGridWithStepMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copyPmGridWithStepMenuItemMnemonic").charAt(0));
		copyPmGridWithStepMenuItem.setText(bundle.getString("MainFrame.copyPmGridWithStepMenuItem.text"));
		copyPmGridWithStepMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyPmGridWithStepMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copyPmGridWithStepMenuItem);

		copyLibraryMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copyLibraryMenuItemMnemonic").charAt(0));
		copyLibraryMenuItem.setText(bundle.getString("MainFrame.copyLibraryMenuItem.text"));
		copyLibraryMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyLibraryMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copyLibraryMenuItem);

		copySSMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.copySSMenuItem.mnemonic").charAt(0));
		copySSMenuItem.setText(bundle.getString("MainFrame.copySSMenuItem.text"));
		copySSMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copySSMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(copySSMenuItem);

		pasteMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		pasteMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.pasteMenuItemMnemonic").charAt(0));
		pasteMenuItem.setText(bundle.getString("MainFrame.pasteMenuItem.text"));
		pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pasteMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(pasteMenuItem);
		editMenu.add(new javax.swing.JPopupMenu.Separator());
		
		editGivensMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("editGivensMenuItemMnemonic").charAt(0));
		editGivensMenuItem.setText(bundle.getString("MainFrame.editGivensMenuItem.text"));
		editGivensMenuItem.setEnabled(false);
		editGivensMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				spielEditierenMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(editGivensMenuItem);

		playGameMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.spielenMenuItemMnemonic").charAt(0));
		playGameMenuItem.setText(bundle.getString("MainFrame.playGameMenuItem.text"));
		playGameMenuItem.setEnabled(true);
		playGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				spielSpielenMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(playGameMenuItem);
		editMenu.add(new javax.swing.JPopupMenu.Separator());

		restartSpielMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
		restartSpielMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.restartSpielMenuItemMnemonic").charAt(0));
		restartSpielMenuItem.setText(bundle.getString("MainFrame.restartSpielMenuItem.text"));
		restartSpielMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				restartSpielMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(restartSpielMenuItem);

		resetSpielMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.resetSpielMenuItemMnemonic").charAt(0));
		resetSpielMenuItem.setText(bundle.getString("MainFrame.resetSpielMenuItem.text"));
		resetSpielMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetSpielMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(resetSpielMenuItem);
		editMenu.add(new javax.swing.JPopupMenu.Separator());

		configMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.configMenuItemAccelerator")));
		configMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.configMenuItemMnemonic").charAt(0));
		configMenuItem.setText(bundle.getString("MainFrame.configMenuItem.text"));
		configMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				configMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(configMenuItem);

		jMenuBar1.add(editMenu);

		modeMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.modeMenu.mnemonic").charAt(0));
		modeMenu.setText(bundle.getString("MainFrame.modeMenu.text"));

		modeButtonGroup.add(playingMenuItem);
		playingMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.playingMenuItem.mnemonic").charAt(0));
		playingMenuItem.setSelected(true);
		playingMenuItem.setText(bundle.getString("MainFrame.playingMenuItem.text"));
		playingMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playingMenuItemActionPerformed(evt);
			}
		});
		modeMenu.add(playingMenuItem);

		modeButtonGroup.add(learningMenuItem);
		learningMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.learningMenuItem.mnemonic").charAt(0));
		learningMenuItem.setText(bundle.getString("MainFrame.learningMenuItem.text"));
		learningMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				learningMenuItemActionPerformed(evt);
			}
		});
		modeMenu.add(learningMenuItem);

		modeButtonGroup.add(practisingMenuItem);
		practisingMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.practisingMenuItem.mnemonic").charAt(0));
		practisingMenuItem.setText(bundle.getString("MainFrame.practisingMenuItem.text"));
		practisingMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				practisingMenuItemActionPerformed(evt);
			}
		});
		modeMenu.add(practisingMenuItem);

		jMenuBar1.add(modeMenu);

		optionMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.optionenMenuMnemonic").charAt(0));
		optionMenu.setText(bundle.getString("MainFrame.optionenMenu.text"));

		showCandidatesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showCandidatesMenuItemMnemonic").charAt(0));
		showCandidatesMenuItem.setText(bundle.getString("MainFrame.showCandidatesMenuItem.text"));
		showCandidatesMenuItem.setSelected(Options.getInstance().isShowCandidates());
		showCandidatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showCandidatesMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(showCandidatesMenuItem);

		showCandidateHighlightMenuItem.setText(bundle.getString("MainFrame.showCandidateHighlightMenuItem.text"));
		showCandidateHighlightMenuItem.setSelected(Options.getInstance().isShowCandidateHighlight());
		showCandidateHighlightMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showCandidateHighlightMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(showCandidateHighlightMenuItem);

		showWrongValuesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showWrongValuesMenuItemMnemonic").charAt(0));
		showWrongValuesMenuItem.setText(bundle.getString("MainFrame.showWrongValuesMenuItem.text"));
		showWrongValuesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showWrongValuesMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(showWrongValuesMenuItem);

		showDeviationsMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showDeviationsMenuItemMnemonic").charAt(0));
		showDeviationsMenuItem.setSelected(true);
		showDeviationsMenuItem.setText(bundle.getString("MainFrame.showDeviationsMenuItem.text"));
		showDeviationsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showDeviationsMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(showDeviationsMenuItem);

		showColorKuMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showColorKuMenuItem.mnemonic").charAt(0));
		showColorKuMenuItem.setSelected(true);
		showColorKuMenuItem.setText(bundle.getString("MainFrame.showColorKuMenuItem.text"));
		showColorKuMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showColorKuMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(showColorKuMenuItem);
		optionMenu.add(new javax.swing.JPopupMenu.Separator());

		colorButtonGroup.add(colorCellsMenuItem);
		colorCellsMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.colorCellsMenuItem.mnemonic").charAt(0));
		colorCellsMenuItem.setSelected(true);
		colorCellsMenuItem.setText(bundle.getString("MainFrame.colorCellsMenuItem.text"));
		colorCellsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				colorCellsMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(colorCellsMenuItem);

		colorButtonGroup.add(colorCandidatesMenuItem);
		colorCandidatesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.colorCandidatesMenuItem.mnemonic").charAt(0));
		colorCandidatesMenuItem.setText(bundle.getString("MainFrame.colorCandidatesMenuItem.text"));
		colorCandidatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				colorCandidatesMenuItemActionPerformed(evt);
			}
		});
		optionMenu.add(colorCandidatesMenuItem);

		optionMenu.add(new javax.swing.JPopupMenu.Separator());

		levelMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.levelMenuMnemonic").charAt(0));
		levelMenu.setText(bundle.getString("MainFrame.levelMenu.text"));

		levelButtonGroup.add(levelEasyMenuItem);
		levelEasyMenuItem.setSelected(true);
		levelEasyMenuItem.setText("Leicht");
		levelEasyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelEasyMenuItemActionPerformed(evt);
			}
		});
		levelMenu.add(levelEasyMenuItem);

		levelButtonGroup.add(levelMediumMenuItem);
		levelMediumMenuItem.setText("Mittel");
		levelMediumMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelMediumMenuItemActionPerformed(evt);
			}
		});
		levelMenu.add(levelMediumMenuItem);

		levelButtonGroup.add(levelHardMenuItem);
		levelHardMenuItem.setText("Schwer\n");
		levelHardMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelHardMenuItemActionPerformed(evt);
			}
		});
		levelMenu.add(levelHardMenuItem);

		levelButtonGroup.add(levelDiabolicalMenuItem);
		levelDiabolicalMenuItem.setText("Unfair");
		levelDiabolicalMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelDiabolicalMenuItemActionPerformed(evt);
			}
		});
		levelMenu.add(levelDiabolicalMenuItem);

		levelButtonGroup.add(levelExtremeMenuItem);
		levelExtremeMenuItem.setText("Extrem");
		levelExtremeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				levelExtremeMenuItemActionPerformed(evt);
			}
		});
		levelMenu.add(levelExtremeMenuItem);

		optionMenu.add(levelMenu);

		jMenuBar1.add(optionMenu);

		puzzleMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.raetselMenuMnemonic").charAt(0));
		puzzleMenu.setText(bundle.getString("MainFrame.raetselMenu.text"));

		vagueHintMenuItem.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.ALT_DOWN_MASK));
		vagueHintMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.vageHintMenuItemMnemonic").charAt(0));
		vagueHintMenuItem.setText(bundle.getString("MainFrame.vageHintMenuItem"));
		vagueHintMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vageHintMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(vagueHintMenuItem);

		mediumHintMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12,
				java.awt.event.InputEvent.CTRL_DOWN_MASK));
		mediumHintMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.mediumHintMenuItemMnemonic").charAt(0));
		mediumHintMenuItem.setText(bundle.getString("MainFrame.mediumHintMenuItem.text"));
		mediumHintMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mediumHintMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(mediumHintMenuItem);

		solutionStepMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
		solutionStepMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.loesungsSchrittMenuItemMnemonic").charAt(0));
		solutionStepMenuItem.setText(bundle.getString("MainFrame.loesungsSchrittMenuItem.text"));
		solutionStepMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loesungsSchrittMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(solutionStepMenuItem);
		puzzleMenu.add(new javax.swing.JPopupMenu.Separator());

		solvePuzzleMenuItem.setText(bundle.getString("MainFrame.solvePuzzleMenuItem.text"));
		solvePuzzleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showPuzzleSolution();
			}
		});
		puzzleMenu.add(solvePuzzleMenuItem);
		
		solutionCountMenuItem.setAccelerator(
			javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_M, 
				java.awt.event.InputEvent.CTRL_DOWN_MASK
			)
		);
		solutionCountMenuItem.setText(bundle.getString("MainFrame.solutionCountMenuItem.text"));
		solutionCountMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showSolutionCount();
			}
		});
		puzzleMenu.add(solutionCountMenuItem);		
		puzzleMenu.add(new JSeparator());

		backdoorSearchMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.backdoorSearchMenuItem.mnemonic").charAt(0));
		backdoorSearchMenuItem.setText(bundle.getString("MainFrame.backdoorSearchMenuItem.text"));
		backdoorSearchMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backdoorSearchMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(backdoorSearchMenuItem);

		historyMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.historyMenuItem.mnemonic").charAt(0));
		historyMenuItem.setText(bundle.getString("MainFrame.historyMenuItem.text"));
		historyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historyMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(historyMenuItem);

		createSavePointMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.setSavePointMenuItem.mnemonic").charAt(0));
		createSavePointMenuItem.setText(bundle.getString("MainFrame.createSavePointMenuItem.text"));
		createSavePointMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				createSavePointMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(createSavePointMenuItem);

		restoreSavePointMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.restoreSavePointMenuItem.mnemonic").charAt(0));
		restoreSavePointMenuItem.setText(bundle.getString("MainFrame.restoreSavePointMenuItem.text"));
		restoreSavePointMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				restoreSavePointMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(restoreSavePointMenuItem);
		puzzleMenu.add(new javax.swing.JPopupMenu.Separator());
		
		resetCandidatesMenuItem.setText(bundle.getString("MainFrame.resetCandidatesMenuItem.text"));
		resetCandidatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetCandidatesMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(resetCandidatesMenuItem);		

		setGivensMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.setGivensMenuItem.mnemonic").charAt(0));
		setGivensMenuItem.setText(bundle.getString("MainFrame.setGivensMenuItem.text"));
		setGivensMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setGivensMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(setGivensMenuItem);
		puzzleMenu.add(new javax.swing.JPopupMenu.Separator());

		setAllSinglesMenuItem
				.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
		setAllSinglesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.alleHiddenSinglesSetzenMenuItemMnemonic").charAt(0));
		setAllSinglesMenuItem.setText(bundle.getString("MainFrame.alleHiddenSinglesSetzenMenuItem.text"));
		setAllSinglesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alleHiddenSinglesSetzenMenuItemActionPerformed(evt);
			}
		});
		puzzleMenu.add(setAllSinglesMenuItem);

		jMenuBar1.add(puzzleMenu);

		viewMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.ansichtMenuMnemonic").charAt(0));
		viewMenu.setText(bundle.getString("MainFrame.ansichtMenu.text"));

		sudokuOnlyMenuItem.setAccelerator(
				KeyStroke.getKeyStroke("shift control " + java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.sudokuOnlyMenuItemMnemonic").toUpperCase().charAt(0)));
		viewButtonGroup.add(sudokuOnlyMenuItem);
		sudokuOnlyMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.sudokuOnlyMenuItemMnemonic").charAt(0));
		sudokuOnlyMenuItem.setSelected(true);
		sudokuOnlyMenuItem.setText(bundle.getString("MainFrame.sudokuOnlyMenuItem.text"));
		sudokuOnlyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sudokuOnlyMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(sudokuOnlyMenuItem);
		viewMenu.add(new javax.swing.JPopupMenu.Separator());

		summaryMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift control " + java.util.ResourceBundle
				.getBundle("intl/MainFrame").getString("MainFrame.summaryMenuItemMnemonic").toUpperCase().charAt(0)));
		viewButtonGroup.add(summaryMenuItem);
		summaryMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.summaryMenuItemMnemonic").charAt(0));
		summaryMenuItem.setText(bundle.getString("MainFrame.summaryMenuItem.text"));
		summaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				summaryMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(summaryMenuItem);

		solutionMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift control " + java.util.ResourceBundle
				.getBundle("intl/MainFrame").getString("MainFrame.solutionMenuItemMnemonic").toUpperCase().charAt(0)));
		viewButtonGroup.add(solutionMenuItem);
		solutionMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.solutionMenuItemMnemonic").charAt(0));
		solutionMenuItem.setText(bundle.getString("MainFrame.solutionMenuItem.text"));
		solutionMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				solutionMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(solutionMenuItem);

		allStepsMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift control " + java.util.ResourceBundle
				.getBundle("intl/MainFrame").getString("MainFrame.allStepsMenuItemMnemonic").toUpperCase().charAt(0)));
		viewButtonGroup.add(allStepsMenuItem);
		allStepsMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.allStepsMenuItemMnemonic").charAt(0));
		allStepsMenuItem.setText(bundle.getString("MainFrame.allStepsMenuItem.text"));
		allStepsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				allStepsMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(allStepsMenuItem);

		cellZoomMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift control " + java.util.ResourceBundle
				.getBundle("intl/MainFrame").getString("MainFrame.cellZoomMenuItemMnemonic").toUpperCase().charAt(0)));
		viewButtonGroup.add(cellZoomMenuItem);
		cellZoomMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.cellZoomMenuItemMnemonic").charAt(0));
		cellZoomMenuItem.setText(bundle.getString("MainFrame.cellZoomMenuItem.text"));
		cellZoomMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cellZoomMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(cellZoomMenuItem);
		viewMenu.add(new javax.swing.JPopupMenu.Separator());

		showHintPanelMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showHintPanelMenuItem.mnemonic").charAt(0));
		showHintPanelMenuItem.setSelected(true);
		showHintPanelMenuItem.setText(bundle.getString("MainFrame.showHintPanelMenuItem.text"));
		showHintPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showHintPanelMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(showHintPanelMenuItem);

		showToolBarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showToolBarMenuItem.mnemonic").charAt(0));
		showToolBarMenuItem.setSelected(true);
		showToolBarMenuItem.setText(bundle.getString("MainFrame.showToolBarMenuItem.text"));
		showToolBarMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showToolBarMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(showToolBarMenuItem);

		showHintButtonsCheckBoxMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.showHintButtonsCheckBoxMenuItem.mnemonic").charAt(0));
		showHintButtonsCheckBoxMenuItem.setText(bundle.getString("MainFrame.showHintButtonsCheckBoxMenuItem.text"));
		showHintButtonsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showHintButtonsCheckBoxMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(showHintButtonsCheckBoxMenuItem);

		fullScreenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
				java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
		fullScreenMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.fullScreenMenuItem.mnemonic").charAt(0));
		fullScreenMenuItem.setText(bundle.getString("MainFrame.fullScreenMenuItem.text"));
		fullScreenMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fullScreenMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(fullScreenMenuItem);
		viewMenu.add(new javax.swing.JPopupMenu.Separator());

		resetViewMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.resetViewMenuItemMnemonic").charAt(0));
		resetViewMenuItem.setText(bundle.getString("MainFrame.resetViewMenuItem.text"));
		resetViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetViewMenuItemActionPerformed(evt);
			}
		});
		viewMenu.add(resetViewMenuItem);

		jMenuBar1.add(viewMenu);

		helpMenu.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.helpMenu.mnemonic").charAt(0));
		helpMenu.setText(bundle.getString("MainFrame.helpMenu.text"));

		keyMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.keyMenuItem.mnemonic").charAt(0));
		keyMenuItem.setText(bundle.getString("MainFrame.keyMenuItem.text"));
		keyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				keyMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(keyMenuItem);
		helpMenu.add(new javax.swing.JPopupMenu.Separator());

		userManualMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.userManualMenuItem.mnemonic").charAt(0));
		userManualMenuItem.setText(bundle.getString("MainFrame.userManualMenuItem.text"));
		userManualMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				userManualMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(userManualMenuItem);

		solvingGuideMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.solvingGuideMenuItem.mnemonic").charAt(0));
		solvingGuideMenuItem.setText(bundle.getString("MainFrame.solvingGuideMenuItem.text"));
		solvingGuideMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				solvingGuideMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(solvingGuideMenuItem);

		projectHomePageMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.projectHomePageMenuItem.mnemonic").charAt(0));
		projectHomePageMenuItem.setText(bundle.getString("MainFrame.projectHomePageMenuItem.text"));
		projectHomePageMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				projectHomePageMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(projectHomePageMenuItem);
		helpMenu.add(new javax.swing.JPopupMenu.Separator());

		reportErrorMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.reportErrorMenuItem.mnemonic").charAt(0));
		reportErrorMenuItem.setText(bundle.getString("MainFrame.reportErrorMenuItem.text"));
		reportErrorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reportErrorMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(reportErrorMenuItem);

		askQuestionMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("intl/MainFrame")
				.getString("MainFrame.askQuestionMenuItem.mnemonic").charAt(0));
		askQuestionMenuItem.setText(bundle.getString("MainFrame.askQuestionMenuItem.text"));
		askQuestionMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				askQuestionMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(askQuestionMenuItem);
		helpMenu.add(new javax.swing.JPopupMenu.Separator());

		aboutMenuItem.setMnemonic(
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.aboutMenuItem.").charAt(0));
		aboutMenuItem.setText(bundle.getString("MainFrame.aboutMenuItem.text"));
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(aboutMenuItem);

		jMenuBar1.add(helpMenu);

		setJMenuBar(jMenuBar1);

		pack();
	}

	private void savePuzzleAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		saveToFile(true);
	}

	private void importPuzzleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		importWindow.setVisible(true);
		importWindow.focusCursor();
	}

	private void exportPuzzleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		exportWindow.setVisible(true);
	}

	private void loadPuzzleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		loadFromFile(true);
	}

	private void configMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		new ConfigDialog(this, true, -1).setVisible(true);
		sudokuPanel.resetActiveColor();
		
		if (cellZoomPanel.isColoring()) {
			statusPanelColorResult.setBackground(sudokuPanel.getActiveColor());
		}
		
		sudokuPanel.setColorIconsInPopupMenu();
		check();
		fixFocus();
		sudokuPanel.repaint();
		repaint();
	}

	private void statusLabelCellCandidateMouseClicked(java.awt.event.MouseEvent evt) {
		sudokuPanel.updateCellZoomPanel();
		check();
		fixFocus();
	}

	private void allStepsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		setSplitPane(allStepsPanel);
		// initializeResultPanels();
		repaint();
	}

	private void solutionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setSplitPane(solutionPanel);
		// initializeResultPanels();
		repaint();
	}

	private void sudokuOnlyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		splitPanel.setRight(null);
		if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			if (splitPanel.getBounds().getWidth() < splitPanel.getBounds().getHeight()) {
				setSize(getWidth() + 1, getHeight());
			}
		}
	}

	private void summaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setSplitPane(summaryPanel);
		// initializeResultPanels();
		repaint();
	}

	private void speichernAlsBildMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		WriteAsPNGDialog dlg = new WriteAsPNGDialog(this, true, saveImageDefaultSize, bildAufloesung, bildEinheit);
		dlg.setVisible(true);
		if (dlg.isOk()) {
			File bildFile = dlg.getBildFile();
			bildAufloesung = dlg.getAufloesung();
			saveImageDefaultSize = dlg.getBildSize();
			bildEinheit = dlg.getEinheit();
			int size = 0;
			switch (bildEinheit) {
			case 0:
				size = (int) (saveImageDefaultSize / 25.4 * bildAufloesung);
				break;
			case 1:
				size = (int) (saveImageDefaultSize * bildAufloesung);
				break;
			case 2:
				size = (int) saveImageDefaultSize;
				break;
			}
			if (bildFile.exists()) {
				// Override warning!
				MessageFormat msgf = new MessageFormat("");
				Object[] args = new Object[] { bildFile.getName() };
				msgf.applyPattern(
						java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.file_exists"));
				String warning = msgf.format(args);
				String title = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint");
				if (JOptionPane.showConfirmDialog(null, warning, title,
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}
			sudokuPanel.saveSudokuAsPNG(bildFile, size, bildAufloesung);
		}
	}

	private void druckenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (job == null) {
			job = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = job.defaultPage();
		}
		try {
			job.setPrintable(sudokuPanel, pageFormat);
			if (job.printDialog()) {
				job.print();
			}
		} catch (PrinterException ex) {
			JOptionPane.showMessageDialog(
				this,
				ex.toString(),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
				JOptionPane.ERROR_MESSAGE
			);
		}
	}

	private void printSetupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (job == null) {
			job = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = job.defaultPage();
		}
		pageFormat = job.pageDialog(pageFormat);
	}

	private void restartSpielMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (JOptionPane.showConfirmDialog(this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.start_new_game"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.start_new"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			sudokuPanel.setSudoku(sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY));
			sudokuPanel.checkProgress();
			allStepsPanel.setSudoku(sudokuPanel.getSudoku());
			initializeResultPanels();
			repaint();
			setPlay(true);
			check();
			fixFocus();
		}
	}

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		formWindowClosed(null);
		System.exit(0);
	}

	private void copyLibraryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		copyToClipboard(ClipboardMode.LIBRARY, false);
	}

	private void copyPmGridWithStepMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		SolutionStep activeStep = sudokuPanel.getStep();
		if (activeStep == null) {
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.no_step_selected"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
				JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		copyToClipboard(ClipboardMode.PM_GRID_WITH_STEP, false);
	}

	private void copyPmGridMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		copyToClipboard(ClipboardMode.PM_GRID, false);
	}

	private void copyFilledMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		copyToClipboard(ClipboardMode.VALUES_ONLY, false);
	}

	private void showDeviationsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.setShowDeviations(showDeviationsMenuItem.isSelected());
		check();
		fixFocus();
	}

	private void newGameToolButtonActionPerformed(java.awt.event.ActionEvent evt) {

		int actLevel = Options.getInstance().getActLevel();
		DifficultyLevel actDiffLevel = Options.getInstance().getDifficultyLevel(actLevel);
		if (Options.getInstance().getGameMode() == GameMode.LEARNING) {
			// in LEARNING ANY puzzle is accepted, that has at least one Training Step in it
			actDiffLevel = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
		}
		
		String preGenSudoku = BackgroundGeneratorThread.getInstance().getSudoku(actDiffLevel, Options.getInstance().getGameMode());
		Sudoku2 tmpSudoku = null;
		
		if (preGenSudoku == null) {
			// no pre-genrated puzzle available -> do it in GUI
			GenerateSudokuProgressDialog dlg = new GenerateSudokuProgressDialog(this, true, actDiffLevel, Options.getInstance().getGameMode());
			dlg.setVisible(true);
			tmpSudoku = dlg.getSudoku();
		} else {
			tmpSudoku = new Sudoku2();
			tmpSudoku.setSudoku(preGenSudoku, true);
			Sudoku2 solvedSudoku = tmpSudoku.clone();
			SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
			solver.solve(
				actDiffLevel, 
				solvedSudoku, 
				true, 
				null, 
				false, 
				Options.getInstance().solverSteps, 
				Options.getInstance().getGameMode()
			);
			tmpSudoku.setLevel(solvedSudoku.getLevel());
			tmpSudoku.setScore(solvedSudoku.getScore());
		}
		
		if (tmpSudoku != null) {
			
			sudokuPanel.setSudoku(tmpSudoku, true);
			allStepsPanel.setSudoku(sudokuPanel.getSudoku());
			initializeResultPanels();
			addSudokuToHistory(tmpSudoku);
			sudokuPanel.clearColoring();
			sudokuPanel.setShowHintCellValue(0);
			sudokuPanel.setShowInvalidOrPossibleCells(false);
			
			if (Options.getInstance().getGameMode() == GameMode.LEARNING) {
				// solve the sudoku up until the first trainingStep
				Sudoku2 trainingSudoku = sudokuPanel.getSudoku();
				List<SolutionStep> steps = sudokuPanel.getSolver().getSteps();
				for (SolutionStep step : steps) {
					if (step.getType().getStepConfig().isEnabledTraining()) {
						break;
					} else {
						sudokuPanel.getSolver().doStep(trainingSudoku, step);
					}
				}
			}
			
			clearSavePoints();
			sudokuFileName = null;
			setTitleWithFile();
			check();
		}
		
		cellZoomPanel.setDefaultMouse(true);
		sudokuPanel.clearColoring();
		sudokuPanel.setActiveColor(null);
		setPlay(true);
		fixFocus();
	}

	private void levelComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		Options.getInstance().setActLevel(Options.getInstance().getDifficultyLevels()[levelComboBox.getSelectedIndex() + 1].getOrdinal());
		BackgroundGeneratorThread.getInstance().setNewLevel(Options.getInstance().getActLevel());
		check();
		fixFocus();
	}

	private void levelExtremeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setLevelFromMenu();
	}

	private void levelDiabolicalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setLevelFromMenu();
	}

	private void levelHardMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setLevelFromMenu();
	}

	private void levelMediumMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setLevelFromMenu();
	}

	private void levelEasyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setLevelFromMenu();
	}

	private void f1ToggleButtonActionPerformed1(java.awt.event.ActionEvent evt) {
		f1ToggleButtonActionPerformed(evt);
	}

	private void f1ToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setToggleButton((JToggleButton) evt.getSource(), (evt.getModifiers() & KeyEvent.CTRL_DOWN_MASK) != 0);
	}

	private void redGreenToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.setInvalidCells(!sudokuPanel.isInvalidCells());
		sudokuPanel.repaint();
		check();
		fixFocus();
	}

	private void mediumHintMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		getHint(1);
	}

	private void vageHintMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		getHint(0);
	}

	private void alleHiddenSinglesSetzenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		hinweisAbbrechenButtonActionPerformed(null);
		SolutionStep step = null;
		
		while (
			(step = sudokuPanel.getNextStep(true)) != null && 
			(step.getType() == SolutionType.HIDDEN_SINGLE || 
			 step.getType() == SolutionType.FULL_HOUSE || 
			 step.getType() == SolutionType.NAKED_SINGLE)) {
			sudokuPanel.doStep();
		}
		
		sudokuPanel.abortStep();
		sudokuPanel.checkProgress();
		fixFocus();
		repaint();
	}

	private void hinweisAbbrechenButtonActionPerformed(java.awt.event.ActionEvent evt) {
		abortStep();
	}

	private void hinweisAusfuehrenButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		sudokuPanel.doStep();
		sudokuPanel.checkProgress();
		hinweisTextArea.setText("");
		hinweisAbbrechenButton.setEnabled(false);
		hinweisAusfuehrenButton.setEnabled(false);
		
		if (executeStepToggleButton != null) {
			executeStepToggleButton.setEnabled(false);
		}
		
		if (abortStepToggleButton != null) {
			abortStepToggleButton.setEnabled(false);
		}
		
		fixFocus();
	}

	private void loesungsSchrittMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		getHint(2);
	}

	private void neuerHinweisButtonActionPerformed(java.awt.event.ActionEvent evt) {
		loesungsSchrittMenuItemActionPerformed(evt);
	}

	private void neuMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		newGameToolButtonActionPerformed(null);
	}

	private void copyCluesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		copyToClipboard(ClipboardMode.CLUES_ONLY, false);
	}

	private void showWrongValuesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.setShowWrongValues(showWrongValuesMenuItem.isSelected());
		check();
		fixFocus();
	}

	private void showCandidatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		Options.getInstance().toggleShowCandidates();

		if (!showCandidatesMenuItem.isSelected()) {
			// just set the flag and be done!
			sudokuPanel.setShowCandidates(showCandidatesMenuItem.isSelected());
		} else {
			// if no user candidates have been set, the internal flag is just toggled.
			// if user candidates have been set, further checks have to be made
			if (sudokuPanel.getSudoku().userCandidatesEmpty()) {
				// just set the flag and be done!				
				sudokuPanel.setShowCandidates(showCandidatesMenuItem.isSelected());
				
			} else {
				// display a dialog, that lets the user choose, what to do
				boolean doYes = true;
				if (!sudokuPanel.getSudoku().checkUserCands()) {
					// necessary candidates are missing!
					int ret = JOptionPane.showConfirmDialog(null,
							java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.candidatesMissing"),
							java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (ret == JOptionPane.CANCEL_OPTION) {
						// change the menu item!
						showCandidatesMenuItem.setSelected(false);
						fixFocus();
						return;
					} else if (ret == JOptionPane.YES_OPTION) {
						doYes = true;
					} else {
						doYes = false;
					}
				}
				
				if (doYes) {
					// retain all changes to the user candidates
					sudokuPanel.getSudoku().resetCandidates();
					//sudokuPanel.getSudoku().rebuildAllCandidates();
					sudokuPanel.getSolver().setSudoku(sudokuPanel.getSudoku());
					sudokuPanel.checkProgress();
					boolean showCandidates = showCandidatesMenuItem.isSelected();
					sudokuPanel.setShowCandidates(showCandidates);
				/*
				} else {
					// revert all changes
					sudokuPanel.getSudoku().switchToAllCandidates();
					sudokuPanel.getSolver().setSudoku(sudokuPanel.getSudoku());
					sudokuPanel.checkProgress();
					sudokuPanel.setShowCandidates(showCandidatesMenuItem.isSelected());*/
				}
			}
		}

		check();
		fixFocus();
	}

	private void showCandidateHighlightMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		Options.getInstance().setShowCandidateHighlight(this.showCandidateHighlightMenuItem.isSelected());
		repaint();
	}

	private void redoToolButtonActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.redo();
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		allStepsPanel.resetPanel();
		check();
		fixFocus();
	}

	private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.redo();
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		allStepsPanel.resetPanel();
		check();
		fixFocus();
	}

	private void undoToolButtonActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.undo();
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		allStepsPanel.resetPanel();
		check();
		fixFocus();
	}

	private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		sudokuPanel.undo();
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		allStepsPanel.resetPanel();
		check();
		fixFocus();
	}

	private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		try {
			
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipboardContent = clip.getContents(this);
			
			if ((clipboardContent != null) && (clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor))) {
				String content = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
				setPuzzle(content);
				clearSavePoints();
			}
			
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error pasting from clipboard", ex);
		}
		
		check();
		fixFocus();
	}

	private void outerSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {
		
		// if the hintPanel is to small, the horizontal divider is moved up
		if (!outerSplitPaneInitialized && 
			outerSplitPane.getSize().getHeight() != 0 &&
			hintPanel.getSize().getHeight() != 0) {
			// adjust to minimum size of hintPanel to allow for LAF differences
			outerSplitPaneInitialized = true; // beware of recursion!
			int diff = (int) (hintPanel.getMinimumSize().getHeight() - hintPanel.getSize().getHeight());
			if (diff > 0) {
				resetHDivLocLoc = outerSplitPane.getDividerLocation() - diff - 5;
				outerSplitPane.setDividerLocation(resetHDivLocLoc);;
			}
			outerSplitPaneInitialized = false;
		}

		// if the window layout is reset, the horizontal divider is moved back to its
		// default location; since we dont know, how large toolbar and statu line are
		// in each and every laf, this value is too small and has to be
		// adjusted again!
		if (resetHDivLoc && outerSplitPane.getDividerLocation() != resetHDivLocLoc) {
			resetHDivLoc = false;
			if (System.currentTimeMillis() - resetHDivLocTicks < 1000) {
				outerSplitPane.setDividerLocation(resetHDivLocLoc);
				setSize(getWidth() + 1, getHeight());
			} else {
			}
		}
	}

	private void formWindowClosed(java.awt.event.WindowEvent evt) {
		try {
			if (!changingFullScreenMode) {
				writeOptionsWithWindowState(null);
			}
			changingFullScreenMode = false;
		} catch (FileNotFoundException ex) {
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Can't write options", ex);
		}
	}

	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		formWindowClosed(null);
	}

	private void solveUpToButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (sudokuPanel != null) {
			sudokuPanel.solveUpTo();
		}
		
		check();
		fixFocus();
	}

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		new AboutDialog(this, true).setVisible(true);
		check();
		fixFocus();
	}

	private void keyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		new KeyboardLayoutFrame().setVisible(true);
		check();
		fixFocus();
	}

	private void resetViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setWindowLayout(true);
		check();
		fixFocus();
		repaint();
	}

	private void hintPanelPropertyChange(java.beans.PropertyChangeEvent evt) {
		outerSplitPanePropertyChange(null);
	}

	private void newEmptyGameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (sudokuPanel.getSolvedCellsAnz() != 0) {
			
			int antwort = JOptionPane.showConfirmDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.delete_sudoku"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.new_input"),
				JOptionPane.YES_NO_OPTION
			);
			
			// do nothing
			if (antwort != JOptionPane.YES_OPTION) {
				return;
			}
		}
		
		sudokuPanel.setSudoku((String) null);
		sudokuPanel.checkProgress();
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		resetResultPanels();
		sudokuPanel.setNoClues();
		cellZoomPanel.setDefaultMouse(true);
		sudokuPanel.clearColoring();
		sudokuPanel.setActiveColor(null);
		hinweisAbbrechenButtonActionPerformed(null);
		setPlay(false);
	}

	private void spielEditierenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		resetResultPanels();
		sudokuPanel.setNoClues();
		sudokuPanel.checkProgress();
		sudokuPanel.resetShowHintCellValues();
		hinweisAbbrechenButtonActionPerformed(null);
		setPlay(false);
	}

	private void spielSpielenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (sudokuPanel.getSolvedCellsAnz() > 0) {
			sudokuPanel.setSudoku(sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY));
			sudokuPanel.checkProgress();
			allStepsPanel.setSudoku(sudokuPanel.getSudoku());
			initializeResultPanels();
		}
		
		setPlay(true);
	}

	private void resetSpielMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		if (JOptionPane.showConfirmDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.reset_game"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.reset"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			
			sudokuPanel.setSudoku(sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY));
			sudokuPanel.checkProgress();
			allStepsPanel.setSudoku(sudokuPanel.getSudoku());
			allStepsPanel.resetPanel();
			
			repaint();
			setPlay(true);
			check();
			fixFocus();
		}
	}

	private void statusPanelColor1MouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(Options.getInstance().getColoringColors()[0]);
	}

	private void statusPanelColor2MouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(Options.getInstance().getColoringColors()[2]);
	}

	private void statusPanelColor3MouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(Options.getInstance().getColoringColors()[4]);
	}

	private void statusPanelColor4MouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(Options.getInstance().getColoringColors()[6]);
	}

	private void statusPanelColor5MouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(Options.getInstance().getColoringColors()[8]);
	}

	private void statusPanelColorClearMouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(null);
	}

	private void statusPanelColorResetMouseClicked(java.awt.event.MouseEvent evt) {
		coloringPanelClicked(null);
	}

	private void colorCellsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		//sudokuPanel.setColorCells(true);
		cellZoomPanel.setColorCells(true);
		sudokuPanel.updateCellZoomPanel();
		check();
		fixFocus();
	}

	private void colorCandidatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		//sudokuPanel.setColorCells(false);
		cellZoomPanel.setColorCandidates(true);
		sudokuPanel.updateCellZoomPanel();
		check();
		fixFocus();
	}

	public void toggleSingleClickMode() {
		Options.getInstance().setSingleClickMode(!Options.getInstance().isSingleClickMode());
		if (Options.getInstance().isSingleClickMode()) {
			sudokuPanel.clearLastCandidateMouseOn();
		}
	}

	public void setSingleClickMode(boolean enabled) {

		if (enabled && !Options.getInstance().isSingleClickMode()) {
			sudokuPanel.clearLastCandidateMouseOn();
		}

		Options.getInstance().setSingleClickMode(enabled);
	}

	private void cellZoomMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setSplitPane(cellZoomPanel);
		repaint();
	}

	private void userManualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		MyBrowserLauncher.getInstance().launchUserManual();
	}

	private void solvingGuideMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		MyBrowserLauncher.getInstance().launchSolvingGuide();
	}

	private void projectHomePageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		MyBrowserLauncher.getInstance().launchHomePage();
	}

	private void loadConfigMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		loadFromFile(false);
	}

	private void saveConfigAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		saveToFile(false);
	}

	private void historyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		GuiState state = new GuiState(sudokuPanel, sudokuPanel.getSolver(), solutionPanel);
		state.get(true);
		HistoryDialog dlg = new HistoryDialog(this, true);
		dlg.setVisible(true);
		String puzzle = dlg.getSelectedPuzzle();
		
		if (puzzle != null) {
			if (dlg.isDoubleClicked()) {
				// everything is already initialized, so don't do anything
			} else {
				// act like paste
				setPuzzle(puzzle);
			}
			clearSavePoints();
		} else {
			// restore everything
			setState(state);
		}
		
		state = null;
	}

	private void createSavePointMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		String defaultName = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.default") + 
				" "	+ (savePoints.size() + 1);
		
		String name = (String) JOptionPane.showInputDialog(this,
				ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.message"),
				ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.title"),
				JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
		
		if (name != null) {
			GuiState state = new GuiState(sudokuPanel, sudokuPanel.getSolver(), solutionPanel);
			state.get(true);
			state.setName(name);
			state.setTimestamp(new Date());
			savePoints.add(state);
		}
	}

	private void restoreSavePointMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		GuiState state = new GuiState(sudokuPanel, sudokuPanel.getSolver(), solutionPanel);
		state.get(true);
		RestoreSavePointDialog dlg = new RestoreSavePointDialog(this, true);
		dlg.setVisible(true);
		
		if (!dlg.isOkPressed()) {
			// restore everything
			setState(state);
		}
		
		state = null;
	}

	private void playingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setMode(GameMode.PLAYING, true);
		check();
	}

	private void learningMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setMode(GameMode.LEARNING, true);
		check();
	}

	private void practisingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setMode(GameMode.PRACTISING, true);
		check();
	}

	private void backdoorSearchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		new BackdoorSearchDialog(this, true, sudokuPanel).setVisible(true);
	}
	
	private void resetCandidatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/MainFrame");
		String msg = bundle.getString("MainFrame.resetCandidatesMenuItem.dialog");
		
		int input = JOptionPane.showConfirmDialog(null, msg);
		if (input != 0) {
			return;
		}
		
		sudokuPanel.getSudoku().resetCandidates();
		repaint();
	}

	private void setGivensMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		SetGivensDialog dlg = new SetGivensDialog(this, true);
		dlg.setVisible(true);
		if (dlg.isOkPressed()) {
			String givens = dlg.getGivens();
			sudokuPanel.setGivens(givens);
		}
	}

	private void fullScreenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (fullScreenMenuItem.isSelected()) {
			changingFullScreenMode = true;
			saveWindowStateInOptions();
			dispose();
			setUndecorated(true);
			setExtendedState(MAXIMIZED_BOTH);
			hintPanel.setVisible(false);
			jToolBar1.setVisible(true);
			showToolBarMenuItem.setEnabled(false);
			showHintPanelMenuItem.setEnabled(false);
			setVisible(true);
		} else {
			changingFullScreenMode = true;
			dispose();
			setUndecorated(false);
			setExtendedState(NORMAL);
			showToolBarMenuItem.setEnabled(true);
			showHintPanelMenuItem.setEnabled(true);
			setWindowLayout(false);
			setVisible(true);
		}
		
		check();
		fixFocus();
	}

	private void showHintPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		Options.getInstance().setShowHintPanel(showHintPanelMenuItem.isSelected());
		hintPanel.setVisible(showHintPanelMenuItem.isSelected());
		
		if (Options.getInstance().isShowHintPanel()) {
			
			int horzDivLoc = Options.getInstance().getInitialHorzDividerLoc();
			if (horzDivLoc > getHeight() - 204) {
				horzDivLoc = getHeight() - 204;
				Options.getInstance().setInitialHorzDividerLoc(horzDivLoc);
			}
			
			outerSplitPane.setDividerLocation(horzDivLoc);
		}
	}

	private void showToolBarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		Options.getInstance().setShowToolBar(showToolBarMenuItem.isSelected());
		jToolBar1.setVisible(showToolBarMenuItem.isSelected());
	}

	private void fxyToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setToggleButton((JToggleButton) evt.getSource(), false);
	}

	private void showHintButtonsCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		Options.getInstance().setShowHintButtonsInToolbar(showHintButtonsCheckBoxMenuItem.isSelected());
		setShowHintButtonsInToolbar();
	}

	private void extendedPrintMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		new ExtendedPrintDialog(this, true).setVisible(true);
	}

	private void copySSMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		copyToClipboard(null, true);
	}

	private void savePuzzleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (sudokuFileName != null) {
			try {
				saveToFile(true, sudokuFileName, sudokuFileType);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
					this, 
					ex.toString(),
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
					JOptionPane.ERROR_MESSAGE
				);
				sudokuFileName = null;
			}
		}
	}

	private void showColorKuMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		Options.getInstance().setShowColorKuAct(showColorKuMenuItem.isSelected());
		sudokuPanel.setShowColorKu();
		check();
		fixFocus();
	}

	private void askQuestionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		MyBrowserLauncher.getInstance().launchForum();
	}

	private void reportErrorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		MyBrowserLauncher.getInstance().launchTracker();
	}

	/**
	 * Adjusts icons for hint toggle buttons according to the mode (normal/ColorKu)
	 * and according to the colors (necessary for color changes). Icons are created
	 * on the fly as necessary.
	 * 
	 * @param on
	 */
	private void prepareToggleButtonIcons(boolean on) {
		
		if (on) {
			
			for (int i = 0, lim = toggleButtons.length - 1; i < lim; i++) {
				
				if (toggleButtonImagesColorKu[i] == null || 
					!toggleButtonImagesColorKu[i].getColor().
					equals(Options.getInstance().getColorKuColor(i + 1))) {

					toggleButtonImagesColorKu[i] = new ColorKuImage(
						TOGGLE_BUTTON_ICON_SIZE, 
						Options.getInstance().getColorKuColor(i + 1)
					);
					
					toggleButtonIconsColorKu[i] = new ImageIcon(toggleButtonImagesColorKu[i]);
				}
				
				toggleButtonIcons[i] = toggleButtonIconsColorKu[i];
				emptyToggleButtonIcons[i] = emptyToggleButtonIconOrgColorKu;
			}

		} else {
			for (int i = 0, lim = toggleButtons.length - 1; i < lim; i++) {
				toggleButtonIcons[i] = toggleButtonIconsOrg[i];
				emptyToggleButtonIcons[i] = emptyToggleButtonIconsOrg[i];
			}
		}
	}

	/**
	 * Gets a new hint for the sudoku if possible. Checks are made to ensure, that
	 * hints are only displayed for valid puzzles.
	 * 
	 * @param mode <code>0</code> for "vage hint", <code>1</code> for "concrete
	 *             hint" and <code>2</code> for "show next step".
	 */
	private void getHint(int mode) {
		
		if (sudokuPanel.getSudoku().isSolved()) {
			
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.already_solved")
			);
			
			return;
		}
		
		if (sudokuPanel.getSudoku().getStatus() == SudokuStatus.EMPTY || 
			sudokuPanel.getSudoku().getStatus() == SudokuStatus.INVALID) {
			
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_puzzle")
			);
			
			return;
		}
		
		if (!sudokuPanel.isShowCandidates()) {
			
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.not_available"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint"),
				JOptionPane.INFORMATION_MESSAGE
			);
			
			return;
		}
		
		if (sudokuPanel.getSudoku().checkSudoku() == false) {
			
			JOptionPane.showMessageDialog(
				this,
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_values_or_candidates"),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint"),
				JOptionPane.INFORMATION_MESSAGE
			);
			
			return;
		}
		
		SolutionStep step = sudokuPanel.getNextStep(false);
		if (mode == 0 || mode == 1) {
			
			sudokuPanel.abortStep();
			fixFocus();
			
			if (step != null) {
				
				int strMode = 0;
				String msg = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.vage_hint");
				
				if (mode == 1) {
					strMode = 1;
					msg = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium_hint");
				}
				
				JOptionPane.showMessageDialog(
					this,
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.possible_step") + step.toString(strMode),
					msg,
					JOptionPane.INFORMATION_MESSAGE
				);
				
			} else {
				
				JOptionPane.showMessageDialog(
					this,
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dont_know"),
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
					JOptionPane.ERROR_MESSAGE
				);
			}
		} else {
			
			if (step != null) {
				setSolutionStep(step, false);
			} else {
				hinweisTextArea.setText(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dont_know"));
				hinweisTextArea.setCaretPosition(0);
			}
		}
		
		fixFocus();
		check();
	}

	/**
	 * Sets a new mode ({@link GameMode#LEARNING}, {@link GameMode#PLAYING} or
	 * {@link GameMode#PRACTISING}). If the new mode is "playing", no further action
	 * is necessary. If the new mode is "learning" or "practising", steps have to be
	 * selected.<br>
	 * If a user tries to set "learning" or "practising", but doesnt select any
	 * steps, "playing" is set.<br>
	 * If the configuration dialog is cancelled, the mode is not changed.
	 * 
	 * @param newMode
	 * @param showDialog
	 */
	private void setMode(GameMode newMode, boolean showDialog) {
		
		if (newMode == GameMode.PLAYING) {
			Options.getInstance().setGameMode(newMode);
		} else {
			
			if (showDialog) {
				// show config dialog
				ConfigTrainingDialog dlg = new ConfigTrainingDialog(this, true);
				dlg.setVisible(true);
				if (!dlg.isOkPressed()) {
					return;
				}
			}
			
			String techniques = Options.getInstance().getTrainingStepsString(true);
			if (techniques.isEmpty()) {
				
				JOptionPane.showMessageDialog(
					this,
					ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.notechniques"),
					ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
					JOptionPane.ERROR_MESSAGE
				);
				
				Options.getInstance().setGameMode(GameMode.PLAYING);
				
			} else {
				Options.getInstance().setGameMode(newMode);
			}
		}
	}

	/**
	 * Sets a puzzle and initializes all views. Used by
	 * {@link #pasteMenuItemActionPerformed} and
	 * {@link #historyMenuItemActionPerformed(java.awt.event.ActionEvent)}. This
	 * method should only be used if the puzzle is only available as String. If more
	 * state information is saved, use {@link #setState(sudoku.GuiState)} instead.
	 * 
	 * @param puzzle
	 */
	public void setPuzzle(String puzzle) {

		try {
			sudokuPanel.setSudoku(puzzle);
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error setting sudoku in SudokuPanel", ex);
		}

		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		initializeResultPanels();
		sudokuPanel.clearColoring();
		sudokuPanel.setShowHintCellValue(0);
		sudokuPanel.setShowInvalidOrPossibleCells(false);
		setPlay(true);
		check();
		repaint();
	}

	/**
	 * Restores a complete GUI state including puzzle (optionally with coloring and
	 * selected step), solutions and summary. Used by {@link #loadFromFile(boolean)}
	 * and {@link RestoreSavePointDialog}.<br>
	 * 
	 * @param state
	 */
	public void setState(GuiState state) {
		state.set();
		summaryPanel.initialize(SudokuSolverFactory.getDefaultSolverInstance());
		allStepsPanel.setSudoku(sudokuPanel.getSudoku());
		allStepsPanel.resetPanel();
		setSolutionStep(state.getStep(), true);
		setPlay(true);
		check();
		repaint();
	}

	/**
	 * Adds a new sudoku to the creation history. The size of the history buffer is
	 * adjusted accordingly. New sudokus are always inserted at the start of the
	 * list and deleted from the end of the list, effectively turning the list in a
	 * queue (the performance overhead can be ignored here).
	 * 
	 * @param sudoku
	 */
	private void addSudokuToHistory(Sudoku2 sudoku) {
		Options.getInstance().addSudokuToHistory(sudoku);
	}

	/**
	 * Old GuiStates remain in memory as long as they are not overwritten. Since
	 * that can consume quite a lot of memory, they should be nulled out before
	 * clearing the list.
	 */
	private void clearSavePoints() {
		
		for (int i = 0; i < savePoints.size(); i++) {
			savePoints.set(i, null);
		}
		
		savePoints.clear();
	}

	/**
	 * Should be called only from {@link CellZoomPanel}.
	 * 
	 * @param colorNumber
	 * @param isCell
	 */
	public void setColoring(Color color, boolean isCell) {
		
		//sudokuPanel.setColorCells(isCell);
		if (isCell) {
			cellZoomPanel.setColorCells(true);
		} else {
			cellZoomPanel.setColorCandidates(true);
		}
		
		coloringPanelClicked(color);
		check();
		fixFocus();
	}

	public void coloringPanelClicked(Color color) {
		
		if (color == null) {
			
			statusPanelColorResult.setBackground(Options.getInstance().getDefaultCellColor());
			sudokuPanel.setActiveColor(null);
			/*
			if (colorNumber == -2) {
				sudokuPanel.clearColoring();
				repaint();
			}*/
			
		} else {
			
			statusPanelColorResult.setBackground(color);
			sudokuPanel.setActiveColor(color);
		}
	}

	private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {
		
		if (evt.getButton() == MouseEvent.BUTTON1) {
			switch (tabPane.getSelectedIndex()) {
			case 0:
				summaryMenuItem.setSelected(true);
				break;
			case 1:
				solutionMenuItem.setSelected(true);
				break;
			case 2:
				allStepsMenuItem.setSelected(true);
				break;
			case 3:
				cellZoomMenuItem.setSelected(true);
				break;
			}
		}
	}

	private void saveWindowStateInOptions() {
		
		// save the complete window state
		Options o = Options.getInstance();
		o.setInitialXPos(getX());
		o.setInitialYPos(getY());
		o.setInitialHeight(getHeight());
		o.setInitialWidth(getWidth());
		
		// the horizontal divider position must not be saved if the panel is not visible!
		if (o.isShowHintPanel()) {
			o.setInitialHorzDividerLoc(outerSplitPane.getDividerLocation());
		}
		
		// sudoku only
		o.setInitialDisplayMode(0);
		if (summaryMenuItem.isSelected()) {
			o.setInitialDisplayMode(1);
		}
		
		if (solutionMenuItem.isSelected()) {
			o.setInitialDisplayMode(2);
		}
		
		if (allStepsMenuItem.isSelected()) {
			o.setInitialDisplayMode(3);
		}
		
		if (cellZoomMenuItem.isSelected()) {
			o.setInitialDisplayMode(4);
		}
		
		o.setInitialVertDividerLoc(-1);
		if (o.getInitialDisplayMode() != 0) {
			splitPanel.getDividerLocation();
		}
	}

	private void writeOptionsWithWindowState(String fileName) throws FileNotFoundException {
		
		// save window state
		saveWindowStateInOptions();
		if (fileName == null) {
			Options.getInstance().writeOptions();
		} else {
			Options.getInstance().writeOptions(fileName);
		}
	}

	private void setWindowLayout(boolean reset) {
		
		Options o = Options.getInstance();

		if (reset) {
			o.setInitialDisplayMode(Options.INITIAL_DISP_MODE);
			o.setInitialHeight(Options.INITIAL_HEIGHT);
			o.setInitialHorzDividerLoc(Options.INITIAL_HORZ_DIVIDER_LOC);
			o.setInitialVertDividerLoc(Options.INITIAL_VERT_DIVIDER_LOC);
			o.setInitialWidth(Options.INITIAL_WIDTH);
			o.setShowHintPanel(Options.INITIAL_SHOW_HINT_PANEL);
			o.setShowToolBar(Options.INITIAL_SHOW_TOOLBAR);
		}

		jToolBar1.setVisible(o.isShowToolBar());
		hintPanel.setVisible(o.isShowHintPanel());

		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension screenSize = t.getScreenSize();

		int width = o.getInitialWidth();
		int height = o.getInitialHeight();
		int horzDivLoc = o.getInitialHorzDividerLoc();

		if (screenSize.height - 45 < height) {
			height = screenSize.height - 45;
		}
		
		if (horzDivLoc > height - 204) {
			horzDivLoc = height - 204;
			// can be used during program run, so has to be saved here
			Options.getInstance().setInitialHorzDividerLoc(horzDivLoc);
		}
		
		if (screenSize.width - 20 < width) {
			width = screenSize.width - 20;
		}
		
		setSize(width, height);
		switch (o.getInitialDisplayMode()) {
		case 0:
			splitPanel.setRight(null);
			sudokuOnlyMenuItem.setSelected(true);
			break;
		case 1:
			setSplitPane(summaryPanel);
			summaryMenuItem.setSelected(true);
			break;
		case 2:
			setSplitPane(solutionPanel);
			solutionMenuItem.setSelected(true);
			break;
		case 3:
			allStepsPanel.setSudoku(sudokuPanel.getSudoku());
			setSplitPane(allStepsPanel);
			allStepsMenuItem.setSelected(true);
			break;
		case 4:
			setSplitPane(cellZoomPanel);
			cellZoomMenuItem.setSelected(true);
		}
		if (o.getInitialVertDividerLoc() != -1) {
			splitPanel.setDividerLocation(o.getInitialVertDividerLoc());
		}

		outerSplitPane.setDividerLocation(horzDivLoc);

		// doesn't work at reset sometimes -> adjust in PropertyChangeListener
		// doesn't work when going back from fullscreen mode either

		outerSplitPaneInitialized = false;
		resetHDivLocLoc = horzDivLoc;
		resetHDivLocTicks = System.currentTimeMillis();
		resetHDivLoc = true;
	}

	private void resetResultPanels() {
		summaryPanel.initialize(null);
		solutionPanel.initialize(null);
		allStepsPanel.resetPanel();
	}

	private void initializeResultPanels() {
		summaryPanel.initialize(sudokuPanel.getSolver());
		solutionPanel.initialize(sudokuPanel.getSolver().getSteps());
		allStepsPanel.resetPanel();
	}

	private void setSplitPane(JPanel panel) {
		
		if (!splitPanel.hasRight()) {
			splitPanel.setRight(tabPane);
		}
		
		tabPane.setSelectedComponent(panel);
	}

	private void setPlay(boolean isPlaying) {
		
		gameMode = !isPlaying;
		
		if (isPlaying) {
			if (oldShowDeviationsValid) {
				showDeviationsMenuItem.setSelected(oldShowDeviations);
				oldShowDeviationsValid = false;
			}
		} else {
			oldShowDeviations = showDeviationsMenuItem.isSelected();
			oldShowDeviationsValid = true;
			showDeviationsMenuItem.setSelected(false);
		}
		
		showDeviationsMenuItemActionPerformed(null);

		vagueHintMenuItem.setEnabled(isPlaying);
		mediumHintMenuItem.setEnabled(isPlaying);
		solutionStepMenuItem.setEnabled(isPlaying);
		setAllSinglesMenuItem.setEnabled(isPlaying);
		showDeviationsMenuItem.setEnabled(isPlaying);
		showColorKuMenuItem.setEnabled(isPlaying);

		playGameMenuItem.setEnabled(!isPlaying);
		editGivensMenuItem.setEnabled(isPlaying);
	}

	public void setSolutionStep(SolutionStep step, boolean setInSudokuPanel) {
		
		if (setInSudokuPanel) {
			if (step == null) {
				sudokuPanel.abortStep();
			} else {
				sudokuPanel.setStep(step);
			}
		}
		
		if (step == null) {
			
			hinweisTextArea.setText("");
			hinweisAbbrechenButton.setEnabled(false);
			hinweisAusfuehrenButton.setEnabled(false);
			
			if (executeStepToggleButton != null) {
				executeStepToggleButton.setEnabled(false);
			}
			
			if (abortStepToggleButton != null) {
				abortStepToggleButton.setEnabled(false);
			}
			
		} else {
			
			hinweisTextArea.setText(step.toString());
			hinweisTextArea.setCaretPosition(0);
			hinweisAbbrechenButton.setEnabled(true);
			hinweisAusfuehrenButton.setEnabled(true);
			getRootPane().setDefaultButton(hinweisAusfuehrenButton);
			
			if (executeStepToggleButton != null) {
				executeStepToggleButton.setEnabled(true);
			}
			
			if (abortStepToggleButton != null) {
				abortStepToggleButton.setEnabled(true);
			}
		}
		
		fixFocus();
	}

	/**
	 * Copy the current sudoku to the clipboard. If <code>simpleSudoku</code> is set
	 * to <code>true</code>, the givensm the currently set cells and a PM grid are
	 * copied.
	 * 
	 * @param mode
	 * @param simpleSudoku
	 */
	private void copyToClipboard(ClipboardMode mode, boolean simpleSudoku) {
		
		String clipStr = "";
		
		if (simpleSudoku) {
			String dummy = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
			clipStr = SudokuUtil.getSSFormatted(dummy);
			clipStr += SudokuUtil.NEW_LINE;
			clipStr += SudokuUtil.NEW_LINE;
			dummy = sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY);
			clipStr += SudokuUtil.getSSFormatted(dummy);
			clipStr += SudokuUtil.NEW_LINE;
			clipStr += SudokuUtil.NEW_LINE;
			dummy = sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
			clipStr += SudokuUtil.getSSPMGrid(dummy);
		} else {
			clipStr = sudokuPanel.getSudokuString(mode);
		}
		
		try {
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection content = new StringSelection(clipStr);
			clip.setContents(content, null);
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error writing to clipboard", ex);
		}
		
		fixFocus();
	}

	private void setToggleButton(JToggleButton button, boolean ctrlPressed) {
		
		if (button == null) {
			sudokuPanel.resetShowHintCellValues();
		} else {
			
			int index = 0;
			for (index = 0; index < toggleButtons.length; index++) {
				if (toggleButtons[index] == button) {
					break;
				}
			}
			
			if (index == Sudoku2.UNITS) {
				sudokuPanel.setShowHintCellValue(index + 1);
			} else {
				boolean isActive = sudokuPanel.getShowHintCellValues()[index + 1];
				if (ctrlPressed) {
					sudokuPanel.getShowHintCellValues()[index + 1] = !isActive;
					sudokuPanel.getShowHintCellValues()[10] = false;
				} else {
					if (isActive) {
						sudokuPanel.resetShowHintCellValues();
					} else {
						sudokuPanel.setShowHintCellValue(index + 1);
					}
				}
			}
			sudokuPanel.checkIsShowInvalidOrPossibleCells();
		}
		
		check();
		sudokuPanel.repaint();
		fixFocus();
	}

	/**
	 * Save puzzles/configurations.
	 * 
	 * @param puzzle
	 */
	private void saveToFile(boolean puzzle) {
		
		JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
		chooser.setAcceptAllFileFilterUsed(false);
		MyFileFilter[] filters = puzzleFileSaveFilters;
		if (!puzzle) {
			filters = configFileFilters;
		}
		
		for (int i = 0; i < filters.length; i++) {
			chooser.addChoosableFileFilter(filters[i]);
		}
		
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String path = chooser.getSelectedFile().getPath();
				path = path.substring(0, path.lastIndexOf(File.separatorChar));
				Options.getInstance().setDefaultFileDir(path);
				MyFileFilter actFilter = (MyFileFilter) chooser.getFileFilter();
				int filterType = actFilter.getType();
				path = chooser.getSelectedFile().getAbsolutePath();
				if (!puzzle) {
					// Options
					if (!path.endsWith("." + configFileExt)) {
						path += "." + configFileExt;
					}
				} else {
					if (filterType == 1) {
						if (!path.endsWith("." + solutionFileExt)) {
							path += "." + solutionFileExt;
						}
					} else if (filterType == 9) {
						if (!path.endsWith("." + ssFileExt)) {
							path += "." + ssFileExt;
						}
					} else {
						if (!path.endsWith("." + textFileExt)) {
							path += "." + textFileExt;
						}
					}
				}
				
				File checkFile = new File(path);
				if (checkFile.exists()) {
					// Override warning!
					MessageFormat msgf = new MessageFormat("");
					Object[] args = new Object[] { checkFile.getName() };
					msgf.applyPattern(
							java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.file_exists"));
					String warning = msgf.format(args);
					String title = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint");
					if (JOptionPane.showConfirmDialog(null, warning, title,
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
				}

				saveToFile(puzzle, path, filterType);
				
			} catch (Exception ex2) {
				
				JOptionPane.showMessageDialog(
					this, 
					ex2.toString(),
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
					JOptionPane.ERROR_MESSAGE
				);
				
				sudokuFileName = null;
			}
			
			setTitleWithFile();
		}
	}

	/**
	 * Actually writes configurations and sudoku files. If <code>puzzle</code> is
	 * <code>false</code>, a configuration is written. <code>filterType</code> is
	 * one of the file types defined by {@link MyFileFilter}. If the method is
	 * called via the "Save puzzle" menu item, <code>filterType</code> can be 8
	 * (generic text file). In this case a PM grid is written.
	 * 
	 * @param puzzle
	 * @param path
	 * @param filterType
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveToFile(boolean puzzle, String path, int filterType) throws FileNotFoundException, IOException {
		
		sudokuFileName = path;
		
		if (!puzzle) {
			// Options
			writeOptionsWithWindowState(path);
		} else {

			String newLine = System.getProperty("line.separator");
			if (filterType == 1) {
				
				sudokuFileType = 1;
				ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(path));
				zOut.putNextEntry(new ZipEntry("SudokuData"));
				XMLEncoder out = new XMLEncoder(zOut);
				out.writeObject(sudokuPanel.getSudoku());
				out.writeObject(SudokuSolverFactory.getDefaultSolverInstance().getAnzSteps());
				out.writeObject(SudokuSolverFactory.getDefaultSolverInstance().getSteps());
				out.writeObject(solutionPanel.getTitels());
				out.writeObject(solutionPanel.getTabSteps());
				out.writeObject(savePoints);
				out.close();
				zOut.flush();
				zOut.close();
				
			} else if (filterType == 9) {
				
				sudokuFileType = 9;
				// SimpleSudoku format (see comment in loadFromFile())
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				String clues = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
				out.println(SudokuUtil.getSSFormatted(clues));
				out.println();
				// additionally set cells
				Sudoku2 tmpSudoku = sudokuPanel.getSudoku();
				for (int i = 0; i < Sudoku2.LENGTH; i++) {
					if (tmpSudoku.getValue(i) != 0 && !tmpSudoku.isFixed(i)) {
						out.printf("I%02d%d%n", i, tmpSudoku.getValue(i));
					}
				}
				
				// eliminated candidates
				for (int i = 0; i < Sudoku2.LENGTH; i++) {
					if (tmpSudoku.getValue(i) == 0) {
						for (int j = 1; j <= Sudoku2.UNITS; j++) {
							if (tmpSudoku.isValidValue(i, j) && !tmpSudoku.isCandidate(i, j)) {
								out.printf("E%02d%03d%n", i, j);
							}
						}
					}
				}
				
				out.close();
				
			} else {
				
				sudokuFileType = 8;
				if (filterType == 8) {
					// generic text file: can occur when save is executed
					filterType = 6;
				}
				
				BufferedWriter out = new BufferedWriter(new FileWriter(path));
				String line = "";
				switch (filterType) {
				case 2:
					line = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
					break;
				case 3:
					line = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY_FORMATTED);
					break;
				case 4:
					line = sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
					break;
				case 5:
					line = sudokuPanel.getSudokuString(ClipboardMode.PM_GRID_WITH_STEP);
					break;
				case 6:
					line = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY_FORMATTED);
					line += newLine;
					line += newLine;
					line += sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY_FORMATTED);
					line += newLine;
					line += newLine;
					line += sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
					break;
				case 7:
					line = sudokuPanel.getSudokuString(ClipboardMode.LIBRARY);
					break;
				}
				
				out.write(line);
				out.close();
			}
		}
	}

	/**
	 * Loads puzzles and/or configurations from files. loading either type resets
	 * the mode to "playing".
	 * 
	 * @param puzzle
	 */
	private void loadFromFile(boolean puzzle) {
		
		JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
		chooser.setAcceptAllFileFilterUsed(false);
		MyFileFilter[] filters = puzzleFileLoadFilters;
		
		if (!puzzle) {
			filters = configFileFilters;
		}
		
		for (int i = 0; i < filters.length; i++) {
			chooser.addChoosableFileFilter(filters[i]);
		}
		
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getPath();
			path = path.substring(0, path.lastIndexOf(File.separatorChar));
			Options.getInstance().setDefaultFileDir(path);
			path = chooser.getSelectedFile().getAbsolutePath();
			MyFileFilter filter = (MyFileFilter) chooser.getFileFilter();
			loadFromFile(path, filter.getType());
		}
	}

	public static void ShowWarningMSG(String title, String msg) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Verify if the import line contains valid characters.
	 * 
	 * @param importString
	 * @return
	 */
	public static boolean ValidateImportLine(String line) {

		ResourceBundle bundle = ResourceBundle.getBundle("intl/MainFrame");
		String title = bundle.getString("MainFrame.ValidateImportLine.title");
		String error1 = bundle.getString("MainFrame.ValidateImportLine.error1");
		String error2a = bundle.getString("MainFrame.ValidateImportLine.error2a");
		String error2b = bundle.getString("MainFrame.ValidateImportLine.error2b");
		
		if (line.length() != Sudoku2.LENGTH) {
			ShowWarningMSG(title, error1);
			return false;
		}

		for (int i = 0; i < line.length(); i++) {
			
			char c = line.charAt(i);
			boolean isDigit = c >= '0' && c <= '9';
			boolean isEmpty = c == '.';
			
			if (!isDigit && !isEmpty) {
				ShowWarningMSG(title, error2a + c + error2b);
				return false;
			}
		}

		return true;
	}

	public boolean loadFromImportLine(String importString) {

		String line = importString.trim();
		if (ValidateImportLine(line)) {
			setPuzzle(line);
			clearSavePoints();
			return true;
		}

		return false;
	}

	private void showPuzzleSolution() {

		boolean isStatusValid = sudokuPanel.getSudoku().getStatus() == SudokuStatus.VALID;
		boolean hasMultipleSolutions = sudokuPanel.getSudoku().getStatus() == SudokuStatus.MULTIPLE_SOLUTIONS;
		
		if (hasMultipleSolutions) {			
			String msg = java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solve_error");
			JOptionPane.showMessageDialog(this, msg);			
			return;
		}
		
		if (!isStatusValid || sudokuPanel.getSudoku().isSolved()) {
			return;
		}

		sudokuPanel.saveState();
		Sudoku2 sudoku = sudokuPanel.getSudoku();

		for (int c = 0; c < Sudoku2.UNITS; c++) {
			for (int r = 0; r < Sudoku2.UNITS; r++) {
				int value = sudoku.getSolution(r, c);
				sudoku.setCell(r, c, value);
			}
		}

		repaint();
	}
	
	public void showSolutionCount() {
		
		ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/MainFrame");
		String msg1 = bundle.getString("MainFrame.solutionCountMenuItem.msg1");
		String msg2 = bundle.getString("MainFrame.solutionCountMenuItem.msg2");
		String msg3 = bundle.getString("MainFrame.solutionCountMenuItem.msg3");
		String msg4 = bundle.getString("MainFrame.solutionCountMenuItem.msg4");
		String msg5 = bundle.getString("MainFrame.solutionCountMenuItem.msg5");
		
		Sudoku2 sudoku = sudokuPanel.getSudoku().clone();
		SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
		int solutionCount = generator.getNumberOfSolutions(sudoku, 999);
		
		if (solutionCount == 0) {
			JOptionPane.showMessageDialog(
				this,
				msg3
			);
		} else if (solutionCount == 1) {
			JOptionPane.showMessageDialog(
				this,
				msg1 + " " + solutionCount + " " + msg4
			);
		} else if (solutionCount <= 999) {
			JOptionPane.showMessageDialog(
				this,
				msg1 + " " + solutionCount + " " + msg5
			);
		} else {
			JOptionPane.showMessageDialog(
				this,
				msg2 + " " + solutionCount + " " + msg5
			);
		}
	}

	/**
	 * Loads a file
	 * 
	 * @param path
	 * @param fileType 0 .. options, 1 .. sudoku from hsol, 8 .. sudoku from text
	 *                 file
	 */
	@SuppressWarnings({ "unchecked" })
	private void loadFromFile(String path, int fileType) {

		try {
			sudokuFileName = path;
			sudokuFileType = fileType;
			if (fileType == 0) {
				// Options
				Options.readOptions(path);
				BackgroundGeneratorThread.getInstance().resetAll();
				sudokuFileName = null;
			} else if (fileType == 1) {
				// Puzzle
				ZipInputStream zIn = new ZipInputStream(new FileInputStream(path));
				zIn.getNextEntry();
				XMLDecoder in = new XMLDecoder(zIn);
				GuiState state = new GuiState(sudokuPanel, sudokuPanel.getSolver(), solutionPanel);
				// could be old file -> contains instance of Sudoku and not Sudoku2!
				Object sudokuTemp = in.readObject();
				if (sudokuTemp instanceof Sudoku2) {
					// ok: new version!
					state.setSudoku((Sudoku2) sudokuTemp);
				} else {
					// old version: convert it!
					Sudoku dummy = (Sudoku) sudokuTemp;
					String sudokuTempLib = dummy.getSudoku(ClipboardMode.LIBRARY, null);

					state.setSudoku(new Sudoku2());
					state.getSudoku().setSudoku(sudokuTempLib, false);
					state.getSudoku().setInitialState(dummy.getInitialState());
					// contains another instance of Sudoku (solvedSudoku)
					// that is not needed anymore
					sudokuTemp = in.readObject();
				}
				
				state.setAnzSteps((int[]) in.readObject());
				state.setSteps((List<SolutionStep>) in.readObject());
				state.setTitels((List<String>) in.readObject());
				state.setTabSteps((List<List<SolutionStep>>) in.readObject());
				state.resetAnzSteps();
				
				try {
					savePoints = (List<GuiState>) in.readObject();
					for (int i = 0; i < savePoints.size(); i++) {
						// internal fields must be set!
						savePoints.get(i).initialize(sudokuPanel, SudokuSolverFactory.getDefaultSolverInstance(),
								solutionPanel);
					}
				} catch (Exception ex) {
					// when an older puzzle file is loaded, savepoints are not in the file or the
					// format is incompatible
					clearSavePoints();
				}
				
				in.close();
				setState(state);
				setMode(GameMode.PLAYING, true);
				
			} else if (fileType == 8) {
				
				// load from text file
				BufferedReader in = new BufferedReader(new FileReader(path));
				StringBuilder tmp = new StringBuilder();
				String line = null;
				
				while ((line = in.readLine()) != null) {
					tmp.append(line);
					tmp.append("\r\n");
				}
				
				in.close();
				setPuzzle(tmp.toString());
				clearSavePoints();
				
			} else if (fileType == 9) {
				
				// SimpleSudoku format: The givens followed by set cells and eliminated
				// candidates
				// example:
//                     *-----------*
//                     |38.|...|5.6|
//                     |...|9..|...|
//                     |...|.4.|89.|
//                     |---+---+---|
//                     |.4.|6..|.3.|
//                     |9..|7.1|..4|
//                     |.7.|..8|.1.|
//                     |---+---+---|
//                     |.13|.6.|...|
//                     |...|..4|...|
//                     |2.5|...|.83|
//                     *-----------*
//
//                    I388
//                    I074
//                    I029
//                    I153
//                    I358
//                    I751
//                    E78007
//                    E78009
//                    E73009
				// Innc: nn - index of the cell, c - candidate
				// Ennccc: nn - index of the cell, ccc - candidate
				BufferedReader in = new BufferedReader(new FileReader(path));
				StringBuilder tmp = new StringBuilder();
				String line = null;
				
				while ((line = in.readLine()) != null) {
					
					if (line.trim().isEmpty()) {
						// all the givens are read -> abort reading
						break;
					}
					
					tmp.append(line);
					tmp.append("\r\n");
				}
				
				// first set the givens
				Sudoku2 tmpSudoku = new Sudoku2();
				tmpSudoku.setSudoku(tmp.toString());
				
				// now read set cells and eliminations
				while ((line = in.readLine()) != null) {
					
					if (line.trim().isEmpty()) {
						continue;
					}
					
					char recordType = line.charAt(0);
					if (recordType == 'I') {
						int index = Integer.parseInt(line.substring(1, 3));
						int candidate = Character.digit(line.charAt(3), 10);
						tmpSudoku.setCell(index, candidate);
					} else if (recordType == 'E') {
						int index = Integer.parseInt(line.substring(1, 3));
						int candidate = Integer.parseInt(line.substring(3, 6));
						tmpSudoku.delCandidate(index, candidate);
					}
				}
				
				in.close();
				// everything read and decoded -> set the sudoku
				setPuzzle(tmpSudoku.getSudoku(ClipboardMode.LIBRARY));
				clearSavePoints();
				
			} else {
				
				formatter.applyPattern(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_filename"));
				String msg = formatter.format(new Object[] { path });
				JOptionPane.showMessageDialog(
					this,
					msg,
					java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
					JOptionPane.ERROR_MESSAGE
				);
				
				sudokuFileName = null;
			}
		} catch (Exception ex2) {
			JOptionPane.showMessageDialog(
				this,
				ex2.toString(),
				java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
				JOptionPane.ERROR_MESSAGE
			);
			ex2.printStackTrace();
			sudokuFileName = null;
		}
		
		setTitleWithFile();
	}

	/**
	 * @param args the command line arguments
	 */
	/*
	public static void main(String args[]) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Error setting LaF", ex);
		}
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame(null).setVisible(true);
			}
		});
	}*/

	private void setTitleWithFile() {
		
		savePuzzleMenuItem.setEnabled(sudokuFileName != null);
		if (sudokuFileName == null) {
			setTitle(VERSION);
		} else {
			int index = sudokuFileName.lastIndexOf('\\') + 1;
			int index2 = sudokuFileName.lastIndexOf('/') + 1;
			if (index2 > index) {
				index = index2;
			}
			String fileName = sudokuFileName.substring(index);
			setTitle(VERSION + "  (" + fileName + ")");
		}
	}

	private void setLevelFromMenu() {
		
		int selected = 0;
		for (int i = 0; i < levelMenuItems.length; i++) {
			if (levelMenuItems[i].isSelected()) {
				selected = i + 1;
				break;
			}
		}

		Options.getInstance().setActLevel(Options.getInstance().getDifficultyLevels()[selected].getOrdinal());
		BackgroundGeneratorThread.getInstance().setNewLevel(Options.getInstance().getActLevel());
		check();
		fixFocus();
	}

	public void stepAusfuehren() {
		hinweisAusfuehrenButtonActionPerformed(null);
	}

	public final void fixFocus() {
		sudokuPanel.requestFocusInWindow();
	}

	public SudokuPanel getSudokuPanel() {
		return sudokuPanel;
	}

	public SolutionPanel getSolutionPanel() {
		return solutionPanel;
	}

	public final void check() {
		
		if (sudokuPanel != null) {
			
			undoMenuItem.setEnabled(sudokuPanel.undoPossible());
			undoToolButton.setEnabled(sudokuPanel.undoPossible());
			redoMenuItem.setEnabled(sudokuPanel.redoPossible());
			redoToolButton.setEnabled(sudokuPanel.redoPossible());
			showCandidatesMenuItem.setSelected(sudokuPanel.isShowCandidates());
			showWrongValuesMenuItem.setSelected(sudokuPanel.isShowWrongValues());
			showDeviationsMenuItem.setSelected(sudokuPanel.isShowDeviations());
			showColorKuMenuItem.setSelected(Options.getInstance().isShowColorKuAct());
			prepareToggleButtonIcons(Options.getInstance().isShowColorKuAct());
			
			// either all ToggleButtons are set or none is
			if (toggleButtons[0] != null) {
				
				boolean[] remainingCandidates = sudokuPanel.getRemainingCandidates();
				for (int i = 0; i < remainingCandidates.length; i++) {
					
					JToggleButton button = toggleButtons[i];
					
					// change the standard icons
					if (remainingCandidates[i]) {

						if (button.getIcon() != toggleButtonIcons[i]) {
							button.setIcon(toggleButtonIcons[i]);
						}

					} else {

						if (button.getIcon() != emptyToggleButtonIcons[i]) {
							button.setIcon(emptyToggleButtonIcons[i]);
						}
					}
				}
				
				for (int i = 0; i < toggleButtons.length; i++) {
					if (toggleButtons[i].isEnabled()) {
						if (sudokuPanel.getShowHintCellValues()[i + 1]) {
							toggleButtons[i].setSelected(true);
						} else {
							toggleButtons[i].setSelected(false);
						}
					}
				}
			}
			
			redGreenToggleButton.setSelected(sudokuPanel.isInvalidCells());
			Sudoku2 sudoku = sudokuPanel.getSudoku();
			if (sudoku != null) {
				
				DifficultyLevel tmpLevel = sudoku.getLevel();
				if (tmpLevel != null) {
					statusLabelLevel.setText(StepConfig.getLevelName(tmpLevel) + " (" + sudoku.getScore() + ")");
				} else {
					statusLabelLevel.setText("-");
				}
				
				setProgressLabel();
				
			} else {
				// no puzzle loaded
				statusLabelLevel.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelLevel.text"));
			}
			
			if (cellZoomPanel.isColoringCells()) {
				colorCellsMenuItem.setSelected(true);
				statusLabelCellCandidate.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelCellCandidate.text.cell"));
			} else if (cellZoomPanel.isColoringCandidates()) {
				colorCandidatesMenuItem.setSelected(true);
				statusLabelCellCandidate.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelCellCandidate.text.candidate"));
			} else if (cellZoomPanel.isDefaultMouse()) {
				statusLabelCellCandidate.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelCellCandidate.text.default"));
			}
			
			fixFocus();
		}
		
		// adjust mode menus and labels
		// Options.actLevel is always valid
		if (Options.getInstance().getActLevel() != -1) {
			if (Options.getInstance().getGameMode() != GameMode.PLAYING) {
				// we cant have a level that is easier than the easiest
				// selected training/practising puzzle -> we could never
				// find a new sudoku for that
				int tmpLevel = Options.getInstance().getActLevel();
				for (StepConfig act : Options.getInstance().getOrgSolverSteps()) {
					if (act.isEnabledTraining() && act.getLevel() > tmpLevel) {
						tmpLevel = act.getLevel();
					}
				}
				
				if (tmpLevel != Options.getInstance().getActLevel()) {
//                    level = Options.getInstance().getDifficultyLevel(tmpLevel);
					Options.getInstance().setActLevel(tmpLevel);
				}
			}
			
			int ord = Options.getInstance().getActLevel() - 1;
			if (levelMenuItems[ord] != null && levelComboBox.getItemCount() > ord) {
				levelMenuItems[ord].setSelected(true);
				levelComboBox.setSelectedIndex(ord);
			}
			
			int mOrdinal = Options.getInstance().getGameMode().ordinal();
			if (modeMenuItems != null && modeMenuItems[mOrdinal] != null) {
				
				modeMenuItems[mOrdinal].setSelected(true);
				
				String labelStr = modeMenuItems[mOrdinal].getText();
				if (labelStr.endsWith("...")) {
					labelStr = labelStr.substring(0, labelStr.length() - 3);
				}
				
				if (Options.getInstance().getGameMode() != GameMode.PLAYING) {
					labelStr += " (" + Options.getInstance().getTrainingStepsString(true) + ")";
				}
				
				statusLabelModus.setText(labelStr);
			}
			
			showHintButtonsCheckBoxMenuItem.setSelected(Options.getInstance().isShowHintButtonsInToolbar());
		}
		
		statusLinePanel.invalidate();
	}

	private boolean isStringFlavorInClipboard() {
		
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clip.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			return true;
		}
		
		return false;
	}

	private void adjustPasteMenuItem() {
		
		try {
			
			if (Main.OS_NAME.contains("mac")) {
				pasteMenuItem.setEnabled(true);
			} else {
				if (isStringFlavorInClipboard()) {
					pasteMenuItem.setEnabled(true);
				} else {
					pasteMenuItem.setEnabled(false);
				}
			}
			
		} catch (IllegalStateException ex) {
			clipTimer.start();
		}
	}

	@Override
	public void flavorsChanged(FlavorEvent e) {
		adjustPasteMenuItem();
	}

	private Image getIcon() {
		URL url = getClass().getResource("/img/hodoku02-32.png");
		return getToolkit().getImage(url);
	}

	public List<GuiState> getSavePoints() {
		return savePoints;
	}

	public void abortStep() {
		
		sudokuPanel.abortStep();
		hinweisTextArea.setText("");
		hinweisAbbrechenButton.setEnabled(false);
		hinweisAusfuehrenButton.setEnabled(false);
		
		if (executeStepToggleButton != null) {
			executeStepToggleButton.setEnabled(false);
		}
		
		if (abortStepToggleButton != null) {
			abortStepToggleButton.setEnabled(false);
		}
		
		fixFocus();
	}

	private void createProgressLabelImages() {
		
		try {
			
			progressImages[0] = new ImageIcon(getClass().getResource("/img/invalid20.png"));
			BufferedImage overlayImage = ImageIO.read(getClass().getResource("/img/ce1-20.png"));
			
			for (int i = 1; i < progressImages.length; i++) {
				BufferedImage act = new BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D gAct = (Graphics2D) act.getGraphics();
				gAct.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				gAct.setColor(Options.getInstance().getDifficultyLevels()[i].getBackgroundColor());
				gAct.fillOval(2, 2, 16, 16);
				gAct.drawImage(overlayImage, 0, 0, null);
				progressImages[i] = new ImageIcon(act);
			}
			
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error creating progressLabel images", ex);
		}
	}

	public void setProgressLabel() {
		
		if (sudokuPanel == null) {
			// do nothing
			return;
		}
		
		Sudoku2 sudoku = sudokuPanel.getSudoku();
		if (sudoku.getStatus() != SudokuStatus.VALID) {
			progressLabel.setIcon(progressImages[0]);
			progressLabel.setText("-");
			return;
		}
		
		// ok valid sudoku, currentLevel and currentScore must have been set
		// not necessarily!
		if (getCurrentLevel() != null) {
			
			progressLabel.setIcon(progressImages[getCurrentLevel().getOrdinal()]);
			double proc = getCurrentScore();
			int intProc = (int) (proc / sudoku.getScore() * 100);
			
			if (intProc > 100) {
				intProc = 100;
			}
			
			if (intProc < 1) {
				intProc = 1;
			}
			
			if (getCurrentScore() == 0) {
				intProc = 0;
			}
			
			progressLabel.setText(intProc + "%");
		}
	}

	/**
	 * @return the currentLevel
	 */
	public synchronized DifficultyLevel getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * @param currentLevel the currentLevel to set
	 */
	public synchronized void setCurrentLevel(DifficultyLevel currentLevel) {
		this.currentLevel = currentLevel;
	}

	/**
	 * @return the currentScore
	 */
	public synchronized int getCurrentScore() {
		return currentScore;
	}

	/**
	 * @param currentScore the currentScore to set
	 */
	public synchronized void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	/**
	 * Handles the display of the hint buttons in the toolbar. If they are made
	 * visible the first time, they have to be created.
	 * 
	 */
	private void setShowHintButtonsInToolbar() {
		
		if (vageHintToggleButton == null) {
			
			// create the buttons
			hintSeperator = new javax.swing.JSeparator();
			hintSeperator.setOrientation(javax.swing.SwingConstants.VERTICAL);
			hintSeperator.setMaximumSize(new java.awt.Dimension(5, 32767));
			hintSeperator.setVisible(false);
			jToolBar1.add(hintSeperator);

			vageHintToggleButton = new javax.swing.JButton();
			vageHintToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/vageHint.png")));
			vageHintToggleButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					hintToggleButtonActionPerformed(true);
				}
			});
			vageHintToggleButton.setToolTipText(java.util.ResourceBundle.getBundle("intl/MainFrame")
					.getString("MainFrame.vageHintToolButton.toolTipText"));
			vageHintToggleButton.setVisible(false);
			jToolBar1.add(vageHintToggleButton);

			concreteHintToggleButton = new javax.swing.JButton();
			concreteHintToggleButton
					.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/concreteHint.png")));
			concreteHintToggleButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					hintToggleButtonActionPerformed(false);
				}
			});
			concreteHintToggleButton.setVisible(false);
			concreteHintToggleButton.setToolTipText(java.util.ResourceBundle.getBundle("intl/MainFrame")
					.getString("MainFrame.concreteHintToolButton.toolTipText"));
			jToolBar1.add(concreteHintToggleButton);

			showNextStepToggleButton = new javax.swing.JButton();
			showNextStepToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nextHint.png")));
			showNextStepToggleButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					loesungsSchrittMenuItemActionPerformed(null);
				}
			});
			showNextStepToggleButton.setToolTipText(java.util.ResourceBundle.getBundle("intl/MainFrame")
					.getString("MainFrame.neuerHinweisButton.toolTipText"));
			showNextStepToggleButton.setVisible(false);
			jToolBar1.add(showNextStepToggleButton);

			executeStepToggleButton = new javax.swing.JButton();
			executeStepToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/executeHint.png")));
			executeStepToggleButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					hinweisAusfuehrenButtonActionPerformed(null);
				}
			});
			executeStepToggleButton.setToolTipText(java.util.ResourceBundle.getBundle("intl/MainFrame")
					.getString("MainFrame.hinweisAusfuehrenButton.toolTipText"));
			executeStepToggleButton.setVisible(false);
			jToolBar1.add(executeStepToggleButton);

			abortStepToggleButton = new javax.swing.JButton();
			abortStepToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/abortHint.png")));
			abortStepToggleButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					hinweisAbbrechenButtonActionPerformed(null);
				}
			});
			abortStepToggleButton.setToolTipText(java.util.ResourceBundle.getBundle("intl/MainFrame")
					.getString("MainFrame.hinweisAbbrechenButton.toolTipText"));
			abortStepToggleButton.setVisible(false);
			jToolBar1.add(abortStepToggleButton);
		}
		if (Options.getInstance().isShowHintButtonsInToolbar()) {
			
			hintSeperator.setVisible(true);
			vageHintToggleButton.setVisible(true);
			concreteHintToggleButton.setVisible(true);
			showNextStepToggleButton.setVisible(true);
			executeStepToggleButton.setVisible(true);
			abortStepToggleButton.setVisible(true);
			executeStepToggleButton.setEnabled(hinweisAusfuehrenButton.isEnabled());
			abortStepToggleButton.setEnabled(hinweisAusfuehrenButton.isEnabled());
			
		} else {
			
			hintSeperator.setVisible(false);
			vageHintToggleButton.setVisible(false);
			concreteHintToggleButton.setVisible(false);
			showNextStepToggleButton.setVisible(false);
			executeStepToggleButton.setVisible(false);
			abortStepToggleButton.setVisible(false);
		}
		
		check();
		fixFocus();
	}

	/**
	 * Action event for hint buttons
	 * 
	 * @param isVage
	 */
	private void hintToggleButtonActionPerformed(boolean isVage) {
		if (isVage) {
			vageHintMenuItemActionPerformed(null);
		} else {
			mediumHintMenuItemActionPerformed(null);
		}
	}

	public boolean isInputMode() {
		return gameMode;
	}

	class MyFileFilter extends FileFilter {

		private int type;

		MyFileFilter(int type) {
			this.type = type;
		}

		@Override
		public boolean accept(File f) {
			
			if (f.isDirectory()) {
				return true;
			}
			
			String[] parts = f.getName().split("\\.");
			if (parts.length > 1) {
				String ext = parts[parts.length - 1];
				switch (type) {
				case 0:
					// Configuration Files
					if (ext.equalsIgnoreCase(java.util.ResourceBundle.getBundle("intl/MainFrame")
							.getString("MainFrame.config_file_ext"))) {
						return true;
					}
					break;
				case 1:
					// Puzzles with Solutions
					if (ext.equalsIgnoreCase(java.util.ResourceBundle.getBundle("intl/MainFrame")
							.getString("MainFrame.solution_file_ext"))) {
						return true;
					}
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					// Any kind of text file
					if (ext.equalsIgnoreCase(java.util.ResourceBundle.getBundle("intl/MainFrame")
							.getString("MainFrame.text_file_ext"))) {
						return true;
					}
					break;
				case 9:
					// SimpleSudoku files
					if (ext.equalsIgnoreCase(
							java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.ss_file_ext"))) {
						return true;
					}
					break;
				default:
					return false;
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			switch (type) {
			case 0:
				return java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.config_file_descr");
			case 1:
				return java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr");
			case 2:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_gal");
			case 3:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_gf");
			case 4:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_pm");
			case 5:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_pms");
			case 6:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_pmg");
			case 7:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_l");
			case 8:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_text");
			case 9:
				return java.util.ResourceBundle.getBundle("intl/MainFrame")
						.getString("MainFrame.solution_file_descr_ss");
			default:
				return java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unknown_file_type");
			}
		}

		public int getType() {
			return type;
		}
	}

	class MyCaretListener implements CaretListener {

		private boolean inUpdate = false;

		@Override
		public void caretUpdate(CaretEvent e) {
			
			if (inUpdate) {
				// no recursion!
				return;
			}
			// System.out.println("caretUpdate(): " + e.getDot() + "/" + e.getMark());
			// if everything is highlighted, don't interfere or it will be impossible
			// to copy the step to the clipboard
			// try to identify the line
			String text = hinweisTextArea.getText();
			if (e.getDot() == 0 && e.getMark() == text.length()) {
				// do nothing!
				return;
			}
			// System.out.println(text);
			int dot = e.getDot() > e.getMark() ? e.getDot() : e.getMark();
			int line = 0;
			int start = 0;
			int end = 0;
			int act = -1;
			
			while (act < dot) {
				
				act = text.indexOf('\n', act + 1);
				
				if (act == -1) {
					// ok, done
					end = text.length() - 1;
					break;
				} else if (act < dot) {
					start = act;
					line++;
				} else {
					end = act;
					break;
				}
			}
			
			if (end > 0) {
				
				inUpdate = true;
				
				if (line == 0) {
					sudokuPanel.setChainInStep(-1);
				} else {
					hinweisTextArea.setSelectionStart(start + 1);
					hinweisTextArea.setSelectionEnd(end);
					sudokuPanel.setChainInStep(line - 1);
				}
				
				inUpdate = false;
			}
		}
	}
}
