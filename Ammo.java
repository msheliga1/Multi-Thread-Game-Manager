/* Ammo (ammunition) class, such as missles for asteroids game.
Subclass of UFO  
Mike Sheliga 7.27.17
*/

package games.roids;  // Asteroids video game
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

// if myShip hits enemy, only me destoyed
// fire speed appears constant.
// if enemy ship hits asteroid, both destroyed

// -------------------------------------------
public class Ammo extends UFO {

   static final Polygon shipShape; 
   static final double MAX_SPEED = 12.0;

   static {  // default Ammo shape is a dot
       int[] xPoints = { 0,  1,   1,   0};
       int[] yPoints = { 0,  0,   1,   1};
       int nPoints = xPoints.length;
       shipShape = new Polygon(xPoints, yPoints, nPoints);
   }

   // protected int model;                // most UFOs come in multiple shapes
   // protected Point2D.Double location;  // ships position - top left of ship
   // protected Point2D.Double speed;     // changed by up key, auto-decelerates
   // ship points North originally, which is -y in Java => 270 degs
   // protected double angleInDegrees = 0.0;
   // protected boolean crashed = false;  // on crash delete
   // protected int updatesLeft;          // some items time out
   protected SpaceShip firingShip;        // needed to decrement shotsFired when ammo explodes

  // Constructor - set up location, velocity, angle, updates left

  // UFO Constructor - set up shape, location, velocity
  public Ammo(SpaceShip firingShip, Dimension wrapSize)  {
    // System.out.println("Starting Ammo Constructor ID " + ID);
    Point2D.Double firingPoint = firingShip.getFiringPoint( );
    
    location = new Point2D.Double(firingPoint.getX(), firingPoint.getY());
    double moveAngle = toRadians(firingShip.getAngle());
    speed = new Point2D.Double(MAX_SPEED * cos(moveAngle), MAX_SPEED * sin(moveAngle));
    this.move(wrapSize);  // move ammo away from the ships firing point or it WILL crash 
    angleInDegrees = 0.0;  // Ammo always faces same direction.
    // ammo makes it 3/4 of way around screen
    updatesLeft = (int) (0.75 * wrapSize.getWidth() / MAX_SPEED);
    this.firingShip = firingShip;
    // System.out.println("Done Ammo-Ship Constructor - updates Left is " + updatesLeft);
  } // End Ammo constructor

  // UFO methods ---------------------------------------
  // public void update(Dimension wrapSize)  {
  // move the UFO, wrapping around if necessary
  // public void move(Dimension wrapSize)  {
  // public void accelerate( ) {
  // public void decelerate( ) {
  // public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
  // public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
  // public void fire( ) {
  // public Point2D.Double getLocation ( ) {
  // public void setLocation (Point2D.Double newLocation) {
  // public double getAngle ( ) {
  // public Double getX ( )  {
  // public Double getY ( )  {
  // public int getXInt ( )  {
  // public int getYInt ( )  {

  // Ammo non-get-set methods --------------------

  // public boolean crashedInto(UFO hitItem) {
  // see if two objects CAN crashed into eachother - default is true
  // public boolean canCrashInto(UFO hitItem) {
  // public void draw(Graphics g, Dimension wrapSize) {
  // getShipPolygon - either get all 4 wraps, or get basic and wrap when comparing
  // public Polygon getShipPoly(Dimension wrapSize) {
  // getShipPolygon - get Ammo polygon - dont include wrapping
  // public Polygon getShipPoly1( ) {

  // Ammo can time out (ammo, debris do, others dont).
  public boolean canTimeout() {
      return true;
  }  // end boolean canTimeout

  public boolean canCrashInto(UFO hitItem) {
      if (hitItem instanceof Debris) return ((Debris) hitItem).canCrashInto(this);
      return true;  // ammo can hit asteroids and ships (and even other ammo)
  }

  public List<UFO> explode(Dimension wrapSize)  {
     // System.out.println("Ammo:explode: firing ship is " + firingShip.getClass().getName());
     // System.out.println("Ammo:explode: firing ship activeShots=" + firingShip.getActiveShots());
     firingShip.decrementActiveShots(); 
     return new ArrayList<> ();
  } 

  // must put this in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return Ammo.shipShape;  // as compared to MyShip.shipShape, etc.
  }


 
} // end class Ammo

