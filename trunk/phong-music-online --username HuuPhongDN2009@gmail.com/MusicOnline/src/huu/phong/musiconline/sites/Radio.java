package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.zing.ZingSong;
import huu.phong.musiconline.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
	
	private static Radio radio = new Radio();
	
	public static Radio getIntance(){
		return radio;
	}
	
	private Radio(){
		mapRadio = new HashMap<String, String>();
		for (int i = 0; i < radioTypes.length; i++){
			mapRadio.put(radioTypes[i], radioLinks[i]);
		}
	}
	
	public List<ISong> getRadio(int page, String radioType) {
		return getSongs(mapRadio.get(radioType) + "&page=" + page);
	}

	public List<ISong> getSongs(String link) {
		List<ISong> songs = new ArrayList<ISong>();
		try {
			URL url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String str, title, ln;
			int i = 0, index;
			while ((str = in.readLine()) != null && i < 10) {
				if (str.contains("<a href=\"/Listen.aspx?")
						&& str.trim().startsWith("<a href")) {
					index = str.indexOf("/Listen.aspx?");
					ln = str.substring(index);
					ln = new StringTokenizer(ln, "\"").nextToken();
					title = in.readLine().trim();
					title = Utils.ncrToUnicode(title);
					i++;
					ZingSong song = new ZingSong();
					song.setTitle(title);
					song.setLink(link);
					song.setSite(Site.RADIO_VNMEDIA_VN);
//					song.setSongInfo("");
					while ((str = in.readLine()) != null && !str.contains("</dl>")){
//						song.setSongInfo(song.getSongInfo() + str); 
					}
//					song.setSongInfo(HtmlUtil.htmlToText(song.getSongInfo()));
					songs.add(song);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;
	}

	public Map<Format, String> getSong(String link) {
		Map<Format, String> links = new HashMap<Format, String>();
		try {
			URL url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String str;
			int index = 0;
			while ((str = in.readLine()) != null) {
				if (str.contains("radio.playingTrack")) {
					index = str.indexOf("http://");
					String ret = str.substring(index);
					ret = new StringTokenizer(ret, "'").nextToken();
					links.put(Format.MP3_128_KBPS, ret.replace(" ", "%20"));
				}
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return links;
	}
}
