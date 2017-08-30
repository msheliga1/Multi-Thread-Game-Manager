/* NewShipTimer class, this debris is used to generate a new ship once it times out.
Subclass of UFO->Debris  
Mike Sheliga 7.29.17
*/

package games.roids;  // Asteroids video game
import java.awt.Dimension;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

// if myShip hits enemy, only me destoyed
// fire speed appears constant.
// if enemy ship hits asteroid, both destroyed

// -------------------------------------------------------------------------------
// Make timers subclass of debris so that they do not crash into any other objects
public class NewShipTimer extends Debris {

   static final int MAX_MODELS = 1;  // different shapes for this object
   static final double MAX_SPEED = 2.0;
   static final int MAX_UPDATES = 20;  // object timeouts after this

   static final Polygon[] shipShape = new Polygon[MAX_MODELS];
   static {  // default NewShipTimer shapes are a line-like polygon
       int[] xPoints = { 0,  100};
       int[] yPoints = { 0,  1};
       shipShape[0] = new Polygon(xPoints, yPoints, xPoints.length);
  }

   // protected int model;                // determines which shape to use
   // protected Point2D.Double location;  // ships position - top left of ship
   // protected Point2D.Double speed;     // changed by up key, auto-decelerates
   // protected double angleInDegrees = 0.0;
   // protected boolean crashed = false;  // on crash delete
   // protected boolean cloaked = false;  // dont draw cloaked Romulan ships or timers
   // protected int updatesLeft;          // some items time out

  // Constructor - set up location, velocity, angle, updates left

  // NewShipTimer Constructor - set up shape, location, velocity - speed random
  public NewShipTimer( )  {
    // System.out.println("Starting NewShipTimer-Ship Constructor");
    cloaked = true;  // NewShipTimer never displays.
    updatesLeft = MAX_UPDATES;
    // System.out.println("Done NewShipTimer-Ship Constructor - updates Left is " + updatesLeft);
  } // End NewShipTimer constructor

  // UFO methods ---------------------------------------
  // move the UFO, wrapping around if necessary
  // public void move(Dimension wrapSize)  {
  // public void accelerate( ) {
  // public void decelerate( ) {
  // public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
  // public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
  // public Point2D.Double getLocation ( ) {
  // public void setLocation (Point2D.Double newLocation) {
  // public double getAngle ( ) {
  // public Double getX ( )  {
  // public Double getY ( )  {
  // public int getXInt ( )  {
  // public int getYInt ( )  {

  // UFO non-get-set methods --------------------

  // public boolean crashedInto(UFO hitItem) {
  // see if two objects CAN crashed into eachother - default is true
  // public boolean canCrashInto(UFO hitItem) {
  // public canTimeout( )  {
  // public List<UFO> explode(Dimension wrapSize)  { // default produces nothing.
  // public void draw(Graphics g, Dimension wrapSize) {
  // public Polygon getShipPoly(Dimension wrapSize) {
  // public Polygon getShipPoly1( ) {

  // explode NewShipTimer - produces a new Ship (centered, no speed) - livesLeft decremented elsewhere
  // shotsActive also set after all ufos are exploded
  @Override
  public List<UFO> explode(Dimension wrapSize)  {
      List<UFO> newUFOs = new ArrayList< >();

      // reset position, acceleration, crashed
      newUFOs.add(new MyShip(wrapSize));
      return newUFOs; 

  } // end explode

  // must put this in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return NewShipTimer.shipShape[model];  // as compared to MyShip.shipShape, etc.
  }


} // end class NewShipTimer

