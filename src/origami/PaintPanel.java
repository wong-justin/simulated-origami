package origami;

//import javax.swing.JPanel;		// all these are awt/swing, which im not using for this project
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints; 
//import java.awt.*;
//import javax.swing.*;
import org.eclipse.swt.widgets.*;
//import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
//import org.eclipse.jface.resource.ResourceManager;		// somebody on stackoverflow said this is bad/inefficient
import java.util.ArrayList;


public class PaintPanel extends Canvas{
						
	private Color white, black;	//, cyan;
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Double> anglesOfPoints = new ArrayList<Double>();
	private final int RADIUS = 200;
	private Point size; 						// got size of composite using a control listener; successful!
	private Point center;
	private Point tempPoint;
	private Point clickedPoint;
	private boolean isSamePoint;
	private double tempAngle;
	private GC gc;
	private ObservableAngle tAngle = new ObservableAngle();
	
	/**
	 * Create the panel.
	 */
	public PaintPanel(Composite parent, int style) {

		super(parent, style);
		
		MyMath.setRadius(RADIUS);
		
		white = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		//cyan = getDisplay().getSystemColor(SWT.COLOR_CYAN);
				
//		p.setBackground(cyan);	// only changes panel2 and panel3 to be cyan...
		
		paintBackground(); 								// not really doing much
		
		this.addControlListener(new ControlListener() {	// just to get size of composite
			public void controlMoved(ControlEvent e)
			{}
			public void controlResized(ControlEvent e)		// used because the control (composite) starts out as null
			{												// but changes size to be initialized; meant to be only used for initialization
				size = getSize();
				center = new Point(size.x/2 +1, size.y/2 +1);
				MyMath.setCenter(center);
				tempPoint = new Point(center.x, center.y);		// may cause small problem because temppoint could
																// change randomly if this canvas is resized
			}
		});
		
		this.addMouseListener(new MouseListener() {
			
			public void mouseDoubleClick(MouseEvent e)
			{}
			
			public void mouseDown(MouseEvent e)
			{
				clickedPoint = new Point(e.x, e.y);
				Point pOnCircumference = MyMath.closestPointOnCircle(clickedPoint, center);
				
				for(Point p: points)
				{
					if(pOnCircumference.x == p.x && pOnCircumference.y == p.y)	// gotta check to make sure not to have 2 of the exact same point
					{
						isSamePoint = true;
						break;
					}
				}
				if(!isSamePoint)
				{
					points.add(pOnCircumference);
					anglesOfPoints.add(MyMath.angleOnCircle(pOnCircumference)); //angles are accurate now; before was using angles based on the click, not the circle; led to wonky numbers
					
					// points arr and angles arr correspond at same indices; should never become unaligned (ie diff size or wrong angle for point), 
					// but i dont check for this; i only manually code methods using points and angles arrays at same time
					// probably not the best solution to have 2 regular arrays for one conceptual data set
				}
				isSamePoint = false;
				redraw();
			}
			
			public void mouseUp(MouseEvent e)
			{}	
		});	
		
		this.addMouseMoveListener(new MouseMoveListener() {
			
			public void mouseMove(MouseEvent e)
			{				
				setTempPoint(MyMath.closestPointOnCircle(new Point(e.x, e.y), center));
				tempAngle = MyMath.angleOnCircle(tempPoint);					// this is not the main tempangle; the real thing is housed in the observable angle object so that other class can observe it
				
				tAngle.setAngle(tempAngle);				// this is the important one to change (the observable object)
				redraw();
			}	
		});
		
		this.addMouseTrackListener(new MouseTrackListener() {
			
			public void mouseEnter(MouseEvent e)
			{}
			
			public void mouseExit(MouseEvent e)
			{
				tempPoint = new Point(center.x, center.y);		// works! it doesn't draw the line when mouse leaves canvas
				tAngle.setAngle(-1);							// solution is setting the tempPoint to the center, where line is drawn from center to center
																// setting angle negative so client class knows to set text field blank
				redraw(); 										
			}
			
			public void mouseHover(MouseEvent e)
			{}
		});
		
		this.addPaintListener(new PaintListener() { 
			
			public void paintControl(PaintEvent e)
			{
				//Composite source = (Composite) e.getSource();		// unused right now
				//if(!fromSaving)//if(source instanceof PaintPanel)					
					
				gc = e.gc;
				gc.setAntialias(SWT.ON); 							// supposed to make less jagged paintings
				
				gc.setBackground(black);
				gc.setForeground(black);

				gc.setLineWidth(3);
				
				gc.drawOval(50, 50, 2*RADIUS , 2*RADIUS ); 			// big background circle
				
				gc.setLineWidth(1);
				
				gc.fillOval(center.x-2, center.y-2, 4, 4);			// center point
				
				for(Point p : points) 								// fill in all previously clicked points from array
				{
					drawPointWithRadius(gc, p);
				}
				
//				if(tempPoint != null)
				drawPointWithRadius(gc, tempPoint);
				
				//else							//must be called from the saving place
				
					//gc.copyArea(img, 500, 500);			//moved this job to exclusively inside the getimg method
					//fromSaving = false;
			}		
		});	
	}
	/**
	 * Paint the background of the gc used in the paintpanel as white
	 * @param
	 */
	public void paintBackground() 
	{
		setBackground(white);		// only setting panel2 and panel3 backgrounds?
		setForeground(white);		// this method isnt usefeul anyways
	}

/*  public void repaint()	// can use to modify how to repaint
	{
		
	}*/
	/**
	 * The drawing that is currently on the canvas is put into a given image
	 * @param i
	 * @return
	 */
	public Image getCanvasImage(Image i)
	{
		GC gc = new GC(this);
		gc.copyArea(i, 0, 0);									//puts area of gc into the image; (0,0) is the starting point and it goes up to the image's size

		//redraw();												// maybe it needs to be layout() or update() instead?

		return i;								// it works!!!1!
	}
	/**
	 * Draws the crease to a point on the circumference on the circle;
	 * also draws the circle representing the point on the circle, but 
	 * due to math inconsistencies slightly skewing position compared to 
	 * the circumference, they end up hidden.
	 * 
	 * @param gc
	 * @param pOnCircle
	 */
	public void drawPointWithRadius(GC gc, Point pOnCircle) // generalized to paint the vertex and corresponding crease to the center
	{
		gc.fillOval(pOnCircle.x-1, pOnCircle.y-1, 2, 2);
		gc.drawLine(center.x, center.y, pOnCircle.x, pOnCircle.y);
	}
	
	public void undoLastFold()
	{
		if(points.size() > 0)
		{	
			points.remove(points.size()-1);
			anglesOfPoints.remove(anglesOfPoints.size()-1);
			redraw();
		}
	}
	
	public ArrayList<Point> getPoints()
	{
		return points;
	}
	
	public void setPoints(ArrayList<Point> arr)
	{
		points = arr;
	}
	
	public ArrayList<Double> getAngles()
	{
		return anglesOfPoints;
	}
	
	public void setAngles(ArrayList<Double> arr)
	{
		anglesOfPoints = arr;
	}
	
	public int getRadius()
	{
		return RADIUS;
	}
	
	public Point getCenter()
	{
		return center;
	}
	
	public double getAngle()
	{
		return tAngle.getAngle();
	}
	
	public void setTempPoint(Point p)
	{
		tempPoint = new Point(p.x, p.y);
	}
	
	public GC getGC()
	{
		return gc;
	}
	
	public ObservableAngle getObservableAngle()
	{
		return tAngle;
	}
	
	
}
	



