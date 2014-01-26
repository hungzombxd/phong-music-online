package huu.phong.musiconline.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public interface ISong extends IMedia, Serializable{
	
	public String getDirectLink() throws IOException;
	
	public String getDirectLink(Format format) throws IOException;

	public Map<Format, String> getDirectLinks();
	
}
