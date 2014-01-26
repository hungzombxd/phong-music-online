package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.zing.ZingAlbum;
import huu.phong.musiconline.model.zing.ZingAlbumList;
import huu.phong.musiconline.model.zing.ZingSongList;
import huu.phong.musiconline.utils.HtmlUtil;
import huu.phong.musiconline.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Zing extends MusicSite{
	
	private static final String DOMAIN_API = "http://api.mp3.zing.vn";
	public static final String DOMAIN_MP3 = "http://mp3.zing.vn";
	public static final String DOMAIN_IMAGE = "http://image.mp3.zdn.vn";
	public static final String KEY = "keycode=dce4479e25d509f546f92857b5816060&fromvn=true";
	
	public static final String URL_SEARCH_SONG = DOMAIN_API + "/api/mobile/search/song?requestdata={\"length\":%1$d,\"start\":%2$d,\"q\":\"%3$s\",\"sort\":\"%4$s\"}&" + KEY;
	public static final String URL_SONG_INFO = DOMAIN_API + "/api/mobile/song/getsonginfo?requestdata={\"id\":\"1073752416\"}&" + KEY;
	public static final String URL_SONG_OF_ALBUM = DOMAIN_API + "/api/mobile/playlist/getsonglist?requestdata={\"length\":%1$d,\"id\":\"%2$s\",\"start\":%3$d}&" + KEY;
	public static final String URL_SONG_OF_ARTIST = DOMAIN_API + "/api/mobile/artist/getvideoofartist?requestdata={\"length\":%1$d,\"id\":\"%2$s\",\"start\":%3$d}&" + KEY;
	
	public static final String URL_SEARCH_ALBUM = DOMAIN_API + "/api/mobile/search/playlist?requestdata={\"length\":%1$d,\"start\":%2$d,\"q\":\"%3$s\",\"sort\":\"%4$s\"}&" + KEY;
	public static final String URL_ALBUM_INFO = DOMAIN_API + "/api/mobile/playlist/getalbuminfo?requestdata={\"id\":\"%1$s\"}&" + KEY;
	public static final String URL_ALBUM_OF_ARTIST = DOMAIN_API + "/api/mobile/artist/getalbumofartist?requestdata={\"length\":%1$d,\"id\":\"%2$s\",\"start\":%3$d}&" + KEY;
	
	public static final String URL_ARTIST_BY_GENRE = DOMAIN_API + "/api/mobile/artist/getartistbygenre?requestdata={\"length\":%1$d,\"id\":%2$d,\"start\":%3$d}&" + KEY;
	public static final String URL_ARTIST_INFO = DOMAIN_API + "/api/mobile/artist/getartistinfo?requestdata={\"id\":\"%1$s\"}&" + KEY;
	
	public static final String TOP_SONG_VN = DOMAIN_API + "/api/mobile/charts/getchartsinfo?requestdata={\"length\":%1$d,\"id\":1,\"start\":%2$d}&" + KEY;
	public static final String URL_LOG = DOMAIN_API + "/api/mobile/log/loglisten?requestdata={\"type\":\"song\",\"id\":\"%1$s\",\"device_id\":\"d33f3e748d4a5e41\"}&" + KEY;
	public static final String ALL_TOP_SONG = DOMAIN_API + "/api/mobile/charts/getchartslist?" + KEY;
	public static final String URL_LYRIC = DOMAIN_API + "/api/mobile/song/getlyrics?requestdata={\"id\":\"%1$s\"}&" + KEY;
	
	public static final String ZING_USER_AGENT = "Dalvik/1.6.0 (Linux; U; Android 4.2.2; sdk Build/JB_MR1.1)";
	public static final String ZING_SONG_USER_AGENT = "stagefright/1.2 (Linux;Android 4.2.2)";
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", ""), new ItemCombo("Title", "&t=title"), new ItemCombo("Artist", "&t=artist"), new ItemCombo("Composer", "&t=composer"), new ItemCombo("Lyric", "&t=lyrics")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", ""),new ItemCombo("HQ", "&filter=2"),new ItemCombo("Hit", "&filter=1"),new ItemCombo("Official", "&filter=3"), new ItemCombo("Lyric", "&filter=4")};
	
	private static Zing zing = new Zing();
	
	public static Zing getInstance(){
		return zing;
	}
	
	private Zing() {
		properties.put("User-Agent", ZING_USER_AGENT);
	}

	public List<? extends ISong> getAlbum(String id) throws IOException {
		InputStream in = getInputStream(String.format(URL_SONG_OF_ALBUM, -1, id, 0));
		String response = Utils.streamToString(in);
		System.out.println(response);
		ZingSongList result = gson.fromJson(response, ZingSongList.class);
		return result.getSongs();
	}
	
	public ZingAlbum getAlbumInfo(String id) throws IOException{
		InputStream in = getInputStream(String.format(URL_ALBUM_INFO, id));
		String response = Utils.streamToString(in);
		System.out.println(response);
		ZingAlbum album = gson.fromJson(response, ZingAlbum.class);
		return album;
	}
	
	public List<? extends ISong> searchSong(String value, int page, String filter) throws UnsupportedEncodingException, IOException{
		value = URLEncoder.encode(value, "UTF-8");
		InputStream in = getInputStream(String.format(URL_SEARCH_SONG, numberResult, (page - 1) * numberResult, value, "hot"));
		String response = Utils.streamToString(in);
		ZingSongList result = gson.fromJson(response, ZingSongList.class);
		return result.getSongs();
	}
	
	public List<? extends IAlbum> searchAlbum(String value, int page, String filter) throws IOException{
		value = URLEncoder.encode(value, "UTF-8");
		InputStream in = getInputStream(String.format(URL_SEARCH_ALBUM, numberResult, (page - 1) * numberResult, value, "hot"));
		String response = Utils.streamToString(in);
		System.out.println(response);
		ZingAlbumList result = gson.fromJson(response, ZingAlbumList.class);
		return result.getAlbums();
	}

	public List<String> getLyric(ISong song) throws IOException {
		List<String> lyrics = new ArrayList<String>();
		BufferedReader in = getReader(song.getLink());
		String str, lyric;
		while ((str = in.readLine()) != null) {
			if (str.contains("<p class=\"_lyricContent")) {
				while ((str = in.readLine()) != null){
					lyric = HtmlUtil.htmlToText(str);
					if (!lyric.trim().equals("")) lyrics.add(lyric);
					if (str.contains("</p>")){
						in.close();
						return lyrics;
					}
				}
			}
		}
		in.close();
		return lyrics;
	}

	@Override
	public ItemCombo[] getBys() {
		return BYS;
	}

	@Override
	public ItemCombo[] getFilters() {
		return FILTERS;
	}

	@Override
	public Map<Format, String> getLink(String html) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
