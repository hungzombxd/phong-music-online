package zing.utils;

import java.io.BufferedReader;
import java.io.IOException;

public final class HtmlUtil {
	
	public static String readJoinLines(BufferedReader in) throws IOException{
		String line = null;
		String ret = "";
		while ((line = in.readLine()) != null){
			ret += line.trim();
		}
		return ret;
	}
	
	public static String getHtmlTag(String line, String tag){
		int from = line.indexOf("<" + tag + ">") + tag.length() + 2;
		int to = line.indexOf("</" + tag + ">");
		line = line.substring(from, to);
		return line;
	}
	
	public static String getTag(String line, String tab){
		int from = line.indexOf("<" + tab + ">") + tab.length() + 2;
		int to = line.indexOf("</" + tab + ">");
		line = line.substring(from, to);
		from = line.lastIndexOf("[");
		if (from != -1){
			to = line.indexOf("]");
			line = line.substring(from + 1, to);
		}
		return line;
	}
	
	public static String getContent(String str) {
		int from = str.lastIndexOf("[");
		int to = str.indexOf("]");
		if (from == -1) {
			from = str.indexOf(">");
			to = str.lastIndexOf("<");
		}
		return str.substring(from + 1, to).trim();
	}
	
	public static String getAttribute(String content, String condition){
		return getAttribute(content, condition, "\"");
	}
	
	public static String getAttribute(String content, String condition, String end){
		int index = content.indexOf(condition) + condition.length();
		content = content.substring(index);
		return content.substring(0, content.indexOf(end));
	}
	
	public static String htmlToText(String html){
		int first = -1;
		int last = -1;
		while ((first = html.indexOf("<")) != -1){
			last = html.indexOf(">");
			html = html.replace(html.substring(first, last + 1), "");
		}
		return html;
	}
}
