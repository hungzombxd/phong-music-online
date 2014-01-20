package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Album;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.Song;
import huu.phong.musiconline.model.ZingSong;
import huu.phong.musiconline.utils.HtmlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Zing extends MusicSite{
	
	private static final String DOMAIN_API = "http://api.mp3.zing.vn";
	public static final String DOMAIN_MP3 = "http://mp3.zing.vn";
	public static final String DOMAIN_IMAGE = "http://image.mp3.zdn.vn";
	public static final String KEY = "keycode=dce4479e25d509f546f92857b5816060&fromvn=true";
	
	private static final String URL_SEARCH_SONG = DOMAIN_API + "/api/mobile/search/song?requestdata={\"length\":%1$d,\"start\":%2$d,\"q\":\"%3$s\",\"sort\":\"%4$s\"}&" + KEY;
	public static final String URL_SEARCH_ALBUM = DOMAIN_API + "/api/mobile/search/playlist?requestdata={\"length\":15,\"start\":0,\"q\":\"dan%20truong\",\"sort\":\"hot\"}&" + KEY;
	public static final String URL_LOG = DOMAIN_API + "/api/mobile/log/loglisten?requestdata={\"type\":\"song\",\"id\":\"1073835968\",\"device_id\":\"d33f3e748d4a5e41\"}&" + KEY;
	public static final String URL_SEARCH_ARTIST = DOMAIN_API + "/api/mobile/artist/getvideoofartist?requestdata={\"length\":15,\"id\":\"100\",\"start\":0}&" + KEY;
	
	public static final String URL_ARTIST_INFO = DOMAIN_API + "/api/mobile/artist/getartistinfo?requestdata={\"id\":\"100\"}&" + KEY;
	public static final String URL_ALBUM_OF_ARTIST = DOMAIN_API + "/api/mobile/artist/getalbumofartist?requestdata={\"length\":15,\"id\":\"100\",\"start\":0}&" + KEY;
	
	public static final String TOP_SONG_VN = DOMAIN_API + "/api/mobile/charts/getchartsinfo?requestdata={\"length\":40,\"id\":1,\"start\":0}&" + KEY;
	
	public static final String ALL_TOP_SONG = DOMAIN_API + "/api/mobile/charts/getchartslist?" + KEY;
	
//	public static final String ALL_ARTIST = DOMAIN_API + "/api/mobile/artist/getartistbygenre?requestdata={\"length\":15,\"id\":0,\"start\":0}&keycode=dce4479e25d509f546f92857b5816060&fromvn=true";
	
	public static final String URL_LYRIC = DOMAIN_API + "/api/mobile/song/getlyrics?requestdata={\"id\":\"1074584968\"}&keycode=dce4479e25d509f546f92857b5816060&fromvn=true";
	
	public static final String ALL_ARTIST = DOMAIN_API + "/api/mobile/artist/getartistbygenre?requestdata={\"length\":15,\"id\":0,\"start\":0}&keycode=dce4479e25d509f546f92857b5816060&fromvn=true";
	
	
	public static final String DEFAULT_USER_AGENT = "Dalvik/1.6.0 (Linux; U; Android 4.2.2; sdk Build/JB_MR1.1)";
	public static final String SONG_USER_AGENT = "stagefright/1.2 (Linux;Android 4.2.2)";
	
	private static Map<String, String> songByType = new HashMap<String, String>();
	private static Map<String, String> songByAlbum = new HashMap<String, String>();
	
	public static String[] titlesSongType;
	public static String[] titlesAlbumType;
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", ""), new ItemCombo("Title", "&t=title"), new ItemCombo("Artist", "&t=artist"), new ItemCombo("Composer", "&t=composer"), new ItemCombo("Lyric", "&t=lyrics")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", ""),new ItemCombo("HQ", "&filter=2"),new ItemCombo("Hit", "&filter=1"),new ItemCombo("Official", "&filter=3"), new ItemCombo("Lyric", "&filter=4")};
	
	private static Zing zing;
	
	public static Zing getInstance(){
		if (zing == null){
			zing = new Zing();
		}
		return zing;
	}

	static {
		String[] songTypes = new String[] {
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Viet-Nam/IWZ9Z08I.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Tre/IWZ9Z088.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Tru-Tinh/IWZ9Z08B.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Rap-Viet/IWZ9Z089.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Rock-Viet/IWZ9Z08A.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Cach-Mang/IWZ9Z08C.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Que-Huong/IWZ9Z08D.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Trinh/IWZ9Z08E.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Thieu-Nhi/IWZ9Z08F.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Cai-Luong/IWZ9Z0C6.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Phim/IWZ9Z087.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Au-My/IWZ9Z08O.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Hoa/IWZ9Z08U.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Han-Quoc/IWZ9Z08W.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Nhat-Ban/IWZ9Z08Z.html",
				"http://mp3.zing.vn/the-loai-bai-hat/Nhac-Hoa-Tau/IWZ9Z086.html",
				"http://mp3.zing.vn/chu-de/nhac-viet-hot/IWZ9Z0C8.html",
				"http://mp3.zing.vn/chu-de/nhac-viet-moi/IWZ9Z0ED.html",
				"http://mp3.zing.vn/chu-de/nhac-xuan/IWZ9Z0DW.html",
				"http://mp3.zing.vn/chu-de/nhac-giang-sinh/IWZ9Z0DO.html",
				"http://mp3.zing.vn/chu-de/hom-nay-nghe-gi/IWZ9Z0FE.html",
				"http://mp3.zing.vn/chu-de/rap-viet-hot/IWZ9Z0CC.html",
				"http://mp3.zing.vn/chu-de/nhac-au-my-hot/IWZ9Z0CB.html",
				"http://mp3.zing.vn/chu-de/nhac-han-hot/IWZ9Z0CA.html",
				"http://mp3.zing.vn/chu-de/nhac-nhat-hot/IWZ9Z0FI.html",
				"http://mp3.zing.vn/chu-de/nhac-san/IWZ9Z0DU.html",
				"http://mp3.zing.vn/chu-de/Love-Songs/IWZ9Z0D9.html",
				"http://mp3.zing.vn/chu-de/the-best-of/IWZ9Z0FU.html",
				"http://mp3.zing.vn/chu-de/zing-collection/IWZ9Z0EB.html" };
		String[] albums = new String[] {
				"http://mp3.zing.vn/the-loai-album/Nhac-Viet-Nam/IWZ9Z08I.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Tre/IWZ9Z088.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Tru-Tinh/IWZ9Z08B.html",
				"http://mp3.zing.vn/the-loai-album/Rap-Viet/IWZ9Z089.html",
				"http://mp3.zing.vn/the-loai-album/Rock-Viet/IWZ9Z08A.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Cach-Mang/IWZ9Z08C.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Que-Huong/IWZ9Z08D.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Trinh/IWZ9Z08E.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Thieu-Nhi/IWZ9Z08F.html",
				"http://mp3.zing.vn/the-loai-album/Cai-Luong/IWZ9Z0C6.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Phim/IWZ9Z087.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Au-My/IWZ9Z08O.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Hoa/IWZ9Z08U.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Han-Quoc/IWZ9Z08W.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Nhat-Ban/IWZ9Z08Z.html",
				"http://mp3.zing.vn/the-loai-album/Nhac-Hoa-Tau/IWZ9Z086.html" };

		titlesAlbumType = new String[] { "Nhạc Việt Nam", "Nhạc Trẻ",
				"Nhạc Trữ Tình", "Rap Việt", "Rock Việt", "Nhạc Cách Mạng",
				"Nhạc Quê Hương", "Nhạc Trịnh", "Nhạc Thiếu Nhi", "Cải Lương",
				"Nhạc Phim", "Nhạc Âu Mỹ", "Nhạc Hoa", "Nhạc Hàn Quốc",
				"Nhạc Nhật Bản", "Nhạc Hòa Tấu" };

		titlesSongType = new String[] { "Nhạc Việt Nam", "Nhạc Trẻ",
				"Nhạc Trữ Tình", "Rap Việt", "Rock Việt", "Nhạc Cách Mạng",
				"Nhạc Quê Hương", "Nhạc Trịnh", "Nhạc Thiếu Nhi", "Cải Lương",
				"Nhạc Phim", "Nhạc Âu Mỹ", "Nhạc Hoa", "Nhạc Hàn Quốc",
				"Nhạc Nhật Bản", "Nhạc Hòa Tấu", "Nhạc Hot Việt",
				"Nhạc Việt Mới", "Nhạc Xuân", "Nhạc Giáng Sinh",
				"Hôm Nay Nghe Gì?", "Hot Rap Việt", "Nhạc Hot Âu Mỹ",
				"Nhạc Hot Hàn", "Nhạc Hot Nhật", "Nhạc Sàn", "Love Songs",
				"The Best Of's", "Zing Collection" };

		for (int i = 0; i < albums.length; i++) {
			songByAlbum.put(titlesAlbumType[i], albums[i]);
		}
		for (int i = 0; i < songTypes.length; i++) {
			songByType.put(titlesSongType[i], songTypes[i]);
		}
	};

	public List<Album> getDefaultAlbum() throws IOException {
		return getAlbumBy(titlesAlbumType[0], 1);
	}

	public List<Song> getSongByType(String type, int page) throws IOException {
		return getSongsType(songByType.get(type), page);
	}

	public List<Song> getTopPhong() throws IOException {
		List<Song> songs = new ArrayList<Song>();
		String[] phong = new String[] {
				"http://mp3.zing.vn/xml/playlist/knxGtVGCJagktZFcybmZH",
				"http://mp3.zing.vn/xml/playlist/kGcHydmhuHldtZbJTFGLH",
				"http://mp3.zing.vn/xml/playlist/ZGcGTdHNAJFQTLFJtFnLG" };
		for (int i = 0; i < phong.length; i++) {
			songs.addAll(xmlToSongs(phong[i]));
		}
		return songs;
	}

	public List<Song> getTopKorea() throws UnsupportedEncodingException,
			IOException {
		return getTopSongs("http://mp3.zing.vn/bang-xep-hang/bai-hat/Han-Quoc/IWZ9Z0BO.html");
	}

	public List<Song> getTopVietnamese() throws UnsupportedEncodingException,
			IOException {
		return getTopSongs("http://mp3.zing.vn/bang-xep-hang/bai-hat/Viet-Nam/IWZ9Z08I.html");
	}

	public List<Song> getTopEnglish() throws UnsupportedEncodingException,
			IOException {
		return getTopSongs("http://mp3.zing.vn/bang-xep-hang/bai-hat/Au-My/IWZ9Z0BW.html");
	}

	private List<Song> getTopSongs(String urlTop)
			throws UnsupportedEncodingException, IOException {
		List<Song> lists = new ArrayList<Song>();
		BufferedReader in = getReader(urlTop);
		String str, title, link;
		while ((str = in.readLine()) != null) {
			if (str.contains("href=\"/bai-hat/") && str.contains("<h3>")) {
				title = HtmlUtil.getAttribute(str, "title=\"");
				link = HtmlUtil.getAttribute(str, "href=\"");
				lists.add(new Song(title, link));
			}
		}
		in.close();
		return lists;
	}

	// Get link to mp3 of HTML mp3 link
	public Map<Format, String> getLink(String mp3URL) throws IOException {
		return xmlToSongs(getXML(mp3URL)).get(0).getDirectLinks();
	}

	// Get XML file for song or album
	private String getXML(String link) throws IOException {
		String ret = "";
		BufferedReader in = getReader(link);
		String str;
		int index = 0;
		while ((str = in.readLine()) != null) {
			if (str.contains("<p class=\"song-info\">")){
				information = "<html>" + str.trim() + "</html>";
			}
			if (str.contains("<param name=\"flashvars\" value=\"")) {
				index = str.indexOf("http://mp3.zing.vn/xml/");
				ret = str.substring(index);
				ret = new StringTokenizer(ret, "&").nextToken();
				break;
			}
		}
		in.close();
		return ret;
	}

	// Get songs of album
	public List<Song> getAlbum(String html) throws IOException {
		return xmlToSongs(getXML(html));
	}
	
	public List<Song> searchSong(String value, int page, String filter) throws UnsupportedEncodingException, IOException{
		value = URLEncoder.encode(value, "UTF-8");
		InputStream in = getInputStream(String.format(URL_SEARCH_SONG, numberResult, (page - 1) * numberResult, value, "hot"), null, DEFAULT_USER_AGENT);
		String response = HtmlUtil.streamToString(in);
		ZingSong result = gson.fromJson(response, ZingSong.class);
		return result.getSongs();
	}
	
	public List<Album> searchAlbum(String value, int page, String filter) throws IOException{
		value = URLEncoder.encode(value, "UTF-8");
		List<Album> lists = new ArrayList<Album>();
		BufferedReader in = getReader("http://mp3.zing.vn/tim-kiem/playlist.html?q=" + value + "&p=" + page);
		String str, title, link, albumArt = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("class=\"album-img\"")){
				albumArt = in.readLine();
				albumArt = albumArt.substring(albumArt.indexOf("src=\"") + 5);
				albumArt = albumArt.substring(0, albumArt.indexOf("\""));
				if (albumArt.equals("http://static.mp3.zing.vn/skins/mp3_v3_16/images/avatar_default_82x82.jpg")) albumArt = "";
			}
			if (str.contains("<a title=\"")) {
				str = str.trim();
				if(str.startsWith("<a title") && str.contains("href")){
					StringTokenizer token = new StringTokenizer(str, "\"");
					token.nextElement();
					title = token.nextToken();
					token.nextElement();
					link = "http://mp3.zing.vn" + token.nextToken();
					Album album = new Album(title, link);
					while ((str = in.readLine()) != null){
						if (str.trim().equalsIgnoreCase("</h3>")) break;
						if (str.contains("title=\"Album chất lượng cao\"")) album.setHighQuality(true);
					}
					album.setInfo(HtmlUtil.htmlToText(in.readLine()) + "<br/>" + HtmlUtil.htmlToText(in.readLine()));
					album.setAlbumArt(albumArt);
					lists.add(album);
					if(lists.size() >= 20) break;
				}
			}
		}
		in.close();
		return lists;
	}

	public List<Song> xmlToSongs(String linkXML) throws IOException {
		List<Song> songs = new ArrayList<Song>();
		BufferedReader in = getReader(linkXML);
		String str;
		String title = "";
		String artist = "";
		String link = "";
		Song song;
		while ((str = in.readLine()) != null) {
			if (str.contains("<item")) {
				title = HtmlUtil.getContent(in.readLine());
				artist = HtmlUtil.getContent(in.readLine());
				in.readLine();
				link = in.readLine();
				song = new Song();
				song.setDirectLink(Format.MP3_128_KBPS, HtmlUtil.getContent(link));
				song.setTitle(String.format("%1$s - %2$s", title, artist));
				songs.add(song);
			}
		}
		in.close();
		return songs;
	}

	private List<Song> getSongsType(String linkType, int page) throws UnsupportedEncodingException, IOException {
		List<Song> lists = new ArrayList<Song>();
		BufferedReader in = getReader(linkType + "?p=" + page);
		String str, title, link;
		while ((str = in.readLine()) != null) {
			if (str.contains("<h2><a title=\"")
					|| str.contains("<h3><a title=\"")) {
				StringTokenizer token = new StringTokenizer(str, "\"");
				token.nextElement();
				title = token.nextToken();
				token.nextElement();
				link = token.nextToken();
				Song song = new Song(title, link);
				song.setSongInfo(HtmlUtil.htmlToText(in.readLine()));
				lists.add(song);
			}
		}
		in.close();
		return lists;
	}

	public List<Album> getAlbumBy(String type, int page) throws IOException {
		return getPlayListBy(songByAlbum.get(type), page);
	}

	private List<Album> getPlayListBy(String typePlaylistLink, int page)
			throws IOException {
		List<Album> albums = new ArrayList<Album>();
		BufferedReader in = getReader(typePlaylistLink + "?p=" + page);
		String str;
		int from = -1;
		int to = -1;
		String title = "";
		String link = "";
		String img = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("<span class=\"album-detail-img\">")){
				img = HtmlUtil.getAttribute(str, "src=\"");
			}
			if (str.contains("<a class=\"_trackLink\"") && str.trim().startsWith("<a class=\"_trackLink\"")) {
				from = str.indexOf("title=\"");
				to = str.indexOf("\" href=\"");
				title = str.substring(from + 7, to);
				link = str.substring(to + 8).split("\"")[0];
				in.readLine();
				in.readLine();
				Album album = new Album(title, link);
				album.setInfo("Năm phát hành : " + HtmlUtil.htmlToText(in.readLine().trim()) + " | " + "Lượt nghe trong tuần: " + HtmlUtil.htmlToText(in.readLine().trim()));
				album.setAlbumArt(img);
				album.setSite(Site.MP3_ZING_VN);
				albums.add(album);
			}
		}
		in.close();
		return albums;
	}
	
	public List<String> getLyric(Song song) throws IOException{
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
}
