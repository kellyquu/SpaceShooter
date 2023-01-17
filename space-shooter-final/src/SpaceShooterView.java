//Import the built-in Java classes needed for this class:
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/* The SpaceShooterView Class
 * PURPOSE: display the game page
 * PRE: a SpaceShooterView object must be created in the SpaceShooterStart class and added to a JFrame window
 * POST: displays the game page & main menu; runs the actual game
 */
public class SpaceShooterView extends JPanel implements KeyListener
   
{
	// Declare & initialize the Sounds objects:
	Sounds shoot = new Sounds("sounds_shoot.wav");	
	
	// Declare the image objects:
	private Image spaceshipImage ;
	private Image hostileshipImage;
	private Image offImage;
	private Graphics offGraphics;
	
	// Declare & initialize game background color 
	static final java.awt.Color BG_COLOR = java.awt.Color.black;  
	
	final public SpaceShooter  spaceShooter; //Declare spaceShooter object
	
	/*The *SpaceShooterView* Constructor Method
	  PURPOSE: to initialize a newly-created object of the SpaceShooterView class
	  PRE: N/A; called whenever a new object of the SpaceShooterView class is instantiated
	  POST: initializes the newly-created SpaceShooterView object
	 */
	public SpaceShooterView(SpaceShooter  spaceShooter){	//for the data, state, and control for the game
		this.spaceShooter = spaceShooter;
		spaceShooter.spaceShooterView = this;

		loadImages();         //load the spaceship image from SpaceShip.png 
		addKeyListener(this); //create KeyListener object that listens & responds to keyboard events of the current class/object
	}
	
	@Override 
	public Dimension getPreferredSize() {
		return new Dimension(SpaceShooter.ScreenWidth,SpaceShooter.ScreenHeight);
	}
	
	/*The *update* Method
	  PURPOSE: convert data into image
	  PRE: Graphics g
	  POST: N/A
	 */
	@Override
	public void update(Graphics g) 
	{
        paint(g);
    }
	
	/*The *loadImages* Method
	  PURPOSE: to load the image files required for the game
	  PRE: the image files must be located
	  POST: loads the required image files
	 */
	private void loadImages() {
	    //Declare & load the ImageIcon objects, specify the relative paths that can be used to retrieve the images:
		spaceshipImage = new ImageIcon("SpaceShip.png").getImage();
		hostileshipImage = new ImageIcon("HostileShip.png").getImage(); 
	}
	
	/* The *rerenderView* Method
	 * PURPOSE: repaint while the ships and bullet move, causing the window images to change
	 * PRE: N/A
	 * POST: N/A 
	 */
	public void rerenderView() {
		paint(this.getGraphics());
	}

	/* The *paint* Method
	 * PURPOSE: to draw the game interfaces
	 * PRE: Graphics g
	 * POST: N/A
	 */
	@Override 
	synchronized public void paint(Graphics g) 
	{
		// create a buffer for image storage
		if(g==null )
			return;
		if( offImage==null ) {
			offImage = createImage(SpaceShooter.ScreenWidth,SpaceShooter.ScreenHeight);
			offGraphics = offImage.getGraphics();
		}
		// to avoid screen flashing, save images to the buffer first
		// render buffer images
		offGraphics.setColor(BG_COLOR); // set the background colour
		offGraphics.fillRect(0, 0, SpaceShooter.ScreenWidth,SpaceShooter.ScreenHeight);
        offGraphics.setFont(new java.awt.Font(Font.DIALOG,Font.PLAIN,18)); // customize font
		if( spaceShooter.state==0 ) {
 			// customize main menu
			offGraphics.setColor(Color.lightGray);
			final int x0 = 100, y0 = 150;
			offGraphics.fillRect(x0, y0, SpaceShooter.ScreenWidth-x0*2, SpaceShooter.ScreenHeight-y0*2);
			offGraphics.setColor(Color.BLUE);
			offGraphics.drawRoundRect(x0, y0, SpaceShooter.ScreenWidth-x0*2, SpaceShooter.ScreenHeight-y0*2,10,10);
			offGraphics.setColor(Color.BLACK);
			int y = y0+50;
			if( spaceShooter.winState!=0 ) { //results message displayed after winState changes 
				offGraphics.setColor(Color.RED);
				offGraphics.drawString((spaceShooter.winState>0?"     You win":"     You lost")
						+"!  score = "+spaceShooter.getScore()
						+",  number of hostile ships destroyed = "+spaceShooter.getKilledHostileShips()
						+",  play time = "+spaceShooter.getPlayTime()+" seconds", x0+10, y); y += 50;
			}
			// store main menu
			offGraphics.setColor(Color.BLACK);
			offGraphics.drawString(" [1] Press ENTER to start game.", x0+10, y); y += 50;
			offGraphics.drawString(" [2] Instructions:", x0+10, y);  y += 30;
			offGraphics.drawString("      Use the arrow keys to move space ship left, right, up, and down.", x0+10, y); y += 20;
			offGraphics.drawString("      Press the SPACE bar to shoot at the hostile ships.", x0+10, y);y += 20;
			offGraphics.drawString("      Press the ESC key to restart the game.", x0+10, y); y += 20;
			offGraphics.drawString("      Press the 0 key to exit the window.", x0+10, y); y += 20;
		} else 
		{
			// store progress message to be displayed while in-game
			offGraphics.setColor(Color.RED);
			offGraphics.drawString("# of enemy ships destroyed = "+spaceShooter.getKilledHostileShips(),SpaceShooter.ScreenWidth-300,40);
		}
		// draw the spaceship (the spaceship is an image, copy the image to the location defined by the spaceshipX/spaceshipY):
		if( spaceShooter.winState>=0 ) {
		  offGraphics.drawImage(spaceshipImage, spaceShooter.spaceshipX, spaceShooter.spaceshipY,null);
		}
		// draw the hostile ships： the alive ships are stored in an array, repeatedly put each hostile ship into the screen buffer. 
		final int hostileShipsCount = spaceShooter.getHostileShipsCount();
		 for(int i=0;i<hostileShipsCount;i++) {
			 if( !spaceShooter.hostileShips[i].killed) {
				 offGraphics.drawImage(hostileshipImage,spaceShooter.hostileShips[i].x,spaceShooter.hostileShips[i].y,null);
			 }
		 }
		 // draw the bullets, bullets are stored in a list.
         offGraphics.setFont(new java.awt.Font(Font.DIALOG,Font.PLAIN,48)); // customize font
		 for(int i=0;i<spaceShooter.shoots.length;i++) {
			 SpaceShooter.Shoot s = spaceShooter.shoots[i];
			 if( s.onscreen ) {
				 offGraphics.setColor(s.dirY>0 ?  Color.BLUE : Color.RED );
				 offGraphics.drawString("*",s.x,s.y);
			 }
		 }
		// display buffer images
		g.drawImage(offImage,0,0,this);
	}
	
	/*The *keyTyped* Method 
	  PURPOSE: Checks for the characters being pressed on keyboard
	  PRE: N/A
	  POST: key code pressed
	*/  
	@Override
	public void keyTyped(KeyEvent e) {
		final int state = spaceShooter.state;
		final char keyChar = e.getKeyChar(); 
		if( state==0 && (keyChar==KeyEvent.VK_ENTER  ) ) { // when state == 0,  the game has not started yet)
			spaceShooter.start(); // called on after pressing 'ENTER'
		} else if(  state==1  ) {
			if( keyChar==27 ) {  // check if key pressed is 'ESC'
				spaceShooter.stop(0); //stops game 
			} else if( keyChar==KeyEvent.VK_SPACE) { // check if key pressed is 'SPACE'
				spaceShooter.shoot(); // shoot bullets
				shoot.openSound(); //play shooting sound
			    shoot.playSound();
			}
		}
	}

	/*The *keyPressed* Method
	  PURPOSE: to detect key presses & execute the desired action(s) associated with that key press
	  PRE: N/A
	  POST: executes the desired action(s)--the action(s) executed depend on which key was pressed
	  */
	@Override
    public void keyPressed(KeyEvent e) {
		final int keyCode = e.getKeyCode();
		if( keyCode==KeyEvent.VK_LEFT  ) {
			spaceShooter.moveSpaceship(-1);
		} else if( keyCode==KeyEvent.VK_RIGHT ) {
			spaceShooter.moveSpaceship(1);
		} else if( keyCode==KeyEvent.VK_UP  ) {
			spaceShooter.moveSpaceship(-2);
		} else if( keyCode==KeyEvent.VK_DOWN  ) {
			spaceShooter.moveSpaceship(2);
		} else if( keyCode==KeyEvent.VK_0 ) {
		    System.exit(0);
	    }

	}

	/*The *keyReleased* Method
	  PURPOSE: to detect key releases & execute the desired action(s) associated with that key release
	  PRE: N/A
	  POST: executes the desired action(s)--the action(s) executed depend on which key was released
	  */
	@Override
    public void keyReleased(KeyEvent e) {
		final int keyCode = e.getKeyCode();
		if( keyCode==KeyEvent.VK_LEFT || keyCode==KeyEvent.VK_RIGHT 
			|| keyCode==KeyEvent.VK_UP || keyCode==KeyEvent.VK_DOWN
				) {
			spaceShooter.moveSpaceship(0);
		}
    }
}
