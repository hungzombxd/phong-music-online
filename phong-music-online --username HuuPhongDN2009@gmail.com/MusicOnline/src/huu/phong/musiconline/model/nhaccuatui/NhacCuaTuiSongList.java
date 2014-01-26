package huu.phong.musiconline.model.nhaccuatui;

import huu.phong.musiconline.model.ISong;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiSongList {

	@SerializedName("IsMore")
	public boolean more;

	@SerializedName("Data")
	private List<NhacCuaTuiSong> songs;

	@SerializedName("Result")
	public boolean result;
	
	public List<? extends ISong> getSongs() {
		return songs;
	}
	
	public void setSongs(List<NhacCuaTuiSong> songs){
		this.songs = songs;
	}
}