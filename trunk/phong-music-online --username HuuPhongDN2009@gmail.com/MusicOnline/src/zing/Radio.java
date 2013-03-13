package zing;

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
	
	private String[] ncrDecimal = new String[] { "&#225;", "&#224;", "&#7843;",
			"&#7841;", "&#227;", "&#7855;", "&#7857;", "&#7863;", "&#7859;",
			"&#7861;", "&#7845;", "&#7847;", "&#7849;", "&#7851;", "&#7853;",
			"&#7899;", "&#7901;", "&#7907;", "&#7903;", "&#7905;", "&#243;",
			"&#242;", "&#7885;", "&#7887;", "&#245;", "&#7889;", "&#7891;",
			"&#7897;", "&#7893;", "&#7895;", "&#7913;", "&#7915;", "&#7921;",
			"&#7917;", "&#7919;", "&#273;", "&#233;", "&#232;", "&#7865;",
			"&#7867;", "&#7869;", "&#7871;", "&#7873;", "&#7879;", "&#7875;",
			"&#7877;", "&#237;", "&#236;", "&#7883;", "&#7881;", "&#297;",
			"&#250;", "&#249;", "&#7909;", "&#7911;", "&#361;", "&#253;",
			"&#7923;", "&#7925;", "&#7927;", "&#7929;", "&#244;", "&#234;",
			"&#417;", "&#432;", "&#259;", "&#226;" };
	private String[] utf8 = new String[] { "á", "à", "ả", "ạ", "ã", "ắ", "ằ",
			"ặ", "ẳ", "ẵ", "ấ", "ầ", "ẩ", "ẫ", "ậ", "ớ", "ờ", "ợ", "ở", "ỡ",
			"ó", "ò", "ọ", "ỏ", "õ", "ố", "ồ", "ộ", "ổ", "ỗ", "ứ", "ừ", "ự",
			"ử", "ữ", "đ", "é", "è", "ẹ", "ẻ", "ẽ", "ế", "ề", "ệ", "ể", "ễ",
			"í", "ì", "ị", "ỉ", "ĩ", "ú", "ù", "ụ", "ủ", "ũ", "ý", "ỳ", "ỵ",
			"ỷ", "ỹ", "ô", "ê", "ơ", "ư", "ă", "â" };
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
	
	public String toUTF8(String str){
		for (int i = 0; i < utf8.length; i++) {
			str = str.replaceAll(ncrDecimal[i], utf8[i]);
			str = str.replaceAll(ncrDecimal[i].toUpperCase(), utf8[i].toUpperCase());
		}
		return str;
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
					title = toUTF8(title);
					i++;
					Song song = new Song(title, ln, "radio.vnmedia.vn");
					while ((str = in.readLine()) != null && !str.contains("</dl>")){
						song.lineTwo +=str; 
					}
					song.lineTwo = htmlToText(song.lineTwo);
					songs.add(song);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;
	}

	public String htmlToText(String html){
		int first = -1;
		int last = -1;
		while ((first = html.indexOf("<")) != -1){
			last = html.indexOf(">");
			html = html.replace(html.substring(first, last + 1), "");
		}
		return html;
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
