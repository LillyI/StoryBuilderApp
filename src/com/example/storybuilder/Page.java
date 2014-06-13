package com.example.storybuilder;
/**
 * A page of a story which has some text, audio and image and storyid, title,, author, shared.
*
 * @author Emilome Ileso 
 * @version 2014.05.01
 */
public class Page {
	
	private int _pageid;
	public String _text; // pages's text
	private byte[] _audio; //page's audio
	private byte[] _image; //page's image
	private int _belongTo; //storyid
	
	/**
	 * Constructors
	 */


	public Page(byte[] image, byte[] audio, String text) {

		this._text = text;
		this._audio = audio;
		this._image= image;
	}


	public Page(int id, int belongTo, byte[] image, byte[] audio, String text) {
		this._pageid = id;
		this._text = text;
		this._audio = audio;
		this._image= image;
	}

	public Page(int belongTo, byte[] image, byte[] audio, String text) {
		this._belongTo = belongTo;
		this._text = text;
		this._audio = audio;
		this._image= image;
	}

	public Page(){}


	/**
	 * SETTERS AND GETTERS
	 */
	public int getId() {
		return _pageid;
	}
	public void setId(int id) {
		this._pageid = id;
	}


	/**
	 * getter
	 * @return all fields for this page.
	 */
	public String getAll() {
		return "Page [id=" + _pageid + ", storyid=" + _belongTo + ", text=" + _text + ", audio=" + _audio  + ", image=" + _image + "]";
	}

	public String getText() {
		return _text;
	}

	/**
	 * sets the text of a page
	 * @param text The text of the page.
	 */
	public void setText(String text) {
		this._text = text;
	}

	public String getTitle() {
		return null;
	}
	public byte[] getAudio() {
		return this._audio;
	}

	public void setAudio(byte[] audio) {
		this._audio = audio;
	}

	public byte[] getImage() {
		return this._image;
	}

	/**
	 * setter
	 * @param author The author of the Story.
	 */
	public void setImage(byte[] image) {
		this._image = image;
	}

	@Override
	public String toString() {
		return "* Page";
	}

	public int getBelongTo() {
		return _belongTo;
	}

	public void setBelongTo(int belongTo) {
		this._belongTo = belongTo;
	}

}