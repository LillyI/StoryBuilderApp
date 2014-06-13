package com.example.storybuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * allows the user to view the story they have just created.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class ViewStoryActivity extends Activity implements OnClickListener{

	ImageView imgv2;
	Button imgbtn1, imgbtn2;
	Button btn1, btn2;
	int index = 0;
	DrawerLayout drawerLayout;
	ListView drawerlv;
	ActionBarDrawerToggle actionBarDrawerToggle;
	ActionBar bar;
	ArrayList<Page> pages = new ArrayList<Page>();
	ArrayAdapter<Page> adapter;
	int storyid;
	Story story;
	Intent intent;
	MySQLiteHelper db;
	TextView tv1,tv2;
	long pagep;
	MediaPlayer mediaPlayer;
	boolean stop = false;
	Page audiop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_story);
		//set screen to full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


		mediaPlayer = new MediaPlayer();
		Log.d("View Story", "new page");
		//get actionbar
		bar = getActionBar();

		//set custom layout c to actionbar
		bar.setCustomView(R.layout.c);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);


		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			//get current story id
			storyid = extras.getInt("currentstory");
		}

		//db.
		db = new MySQLiteHelper(getApplicationContext());


		story = db.getStory(storyid);

		//get all pages that belong to the current story
		if(db.getAllPagesByBelongsto(story.getId()).isEmpty()==false){
			for(Page p : db.getAllPagesByBelongsto(story.getId())){
				//add these pages to arrayList pages
				pages.add(p);
			}
		}
		//views
		tv1 = (TextView) findViewById(R.id.tvbtn2);
		imgv2 = (ImageView) findViewById(R.id.view2);
		tv2 = (TextView) findViewById(R.id.tvbtn);


		/* set image and text on startup of activity.*/
		Page p = pages.get(0);

		pagep = p.getId();

		byte[] by = db.getPage(pagep).getImage();

		String st = db.getPage(pagep).getText();

		if (by != null) {
			//Change it to Bitmap
			Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
			imgv2.setImageBitmap(bm);
			Log.d("TAG", "it worked :)");
		} else {
			Log.d("TAG", "it's null :(");
		}


		if (st != null) {
			tv2.setText(st);
			Log.d("TAG", "it worked :)");
		} else {
			Log.d("TAG", "it's null :(");
		}

		/* ..... */
		
		//views and set listeners to these viewvs.
		imgbtn1 = (Button) findViewById(R.id.exitbtn);
		imgbtn1.setOnClickListener(this);

		btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(this);

		btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(this);

		//if story contain one page 
		if(pages.size() == 1){
			//disable prev and next button.
			btn1.setEnabled(false);
			btn2.setEnabled(false);
		}
		//else
		else{
			//disable prev button
			btn1.setEnabled(false);
		}
		
		//audio page pointer.
		audiop = db.getPage(pagep);
		imgbtn2 = (Button) findViewById(R.id.audiobtn);
		imgbtn2.setOnClickListener(this);


		imgbtn2.setOnLongClickListener(new OnLongClickListener() { 
			@Override
			//on long click stop audio.
			public boolean onLongClick(View v) {
				//if there is an audio for current page.
				if(audiop.getAudio() != null){
					audio();
				}
				return true;
			}
		});

		String TitleAuthor = " | " + story.toString() + " | ";

		tv1.setText(TitleAuthor);

		// get ListView defined in activity_main.xml
		drawerlv = (ListView) findViewById(R.id.left_drawer);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new ArrayAdapter<Page>(this,
				android.R.layout.simple_list_item_1, pages);


		// Set the adapter for the list view
		drawerlv.setAdapter(adapter);

		// 2. App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 2.1 create ActionBarDrawerToggle
		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				);

		// just styling option
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		// 2. App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// 2.2 Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		drawerlv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			/**
			 * on item click 
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {


				//close drawer
				drawerLayout.closeDrawer(Gravity.LEFT);
				//get page
				Page p = adapter.getItem(arg2);
				pagep = p.getId();
				Page page = db.getPage(pagep);

				//if page has text
				if(page.getText() != null){
					//get and set text for page.
					tv2.setText(page.getText());
				}
				else {
					
					tv2.setText("");
				}
				index = arg2;
				//get image 
				byte[] bitmapdata = page.getImage();

				if(bitmapdata != null){
					Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
					imgv2.setImageBitmap(bitmap);
				}else{
					imgv2.setImageBitmap(null);
				}

			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.exitbtn:
			AlertDialog.Builder ad = new AlertDialog.Builder(ViewStoryActivity.this);
			ad.setMessage("Are you sure you want to exit?");

			ad.setCancelable(false);

			ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
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

			
			//on click audio button
		case R.id.audiobtn:

			//get page
			Page audiop = db.getPage(pagep);
			//if page has audio 
			if(audiop.getAudio() != null){
				//play audio
				playMp3(audiop.getAudio());
				//else
			} else{
				//Show Toast
				Toast.makeText(getApplicationContext(), "No Recording", Toast.LENGTH_SHORT).show();
			}
			break;
			
			//on click next button
		case R.id.button2:
			btn1.setEnabled(true);
			if(index < pages.size() - 1){
				//increase page index
				index++;
				Page set = pages.get(index);
				pagep = set.getId();
				int addone = index + 1;
				Toast.makeText(getApplicationContext(), "Page " + addone, Toast.LENGTH_LONG).show();
				byte[] by = set.getImage();

				String st = set.getText();

				if (by != null) {
					//Change it to Bitmap
					Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
					imgv2.setImageBitmap(bm);

				} else{
					imgv2.setImageBitmap(null);
				}
				tv2.setText(st);

			} else {
				Toast.makeText(getApplicationContext(), "No more Pages", Toast.LENGTH_SHORT).show();
			}


			break;
			
			// on click prev button
		case R.id.button1:
			if(index > 0){
				//decrease page index.
				index--;
				//get page
				Page set2 = pages.get(index);
				pagep = set2.getId();
				int addone = index + 1;
				Toast.makeText(getApplicationContext(), "Page " + addone, Toast.LENGTH_SHORT).show();
				byte[] by2 = set2.getImage();

				String st2 = set2.getText();
				
				//set text and image 

				if (by2 != null) {
					//Change it to Bitmap
					Bitmap bm = BitmapFactory.decodeByteArray(by2, 0, by2.length);
					imgv2.setImageBitmap(bm);
				} else{
					imgv2.setImageBitmap(null);
				}

				tv2.setText(st2);
				// show the data here
			}else {
				Toast.makeText(getApplicationContext(), "Page 1", Toast.LENGTH_LONG).show();
			}

			break;
		default:
			break;
		}

	}

	/**
	 * plays the audio
	 * @param mp3SoundByteArray the audio for the page
	 */
	private void playMp3(byte[] mp3SoundByteArray) 
	{
		//if stop button has been clicked once 
		if(stop == true){
			//reset mediaplayer
			mediaPlayer.reset();
		}
		try {
			//create a file and set file path
			File path=new File(getCacheDir()+"/musicfile.3gp");

			//write the mp3SoundByteArray to the FileOutputStream
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(mp3SoundByteArray);
			fos.close();

			mediaPlayer.setDataSource(getCacheDir()+"/musicfile.3gp");
			mediaPlayer.prepare();
			mediaPlayer.start();
			Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * stops audio
	 */
	public void audio(){
		stop = true;
		mediaPlayer.stop();
		Toast.makeText(getBaseContext(), "Audio stoped", Toast.LENGTH_LONG).show();

	}
}