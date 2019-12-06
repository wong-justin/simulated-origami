Created by Justin Wong

Jan 2017

For project summary with pictures and conclusions, see origami_sim_summary.PDF

A programming/simulation project to visualize an origami theorem. 
Intended for educational use. It is made as an SWT Project in Eclipse.

Inspired by Erik Demaine's video lectures.

#### Known Issues:
- Saving more than once crashes
- Clicking "Cancel" in the save dialog crashes
- Clicking the top-right red [X] on the save dialog crashes
- Clicking help more than once crashes
- Some angles arent actually added in order; sometimes correct, just depends on where new folds are created and which order
	- example - clicking 95 -> 150 -> 290 -> 310 could produce diff results than clicking
	  290 -> 95 -> 310 -> 150
	- this means that my putInOrder methods are bad; should fix
	- quick fix - keep selection sort in the fold it method
- Fold It is unimplemented for an odd number of creases
	- Fold It will not always work the first time for an even number of creases; must keep clicking
- Save As can produce an image that has a corner of a window showing; don't know how to reproduce; 
	- found 5-10-17 (I thought images were taken care of by now; guess not)

#### Fixed Issues:
- Incorrect angles, especially around 180 degrees
- Clicking "Make Foldable" with a blank canvas crashes; need to fix method with array to account for this case
- Clicking "Fold It!" with a blank canvas crashes; see above
- Undo does not perform correctly chronologically all the time, especially with several folds present
- Undo messes up future 'fold it' calls so that some things are negative and not displayed correctly
	- maybe Undo should be avoided for now altogether :(

#### Other Notes:
- The .classpath file for the project for eclipse is modified if the reference libraries have a different path
	(i.e. different computer)
	- Should I try to have 2 copies of the .classpath file to toggle between home/school?
- There may be non-disposed resources because of some unfamiliarity with SWT
- minor - Should the program icon be a different file type (gif or ico or other)?

#### Existing Features:
- Given creases, tell if foldable
- Adjust given creases to be foldable
- Save as jpg
- Undo

#### Features to add:
- Other demo types (eg. 1D flat folding, fold and one cut)
- Save as other file (eg. vector image, custom file type that saves points/angles)
	- Open file
- Type in angle to create a line (another option besides just clicking)
	- This wouldn't be that bad since I already have an angleToPoint method
- M/V assignment algorithm 
	- users M/V assignment (an edit mode of sorts)
- Toggle mode to hover over a sector and show its angle, rather than just automatically creating temp line on cursor
- Instead of unrestricted cursor clicks that produce decimal angles, use a snap-to-integer angle system where everything will be integers
	- This would not only help be more exact (combating the imprecise use of integer pixel coords of SWT Point), 
	  but also would contribute to a better user/learning experience; user would have more control and it would be easier to 
	  experiment and produce desired results (e.g. make two sectors of equal size)
- Maybe use linked list instead of array lists, where each node represents a crease (that has an angle and a point)
	- This would probably help reduce confusion among all the array lists and make more readable

#### Shoutouts to:
- Lindsey Spalding, mentor
- Cheerleaders and testers: Stefan I, Michael C, Tess M, Tori G, Madison D, Jacob E, Nick H, 
- Robert Lang, Jun Mitani for inspiring program
