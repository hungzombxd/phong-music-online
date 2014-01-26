package huu.phong.musiconline.model.nhaccuatui;

import huu.phong.musiconline.model.ISong;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiAlbumDetail {

	@SerializedName("IsMore")
	public boolean more;

	@SerializedName("Data")
	public List<NhacCuaTuiSong> songs;

	@SerializedName("Result")
	public boolean result;
	
	public List<? extends ISong> getSongs(){
		return songs;
	}
}