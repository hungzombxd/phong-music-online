package huu.phong.musiconline.model;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.NhacCuaTui;
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
	public String title = null;
	public String info = null;
	public List<Song> songs = new ArrayList<Song>();
	public long time = 0;
	public String link = null;
	public boolean highQuality = false;
	public String albumArt = null;
	public transient ImageIcon icon;
	
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
				if (song.songInfo == null) song.songInfo = "Album: " + title;
				if (highQuality) song.quality = Format.MP3_320_KBPS;
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
