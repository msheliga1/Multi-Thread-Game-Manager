/* Asteroid class for asteroids video game
 - started 7.24.17 - MJS - most routines are inherited from UFO class
*/

package games.roids;  // Asteroids video game
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D; 
import java.awt.Dimension;
import java.awt.Graphics2D;  // needed for affineTransforms
import java.awt.Polygon;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;  // use paintComponent instead of paint
import static java.lang.Math.random; // to just call round() instead of Math.round()
import static java.lang.Math.sin; 


// -------------------------------------------
public class AsteroidBig extends Asteroid {

   static final int MAX_MODELS = 2;  // different shapes for this object
   static final double MAX_SPEED = 2.0;
   static final int CRASH_POINTS = 10;
   static final Polygon[ ] polyShapes = new Polygon[MAX_MODELS]; 
   static {
       int[] xPoints = {4,  20,  60, 50, 60, 30,  0,  8, 4};
       int[] yPoints = {4,  10,   0, 20, 60, 50, 40, 20, 4};
       polyShapes[0] = new Polygon(xPoints, yPoints, xPoints.length);
       int[] xPoints1 = {4,  30,  50, 60, 40, 30,  5,  0, 4};
       int[] yPoints1 = {8,   0,   5, 30, 45, 60, 42, 30, 8};
       polyShapes[1] = new Polygon(xPoints1, yPoints1, xPoints1.length);
   }

   // inherited instance variables
   // protected long ID;                   // serial ID of all UFOs
   // protected int model;                 // multiple shapes or models
   // protected Point2D.Double location;   // ships position - top left of ship
   // protected Point2D.Double speed;      // changed by up key, auto-decelerates
   // protected double angleInDegrees;     // direction ship is pointing (to shoot, accelerate)
   // protected boolean cloaked = false;   // Romulans, timers are cloaked
   // protected boolean crashed;           // set when this crashes into another object
   // protected int updatesLeft;           // used to timeout other objects

  // Asteroid Constructor - set up shape, location, velocity
  public AsteroidBig( )  {
    model = (int) this.ID % MAX_MODELS;
    location = new Point2D.Double(0.0, 0.0);
    speed = new Point2D.Double(-MAX_SPEED + random()*MAX_SPEED * 2.0, -MAX_SPEED + random()*MAX_SPEED*2.0);
    angleInDegrees = 0.0;  // no rotation for asteroids  
  } // end asteroidBig constructor

  public AsteroidBig(Dimension wrapSize)  {
      this();  // call no-arg constructor

      double xVal = 0.0; 
      double yVal = 0.0;
      boolean done = false;

      // System.out.println("Starting asteroidBig constructor");
      while (!done) {
          xVal = random();
          yVal = random();
          // dont allow new asteroids in the center where the ship is
          if ((xVal < 0.4 || xVal > 0.6) && (yVal < 0.4 || yVal > 0.6)) done = true;
      } 
      location = new Point2D.Double(xVal * wrapSize.getWidth(), yVal * wrapSize.getHeight()); 
      // System.out.println("Done AsteroidBig constructor");
  } // end asteroid constructor
 // Asteroid Big methods ---------------------------------------

  // this must be in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return AsteroidBig.polyShapes[model];  // as compared to MyShip.shipShape, etc.
  }

  public List<UFO> explode(Dimension wrapSize)  {
      List<UFO> newUFOs = new ArrayList< >();
      Asteroid newRoid;

      newRoid = new AsteroidMedium(wrapSize);
      newRoid.setLocation(getLocation());
      newRoid.move(wrapSize);  // move once so not right on top of eachother
      newUFOs.add(newRoid);
      newRoid = new AsteroidMedium(wrapSize); 
      newRoid.setLocation(this.getLocation());
      newRoid.move(wrapSize);
      newUFOs.add(newRoid);   
      return newUFOs; 
  } // explode the current asteroid - produce 2 new asteroids

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

} // end class AsteroidBig

