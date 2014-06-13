package com.example.storybuilder;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
/**
 * menu page of app.
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class MenuActivity extends Activity implements OnClickListener  {

	Intent intent;
	ImageButton btn1, btn2, btn3, btn4, btn5,btn6;
	AnimationDrawable animd;
	ImageView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		view = (ImageView) findViewById(R.id.imageView3);
		//Setting animation_list.xml as the background of the image view
		view.setBackgroundResource(R.drawable.animation_list);

		//Typecasting the Animation Drawable
		animd = (AnimationDrawable) view.getBackground();


		btn2 = (ImageButton)findViewById(R.id.TextView01);
		btn2.setOnClickListener(this);

		btn3 = (ImageButton)findViewById(R.id.textView4);
		btn3.setOnClickListener(this);

		btn4 = (ImageButton)findViewById(R.id.menu_btn_3);
		btn4.setOnClickListener(this);

		btn5 = (ImageButton)findViewById(R.id.textView5);
		btn5.setOnClickListener(this);

		btn6= (ImageButton)findViewById(R.id.imageView2);
		btn6.setOnClickListener(this);


		btn1 = (ImageButton)findViewById(R.id.textView1);
		btn1.setOnClickListener(this);


	}

	//Called when Activity becomes visible or invisible to the user
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			//Starting the animation when in Focus
			animd.start();
		} else {
			//Stoping the animation when not in Focus
			animd.stop();
		}
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
	public void onClick(View v) {

		switch (v.getId()) {
		/* starts new activties*/
		case R.id.textView1:
			intent = new Intent(MenuActivity.this, TitleAuthorActivity.class);
			startActivity(intent);
			break;

		case R.id.TextView01:
			intent = new Intent(MenuActivity.this, RandomStoryActivity.class);
			startActivity(intent);
			break;

		case R.id.textView4:
			intent = new Intent(MenuActivity.this, BrowseStoriesActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.menu_btn_3:
			intent = new Intent(MenuActivity.this, MyStoriesActivity.class);
			startActivity(intent);
			break;

		case R.id.textView5:
			intent = new Intent(MenuActivity.this, HelpActivity.class);
			startActivity(intent);
			break;

		case R.id.imageView2:
			AlertDialog.Builder ad = new AlertDialog.Builder(MenuActivity.this);
			ad.setMessage("Are you sure you want to exit?");

			ad.setCancelable(false);

			ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					//closes dialog
					finish();
				}
			});

			// Setting Negative "NO" Button
			ad.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			ad.show();
			break;
		default:
			break;
		}

	}


}
