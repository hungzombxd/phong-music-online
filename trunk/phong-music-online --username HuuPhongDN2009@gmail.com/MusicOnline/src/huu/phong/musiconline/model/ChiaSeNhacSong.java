package huu.phong.musiconline.model;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.Site;

public class ChiaSeNhacSong extends Song {
	
	private static final long serialVersionUID = -8706694968193862338L;
	
	public String title;
	
	public String artist;
	
	public String link;

	public Map<Format, String> directLinks;
	
	public String id;
	
	public ChiaSeNhacSong(){
		id = new Date().toString();
	}
	
	@Override
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	public void setArtist(String artist) {
		// TODO Auto-generated method stub
		
	}

	public void setLink(String link) {
		// TODO Auto-generated method stub
		
	}

	public void setSite(Site chiaSeNhac) {
		// TODO Auto-generated method stub
		
	}

	public void setQuality(Format format) {
		// TODO Auto-generated method stub
		
	}
}
