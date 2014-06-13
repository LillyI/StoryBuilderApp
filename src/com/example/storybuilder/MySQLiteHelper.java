package com.example.storybuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
SQLite database.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 8;
	// Database Name
	private static final String DATABASE_NAME = "StoriesPage.db";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create Story table
		String CREATE_STORY_TABLE = "CREATE TABLE Stories ( " +
				"storyid INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"title TEXT, "+
				"author TEXT, "+
				"shared TEXT )";

		String CREATE_PAGE_TABLE = "CREATE TABLE Pages ( " +
				"pageid INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"belongsto INTEGER, "+
				"text TEXT, "+
				"image BLOB, "+
				"audio BLOB )";

		// create Storys table
		db.execSQL(CREATE_STORY_TABLE);

		db.execSQL(CREATE_PAGE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older Storys table if existed
		db.execSQL("DROP TABLE IF EXISTS Stories");
		db.execSQL("DROP TABLE IF EXISTS Pages");

		// create fresh Storys table
		this.onCreate(db);
	}
	//---------------------------------------------------------------------

	/**
	 * CRUD operations (create "add", read "get", update, delete) Story + get all Storys + delete all Storys
	 */

	// Storys table name
	private static final String TABLE_STORIES = "Stories";

	// Storys Table Columns names
	private static final String KEY_ID = "storyid";
	private static final String KEY_TITLE = "title";
	private static final String KEY_AUTHOR = "author";
	private static final String KEY_SHARED = "shared";

	//private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_AUTHOR};

	// Storys table name
	private static final String TABLE_PAGES = "Pages";

	// Storys Table Columns names
	private static final String PAGE_KEY_ID = "pageid";
	private static final String PAGE_KEY_IMAGE= "image";
	private static final String PAGE_KEY_AUDIO = "audio";
	private static final String PAGE_KEY_TEXT = "text";
	private static final String PAGE_KEY_BELONGSTO = "belongsto";

	//private static final String[] COLUMNS2 = {PAGE_KEY_ID, KEY_ID, PAGE_KEY_IMAGE,PAGE_KEY_AUDIO,PAGE_KEY_TEXT};
	//private static final String[] COLUMNS2 = {PAGE_KEY_ID,PAGE_KEY_BELONGSTO,PAGE_KEY_TEXT,PAGE_KEY_IMAGE,PAGE_KEY_AUDIO};

	public long addStory(Story story){
		Log.d("addStory", story.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, story.getTitle()); // get title 
		values.put(KEY_AUTHOR, story.getAuthor()); // get author
		values.put(KEY_SHARED, story.getShared());
		

		// 3. insert
		long storyid = db.insert(TABLE_STORIES, // table
				null, //nullColumnHack
				values); // key/value -> keys = column names/ values = column values

		return storyid;
	}


	/*
	 * Creating a todo
	 */
	public long addPage(Page page) {
		Log.d("addPage", page.toString());
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(PAGE_KEY_BELONGSTO, page.getBelongTo());
		values.put(PAGE_KEY_TEXT, page.getText());
		values.put(PAGE_KEY_IMAGE, page.getImage());
		values.put(PAGE_KEY_AUDIO, page.getAudio());

		// insert row
		long pageid = db.insert(TABLE_PAGES, null, values);

		return pageid;
	}

	public Story getStory(long storyid){
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_STORIES + " WHERE "
				+ KEY_ID + " = " + storyid;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Story story = new Story();
		story.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		story.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
		story.setAuthor(c.getString(c.getColumnIndex(KEY_AUTHOR)));
		story.setShared(c.getString(c.getColumnIndex(KEY_SHARED)));

		Log.d("getStory("+storyid+")", story.toString());

		// 5. return Story
		return story;
	}

	public Page getPage(long pageid){

		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PAGES + " WHERE "
				+ PAGE_KEY_ID + " = " + pageid;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Page page = new Page();
		page.setId(c.getInt(c.getColumnIndex(PAGE_KEY_ID)));
		page.setBelongTo(c.getInt(c.getColumnIndex(PAGE_KEY_BELONGSTO)));
		page.setText((c.getString(c.getColumnIndex(PAGE_KEY_TEXT))));
		page.setImage((c.getBlob(c.getColumnIndex(PAGE_KEY_IMAGE))));
		page.setAudio((c.getBlob(c.getColumnIndex(PAGE_KEY_AUDIO))));

		Log.d("getPage("+pageid+")", page.toString());

		// 5. return Story
		return page;
	}

	// Get All Storys
	public List<Story> getAllStories() {
		List<Story> Storys = new LinkedList<Story>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_STORIES;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build Story and add it to list
		Story story = null;
		if (cursor.moveToFirst()) {
			do {
				story = new Story();
				story.setId(Integer.parseInt(cursor.getString(0)));
				story.setTitle(cursor.getString(1));
				story.setAuthor(cursor.getString(2));
				story.setShared(cursor.getString(3));

				// Add Story to Storys
				Storys.add(story);
			} while (cursor.moveToNext());
		}

		Log.d("getAllStorys()", Storys.toString());

		// return Storys
		return Storys;
	}
	
	

	public List<Page> getAllPages() {
		List<Page> pages = new LinkedList<Page>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_PAGES;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build Story and add it to list
		Page page = null;
		if (cursor.moveToFirst()) {
			do {
				page = new Page();
				page.setId(Integer.parseInt(cursor.getString(0)));
				page.setBelongTo(Integer.parseInt(cursor.getString(1)));
				page.setText(cursor.getString(2));
				page.setImage(cursor.getBlob(3));
				page.setAudio(cursor.getBlob(4));

				// Add Story to Storys
				pages.add(page);
			} while (cursor.moveToNext());
		}

		Log.d("getAllPages()", pages.toString());

		// return Storys
		return pages;
	}
	
	public List<Page> getAllPagesByBelongsto(long belongsto) throws SQLException  {
	    List<Page> pages = new ArrayList<Page>();

	    String selectQuery = "SELECT  * FROM " + TABLE_PAGES + " WHERE "
	            + PAGE_KEY_BELONGSTO + " = ?";
	    Log.e("sql", selectQuery);

	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, new String[] { Long.toString(belongsto)});

	    Page page = null;
	    if (c.moveToFirst()) {
	        do {
				page = new Page();
				page.setId(c.getInt((c.getColumnIndex(PAGE_KEY_ID))));
				page.setBelongTo(c.getInt((c.getColumnIndex(PAGE_KEY_BELONGSTO))));
				page.setText(c.getString((c.getColumnIndex(PAGE_KEY_TEXT))));
				page.setImage(c.getBlob((c.getColumnIndex(PAGE_KEY_IMAGE))));
				page.setAudio(c.getBlob((c.getColumnIndex(PAGE_KEY_AUDIO))));
				
				pages.add(page);
	        } while (c.moveToNext());
	    }


		Log.d("getAllPagesByBelongsto()", pages.toString());
	    return pages;
	}



	// Updating single Story
	public int updateStory(Story story) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put("title", story.getTitle()); // get title 
		values.put("author", story.getAuthor()); // get author
		values.put("shared", story.getShared());


		// 3. updating row
		int i = db.update(TABLE_STORIES, //table
				values, // column/value
				KEY_ID+" = ?", // selections
				new String[] { String.valueOf(story.getId()) }); //selection args

		// 4. close
		db.close();

		return i;

	}

	// Updating single Page
	public int updatePage(Page page) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(PAGE_KEY_BELONGSTO, page.getBelongTo());
		values.put(PAGE_KEY_TEXT, page.getText());
		values.put(PAGE_KEY_IMAGE, page.getImage());
		values.put(PAGE_KEY_AUDIO, page.getAudio());

		// 3. updating row
		int i = db.update(TABLE_PAGES, //table
				values, // column/value
				PAGE_KEY_ID+" = ?", // selections
				new String[] { String.valueOf(page.getId()) }); //selection args

		// 4. close
		db.close();

		return i;

	}

	// Deleting single Story
	public void deleteStory(Story story) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_STORIES,
				KEY_ID+" = ?",
				new String[] { String.valueOf(story.getId()) });

		// 3. close
		db.close();

		Log.d("deleteStory", story.toString());

	}

	// Deleting single Story
	public void deletePage(long pageid) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_PAGES,
				PAGE_KEY_ID+" = ?",
				new String[] { String.valueOf(pageid) });
		
		Log.d("deletePage", "");

	}
	
	 // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
