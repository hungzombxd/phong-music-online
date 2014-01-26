package huu.phong.musiconline.model;

import java.io.Serializable;
import java.util.Date;

import javax.swing.ImageIcon;

import huu.phong.musiconline.sites.Site;

public interface IMedia extends Serializable{

	public String getId();

	public String getLink();

	public String getTitle();

	public Site getSite();

	public Format getQuality();

	public String getArtist();
	
	public ImageIcon getThumbnail();
	
	public boolean hasThumbnail();
	
	public boolean isThumbnailLoaded();

	public String getGenre();

	public String getUsername();

	public long getCount();

	public String getArtistId();

	public String getAlbumId();

	public String getGenreId();

	public boolean isHit();

	public boolean isOfficial();

	public long getLikes();
	
	public Date getDate();

	public String getFullTitle();
	
	public String getDetailTitle();

	public String getDescription();
	
	public int hashCode();
	
	public boolean equals(Object obj);
	
	public String toString();
}
