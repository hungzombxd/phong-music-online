package huu.phong.musiconline.sites;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.model.Album;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.FormatAdaptor;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.Song;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public abstract class MusicSite {
	
	protected String error = null;
	
	protected String information = null;
	
	public int numberResult = 15;
	
	protected static Gson gson = new Gson();
	
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Map.class, new FormatAdaptor());
		gson = builder.create();
	}
	
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
	
	public InputStream getInputStream(String link, Map<String, String> properties) throws IOException{
		return getInputStream(link, properties, null);
	}
	
	public InputStream getInputStream(String link, Map<String, String> properties, String userAgent) throws IOException{
		URL url = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if(userAgent == null) userAgent = Configure.getInstance().userAgent;
		connection.addRequestProperty("User-Agent", Configure.getInstance().userAgent);
		if (properties != null){
			Set<Entry<String, String>> pros = properties.entrySet();
			for (Entry<String, String> pro : pros){
				connection.addRequestProperty(pro.getKey(), pro.getValue());
			}
		}
		InputStream in = connection.getInputStream();
		if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) in = new GZIPInputStream(in);
		return in;
	}
	
	public BufferedReader getReader(String link, Map<String, String> properties) throws IOException{
		return new BufferedReader(new InputStreamReader(getInputStream(link, properties), "UTF-8"));
	}
}
