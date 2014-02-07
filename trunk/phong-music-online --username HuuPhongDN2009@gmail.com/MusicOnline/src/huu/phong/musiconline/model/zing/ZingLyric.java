package huu.phong.musiconline.model.zing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZingLyric {
	
	public String author;

	public boolean mark;

	public String content;
	
	public List<String> getLyric(){
		if (content == null || content.equals("")) return Collections.emptyList();
		return Arrays.asList(content.split("\r\n"));
	}
}
