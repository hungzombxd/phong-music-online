package huu.phong.musiconline.model.nhaccuatui;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.Song;
import huu.phong.musiconline.sites.Site;

public class NhacCuaTuiSong extends Song{

	private static final long serialVersionUID = 2394970920947385612L;
	
	@SerializedName("ObjType")
	public String type;
	
	@SerializedName("PlaylistID")
	public String albumId;

	@SerializedName("PlaylistThumb")
	public String albumThumbnail;
	
//	@SerializedName("Status")
//	private int status;
//	private String id;

	@SerializedName("Image")
	public String thumbnail;

	@SerializedName("Liked")
	public String liked;

	@SerializedName("LinkShare")
	public String linkShare;

	@SerializedName("Listened")
	public String listened;

	@SerializedName("Singername")
	public String artist;

	@SerializedName("SongId")
	public String id;

	@SerializedName("SongKey")
	public String songKey;

	@SerializedName("SongTitle")
	public String title;

	@SerializedName("StreamURL")
	public String url;
	
	@SerializedName("LinkdownHQ")
	public String urlHq;

	@Override
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		if (Format.MP3_320_KBPS.equals(format) && urlHq != null) {
			return urlHq;
		}
		return url;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Site getSite() {
		return Site.NHAC_CUA_TUI;
	}

	@Override
	public String getFullTitle() {
		return title + (artist == null || artist.equals("") ? "" : String.format(" - %s", artist));
	}

	@Override
	public String getDetailTitle() {
		return String.format("<html><b>%s</b><br/>Number like: %s</html>", getFullTitle(), liked);
	}

}
