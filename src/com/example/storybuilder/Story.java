package com.example.storybuilder;
/**
 * A story which has a storyid, title,, author and string shared.
 * A story can be added or deleted from the SQLite database.
 * 
 * @author Emilome Ileso 
 * @version 2014.05.01
 */

public class Story {

	private int _storyid;
	private String _title;
	private String _author;
	private String _shared;

	/**
	 * Constructors
	 */

	public Story(){}

	public Story(String title, String author, String shared) {
		super();
		this._title = title;
		this._author = author;
		this._shared = "Not Shared";
	}

	public Story(int id, String title, String author, String shared) {
		super();
		this._title = title;
		this._author = author;
		this._shared = shared;
	}

	public Story(int id, String title, String author) {
		super();
		this._storyid = id;
		this._title = title;
		this._author = author;
	}

	/**
	 * SETTERS AND GETTERS
	 */
	public int getId() {
		return _storyid;
	}
	public void setId(int id) {
		this._storyid = id;
	}
	public String getTitle() {
		return _title;
	}

	/**
	 * sets the title of the story-
	 * @param title The title of the Story.
	 */
	public void setTitle(String title) {
		this._title = title;
	}
	public String getAuthor() {
		return _author;
	}

	/**
	 * setter
	 * @param author The author of the Story.
	 */
	public void setAuthor(String author) {
		this._author = author;
	}
	@Override
	public String toString() {
		return _title + " by " + _author;
	}

	/**
	 * getter
	 * @return all fields for this story.
	 */
	public String getAll()  {
		return "Story [id=" + _storyid + ", title=" + _title + ", author=" + _author + ", shared=" + _shared + "]";
	}

	public String getShared() {
		return _shared;
	}
	public void setShared(String shared) {
		this._shared = shared;
	}

}