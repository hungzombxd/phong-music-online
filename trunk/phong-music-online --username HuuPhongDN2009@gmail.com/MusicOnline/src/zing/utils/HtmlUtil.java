package zing.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlUtil {
	
	public static String streamToString(InputStream in) throws IOException{
		Scanner scanner = new Scanner(in, "UTF-8").useDelimiter("\\A");
		String ret = scanner.hasNext() ? scanner.next(): "";
		in.close();
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
