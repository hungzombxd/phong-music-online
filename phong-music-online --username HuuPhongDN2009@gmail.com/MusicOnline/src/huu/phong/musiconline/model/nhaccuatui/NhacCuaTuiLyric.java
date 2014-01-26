package huu.phong.musiconline.model.nhaccuatui;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiLyric {

	@SerializedName("Lyric")
	public String lyric;

	@SerializedName("LyricId")
	public String id;

	@SerializedName("TimedLyric")
	public String timedLyric;

	@SerializedName("UsernameCreated")
	public String author;
}