/* Mikes first swing draw program on new computer 
Main purpose - Get any graphics working - started 7.10
*/

package games.roids;  // Asteroids video game
import java.awt.*;
import java.awt.geom.Rectangle2D; 
import java.awt.Graphics2D;  // needed for affineTransforms
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDateTime;  // New Java8 dateTime classes
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;  // use paintComponent instead of paint
import static java.lang.Math.cos; 
import static java.lang.Math.round;  // to just call round() instead of Math.round()
import static java.lang.Math.sin;
import static java.lang.Math.toRadians; 
import static java.lang.Math.sqrt;

// -------------------------------------------
public abstract class SpaceShip extends UFO {


// inherited instance variables 
// protected int model = 0;            // most UFOs have mutliple shapes
// protected Point2D.Double location;  // ships position - top left of ship
// protected Point2D.Double speed;     // changed by up key, auto-decelerates
// protected double angleInDegrees = 0.0;  // angle ship is pointing (not moving)
// protected boolean cloaked = false;  // Romulans, timers are cloaked
// protected boolean crashed = false;  // on crash, explode then delete
// protected int updatesLeft = 0;      // time left till UFO expires (for ammo, debris)
   protected int activeShots = 0;       // shots fired but not done with

  // SpaceShip methods - overrides of UFO superclass
  // -----------------------------------------------------

  // public void rotateLeft( ) {  // since y axis is down and 90, left rotate => smaller angle
  // public void rotateRight( ) {  // since y axis is down and 90, right rotate => larger angle
  // public Point2D.Double getLocation ( ) {
  // public double getAngle ( ) {
  // public Double getX ( )  {
  // public Double getY ( )  {
  // public int getXInt ( )  {
  // public int getYInt ( )  {
  // SpaceShip non-get-set methods ---------------------------------------
  // public void draw(Graphics g, Dimension wrapSize) {
  // public void update(Dimension wrapSize)  {
  // public void move(Dimension wrapSize)  {
  // public boolean crashedInto(UFO hitItem) {
  // public int getCrashPoints(UFO ufo2) {
  // public int getCrashPoints( ) {
  // public boolean timedOut() {
  // public boolean canTimeout() {
  // public List<UFO> explode(Dimension wrapSize)  {
  // public void decelerate( ) {

  // accelerate UFO - when user hits accelerate button - cap at MAX_SPEED
  public void accelerate( ) {
      super.accelerate();   // use normal accelerate of UFO by 1 total unit. 
      Double angleInRads = toRadians(angleInDegrees);  // static MATH method
      // System.out.println("SpaceShip Accelerate angle-degrees:" + angleInDegrees + " speed " + speed.getX() + ", " + speed.getY());
      // add y below since 90 (down) is same as y-axis in java graphics
      double totalSpeed = sqrt(speed.getX()*speed.getX() + speed.getY()*speed.getY());
      if (totalSpeed > getMaxSpeed()) {
         // System.out.println("SpaceShip:Accelerate: trimming speed of " + totalSpeed);
         speed.setLocation(getMaxSpeed() * cos(angleInRads), getMaxSpeed() * sin(angleInRads));
      }
  }  

  // see if two objects CAN crashed into eachother - default is true
  // many object cant crash into eachother (2 asteroids, debris, etc.)
  @Override
  public boolean canCrashInto(UFO hitItem) {
      if (hitItem instanceof Debris) return ((Debris) hitItem).canCrashInto(this);
      return true;  // ships crash into everything but debris
  }  // end boolean canCrashInto

  // Methods that apply only to ship subclass 
  // ----------------------------------------------------------

  // ---------- Abstract methods ----------------------
  // getFiringPoint - get polygon - then extract index of front of ship
  public abstract Point2D.Double getFiringPoint();

  // each ship type has a maximum number of active shots that can be in the air at once
  // using this.MAX_ACTIVE_SHOTS will not compile here.
  public abstract int getMaxActiveShots();  

  // each ship type has a maximum speed.
  public abstract double getMaxSpeed();

  // fire routines only in ship class (as nothing else can shoot).
  public List<UFO> fire(Dimension wrapSize)  {
    List<UFO> newUFOs = new ArrayList<UFO> ();
    // System.out.println("SpaceShip firing");
    if (activeShots < getMaxActiveShots()) {
        Ammo newAmmo = new Ammo(this, wrapSize);
        newUFOs.add(newAmmo);
        activeShots++;  
        System.out.println("SpaceShip: fire: ActiveShots incremented to " + activeShots);
    }
    return newUFOs;
  }

  public int getActiveShots( ) {
      return activeShots;
  }

  public void setActiveShots(int newShots) {
      try {
         if  (newShots < 0 || newShots > getMaxActiveShots()) {
             String s = "SpaceShip.setActiveShots-Illegal number of shots: " + newShots;
             throw new IllegalArgumentException(s);
         }
         activeShots = newShots;
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      }
  }

  public void decrementActiveShots( ) {
      try {
         int newShots = activeShots - 1;
         if  (newShots < 0 || newShots > getMaxActiveShots()) {
             String s = "SpaceShip.decrementActiveShots-Illegal number of shots: " + newShots;
             throw new IllegalArgumentException(s);
         }
         activeShots = newShots;
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      }
  }


} // end class SpaceShip

