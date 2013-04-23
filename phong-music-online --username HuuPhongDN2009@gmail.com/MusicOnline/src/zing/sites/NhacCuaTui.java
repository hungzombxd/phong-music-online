package zing.sites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import zing.model.Album;
import zing.model.ItemCombo;
import zing.model.Song;
import zing.utils.HtmlUtil;

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
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		String str;
		int from = -1;
		String title = "";
		String link = "";
		String artist = "";
		str = HtmlUtil.readJoinLines(in);
		while ((from = str.indexOf("<track>")) != -1){
			str = str.substring(from + 7);
			title = HtmlUtil.getTag(str, "title");
			artist = HtmlUtil.getTag(str, "creator");
			link = HtmlUtil.getTag(str, "location");
			Song song = new Song();
			song.setTitle(title + " - " + artist);
			song.setDirectLink(link);
			song.setHost("nhaccuatui.com");
			songs.add(song);
		}
		in.close();
		return songs;
	}
	
	public String htmlToXML(String html) throws IOException{
		BufferedReader in = getInputStream(html);
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
	
	public String getLink(String html) throws IOException{
		BufferedReader in = getInputStream("http://www.nhaccuatui.com/download/song/" + html.substring(html.length() - 15).substring(0, 10));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("Success")){
				str = str.substring(str.indexOf("http:\\/\\/"));
				str = str.substring(0, str.indexOf("\"")).replace("\\", "").trim();
				break;
			}
		}
		in.close();
		return str == null ? xmlToSongs(htmlToXML(html)).get(0).getDirectLink() : str.trim();
	}
	
	public List<Song> searchSong(String value, int page, String filter) throws IOException{
		value = URLEncoder.encode(value, "UTF-8");
		List<Song> songs = new ArrayList<Song>();
		BufferedReader in = getInputStream("http://www.nhaccuatui.com/tim-kiem/bai-hat?q=" + value + "&page=" + page);
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
						song.quality = str.contains("320kb") || str.contains("Official") ? Song.MP3_320_KBPS : Song.MP3_128_KBPS;
						str = in.readLine();
						song.link = HtmlUtil.getAttribute(str, "href=\"");
						if (!song.link.startsWith("http")) song.link = "http://www.nhaccuatui.com" + song.link;
						song.title = HtmlUtil.getAttribute(str, "title=\"");
						while ((str = in.readLine()) != null){
							if (!str.contains("class=\"singer\"")) continue;
							str = in.readLine();
							song.title = song.title + " - " + HtmlUtil.htmlToText(str).trim();
							str = in.readLine();
							song.songInfo = "Lượt nghe: " + HtmlUtil.htmlToText(in.readLine()).trim() + " | Upload bởi: " + HtmlUtil.htmlToText(in.readLine()).trim();
							break;
						}
						song.host = "nhaccuatui.com";
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
		BufferedReader in = getInputStream("http://www.nhaccuatui.com/tim-kiem/playlist?q=" + value + "&page=" + page);
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul class=\"list-al-pl\"")){
				while ((str = in.readLine()) != null && !str.contains("</ul>")){
					if (str.contains("<p class=\"name\">")){
						Album album = new Album();
						album.info = "Tạo bởi: " + HtmlUtil.htmlToText(str).trim() + " | Lượt nghe: " + HtmlUtil.htmlToText(in.readLine()).trim();
						in.readLine(); in.readLine();
						str = in.readLine();
						album.link = HtmlUtil.getAttribute(str, "href=\"");
						album.title = HtmlUtil.getAttribute(str, "title=\"");
						album.albumArt = HtmlUtil.getAttribute(in.readLine(), "src=\"");
						str = in.readLine();
						while ((str = in.readLine()) != null){
							if (str.contains("<a href=\"http://www.nhaccuatui.com/tim-kiem")){
								album.title += " - " + HtmlUtil.htmlToText(str);
								album.info = "Trình bày: " + HtmlUtil.htmlToText(str) + "<br/>" + album.info;
								break;
							}
						}
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
	
	public static void main(String[] args) throws IOException {
		System.out.println(NhacCuaTui.getInstance().searchAlbum("lam truong", 1, ""));
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
