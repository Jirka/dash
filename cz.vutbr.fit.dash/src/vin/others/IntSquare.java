package vin.others;


/**************************************************************
 *  Square
 **************************************************************/

public class IntSquare {
    public final int x, y;
    public final int size;

    // empty square
    public IntSquare() {
        x = 0;
        y = 0;
        size = 0;
    }

    // set rectangle to given values
    public IntSquare(int x, int y, int size) {
        this.x    = x;
        this.y    = y;
        this.size = size;
    }

   // add constructor that takes two points to define rectangle, force to be square rooted at p
   public IntSquare(IntPoint p, IntPoint q) {
      int dx = p.x - q.x;
      int dy = p.y - q.y;
      size = Math.max(Math.abs(dx), Math.abs(dy));
      if (dx > 0) x = p.x - size;
      else        x = p.x;
      if (dy > 0) y = p.y - size;
      else        y = p.y;
   }

}
