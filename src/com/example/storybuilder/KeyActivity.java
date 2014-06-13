package com.example.storybuilder;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * @author Emilome Ileso
 *
 *This activity class displays an keyboard image 
 * 
 *Operation: click back button
 */
public class KeyActivity extends Activity {
	
	ActionBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//get actionbar
		bar = getActionBar();

		// Enabling Up / Back navigation
		bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//close activity
			this.finish();
			break;
		}
		return true;
	}

}
