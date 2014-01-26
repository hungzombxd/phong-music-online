package huu.phong.musiconline.sites;

import static huu.phong.musiconline.sites.MusicSite.DEFAULT_AGENT;
import static huu.phong.musiconline.sites.NhacCuaTui.NHACCUATUI_USER_AGENT;
import static huu.phong.musiconline.sites.Zing.ZING_USER_AGENT;
import static huu.phong.musiconline.sites.Zing.ZING_SONG_USER_AGENT;

public enum Site {
	
	MP3_ZING_VN ("mp3.zing.vn", ZING_USER_AGENT, ZING_SONG_USER_AGENT),
	NHAC_CUA_TUI ("nhaccuatui.com", NHACCUATUI_USER_AGENT, NHACCUATUI_USER_AGENT),
	CHIA_SE_NHAC ("chiasenhac.com", DEFAULT_AGENT, DEFAULT_AGENT),
	RADIO_VNMEDIA_VN ("radio.vnmedia.vn", DEFAULT_AGENT, DEFAULT_AGENT),
	MY_COMPUTER ("My Computer", DEFAULT_AGENT, DEFAULT_AGENT),
	INTERNET_URL ("Internet File", DEFAULT_AGENT, DEFAULT_AGENT);
	
	private final String host;
	
	private final String defaultAgent;
	
	private final String songAgent;
	
	private Site(String host, String defaultAgent, String songAgent){
		this.host = host;
		this.defaultAgent = defaultAgent;
		this.songAgent = songAgent;
	}
	
	public String getHost(){
		return host;
	}
	
	public String getFullHost(){
		return String.format("http://%s", host);
	}
	
	public String getDefaultAgent(){
		return defaultAgent;
	}
	
	public String getSongAgent(){
		return songAgent;
	}
}
