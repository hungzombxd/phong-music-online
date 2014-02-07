package huu.phong.musiconline.model.nhaccuatui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	
	public List<String> getLyric(){
		if (lyric == null || lyric.equals("")) return Collections.emptyList();
		return Arrays.asList(lyric.split("\n"));
	}
}