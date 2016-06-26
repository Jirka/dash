package vin.others;


/*************************************************************************
 *  Compilation:  javac DoublePoint.java
 *  Execution:    java DoublePoint
 *
 *  Implementation of 2D point using rectangular coordinates.
 *
 *************************************************************************/

public class DoublePoint { 
    public final double x;
    public final double y; 
   
    // create and initialize a point with given (x, y)
    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
  
    // return string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
