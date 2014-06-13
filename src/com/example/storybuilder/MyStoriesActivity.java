package com.example.storybuilder;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
/**
 * displays all stories created by user.
 * 
 * operations: click view story, search stories, delete story, share/upload story to webserver,
 * 					change title or author or story.
 * 
 * @author Emilome Ileso
 * @version 2014.05.01
 * 
 *  References
 * How to connect Android with PHP, MySQL [Online] 2012 
 * Available from: http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
 *  [accessed 04 April 2014]
 */
public class MyStoriesActivity extends Activity implements OnItemClickListener, OnQueryTextListener  {

	ListView lview;

	MySQLiteHelper db;
	ArrayList<Story> stories = new ArrayList<Story>();
	ArrayAdapter<Story> arrayAdapter;

	SearchView sv;

	Button del_btn1,view,edit,share_btn1, btnchange;

	int newstory = 0;

	TextView mytitle,myauthor,textView3,bytitle,byauthor;

	Intent intent;

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	// url to create new product
	private static String url_create_story = "http://storybuilder.comuv.com";

	private static String url_delete_story = "http://storybuilder.comuv.com/delete.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_stories);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ActionBar actionBar = getActionBar();
		db = new MySQLiteHelper(this);

		mytitle = (TextView) findViewById(R.id.mytitle);
		myauthor = (TextView) findViewById(R.id.myauthor);
		textView3 = (TextView) findViewById(R.id.textView3);
		bytitle = (TextView) findViewById(R.id.bytitle);
		byauthor = (TextView) findViewById(R.id.byauthor);

		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);

		//if db.getAllStories() array is not empty
		if(db.getAllStories().isEmpty() == false){
			//for each
			for (Story s : db.getAllStories()){
				//add to aarryList stories
				stories.add(s);
			}
		}


		sv = (SearchView) findViewById(R.id.sv1);
		lview = (ListView) findViewById(R.id.lv1);

		arrayAdapter = new ArrayAdapter<Story>(
				this, 
				android.R.layout.simple_list_item_1,
				stories );
		lview.setAdapter(arrayAdapter);
		lview.setOnItemClickListener(this);		

		//enables type filtering
		lview.setTextFilterEnabled(true);
		setupSearchView();

		del_btn1 = (Button) findViewById(R.id.del);
		del_btn1.setOnClickListener(new OnClickListener() {   
			//on click delete button
			public void onClick(View v) {   
				//remove from arrayAdapter
				arrayAdapter.remove(db.getStory(newstory));
				//update arrayadapter
				arrayAdapter.notifyDataSetChanged();
				//get current story
				//delete from database.
				db.deleteStory(db.getStory(newstory));
				String msg1 = "Story Deleted";
				//Show toast
				Toast.makeText(getBaseContext(), msg1, Toast.LENGTH_SHORT).show();

				restart();
			}
		});

		edit = (Button) findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() 

		{   //on click edit button
			public void onClick(View v) 
			{   
				//show toast 
				Toast.makeText(getBaseContext(), "Edit Story !", Toast.LENGTH_LONG).show();
				//start new activity.
				intent = new Intent(MyStoriesActivity.this, EditStoryMakerActivity.class);
				intent.putExtra("currentstory", newstory);
				startActivity(intent); 

			}
		});

		share_btn1 = (Button) findViewById(R.id.share_btn1);
		share_btn1.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				if (db.getStory(newstory).getShared().equals("Not Shared")) {
					// creating new story in background thread
					new CreateNewStory().execute();

				} else
				{
					// deleting new story in background thread
					new DeleteStory().execute();
				}
			}
		});


		btnchange = (Button) findViewById(R.id.btnchange);
		btnchange.setOnClickListener(new OnClickListener() {   
			//on click button "btnchange"
			public void onClick(View v) {


				AlertDialog.Builder ad = new AlertDialog.Builder(MyStoriesActivity.this);

				ad.setTitle("Change title or author?");

				LinearLayout layout = new LinearLayout(v.getContext());
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(10, 10, 10, 10);

				final TextView titleView = new TextView(v.getContext());
				titleView.setText("Title:");
				titleView.setTextSize(20);
				layout.addView(titleView);

				//get new author and title from the edittext

				final EditText titleBox = new EditText(v.getContext());
				titleBox.setText(db.getStory(newstory).getTitle());
				layout.addView(titleBox);

				final TextView authorView = new TextView(v.getContext());
				authorView .setText("Author:");
				authorView .setTextSize(20);
				layout.addView(authorView );

				final EditText authorBox = new EditText(v.getContext());

				authorBox.setText(db.getStory(newstory).getAuthor());
				layout.addView(authorBox);

				ad.setView(layout);

				//on click yes button
				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						String value = titleBox.getText().toString().trim();
						String value2 = authorBox.getText().toString().trim();

						// set new author and title for story.

						Story s = db.getStory(newstory);
						s.setTitle(value);
						s.setAuthor(value2);

						bytitle.setText(value);
						byauthor.setText(value2);
						db.updateStory(s);
						dialog.cancel();
						Toast.makeText(getBaseContext(), "Changed!", Toast.LENGTH_SHORT).show();

						restart();
					}
				});

				// Setting Negative "NO" Button
				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//close dialog.
						dialog.cancel();
					}
				});

				ad.show();
			}
		});

		view = (Button) findViewById(R.id.view_btn);
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				//on click view button
				Toast.makeText(getBaseContext(), "Story launched!", Toast.LENGTH_SHORT).show();
				// starts a new activity "ViewStoryActivity"
				intent = new Intent(MyStoriesActivity.this, ViewStoryActivity.class);
				intent.putExtra("currentstory", newstory);
				startActivity(intent);


			}
		});

	}
	/**
	 * 
	 */
	private void setupSearchView() {
		//sets the background color of the search bar.
		sv.setBackgroundDrawable(new ColorDrawable(0xff63b7cb));
		//set a listener for user actions within the SearchView.
		sv.setOnQueryTextListener(this);
		//set searchview hint to text below.
		sv.setQueryHint("Search stories here");
	}

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


	/**
	 * If item in listview clicked run method.
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		//set visible view to invisible
		textView3.setVisibility(View.INVISIBLE);

		//set all invisible views to visible
		mytitle.setVisibility(View.VISIBLE);
		myauthor.setVisibility(View.VISIBLE);
		share_btn1.setVisibility(View.VISIBLE);
		edit.setVisibility(View.VISIBLE);
		del_btn1.setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);
		btnchange.setVisibility(View.VISIBLE);

		//onclick item, get storyid and set it as story pointer.
		newstory = arrayAdapter.getItem(position).getId();

		//set title and author of story
		bytitle.setText(db.getStory(newstory).getTitle());
		byauthor.setText(db.getStory(newstory).getAuthor());


		String sharedmsg = db.getStory(newstory).getShared();

		if(sharedmsg.equals("Not Shared")){
			share_btn1.setText("Share");
		}else{
			share_btn1.setText("Shared");
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			MyStoriesActivity.this.finish();
			intent = new Intent(MyStoriesActivity.this, MenuActivity.class);
			startActivity(intent);
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
				MyStoriesActivity.this.finish();
			}
		})
		.setNegativeButton("No", null)
		.show();
	}



	public void restart(){
		db.close();

		MyStoriesActivity.this.finish();
		intent = new Intent(MyStoriesActivity.this, MyStoriesActivity.class);
		startActivity(intent);
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewStory extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyStoriesActivity.this);
			pDialog.setMessage("Sharing Story..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			Log.d("Show now ", "");
			String storyid = Integer.toString(newstory);
			String title = db.getStory(newstory).getTitle();
			String author = db.getStory(newstory).getAuthor();


			/*Page sharep = db.getAllPagesByBelongsto(newstory).get(0);
			String pageid = Integer.toString(sharep.getId());
			String belongsto = Integer.toString(sharep.getBelongTo());
			String text =sharep.getText();
			 */
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("password", "password"));
			params.add(new BasicNameValuePair("storyid", storyid));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("author", author));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_story,
					"POST", params);

			// check log cat fro response
			//Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Story changedstory = db.getStory(newstory);
					changedstory.setShared("Shared");
					db.updateStory(changedstory);

					runOnUiThread(new Runnable() {
						public void run() {
							share_btn1.setText("Shared");
							//on click show Toast
							Toast.makeText(getBaseContext(), "Story Shared", Toast.LENGTH_SHORT).show();
						}
					});
				} else {

					runOnUiThread(new Runnable() {
						public void run() {

							//on click show Toast
							Toast.makeText(getBaseContext(), "Failed to share story!", Toast.LENGTH_SHORT).show();
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

		}

	}


	/**
	 * Background Async Task to Create new product
	 * */
	class DeleteStory extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyStoriesActivity.this);
			pDialog.setMessage("Deleting Story..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String storyid = Integer.toString(newstory);
			String title = bytitle.getText().toString();
			String author = byauthor.getText().toString();

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("password", "password"));
				params.add(new BasicNameValuePair("storyid", storyid));
				params.add(new BasicNameValuePair("title", title));
				params.add(new BasicNameValuePair("author", author));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_story, "POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Story changedstory = db.getStory(newstory);
					changedstory.setShared("Not Shared");
					db.updateStory(changedstory);

					runOnUiThread(new Runnable() {
						public void run() {
							share_btn1.setText("Share");
							//on click show Toast
							Toast.makeText(getBaseContext(), "Story not shared anymore", Toast.LENGTH_SHORT).show();
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

		}

	}


}
