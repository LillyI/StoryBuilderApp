package com.example.storybuilder;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
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
 * A story which has a storyid, title,, author, shared.
 * A story can be add or deleted from the SQLite database.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class TitleAuthorActivity extends Activity implements OnClickListener {

	Intent intent;
	ImageButton imgbtn1; 
	Button helpbtn;
	TextView tv1, tv2;
	EditText titleBox, authorBox;
	Animation anim;
	String newTitle,newAuthor;
	MySQLiteHelper db;
	ActionBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title_author);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		db = new MySQLiteHelper(getApplicationContext());
		
		//get all pages
		db.getAllPages();
		//get all stories
		db.getAllStories();
		
		/**
		 * delete pages not being used.
		 */
		if(db.getAllPagesByBelongsto(0).isEmpty()==false){
			for(Page p : db.getAllPagesByBelongsto(0)){
				db.deletePage(p.getId());
		}
			}
		
		db.closeDB();
		
		//get actionbar
		bar = getActionBar();

		bar.setCustomView(R.layout.d);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		 //Enabling Up / Back navigation
		bar.setDisplayHomeAsUpEnabled(true);

		
		//views
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

		//load anim
		anim = AnimationUtils.loadAnimation(this, R.anim.fade);

	}

	public void onClick(View v) {

		switch(v.getId()){
		case R.id.imageView1:

			//gets title
			newTitle = titleBox.getText().toString();
			//get author
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

				Toast.makeText(getApplicationContext(), "Hello " + newAuthor +", lets create a story!", Toast.LENGTH_LONG).show();
				intent = new Intent(TitleAuthorActivity.this, StoryMakerActivity.class);
				//passes the title and author through the intent.
				intent.putExtra("Title",  newTitle);
				intent.putExtra("Author",  newAuthor);
				startActivity(intent);
				finish();
			}


			break;
			
		case R.id.helpbtnd:
			intent = new Intent(TitleAuthorActivity.this, KeyActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	/**
	 *  delay textview for 50 seconds.
	 */
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		}
		return true;
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
