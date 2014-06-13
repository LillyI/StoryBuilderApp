package com.example.storybuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
/**
 * randomly generates pages and images and text for these pages.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 * References
 * Jim’s Story [Online] 1997, Jim Davidson, 
 * Available from: http://objectsandaspects.com/ucsd/java/java1hw5b.html [accessed 04 April 2014]
 */
public class RandomStoryActivity extends Activity implements OnClickListener {

	String title,author;
	ImageView imgv1;
	EditText et1;
	DrawerLayout drawerLayout;
	ListView drawerlv;
	ActionBarDrawerToggle actionBarDrawerToggle;
	ActionBar bar;
	ArrayList<Page> pages = new ArrayList<Page>();
	ArrayAdapter<Page> adapter;
	Button btn1;
	int storyid;
	Story story;
	Button btn2, btn3, btn4, savetextbtn;
	Intent intent;
	MySQLiteHelper db;
	Bitmap bitmap;
	long pagep, storyp;
	private static final String tag ="State: ";
	private static final int TAKE_PICTURE = 0;
	private Uri mUri;

	String[] article = {"the","a","one","some","any"};
	String[] noun = {"boy", "girl", "dog", "town", "car", "book", "student",
			"cat", "army truck", "bulldozer", "banana", "apple", "witch"};
	String[] verb = {"drove", "jumped", "ran", "walked", "skipped",
			"schlepped", "oozed", "poured", "crawled", "climbed", "sang"};
	String[] preposition = {"to", "from", "over", "under", "on", "around",
	"through" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_story);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		db = new MySQLiteHelper(getApplicationContext());

		Log.d("Random Story Maker", "new page");

		bar = getActionBar();

		bar.setCustomView(R.layout.b);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		generatePages();

		et1 = (EditText) findViewById(R.id.et1);
		imgv1 = (ImageView) findViewById(R.id.view1);
		imgv1.setOnClickListener(this);

		btn2 = (Button) findViewById(R.id.biv3);
		btn2.setOnClickListener(this);

		btn3 = (Button) findViewById(R.id.biv4);
		btn3.setOnClickListener(this);


		btn4 = (Button) findViewById(R.id.biv2);
		btn4.setOnClickListener(this);
		
		savetextbtn = (Button)findViewById(R.id.savetextbtn);
		savetextbtn.setOnClickListener(this);
		

		Page p = pages.get(0);

		pagep = p.getId();

		byte[] by = db.getPage(pagep).getImage();

		String st = db.getPage(pagep).getText();

		if (by != null) {
			//Change it to Bitmap
			Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
			imgv1.setImageBitmap(bm);
			Log.d("TAG", "it worked :)");
		} else {
			Log.d("TAG", "it's null :(");
		}


		if (st != null) {
			et1.setText(st);
			Log.d("TAG", "it worked :)");
		} else {
			Log.d("TAG", "it's null :(");
		}

		
		// get ListView defined in activity_main.xml
		drawerlv = (ListView) findViewById(R.id.left_drawer);

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		//set Title on ActionBar to "Image"

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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				drawerLayout.closeDrawer(Gravity.LEFT);
				Page p = adapter.getItem(arg2);
				pagep = p.getId();

				Page onclickpage = db.getPage(pagep);


				if(onclickpage.getText() != null){
					et1.setText(onclickpage.getText());
				} else
				{
					String empty = "";
					et1.setText(empty);
				}

				if(onclickpage.getImage() != null){
					Bitmap bitmap = BitmapFactory.decodeByteArray(onclickpage.getImage() , 0, onclickpage.getImage().length);
					imgv1.setImageBitmap(bitmap);
				}
				else{
					imgv1.setImageBitmap(null);
				}

			}
		});

		btn4.setOnLongClickListener(new OnLongClickListener() { 
			@Override
			public boolean onLongClick(View v) {

				Page audiop = db.getPage(pagep);
				if(audiop.getAudio() != null){

					audiop.setAudio(null);
					db.updatePage(audiop);
					db.getPage(pagep);
					Toast.makeText(getBaseContext(), "Audio deleted", Toast.LENGTH_LONG).show();

				} else{
					Toast.makeText(getBaseContext(), "No Audio, Click to record", Toast.LENGTH_LONG).show();
				}


				return true;
			}
		});
	}


	public void generatePages() {

		Random r = new Random();
		// gives a random integer between 4 and 10
		int i1 = r.nextInt(10 - 4) + 4; // number of pages

		int i2 = r.nextInt(5 - 1) + 1; // number of pages

		int i3 = r.nextInt(7 - 1) + 1; // number of pages

		//for each page
		for( int i= 0; i < i1; i++){
			Page page = new Page(null, null, null);
			// create new page and add to db
			long p = db.addPage(page);
			// get page from database and add to arraylist.
			pages.add(db.getPage(p));
		}

		Page p = pages.get(0);

		if(.5 < Math.random()){

			p.setText("Once upon a time... ");
			db.updatePage(p);
		}
		else {
			p.setText("It was a dark and stormy night. \n");
			db.updatePage(p);
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Some text");
		stringBuilder.append("Some text");
		stringBuilder.append("Some text");
		stringBuilder.append("Some text");

		for(Page page : pages){
			if(pages.indexOf(page) == 0 || pages.indexOf(page) == (pages.size()-1)){
				Resources res = getResources();
				Drawable drawable = res.getDrawable(R.drawable.untitled);
				Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bitMapData = stream.toByteArray();

				page.setImage(bitMapData);
				db.updatePage(page);
			}else{
				Resources res = getResources();
				Drawable drawable = res.getDrawable(R.drawable.simle);
				Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bitMapData = stream.toByteArray();

				page.setImage(bitMapData);
				db.updatePage(page);
				
				page.setText(MakeASentence() + " " + MakeASentence());
				db.updatePage(page);
			}
		}

		Page p2 = pages.get(pages.size()-1);

		if(.5 < Math.random()){

			p2.setText("And they lived happily ever after. THE END");
			db.updatePage(p2);
		}
		else {
			p2.setText("And the evil witch cooked them up for dinner. \n THE END");
			db.updatePage(p2);
		}

		for( int i= 0; i < i2; i++){
			setText();
		}

		for( int i= 0; i < i3; i++){
			setImage();
		}

	}

	public Page getRandomPage() {
		Random gen = new Random();
		int index = gen.nextInt(pages.size());
		Page item = pages.get(index);
		return item;
	}

	public void setText(){

		Page randomp = getRandomPage();
		if(pages.indexOf(randomp) == 0 || pages.indexOf(randomp) == (pages.size()-1)){
		}else{
			randomp.setText(null);
			db.updatePage(randomp);
		}
	}

	public void setImage(){
		Page randomp = getRandomPage();
		if(pages.indexOf(randomp) == 0 || pages.indexOf(randomp) == (pages.size()-1)){
		}else{
		randomp.setImage(null);
		db.updatePage(randomp);
		}
	}


	public void onClick(View v) {

		switch (v.getId()) {
		
		case R.id.savetextbtn:
			Page changep = db.getPage(pagep);
			changep.setText(et1.getText().toString());
			db.updatePage(changep);
			db.getPage(pagep);
			adapter.notifyDataSetChanged();
			Toast.makeText(getBaseContext(), "Text saved", Toast.LENGTH_LONG).show();
			break;
			
		case R.id.biv2:

			intent = new Intent(RandomStoryActivity.this, AudioActivity.class);
			intent.putExtra("pagep", pagep);
			startActivityForResult(intent, 1);
			break;

		case R.id.biv3:
			AlertDialog.Builder ad = new AlertDialog.Builder(RandomStoryActivity.this);
			ad.setMessage("Are you sure you want to exit?");

			ad.setCancelable(false);

			ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					//db.closeDB();
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
			
		case R.id.view1:
			registerForContextMenu(imgv1);
			openContextMenu(imgv1);
			break;

		case R.id.biv4:
			Story s = new Story();
			long sp = db.addStory(s);

			//for each page in arraylist
			for(Page p :pages){
				//and if their belongtoid equals 0  
				if(p.getBelongTo() == 0){
					//get page from db
					Page ps = db.getPage(p.getId());
					//set page's belongstoid to story id in db
					ps.setBelongTo(db.getStory(sp).getId());
					//update page in db
					db.updatePage(ps);
				}
			}
			db.closeDB();
			intent = new Intent(RandomStoryActivity.this,TitleAuthor2Activity.class);
			intent.putExtra("storyp", sp);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

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

	final int CONTEXT_MENU_VIEW =1;
	final int CONTEXT_MENU_VIEW3 =2;
	final int CONTEXT_MENU_ARCHIVE =3;

	private static int RESULT_LOAD_IMAGE = 1;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		//Context menu
		menu.setHeaderTitle("Select Image");          
		menu.add(Menu.NONE, CONTEXT_MENU_VIEW, Menu.NONE, "Select Image from Gallery");
		menu.add(Menu.NONE, CONTEXT_MENU_VIEW3, Menu.NONE, "Create Image");
		menu.add(Menu.NONE, CONTEXT_MENU_ARCHIVE, Menu.NONE, "Take Photo via Camera");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Toast.makeText(getApplicationContext(), 
					"ImageView: " + imgv1.getWidth() + " x " + imgv1.getHeight() + " " + picturePath, 
					Toast.LENGTH_LONG).show();

			Bitmap bitmap;
			bitmap = decodeSampledBitmapFromUri(
					selectedImage,
					imgv1.getWidth(), imgv1.getHeight());

			if(bitmap == null){
				Toast.makeText(getApplicationContext(), "the image data could not be decoded", Toast.LENGTH_LONG).show();

			}else{
				Toast.makeText(getApplicationContext(), 
						"Decoded Bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight(), 
						Toast.LENGTH_LONG).show();
				imgv1.setImageBitmap(bitmap);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				Page imagepage = db.getPage(pagep);
				imagepage.setImage(byteArray);
				db.updatePage(imagepage);

			}
		}
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				getContentResolver().notifyChange(mUri, null);
				//ContentResolver cr = getContentResolver();
				try {
					//Bitmap b = android.provider.MediaStore.Images.Media.getBitmap(cr, mUri);

					Toast.makeText(getApplicationContext(), 
							"ImageView: " + imgv1.getWidth() + " x " + imgv1.getHeight() + " " + mUri.toString(), 
							Toast.LENGTH_LONG).show();

					Bitmap bitmap;
					bitmap = decodeSampledBitmapFromUri(
							mUri,
							imgv1.getWidth(), imgv1.getHeight());

					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "the image data could not be decoded", Toast.LENGTH_LONG).show();

					}else{
						Toast.makeText(getApplicationContext(), 
								"Decoded Bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight(), 
								Toast.LENGTH_LONG).show();

						Drawable d = new BitmapDrawable(getResources(),bitmap);

						imgv1.setImageDrawable(d);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] byteArray = stream.toByteArray();
						Page imagepage = db.getPage(pagep);
						imagepage.setImage(byteArray);
						db.updatePage(imagepage);

					
					}


				} catch (Exception e) {
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}}
		}

		if(resultCode==1)           // check result code set in secondActivity that is 1
		{


		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case CONTEXT_MENU_VIEW:
		{
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			startActivityForResult(intent, RESULT_LOAD_IMAGE);
		}
		break;
		case CONTEXT_MENU_VIEW3:
		{

			//db.closeDB();
			Log.d("tag", "check if clicked");
			intent = new Intent(RandomStoryActivity.this, DrawToolActivity.class);
			intent.putExtra("pagep", pagep);
			intent.putExtra("openg", "openg");
			startActivityForResult(intent, 8);

		}
		break;
		case CONTEXT_MENU_ARCHIVE:
		{
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			File f = new File(Environment.getExternalStorageDirectory(),  "photo.jpg");
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			mUri = Uri.fromFile(f);
			startActivityForResult(i, TAKE_PICTURE);
		}
		break;
		}
		return super.onContextItemSelected(item);
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

	public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {

		Bitmap bm = null;

		try{
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}

		return bm;
	}

	public int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight); 
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth); 
			} 
		}
		return inSampleSize; 
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.d(tag,"onResume");

		Page p = db.getPage(pagep);
		if(p.getImage() != null){

			Bitmap b = BitmapFactory.decodeByteArray(
					p.getImage(), 0,p.getImage().length);
			imgv1.setImageBitmap(b);

			String string = p.getText();
			et1.setText(string);
		}
	}

	/**
	 * Generates a random story.
	 * to make things a little more interesting, we ensure that the nouns
	 * and articles don't repeat themselves in the same sentence
	 * @return String A sentence of dubious random content.
	 **/
	public String MakeASentence()
	{
		int iArticle1,
		iArticle2,
		iNoun1,
		iNoun2;

		iArticle1 = (int)(Math.random()*article.length);
		do
		{
			iArticle2 = (int)(Math.random()*article.length);
		} while (iArticle1 == iArticle2);

		iNoun1 = (int)(Math.random()*noun.length);
		do
		{
			iNoun2 = (int)(Math.random()*noun.length);
		} while (iNoun1 == iNoun2);


		StringBuffer sbTemp = new StringBuffer(80);
		sbTemp.append(article[iArticle1]);
		sbTemp.append(' ');
		sbTemp.append(noun[iNoun1]);
		sbTemp.append(' ');
		sbTemp.append(verb[(int)(Math.random()*verb.length)]);
		sbTemp.append(' ');
		sbTemp.append(preposition[(int)(Math.random()*preposition.length)]);
		sbTemp.append(' ');
		sbTemp.append(article[iArticle2]);
		sbTemp.append(' ');
		sbTemp.append(noun[iNoun2]);
		sbTemp.append('.');
		sbTemp.setCharAt(0,Character.toUpperCase(sbTemp.charAt(0)));
		return(sbTemp.toString());
	}
}
