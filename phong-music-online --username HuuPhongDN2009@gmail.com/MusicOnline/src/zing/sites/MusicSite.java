package zing.sites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import zing.Configure;
import zing.model.Album;
import zing.model.Format;
import zing.model.ItemCombo;
import zing.model.Song;

public abstract class MusicSite{
	protected String error = null;
	protected String information = null;
	
	public static MusicSite getInstanceBy(Site site){
		switch (site) {
		case MP3_ZING_VN:
			return Zing.getInstance();
			
		case CHIA_SE_NHAC:
			return ChiaSeNhac.getInstance();
			
		case NHAC_CUA_TUI:
			return NhacCuaTui.getInstance();

		default:
			throw new RuntimeException(String.format("Unknow site %1$s", site));
		}
	}
	
	public abstract List<Song> searchSong(String value, int page, String filter) throws IOException;
	
	public abstract Map<Format, String> getLink(String html) throws IOException;
	
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
	
	public BufferedReader getReader(String link) throws IOException{
		return getReader(link, null);
	}
	
	public BufferedReader getReader(String link, Map<String, String> properties) throws IOException{
		URL url = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", Configure.getInstance().userAgent);
		if (properties != null){
			Set<Entry<String, String>> pros = properties.entrySet();
			for (Entry<String, String> pro : pros){
				connection.addRequestProperty(pro.getKey(), pro.getValue());
			}
		}
		InputStream in = connection.getInputStream();
		if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) in = new GZIPInputStream(in);
		return new BufferedReader(new InputStreamReader(in, "UTF-8"));
	}
}
