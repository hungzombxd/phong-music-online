package huu.phong.musiconline.sites;

import huu.phong.musiconline.Configure;

public enum Site {
	MP3_ZING_VN ("mp3.zing.vn"),
	NHAC_CUA_TUI ("nhaccuatui.com"),
	CHIA_SE_NHAC ("chiasenhac.com"),
	RADIO_VNMEDIA_VN ("radio.vnmedia.vn"),
	MY_COMPUTER ("My Computer"),
	INTERNET_URL ("Internet File");
	
	private final String host;
	
	private Site(String host){
		this.host = host;
	}
	
	public String getHost(){
		return host;
	}
	
	public String getFullHost(){
		return String.format("http://%s", host);
	}
	
	public static String getUserAgent(Site site, boolean isGetSong){
		if (site.equals(Site.MP3_ZING_VN)){
			return isGetSong ? Zing.SONG_USER_AGENT : Zing.DEFAULT_USER_AGENT;
		}
		return Configure.getInstance().userAgent;
	}
}
