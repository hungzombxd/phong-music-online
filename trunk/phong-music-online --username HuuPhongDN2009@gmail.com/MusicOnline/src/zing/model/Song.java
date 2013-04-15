package zing.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import zing.Configure;
import zing.sites.ChiaSeNhac;
import zing.sites.MusicGoVn;
import zing.sites.NhacCuaTui;
import zing.sites.Radio;
import zing.sites.Zing;


public class Song implements Serializable {
	private static final long serialVersionUID = -1080772505347758185L;
	public static final int MP3_128_KBPS = 0;
	public static final int MP3_320_KBPS = 1;
	public static final int LOSSLESS = 2;
	public String link = null;
	public String title = null;
	public String directLink = null;
	public String host = "mp3.zing.vn";
	public int quality = MP3_128_KBPS;
	public String songInfo = null;
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

	public void setDirectLink(String directLink) {
		this.directLink = directLink;
	}

	public String getDirectLink() throws IOException {
		if (link == null || ((System.currentTimeMillis() - time) <= Configure.getInstance().timeLive && !(directLink == null))) return directLink;
		time = System.currentTimeMillis();
		if (host.equals("mp3.zing.vn")) {
			directLink = Zing.getInstance().getLink(link);
		}else if (host.equals("radio.vnmedia.vn")){
			if (!directLink.equals("")) return directLink;
			directLink = Radio.getIntance().getSong(link);
		}else if (host.equals("nhaccuatui.com")){
			directLink = NhacCuaTui.getInstance().getLink(link);
		}else if (host.equals("music.go.vn")){
			directLink = MusicGoVn.getInstance().getLink(link);
		}else if (host.equals("chiasenhac.com")){
			directLink = ChiaSeNhac.getInstance().getLink(link);
		}
		return directLink;
	}
	
	public void saveToFile(String dir){
		dir = (dir.endsWith(File.separator))? dir : dir + File.separator;
		try {
			URLConnection connection = new URL(getDirectLink()).openConnection();
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

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
	
	public String toString(){
		String ret = "";
		if (songInfo == null){
			ret = "<html><b>" + title + "</b><br/>Website: " + host + "</html>";
		}else{
			ret = "<html><b>" + title + "</b><br/>" + songInfo + "<br/>Website: " + host + "<html>";
		}
		return ret;
	}
}
