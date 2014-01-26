package huu.phong.musiconline.sites;

import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.nhaccuatui.NhacCuaTuiAlbumDetail;
import huu.phong.musiconline.model.nhaccuatui.NhacCuaTuiAlbumList;
import huu.phong.musiconline.model.nhaccuatui.NhacCuaTuiLyricList;
import huu.phong.musiconline.model.nhaccuatui.NhacCuaTuiSongList;
import huu.phong.musiconline.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.nct.constants.Constants;
import com.nct.dataloader.URLProvider;

public class NhacCuaTui extends MusicSite {
	
	private static final String deviceInfo = "{\"DeviceID\":\"5284047f4ffb4e04824a2fd1d1f0cd62\",\"OsName\":\"ANDROID\",\"OsVersion\":\"17\",\"AppName\":\"NhacCuaTui\",\"AppVersion\":\"5.0.1\",\"UserInfo\":\"\",\"LocationInfo\":\"\"}";
	
	private static final int SONG_PER_PAGE = 15;
	
	public static final String NHACCUATUI_USER_AGENT = "android-async-http/1.4.3 (http://loopj.com/android-async-http)";
	
	private static NhacCuaTui nhacCuaTui = new NhacCuaTui();
	
	public static ItemCombo[] BYS = new ItemCombo[]{new ItemCombo("Default", "")};
	public static ItemCombo[] FILTERS = new ItemCombo[]{new ItemCombo("Default", "")};
	
	public static NhacCuaTui getInstance(){
		return nhacCuaTui;
	}
	
	private NhacCuaTui() {
		Constants.DEVICE_INFOR = deviceInfo;
		properties.put("User-Agent", NHACCUATUI_USER_AGENT);
	}
	
	public Map<Format, String> getLink(String html) throws IOException{
		return null;
	}
	
	public List<? extends ISong> searchSong(String value, int page, String filter) throws IOException {
		InputStream in = getInputStream(URLProvider.getSearchSong(value, page, SONG_PER_PAGE));
		String response = Utils.streamToString(in);
		NhacCuaTuiSongList songList = gson.fromJson(response, NhacCuaTuiSongList.class);
		return songList.getSongs();
	}

	@Override
	public List<? extends IAlbum> searchAlbum(String value, int page, String filter) throws IOException {
		InputStream in = getInputStream(URLProvider.getSearchPlaylist(value, page, SONG_PER_PAGE));
		String response = Utils.streamToString(in);
		NhacCuaTuiAlbumList albumList = gson.fromJson(response, NhacCuaTuiAlbumList.class);
		return albumList.getAlbums();
	}

	@Override
	public List<? extends ISong> getAlbum(String id) throws IOException {
		String[] data = id.split("|");
		InputStream in = getInputStream(URLProvider.getPlaylistInfo(data[0], data[1]));
		String response = Utils.streamToString(in);
		NhacCuaTuiAlbumDetail albumDetail = gson.fromJson(response, NhacCuaTuiAlbumDetail.class);
		return albumDetail.getSongs();
	}

	public List<String> getLyric(ISong song) throws IOException {
		InputStream in = getInputStream(URLProvider.getLyris(song.getId()));
		String response = Utils.streamToString(in);
		NhacCuaTuiLyricList lyricList = gson.fromJson(response, NhacCuaTuiLyricList.class);
		return Arrays.asList(lyricList.lyric.lyric);
	}

	@Override
	public ItemCombo[] getBys() {
		return BYS;
	}

	@Override
	public ItemCombo[] getFilters() {
		return FILTERS;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		Constants.DEVICE_INFOR = deviceInfo;
		System.out.println(URLDecoder.decode("&deviceinfo=%7B%22DeviceID%22%3A%225284047f4ffb4e04824a2fd1d1f0cd62%22%2C%22OsName%22%3A%22ANDROID%22%2C%22OsVersion%22%3A%2217%22%2C%22AppName%22%3A%22NhacCuaTui%22%2C%22AppVersion%22%3A%225.0.1%22%2C%22UserInfo%22%3A%22%22%2C%22LocationInfo%22%3A%22%22%7D&time=1390117048985&token=d5cf791a2712361e8144e564cdeee0bb", "utf-8"));
		System.out.println(URLProvider.getSearchSong("quang le", 1, 20));
	}
}
