/* Class implements a UFO space ship that can move and fire, for asteroids game. 
 * First swing draw program on new computer 
 * Main purpose - Get any graphics working - started 7.10
*/

package games.roids;  // Asteroids video game
import java.awt.*;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
 
// protected int model = 0;            // most UFOs have mutliple shapes
// protected Point2D.Double location;  // ships position - top left of ship
// protected Point2D.Double speed;     // changed by up key, auto-decelerates
// protected double angleInDegrees = 0.0;  // angle ship is pointing (not moving)
// protected boolean cloaked = false;  // Romulans, timers are cloaked
// protected boolean crashed = false;  // on crash, explode then delete
// protected int updatesLeft = 0;      // time left till UFO expires (for ammo, debris)
// SpaceShip instance variables
// protected int activeShots = 0;       // shots fired but not done with
 
  // ---------- UFO methods -----------------------------------
  // accelerate UFO - when user hits accelerate button
  // public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
  // public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
  // public Point2D.Double getLocation ( ) {
  // public double getAngle ( ) {
  // public Double getX ( )  {
  // public Double getY ( )  {
  // public int getXInt ( )  {
  // public int getYInt ( )  {
  // MyShip non-get-set methods ---------------------------------------
  // public void draw(Graphics g, Dimension wrapSize) {
  // public void update(Dimension wrapSize)  {
  // public void move(Dimension wrapSize)  {
  // public boolean crashedInto(UFO hitItem) {
  // public boolean canCrashInto(UFO hitItem) {
  // public int getCrashPoints(UFO ufo2) {
  // public int getCrashPoints( ) {
  // public boolean timedOut() {
  // public boolean canTimeout() {
  // ----------- SpaceShip methods ----------------------------
  //  public int getActiveShots( ) {
  //  public void setActiveShots(int newShots) {
  //  public void decrementActiveShots( ) {


// -------------------------------------------
public class MyShip extends SpaceShip {

   static final int MAX_MODELS = 1;
   static final double MAX_SPEED = 10.0;
   static final int MAX_ACTIVE_SHOTS = 4;
   static final int SHOOT_INDEX = 2;  // shots fired originate at this vertex

   static final Polygon[] shipShapes = new Polygon[MAX_MODELS]; 
   static {
       int[] xPoints = {10, 0, 30,   0, 10};
       int[] yPoints = {10, 0, 10,  20, 10};
       int nPoints = xPoints.length;
       shipShapes[0] = new Polygon(xPoints, yPoints, nPoints);
   }

   // inherited instance variables 
   // protected Point2D.Double location;   // ships position - top left of ship
   // protected Point2D.Double speed;      // changed by up key, auto-decelerates
   // protected double angleInDegrees;     // direction ship is pointing (to shoot, accelerate)
   // protected int activeShots = 0;          // shots fired but not done with

  // MyShip Constructor - set up shape, location, velocity
  public MyShip(Dimension wrapSize)  {
    // System.out.println("Starting MyShip Constructor with wrapSize");
    location = new Point2D.Double(wrapSize.getWidth()/2.0, wrapSize.getHeight()/2.0);
    speed = new Point2D.Double(0.0, 0.0);
    angleInDegrees = 270.0;  // ship points North originally, which is -y in Java => 270 degs
    // System.out.println(" Done MyShip Constructor with wrapSize");
  } // End MyShip constructor

  // myShip methods - overrides of UFO superclass
  // -----------------------------------------------------

  // explode myShip - produces a new Ship (centered, no speed) - livesLeft decremented elsewhere
  // shotsActive also set after all ufos are exploded
  @Override
  public List<UFO> explode(Dimension wrapSize)  {
      List<UFO> newUFOs = new ArrayList< >();

      // System.out.println("Exploding crashed ship");
      // reset position, acceleration, crashed
      // add 4 random pieces of debris (will be at random locations)
      newUFOs.add(new MyDebris(this, wrapSize));  
      newUFOs.add(new MyDebris(this, wrapSize));
      newUFOs.add(new MyDebris(this, wrapSize));
      newUFOs.add(new MyDebris(this, wrapSize));
      newUFOs.add(new NewShipTimer());  // when this timesout, new myShip is created
      return newUFOs; 
  } // end explode


  // this is to slowly slow-down the ship
  @Override
  public void decelerate( ) {
      speed.setLocation(speed.getX() * 0.98, speed.getY() * 0.98);
  }

  // this must be in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return MyShip.shipShapes[model];  // as compared to Asteroid.shipShape, etc.
  } 


  // Methods that apply only to ship subclass 
  // ----------------------------------------------------------

  // ------------- Abstract methods from SpaceShip parent class ---------------------
  // getFiringPoint - get polygon - then extract index of front of ship
  public Point2D.Double getFiringPoint( ) {
      Polygon movedShip = getShipPoly();                // ship moved and rotated
      // System.out.println("getFiringPoint SHOOT-INDEX is " + SHOOT_INDEX);
      return new Point2D.Double(movedShip.xpoints[SHOOT_INDEX], movedShip.ypoints[SHOOT_INDEX]);    
  } // end UFO getFiringPoint


  public int getMaxActiveShots( ) {
      return MAX_ACTIVE_SHOTS;    
  } // end UFO getMaxActiveShots

  public double getMaxSpeed( ) {
      return MAX_SPEED;    
  } // end UFO getMaxSpeed

  // ----------- non-abstract methods that apply only to ships
  // fire routines only in ship classes (as nothing else can shoot).
  public List<UFO> fire(Dimension wrapSize)  {
    List<UFO> newUFOs = new ArrayList<UFO> ();
    // System.out.println("MyShip firing");
    if (activeShots < MAX_ACTIVE_SHOTS) {
        Ammo newAmmo = new Ammo(this, wrapSize);
        newUFOs.add(newAmmo);
        activeShots++;
    }
    return newUFOs;
  }


} // end class MyShip

