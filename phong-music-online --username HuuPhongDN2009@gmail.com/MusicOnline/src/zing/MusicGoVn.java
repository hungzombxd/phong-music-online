package zing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MusicGoVn extends MusicSite{
	private static MusicGoVn musicGoVn;
	
	public static MusicGoVn getInstance(){
		if (musicGoVn == null) musicGoVn = new MusicGoVn();
		return musicGoVn;
	}
	
	public List<Song> searchSong(String value, int page, String filter) throws IOException {
		value = URLEncoder.encode(value, "UTF-8");
		List<Song> songs = new ArrayList<Song>();
		URL url = new URL("http://music.go.vn/tim-kiem/ca-khuc.html?_keyword=" + value + "&_page=" + page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		int from = -1;
		String title = "";
		String artist = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("<div class=\"list_ck_feature\">")) {
				while ((from = str.indexOf("<div class='name_song'>")) != -1){
					Song song = new Song();
					song.host = "music.go.vn";
					str = str.substring(from + "<div class='name_song'>".length());
					song.link = "http://music.go.vn" + str.substring(9, from = str.indexOf("'", 11));
					title = str.substring(from + 9, str.indexOf("'", from + 9));
					str = str.substring(str.indexOf("itemprop=\"byArtist\">") + "itemprop=\"byArtist\">".length());
					artist = str.substring(0, str.indexOf("<"));
					song.title = title + " - " + artist;
					str = str.substring(str.indexOf("<div class='song_info'>"));
					song.lineTwo = htmlToText(str.substring(0, str.indexOf("</div>"))).replace("&nbsp;", "").trim();
					song.highQuality = song.lineTwo.contains("320kb/s");
					songs.add(song);
				}
				break;
			}
		}
		in.close();
		return songs;
	}
	
	public String getLink(String html){
		html = html.substring(html.lastIndexOf("/") + 1);
		return "http://dl.music.go.vn/download.aspx?sid=" + html.substring(0, html.indexOf("."));
	}

	@Override
	public List<Album> searchAlbum(String value, int page, String filter) throws IOException {
		List<Album> albums = new ArrayList<Album>();
		value = URLEncoder.encode(value, "UTF-8");
		URL url = new URL("http://music.go.vn/tim-kiem/album.html?_keyword=" + value + "&_page=" + page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		int from = -1;
		while ((str = in.readLine()) != null) {
			if (str.contains("<div class=\"list_album")){
				while ((from = str.indexOf("itemprop=\"album\"")) != -1){
					str = str.substring(from) + 13;
					Album album = new Album();
					album.link = "http://music.go.vn" + getAttribute(str, "href=\"");
					album.albumArt = getAttribute(str, "src=\"");
					str = str.substring(str.indexOf("<div class=\"name_album\">"));
					album.title = getAttribute(str, "title='", "'");
					album.info = getAttribute(str, "itemprop=\"byArtist\">", "<");
					albums.add(album);
				}
				break;
			}
		}
		in.close();
		return albums;
	}

	@Override
	public List<Song> getAlbum(String html) throws IOException {
		List<Song> songs = new ArrayList<Song>();
		URL url = new URL(html);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		int from = -1;
		String title = "";
		String artist = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul id='ulSongRecent'>")) {
				while ((from = str.indexOf("<div class='name_song'>")) != -1){
					Song song = new Song();
					song.host = "music.go.vn";
					str = str.substring(from + "<div class='name_song'>".length());
					song.link = "http://music.go.vn" + str.substring(9, from = str.indexOf("'", 11));
					title = str.substring(from + 9, str.indexOf("'", from + 9));
					str = str.substring(str.indexOf("itemprop=\"byArtist\">") + "itemprop=\"byArtist\">".length());
					artist = str.substring(0, str.indexOf("<"));
					song.title = title + " - " + artist;
					str = str.substring(str.indexOf("<div class='song_info'>"));
					song.lineTwo = htmlToText(str.substring(0, str.indexOf("</div>"))).replace("&nbsp;", "").trim();
					song.highQuality = song.lineTwo.contains("320kb/s");
					songs.add(song);
				}
				break;
			}
		}
		in.close();
		return songs;
	}

	public List<String> getLyric(String html) throws IOException {
		List<String> lyrics = new ArrayList<String>();
		return lyrics;
	}
}
