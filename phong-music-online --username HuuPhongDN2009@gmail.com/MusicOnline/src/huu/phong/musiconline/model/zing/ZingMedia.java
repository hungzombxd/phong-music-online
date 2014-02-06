package huu.phong.musiconline.model.zing;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.Media;
import huu.phong.musiconline.sites.Site;

import com.google.gson.annotations.SerializedName;

public abstract class ZingMedia extends Media{

	private static final long serialVersionUID = 7902153106448791986L;
	protected String link;
	protected String title;
	protected Format quality = Format.MP3_128_KBPS;
	protected String artist;
	protected String genre;
	protected String username;
	@SerializedName("total_play")
	protected long count;
	@SerializedName("artist_id")
	protected String artistId;
	@SerializedName("album_id")
	protected String albumId;
	@SerializedName("genre_id")
	protected String genreId;
	@SerializedName("is_hit")
	protected boolean hit;
	@SerializedName("is_official")
	protected boolean official;
	protected long likes;

	public String getLink() {
		if (link == null) return link;
		return link.contains("http") ? link : String.format("%s%s", getSite().getFullHost(), link);
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

	public Site getSite() {
		return Site.MP3_ZING_VN;
	}

	public Format getQuality() {
		return quality;
	}

	public void setQuality(Format quality) {
		this.quality = quality;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getArtistId() {
		return artistId;
	}

	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getGenreId() {
		return genreId;
	}

	public void setGenreId(String genreId) {
		this.genreId = genreId;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public boolean isOfficial() {
		return official;
	}

	public void setOfficial(boolean official) {
		this.official = official;
	}

	public long getLikes() {
		return likes;
	}

	public void setLikes(long likes) {
		this.likes = likes;
	}
	
	@Override
	public String getFullTitle() {
		return title + (artist == null || artist.equals("") ? "" : String.format(" - %s", artist));
	}
}