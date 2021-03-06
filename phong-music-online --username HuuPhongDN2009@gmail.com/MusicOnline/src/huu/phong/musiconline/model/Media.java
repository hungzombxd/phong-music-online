package huu.phong.musiconline.model;

import java.awt.MediaTracker;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import javax.swing.ImageIcon;

public abstract class Media implements IMedia {

	private static final long serialVersionUID = 6612280209817722109L;

	@Override
	public String getLink() {
		return null;
	}

	@Override
	public Format getQuality() {
		return Format.MP3_128_KBPS;
	}

	@Override
	public String getArtist() {
		return null;
	}

	@Override
	public ImageIcon getThumbnail() {
		return null;
	}

	@Override
	public boolean hasThumbnail() {
		return false;
	}

	@Override
	public boolean isThumbnailLoaded() {
		return false;
	}

	@Override
	public String getGenre() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public long getCount() {
		return 0;
	}

	@Override
	public String getArtistId() {
		return null;
	}

	@Override
	public String getAlbumId() {
		return null;
	}

	@Override
	public String getGenreId() {
		return null;
	}

	@Override
	public boolean isHit() {
		return false;
	}

	@Override
	public boolean isOfficial() {
		return false;
	}

	@Override
	public long getLikes() {
		return 0;
	}

	@Override
	public Date getDate() {
		return null;
	}
	
	@Override
	public String getDetailTitle() {
		return String.format("<html><b>%s</b><br/>%s<br/>Website: %s</html>", getFullTitle(), getDescription(), getSite().getHost());
	}
	
	public int hashCode(){
		return getId().hashCode();
	}
	
	public String toString(){
		return getDetailTitle();
	}
	
	public boolean equals(Object object){
		if (object instanceof IMedia){
			IMedia that = (IMedia) object;
			return getId().equals(that.getId());
		}
		return false;
	}
	
	public static ImageIcon getThumbnail(String thumbnail){
		ImageIcon icon = null;
		try {
			icon = new AlbumImage(new URL(thumbnail));
			if (icon.getImageLoadStatus() == MediaTracker.ERRORED) {
				icon = AlbumRenderer.DEFAULT_DETAIL_ALBUM_ART;
			}
		} catch (MalformedURLException e) {
			icon = AlbumRenderer.DEFAULT_DETAIL_ALBUM_ART;
			e.printStackTrace();
		}
		return icon;
	}
	
	public String getDirectLink(Map<Format, String> directLinks, Format format){
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
		
		if (link == null) throw new RuntimeException("Can not find any direct link.");
		
		return link;
	}
}
