/* Debris class for asteroids video game
 - started 7.27.17 - MJS - most routines are inherited from UFO class
*/

package games.roids;  // Debriss video game

// -------------------------------------------
public abstract class Debris extends UFO {

   // inherited instance variables 
   // protected int model;                 // many ufos have different models (for drawing)
   // protected Point2D.Double location;   // ships position - top left of ship
   // protected Point2D.Double speed;      // changed by up key, auto-decelerates
   // protected double angleInDegrees;     // direction ship is pointing (to shoot, accelerate)
   // protected int updatesLeft;           // some ufos timeout (debris, ammo)

  // Debris methods ---------------------------------------
  // see if two objects CAN crashed into eachother - default is true
  // 2 asteroids cant crash into eachother 
  // must use UFO below to override (not overload) UFO call
  @Override
  public boolean canCrashInto(UFO hitItem) {
      return false;  // debris never crashes into anything in this game
  }  // end boolean canCrashInto
 
  // MyDebris can time out (ammo, debris do, others dont).
  public boolean canTimeout() {
      return true;
  }  // end boolean canTimeout

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

} // end class Debris

