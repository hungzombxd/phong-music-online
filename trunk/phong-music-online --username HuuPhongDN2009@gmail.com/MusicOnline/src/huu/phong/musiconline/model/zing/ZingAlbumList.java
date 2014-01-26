package huu.phong.musiconline.model.zing;

import huu.phong.musiconline.model.IAlbum;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ZingAlbumList{
	private int numFound;
	private int start;
	@SerializedName("docs")
	private List<ZingAlbum> albums;

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

	public List<? extends IAlbum> getAlbums() {
		return albums;
	}
}
