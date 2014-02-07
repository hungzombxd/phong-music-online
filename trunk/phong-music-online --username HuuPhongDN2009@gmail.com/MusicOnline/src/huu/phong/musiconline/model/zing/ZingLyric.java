package huu.phong.musiconline.model.zing;

import java.util.Arrays;
import java.util.List;

public class ZingLyric {
	
	public String author;

	public boolean mark;

	public String content;
	
	public List<String> getLyric(){
		return Arrays.asList(content.split("\r\n"));
	}
}
