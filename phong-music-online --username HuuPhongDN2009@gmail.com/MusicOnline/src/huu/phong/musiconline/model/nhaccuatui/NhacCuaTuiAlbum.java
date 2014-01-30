package huu.phong.musiconline.model.nhaccuatui;

import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

import huu.phong.musiconline.model.Album;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.sites.NhacCuaTui;
import huu.phong.musiconline.sites.Site;

import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiAlbum extends Album{

	private static final long serialVersionUID = -1189068517308807038L;

	@SerializedName("Description")
	public String description;

	@SerializedName("Genre")
	public String genre;

	@SerializedName("NumSong")
	public String numberOfSong;

	@SerializedName("ObjType")
	public String type;
	public String id;

	@SerializedName("Liked")
	public String liked;

	@SerializedName("LinkShare")
	public String linkShare;

	@SerializedName("Listened")
	public String listened;

	@SerializedName("PlaylistCover")
	public String playlistCover;

	@SerializedName("PlaylistId")
	public String playlistId;

	@SerializedName("PlaylistImage")
	public String playlistImage;

	@SerializedName("PlaylistKey")
	public String playlistKey;

	@SerializedName("PlaylistThumb")
	public String thumbnail;

	@SerializedName("PlaylistTitle")
	public String title;

	@SerializedName("Singername")
	public String artist;
	
	private transient ImageIcon icon;

	@Override
	public List<? extends ISong> getSongs() throws IOException {
		return NhacCuaTui.getInstance().getAlbum(playlistId);
	}

	@Override
	public boolean isAlbum() {
		return false;
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
	
	@Override
	public ImageIcon getThumbnail() {
		if (icon == null) icon = getThumbnail(thumbnail);
		return icon;
	}
	
	@Override
	public boolean isThumbnailLoaded() {
		return icon != null;
	}
	
	@Override
	public boolean hasThumbnail() {
		return thumbnail != null && !thumbnail.equals("");
	}
}