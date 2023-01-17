//ICS4U1
//Kelly Qu
//June 28, 2021
//Assignment 3 - GUI Application
//The purpose of this program is to run a Space Shooter game.

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/* The SpaceShooterStart Class
 * PURPOSE: to create objects of the SpaceShooter and SpaceShooterView classes & add the JPanel to the *JFrame* window
 * PRE: N/A
 * POST: creates SpaceShooter and SpaceShooterView object & adds to the *JFrame* window
 */
public class SpaceShooterStart {

	/* The *main* Method
	   PURPOSE: to create a new object (JFrame) of the SpaceShooterStart class
	   PRE: N/A
	   POST: creates a SpaceShooterStart object (JFrame/window)
	*/
	public static void main(String args[])
	{

		//Declare & initialize the Sounds objects:
		Sounds bgMusic = new Sounds("backgroundmusic.wav");
		
		JFrame frame = new JFrame("Space Shooter"); //create a new frame called *Space Shooter*
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //allow pFrame to be closed

		SpaceShooter spaceShooter = new SpaceShooter();
		SpaceShooterView view = new SpaceShooterView(spaceShooter);
		frame.setLayout(new BorderLayout());
		frame.add( view ,BorderLayout.CENTER);
		frame.setSize(frame.getPreferredSize()); 
		frame.setResizable(false);
		frame.setVisible(true);
		view.requestFocus();
	
		bgMusic.openSound(); //open, play, and loop background music
	    bgMusic.playSound();
	    bgMusic.loopSound();
	}
}
