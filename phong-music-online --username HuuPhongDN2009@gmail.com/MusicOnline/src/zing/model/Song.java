package zing.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import zing.sites.ChiaSeNhac;
import zing.sites.NhacCuaTui;
import zing.sites.Radio;
import zing.sites.Site;
import zing.sites.Zing;
import zing.utils.Utils;

public class Song implements Serializable {
	private static final long serialVersionUID = -1080772505347758185L;
	public String link = null;
	public String title = null;
	public Map<Format, String> directLinks = null;
	public Site site = Site.MP3_ZING_VN;
	public transient Format currentFormat;
	public Format quality = Format.MP3_128_KBPS;
	public String songInfo = null;
	
	public Song() {
	}

	public Song(String title, String link) {
		this.title = title;
		this.link = link;
	}
	
	public Song(String title, String link, Site site) {
		this.title = title;
		this.link = link;
		this.site = site;
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
	
	public void setDirectLinks(Map<Format, String> directLinks){
		this.directLinks = directLinks;
	}
	
	public void setDirectLink(Format format, String directLink){
		if (directLinks == null){
			directLinks = new HashMap<Format, String>();
		}
		directLinks.put(format, directLink);
	}

	public String getDirectLink(Format format) throws IOException {
		if (directLinks != null && Utils.isURLAvailable(getLink(format))) return getLink(format);
		
		switch (site) {
		
		case MP3_ZING_VN:
			directLinks = Zing.getInstance().getLink(link);
			break;
		
		case CHIA_SE_NHAC:
			directLinks = ChiaSeNhac.getInstance().getLink(link);
			break;
			
		case NHAC_CUA_TUI:
			directLinks = NhacCuaTui.getInstance().getLink(link);
			break;
		
		case RADIO_VNMEDIA_VN:
			directLinks = Radio.getIntance().getSong(link);
			break;
			
		default:
			throw new RuntimeException("Site is empty");
		}
		
		return getLink(format);
	}
	
	private String getLink(Format format){
		String link = null;
		switch (format) {
		case LOSSLESS:
			link = directLinks.get(Format.LOSSLESS); if (link != null) break;
		
		case MP3_320_KBPS:
			link = directLinks.get(Format.MP3_320_KBPS); if (link != null) break;
			
		case MP3_128_KBPS:
			link = directLinks.get(Format.MP3_128_KBPS); if (link != null) break;
			
		default:
			link = directLinks.get(null); break;
		}
		return link;
	}
	
//	public void saveToFile(String dir){
//		dir = (dir.endsWith(File.separator))? dir : dir + File.separator;
//		try {
//			URLConnection connection = new URL(getDirectLink(Configure.getInstance().format)).openConnection();
//			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
//			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dir + toTitle(title) + (Configure.getInstance().format == Format.LOSSLESS ? ".flac" : ".mp3")));
//			int readed = -1;
//			byte[] buffered = new byte[63888];
//			while ((readed = in.read(buffered)) != -1){
//				out.write(buffered, 0, readed);
//			}
//			out.flush();
//			out.close();
//			in.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String toTitle(){
		String str = title.replace(":", "-");
		StringTokenizer token = new StringTokenizer(str, "-");
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

	public Format getQuality() {
		return quality;
	}

	public void setQuality(Format format) {
		this.quality = format;
	}
	
	public String toString(){
		String ret = "";
		if (songInfo == null){
			ret = "<html><b>" + title + "</b><br/>Website: " + site.getHost() + "</html>";
		}else{
			ret = "<html><b>" + title + "</b><br/>" + songInfo + "<br/>Website: " + site.getHost() + "<html>";
		}
		return ret;
	}
}
