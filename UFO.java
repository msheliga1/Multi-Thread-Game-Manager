/* UFO class - superclass of all outer space objects
UFO is abstract - you cant actually create a UFO - get it :) !! 
Mike Sheliga 7.22.17
*/

package games.roids;  // Asteroids video game
import java.awt.*;
import java.awt.geom.Rectangle2D; 
import java.awt.Graphics2D;  // needed for affineTransforms
import java.awt.Polygon;
import javamjs.awt.MyPolygon;  // for rotate and center methods
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
// import java.awt.geom.Polygon2D; no poly2D, path2D instead
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.cos;  // to just call round() instead of Math.round()
import static java.lang.Math.round;  
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

// if myShip hits enemy, only me destoyed
// fire speed appears constant.
// if enemy ship hits asteroid, both destroyed

// -------------------------------------------
public abstract class UFO {

   static final Polygon shipShape; 
   static final int CRASH_POINTS = 0;  // default crash points
   static long INSTANCE_COUNT = 1;

   static {  // default UFO shape is a diamond (hard to make a saucer)
       int[] xPoints = {50,  0, -50,   0};
       int[] yPoints = { 0, 50,   0, -50};
       int nPoints = xPoints.length;
       shipShape = new Polygon(xPoints, yPoints, nPoints);
   }

   protected long ID;                   
   protected int model = 0;            // most UFOs have mutliple shapes
   protected Point2D.Double location;  // ships position - top left of ship
   protected Point2D.Double speed;     // changed by up key, auto-decelerates
   // ship points North originally, which is -y in Java => 270 degs
   protected double angleInDegrees = 0.0;  // angle ship is pointing (not moving)
   protected boolean cloaked = false;  // Romulans, timers are cloaked
   protected boolean crashed = false;  // on crash, explode then delete
   protected int updatesLeft = 0;      // time left till UFO expires (for ammo, debris)


  // UFO Constructor - set up shape, location, velocity
  public UFO( )  {
    // System.out.println("Starting UFO Constructor");
    ID = INSTANCE_COUNT++;
    location = new Point2D.Double(10.0, 300.0);
    speed = new Point2D.Double(2.0, 0.5);
    // System.out.println(" Done UFO Constructor");
  } // End MyShip constructor

  // Simple UFO methods ---------------------------------------

  // accelerate UFO - when user hits accelerate button
  public void accelerate( ) {
      Double angleInRads = toRadians(angleInDegrees);  // static MATH method
      // System.out.println("Accelerate angle in degrees:" + angleInDegrees + " speed " + speed.getX() + ", " + speed.getY());
      // add y below since 90 (down) is same as y-axis in java graphics
      speed.setLocation(speed.getX() + cos(angleInRads), speed.getY() + sin(angleInRads));
  } 

  // this is to slowly slow-down the ship
  public void decelerate( ) {
      // most objects dont decelerate
  }

  public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
    angleInDegrees = (angleInDegrees - 5.0) % 360.0;
  } 

  public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
    angleInDegrees = (angleInDegrees + 5.0) % 360.0;
  } 


  public Point2D.Double getLocation ( ) {
      return location;
  }

  public void setLocation (Point2D.Double newLocation) {
      location.x = newLocation.getX();
      location.y = newLocation.getY();
  }

  public double getAngle ( ) {
      return angleInDegrees;
  }

  public Double getX ( )  {
    return location.getX();
  } // end getX

  public Double getY ( )  {
    return location.getY();
  } // end getY  

  // return x location (top left) rounded to nearest int
  public int getXInt ( )  {
    return (int) Math.round(location.getX());
  } // end getXInt rounded to nearest integer

  public int getYInt ( )  {
    return (int) Math.round(location.getY());
  } // end getYIntrounded to nearest integer

  public void addToUpdatesLeft (int tics)  {
      updatesLeft += tics;
  } // end addToUpdatesLeft

  // MyShip non-get-set methods --------------------
  // update ships position
  public void update(Dimension wrapSize)  {
    // System.out.println(" Ship Update in class UFO");
    decelerate( );  // some UFOs (such as ships) auto-slow over time
    move(wrapSize);
    updatesLeft--;  // some UFOs (ammo, debris, timers) only lasts so many updates
  } // End update

  // move the UFO, wrapping around if necessary
  public void move(Dimension wrapSize)  {
    double wrapWidth  = (double) wrapSize.getWidth();  // note that dimensions are integers
    double wrapHeight = (double) wrapSize.getHeight();
    double newX = (location.getX() + speed.getX())  % (double) wrapWidth;
    double newY = (location.getY() + speed.getY()) % (double) wrapHeight;
    // -2.3 %  60.0 is -2.3 (remainder always matches sign of numerator)
    if (newX < 0.0) newX = newX + (double) wrapWidth;
    if (newY < 0.0) newY = newY + (double) wrapHeight; 
    location.setLocation(newX, newY);
  } // End move

  // see if two objects have crashed into eachother
  // many object cant crash into eachother (2 asteroids, debris, etc.)
  public boolean crashedInto(UFO hitItem, Dimension wrapSize) {
       Polygon thisPoly, hitPoly, tempPoly; // 
       Area thisArea, tempArea;
       double xMove, yMove;

       // debris cant ever crash, 2 asteroids cant crash
       if (!this.canCrashInto(hitItem)) return false;

       thisPoly = this.getShipPoly( );   // ufo moved and rotated
       thisArea = new Area(thisPoly);
       hitPoly = hitItem.getShipPoly( );  // 2nd ufo moved and rotated

       // always shift the 2nd hitPoly - 0=>shift none,right, -1=>shift left,none
       int startX = (hitPoly.getBounds().getX() < thisPoly.getBounds().getX()) ? 0: -1;
       int startY = (hitPoly.getBounds().getY() < thisPoly.getBounds().getY()) ? 0: -1;
       for (int xWraps = startX; xWraps <= startX + 1; xWraps++) {  
         for (int yWraps = startY; yWraps <= startY + 1; yWraps++) {
           xMove = xWraps * wrapSize.getWidth();
           yMove = yWraps * wrapSize.getHeight();
           tempPoly = new Polygon(hitPoly.xpoints, hitPoly.ypoints, hitPoly.npoints);
           tempPoly.translate((int)(xMove+0.5), (int)(yMove+0.5)); // shift here
           tempArea = new Area(tempPoly); 
           tempArea.intersect(thisArea);
           if (!tempArea.isEmpty()) {
               return true;
           } 
           // could also compare wrapped shapes . . . 

          } // end for yWraps
      } // end for xWraps  
       return false;
  }   // end boolean crashedInto

  // see if two objects CAN crashed into eachother - default is true
  // many object cant crash into eachother (2 asteroids, debris, etc.)
  public boolean canCrashInto(UFO hitItem) {
      return true;
  }  // end boolean canCrashInto

  // get points for 2 objects that crashed into each other
  public int getCrashPoints(UFO ufo2) {
     if (this instanceof Ammo) {
         return ufo2.getCrashPoints();         
     } else if (ufo2 instanceof Ammo) {
         return this.getCrashPoints();
     } else {
         return 0;
     }
  }  // end int getCrashPoints

  // get points for an object hit by myAmmo
  public int getCrashPoints( ) {
         return CRASH_POINTS;
  }  // end int getCrashPoints

  // see if an object has timed out (ammo, debris do, others dont).
  public boolean timedOut() {
      return (canTimeout() && updatesLeft <= 0);
  }  // end boolean timedOut

  // see if an object can time out (ammo, debris do, others dont).
  public boolean canTimeout() {
      return false;
  }  // end boolean canTimeout

  public List<UFO> explode(Dimension wrapSize)  {
      return new ArrayList<UFO> ();
  } // explode the current UFO - can produce 2 new asteroids. several debris pieces, etc.

  public void draw(Graphics g, Dimension wrapSize) {
      AffineTransform affineOrig;
      Polygon movedShip;   // ship moved to x,y coordinates
      Polygon rotatedShip; // ship moved and then rotated (must rotate last)
      Rectangle2D rect2D;
      Point2D.Double center;
      double xCenter;
      double yCenter;
 
      if (cloaked) {
         return;
      } 
      Graphics2D g2D = (Graphics2D) g.create();  // affineTransform requires g2D
      affineOrig = g2D.getTransform();           // save original to restore at end of method
      // g.drawRect((int)(double)(getX()), (int)(double)getY(), 20, 20);
      // topLeft of UFO on screen => always wrap up and to left
      for (int xWraps = 0; xWraps >= -1; xWraps--) {
          for (int yWraps = 0; yWraps >= -1; yWraps--) {
              // copy the shipShape
              Polygon myShape = getShape(); 
              movedShip = new Polygon(myShape.xpoints, myShape.ypoints, myShape.npoints); 
              // then move it . . .
              movedShip.translate(getXInt(), getYInt()); 
              // then wrap it . . . 
              movedShip.translate(xWraps*(int)wrapSize.getWidth(), yWraps*(int)wrapSize.getHeight());  
              // Only now use an affine transform to rotate the ship 
              // at = movedShip.getAffineTransform();  // no such method for Polygon
              rect2D = movedShip.getBounds2D();
              xCenter = rect2D.getX() + rect2D.getWidth()/2.0;
              yCenter = rect2D.getY() + rect2D.getHeight()/2.0;
              center = new Point2D.Double(xCenter, yCenter);
              g2D.drawOval((int)xCenter, (int)yCenter, 2, 2);
              // must rotate ship about its center
              rotatedShip = (Polygon) ((new MyPolygon(movedShip)).rotate(angleInDegrees, center)); 
              g2D.drawPolygon(rotatedShip);   
          } // end for yWraps
      } // end for xWraps  
      g2D.setTransform(affineOrig);
      g2D.dispose();  
  } // end UFO draw routine


  // getShipPolygon - get basic unwrapped shape, wrap when looking for overlaps if needed
  public Polygon getShipPoly( ) {
      Polygon movedShip;                // ship moved to x,y coordinates
      Point2D.Double center;

      // copy the shipShape
      Polygon myShape = getShape(); 
      movedShip = new Polygon(myShape.xpoints, myShape.ypoints, myShape.npoints); 
      // then move it . . .
      movedShip.translate(getXInt(), getYInt()); 
      // no need to wrap it . . .  
      center = (new MyPolygon(movedShip)).getCenter();
      // must rotate ship about its center(must rotate last)
      return (Polygon) (new MyPolygon(movedShip)).rotate(angleInDegrees, center);    
  } // end UFO getShipPoly1

  // must put this in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return UFO.shipShape;  // as compared to MyShip.shipShape, etc.
  }


  // getShipPolygon - get UFO polygon - dont include wrapping
  public Point2D.Double getCenter2(Polygon inPoly) {
      Rectangle2D rect2D;
      double xCenter;
      double yCenter;
   
      rect2D = inPoly.getBounds2D();
      xCenter = rect2D.getX() + rect2D.getWidth()/2.0;
      yCenter = rect2D.getY() + rect2D.getHeight()/2.0;
      return (new Point2D.Double(xCenter, yCenter));    
  } // end UFO getCenter


  public Polygon rotate2(Polygon inPoly, double angleInDegrees, Point2D.Double center) {
      AffineTransform at = new AffineTransform();
      int   npts  = inPoly.npoints;
      int[] xpts  = inPoly.xpoints;
      int[] ypts  = inPoly.ypoints;
 
      // String s = "Polygon.rotate npts= " + npts + " angle:" + angleInDegrees;
      // System.out.println(s + " Center " +  center.getX() + ", " + center.getY() );
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
      return new Polygon(xpts2, ypts2, npts);      
  
  } // end polygon.rotate 

} // end absract class UFO

