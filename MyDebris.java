/* MyDebris class, such as blown up hmyShip for asteroids game.
Subclass of UFO->Debris  
Mike Sheliga 7.27.17
*/

package games.roids;  // Asteroids video game
import java.awt.Polygon;
import javamjs.awt.MyPolygon;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import static java.lang.Math.cos;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

// if myShip hits enemy, only me destoyed
// fire speed appears constant.
// if enemy ship hits asteroid, both destroyed

// -------------------------------------------
public class MyDebris extends Debris {

   static final int MAX_MODELS = 4;  // different shapes for this object
   static final double MAX_SPEED = 1.5;
   static final int MAX_UPDATES = 15;  // object timeouts after this

   static final Polygon[] shipShape = new Polygon[MAX_MODELS];
   static {  // default MyDebris shapes are a line-like polygon
       int[] xPoints = { 0,  10,   10,   0};
       int[] yPoints = { 0,  0,     1,   1};
       int nPoints = xPoints.length;
       shipShape[0] = new Polygon(xPoints, yPoints, nPoints);
       int[] xPoints1 = { 0,  0,    1,   1};
       int[] yPoints1 = { 0,  16,  16,   0};
       shipShape[1] = new Polygon(xPoints1, yPoints1, xPoints1.length);
       int[] xPoints2 = { 0,  -14,   -14,    0};
       int[] yPoints2 = { 0,  -14,   -15,   -1};
       shipShape[2] = new Polygon(xPoints2, yPoints2, xPoints2.length);
       int[] xPoints3 = { 0,  2,   2,    0};
       int[] yPoints3 = { 0,  0,   1,    1};
       shipShape[3] = new Polygon(xPoints3, yPoints3, xPoints3.length);   }

   // protected int model;                // determines which shape to use
   // protected Point2D.Double location;  // ships position - top left of ship
   // protected Point2D.Double speed;     // changed by up key, auto-decelerates
   // ship points North originally, which is -y in Java => 270 degs
   // protected double angleInDegrees = 0.0;
   // protected boolean crashed = false;  // on crash delete
   // protected boolean cloaked = false;  // dont draw cloaked Romulan ships
   // protected int updatesLeft;          // some items time out


  // Constructor - set up location, velocity, angle, updates left

  // MyDebris Constructor - set up shape, location, velocity - speed random
  public MyDebris(MyShip myShip, Dimension wrapSize)  {
    // System.out.println("Starting MyDebris-Ship Constructor");

    model = (int) (random() * (MAX_MODELS - 1)); 
    // convert Polygon to MyPolygon to be able to use getCenter method.
    Point2D.Double center = (new MyPolygon(myShip.getShipPoly())).getCenter(); 
    location = new Point2D.Double(center.getX(), center.getY());
    double moveAngle = toRadians(random() * 360.0);
    speed = new Point2D.Double(MAX_SPEED * random() * cos(moveAngle), 
                               MAX_SPEED * random() * sin(moveAngle)); 
    angleInDegrees = 0.0;  // MyDebris always faces same direction.
    updatesLeft = MAX_UPDATES;
    // System.out.println("Done MyDebris-Ship Constructor - updates Left is " + updatesLeft);
  } // End MyDebris constructor

  // UFO methods ---------------------------------------
  // public void update(Dimension wrapSize)  {
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


  // must put this in a function since shipShape is a class variable, overriding cant happen
  public Polygon getShape() {
      return MyDebris.shipShape[model];  // as compared to MyShip.shipShape, etc.
  }

} // end class MyDebris

