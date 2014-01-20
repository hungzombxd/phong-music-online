package huu.phong.musiconline.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ZingSong {
	private int numFound;
	private int start;
	@SerializedName("docs")
	private List<Song> songs;
	
	public ZingSong(){
		
	}

	public ZingSong(int numFound, int start, List<Song> docs) {
		super();
		this.numFound = numFound;
		this.start = start;
		this.songs = docs;
	}

	public int getNumFound() {
		return numFound;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setDocs(List<Song> songs) {
		this.songs = songs;
	}
}
