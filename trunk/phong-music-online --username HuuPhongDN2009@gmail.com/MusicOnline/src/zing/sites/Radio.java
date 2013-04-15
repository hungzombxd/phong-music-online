package zing.sites;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import zing.model.Song;
import zing.utils.HtmlUtil;
import zing.utils.Utils;

public class Radio {
	public String[] radioTypes = new String[]{
		"Điểm chạm cảm xúc",
		"Nhật kí tình yêu",
		"Love full",
		"Truyện ngắn",
		"Sắc màu cuộc sống",
		"Lời yêu thương",
		"Cà phê sách",
		"Nghệ thuật sống",
		"Mẹo hay",
		"Truyện dài kỳ"
	};
	
	private String[] radioLinks = new String[]{
			"http://radio.vnmedia.vn/Browse.aspx?id=26-diem-cham-cam-xuc",
			"http://radio.vnmedia.vn/Browse.aspx?id=32-nhat-ki-tinh-yeu",
			"http://radio.vnmedia.vn/Browse.aspx?id=25-love-full",
			"http://radio.vnmedia.vn/Browse.aspx?id=3-truyen-ngan",
			"http://radio.vnmedia.vn/Browse.aspx?id=30-sac-mau-cuoc-song",
			"http://radio.vnmedia.vn/Browse.aspx?id=21-loi-yeu-thuong",
			"http://radio.vnmedia.vn/Browse.aspx?id=27-cafe-sach",
			"http://radio.vnmedia.vn/Browse.aspx?id=4-nghe-thuat-song",
			"http://radio.vnmedia.vn/Browse.aspx?id=7-meo-hay",
			"http://radio.vnmedia.vn/Browse.aspx?id=17-truyen-dai-ky"
	};
	
	private Map<String, String> mapRadio;
	
	private static Radio radio;
	
	public static Radio getIntance(){
		if (radio == null){
			radio = new Radio();
		}
		return radio;
	}
	
	private Radio(){
		mapRadio = new HashMap<String, String>();
		for (int i = 0; i < radioTypes.length; i++){
			mapRadio.put(radioTypes[i], radioLinks[i]);
		}
	}
	
	public List<Song> getRadio(int page, String radioType) {
		return getSongs(mapRadio.get(radioType) + "&page=" + page);
	}

	public List<Song> getSongs(String link) {
		List<Song> songs = new ArrayList<Song>();
		try {
			URL url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));
			String str, title, ln;
			int i = 0, index;
			while ((str = in.readLine()) != null && i < 10) {
				if (str.contains("<a href=\"/Listen.aspx?")
						&& str.trim().startsWith("<a href")) {
					index = str.indexOf("/Listen.aspx?");
					ln = str.substring(index);
					ln = "http://radio.vnmedia.vn"
							+ new StringTokenizer(ln, "\"").nextToken();
					title = in.readLine().trim();
					title = Utils.toUTF8(title);
					i++;
					Song song = new Song(title, ln, "radio.vnmedia.vn");
					song.songInfo = "";
					while ((str = in.readLine()) != null && !str.contains("</dl>")){
						song.songInfo += str; 
					}
					song.songInfo = HtmlUtil.htmlToText(song.songInfo);
					songs.add(song);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;
	}

	public String getSong(String link) {
		String ret = "";
		try {
			URL url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String str;
			int index = 0;
			while ((str = in.readLine()) != null) {
				if (str.contains("radio.playingTrack")) {
					index = str.indexOf("http://");
					ret = str.substring(index);
					ret = new StringTokenizer(ret, "'").nextToken();
				}
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.replace(" ", "%20");
	}
}
