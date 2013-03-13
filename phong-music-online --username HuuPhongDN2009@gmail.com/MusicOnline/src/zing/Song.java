package zing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class Song implements Serializable {
	private static final long serialVersionUID = -1080772505347758185L;
	String link = "";
	String title = "";
	String originLink = "";
	String host = "mp3.zing.vn";
	boolean highQuality = false;
	String songInfo = "(^_^) Không tìm thấy thông tin (^_^)";
	String lineOne = "";
	String lineTwo = "";
	long time = 0;
	
	public Song() {
	}

	public Song(String title, String link) {
		this.title = title;
		this.link = link;
	}
	
	public Song(String title, String link, String host) {
		this.title = title;
		this.link = link;
		this.host = host;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOriginLink(String originLink) {
		this.originLink = originLink;
	}

	public String getOriginLink() throws IOException {
		if (link.equals("") || ((System.currentTimeMillis() - time) <= Configure.getInstance().timeLive && !originLink.equals(""))) return originLink;
		time = System.currentTimeMillis();
		if (host.equals("mp3.zing.vn")) {
			originLink = Zing.getInstance().getLink(link);
			songInfo = Zing.getInstance().htmlSongInfo.replace("128kb/s | ", "");
		}else if (host.equals("radio.vnmedia.vn")){
			if (!originLink.equals("")) return originLink;
			originLink = Radio.getIntance().getSong(link);
		}else if (host.equals("nhaccuatui.com")){
			originLink = NhacCuaTui.getInstance().getLink(link);
		}else if (host.equals("music.go.vn")){
			originLink = MusicGoVn.getInstance().getLink(link);
		}else if (host.equals("chiasenhac.com")){
			originLink = ChiaSeNhac.getInstance().getLink(link);
		}
		return originLink;
	}
	
	public void saveToFile(String dir){
		dir = (dir.endsWith(File.separator))? dir : dir + File.separator;
		try {
			URLConnection connection = new URL(getOriginLink()).openConnection();
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dir + toTitle(title) + ".mp3"));
			int readed = -1;
			byte[] buffered = new byte[63888];
			while ((readed = in.read(buffered)) != -1){
				out.write(buffered, 0, readed);
			}
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String toTitle(String str){
		str = str.replace(":", "-");
		StringTokenizer token = new StringTokenizer(str,"-");
		if (token.countTokens() == 2){
			String titleSong = token.nextToken().trim();
			String artistSong = token.nextToken().trim();
			return artistSong + " - " + titleSong;
		}
		return str;
	}
	public boolean equals(Object obj){
		if (obj instanceof Song){
			Song song = (Song) obj;
			if (song.getTitle().equals(title)) return true;
		}
		return false;
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}
	
	public String toString(){
		String ret = "";
		if (lineTwo.equals("")){
			ret = "<html><b>" + title + "</b><br/>Website: " + host + "</html>";
		}else{
			ret = "<html><b>" + title + "</b><br/>" + lineTwo + "<br/>Website: " + host + "<html>";
		}
		return ret;
	}
}
