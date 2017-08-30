/* Asteroid class for asteroids video game
 - started 7.24.17 - MJS - most routines are inherited from UFO class
*/

package games.roids;  // Asteroids video game

// -------------------------------------------
public abstract class Asteroid extends UFO {

   // inherited instance variables 
   // protected Point2D.Double location;   // ships position - top left of ship
   // protected Point2D.Double speed;      // changed by up key, auto-decelerates
   // protected double angleInDegrees;     // direction ship is pointing (to shoot, accelerate)

  // Asteroid methods ---------------------------------------
  // see if two objects CAN crashed into eachother - default is true
  // 2 asteroids cant crash into eachother 
  // must use UFO below to override (not overload) UFO call
  @Override
  public boolean canCrashInto(UFO hitItem) {
      if (hitItem instanceof Debris) return ((Debris) hitItem).canCrashInto(this);
      if (hitItem instanceof Ammo) return ((Ammo) hitItem).canCrashInto(this);
      if (hitItem instanceof Asteroid) return false;
      return true;
  }  // end boolean canCrashInto

  // public List<UFO> explode(Dimension wrapSize)  {
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

} // end class Asteroid

