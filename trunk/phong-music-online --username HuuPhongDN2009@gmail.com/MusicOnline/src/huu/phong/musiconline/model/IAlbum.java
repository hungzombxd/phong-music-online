package huu.phong.musiconline.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface IAlbum extends IMedia, Serializable{
	
	public List<? extends ISong> getSongs() throws IOException;
	
	public boolean isAlbum();
	
}
