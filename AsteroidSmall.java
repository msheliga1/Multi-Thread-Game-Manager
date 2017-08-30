/* AsteroidSmall class for asteroids video game - explosion produces nothing.
 - started 7.24.17 - MJS - most routines are inherited from UFO class
*/

package games.roids;  // Asteroids video game
import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.awt.Polygon;
import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.random; // to just call round() instead of Math.round()


// -------------------------------------------
public class AsteroidSmall extends Asteroid {

   static final double MAX_SPEED = 4.0;  // smallest asteroid has fastest max speed
   static final int CRASH_POINTS = 40; 
   static final Polygon polyShape; 
   static {
       int[] xPoints = {1,  5,  13, 15, 14, 7,   0,  0, 1};
       int[] yPoints = {2,  0,   2,  6, 13, 15, 14,  5, 2};
       int nPoints = xPoints.length;
       polyShape = new Polygon(xPoints, yPoints, nPoints);
   }

   // inherited instance variables 
   // protected Point2D.Double location;   // ships position - top left of ship
   // protected Point2D.Double speed;      // changed by up key, auto-decelerates
   // protected double angleInDegrees;     // direction ship is pointing (to shoot, accelerate)

  // Asteroid Constructor - set up shape, location, velocity
  public AsteroidSmall( )  {
    location = new Point2D.Double(0.0, 0.0);
    speed = new Point2D.Double(-MAX_SPEED + random()*MAX_SPEED * 2.0, -MAX_SPEED + random()*MAX_SPEED*2.0);
    angleInDegrees = 0.0;  // no rotation for asteroids  
  } // end asteroidSmall constructor

  public AsteroidSmall(Dimension wrapSize)  {
      this();  // call no-arg constructor

      double xVal = 0.0; 
      double yVal = 0.0;
      boolean done = false;

      // System.out.println("Starting asteroidSmall constructor");
      while (!done) {
          xVal = random();
          yVal = random();
          // dont allow new asteroids in the center where the ship is
          if ((xVal < 0.4 || xVal > 0.6) && (yVal < 0.4 || yVal > 0.6)) done = true;
      } 
      location = new Point2D.Double(xVal * wrapSize.getWidth(), yVal * wrapSize.getHeight());  
      // System.out.println("Done AsteroidSmall constructor");
  } // end asteroidSmall constructor


 // Asteroid Small methods ---------------------------------------

  // this must be in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return AsteroidSmall.polyShape;  // as compared to MyShip.shipShape, etc.
  }

  public List<UFO> explode(Dimension wrapSize)  {
      List<UFO> newUFOs = new ArrayList< >();
      // System.out.println("No new asteroids added when exploding small asteroids.");
      return newUFOs; 
  } // end explode the current asteroid

  // get points for an object hit by ammo
  public int getCrashPoints( ) {
         return CRASH_POINTS;
  }  // end int getCrashPoints

  // public void decelerate( ) {
  // public void fire( ) {  
  // public void update(Dimension wrapSize)  {
  // public void move(Dimension wrapSize)  {
  // public void accelerate( ) 
  // public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
  // public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
  // public Point2D.Double getLocation ( ) {
  // public double getAngle ( ) {
  // public Double getX ( )  {
  // public Double getY ( )  {
  // public int getXInt ( )  {
  // public int getYInt ( )  {
  // MyShip non-get-set methods --------------------
  // public void draw(Graphics g, Dimension wrapSize) {

} // end class AsteroidSmall

