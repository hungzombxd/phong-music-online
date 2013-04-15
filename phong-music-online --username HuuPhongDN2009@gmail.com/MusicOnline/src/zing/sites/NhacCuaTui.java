package zing.sites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
			title = HtmlUtil.getTab(str, "title");
			artist = HtmlUtil.getTab(str, "creator");
			link = HtmlUtil.getTab(str, "location");
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
		URL url = new URL(html);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.indexOf("NCTNowPlaying.intFlashPlayer") != -1){
				str = HtmlUtil.getAttribute(str, "\"song\", \"");
				break;
			}
		}
		connection.disconnect();
		in.close();
		return "http://www.nhaccuatui.com/flash/xml?key1=" + str;
	}
	
	public String getLink(String html) throws IOException{
		URL url = new URL("http://www.nhaccuatui.com/download/song/" + html.substring(html.length() - 15).substring(0, 10));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
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
		URL url = new URL("http://www.nhaccuatui.com/tim-kiem/bai-hat?q=" + value + "&page=" + page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
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
		URL url = new URL("http://www.nhaccuatui.com/tim-kiem/playlist?q=" + value + "&page=" + page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		String title = "";
		String link = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul class=\"list_playlist\">")){
				while ((str = in.readLine()) != null && !str.contains("</ul>")){
					if (str.contains("<li >")){
						str = in.readLine();
						link = "http://www.nhaccuatui.com" + HtmlUtil.getAttribute(str, "href=\"");
						title = HtmlUtil.getAttribute(str, "title=\"");
						Album album = new Album(title, link);
						album.albumArt = HtmlUtil.getAttribute(str, "src=\"");
						in.readLine();
						str = "";
						for (int i = 0; i < 5; i++){
							str += in.readLine().trim();
							if (i == 2 && str.contains("<br />")){
								break;
							}
						}
						album.info = HtmlUtil.htmlToText(str) + "<br/>" + in.readLine().trim();
						albums.add(album);
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
		System.out.println(NhacCuaTui.getInstance().searchSong("pham truong", 1, ""));
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
