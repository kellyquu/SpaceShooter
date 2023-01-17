import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;

/* The SpaceShooter Class
 * PURPOSE: For game mechanics 
 */
public class SpaceShooter //implements java.lang.Runnable
{

	//Declare & initialize the Sounds objects:
	Sounds invaderkilled = new Sounds("sounds_invaderkilled.wav");
	Sounds explosion = new Sounds("sounds_explosion.wav");
		
	// Declare & initialize game setting constants, adjusted based on the difficulty, configures game 
	final static int MaxDifficulty = 10;	// Maximum difficulty level
	final static int MinHostileShipsCount = 4;   // Number of hostile ships
	final static int MinHostileShipSpeed  = 50;  // Minimum speed of hostile ship (Pixels moved within one second)
	final static int MaxHostileShipSpeed  = 100; // Maximum speed of hostile ship 
	final static int SpaceshipSpeed       = 200; // speed of the spaceship
	final static int SpaceShootSpeed      = 500; // the speed of the bullets from the spaceship (Pixels moved within one second)
	final static int MinHostileShootSpeed = 200; // the speed of the bullets from the hostile ships (Pixels moved within one second)
	final static int HostileShootSpeedD   =  10; // the frequency bullets shot from hostile ships, adjusted per the difficulty level
	//the frequency of the hostile ship shooting bullets: 4 - 16
	final static int MinHostileShootRate = 4; 
	final static int MaxHostileShootRate = 16; 
	
	// Declare & initialize window size variable
	final static public int ScreenWidth = 1024,ScreenHeight = 600;
	
	// Declare & initialize spaceship size variable
	final static public int SpaceshipWidth = 100, SpaceshipHeight = 40;
	
	// Declare & initialize ranges for spaceship movement variables
	final static public int MinSpaceshipY = 0, MaxSpaceshipY = ScreenHeight - 170;
	final static public int HostileShipWidth = 50;
	
	// Declare & initialize difficulty variable
	public int difficult;
	
	// Declare & initialize variables for spaceship movement:
	int spaceshipSpeed;
	int spaceshipMoveDir;	//0 stop moving, 1 right, -1 left, 2 downward, -2 moving upward
	int state;	// current status of spaceship (0 = not started, 1 = in-game)
	int spaceshipX = (ScreenWidth-SpaceshipWidth)/2, spaceshipY = 50;	// current location of spaceship
	
	// Declare time variables
	long startTime; // the time when the game starts.
	long endTime; // the time when the game ends.
	long lastUpdateViewTime; // last update view time
	
	// Declare variable for winning/losing
	int winState;
	
	SpaceShooterView spaceShooterView;
	
	/*The *SpaceShooter* Constructor Method
	  PURPOSE: to initialize a newly-created object of the SpaceShooter class
	  PRE: N/A
	  POST: initializes the newly-created SpaceShooter object
	 */
	public  SpaceShooter() {
		//When difficulty level is increased by 1, one more hostile ship, up to MinHostileShipsCount+MaxDifficulty, will be added
		hostileShips = new HostileShip[MinHostileShipsCount+MaxDifficulty];
		int random;
		
		for(int i=0;i<hostileShips.length;i++) {
			hostileShips[i] = new HostileShip();
			random = (int) ( Math.random() * (ScreenWidth-HostileShipWidth));
			hostileShips[i].x = random;  // randomize hostile ship movement, 50% chance go left, 50% right
			hostileShips[i].moveDir = (i&1)==0 ? 1 : -1;
			random = (int) ( Math.random() * (ScreenWidth-HostileShipWidth) / 10);
			hostileShips[i].moveSpeed = MinHostileShipSpeed + random; //randomly generate the moving speed for hostile ships 
		}
		this.spaceshipSpeed = SpaceshipSpeed;
		for(int i=0;i<shoots.length;i++) {
			shoots[i] = new Shoot();
		}
	}
	
	/*The *incDifficulty* Method
	  PURPOSE: increases/decreases MaxDifficulty level
	  PRE: increment of difficulty level
	  POST: none 
	 */
	public void incDifficulty(int inc) {
		difficult += inc;
		if( difficult<0 )
			difficult = 0;
		else if( difficult>MaxDifficulty )
			difficult = MaxDifficulty;
		
	}
	
	// create array to store hostile ships
	final HostileShip[]  hostileShips ;
	
   // create an array for bullets
    final Shoot[] shoots = new Shoot[100]; //maximum 100 bullets shown on the screen.
	
	/* The getHostileShipsCount Method
	 * PURPOSE: obtain the current difficulty level and the number of the hostile ships.
	 * PRE: minimum number of the hostile ships and difficulty level.
	 * POST: number of hostile ships
	 */
	int getHostileShipsCount() {
		return MinHostileShipsCount+this.difficult;
	}
	
	/* The reset Method
	 * PURPOSE: reset to initial state after winning/losing
	 * PRE: N/A
	 * POST: spaceship reset to initial position
	 */
	void reset() {
		for(int i=0;i<shoots.length;i++) {
			shoots[i].onscreen = false;
		}
		
		this.spaceshipMoveDir = 0;
		spaceshipX = (ScreenWidth-SpaceshipWidth)/2;
		spaceshipY = 50;
	}

	/* The Start Method
	 * PURPOSE: Starts the game
	 * PRE: N/A
	 * POST: N/A
	 */
	public void start() {
		if( state==0 ) {
			if( winState>0 ) // increase the difficulty level if the player won the last round
				this.incDifficulty(1); 
			else if( winState<0 ) // decrease the difficulty level if the player had lost.
				this.incDifficulty(-1);
			state = 1;
			reset();
			winState = 0;
			lastUpdateViewTime = startTime = System.currentTimeMillis();
			
			for(int i=0;i<hostileShips.length;i++) {
				hostileShips[i].killed = false;    
				hostileShips[i].lastShootTime = 0;
			}
			spaceShooterView.rerenderView();
	
			//when the game runs, initiate a thread to trigger the game window to re-render every 100ms. 
			if( timer!=null ) {
				timer.stop();
				timer = null;
			}
			timer = new Timer(100,
					new ActionListener() {
						    @Override public void actionPerformed(ActionEvent e) {
							    SpaceShooter.this.updateView();
					 	    }
						}
					);
			timer.start();
		}
	}
	
	Timer timer ;
	
	/* The moveSpaceship Method
	 * PURPOSE: moves the spaceship or stops its movement
	 * PRE: 0, 1, -1, 2, -2
	 * POST: stop moving, shift right, shift left, shift upwards, shift downwards respectively
	 */
	public void moveSpaceship(int dir) {
		if( this.state==1 ) {
			this.updateView();
			this.spaceshipMoveDir = dir;
		}

	}
	
	/* The updateView method
     * PURPOSE: Periodically call this method to update the graphic image buffer， usually each 100ms from the timer event, or when a key is pressed.
     * PRE: N/A
     * POST: N/A
	 */
	synchronized void updateView() {
		final long t0 = lastUpdateViewTime;
		final long t = System.currentTimeMillis(); 
		final int hostileShipsCount = this.getHostileShipsCount();
		int nonKilledShips = 0;
		int random; 
		long  nextShootTime;
		
		if( spaceshipMoveDir ==1 || spaceshipMoveDir==-1 ) {
			
			// makes the spaceship move, spaceshipX left/right, spaceshipY up/down 
			spaceshipX += spaceshipMoveDir * this.spaceshipSpeed * (int)(t-t0) / 1000;
			// the spaceship only moves within the window， if out of range, set to 0 or right/bottom window border
			if( spaceshipX<0 ) {
				spaceshipX = 0;
			} else if( spaceshipX>ScreenWidth-SpaceshipWidth )
				spaceshipX = ScreenWidth-SpaceshipWidth;
		} else if( spaceshipMoveDir ==2 || spaceshipMoveDir==-2 ) {
			spaceshipY += (spaceshipMoveDir/2) * this.spaceshipSpeed * (int)(t-t0) / 1000;
			if( spaceshipY<MinSpaceshipY )
				spaceshipY = MinSpaceshipY;
			else if( spaceshipY>MaxSpaceshipY )
				spaceshipY = MaxSpaceshipY;
		}
		// bullet movement
		for(int i=0;i<this.shoots.length;i++)
		{
			final Shoot s = this.shoots[i];
			if( !s.onscreen ) {
				continue;
			}
			s.x += s.dirX*s.shootSpeedX * (int)(t-t0) / 1000;
			if( s.x<=0 || s.x>=ScreenWidth ) {
				// ignore the bullet out of the scope, x-direction
				s.onscreen = false;
				continue;
			}
			s.y += s.dirY * s.shootSpeedY * (int)(t-t0) / 1000;
			if( s.y<=0 || s.y>=ScreenHeight ) {
				// ignore the bullet out of the scope, y-direction
				s.onscreen = false;
				continue;
			}
			if( s.dirY<0 && s.y<spaceshipY+SpaceshipHeight-5 
				&& s.x>this.spaceshipX+5 && s.x<this.spaceshipX+SpaceshipWidth-5 
				&& s.y>spaceshipY
				) {
				// stop playing if the space ship is shot by the hostile ships.
				explosion.openSound();
				explosion.playSound();
				this.stop(-1);
				return;
			}
			if( s.dirY>0 ) {
				for(int j=0;j<hostileShipsCount;j++) {
					HostileShip hs = hostileShips[j]; 
					if( !hs.killed && s.y>hs.y+4
							&& s.x>hs.x+2 && s.x<hs.x+HostileShipWidth-2 
							) {
						hs.killed = true;
						s.onscreen = false;
						invaderkilled.openSound();
						invaderkilled.playSound();
						continue;
					}
				}
			}
		}
		// if the hostile ships touch the border, change direction and move back.
		for(int i=0;i<hostileShipsCount;i++) {
			final HostileShip s = hostileShips[i];
			if( s.killed )
				continue;
			nonKilledShips++;
			if(  s.moveDir!=0 ) {
				s.x += s.moveDir * s.moveSpeed * (int)(t-t0) / 1000;
				int maxX = ScreenWidth-HostileShipWidth; // limits where the hostile ship can reach 
				if( s.x <0 ) {
					s.moveDir = -s.moveDir;
					s.x = -s.x;
				} else if( s.x >maxX ) {
					s.moveDir = -s.moveDir;
					s.x = maxX - (s.x-maxX);
				}
			}
			// decide if randomly shoot a bullet：
			random = (int) ( Math.random() * (ScreenWidth + HostileShipWidth) / 80);
			nextShootTime = s.lastShootTime==0 ? 
				startTime + random * 1000
				: s.lastShootTime + (MinHostileShootRate + random)*1000;
			
			if( nextShootTime<=t ) {
				s.lastShootTime = t;
				Shoot shoot = null;
				for(int j=0;j<this.shoots.length;j++) {
					if( !shoots[j].onscreen ) {
						shoot = shoots[j];
						break;
					}
				}
				if( shoot!=null ) {
				    shoot.dirY = -1;
				    shoot.dirX = s.moveDir;
				    shoot.shootSpeedY = MinHostileShootSpeed + this.difficult*HostileShootSpeedD;
				    shoot.shootSpeedX = s.moveSpeed;
				    shoot.x = s.x + 20;
				    shoot.y = s.y;
				    shoot.onscreen = true;
				}
			}
		}
		if( nonKilledShips==0 ) {
			// if all the hostile ships are killed, stop game and start new level 
			this.stop(1);
			return;
		}
		lastUpdateViewTime = t;
		spaceShooterView.rerenderView(); // 100ms
	}
	
	/* the stop method
	 * PURPOSE: stops the game
	 * PRE: winState
	 * POST: N/A
	 */
	public void stop (int winState) {
		if( state==1 ) {
			if( timer!=null ) {
				timer.stop();
				timer = null;
			}
			this.winState = winState;
			state = 0;
			reset();
			endTime = System.currentTimeMillis();
			spaceShooterView.rerenderView();
		}
	}
	
	/* the shoot method
	 * PURPOSE: to allow spaceship to shoot bullets
	 * PRE: N/A
	 * POST: bullet shot
	 */
	synchronized public void shoot() {
		if( this.state==0 ) {
			return;
		}
		this.updateView();
		Shoot s = null;
		for(int i=0;i<this.shoots.length;i++) {
			if( !shoots[i].onscreen ) {
				s = shoots[i];
				break;
			}
		}
		if( s==null ) {
			return;
		}
		s.dirY = 1;
		if( this.spaceshipMoveDir==-1 || this.spaceshipMoveDir==1) {
			s.dirX = this.spaceshipMoveDir;
		} else
		{
			s.dirX = 0;
		}
		s.shootSpeedY = SpaceShootSpeed;
		s.shootSpeedX = SpaceshipSpeed;
		s.x = this.spaceshipX + SpaceshipWidth/2 - 5;
		s.y = spaceshipY+SpaceshipHeight;
		s.onscreen = true;
		spaceShooterView.rerenderView();
	}
	
	
	/* The HostileShip Class
     * PURPOSE: define parameters for hostile ship
	 */
	static class HostileShip
	{
		int y = ScreenHeight - 100;
		int x;
		int moveDir; // -1 is left, 1 is right
		int moveSpeed; //in pixels per second
		boolean killed; // hostile ship killed
		long lastShootTime; // time of the last bullet shot
	}
	
	/* The Shoot Class
	 * PURPOSE: define parameters of the bullets
	 */
	static class Shoot
	{
		int x,y; //location of bullet
		int dirY; // -1 upwards (hostile ship fire), 1 downwards (spaceship fire)
		int dirX; // left/right movement
		int shootSpeedX; // bullet left/right speed
		int shootSpeedY; // bullet up/down speed	
		boolean onscreen; //bullet shown on screen 
	}	
	
	/* The getScore method
	 * PURPOSE: to score the player
	 * PRE: N/A
	 * POST: score
	 */
	int getScore() {
		int score = getKilledHostileShips()*10; //10 points per ship killed
		int time = getPlayTime() ;
		
		if( winState>0 ) {		// points rewarded for less time used
			if( time<10 ) {
				score += 50;
			}else if( time<15 ) {
				score += 40;
			}else if( time<20 ) {
				score += 30;
			} else if( time<30 ) {
				score += 20;
			}else if( time<50 ) {
				score += 10;
			}
		}
		return score;
	}
	
	/* The getPlayTime method
	 * PURPOSE: read the time used by the player
	 * PRE: N/A
	 * POST: play time
	 */
	int getPlayTime() {
		return (int)((this.endTime-this.startTime)/1000);
	}
	
	/* The getKilledHostileShips method
	 * PURPOSE: get the number of killed ships
	 * PRE: N/A
	 * POST: number of hostile ships killed
	 */
	int getKilledHostileShips() {
		final int hostileShipsCount = this.getHostileShipsCount();
		int n = 0;
		
		for(int i=0;i<hostileShipsCount;i++) {
			if( hostileShips[i].killed ) {
				n++;
			}
		}
		return n;
	}
	
}
