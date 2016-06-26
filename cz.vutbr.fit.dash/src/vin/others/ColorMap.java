package vin.others;


/**************************************************************
 *  The class ColorMap maintains a Vector of 256 Colors.
 *  It can read in colormaps in RGB format from a URL.
 **************************************************************/

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class ColorMap {
   private ArrayList<Color> colormap = new ArrayList<Color>();

   // noarg constructor
   ColorMap() { init(); }

   // create a colormap, reading parameters from supplied url
   ColorMap(URL url) { init(url); }

   
   // get color #t
   public Color getColor(int t) {
       return colormap.get(t);
   }

   // get number of available colors
   public int size() {
      return colormap.size();
   }

   // use grayscale as default
   protected void init() {
      int max = 256;  // number of RGB color values (0 - 255)
      for (int i = 0; i < max; i++)
         colormap.add(colormap.size(), new Color(max-i-1, max-i-1, max-i-1));
   }

      
   // initialize ColorMap by reading from url
   protected void init(URL url) {
      try {
         String s;
         InputStreamReader is = new InputStreamReader(url.openStream());
         BufferedReader br = new BufferedReader(is);
         while((s = br.readLine()) != null) {
            // each color consists of three integers in RGB format (between 0 and 255)
            StringTokenizer st = new StringTokenizer(s);
            int d1 = Integer.parseInt(st.nextToken());
            int d2 = Integer.parseInt(st.nextToken());
            int d3 = Integer.parseInt(st.nextToken());
            Color c = new Color(d1, d2, d3);
            colormap.add(colormap.size(), c);
         }
         br.close();
         is.close();
      }
      catch (IOException ex) {
         System.out.println("Failed to read in color map from " + url);
         System.out.println("Using default grayscale colormap instead.");
         init();  // use grayscale colormap if unsuccessful
      }
   }
   
}
