package huu.phong.musiconline.model.zing;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.utils.Utils;
import huu.phong.musiconline.sites.Zing;
import huu.phong.musiconline.Configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.google.gson.annotations.SerializedName;

public class ZingSong extends ZingMedia implements ISong {
	
	private static final long serialVersionUID = -1080772505347758185L;
	
	private static final String DESCRIPTIONS[] = {"Thời lượng: %s", "Lượt nghe: %s", "Thể loại: %s", "%s"};
	
	@SerializedName("song_id")
	private String id;
	@SerializedName("source")
	private Map<Format, String> directLinks;
//	private transient Format currentFormat;
	private String songInfo;
//	private String bitrate;
	private int duration;
//	@SerializedName("have_rbt")
//	private boolean rbt;
//	@SerializedName("download_status")
//	private int status;
//	private String copyright;
//	@SerializedName("link_download")
//	private Map<String, String> downloadLinks;
//	@SerializedName("song_id_encode")
//	private String idEncode;
//	private String album;
//	@SerializedName("composer_id")
//	private String composerId;
//	private String composer;
//	private String thumbnail;
//	@SerializedName("album_cover")
//	private String albumArt;
	
	public ZingSong() {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ImageIcon getThumbnail() {
		return null;
	}

	@Override
	public boolean hasThumbnail() {
		return false;
	}

	@Override
	public boolean isThumbnailLoaded() {
		return false;
	}

	@Override
	public String getDetailTitle() {
		String[] values = {Utils.toDuaration(duration * 1000), Utils.formatNumber(count), genre, songInfo};
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < DESCRIPTIONS.length; i++){
			if (values[i] != null && !values[i].equals("")){
				builder.append(String.format(DESCRIPTIONS[i], values[i]));
				builder.append(" | ");
			}
		}
		if (builder.length() > 0) builder.delete(builder.length() - 3, builder.length());
		return String.format("<html><b>%s</b><br/>%s<br/>Website: %s<html>", getFullTitle(), builder.toString(), site.getHost());
	}

	@Override
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		if (directLinks != null) {
			String oldDirectLink = getDirectLink(directLinks, format, site);
			if (Utils.isURLAvailable(oldDirectLink, site.getSongAgent())){
				return oldDirectLink;
			}
		}

		if (!Utils.isURLAvailable(link)) throw new IOException(String.format("Link %s is not available", link));
		directLinks = Zing.getInstance().getLink(id);
		return getDirectLink(directLinks, format, site);
	}

	@Override
	public Map<Format, String> getDirectLinks(){
		return directLinks;
	}

	public void setDirectLink(Format format, String directLink) {
		if (directLinks == null){
			directLinks = new HashMap<Format, String>();
		}
		directLinks.put(format, directLink);
	}

	public Format getQuality() {
		if (directLinks == null) return quality;
		if (directLinks.containsKey(Format.LOSSLESS)) {
			quality = Format.LOSSLESS;
		}else if (directLinks.containsKey(Format.MP3_320_KBPS)) {
			quality = Format.MP3_320_KBPS;
		}
		return quality;
	}
}
