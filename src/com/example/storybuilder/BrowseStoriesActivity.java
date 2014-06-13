package com.example.storybuilder;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ViewFlipper;
/**
 *This class allows users to view stories from Storybuilders web server. 
 * Also displays the stories stored on the mysql database.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 * 
 * References
 * How to connect Android with PHP, MySQL [Online] 2012 
 * Available from: http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
 *  [accessed 04 April 2014]
 */
public class BrowseStoriesActivity extends Activity implements OnItemClickListener, OnQueryTextListener  {


	//array of stories
	ArrayList<Story> storiesList = new ArrayList<Story>();
	//arrayadapter
	ArrayAdapter<Story> arrayAdapter;

	private boolean open = false;
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ActionBar bar;
	// url to get all stories list
	private static String url_all_stories = "http://storybuilder.comuv.com/get_all.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_STORIES = "stories";
	private static final String TAG_STORYID = "storyid";
	private static final String TAG_TITLE = "title";
	private static final String TAG_AUTHOR = "author";

	// stories JSONArray
	JSONArray stories = null;

	ListView lview;

	MySQLiteHelper db;

	SearchView sv; 

	Button del_btn1,view,edit,share_btn1, btnchange;

	Story newstory;
	boolean toggle = false;

	TextView mytitle,myauthor,textView3,bytitle,byauthor;

	Intent intent;
	Button imgbtn1, imgbtn2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_stories);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		bar = getActionBar();
		db = new MySQLiteHelper(this);

		mytitle = (TextView) findViewById(R.id.mytitle);
		myauthor = (TextView) findViewById(R.id.myauthor);
		textView3 = (TextView) findViewById(R.id.textView3);
		bytitle = (TextView) findViewById(R.id.bytitle);
		byauthor = (TextView) findViewById(R.id.byauthor);

		sv = (SearchView) findViewById(R.id.sv1);
		lview = (ListView) findViewById(R.id.lv1);
		// Enabling Up / Back navigation
		bar.setDisplayHomeAsUpEnabled(true);


		//if there is a internet connection load stories
		if(isNetworkAvailable()== true){
			Toast.makeText(getBaseContext(), "internet connection!", Toast.LENGTH_SHORT).show();
			new LoadAllStories().execute();
			//set view to invisible
			textView3.setVisibility(View.INVISIBLE);
			//else show Toast.
		}else{
			//set view to invisible
			textView3.setVisibility(View.INVISIBLE);
			Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
		}


		arrayAdapter = new ArrayAdapter<Story>(
				this, 
				android.R.layout.simple_list_item_1,
				storiesList);
		lview.setAdapter(arrayAdapter);
		lview.setOnItemClickListener(this);		
		lview.setTextFilterEnabled(true);
		setupSearchView();

		/**
		 * on click view button
		 */
		view = (Button) findViewById(R.id.view_btn);
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "Story launched!", Toast.LENGTH_SHORT).show();
				ViewFlipper vf = (ViewFlipper) findViewById( R.id.viewFlipper);
				//show new layout in viewflipper
				vf.showNext();

				//get actionbar
				bar = getActionBar();

				//set custom layout
				bar.setCustomView(R.layout.e);
				bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
						| ActionBar.DISPLAY_SHOW_HOME);
				bar.setDisplayHomeAsUpEnabled(false);
				
				//set open to true
				open = true;

				imgbtn1 = (Button) findViewById(R.id.exitbtn);
				imgbtn1.setOnClickListener(new OnClickListener() {   
					public void onClick(View v) {   
						// close new layout
						BrowseStoriesActivity.this.finish();
					}
				});
			}

		});
	}


	private void setupSearchView() {
		//sets the background color of the search bar.
		sv.setBackgroundDrawable(new ColorDrawable(0xff63b7cb));
		//set a listener for user actions within the SearchView.
		sv.setOnQueryTextListener(this);
		//set searchview hint to text below.
		sv.setQueryHint("Search stories here");
	}

	/**
	 *  on text change clear listveiw 
	 *  else sets the initial value for the text filter.
	 */
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			lview.clearTextFilter();
		} else {
			lview.setFilterText(newText.toString());
		}
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	/*
	 * return true if there is internet on your tablet device.
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * If item in listview is clicked
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		//set all invisible views to visible
		mytitle.setVisibility(View.VISIBLE);
		myauthor.setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);

		//onclick item, get storyid and set it as story pointer.
		newstory = arrayAdapter.getItem(position);

		//set title and author of story
		bytitle.setText(newstory.getTitle());
		byauthor.setText(newstory.getAuthor());
	}

	//Enables back button for this activity.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(open== false){
			switch (item.getItemId()) {
			case android.R.id.home:
				//closes activty.
				this.finish();
				break;
			}
		}
		return true;

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllStories extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BrowseStoriesActivity.this);
			pDialog.setMessage("Loading Stories. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * getting All stories from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("password", "password"));

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_stories, "GET", params);

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// stories found
					// Getting Array of stories
					stories = json.getJSONArray(TAG_STORIES);

					// looping through All Stories
					for (int i = 0; i < stories.length(); i++) {
						JSONObject c = stories.getJSONObject(i);

						// Storing each json item in variable
						String storyid = c.getString(TAG_STORYID);
						String title = c.getString(TAG_TITLE);
						String author = c.getString(TAG_AUTHOR);
						int id = Integer.parseInt(storyid);
						Story s = new Story(id,title,author);

						storiesList.add(s);
					}

					for(Story s : storiesList){
						System.out.print(s.getAll());
					}
				}
				else {
					// no stories found
					runOnUiThread(new Runnable() {
						public void run() {
							//on click show Toast
							Toast.makeText(getBaseContext(), "No stories found, share story", Toast.LENGTH_LONG).show();
						}
					});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					arrayAdapter.notifyDataSetChanged();
				}
			});

		}
	}
}