package huu.phong.musiconline.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlUtil {
	
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
	
	public static String getContentOfTag(String condition, BufferedReader reader) throws IOException{
		StringBuilder builder = new StringBuilder();
		Pattern patternStart = Pattern.compile("<([^ >/]+)");
		while (true){
			String line = reader.readLine();
			if (line == null) break;
			if (line.contains(condition)){
				Matcher matcher = patternStart.matcher(line);
				if (!matcher.find()) throw new RuntimeException("Not found tag");
				String tag = matcher.group(1);
				builder.append(line);
				patternStart = Pattern.compile(String.format("<%1$s .*?>", tag));
				Pattern patternEnd = Pattern.compile(String.format("</%1$s>", tag));
				int count = 0;
				while (true){
					line = reader.readLine();
					builder.append(line);
					matcher = patternStart.matcher(line);
					if (matcher.find()){
						count++;
						continue;
					}
					matcher = patternEnd.matcher(line);
					if (matcher.find()) {
						if (count-- == 0){
							break;
						}
					}
				}
			} else {
				continue;
			}
			break;
		}
		return builder.toString();
	}
}
