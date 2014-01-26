package huu.phong.musiconline.model.nhaccuatui;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiLyricList {

	@SerializedName("Result")
	public boolean result;

	@SerializedName("Data")
	public NhacCuaTuiLyric lyric;
}