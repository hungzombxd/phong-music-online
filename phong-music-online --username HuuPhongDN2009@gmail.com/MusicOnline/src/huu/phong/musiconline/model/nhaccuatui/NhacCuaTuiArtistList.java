package huu.phong.musiconline.model.nhaccuatui;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiArtistList {

	@SerializedName("IsMore")
	public boolean more;

	@SerializedName("Data")
	public List<NhacCuaTuiArtist> artists;

	@SerializedName("Result")
	public boolean result;
}