package origami;

import java.util.ArrayList;
import org.eclipse.swt.graphics.Point;

public class MyMath {
	
	private static int radius;
	private static Point center;
	
	public static double distance(Point p1, Point p2)
	{
		return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y - p1.y)*(p2.y-p1.y));
	}
	
	public static double angleOnCircle(Point pFromCircle) //given point on circle
	{
		Point base = new Point(0, -radius);
		Point newP = new Point(pFromCircle.x - center.x, pFromCircle.y - center.y);
		
		double dist = distance(newP, base);

		double angle = 2 * Math.asin(0.5 * dist / radius); //returns radians -pi/2 to pi/2
		
		// 0 = top 	ie p = base
		// 90 = left
		// 180/-180 = bot
		// -90 = right
		
		angle *= 180 / Math.PI;			// to degrees
		if(pFromCircle.x > center.x)
		{
			angle = 360 - angle;
		}
		return angle;
		 //angles near bottom of circle (180 degrees) are slightly inaccurate; goes 169, 174, 173, 171.9, 180.0, 188, 171.9
	}
	
	public static Point closestPointOnCircle(Point cursor, Point center) //only working for 1/4 of the circle
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
	
	public static double[] anglesBetweenCreases(ArrayList<Double> a)
	{
		double[] arr = new double[a.size()];		// must put into new array because it was modifying old arraylist from paintpanel, messing up any later fold requests
		for(int i = 0; i < a.size(); i++)
		{
			arr[i] = a.get(i);
		}
		
		for(int i = 1; i < arr.length; i++)		// selection sort? to order the crease angles from 0 to 360
		{
			int j = i;
			while(j > 0 && arr[j] < arr[j-1])
			{
				double temp = arr[j];
				arr[j] = arr[j-1];
				arr[j-1] = temp;
				j--;
			}
		}
		
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
		
		return halfAngleSum > 178 && halfAngleSum < 182; //if the angle sum is about 180	
		// margin of error is arbitrarily 2.0 degrees; physical folding test shows it's pretty foldable
		
		// current problem - angles near bottom are inaccurate
	}
	
	public static void solveFoldablePoints(ArrayList<Point> pts,		// It's void b/c i will be modifying the pts arraylist from paintpanel directly by adding or modifying it (object by reference blah blah)
			ArrayList<Double> angs)	{									// i could approach this as finding the smallest change possible (ie wouldnt move a line 90 degrees around if 
																		// i could move a different one only 10); this would involve more computation and work to find best case scenario
																		// it's already 4-27 and i dont know if i could implement this in time RIP; this would demonstrate the most knowledge tho (ie coolest)
		if(willFold(angs))
		{
			System.out.println("It already folds!");
			return;
		}
		
		double[] angsBtwn = anglesBetweenCreases(angs);
		
		if(angs.size() % 2 == 0)
		{
			// adjust one point; maybe ill have to adjust more than one?
			
			// psuedocode:
			// 
			// (360 - halfanglesum) / 2 = difference			// amount i need to move a crease by
			// search for a point that i could move by that much; i think there
			// will be some that i couldnt move while keeping within other creases
			// 
			// angle of movable point + difference = new angle 
			// make new point out of new angle
			// replace old point with new point
			// 
			// theres probably a better solution out there
			
			double alternatingAngleSum = 0;
			for(int i = 0; i < angsBtwn.length; i+=2)
			{
				alternatingAngleSum += angsBtwn[i]; 	//sum of even index half
			}
			
			double angleDiffToMoveBy = (180 - alternatingAngleSum)/2;	//if positive, means that the even indices sum < 180
			
			// find a point that can move
			ArrayList<Integer> angsThatCanChange = new ArrayList<Integer>();		// is the index of the angle to change (instead of value); it will be increased if alternatinganglesum is < 180 or decreased otherwise
			for(int i = 0; i < angsBtwn.length-1; i++)	// am i guaranteed to find one?
			{
				if(angsBtwn[i+1] > angleDiffToMoveBy && angsBtwn[i] + angleDiffToMoveBy > 0)	// if there's enough room to shift the line (point) between the two angles
				{
					angsThatCanChange.add(i);
				}
				
				// i and i+1 will be the indices of the angles in angs (the reference to the official arraylist) to change; the line (point) between the angles will be moved
					
				// find the smallest necessary change out of possible angs in angsThatCanChange?
				
				
			}
		
		}
		else
		{
			// its an odd number and you gotta add a point; maybe ill have to add a point and change another as well?
		}
		
		System.out.println("Now it should fold.");
		return;
	}
	
	public static Point calcPointGivenAngle(double ang)
	{
		double xSol = center.x - Math.sin(ang);
		double ySol = center.y + Math.cos(ang);
		
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
