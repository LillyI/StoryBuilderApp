package com.example.storybuilder;
/**
 * Libraries
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

/**
 *This activity class records and plays 
 * audio and saves the audio on to the sqlite database.
 * 
 *Operations: start audio, record audio, stop/save audio.
 * @author Emilome Ileso 
 * @version 2014.05.01
 * 
 * References
 * Audio Capture [Online] Jim’s Story [Online] 1997, Android developer, 
 * http://developer.android.com/guide/topics/media/audio-capture.html [accessed 04 April 2014]
 */
public class AudioActivity extends Activity {

	//String
	private String outputFile = null;

	//Buttons
	private Button startbtn,stopbtn,playbtn;

	//database, intent, audio recorder, actionbar, mediarecorder
	MySQLiteHelper db;
	Intent intent;
	MediaRecorder myAudioRecorder;
	ActionBar bar;

	//boolean, long
	private boolean isRecording = false;
	long pagep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Sets the activity content from layout activity_audio.
		setContentView(R.layout.activity_audio);

		//sets screen to fullsize
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//start db
		db = new MySQLiteHelper(getApplicationContext());

		//get actionbar
		bar = getActionBar();

		getCurrentPage();

		// Enabling Up / Back navigation
		bar.setDisplayHomeAsUpEnabled(true);

		//views identified by their id attribute 
		startbtn = (Button)findViewById(R.id.button1);
		stopbtn = (Button)findViewById(R.id.button2);
		playbtn = (Button)findViewById(R.id.button3);

		//if phone has no microphone
		if (!hasMicrophone())
		{
			//disable and hide all buttons
			stopbtn.setEnabled(false);
			stopbtn.setVisibility(View.INVISIBLE);
			playbtn.setEnabled(false);
			playbtn.setVisibility(View.INVISIBLE);
			startbtn.setEnabled(false);
			startbtn.setVisibility(View.INVISIBLE);

			Toast.makeText(getApplicationContext(), "No microphone", Toast.LENGTH_SHORT).show();
		} else {
			//else hide and sisbale the play and stop button
			playbtn.setEnabled(false);
			stopbtn.setEnabled(false);
			stopbtn.setVisibility(View.INVISIBLE);
			playbtn.setVisibility(View.INVISIBLE);
		}

		//path of the audio file. 
		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/myrecording.3gp";;


	}
	/**
	 * Returns the intent that started this activity and
	 * gets the extra data pass with the intent.
	 */
	public void getCurrentPage() {
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			setCurrentPage(extras);
		}
	}

	/**
	 * setter
	 * @param extras the name of the data
	 */
	public void setCurrentPage(Bundle extras){
		pagep = extras.getLong("pagep");
	}

	/**
	 * 
	 * @return true or false if phone has microphone
	 */
	protected boolean hasMicrophone() {
		PackageManager pmanager = this.getPackageManager();
		return pmanager.hasSystemFeature(
				PackageManager.FEATURE_MICROPHONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//closes db
			db.closeDB();
			//return to previous activity
			final Intent intent=new Intent();
			setResult(1,intent);
			finish();
			break;
		}
		return true;
	}

	/**
	 * On click start button 
	 * @param view
	 * @throws IOException
	 */
	public void start(View view) throws IOException{

		//set to true
		isRecording = true;
		//disable and hide the play and stop button
		stopbtn.setEnabled(true);
		playbtn.setEnabled(false);
		startbtn.setEnabled(false);

		playbtn.setVisibility(View.INVISIBLE);
		startbtn.setVisibility(View.INVISIBLE);

		//show stop button
		stopbtn.setVisibility(View.VISIBLE);

		try {
			myAudioRecorder = new MediaRecorder();
			myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			myAudioRecorder.setOutputFile(outputFile);
			myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			myAudioRecorder.prepare();

		} catch (Exception e) {
			e.printStackTrace();
		}

		myAudioRecorder.start(); // recording started
		Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();

	}
 /**
  * on click stop button convert audio to bytes.
  * @param view
  * @throws IOException
  */
	public void stop(View view) throws IOException{
		stopbtn.setEnabled(false);
		stopbtn.setVisibility(View.INVISIBLE);
		playbtn.setEnabled(true);
		playbtn.setVisibility(View.VISIBLE);

		//if audio is playing
		if (isRecording)
		{	
			//stop audio
			startbtn.setEnabled(true);
			startbtn.setVisibility(View.VISIBLE);
			myAudioRecorder.stop();
			myAudioRecorder.release();
			myAudioRecorder = null;
			isRecording = false;

		
			File file = new File(outputFile);
			FileInputStream fin = null;
			try {
				// create FileInputStream object
				fin = new FileInputStream(file);

				byte fileContent[] = new byte[(int)file.length()];

				// Reads up to certain bytes of data from this input stream into an array of bytes.
				fin.read(fileContent);

				//get page
				Page audiop = db.getPage(pagep);
				//set audio to page
				audiop.setAudio(fileContent);
				//update page in db.
				db.updatePage(audiop);

				Toast.makeText(getApplicationContext(), "Audio recorded and saved",
						Toast.LENGTH_LONG).show();
			}
			catch (FileNotFoundException e) {
				Log.d("tag", "File not found" + e);
			}
			catch (IOException ioe) {
				Log.d("tag", "Exception while reading file " + ioe);
			}
			finally {
				// close the streams using close method
				try {
					if (fin != null) {
						fin.close();
					}
				}
				catch (IOException ioe) {
					Log.d("tag", "Error while closing stream: " + ioe);
				}
			}
		} else {
			//stop audio recorder
			myAudioRecorder.release();
			myAudioRecorder = null;
			startbtn.setEnabled(true);
			startbtn.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * on click play button play audio recored.
	 * @param view
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void play(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		MediaPlayer m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();
		Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
	}

	
	/**
	 * Called when the activity has detected the user's press of the back key
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to exit?")
		.setCancelable(false)
		//set yes button
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//closes db
				db.closeDB();
				//closes activity.
				finish();
			}
		})
		//set no button
		.setNegativeButton("No", null)
		.show();
	}
}