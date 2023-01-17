//Import the built-in Java classes/packages that are needed for this class:
import java.io.File;
import javax.sound.sampled.*;


/*The Sounds Class
PURPOSE: to open, play, loop, stop, or close a sound effect (WAV) file
PRE: the Sounds objects must be declared; there must be a *soundEffectPath* String that is passed to the constructor method
POST: opens, plays, loops, stops, or closes a sound effect (WAV) file
*/
public class Sounds {
  //Declare objects:
  String sfxPath;
  Clip sfxClip;
  
  /*The *Sounds* Constructor Method
  PURPOSE: to assign the passed String (*soundEffectPath*) to a global variable (*sfxPath*) of the Sounds class
  PRE: the *soundEffectPath* String must contain a valid path
  POST: initializes the *sfxPath* object
  */
  public Sounds(String soundEffectPath) {  
    sfxPath = soundEffectPath; //assign the (String) value of *soundEffectPath* to *sfxPath*
  }

  /*The *openSound* Method
  PURPOSE: to open a sound effect (WAV) file
  PRE: the *sfxPath* String must contain a valid relative path to the desired WAV file
  POST: opens a sound effect (WAV) file
  */
  public void openSound() {    
    try {         
      File soundEffect = new File(sfxPath); //create a File object & specify the path of the desired WAV file
      sfxClip = AudioSystem.getClip(); //create a Clip object
      sfxClip.open(AudioSystem.getAudioInputStream(soundEffect)); //open the Clip from the passed *soundEffect* File
    }
    catch(Exception e) {
      //the program goes here if any errors are encountered with retrieving the sound effect (WAV) file
    }
  }
  
  /*The *playSound* Method
  PURPOSE: to start playing a sound effect (WAV) file
  PRE: N/A
  POST: plays a sound effect (WAV) file from the beginning
  */
  public void playSound() {
    sfxClip.setFramePosition(0); //set the *sfxClip* to play from the beginning
    sfxClip.start(); //start playing the *sfxClip*
  }
  
  /*The *loopSound* Method
  PURPOSE: to loop a sound effect (WAV) file
  PRE: N/A
  POST: loops a sound effect (WAV) file
  */
  public void loopSound() {
    sfxClip.loop(Clip.LOOP_CONTINUOUSLY); //set the *sfxClip* to loop (i.e. play continuously)
  }
   
  /*The *stopSound* Method
  PURPOSE: to stop playing a sound effect (WAV) file
  PRE: N/A
  POST: stops playing a sound effect (WAV) file
  */
  public void stopSound() {
    sfxClip.stop(); //stop playing the *sfxClip* 
    sfxClip.close(); //close the *sfxClip*
  }
}