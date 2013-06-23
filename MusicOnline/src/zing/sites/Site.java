package zing.sites;

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
}
