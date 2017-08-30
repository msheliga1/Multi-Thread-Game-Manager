// MyPolygon extends Polygon - MJS 8.15.17
// currently has constructors, center and rotate methods
package javamjs.awt;  // analagous path as regular polygon, but under javamjs

import static java.lang.Math.round;
import static java.lang.Math.toRadians;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MyPolygon extends Polygon {

	static final long serialVersionUID = 1L;

    public MyPolygon(Polygon inPoly) {  // user super call to init all fields  	
         super(inPoly.xpoints, inPoly.ypoints, inPoly.npoints);  
    }

    public MyPolygon(int[] xpoints, int[] ypoints, int npoints) {  // user super call to init all fields  	
        super(xpoints, ypoints, npoints);  
   }
    
	// get the center of the bounding box of a polygon
	  public Point2D.Double getCenter( ) {
	      Rectangle2D rect2D;
	     
	      double xCenter;
	      double yCenter;
	   
	      rect2D = this.getBounds2D();
	      xCenter = rect2D.getX() + rect2D.getWidth()/2.0;
	      yCenter = rect2D.getY() + rect2D.getHeight()/2.0;
	      return (new Point2D.Double(xCenter, yCenter));    
	  } // end MyPolygon getCenter


	  public MyPolygon rotate(double angleInDegrees, Point2D.Double center) {
	      AffineTransform at = new AffineTransform();
	      int   npts  = npoints;
	      int[] xpts  = xpoints;
	      int[] ypts  = ypoints;
	 
	      if (npts < 0) {
	        throw new IllegalArgumentException("Polygon.rotate nPts < 0 (" + npts + ").");
	      }
	      if (xpts.length < npts) {
	        String s2 = "Polygon.rotate xPts (" + xpts.length + ") < nPts (" + npts + ")."; 
	        throw new IllegalArgumentException(s2);
	      }
	      if (ypts.length < npts) {
	        String s3 = "Polygon.rotate yPts (" + ypts.length + ") < nPts (" + npts + ")."; 
	        throw new IllegalArgumentException(s3);
	      }

	      at.rotate(toRadians(angleInDegrees), center.getX(), center.getY());      

	      double[] srcPts = new double[npts*2]; 
	      double[] newPts = new double[npts*2];
	      for (int i = 0; i < npts; i++) {
	          srcPts[2*i  ] = xpts[i];
	          srcPts[2*i+1] = ypts[i];
	      }
	      at.transform(srcPts, 0, newPts, 0, npts);
	      int[] xpts2 = new int[npts];  // polygons only use ints 
	      int[] ypts2 = new int[npts];
	      for (int i = 0; i < npts; i++) {
	          xpts2[i] = (int)round(newPts[2*i]);
	          ypts2[i] = (int)round(newPts[2*i+1]);
	      }
	      return new MyPolygon(xpts2, ypts2, npts);      
	  
	  } // end MyPolygon.rotate
	
	
	
}
