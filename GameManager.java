// Mikes - Main purpose - Have game-manager buttons invoke functions 
// Sub package - 7.12.16
// Buttons for about, hi-score and play.
   
package games;
import games.roids.RoidThread;  //  
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;  // for buttons
// import java.awt.Button;  // old awt button (for testing only)
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets; 
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;  // info, warning, alert msgs 
import javax.swing.JPanel;
import javax.swing.JButton;

public class GameManager implements ActionListener {

   static final int MAX_THREADS = 3;
 
   static int gameCounter = 0;
   // RoidThread[ ] activeThreads = new RoidThread[MAX_THREADS]; // wont work 
   // isAlive only for threads, not runnables 
   ExecutorService executor = null;
   BlockingQueue<Integer> queue = null;  // get msgs from child games -> send to high score processor
   HighScoreProcessor roidScoreProcessor;

   public static void main (String[] args)  {
     new GameManager();   
   } // end main

   // ------------ Constructors ------------------
   // set up the executor service and GUI frames/panels/buttons for the game manager screen
   public GameManager( ) {
     try { 
       executor = Executors.newFixedThreadPool(MAX_THREADS);
       queue = new ArrayBlockingQueue<>(6);
       roidScoreProcessor = new HighScoreProcessor((new RoidThread("Dummy", queue)), queue);
       executor.execute(roidScoreProcessor);      
     } finally { 
       if (executor == null) {
         System.out.println("GameManager HighScoreProcessor finally: executor is null");
       }
     } // end try-finally  
 
     JFrameInsets frame = new JFrameInsets("Mike's Asteroids Game Manager");

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // dont just dispose, exit (cmd tool over)
      frame.getContentPane().setBackground(Color.WHITE);
      frame.setBackground(Color.WHITE);
      frame.setLayout(new FlowLayout());  // Grid or box likely better
      // frame.setLayout(new GridLayout(0, 3, 60, 60)); // anyRows, 3 cols, gaps

      JPanel panel = new JPanel();  
      panel.setLayout(new GridLayout(0, 3, 50, 50));
      panel.setBackground(Color.WHITE);
      frame.add(panel);

      JButton playButton = new JButton("Play Asteroids");
      playButton.setPreferredSize(new Dimension(100, 20));
      playButton.addActionListener(this);
      panel.add(playButton);
      JButton scoreButton = new JButton("High Scores");
      scoreButton.addActionListener(this);
      panel.add(scoreButton);
      JButton aboutButton = new JButton("About Asteroids");
      aboutButton.addActionListener(this);
      panel.add(aboutButton);
      frame.setPreferredSize(new Dimension(600, 150));
      // JButton button4 = new JButton("Exit");
      // frame.add(button4);
      frame.pack();
      frame.setLocationByPlatform(true);  // often makes frame down-right from parent
      frame.setVisible(true);     // show frame on the screen (dont hide it)
      // background black bug even with Panel inside frame
      frame.setResizable(false);  // fix JFrame resize background black bug (Win7)

   }  // end constructor GameButton 

  public void actionPerformed(ActionEvent e) {  // for buttons
    // System.out.println("Action Performed called (button click)");
    Object obj = e.getSource();
    if (obj instanceof JButton) {
      JButton button = (JButton) obj;
      // System.out.println("The buttons label is " + button.getText());  // getLabel deprecated
      if (button.getText().equals("About Asteroids")) {
       String about = "Asteroids by Michael Sheliga.  July 2017 \r\n";
       about += "This asteroids version includes sone basic Swing graphics, NIO2, abstract classes and \r\n";
       about += "is multi-threaded. Games are paused using synchronized wait-notify methods. \r\n";
       about += "It is mainly written to demonstrate the use of various Java techniques.";    
       JOptionPane.showMessageDialog(null, about, "About Asteroids", JOptionPane.INFORMATION_MESSAGE); 
      } else if (button.getText().equals("Play Asteroids")) {
        invokeGameThread("Mikes Game Thread");
      } else if (button.getText().equals("High Scores")) {
        roidScoreProcessor.showHighScores( );
     } // end if-elseif
    } else {
      System.out.println("GameButtons ActionPerformed not from JButton - was a " + obj);
    }
  } // end actionPerformed

  // create a new game thread, but first make sure we haven't exceed the maximum number of apps
  public void invokeGameThread(String name) {
    ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
    long totalTasks = tpe.getTaskCount();  // complete, active and in queue
    long completedTasks = tpe.getCompletedTaskCount(); 
    long uncompletedTasks = totalTasks - completedTasks;  // in queue and active
    
    // System.out.println("invokeGameThread: getTaskCount is " + totalTasks); 
    // System.out.println("invokeGameThread: getCompletedTaskCount is " + completedTasks); 
    if (uncompletedTasks >= MAX_THREADS) {
       // dont count background HighScoreProcessor in the message.
       String msg = "You already have " + (MAX_THREADS-1) + " applications running.\r\n";
       msg = msg + "You must finish one before starting another.";
       String title = "Maximum Application Notice";
       JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
       return;
      }
    try { 
      // must be a thread or subclass to call isAlive, but isAlive wont work - maybe needs start
      // isAlive always returns false - arghh
      // should NOT use a Thread for execute, use runnable only
      RoidThread gameThread = new RoidThread("Mike's Asteroid Thread " + ++gameCounter + " ", queue);
      executor.execute(gameThread);
    } finally { 
      if (executor == null) {
        System.out.println("Invoke Game thread finally: executor already null");
       }
     } // end try-finally 
   } // end invokeGameThread

}  // end class GameButton
  

// ---------------------------------------------
class JFrameInsets extends JFrame {
    private static final long serialVersionUID = 1;

  public JFrameInsets(String name) {
    super(name);
  }

  public JFrameInsets( ) {
    super( );
  }

  @Override
  public Insets getInsets( ) {
    return new Insets(50, 50, 50, 50);
  }  // end getInsets


}  // end class InsetJFrame



