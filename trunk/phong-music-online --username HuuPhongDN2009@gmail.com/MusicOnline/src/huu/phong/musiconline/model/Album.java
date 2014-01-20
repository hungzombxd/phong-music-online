package huu.phong.musiconline.model;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.NhacCuaTui;
import huu.phong.musiconline.sites.Site;
import huu.phong.musiconline.sites.Zing;
import huu.phong.musiconline.utils.Utils;

import java.awt.MediaTracker;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Album implements Serializable{
	private static final long serialVersionUID = -2824232816762868663L;
	private String title;
	private String info;
	private List<Song> songs = new ArrayList<Song>();
	private long time;
	private String link;
	private boolean highQuality;
	private String albumArt;
	private transient ImageIcon icon;
	private Site site = Site.MP3_ZING_VN;
	
	public Album(){
		
	}
	
	public Album(String title, String link){
		this.title = title;
		this.link = link;
	}
	
	public Album(String title, String link, String info){
		this.title = title;
		this.link = link;
		this.info = info;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getLink() {
		if (link == null) return link;
		return link.contains("http") ? link : String.format("%s%s", site.getFullHost(), link);
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	public String getAlbumArt() {
		return albumArt;
	}

	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

	public int hashCode(){
		return title.hashCode();
	}
	
	public boolean equals(Object obj){
		if (obj instanceof Album){
			Album that = (Album) obj;
			if (that.title.equals(title)){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		if (info == null){
			return "<html><b>" + title + "</b></html>";
		}else{
			return "<html><b>" + title + "</b><br/>" + info + "<html>";
		}
	}
	
	public List<Song> getSongs() throws IOException{
		if (songs.isEmpty() || !Utils.isURLAvailable(songs.get(0).getDirectLinkWithoutRefresh(Configure.getInstance().format))){
			if (link.contains("mp3.zing.vn")){
				songs = Zing.getInstance().getAlbum(link);
			}else if (link.contains("nhaccuatui.com")){
				songs = NhacCuaTui.getInstance().getAlbum(link);
			}
			for (Song song : songs){
				if (song.getSongInfo() == null) song.setSongInfo("Album: " + title);
				if (highQuality) song.setQuality(Format.MP3_320_KBPS);
			}
		}
		return songs;
	}
	
	public ImageIcon getImage(){
		if (icon == null){
			try {
				icon = new AlbumImage(new URL(albumArt));
				if (icon.getImageLoadStatus() == MediaTracker.ERRORED){
					icon = AlbumRenderer.DEFAULT_DETAIL_ALBUM_ART;
				}
			} catch (MalformedURLException e) {
				icon = AlbumRenderer.DEFAULT_DETAIL_ALBUM_ART;
				e.printStackTrace();
			}
		}
		return icon;
	}
}
