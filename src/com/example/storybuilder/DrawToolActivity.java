package com.example.storybuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Operations:
 * upload image via camera or gallery
 * draw on images
 * rotate image
 * 
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 * 
 * References 
 * Android SDK: Drawing with Opacity [Online] 2013, Sue Smith
 * Available from: http://code.tutsplus.com/tutorials/android-sdk-drawing-with-opacity--mobile-19682
 *  [accessed 04 April 2014]
 *
 */
public class DrawToolActivity extends Activity{

	Bitmap b;
	Intent intent, intent2;
	ImageView imgv;
	TextView spinnerTarget;
	ImageButton exit_btn3;
	ImageButton save_btn3;
	ImageButton ib_cam;
	ActionBar bar;
	String value = " ";
	Bitmap mPhoto = null;
	Spinner spinner1;

	//custom drawing view
	private DrawingView drawView;
	//buttons
	private ImageButton currPaint;
	//sizes
	private float smallBrush, mediumBrush, largeBrush;
	private Uri mUri;
	MySQLiteHelper db;
	private static final int TAKE_PICTURE = 0;
	long pagep;
	int c = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_tool);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		db = new MySQLiteHelper(getApplicationContext());


		bar = getActionBar();

		//set ActionBar Background color to colordrawable
		bar.setBackgroundDrawable(new ColorDrawable(0xff63b7cb));

		bar.setCustomView(R.layout.a);

		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);


		//get drawing view
		drawView = (DrawingView)findViewById(R.id.drawing);

		spinnerTarget = (TextView)findViewById(R.id.spinnerTarget);

		//get the palette and first color button
		RelativeLayout paintLayout = (RelativeLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		//sizes from dimensions
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		//set initial size
		drawView.setBrushSize(mediumBrush);

		imgv = (ImageView) findViewById(R.id.view1);

		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			pagep = extras.getLong("pagep");
		}

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.add("Options");
		list.add("Drawing pen");
		list.add("New drawing");
		list.add("Save");
		list.add("Eraser");
		list.add("Rotate background image 90 degrees");
		list.add("Background color");
		list.add("Upload background image");
		list.add("Take a image");
		list.add("Exit");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);

		dataAdapter.setDropDownViewResource(R.layout.spinner_layout);

		spinner1.setAdapter(dataAdapter);

		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	private static int RESULT_LOAD_IMAGE = 1;

	private static int RESULT_LOAD_IMAGE2 = 2;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
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
					"ImageView: " + drawView.getWidth() + " x " + drawView.getHeight() + " " + picturePath, 
					Toast.LENGTH_LONG).show();

			Bitmap bitmap;

			bitmap = decodeSampledBitmapFromUri(
					selectedImage,
					drawView.getWidth(), drawView.getHeight());

			if(bitmap == null){
				Toast.makeText(getApplicationContext(), "the image data could not be decoded", Toast.LENGTH_LONG).show();

			}
			mPhoto = bitmap;
			Drawable d = new BitmapDrawable(getResources(),bitmap);
			Log.d("b","b" );
			drawView.setBackgroundDrawable(d);
		}

		if (requestCode == RESULT_LOAD_IMAGE2 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();


			Toast.makeText(getApplicationContext(), 
					"ImageView: " + drawView.getWidth() + " x " + drawView.getHeight() + " " + picturePath, 
					Toast.LENGTH_LONG).show();

			Bitmap bitmap;

			bitmap = decodeSampledBitmapFromUri(
					selectedImage,
					drawView.getWidth(), drawView.getHeight());

			if(bitmap == null){
				Toast.makeText(getApplicationContext(), "the image data could not be decoded", Toast.LENGTH_LONG).show();

			}else{
				Toast.makeText(getApplicationContext(), 
						"Decoded Bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight(), 
						Toast.LENGTH_LONG).show();

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
							"ImageView: " + drawView.getWidth() + " x " + drawView.getHeight() + " " + mUri, 
							Toast.LENGTH_LONG).show();

					Bitmap bitmap;
					bitmap = decodeSampledBitmapFromUri(
							mUri,
							drawView.getWidth(), drawView.getHeight());

					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "the image data could not be decoded", Toast.LENGTH_LONG).show();

					}else{
						Toast.makeText(getApplicationContext(), 
								"Decoded Bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight(), 
								Toast.LENGTH_LONG).show();

						mPhoto = bitmap;
						Drawable d = new BitmapDrawable(getResources(),bitmap);

						drawView.setBackgroundDrawable(d);
					}


				} catch (Exception e) {
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

				}
			}}

	}
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}



	//user clicked paint
	public void paintClicked(View view){
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			//update ui
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to exit?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				final Intent intent=new Intent();
				setResult(2,intent); 
				finish();
			}
		})
		.setNegativeButton("No", null)
		.show();
	}


	public static Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable =view.getBackground();
		if (bgDrawable!=null) 
			bgDrawable.draw(canvas);
		else 
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}

	public void openg(){
		intent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(intent, RESULT_LOAD_IMAGE);

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

	public static Bitmap drawableToBitmap (Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();

			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(
						b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				throw ex;
			}
		}
		return b;
	}


	class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {


			if (spinner1.getSelectedItem().toString().equals("Drawing pen")) {
				//draw button clicked
				final Dialog brushDialog = new Dialog(DrawToolActivity.this);
				brushDialog.setTitle("Brush size:");
				brushDialog.setContentView(R.layout.brush_chooser);
				//listen for clicks on size buttons
				ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
				smallBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(smallBrush);
						drawView.setLastBrushSize(smallBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
				mediumBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(mediumBrush);
						drawView.setLastBrushSize(mediumBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
				largeBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(largeBrush);
						drawView.setLastBrushSize(largeBrush);
						brushDialog.dismiss();
					}
				});
				//show and wait for user interaction
				brushDialog.show();
			}
			if (spinner1.getSelectedItem().toString().equals("New drawing")) {
				//new button

				AlertDialog.Builder newad = new AlertDialog.Builder(DrawToolActivity.this);

				newad.setTitle("Create new Image");
				newad.setMessage("Create new Image (you will lose the current drawing)?");

				newad.setCancelable(false);

				newad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						drawView.startNew();
						drawView.setBackgroundDrawable(null);
						dialog.dismiss();
					}
				});

				// Setting Negative "NO" Button
				newad.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				newad.show();
			}

			if (spinner1.getSelectedItem().toString().equals("Save")) {
				//save drawing
				AlertDialog.Builder savead = new AlertDialog.Builder(DrawToolActivity.this);
				savead.setTitle("Save Image?");
				savead.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){

						Page setpage = db.getPage(pagep);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						getBitmapFromView(drawView).compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] byteArray = stream.toByteArray();

						setpage.setImage(byteArray);
						db.updatePage(setpage);
						db.getPage(pagep);

						drawView.destroyDrawingCache();

						Log.d("tag", "check if clicked2");
						final Intent intent=new Intent();
						setResult(8,intent); 
						finish();		
					}
				});
				savead.setNegativeButton("No", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
				savead.show();
			}

			if (spinner1.getSelectedItem().toString().equals("Rotate background image 90 degrees")) {
				if(drawView.getBackground() != null){

					if (mPhoto!=null) {
						Matrix matrix = new Matrix();
						matrix.postRotate(90);
						mPhoto = Bitmap.createBitmap(mPhoto , 0, 0, mPhoto.getWidth(), mPhoto.getHeight(), matrix, true);

						Drawable d =new BitmapDrawable(getResources(),mPhoto);
						drawView.setBackgroundDrawable(d);
					}else{
						Toast.makeText(getApplicationContext(), 
								"no background image", 
								Toast.LENGTH_LONG).show();
					}
				}
			}



			if (spinner1.getSelectedItem().toString().equals("Background color")) {
				final CharSequence[] items = {"Red", "Green", "Blue", "Yellow", "Magenta", "White", "Black", "Orange", "Brown", "Grey"};

				final AlertDialog.Builder builder = new AlertDialog.Builder(DrawToolActivity.this);
				builder.setTitle("Pick a image background color");
				builder.setCancelable(true);
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						switch (item){                 
						case 0: 
							drawView.setBackgroundColor(Color.RED);
							break;

						case 1: 
							drawView.setBackgroundColor(Color.GREEN);
							break;

						case 2: 
							drawView.setBackgroundColor(Color.BLUE);
							break;
						case 3:
							drawView.setBackgroundColor(Color.YELLOW);
							break;
						case 4:
							drawView.setBackgroundColor(Color.MAGENTA);
							break;
						case 5:
							drawView.setBackgroundColor(Color.WHITE);
							break;
						case 6:
							drawView.setBackgroundColor(Color.BLACK);
							break;
						case 7:
							drawView.setBackgroundColor(Color.parseColor("#FF660000"));
							break;
						case 8:
							drawView.setBackgroundColor(Color.parseColor("#A52A2A"));
							break;
						case 9:
							drawView.setBackgroundColor(Color.GRAY);
							break;
						}
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			}
			if (spinner1.getSelectedItem().toString().equals("Upload background image")) {
				openg();
			}

			if (spinner1.getSelectedItem().toString().equals("Take a image")) {
				Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
				File f = new File(Environment.getExternalStorageDirectory(),  "photo.jpg");
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				mUri = Uri.fromFile(f);
				startActivityForResult(i, TAKE_PICTURE);
			}


			if (spinner1.getSelectedItem().toString().equals("Exit")) {

				AlertDialog.Builder ad = new AlertDialog.Builder(DrawToolActivity.this);
				ad.setMessage("Are you sure you want to exit without saving?");

				ad.setCancelable(false);

				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {

						final Intent intent=new Intent();
						setResult(1,intent); 
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
			}

			if (spinner1.getSelectedItem().toString().equals("Eraser")) {
				//switch to erase - choose size
				final Dialog brushDialog = new Dialog(DrawToolActivity.this);
				brushDialog.setTitle("Eraser size:");
				brushDialog.setContentView(R.layout.brush_chooser);
				//size buttons
				ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
				smallBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(smallBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
				mediumBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(mediumBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
				largeBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(largeBrush);
						brushDialog.dismiss();
					}
				});
				brushDialog.show();
			}

			spinner1.setSelection(0);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

}
