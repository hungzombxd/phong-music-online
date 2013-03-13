package zing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NhacCuaTui extends MusicSite {
	private static NhacCuaTui nhacCuaTui;
	
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
		while ((str = in.readLine()) != null) {
			while ((from = str.indexOf("<track>")) != -1){
				str = str.substring(from + 7);
				title = getTab(str, "title");
				artist = getTab(str, "creator");
				link = getTab(str, "location");
				Song song = new Song();
				song.setTitle(title + " - " + artist);
				song.setOriginLink(link);
				song.setHost("nhaccuatui.com");
				songs.add(song);
			}
		}
		in.close();
		return songs;
	}
	
	public String getTab(String line, String tab){
		int from = line.indexOf("<" + tab + ">") + tab.length() + 2;
		int to = line.indexOf("</" + tab + ">");
		line = line.substring(from, to);
		from = line.lastIndexOf("[");
		if (from != -1){
			to = line.indexOf("]");
			line = line.substring(from + 1, to);
		}
		return line;
	}
	
	public String htmlToXML(String html) throws IOException{
		URL url = new URL(html);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		int from = -1;
		while ((str = in.readLine()) != null) {
			if ((from = str.indexOf("file=")) != -1){
				str = str.substring(from + 5);
				str = str.substring(0, str.indexOf("\""));
				break;
			}
		}
		connection.disconnect();
		in.close();
		return str;
	}
	
	public String getLink(String html) throws IOException{
		URL url = new URL("http://www.nhaccuatui.com/download/song/" + html.substring(html.length() - 15).substring(0, 10));
		System.out.println(url.getPath());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("Success")){
				System.out.println(str);
				str = str.substring(str.indexOf("http:\\/\\/"));
				str = str.substring(0, str.indexOf("\"")).replace("/", "").trim();
				System.out.println(str);
				break;
			}
		}
		in.close();
		return str == null ? null : str.trim();
	}
	
	public List<Song> searchSong(String value, int page, String filter) throws IOException{
		value = URLEncoder.encode(value, "UTF-8");
		List<Song> songs = new ArrayList<Song>();
		URL url = new URL("http://www.nhaccuatui.com/tim-nang-cao?title=" + value + "&page=" + page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String str;
		String title = "";
		String artist = "";
		String link = "";
		String info = "";
		while ((str = in.readLine()) != null) {
			if (str.contains("<ul class=\"list-song\">")){
				while ((str = in.readLine()) != null && !str.contains("</ul>")){
					if (str.contains("<li class=\"clearfix\">")){
						str = in.readLine();
						link = "http://www.nhaccuatui.com" + getAttribute(str, "href=\"");
						title = htmlToText(str).trim();
						artist = htmlToText(in.readLine()).trim();
						info = htmlToText("Lượt nghe: " + htmlToText(in.readLine()) + " | Upload bởi: " + htmlToText(in.readLine()));
						
						Song song = new Song(title + " - " + artist, link, "nhaccuatui.com");
						song.lineTwo = info;
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
						link = "http://www.nhaccuatui.com" + getAttribute(str, "href=\"");
						title = getAttribute(str, "title=\"");
						Album album = new Album(title, link);
						album.albumArt = getAttribute(str, "src=\"");
						in.readLine();
						str = "";
						for (int i = 0; i < 5; i++){
							str += in.readLine().trim();
							if (i == 2 && str.contains("<br />")){
								break;
							}
						}
						album.info = htmlToText(str) + "<br/>" + in.readLine().trim();
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
}
