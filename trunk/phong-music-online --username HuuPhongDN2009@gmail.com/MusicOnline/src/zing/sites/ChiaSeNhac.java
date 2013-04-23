package zing.sites;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import zing.Configure;
import zing.model.Album;
import zing.model.ItemCombo;
import zing.model.Song;
import zing.utils.HtmlUtil;

public class ChiaSeNhac extends MusicSite{
	
	private static ChiaSeNhac chiaSeNhac = new ChiaSeNhac();
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", ""), new ItemCombo("Artist", "&mode=artist"), new ItemCombo("Composer", "&mode=composer"), new ItemCombo("Album", "&mode=album"), new ItemCombo("Lyric", "&mode=lyric")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", "&cat=music")};
	
	public static ChiaSeNhac getInstance(){
		return chiaSeNhac;
	}
	
	private ChiaSeNhac(){
	}

	@Override
	public List<Song> searchSong(String value, int page, String filter)
			throws IOException {
		value = URLEncoder.encode(value, "UTF-8");
		List<Song> songs = new ArrayList<Song>();
		BufferedReader in = getInputStream("http://search.chiasenhac.com/search.php?mode=&order=quality&cat=music&s=" + value + "&page=" + page + filter);
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
				Song song = new Song(title + " - " + artist, link, "chiasenhac.com");
				song.quality = info.contains("Lossless") ? Song.LOSSLESS : info.contains("320kbps") ? Song.MP3_320_KBPS : Song.MP3_128_KBPS;
				song.songInfo = info.replace("Lossless", "<b style='color: blue'>Lossless</b>");
				songs.add(song);
				if (songs.size() == 25) break;
			}
		}
		in.close();
		return songs;
	}

	@Override
	public String getLink(String html) throws IOException {
		List<String> links = new ArrayList<String>();
		BufferedReader in = getInputStream(html.replace("http://", "http://download.").replace(".html", "_download.html"));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.contains("<div id=\"downloadlink\"")){
				while (!(str = in.readLine()).contains("</div>")){
					if (str.contains("http://data.chiasenhac.com/downloads/")){
						str = HtmlUtil.getAttribute(str, "href=\"").replace(" ", "+");
						if (str.toLowerCase().endsWith(".mp3") || str.toLowerCase().endsWith(".flac")) links.add(str);
					}
				}
			}
		}
		in.close();
		str = Configure.getInstance().highQuality ? links.get(links.size() - 1) : links.get(0);
		return str;
	}

	@Override
	public List<Album> searchAlbum(String value, int page, String filter)
			throws IOException {
		List<Album> albums = new ArrayList<Album>();
		return albums;
	}

	@Override
	public List<Song> getAlbum(String html) throws IOException {
		List<Song> songs = new ArrayList<Song>();
		return songs;
	}
	
	public static void main(String[] args) throws IOException {
		new ChiaSeNhac().getLink("http://chiasenhac.com/mp3/vietnam/v-pop/no~pham-truong~1004374.html");
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
