package com.example.storybuilder;
/**
 * Libraries
 */
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
/**
 * @author Emilome Ileso
 *
 *This activity class provides all the information about the app.
 * 
 *Operations: Click Create new story, Create random story, etc. 
 *
 *Incomplete
 */
public class HelpActivity extends Activity implements OnClickListener {

	ActionBar bar;
	//Buttons
	Button btn1, btn2, btn3, btn4;
	TextView tv1;
	boolean checkifopen = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		//sets the help activity screen to fullscreen 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//retrieve actionbar
		bar = getActionBar();

		// enables the back button
		bar.setDisplayHomeAsUpEnabled(true);

		btn1 = (Button)findViewById(R.id.help_button1);
		btn1.setOnClickListener(this);

		btn2 = (Button)findViewById(R.id.help_button2);
		btn2.setOnClickListener(this);

		btn3 = (Button)findViewById(R.id.help_button3);
		btn3.setOnClickListener(this);

		btn4 = (Button)findViewById(R.id.help_button4);
		btn4.setOnClickListener(this);

		tv1 = (TextView)findViewById(R.id.help_input1);

	}

	/**
	 * an item in your options menu is selected
	 */
	@Override
	public void onBackPressed() {
		//create alert dialog
		new AlertDialog.Builder(this)
		//set message
		.setMessage("Are you sure you want to exit?")
		.setCancelable(false)
		//set Yes button
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//close activity
				finish();
			}
		})
		//set No button
		.setNegativeButton("No", null)
		.show();
	}

	/**
	 * on click action bar back button 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//close activity
			finish();
			break;
		}
		return true;
	}

	/**
	 * on click
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		//if create a story button is clicked 
		case R.id.help_button1:
			//if button is opened
			if(checkifopen == true){
				//set checkifopen to false
				checkifopen = false;
				//display button info
				tv1.setText(Html.fromHtml(getString(R.string.nice_html)));
				ViewGroup.LayoutParams params = btn1.getLayoutParams();
				//button new width
				params.height = 80;
				//set buttons width
				btn1.setLayoutParams(params);
			} else {
				//set checkifopen to true
				checkifopen = true;
				//clear textview
				tv1.setText(" ");
				ViewGroup.LayoutParams params = btn1.getLayoutParams();
				//button new width
				params.height = 100;
				//set buttons width
				btn1.setLayoutParams(params);
			}
			break;

		case R.id.help_button2:
			if(checkifopen == true){
				checkifopen = false;
				tv1.setText(Html.fromHtml(getString(R.string.nice_html)));
				ViewGroup.LayoutParams params = btn2.getLayoutParams();
				//Button new width
				params.height = 80;
				btn2.setLayoutParams(params);
			} else {
				checkifopen = true;
				tv1.setText(" ");
				ViewGroup.LayoutParams params = btn2.getLayoutParams();
				//Button new width
				params.height = 100;
				btn2.setLayoutParams(params);
			}
			break;
		default:
			break;
		}
	}
}