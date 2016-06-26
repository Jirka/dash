package vin.others;


/**************************************************************
 *  Rectangle with double coordinates.
 **************************************************************/

import java.text.DecimalFormat;

public class DoubleSquare {
    public final double x, y;
    public final double size;

    public DoubleSquare(double x, double y, double size) {
       if (size < 0) throw new RuntimeException("Illegal square: size < 0");
       this.x = x;
       this.y = y;
       this.size = size;
    } 

    // multiply size of square by alpha, keeping centered at same location
    public DoubleSquare scale(double alpha) {
       if (alpha <= 0) throw new RuntimeException("Illegal scaling factor");
       double xnew = x + 0.5 * size * (1 - alpha);
       double ynew = y + 0.5 * size * (1 - alpha);
       return new DoubleSquare(xnew, ynew, alpha * size);
    }

   // display as a string
   public String toString() {
      DecimalFormat df = new DecimalFormat("0.00000000");
      String s = "(x, y) = (" + df.format(x) + ", " + df.format(y) + "),  ";
      s += "size = " + df.format(size);
      return s; 
   }


}
