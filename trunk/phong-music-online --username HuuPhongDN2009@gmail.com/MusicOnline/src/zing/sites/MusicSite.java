package zing.sites;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zing.model.Album;
import zing.model.ItemCombo;
import zing.model.Song;

public abstract class MusicSite{
	public static final int MP3_ZING_VN = 0;
	public static final int NHACCUATUI_COM = 1;
	public static final int MUSIC_GO_VN = 2;
	
	protected String error = null;
	protected String information = null;
	
	public abstract List<Song> searchSong(String value, int page, String filter) throws IOException;
	
	public abstract String getLink(String html) throws IOException;
	
	public abstract List<Album> searchAlbum(String value, int page, String filter) throws IOException;
	
	public abstract List<Song> getAlbum(String html) throws IOException;
	
	public List<String> getLyric(Song song) throws IOException{
		List<String> lyrics = new ArrayList<String>();
		return lyrics;
	}
	
	public abstract ItemCombo[] getBys();
	
	public abstract ItemCombo[] getFilters();
	
	public String getError(){
		return error;
	}
	
	public String getInformation(){
		return information;
	}
}
