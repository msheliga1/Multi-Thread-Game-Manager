/* Asteroid Game - Similar to arcade game, but with more concurrency
 * Mikes first swing draw program on new computer 
 * started 7.10.17
*/ 

package games.roids;
import games.HighScoreClassInfo;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;  // New Java8 dateTime classes
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;   
import java.util.List;   
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.*;  // use paintComponent instead of paint
import static java.lang.Math.min;

// ---------------------------------------------------------------------------------------------
public class RoidThread implements Runnable, HighScoreClassInfo {

    static final int SLEEP_TIME = 30;           // sleep time in main run loop
    static final int MAX_GAME_TIME = 600000;    // exit game after this number of millisecs
    static final String HIGH_SCORE_FILENAME  = "RoidHighScores.txt";
    static final int BONUS_LIFE_POINTS = 1000;  // bonus life every this number of pts

    private String threadName;
    private BlockingQueue<Integer> queue;  // pass score to invoking routine
    private RoidsGUI roidGUI;  // game frame window with panels, buttons, etc.
    private boolean threadSuspended = false;
    private long updates = 0;  // times main loop has run
    private boolean windowClosing = false; 

    // Variables used to display graphics in outer space panel (Not outter!)
    private MyShip myShip;  // this also goes into the UFO list ... need myShip for key input (accelerate, rotate, etc.)
    private List<UFO> ufos = new ArrayList< >();
    // for new UFOs (ammo) requested by keyboard input - must be synchronized or caused crashes
    private List<UFO> newKeyUFOs = new ArrayList<>();  

    // values used to display info in message panel
    private int gameLevel = 1;      // each screen equates to a game level
    private int livesLeft = 3;      // 3 lives to start with
    private StringBuilder panelMessage = new StringBuilder("");
    private int score = 0;  
 

    // ------------ RoidThread Main --------------------------
    public static void main(String[] args)  {
      BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1); // only 1 thread ever here.
      new RoidThread("Mikes Asteroid Standalone Game", queue);	
    }  // End main

    // ------------ RoidThread Constructors --------------------------
    public RoidThread(String s, BlockingQueue<Integer> inQueue) {
      threadName = s;
      queue = inQueue;
    }

    // ------------ getHighScoreClassInfo interface methods -------------------------
    public String getHighScoreFileName( ) {
        return HIGH_SCORE_FILENAME;
    }

    // ------- RoidThread methods ---------------------------------
    public void run( ) {
      roidGUI = new RoidsGUI(threadName);
      // cant properly space new asteufos/ship until roidGUI declared since no inner class access to statics
      initializeGameLevel();
      while (!gameOver()) {
        mySleep(SLEEP_TIME);
        // Update game values and redraw screen
        roidUpdate();   // decelerate, move, seeIfCrashed, explode, delete crashes, timeouts
        // do a suspendPoll check here - enter wait if threadSuspended is true
        pollSuspend( );  // poll threadSuspended, enter wait state if so
      } // end main run while loop 
      roidGUI.dispose();  // get Rid of GUI frame Window
      try {
          if (!windowClosing) queue.put(score);   // pass score to parent, so it might be recorded.
      } catch (InterruptedException e) {
          // System.out.println("Roid Game interrupted while trying to post score.");
          Thread.currentThread().interrupt();
      } 
    } // end run

    // sleep routine to clean up normal sleep try-catch 
    public void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }  // end try-catch
    } // end mySleep

    public void pollSuspend( ) {
        // System.out.println("Entering pollSuspend with threadSupsended of " + threadSuspended); 
        try {
          // this below refers to roidThread - want it to refer to game frame for use with frame getFocus
          // need to synch on same object as notify which is in RoidFrame.getFocus()
          if (threadSuspended) {
            synchronized(roidGUI) {  // Illegal monitor exception without synchronized wait call
              while (threadSuspended) { 
                roidGUI.wait(); 
              } // end while
            } // end synchronized block
          } // end if threadSuspended
        } catch (InterruptedException e) {
          // System.out.println("Roid Thread interrupted while waiting."); 
          e.printStackTrace();
        } // end try-catch
    } // end pollSuspend

    public boolean gameOver( )  {
        // System.out.println("Startin gameOver in class Roids");  // updates > MAX_GAME_TIME/SLEEP_TIME
        if (windowClosing || livesLeft <= 0) {
            return true;
        } else {
            return false;
        }
    } // End gameOver

    public boolean levelOver( )  {
        // System.out.println("Starting levelOver in class Roids");
        if (ufos.size()==0 || (ufos.size() == 1  && (ufos.get(0) instanceof MyShip))) {
            return true;
        } else {
            return false;
        }
    } // End levelOver

    public void initializeGameLevel() {
      // System.out.println("Beginning RoidThread initGameLevel " + gameLevel);
      ufos = new ArrayList< >();
      myShip = new MyShip(roidGUI.getSpaceDimensions());
      ufos.add(myShip);
      int roidsToAdd = min(8, 2 + (int) (1.0 * gameLevel));
      for (int i=1; i <= roidsToAdd; i++) {
        ufos.add(new AsteroidBig(roidGUI.getSpaceDimensions()));
      }
      // System.out.println("Ending RoidThread initGameLevel " + gameLevel);
    } // end run

    // Update asteroid game (location of all objects) and repaint screen
    public void roidUpdate ( )  {
      List<UFO> newUFOs = new ArrayList< >();
      Dimension wrapSize = roidGUI.getSpaceDimensions(); 

      try {
        // System.out.println(" Roid Update in class RoidThread");
        updates++; 

        if (levelOver()) {
            gameLevel++;
            score += 100;
            initializeGameLevel();
        }
        if (myShip == null) System.out.println("MyShip null in roidUpdate --------------------");
        for (UFO ufo: ufos) {
            ufo.update(wrapSize); // decelerate, move, decrement updatesLeft 
            // dont add a new ship as long as asteroid near center of screen
            if ((ufo instanceof NewShipTimer) && ufo.timedOut() && screenCenterFilled(wrapSize)) {
                ufo.addToUpdatesLeft(1);
            }           
        } // end for all asteufos
        for (int ind1 = 0; ind1 < ufos.size()-1; ind1++) {  // skip final indice
            for (int ind2 = ind1+1; ind2 < ufos.size(); ind2++) { 
                UFO ufo1 = ufos.get(ind1);
                UFO ufo2 = ufos.get(ind2);
                if (ufo1.crashedInto(ufo2, wrapSize)) {
                    String s = "UFO " + ind1 + " " + ufo1.getClass().getName();
                    // System.out.println(s + " crashed into UFO " + ind2 + " " + ufo2.getClass().getName());
                    recordCrash(ufo1, ufo2); 
                } // end if
            }  // end for UFO index 2           
        } // end for UFO index 1
        // explode crashed and timed-out items, often generating new items
        for (UFO ufo: ufos) {
            if (ufo.crashed || ufo.timedOut()) { 
                // System.out.println("Exploding " + ufo.getClass().getName());
                newUFOs.addAll(ufo.explode(roidGUI.getSpaceDimensions()));
            } // end if crashed or timedout            
        } // end for all ufos

        ufos.removeIf((ufo)-> ufo.crashed || ufo.timedOut());     // remove all crashed and timedOut items

        // add new asteufos, debris, etc. to roid list.
        // combine UFOs from explosions/timeouts with new UFOs (ammo) requested by keyboard input
        synchronized (newKeyUFOs) {  
            newUFOs.addAll(newKeyUFOs);
            newKeyUFOs = new ArrayList<>();
        } 
        for (UFO ufo: newUFOs) {
            // System.out.println("Adding new ufo of type " + ufo.getClass().getName());
            ufos.add(ufo);
            if (ufo instanceof MyShip) { 
                // 7.29.17 Ammo now points to firingShip - this new Ship has new activeShots counter
                myShip = (MyShip) ufo; ;
                livesLeft--;
            }
        } // end for all asteufos

        // calls paint which calls paintComponent - automatically paints child components (space, msg panels)
        roidGUI.repaint();
     } catch (NullPointerException e) {
        System.out.println("Null ptr exception in roidUpdate.");
        e.printStackTrace();
     }
    } // End roidUpdate

    // check if center of screen is filled by an an asteroid.
    public boolean screenCenterFilled(Dimension wrapSize) {
        Rectangle2D rect2D;
        double xMin, xMax, yMin, yMax;
        Polygon asteroid;

        // opening size need in middle of playing area
        final double OPEN_PCT = 20.0;  // fairly small per original game
        final double MIN_PCT = 50.0 - OPEN_PCT/2.0;
        final double MAX_PCT = 50.0 + OPEN_PCT/2.0;
        final double MIN_XPIXEL = wrapSize.getWidth() * MIN_PCT / 100.0; 
        final double MAX_XPIXEL = wrapSize.getWidth() * MAX_PCT / 100.0;
        final double MIN_YPIXEL = wrapSize.getHeight() * MIN_PCT / 100.0;
        final double MAX_YPIXEL = wrapSize.getHeight() * MAX_PCT / 100.0;

        for (UFO ufo: ufos) {
            if (ufo instanceof Asteroid) {
                asteroid = ((Asteroid) ufo).getShipPoly();
                rect2D = asteroid.getBounds();
                xMin = rect2D.getX();
                xMax = xMin + rect2D.getWidth();
                yMin = rect2D.getY();
                yMax = yMin + rect2D.getHeight();

                if ((xMin < MAX_XPIXEL && xMax > MIN_XPIXEL) && 
                    (yMin < MAX_YPIXEL && yMax > MIN_YPIXEL))  {
                    return true;
                }       
            }
        } // end for all asteroids
        return false;
    }

    // 2 objects have crashed, update score, livesLeft and record the crash in the UFO objects
    public void recordCrash(UFO ufo1, UFO ufo2)  {
        int oldScore = score;
        score += ufo1.getCrashPoints(ufo2); 
        if ((score / BONUS_LIFE_POINTS) != (oldScore / BONUS_LIFE_POINTS)) {
            livesLeft++;
        }
        ufo1.crashed = true;
        ufo2.crashed = true;  // not enemyShip-myShip in original game 
    } // End recordCrash


  // ---------------------------------------------------------------------------------------------------
  // ---------------------- Inner class RoidsGUI -------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------
  class RoidsGUI extends JFrame implements FocusListener, WindowListener { // inner class

	static final long serialVersionUID = 1;
    static final int SPACE_WIDTH = 600; // frame width for game, "SPACE" => outter space graphics area 
    static final int SPACE_HEIGHT = 600;  
    static final int TEXT_HEIGHT = 50;  // Message panel at bottom of game

    private RoidPanel space;         // panel that represents outer space
    private RoidStatusPanel status;  // panel for score, level, lives left, msgs, etc. 

    // this method cannot be static since innerClass is associated with specific outer class instance
    public Dimension getSpaceDimensions() { 
        return new Dimension(SPACE_WIDTH, SPACE_HEIGHT);
    }

    // RoidsGUI Constructors ---------------------------------------------------------
    // RoidsGUI Constructor - set up frame and space and text panels
    public RoidsGUI(String name)  {
        super(name);

	// frame = new RoidFrame("Mike's Asteufos - Game " + name);  
	// Using X to close frame -> must exit program.
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Dispose doesnt register with Executor Service
	setLocation(300, 50); // locn - overall locn on screen (top left)			
        setLayout(new BorderLayout());   // default is bordered-centered for frames anyways        
        
        Container c = this.getContentPane();
        c.setPreferredSize(new Dimension(SPACE_WIDTH, SPACE_HEIGHT + TEXT_HEIGHT));
        pack();
        setVisible(true);     // put visible after resizable and wrong 610x610
        setResizable(false);  // put this after set visible and correct sizes

        // System.out.println("Frames size is " + this.getBounds().height + ", " + this.getBounds().width);    
        // System.out.println("Frames content size is " + getContentPane().getSize());

        // For border, edge element sizes override center (center gets what is left)
	// add panels and text areas for the actual graphics ---------------
	space = new RoidPanel();  // sPanel = spacePanel (for drawing)
        space.setPreferredSize(new Dimension(SPACE_WIDTH, SPACE_HEIGHT));
	space.setBackground(Color.BLACK);
        getContentPane().add(space, BorderLayout.PAGE_START);
        // status.setSize(new Dimension(600, 100));
        status = new RoidStatusPanel();
        status.setPreferredSize(new Dimension(SPACE_WIDTH, TEXT_HEIGHT));
	status.setBackground(Color.WHITE);
        JLabel label = new JLabel("Mikes Label Version3");
        label.setSize(new Dimension(122, 16));
        status.add(label, BorderLayout.CENTER);
        getContentPane().add(status, BorderLayout.CENTER);

        pack();
        setResizable(false);  
	setVisible(true);  // shows frame on screen  --- 
        // space.repaint(); // calls paint which calls paintComponent - myShip not yet set

        // Add event listeners and key bindings
        addFocusListener(this);  // to suspend/resume program when frame loses/gains focus
        addWindowListener(this); // to help close window properly
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
        this.getRootPane().getActionMap().put("left", new LeftAction());
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
        this.getRootPane().getActionMap().put("right", new RightAction());
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "speedup");
        this.getRootPane().getActionMap().put("speedup", new UpAction());
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "fire");
        this.getRootPane().getActionMap().put("fire", new SpaceAction());

        // frame sizes only exists after set visible
        // System.out.println("------------------------------------------------------------");
        // System.out.println("Frames size is " + getBounds().height + ", " + getBounds().width);
        // System.out.println("Frames content size is " + getContentPane().getSize());
    } // End RoidsGUI(String) constructor


    // RoidsGUI No argument Constructor - call string constructor
    public RoidsGUI()  {
        this("");  // call constructor(String) with blank string
    } // End RoidsGUI(String) constructor


    // RoidsGUI Event Handlers -----------------------------------------------------------
    // FocusListener Event Handlers (focusGained, focusLost) -----------------------------
    @Override  // method synchronized - needed for notify
    public synchronized void focusGained(FocusEvent e) {
      if (threadSuspended) {  
        // System.out.println("RoidGUI - unSuspendingThread - notifying."); 
        threadSuspended = false;
        notify();
      } // end if threadSuspended
    }  // end FocusGained

    @Override
    public synchronized void focusLost(FocusEvent e) {
      threadSuspended = true; // signal thread to enter wait state
    }  // end FocusLlost

    // WindowListener Event Handlers ------------------\
    // (Activated, Closed, Closing, Deactivated, Deiconified, Iconified, Opened) -----------------------------
    @Override
    public synchronized void windowActivated(WindowEvent e) {
      // System.out.println("RoidGUI - Window Activated.");
    }

    @Override
    public synchronized void windowClosed(WindowEvent e) {
      // System.out.println("RoidGUI - Window Closed.");
    }

    @Override
    public synchronized void windowClosing(WindowEvent e) {
      // System.out.println("RoidGUI - Window Closing.");
      // Using DISPOSE_ON_CLOSE doesnt register with ExecutorService Active Processes count
      windowClosing = true;  // signals gameOver function, which makes run stop
    }

    @Override
    public synchronized void windowDeactivated(WindowEvent e) {
      // System.out.println("RoidGUI - Window Deactivated.");
    }

    @Override
    public synchronized void windowDeiconified(WindowEvent e) {
      // System.out.println("RoidGUI - Window Deinconified.");
    }

    @Override
    public synchronized void windowIconified(WindowEvent e) {
      // System.out.println("RoidGUI - Window Iconified.");
    }

    @Override
    public synchronized void windowOpened(WindowEvent e) {
      // System.out.println("RoidGUI - Window Opened.");
    }  

  // Inner classes for keyboard presses ----------------
  class LeftAction extends AbstractAction {

      static final long serialVersionUID = 1;
	  public LeftAction() {
      } 

      @Override
      public void actionPerformed(ActionEvent e) {
          // System.out.println("LeftAction: action performed - rotateLeft " + myShip.getAngle());
          if (!ufos.contains(myShip)) return; // dont change destroyed ships
          myShip.rotateLeft();
      } // end actionPerformed
  } // end class LeftAction

  // Inner class for keyboard presses ----------------
  class RightAction extends AbstractAction {

      static final long serialVersionUID = 1;
	  public RightAction() {
      } 

      @Override
      public void actionPerformed(ActionEvent e) {
          // System.out.println("RightAction: action performed - rotateRight " + myShip.getAngle());
          if (!ufos.contains(myShip)) return; // dont change destroyed ships
          myShip.rotateRight();
      } // end actionPerformed
  } // end class RightAction


  // Inner class for keyboard presses ----------------
  class UpAction extends AbstractAction {

      static final long serialVersionUID = 1;
	  public UpAction() {
          // System.out.println("UpAction: running constructor");
      } 

      @Override
      public void actionPerformed(ActionEvent e) {
          // System.out.println("UpAction: action performed - accelerating");
          if (!ufos.contains(myShip)) return; // dont change destroyed ships
          myShip.accelerate();
      } // end actionPerformed
  } // end class UpAction


  // Inner class for keyboard presses ----------------
  class SpaceAction extends AbstractAction {

      static final long serialVersionUID = 1;
	  public SpaceAction() {
      } 

      @Override
      public void actionPerformed(ActionEvent e) {
          // System.out.println("SpaceAction: action performed - firing");
          if (!ufos.contains(myShip)) return; // dont change destroyed ships
          synchronized (newKeyUFOs) {
              // System.out.println("SpaceAction: synch(roid) action performed - firing");
              newKeyUFOs.addAll(myShip.fire(roidGUI.getSpaceDimensions()));
          }
      } // end actionPerformed

  } // end class UpAction
    // RoidsGUI methods ------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------------
    // Inner class - asteroids space panel (not for text as paintComponent specific to outer space).
    class RoidPanel extends JPanel {

      static final long serialVersionUID = 1;
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        // myShip null until outer space size known  
        for (UFO roid: ufos) {
           if (roid == null) {
               System.out.println("RoidSpacePanel: roid is null! \r\n");
           } else {           
               roid.draw(g, new Dimension(SPACE_WIDTH, SPACE_HEIGHT));
           }
        } // end for all asteufos

      }  // end paintComponent

    }  // end class RoidPanel


    // --------------------------------------------------------------------------------------------------
    // asteufos message panel (for score, level, lives left, text msgs, etc.).
    class RoidStatusPanel extends JPanel {
      static final long serialVersionUID = 1;
    	
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.drawString("StatusPanel size=" + getSize().width + ", " + getSize().height, 20, 20); 
        g.drawString("Score: " + score + " Lives Left: " + livesLeft + " Level " + gameLevel, 420, 20);
      }  // end paintComponent

    }  // end class RoidStatusPanel

  } // end class RoidsGUI

}  // end class RoidThread

