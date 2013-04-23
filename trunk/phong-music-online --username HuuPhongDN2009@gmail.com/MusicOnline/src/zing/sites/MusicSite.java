package zing.sites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import zing.Configure;
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
	
	public BufferedReader getInputStream(String link) throws IOException{
		URL url = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", Configure.getInstance().userAgent);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		return in;
	}
}
