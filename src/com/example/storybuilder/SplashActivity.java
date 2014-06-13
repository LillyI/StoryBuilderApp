package com.example.storybuilder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * This activity displays an animation and plays some audio for 30 sec
 * Splash screen displayed once
 * References
 * Android Fancy Animated Splash Screen [Online] 2012, manishkpr,  
 * Available from:  http://manishkpr.webheavens.com/android-fancy-animated-splash-screen/ [accessed 04 April 2014]
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class SplashActivity extends Activity {

	private Animation anim;
	private ImageView iv;
	private String TAG = "tag";

	private static final int TIMER = 3 * 1000;

	MediaPlayer mp;
	Intent intent;
	LinearLayout l;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SharedPreferences sharedPreferences = getSharedPreferences("version", 0);
		int savedVersionCode = sharedPreferences.getInt("VersionCode", 0);

		int appVershionCode = 0;

		try {
			appVershionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

		} catch (NameNotFoundException nnfe) {
			Log.w(TAG, "$ Exception caz of appVershionCode : " + nnfe);
		}   

		if(savedVersionCode == appVershionCode){
			Log.d(TAG, "$$ savedVersionCode == appVershionCode");

			//start a new activity
			intent = new Intent(SplashActivity.this, MenuActivity.class);
			startActivity(intent);
			//close this activity
			SplashActivity.this.finish();
		}else{
			Log.d(TAG, "$$ savedVersionCode != appVershionCode");

			SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putInt("VersionCode", appVershionCode);
			sharedPreferencesEditor.commit();

			iv = (ImageView) findViewById(R.id.splash_img);
			music();
			startanim();

		}


	}

	/**
	 * start or resumes audio
	 */
	public void music() {
		mp = MediaPlayer.create(getBaseContext(), R.raw.beep);
		mp.start();
	}

	/**
	 * loads and starts the animation, then sleeps for 30seconds and activity closes
	 */
	private void startanim() {
		//load animaton
		anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		anim = AnimationUtils.loadAnimation(this, R.anim.translate);
		anim.reset();
		iv.clearAnimation();
		iv.startAnimation(anim);

		new Handler().postDelayed(new Runnable() {

			/*Showing splash screen with a timer.*/

			@Override
			public void run() {
				//Apply splash exit (fade out) and main entry (fade in) animation transitions.
				overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout); 
				//storu audio
				mp.stop();


				//start a new activity
				intent = new Intent(SplashActivity.this, MenuActivity.class);
				startActivity(intent);
				//close this activity
				SplashActivity.this.finish();
			}

		}, TIMER);
	}


}
