package origami;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.wb.swt.SWTResourceManager;

//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
//import swing2swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseMoveListener;
//import org.eclipse.swt.events.MouseTrackListener;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.layout.GridLayout;

public class MainWindow implements Observer {

	protected Shell shell;
	private Shell helpWindow;
	private MenuItem kawasakiDemo1;
	private MenuItem demo2_choice;
	private MenuItem demo3_choice;
	private MenuItem[] demos;
	private MenuItem prevItem;
	private PaintPanel topPaint;
	private PaintPanel paintPanel1, paintPanel2, paintPanel3;
	 StackLayout stackLayout;
	private Display display;
	private Composite topleft, topright, botright, botleft;
	private Button foldBtn;
	private Color white;
	private Label typeLbl;
	private Image i = new Image(Display.getCurrent(), 500, 500);		// idk if it has to be initialized
	private Label angleBoxChanging;
	
	private String filepath;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		
		Image icon = new Image(Display.getCurrent(), "appicon.jpg");
		
		shell.setImage(icon);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
//	    while (!helpWindow.isDisposed()) {			// cant open help window twice; tried to fix
//		       if (!display.readAndDispatch())
//		          display.sleep();
//		    }
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(763, 632);
		shell.setMinimumSize(438,480);
		shell.setText("Origami Folding Simulations");
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.horizontalSpacing = 10;
		gl_shell.verticalSpacing = 10;
		gl_shell.marginWidth = 10;
		gl_shell.marginHeight = 10;
		shell.setLayout(gl_shell);
		
		white = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);			//random placement for color		
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem file_barbtn = new MenuItem(menu, SWT.CASCADE);
		file_barbtn.setText("File");
		
		Menu menu_1 = new Menu(file_barbtn);
		file_barbtn.setMenu(menu_1);
		
		MenuItem saveas_file = new MenuItem(menu_1, SWT.NONE);
		saveas_file.setText("Save As...");
		
		MenuItem check_file = new MenuItem(menu_1, SWT.CHECK);
		check_file.setText("check this out");
		
		MenuItem edit_barbtn = new MenuItem(menu, SWT.CASCADE);
		edit_barbtn.setText("Edit");
		
		Menu menu_2 = new Menu(edit_barbtn);
		edit_barbtn.setMenu(menu_2);
		
		MenuItem mntmNewItem = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem.setText("Menu Item");
		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		MenuItem submenu_edit = new MenuItem(menu_2, SWT.CASCADE);
		submenu_edit.setText("Demo Type");
		
		Menu menu_3 = new Menu(submenu_edit);
		submenu_edit.setMenu(menu_3);			
				
		
		helpWindow = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		helpWindow.setSize(300, 200);
		helpWindow.setText("Help");
		
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 10;
		fillLayout.marginWidth = 10;
		helpWindow.setLayout(fillLayout);
		
		Label helpExplained = new Label(helpWindow, SWT.WRAP);
		helpExplained.setText("These are a series of simulations of local origami phenomena, inspired" 
				+ " by Erik Demaine's lectures. Created by Justin Wong as part of Hagerty High's "
				+ "Modeling and Simulation program. Last updated 4-27-2017.");
		helpExplained.setBounds(0, 0, SWT.FILL, SWT.FILL - 20);
		
		Button okHelp = new Button(helpWindow, SWT.PUSH);
		okHelp.setText("OK");
		okHelp.setBounds(0, 0, SWT.FILL, 20);
		
		Listener okListener = new Listener() {
			public void handleEvent(Event e) {
				if(e.widget == okHelp)
				{
					/*helpWindow.addListener(SWT.Close, new Listener() {
						
						public void handleEvent(Event event) {
							
					        int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					        MessageBox messageBox = new MessageBox(shell, style);
					        messageBox.setText("Information");
					        messageBox.setMessage("Close the shell?");
					        event.doit = messageBox.open() == SWT.YES;
					
					
					});*/
					helpWindow.dispose();
				}
					
			}
		};
		okHelp.addListener(SWT.Selection, okListener);
		
		MenuItem help_barbtn = new MenuItem(menu, SWT.NONE);
		help_barbtn.setText("Help");
		help_barbtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				helpWindow.open();
			}
		});
		
		topleft = new Composite(shell, SWT.NO_REDRAW_RESIZE);
		topleft.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topleft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topleft.setToolTipText("");
		stackLayout = new StackLayout();
		topleft.setLayout(stackLayout);
		GridData gd_topleft = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_topleft.heightHint = 500;
		gd_topleft.widthHint = 500;
		
		topleft.setLayoutData(gd_topleft);
		
		paintPanel1 = new PaintPanel(topleft, SWT.DOUBLE_BUFFERED);	
		paintPanel1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		paintPanel1.setToolTipText("");
		
		paintPanel2 = new PaintPanel(topleft, SWT.DOUBLE_BUFFERED);
		paintPanel2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		paintPanel2.setToolTipText("");
		
		paintPanel3 = new PaintPanel(topleft, SWT.DOUBLE_BUFFERED);
		paintPanel3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		paintPanel3.setToolTipText("");
		
		stackLayout.topControl = paintPanel1;			//key line that was missing before; start off with top initialized
		topPaint = paintPanel1;							// topPaint is a convenience variable to refer to the current paintpanel being worked on
		topleft.setTabList(new Control[]{paintPanel1, paintPanel2, paintPanel3});
		
		kawasakiDemo1 = new MenuItem(menu_3, SWT.CHECK);
		kawasakiDemo1.setText("Kawasaki Demo");
		kawasakiDemo1.setID(1);
		
		kawasakiDemo1.setSelection(true);
		prevItem = kawasakiDemo1;			// start off the program with circle demo
		
		demo2_choice = new MenuItem(menu_3, SWT.CHECK);
		demo2_choice.setText("Demo 2");
		demo2_choice.setID(2);
		
		demo3_choice = new MenuItem(menu_3, SWT.CHECK);
		demo3_choice.setText("Demo 3");
		demo3_choice.setID(3);
		
		demos = new MenuItem[3];
		demos[0] = kawasakiDemo1;
		demos[1] = demo2_choice;
		demos[2] = demo3_choice;
		
		
		topright = new Composite(shell, SWT.NONE);
		
		GridLayout gl_topright = new GridLayout(2, false);
		gl_topright.horizontalSpacing = 10;
		gl_topright.marginHeight = 20;
		gl_topright.marginWidth = 20;
		gl_topright.verticalSpacing = 30;
		
		topright.setLayout(gl_topright);
		GridData gd_topright = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_topright.heightHint = 296;
		gd_topright.widthHint = 544;
		topright.setLayoutData(gd_topright);
		
		typeLbl = new Label(topright, SWT.WRAP);
		GridData gd_typeLbl = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_typeLbl.widthHint = 150;
		gd_typeLbl.heightHint = 125;
		typeLbl.setLayoutData(gd_typeLbl);
		typeLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.WRAP));
		typeLbl.setText("Kawasaki's Theorem - Single Vertex Folding");
		
		Label angleLbl = new Label(topright, SWT.NONE);
		GridData gd_angleLbl = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_angleLbl.widthHint = 67;
		angleLbl.setLayoutData(gd_angleLbl);
		angleLbl.setAlignment(SWT.RIGHT);
		angleLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		angleLbl.setText("angle:");
		
		angleBoxChanging = new Label(topright, SWT.BORDER | SWT.CENTER);
		GridData gd_angleBoxChanging = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_angleBoxChanging.widthHint = 192;
		angleBoxChanging.setLayoutData(gd_angleBoxChanging);
		angleBoxChanging.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		angleBoxChanging.setText("");
		angleBoxChanging.setBackground(white);
		
//		angleBoxChanging.addListener (SWT.Modify, new Listener() {		// trying to get info when a mouse moves in the canvas
//																		// i dont think this is the solution
//			public void handleEvent(Event e)
//			{
//				angleBoxChanging.setText("" + topPaint.getAngle());
//			}
//		});
		
		
		Button clearBtn = new Button(topright, SWT.CENTER);
		GridData gd_clearBtn = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_clearBtn.widthHint = 171;
		clearBtn.setLayoutData(gd_clearBtn);
		clearBtn.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		clearBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				
				topPaint.setAngles(new ArrayList<Double>(0));		// set angles array empty
				topPaint.setPoints(new ArrayList<Point>(0));
				topPaint.setLastIndices(new ArrayList<Integer>(0));
				topPaint.redraw();
			}
		});
		clearBtn.setText("Clear Canvas");
		
		Button solveBtn = new Button(topright, SWT.CENTER);
		solveBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		solveBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MyMath.solveFoldablePoints(topPaint.getAngles(), topPaint.getPoints());
				topPaint.redraw();		// the official set of arraylist points should be updated to be foldable, so redrawing should show it
				
			}
		});
		solveBtn.setText("Make Foldable");
		solveBtn.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		
		Button undoBtn = new Button(topright, SWT.CENTER);
		undoBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				topPaint.undoLastFold();
				topPaint.redraw();
			}
		});
		undoBtn.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		undoBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		undoBtn.setText("Undo Last Fold");
		
		botleft = new Composite(shell, SWT.NONE);
		GridData gd_botleft = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_botleft.heightHint = 120;
		gd_botleft.widthHint = 573;
		botleft.setLayoutData(gd_botleft);
		
		foldBtn = new Button(botleft, SWT.NONE);
		foldBtn.setLocation(0, 0);
		foldBtn.setSize(500, 43);
		foldBtn.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		foldBtn.setText("Fold It!");		
		
		botright = new Composite(shell, SWT.NONE);
		GridData gd_botright = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_botright.heightHint = 219;
		gd_botright.widthHint = 318;
		botright.setLayoutData(gd_botright);
		
		Label foldResponseLbl = new Label(botright, SWT.WRAP);
		foldResponseLbl.setAlignment(SWT.CENTER);
		foldResponseLbl.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		foldResponseLbl.setBounds(0, 0, 217, 43);
		foldResponseLbl.setText("");
		
		foldBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//int radius = topPaint.getRadius();
				//Point center = topPaint.getCenter();		// these were set as static constants in mymath during paintpanel initialization
				
				ArrayList<Double> angles = topPaint.getAngles();
				
				if(MyMath.willFold(angles))					// first call of willFold
			    {
			    	foldResponseLbl.setText("It will fold.");
			    }
			    else
			    {
			    	foldResponseLbl.setText("No fold for you.");
			    }
			}
		});
		
		topPaint.addMouseTrackListener(new MouseTrackListener() {
			@Override
			public void mouseEnter(MouseEvent arg0) {
				foldResponseLbl.setText(""); 				// supposed to set the "no fold for you label thing" blank upon reentering topleft region
			}												// not very effective design tho because it disappers quickly

			@Override
			public void mouseExit(MouseEvent arg0) {}

			@Override
			public void mouseHover(MouseEvent arg0) {}
		});

		for(MenuItem demoChoice : demos)		// logic for toggling bt different demos (correlated to switching paintpanels)
		{
			demoChoice.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se)
				{
					MenuItem sourceItem = (MenuItem) se.getSource();
					
					switch(sourceItem.getID()) { 							//IDs are good; goes to each case
					
					case 1:	stackLayout.topControl = paintPanel1; 			
							typeLbl.setText("Kawasaki's Theorem - Single Vertex Folding");
						break;
					case 2:	stackLayout.topControl = paintPanel2;
							typeLbl.setText("Demo 2");
						break;
					case 3: stackLayout.topControl = paintPanel3;
							typeLbl.setText("Demo 3");
						break;
					}
					
					topPaint = ((PaintPanel) stackLayout.topControl);
										
					if(prevItem != null)					//not necessary since starting with circle demo selected as prevItem
					{
					 	prevItem.setSelection(false);
					}
					
					prevItem = sourceItem;
					
					sourceItem.setSelection(true);
					
					topleft.layout(); 						// updates layout so changes take effect
				}
			});
		}
		
		
		saveas_file.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Save As...");
				fileDialog.setFilterNames(new String[] {"JPEG (.jpg)", "All Files (*.*)"});
				fileDialog.setFilterExtensions(new String[] {"*.jpg", "*.*"});	// permitted file extensions
				fileDialog.setFilterPath("c:\\");								// initial directory to start
				fileDialog.setFileName("origami_image.jpg");					// initial file name
				
				//if(saveas is clicked)... 
				
				i = topPaint.getCanvasImage(i);		
				
				filepath = fileDialog.open();		// trying to get the filepath from what was selected in the fileDialog
				
				
				// if(fileDialog.getFilterName().equals("obj")
				// then use another method that saves as obj (vertices, etc text file)
				// i think i can make my own filetype that saves the vertex locations in a text file, something like
				// 
				// kawasaki_file.ogmi
				// ***PTS
				// RADIUS: 200 		// optional? i havent made it generalized enough for any size circle; all are for radius of 200
				// (246, 31)
				// (98, 381)
				// ***
				// or
				//
				// ***ANGS
				// 180.0
				// 160.0
				// 15.0
				// 5.0
				// ***
				
				save(i, filepath);						// assumes that its always a jpg for my basic project
														// do i need to put a listener for when they click saveas? i dont think so				
				System.out.println("Image saved to: " + filepath);			// i guess fileDialog.open() toString returns the filepath
				
				// important note - cant click saveas again; throws error, closes window
				// also cant close the saveas window [X] box; same error
			}
		});
		
		observe(topPaint.getObservableAngle());		// set mainwindow to watch for angle changes (from mousemoves) in order to update text lbl
	}
	
	public void save(Image i, String filepath)
	{
		ImageLoader loader = new ImageLoader();
		ImageData data = i.getImageData();
		loader.data = new ImageData[] {data};			//saver's data is an array to allow for gifs
		loader.save(filepath, SWT.IMAGE_JPEG);
		
		i.dispose();
	}
	
	/*public void setFilepath(String s)
	{
		filepath = s;
	}
	
	public String getFilepath(String s)
	{
		return filepath;
	}*/
	
	public void observe(Observable o)
	{
		o.addObserver(this);
	}
	
	public void update(Observable o, Object arg)			// changes text label whenever mouse moves
	{
		double ang = ((ObservableAngle) o).getAngle();
		String angleForLbl = "";
		
		if(ang >= 0)										// manually set to < 0 when cursor leaves the canvas, in order to make label blank
		{	
			angleForLbl += (int) (ang * 10) / 10.;			// to truncate after tenths place
		}
		
		angleBoxChanging.setText(angleForLbl);
		
	}
}
