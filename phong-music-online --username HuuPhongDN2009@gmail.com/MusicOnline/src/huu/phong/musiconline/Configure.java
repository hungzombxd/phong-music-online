package huu.phong.musiconline;

import huu.phong.musiconline.model.AlbumRenderer;
import huu.phong.musiconline.model.Format;
import huu.phong.musiconline.model.IAlbum;
import huu.phong.musiconline.model.ISong;
import huu.phong.musiconline.model.ItemCombo;
import huu.phong.musiconline.model.Playlist;
import huu.phong.musiconline.model.SongRenderer;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Configure implements Serializable{
	private static final long serialVersionUID = 96678343791424943L;
	private transient static Configure configure;
	public List<ISong> songs;
	public List<IAlbum> albums;
	public List<Playlist> userPlaylists;
	public String title = "Music Online v8.3.3";
	public String host, port, username, password, total = "1", page = "1", defaultMediaPlayer = title;
	public boolean useProxy, systemProxy;
	public List<String> mediaPlayers;
	public String type = "Song";
	public ItemCombo filter = new ItemCombo("Default", "");
	public String repeat = "Default";
	public String value = "";
	public Point location = null;
	public int numberReconnect = 5;
	public boolean includeAlbum = true;
	public List<String> valueRecently = new ArrayList<String>();
	public String status = title + " | huuphongdn2009@gmail.com";
	public String oldFolder = ".";
	public int lastPageSong = 1;
	public String lastValueSong = "";
	public ItemCombo by = new ItemCombo("Default", "");
	public boolean update = true;
	public int viewModeSong = SongRenderer.VIEW_MODE_DETAIL;
	public int viewModeAlbum = AlbumRenderer.VIEW_MODE_DETAIL;
	public String site = "mp3.zing.vn";
	public String lastValueAlbum = "";
	public int lastPageAlbum = 1;
	public String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1";
	public Format format = Format.MP3_320_KBPS;
	
	public static Configure getInstance(){
		if (configure == null) configure = load();
		return configure;
	}
	
	private Configure(){
		mediaPlayers = new ArrayList<String>();
		mediaPlayers.add(title);
		mediaPlayers.add("C:\\Program Files (x86)\\foobar2000\\foobar2000.exe");
		mediaPlayers.add("D:\\Software\\Portable\\VLC\\vlc.exe");
		mediaPlayers.add("C:\\Program Files\\Windows Media Player\\wmplayer.exe");
		mediaPlayers.add("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe");
		mediaPlayers.add("/usr/bin/audacious");
		songs = new ArrayList<ISong>();
		albums = new ArrayList<IAlbum>();
		userPlaylists = new ArrayList<Playlist>();
	}
	
	public void save(){
//		if (songs.size() > 0 && (songs.get(0).getLink() == null)){
//			songs = new ArrayList<ISong>();		
//		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Music Online.conf"));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Configure load(){
		Configure that = new Configure();
		File file = new File("Music Online.conf");
		if (!file.exists()) return that;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			Object object = in.readObject();
			if (object instanceof Configure){
				Configure exist = (Configure) object;
				if (exist.title != null && exist.title.equals(that.title)){
					that = exist;
				}else{
					JOptionPane.showMessageDialog(null, "File old configure of " + exist.title +  " is not compatible with " + that.title + "\r\nOld configure will rename to " + exist.title + ".old.conf", "Loading configuare", JOptionPane.INFORMATION_MESSAGE);
					boolean rename = false;
					try {
						ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(exist.title + ".old.conf"));
						out.writeObject(exist);
						out.flush();
						out.close();
						rename = true;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "Rename is " + (rename ? "Successfull" : "Fail") + "\r\nOld configuare will replace with default configuare of " + that.title, "Loading configuare", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return that;
	}
}
