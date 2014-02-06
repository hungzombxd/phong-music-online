package huu.phong.musiconline.model;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.ChiaSeNhac;
import huu.phong.musiconline.sites.Site;
import huu.phong.musiconline.utils.Utils;

public class ChiaSeNhacSong extends Song {
	
	private static final long serialVersionUID = -8706694968193862338L;
	
	public String title;
	
	public String artist;
	
	public String link;

	public Map<Format, String> directLinks;
	
	public String id;
	
	public Format quality;
	
	public String description;
	
	public ChiaSeNhacSong(){
		id = new Date().toString();
	}
	
	@Override
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		if (directLinks == null){
			directLinks = ChiaSeNhac.getInstance().getLink(link);
		} else {
			String oldDirectLink = getDirectLink(directLinks, format);
			if (!Utils.isURLAvailable(oldDirectLink)) directLinks = ChiaSeNhac.getInstance().getLink(link);
		}
		return getDirectLink(directLinks, format);
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
		return Site.CHIA_SE_NHAC;
	}

	@Override
	public String getFullTitle() {
		return title + (artist == null || artist.equals("") ? "" : String.format(" - %s", artist));
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setQuality(Format format) {
		quality = format;
	}
	
	@Override
	public Format getQuality() {
		return quality;
	}
	
	@Override
	public String getArtist() {
		return artist;
	}
}
