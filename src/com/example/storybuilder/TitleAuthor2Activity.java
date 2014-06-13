package com.example.storybuilder;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
/**
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class TitleAuthor2Activity extends Activity implements OnClickListener {

	Intent intent;
	ActionBar bar;
	ImageButton imgbtn1; 
	Button helpbtn;
	TextView tv1, tv2;
	EditText titleBox, authorBox;
	Animation anim;
	String newTitle,newAuthor;
	MySQLiteHelper db;
	long storyp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title_author2);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		db = new MySQLiteHelper(getApplicationContext());
		
		bar = getActionBar();

		bar.setCustomView(R.layout.d);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			storyp = extras.getLong("storyp");
		}

		imgbtn1 = (ImageButton) findViewById(R.id.imageView1);
		imgbtn1.setOnClickListener(this);
		
		helpbtn = (Button) findViewById(R.id.helpbtnd);
		helpbtn.setOnClickListener(this);

		tv1 = (TextView)findViewById(R.id.textView1);
		tv2 = (TextView)findViewById(R.id.TextView2);

		// grab the content of the new story from the user
		// grab the title
		titleBox = (EditText) findViewById(R.id.TitleEdit);
		authorBox = (EditText) findViewById(R.id.AuthorEdit);

		titleBox.setGravity(Gravity.CENTER_HORIZONTAL);
		authorBox.setGravity(Gravity.CENTER_HORIZONTAL);

		anim = AnimationUtils.loadAnimation(this, R.anim.fade);

	}

	public void onClick(View v) {

		switch(v.getId()){
		case R.id.imageView1:

			newTitle = titleBox.getText().toString();
			newAuthor = authorBox.getText().toString();

			if ("".equals(newTitle)) {
				Toast.makeText(getApplicationContext(), "Please enter a story title",
						Toast.LENGTH_SHORT).show();
			}

			if ("".equals(newAuthor)) {
				Toast.makeText(getApplicationContext(), "Please enter an author name",
						Toast.LENGTH_SHORT).show();
			}

			else{
				Story s = db.getStory(storyp);
				s.setTitle(newTitle);
				s.setAuthor(newAuthor);
				s.setShared("Not Shared");
				db.updateStory(s);
				
				char a = 0x0027;
				Toast.makeText(getBaseContext(), "Preview Story in " + a + " My Stories "+ a, Toast.LENGTH_LONG).show();
				intent = new Intent(TitleAuthor2Activity.this, MenuActivity.class);
				startActivity(intent);
				finish();
			}


			break;
			
		case R.id.helpbtnd:
			intent = new Intent(TitleAuthor2Activity.this, KeyActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus)
			tv1.startAnimation(anim);
		int delay = 5000;// in ms 

		Timer timer = new Timer();

		timer.schedule( new TimerTask(){
			public void run() { 

			}
		}, delay);
		tv2.startAnimation(anim);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to exit?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		})
		.setNegativeButton("No", null)
		.show();
	}

}

