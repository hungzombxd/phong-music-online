package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.FormatAdaptor;
import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.ItemCombo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class MusicSite {
	
	public static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1";
	
	protected String error = null;
	
	protected String information = null;
	
	public int numberResult = 15;
	
	protected static Gson gson = new Gson();
	
	protected Map<String, String> properties = new HashMap<String, String>();
	
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Map.class, new FormatAdaptor());
		builder.registerTypeAdapter(boolean.class, new JsonDeserializer<Boolean>(){
			@Override
			public Boolean deserialize(JsonElement e, Type type,
					JsonDeserializationContext ctx) throws JsonParseException {
				String value = e.getAsString();
				return "true".equals(value) || "1".equals(value) ? true : false;
			}
			
		});
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>(){
			@Override
			public Date deserialize(JsonElement e, Type type,
					JsonDeserializationContext ctx) throws JsonParseException {
				return new Date(e.getAsLong() * 1000);
			}
			
		});
		gson = builder.create();
	}
	
	protected MusicSite() {
		properties.put("User-Agent", DEFAULT_AGENT);
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
	
	public abstract List<? extends ISong> searchSong(String value, int page, String filter) throws IOException;
	
	public abstract Map<Format, String> getLink(String html) throws IOException;
	
	public abstract List<? extends IAlbum> searchAlbum(String value, int page, String filter) throws IOException;
	
	public abstract List<? extends ISong> getAlbum(String html) throws IOException;
	
	public List<String> getLyric(ISong song) throws IOException{
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
	
	public InputStream getInputStream(String link, Map<String, String> properties) throws IOException{
		URL url = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
	
	public InputStream getInputStream(String link) throws IOException {
		return getInputStream(link, getRequestProperties());
	}
	
	public BufferedReader getReader(String link, Map<String, String> properties) throws IOException{
		return new BufferedReader(new InputStreamReader(getInputStream(link, properties), "UTF-8"));
	}
	
	public BufferedReader getReader(String link) throws IOException{
		return getReader(link, getRequestProperties());
	}
	
	private Map<String, String> getRequestProperties() {
		return properties;
	}
}
