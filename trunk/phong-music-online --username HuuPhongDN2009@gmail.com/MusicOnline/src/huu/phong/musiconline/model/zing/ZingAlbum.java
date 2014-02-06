package huu.phong.musiconline.model.zing;

import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.sites.Zing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import com.google.gson.annotations.SerializedName;

public class ZingAlbum extends ZingMedia implements IAlbum{
	
	private static final long serialVersionUID = -2824232816762868663L;
	
//	private String albumInfo;
	private List<? extends ISong> songs = new ArrayList<ISong>();
//	private long time;
//	private boolean highQuality;
	@SerializedName("cover")
	private String albumArt;
	private transient ImageIcon icon;
	@SerializedName("playlist_id")
	private String id;
	@SerializedName("modified_date")
	private Date modifiedDate;
	@SerializedName("is_album")
	private boolean album;
//	private int year;
//	private long comments;
	
	public ZingAlbum(){
		
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ImageIcon getThumbnail() {
		if (icon == null){
			icon = getThumbnail(String.format("%s/%s", Zing.DOMAIN_IMAGE, albumArt));
		}
		return icon;
	}

	@Override
	public boolean hasThumbnail() {
		return albumArt != null;
	}
	
	@Override
	public boolean isThumbnailLoaded() {
		return icon != null;
	}

	@Override
	public Date getDate() {
		return modifiedDate;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<? extends ISong> getSongs() throws IOException {
		if (songs.isEmpty()) songs = Zing.getInstance().getAlbum(id);
		return songs;
	}

	@Override
	public boolean isAlbum() {
		return album;
	}

	public void setAlbumArt(String attribute) {
	}
}
