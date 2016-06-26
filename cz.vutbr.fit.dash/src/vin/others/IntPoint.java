package vin.others;


/*************************************************************************
 *  Compilation:  javac IntPoint.java
 *  Execution:    java IntPoint
 *
 *  Implementation of 2D point using rectangular coordinates.
 *
 *************************************************************************/

public class IntPoint { 
    public final int x;
    public final int y; 
   
    // create and initialize a point with given (x, y)
    public IntPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
  
    // return string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
