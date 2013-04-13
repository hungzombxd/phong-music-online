package zing;

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
	String title = "";
	String info = "";
	List<Song> songs = new ArrayList<Song>();
	long time = 0;
	String link = "";
	boolean highQuality = false;
	String albumArt = "";
	transient ImageIcon icon;
	
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
		if (info.equals("")){
			return "<html><b>" + title + "</b></html>";
		}else{
			return "<html><b>" + title + "</b><br/>" + info + "<html>";
		}
	}
	
	public List<Song> getSongs() throws IOException{
		if (System.currentTimeMillis() - time > Configure.getInstance().timeLive){
			time = System.currentTimeMillis();
			if (link.contains("mp3.zing.vn")){
				songs = Zing.getInstance().getAlbum(link);
			}else if (link.contains("nhaccuatui.com")){
				songs = NhacCuaTui.getInstance().getAlbum(link);
			}else if (link.contains("music.go.vn")){
				songs = MusicGoVn.getInstance().getAlbum(link);
			}
			for (Song song : songs){
				if (song.lineTwo.equals("")) song.lineTwo = "Album: " + title;
				if (highQuality) song.quality = Song.MP3_320_KBPS;
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
