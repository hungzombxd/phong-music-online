package zing;

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


public abstract class MusicSite{
	public static final int MP3_ZING_VN = 0;
	public static final int NHACCUATUI_COM = 1;
	public static final int MUSIC_GO_VN = 2;
	
	public abstract List<Song> searchSong(String value, int page, String filter) throws IOException;
	
	public abstract String getLink(String html) throws IOException;
	
	public abstract List<Album> searchAlbum(String value, int page, String filter) throws IOException;
	
	public abstract List<Song> getAlbum(String html) throws IOException;
	
	public List<String> getLyric(Song song) throws IOException{
		List<String> lyrics = new ArrayList<String>();
//		URL url = new URL("http://www.lyricsplugin.com/plugin/0.4/wmplayer/plugin.php");
//		String data = "a=" + URLEncoder.encode(artist, "UTF-8") + "&t=" + URLEncoder.encode(title, "UTF-8") + "&i=1348500217&pid=1665487f0013ca6a073144103d1e86d8&sid=298dbb06a5723403e7241bcc4ad21568&v=12a";
//		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
////		connection.addRequestProperty("User-Agent", "Lyrics Plugin/0.4");
//		connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		connection.addRequestProperty("Content-Length", String.valueOf(data.length()));
//		connection.setDoOutput(true);
//	    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
//	    wr.write(data);
//	    wr.flush();
//		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//		String str;
//		while ((str = in.readLine()) != null) {
//			lyrics.add(str);
//		}
//		in.close();
		return lyrics;
	}
	
	public String getHTMLTag(String line, String tag){
		int from = line.indexOf("<" + tag + ">") + tag.length() + 2;
		int to = line.indexOf("</" + tag + ">");
		line = line.substring(from, to);
		return line;
	}
	
	public String getAttribute(String content, String condition){
		int index = content.indexOf(condition) + condition.length();
		content = content.substring(index);
		return content.substring(0, content.indexOf("\""));
	}
	
	public String getAttribute(String content, String condition, String end){
		int index = content.indexOf(condition) + condition.length();
		content = content.substring(index);
		return content.substring(0, content.indexOf(end));
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
	
	public String NCR2Unicode(String str) {
        String ostr = new String();
        int i1=0;
        int i2=0;
        while(i2<str.length()) {
            i1 = str.indexOf("&#",i2);
            if (i1 == -1 ) {
                ostr += str.substring(i2, str.length());
                break ;
            }
            ostr += str.substring(i2, i1);
            i2 = str.indexOf(";", i1);
            if (i2 == -1 ) {
                ostr += str.substring(i1, str.length());
                break ;
            }
            String tok = str.substring(i1+2, i2);
            try {
                int radix = 10 ;
                if (tok.trim().charAt(0) == 'x') {
                    radix = 16 ;
                    tok = tok.substring(1,tok.length());
                }
                ostr += (char) Integer.parseInt(tok, radix);
            } catch (NumberFormatException exp) {
                ostr += '?' ;
            }
            i2++ ;
        }
        return ostr ;
    }
	
	public List<Song> m3uToSongs(String m3uFile) throws IOException{
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
	
	public void SongsToM3UFile(List<Song> songs, String file) throws IOException{
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
	
	public abstract ItemCombo[] getBys();
	
	public abstract ItemCombo[] getFilters();
}
