package huu.phong.musiconline.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import huu.phong.musiconline.sites.Site;

public class LocalSong extends huu.phong.musiconline.model.Song {

	private static final long serialVersionUID = 4935401458148202991L;
	
	private String id;
	private File file;
	
	public LocalSong(String pathToFile){
		this(new File(pathToFile));
	}
	
	public LocalSong(File file){
		this.file = file;
		id = new Date().toString();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return file.getName();
	}

	@Override
	public Site getSite() {
		return Site.MY_COMPUTER;
	}

	@Override
	public Format getQuality() {
		return file.getName().endsWith(".mp3") ? Format.MP3_128_KBPS : Format.LOSSLESS;
	}

	@Override
	public String getFullTitle() {
		return file.getName();
	}
	
	@Override
	public String getDescription() {
		return file.getParentFile().getAbsolutePath();
	}

	@Override
	public String getDirectLink() throws IOException {
		return String.format("file:%s", file.getAbsoluteFile());
	}

	@Override
	public String getDirectLink(Format format) throws IOException {
		return getDirectLink();
	}
}
