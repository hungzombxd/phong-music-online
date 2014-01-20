package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Album;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.Song;
import huu.phong.musiconline.utils.HtmlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NhacCuaTui extends MusicSite {
	private static NhacCuaTui nhacCuaTui;
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", "")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", "")};
	
	public static NhacCuaTui getInstance(){
		if (nhacCuaTui == null) nhacCuaTui = new NhacCuaTui();
		return nhacCuaTui;
	}
	
	public List<Song> xmlToSongs(String xml) throws UnsupportedEncodingException, IOException{
		List<Song> songs = new ArrayList<Song>();
		URL url = new URL(xml);
		String str;
		int from = -1;
		String title = "";
		String link = "";
		String artist = "";
		str = HtmlUtil.streamToString(url.openStream());
		while ((from = str.indexOf("<track>")) != -1){
			str = str.substring(from + 7);
			title = HtmlUtil.getTag(str, "title");
			artist = HtmlUtil.getTag(str, "creator");
			link = HtmlUtil.getTag(str, "location");
			Song song = new Song();
			song.setTitle(title + " - " + artist);
			song.setDirectLink(Format.MP3_128_KBPS, link);
			song.setSite(Site.NHAC_CUA_TUI);
			songs.add(song);
		}
		return songs;
	}
	
	public String htmlToXML(String html) throws IOException{
		BufferedReader in = getReader(html);
		String str;
		while ((str = in.readLine()) != null) {
			if (str.indexOf("NCTNowPlaying.intFlashPlayer") != -1){
				if (str.contains("playlist")){
					str = "http://www.nhaccuatui.com/flash/xml?key2=" + HtmlUtil.getAttribute(str, "\"playlist\", \"");
				}else{
					str = "http://www.nhaccuatui.com/flash/xml?key1=" + HtmlUtil.getAttribute(str, "\"song\", \"");
				}
				break;
			}
		}
		in.close();
		return str;
	}
	
	public Map<Format, String> getLink(String html) throws IOException{
		BufferedReader in = getReader("http://www.nhaccuatui.com/download/song/" + html.substring(html.length() - 15).substring(0, 10));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("Success")){
				str = str.substring(str.indexOf("http:\\/\\/"));
				str = str.substring(0, str.indexOf("\"")).replace("\\", "").trim();
				break;
			}
		}
		in.close();
		Map<Format, String> links = new HashMap<Format, String>();
		if (str == null){
			links.putAll(xmlToSongs(htmlToXML(html)).get(0).getDirectLinks());
		}else{
			links.put(Format.MP3_128_KBPS, str.trim());
		}
		return links;
	}
	
	public List<Song> searchSong(String value, int page, String filter) throws IOException{
		value = URLEncoder.encode(value, "UTF-8");
		List<Song> songs = new ArrayList<Song>();
		BufferedReader in = getReader("http://www.nhaccuatui.com/tim-kiem/bai-hat?q=" + value + "&page=" + page);
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul class=\"list-song\">")){
				while ((str = in.readLine()) != null && !str.contains("</ul>")){
					if (str.contains("<li class=\"clearfix song-item\"")){
						for (int i = 0; i < 3; i++){
							in.readLine();
						}
						str = in.readLine();
						Song song = new Song();
						song.setQuality(str.contains("320kb") || str.contains("Official") ? Format.MP3_320_KBPS : Format.MP3_128_KBPS);
						str = in.readLine();
						song.setLink(HtmlUtil.getAttribute(str, "href=\""));
						song.setTitle(HtmlUtil.getAttribute(str, "title=\""));
						while ((str = in.readLine()) != null){
							if (!str.contains("class=\"singer\"")) continue;
							str = in.readLine();
							song.setTitle(song.getTitle() + " - " + HtmlUtil.htmlToText(str).trim());
							str = in.readLine();
							song.setSongInfo("Lượt nghe: " + HtmlUtil.htmlToText(in.readLine()).trim() + " | Upload bởi: " + HtmlUtil.htmlToText(in.readLine()).trim());
							break;
						}
						song.setSite(Site.NHAC_CUA_TUI);
						songs.add(song);
					}
				}
				break;
			}
		}
		in.close();
		return songs;
	}

	@Override
	public List<Album> searchAlbum(String value, int page, String filter) throws IOException {
		List<Album> albums = new ArrayList<Album>();
		value = URLEncoder.encode(value, "UTF-8");
		BufferedReader in = getReader("http://www.nhaccuatui.com/tim-kiem/playlist?q=" + value + "&page=" + page);
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul class=\"list-al-pl\"")){
				while ((str = in.readLine()) != null && !str.contains("</ul>")){
					if (str.contains("<p class=\"name\">")){
						Album album = new Album();
						album.setInfo("Tạo bởi: " + HtmlUtil.htmlToText(str).trim() + " | Lượt nghe: " + HtmlUtil.htmlToText(in.readLine()).trim());
						in.readLine(); in.readLine();
						str = in.readLine();
						album.setLink(HtmlUtil.getAttribute(str, "href=\""));
						album.setTitle(HtmlUtil.getAttribute(str, "title=\""));
						album.setAlbumArt(HtmlUtil.getAttribute(in.readLine(), "src=\""));
						str = in.readLine();
						while ((str = in.readLine()) != null){
							if (str.contains("<a href=\"http://www.nhaccuatui.com/tim-kiem")){
								album.setTitle(album.getTitle() + " - " + HtmlUtil.htmlToText(str));
								album.setInfo("Trình bày: " + HtmlUtil.htmlToText(str) + "<br/>" + album.getInfo());
								break;
							}
						}
						album.setSite(Site.NHAC_CUA_TUI);
						albums.add(album);
						album = null;
					}
				}
				break;
			}
		}
		in.close();
		
		return albums;
	}

	@Override
	public List<Song> getAlbum(String html) throws IOException {
		return xmlToSongs(htmlToXML(html));
	}

	public List<String> getLyric(String html) throws IOException {
		List<String> lyrics = new ArrayList<String>();
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
