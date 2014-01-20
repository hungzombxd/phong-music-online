package huu.phong.musiconline.model;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.ChiaSeNhac;
import huu.phong.musiconline.sites.NhacCuaTui;
import huu.phong.musiconline.sites.Radio;
import huu.phong.musiconline.sites.Site;
import huu.phong.musiconline.sites.Zing;
import huu.phong.musiconline.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Song implements Serializable {
	private static final long serialVersionUID = -1080772505347758185L;
	private static final String DESCRIPTIONS[] = {"Thời lượng: %s", "Lượt nghe: %s", "Thể loại: %s", "%s"};
	private String link;
	private String title;
	@SerializedName("source")
	private Map<Format, String> directLinks;
	private Site site = Site.MP3_ZING_VN;
	private transient Format currentFormat;
	private Format quality = Format.MP3_128_KBPS;
	private String songInfo;
	
	@SerializedName("song_id")
	private String id;
	private String artist;
	private String genre;
	private String username;
	private String bitrate;
	private int duration;
	@SerializedName("have_rbt")
	private boolean rbt;
	@SerializedName("download_status")
	private int status;
	private String copyright;
	@SerializedName("link_download")
	private Map<String, String> downloadLinks;
	@SerializedName("total_play")
	private long count;
	
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
		if (link == null) return link;
		return link.contains("http") ? link : String.format("%s%s", site.getFullHost(), link);
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
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getBitrate() {
		return bitrate;
	}

	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isRbt() {
		return rbt;
	}

	public void setRbt(boolean rbt) {
		this.rbt = rbt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public Map<String, String> getDownloadLinks() {
		return downloadLinks;
	}

	public void setDownloadLinks(Map<String, String> downloadLinks) {
		this.downloadLinks = downloadLinks;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public Map<Format, String> getDirectLinks() {
		return directLinks;
	}

	public void setDirectLink(Format format, String directLink){
		if (directLinks == null){
			directLinks = new HashMap<Format, String>();
		}
		directLinks.put(format, directLink);
	}
	
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	public String getDirectLink(Format format) throws IOException {
		if (directLinks != null) {
			String userAgent = Site.getUserAgent(site, true);
			if (Utils.isURLAvailable(getDirectLinkWithoutRefresh(format), userAgent)){
				return getDirectLinkWithoutRefresh(format);
			}
		}
		
		if (!Utils.isURLAvailable(link)) throw new IOException(String.format("Link %s is not availble", link));
		
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
		
		return getDirectLinkWithoutRefresh(format);
	}
	
	public String getDirectLinkWithoutRefresh(Format format){
		String userAgent = Site.getUserAgent(site, true);
		String link = null;
		switch (format) {
		case LOSSLESS:
			link = directLinks.get(Format.LOSSLESS); if (Utils.isURLAvailable(link, userAgent)) break;
		
		case MP3_320_KBPS:
			link = directLinks.get(Format.MP3_320_KBPS); if (Utils.isURLAvailable(link, userAgent)) break;
			
		case MP3_128_KBPS:
			link = directLinks.get(Format.MP3_128_KBPS); if (Utils.isURLAvailable(link, userAgent)) break;
			
		default:
			link = directLinks.get(null); break;
		}
		
		return link;
	}
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public boolean equals(Object obj){
		if (obj instanceof Song){
			Song song = (Song) obj;
			if (song.getTitle().equals(title)) return true;
		}
		return false;
	}

	public Format getQuality() {
		if (directLinks == null) return quality;
		if (directLinks.containsKey(Format.LOSSLESS)) {
			quality = Format.LOSSLESS;
		}else if (directLinks.containsKey(Format.MP3_320_KBPS)) {
			quality = Format.MP3_320_KBPS;
		}
		return quality;
	}

	public void setQuality(Format format) {
		this.quality = format;
	}
	
	public void setSongInfo(String songInfo) {
		this.songInfo = songInfo;
	}

	public String getSongInfo(){
		return songInfo;
	}
	
	public Format getCurrentFormat() {
		return currentFormat;
	}

	public void setCurrentFormat(Format currentFormat) {
		this.currentFormat = currentFormat;
	}

	public String toString(){
		if (buildSongInfo() == null) setSongInfo(buildSongInfo());
		return String.format("<html><b>%s</b><br/>%s<br/>Website: %s<html>", getSongName() , buildSongInfo(), site.getHost());
	}
	
	private String buildSongInfo(){
		String[] values = {Utils.toDuaration(duration * 1000), Utils.formatNumber(count), genre, songInfo};
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < DESCRIPTIONS.length; i++){
			if (values[i] != null && !values[i].equals("")){
				builder.append(String.format(DESCRIPTIONS[i], values[i]));
				builder.append(" | ");
			}
		}
		if (builder.length() > 0) builder.delete(builder.length() - 3, builder.length());
		return builder.toString();
	}
	
	public String getSongName(){
		return title + (artist == null ? "" : String.format(" - %s", artist));
	}
}
