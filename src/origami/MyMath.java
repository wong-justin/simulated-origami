package origami;

//import java.lang.reflect.Array;
import java.util.ArrayList;
import org.eclipse.swt.graphics.Point;

public class MyMath {
	
	private static int radius;
	private static Point center;
	private static final double MARGIN_OF_ERROR = 2;	// to account for user imprecision
	
	/**
	 * math from pythagorean thm
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double distance(Point p1, Point p2)
	{
		return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y - p1.y)*(p2.y-p1.y));
	}
	/**
	 * 
	 * @param circleP is a point on the circle circumference (not just any cursor point)
	 * @return angle 0 - 360, -1 if theres a problem
	 */
	public static double angleOnCircle(Point circleP)
	{
		Point baseP = new Point(radius, 0);
		Point newP = new Point(center.y - circleP.y, center.x - circleP.x);

		// normalize to an imaginary circle with center 0,0 and 0 degrees being on pos x-axis
		// now the calculations are easier
		// (x,y) -> (y, -x)
		// circleP.x - center.x, circleP.y - center.y
		// transforms to
		// circleP.y - center.y, center.x - circleP
		// except graphics coordinates reverse y axis direction so now its
		// -> (-y, -x)
		
		if(circleP.y > center.y)	// changes base of measurement to be accurate near extremities;
									// however, it means extra "if" statements at end of this method
									// because there are quadrants to account for
		{
			baseP = new Point(-radius, 0);
		}
		
		
		double dist = distance(newP, baseP);

		double angle = 2 * Math.asin(0.5 * dist / radius); //returns radians -pi/2 to pi/2
		
		angle *= 180 / Math.PI;			// radians to degrees
		
		if(circleP.x > center.x ) //fixing each quadrant so final result is 0 - 360
		{
			if(circleP.y < center.y)
			{
				angle = 360 - angle;
			}
			else
			{
				angle += 180;
			}
		}
		else
		{
			if(circleP.y > center.y)
			{
				angle = 180 - angle;
			}
		}
		
		if(Double.isNaN(angle))		// trying to be a good boy with error checking thing
		{
			return -1;
		}
		
		return angle;
		
		// angles near bottom of circle (180 degrees) are slightly inaccurate; goes 169, 174, 173, 171.9, 180.0, 188, 171.9
		// edit: fixed this! used another reference point depending on position
		// i think earlier problem is that 180 got too far away from base point and the differences between points became almost 0; pixels matter
		// see paint drawing example explanation
	}
	
	/**
	 * Used to find the line that 'follows' the cursor
	 * @param cursor
	 * @param center
	 * @return
	 */
	public static Point closestPointOnCircle(Point cursor) //only working for 1/4 of the circle
	{
		// need to find a solution more accurate (especially near bottom of circle)
		
		// solution 1
		// double m = (0. + cursor.y - center.y)/(cursor.x - center.x); //slope of line from cursor to center of circle
		// double m = (0. - cursor.x)/cursor.y;		// tangent to circle
		// y - center.y = m(x - center.x)
		
		double dist = distance(cursor, center);
		double xSol = (cursor.x - center.x)*radius/dist + center.x;
		double ySol = (cursor.y - center.y)*radius/dist + center.y;
		
		return new Point((int)xSol, (int)ySol);
		
		// solution 2
		// intersect with tangent line to circle
		// point.y = center.y +  (int) Math.round(Math.sqrt((r*r*m*m) / (1 + m*m))); // maybe cursor.y or radius instead of center.y?
		// point.x = center.x + (int) Math.round(Math.sqrt(r*r - (point.y - center.y)*(point.y - center.y))); //other math
	}
	
	public static double[] selectionSort(double[] a)
	{
		for(int i = 1; i < a.length; i++)		// selection sort? to order the crease angles from 0 to 360
		{											// should be sorted now in paintpanel but it isnt; ideally wont need this sort
			int j = i;
			while(j > 0 && a[j] < a[j-1])
			{
				double temp = a[j];
				a[j] = a[j-1];
				a[j-1] = temp;
				j--;
			}
		}
		return a;
	}
	
	public static double[] anglesBetweenCreases(ArrayList<Double> a)
	{
		double[] arr = new double[a.size()];		// must put into new array because it was modifying old arraylist from paintpanel, messing up any later fold requests
		for(int i = 0; i < a.size(); i++)
		{
			arr[i] = a.get(i);
		}
		
		arr = selectionSort(arr);
		
		System.out.println("\nLines:");
		for(double d: arr)
		{
			System.out.println(d);				// test to see if angles are correct and if array is sorted
		}
		
		double temp = arr[arr.length - 1];
		for(int i = arr.length-1; i > 0; i--)		// angles are 0 - 360; convert to angles between lines so that they sum to 360
		{
			arr[i] = arr[i] - arr[i-1];
		}
		arr[0] = arr[0] + 360 - temp;
		
		System.out.println("\nAngles between lines:");
		//double sum = 0;
		for(double d: arr)
		{
			System.out.println(d);
			//sum += d;
		}
		//System.out.println("\nsum: " + sum);		// should always be 360; now unimportant test
		return arr;
	}
	
	public static boolean willFold(ArrayList<Double> a)
	{
		System.out.println();
		if(a.size() % 2 == 1)
		{							//must have even number of folds per Kawasaki's thm
			System.out.println("Won't fold; odd number of creases");
			return false;
		}
		if(a.size() == 0)
		{
			System.out.println("No creases to fold; technically flat folded");
			return true;		// or is it false???
		}
		
		double[] arr = anglesBetweenCreases(a);
		
		double halfAngleSum = 0;				// i think theres another algorithm to solve the same problem, involving 
												// recursive or something like getting cone, reducing to base case of a cone that will fold flat;
												// listen to lecture 03 again
												// that sounds cooler than my method
		
		for(int i = 0; i < arr.length; i+=2)
		{
			halfAngleSum += arr[i];
		}
		System.out.println("Sum of alternating half: " + halfAngleSum);
		
		return Math.abs(180-halfAngleSum) <= MARGIN_OF_ERROR; // aka. if the angle sum is about 180	
		// margin of error is arbitrarily 2.0 degrees; physical folding test shows it's pretty foldable with that margin
	}
	
	public static void solveFoldablePoints(ArrayList<Double> angs, ArrayList<Point> pts)		// It's void b/c i will be modifying the pts arraylist from paintpanel directly by adding or modifying it (object by reference blah blah))
	{																	// i could approach this as finding the smallest change possible (ie wouldnt move a line 90 degrees around if 
																		// i could move a different one only 10); this would involve more computation and work to find best case scenario
																		// it's already 4-27 and i dont know if i could implement this in time RIP; this would demonstrate the most knowledge tho (ie coolest)
		if(willFold(angs))
		{
			System.out.println("It already folds!");
			return;
		}
		
		double[] angsBtwn = anglesBetweenCreases(angs);		// should be easier to work with arr instead of arrList;
															// also dont want to modify/reorder the arrList passed in until im ready
		
		double[] angles = new double[angs.size()];		// must put into new array because it was modifying old arraylist from paintpanel, messing up any later fold requests
		for(int i = 0; i < angs.size(); i++)
		{
			angles[i] = angs.get(i);
		}
		angles = selectionSort(angles);
		
		if(angs.size() % 2 == 0)		// adjust one point; maybe ill have to adjust more than one?
		{			
			// pseudocode:
			// 
			// 180 - altAngleSum = difference			// amount i need to move a crease by so that it will equal 180 in end
			// search for a point that i could move by that much; i think there
			// will be some that i couldnt move while keeping within other creases
			// 
			// angle of movable point + difference = new angle 
			// make new point out of new angle
			// replace old point with new point
			// 
			// there will be multiple possible points that can be moved by this angleDifference
			// the cheap solution is to just use the first one that works
			// - going to do this in the interest of completion deadline
			//
			// there's probably a better solution anyways
			//
			
			double alternatingAngleSum = 0;
			for(int i = 0; i < angsBtwn.length; i+=2)
			{
				alternatingAngleSum += angsBtwn[i]; 	// sum of even index half; i think its important to know that its the even angles
			}
			
			double angleDiffToMoveBy = Math.abs(180 - alternatingAngleSum);	//if positive, means that the even-indices angles sum is < 180
																			// im only using the abs value for the particular solution at hand tho
																			// should revert back and keep sign (no abs) for better solution in the future
			
			// cheap solution to find one point that would work rather than multiple
			
			// I postulate that the altAngleSum containing the largest angle will be > 180
			// 		- except case of multiple equal largest angles; happily ignoring for now...
			// Moreover, this angle will be larger than the abs value of angleDiffToMoveBy (which is 180 - alternatingAngleSum)
			// 		- it follows that i will be able to reduce that angle by the diff and increase an adjacent angle by the diff
			
			// find index of largest sector angle; for better solution in future i could find any angs (included in the larger sum) that are larger than the angle difference
			int tempMaxLoc = 0;
			for(int i = 0; i < angsBtwn.length; i++)
			{
				if(angsBtwn[tempMaxLoc] < angsBtwn[i])
				{
					tempMaxLoc = i;
				}
			}
			
			// decrease corresponding angle in the arraylist
			// must increase adj angle in return to keep everything = 360
			// 		- which angle is adjacent? "behind" or "in front"? so many choices
			// 		- going to arbitrarily choose "behind" for now
			//			- actually not as arbitrary since angsBtwn are bounded by thisangle and thisangle-1 (the -1 will be the adj one)
			// EDIT: dont need any adj angle because only one line moves (one angle and one point;
			// no need to adjust and treat like an angle btwn)
			
			
			// problem starts below; need to get the proper line angle from angs rather than just angleBtwn
			
			double newAng = angs.get(tempMaxLoc) - angleDiffToMoveBy;		//angs.get something else
			if(newAng < 0)				// now it wont go below 0 and instead wrap around like a good circle
			{
				newAng = 360 + newAng;
			}
			
			angs.set(tempMaxLoc, newAng);
			pts.set(tempMaxLoc, calcPointGivenAngle(angs.get(tempMaxLoc)));	
				
			
			// problems so far:
			// - somehow adds angles to arrList; i can see it in the console prints after clicking fold it
			// - made some angles negative..
			// - pts array is not in order of angles (ie not corresponding)
			// 
			// some noted causes/bad things:
			// - pts and angs from paintpanel are in chronological order of clicks, not order around the circle
			//		- good for undo btn
			// 		- bad for everything else
			//
			//		- solution: automatically put angle/point in ordered position in array upon clicking 
			//		- would require changing undo btn a little, but thats ok because undo isnt as important
			//
			// - im treating angs like ansBtwn, but angs are actually the 0 to 360 representations of the creases!
			// 		- solution: 
			// 			...
			//
			// - i forgot that calcPointGivenAngle is already screwy; gotta fix that math if i expect this whole thing to work
			// edit: calcPoint may be fixed
			//
			// if i solve all these probs then i think it should work
			// but what do i know
			
			
			
			
			
			// finish implementing me justiiiinnnnn
			// find the points that can move
			/*
			ArrayList<Integer> angsThatCanChange = new ArrayList<Integer>();		// is the index of the angle to change (instead of value); it will be increased if alternatinganglesum is < 180 or decreased otherwise
			for(int i = 0; i < angsBtwn.length-1; i++)	// am i guaranteed to find one?
			{
				if(angsBtwn[i+1] > angleDiffToMoveBy && angsBtwn[i] + angleDiffToMoveBy > 0)	// if there's enough room to shift the line (point) between the two angles
				{
					angsThatCanChange.add(i);
				}
				
				// i and i+1 will be the indices of the angles in angs (the reference to the official arraylist) to change; the line (point) between the angles will be moved
					
				// find the smallest necessary change out of possible angs in angsThatCanChange?
				
				
			}*/
		}
		else	// there are an odd number of angs and you gotta add one; maybe ill have to add a point and shift another as well?
		{

		}
		
		System.out.println("Now it should fold.");
		return;
	}
	
	public static Point calcPointGivenAngle(double ang) //wrong calculations; i was using degrees instead of radians!!!
	{
		double xSol = center.x + radius*Math.cos((ang+90)*Math.PI/180);
		double ySol = center.y - radius*Math.sin((ang+90)*Math.PI/180);
		
		return new Point((int) xSol, (int) ySol);
	}
	
	public static void setRadius(int r)
	{
		radius = r;
	}
	
	public static void setCenter(Point p)
	{
		center = p;
	}
}
