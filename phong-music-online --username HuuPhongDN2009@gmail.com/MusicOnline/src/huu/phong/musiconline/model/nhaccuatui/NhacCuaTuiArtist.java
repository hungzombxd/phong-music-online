package huu.phong.musiconline.model.nhaccuatui;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiArtist {

	@SerializedName("ArtistAvatar")
	public String thumbnail;

	@SerializedName("ArtistId")
	public String id;

	@SerializedName("ArtistName")
	public String name;

	@SerializedName("ObjType")
	public String type;

	@SerializedName("PlaylistCount")
	public int count;

	@SerializedName("SongCount")
	public int numberOfSong;

	@SerializedName("VideoCount")
	public int numberOfVideo;
}