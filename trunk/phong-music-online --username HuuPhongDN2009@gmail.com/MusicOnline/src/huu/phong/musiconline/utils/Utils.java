package huu.phong.musiconline.utils;

import huu.phong.musiconline.model.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Utils{
	
	public static int[] ERROR_CODE = {403};
	
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
	
	private static Pattern ncrDecimalPattern = Pattern.compile("&#(\\d+);");
	
	public static String toANSI(String str){
		for (int i = 0; i < utf8.length; i++) {
			str = str.replaceAll(utf8[i], ansi[i]);
			str = str.replaceAll(utf8[i].toUpperCase(), ansi[i].toUpperCase());
		}
		return str;
	}
	
	public static String ncrToUnicode(String str){
		Matcher matcher = ncrDecimalPattern.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()){
			matcher.appendReplacement(buffer, String.valueOf((char)Integer.parseInt(matcher.group(1))));
		}
		matcher.appendTail(buffer);
		return buffer.toString();
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
	
	public static String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	public static String toDuaration(int position){
		int min = position / 60000;
		int sec = (position / 1000) % 60;
		return numberToString(min) + ":" + numberToString(sec);
	}
	
	public static boolean isURLAvailable(String link){
		boolean ret = false;
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			connection.setInstanceFollowRedirects(false);
//			connection.setRequestMethod("HEAD");
			connection.connect();
			if (connection.getContentLength() == -1){
				ret = false;
			} else if (isErrorCode(connection.getResponseCode())) {
				ret = false;
			} else {
				ret = true;
			}
			connection.disconnect();
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}
	
	private static boolean isErrorCode(int code){
		boolean ret = false;
		for (int i = 0; i < ERROR_CODE.length; i++) {
			if (code == ERROR_CODE[i]) {
				ret = true;
				break;
			}
		}
		if (code >= 300) ret = true;
		return ret;
	}
}
