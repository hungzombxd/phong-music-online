package zing.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import zing.model.Song;


public final class Utils{
	
	private static String[] ansi = new String[]{"a", "a", "a", "a", "a", "a", "a",
			"a", "a", "a", "a", "a", "a", "a", "a", "o", "o", "o", "o", "o",
			"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u",
			"u", "u", "d", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e",
			"i", "i", "i", "i", "i", "u", "u", "u", "u", "u", "y", "y", "y",
			"y", "y", "o", "e", "o", "u", "a", "a"};
	private static String[] utf8  = new String[] { "á", "à", "ả", "ạ", "ã", "ắ", "ằ",
					"ặ", "ẳ", "ẵ", "ấ", "ầ", "ẩ", "ẫ", "ậ", "ớ", "ờ", "ợ", "ở", "ỡ",
					"ó", "ò", "ọ", "ỏ", "õ", "ố", "ồ", "ộ", "ổ", "ỗ", "ứ", "ừ", "ự",
					"ử", "ữ", "đ", "é", "è", "ẹ", "ẻ", "ẽ", "ế", "ề", "ệ", "ể", "ễ",
					"í", "ì", "ị", "ỉ", "ĩ", "ú", "ù", "ụ", "ủ", "ũ", "ý", "ỳ", "ỵ",
					"ỷ", "ỹ", "ô", "ê", "ơ", "ư", "ă", "â" };
	
	private static String[] ncrDecimal = new String[] { "&#225;", "&#224;", "&#7843;",
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
	
	public static String toANSI(String str){
		for (int i = 0; i < utf8.length; i++) {
			str = str.replaceAll(utf8[i], ansi[i]);
			str = str.replaceAll(utf8[i].toUpperCase(), ansi[i].toUpperCase());
		}
		return str;
	}
	
	public static String toUTF8(String str){
		for (int i = 0; i < utf8.length; i++) {
			str = str.replaceAll(ncrDecimal[i], utf8[i]);
			str = str.replaceAll(ncrDecimal[i], utf8[i].toUpperCase());
		}
		return str;
	}
	
	public static List<Song> m3uToSongs(String m3uFile) throws IOException{
		List<Song> songs = new ArrayList<Song>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(m3uFile), "UTF-8"));
		String line = null;
		while ((line = reader.readLine()) != null){
			if (line.startsWith("#EXTINF")){
				StringTokenizer token = new StringTokenizer(line, ",");
				String title = token.nextToken();
				title = token.nextToken();
				String link = reader.readLine();
				Song song = new Song();
				song.setTitle(title);
				song.setLink(link);
				songs.add(song);
			}
		}
		reader.close();
		return songs;
	}
	
	public static void songsToM3UFile(List<Song> songs, String file) throws IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		writer.write("#EXTM3U");
		writer.newLine();
		for (int i = 0; i < songs.size(); i++){
			Song song = songs.get(i);
			writer.write("#EXTINF:-1," + song.getTitle());
			writer.newLine();
			writer.write(song.getLink());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}
}
