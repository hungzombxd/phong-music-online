package huu.phong.musiconline.model.nhaccuatui;

import huu.phong.musiconline.model.IAlbum;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiAlbumList {

	@SerializedName("IsMore")
	public boolean more;

	@SerializedName("Message")
	public boolean message;

	@SerializedName("Data")
	public List<NhacCuaTuiAlbum> albums;

	@SerializedName("Result")
	public boolean result;
	
	public List<? extends IAlbum> getAlbums(){
		return albums;
	}
}