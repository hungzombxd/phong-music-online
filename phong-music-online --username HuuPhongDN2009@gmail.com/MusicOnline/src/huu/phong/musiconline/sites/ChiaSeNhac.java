package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.ChiaSeNhacSong;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.utils.HtmlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiaSeNhac extends MusicSite{
	
	private static ChiaSeNhac chiaSeNhac = new ChiaSeNhac();
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", ""), new ItemCombo("Artist", "&mode=artist"), new ItemCombo("Composer", "&mode=composer"), new ItemCombo("Album", "&mode=album"), new ItemCombo("Lyric", "&mode=lyric")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", "&cat=music")};
	
	public static ChiaSeNhac getInstance(){
		return chiaSeNhac;
	}
	
	private ChiaSeNhac() {
		
	}

	@Override
	public List<ISong> searchSong(String value, int page, String filter) throws IOException {
		value = URLEncoder.encode(value, "UTF-8");
		List<ISong> songs = new ArrayList<ISong>();
		BufferedReader in = getReader("http://search.chiasenhac.com/search.php?mode=&order=quality&cat=music&s=" + value + "&page=" + page + filter);
		String str;
		String title = "";
		String artist = "";
		String link = "";
		String info = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("class=\"musictitle\"")){
				title = HtmlUtil.htmlToText(str).trim();
				link = "http://chiasenhac.com/" + HtmlUtil.getAttribute(str, "href=\"");
				artist = HtmlUtil.htmlToText(in.readLine()).trim();
				while (!(str = in.readLine()).contains("<span class=\"gen\">")){
				}
				info = HtmlUtil.htmlToText(str.replace("<br />", " | ")).trim();
				ChiaSeNhacSong song = new ChiaSeNhacSong();
				song.setTitle(title);
				song.setArtist(artist);
				song.setLink(link);
				song.setQuality(info.contains("Lossless") ? Format.LOSSLESS : info.contains("320kbps") ? Format.MP3_320_KBPS : Format.MP3_128_KBPS);
				song.description = info;
				songs.add(song);
				if (songs.size() == 25) break;
			}
		}
		in.close();
		return songs;
	}

	@Override
	public Map<Format, String> getLink(String html) throws IOException {
		Map<Format, String> links = new HashMap<Format, String>();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("Cookie", "csn_data=a%3A2%3A%7Bs%3A11%3A%22autologinid%22%3Bs%3A0%3A%22%22%3Bs%3A6%3A%22userid%22%3Bi%3A103497%3B%7D; csn_sid=51b467f7ea25cd8887b53fbb69458f9b");
		BufferedReader in = getReader(html.replace("http://", "http://download.").replace(".html", "_download.html"), properties);
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("<div id=\"downloadlink\"")){
				while (!(str = in.readLine()).contains("</div>")){
					if (str.contains("chiasenhac.com/downloads/")){
						str = HtmlUtil.getAttribute(str, "href=\"").replace(" ", "+");
						if (str.toLowerCase().endsWith(".mp3") || str.toLowerCase().endsWith(".flac")){
							links.put(getFormat(str), str);
						}
					}
				}
			}
		}
		in.close();
		return links;
	}
	
	private Format getFormat(String link){
		if (link.contains("128kbps")){
			return Format.MP3_128_KBPS;
		}else if (link.contains("320kbps")){
			return Format.MP3_320_KBPS;
		}else if (link.contains("FLAC")){
			return Format.LOSSLESS;
		}
		return null;
	}

	@Override
	public List<IAlbum> searchAlbum(String value, int page, String filter)
			throws IOException {
		List<IAlbum> albums = new ArrayList<IAlbum>();
		return albums;
	}

	@Override
	public List<ISong> getAlbum(String html) throws IOException {
		List<ISong> songs = new ArrayList<ISong>();
		return songs;
	}

	@Override
	public ItemCombo[] getBys() {
		return BYS;
	}

	@Override
	public ItemCombo[] getFilters() {
		return FILTERS;
	}
	
	public static void main(String[] args) {
		getInstance();
	}
}
