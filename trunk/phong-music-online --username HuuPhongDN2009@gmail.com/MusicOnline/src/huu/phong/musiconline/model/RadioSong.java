package huu.phong.musiconline.model;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.sites.Radio;
import huu.phong.musiconline.sites.Site;

import java.io.IOException;
import java.util.Date;

public class RadioSong extends Song {

	private static final long serialVersionUID = 6012979312065910757L;
	
	private String id;
	
	private String fullTitle;
	
	private String description;
	
	private String directLink;
	
	private String link;
	
	public RadioSong() {
		id = new Date().toString();
	}
	
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return fullTitle;
	}

	@Override
	public Site getSite() {
		return Site.RADIO_VNMEDIA_VN;
	}

	@Override
	public String getFullTitle() {
		return fullTitle;
	}

	@Override
	public String getDirectLink() throws IOException {
		return getDirectLink(Configure.getInstance().format);
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		if (directLink == null) directLink = Radio.getIntance().getDirectLink(getLink());
		return directLink;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getLink() {
		if (link == null) return link;
		return link.contains("http") ? link : String.format("%s%s", Site.RADIO_VNMEDIA_VN.getFullHost(), link);
	}
}
